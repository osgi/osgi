package org.osgi.util.pushstream;

import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

class SimplePushEventSourceImpl<T, U extends BlockingQueue<PushEvent< ? extends T>>>
		implements SimplePushEventSource<T> {

	private final Object								lock		= new Object();

	private final Executor								worker;

	private final ScheduledExecutorService				scheduler;

	private final QueuePolicy<T,U>						queuePolicy;

	private final U										queue;

	private final int									parallelism;

	private final Semaphore								semaphore;

	private final List<PushEventConsumer< ? super T>>	connected	= new ArrayList<>();

	private final Runnable								onClose;

	private boolean										closed;
	
	private Deferred<Void>								connectPromise;

	private boolean										waitForFinishes;


	public SimplePushEventSourceImpl(Executor worker,
			ScheduledExecutorService scheduler, QueuePolicy<T,U> queuePolicy,
			U queue, int parallelism, Runnable onClose) {
		this.worker = worker;
		this.scheduler = scheduler;
		this.queuePolicy = queuePolicy;
		this.queue = queue;
		this.parallelism = parallelism;
		this.semaphore = new Semaphore(parallelism);
		this.onClose = onClose;
		this.closed = false;
		this.connectPromise = null;
	}

	@Override
	public AutoCloseable open(PushEventConsumer< ? super T> pec)
			throws Exception {
		Deferred<Void> toResolve = null;
		synchronized (lock) {
			if (closed) {
				throw new IllegalStateException(
						"This PushEventConsumer is closed");
			}

			toResolve = connectPromise;
			connectPromise = null;

			connected.add(pec);
		}

		if (toResolve != null) {
			toResolve.resolve(null);
		}

		return () -> {
			closeConsumer(pec, PushEvent.close());
		};
	}

	private void closeConsumer(PushEventConsumer< ? super T> pec,
			PushEvent<T> event) {
		boolean sendClose;
		synchronized (lock) {
			sendClose = connected.remove(pec);
		}
		if (sendClose) {
			doSend(pec, event);
		}
	}

	private void doSend(PushEventConsumer< ? super T> pec, PushEvent<T> event) {
		try {
			worker.execute(() -> safePush(pec, event));
		} catch (RejectedExecutionException ree) {
			// TODO log?
			if (!event.isTerminal()) {
				close(PushEvent.error(ree));
			} else {
				safePush(pec, event);
			}
		}
	}

	@SuppressWarnings("boxing")
	private Promise<Long> doSendWithBackPressure(
			PushEventConsumer< ? super T> pec, PushEvent<T> event) {
		Deferred<Long> d = new Deferred<>();
		try {
			worker.execute(
					() -> d.resolve(System.nanoTime() + safePush(pec, event)));
		} catch (RejectedExecutionException ree) {
			// TODO log?
			if (!event.isTerminal()) {
				close(PushEvent.error(ree));
				return Promises.resolved(System.nanoTime());
			} else {
				return Promises
						.resolved(System.nanoTime() + safePush(pec, event));
			}
		}
		return d.getPromise();
	}

	private long safePush(PushEventConsumer< ? super T> pec,
			PushEvent<T> event) {
		try {
			long backpressure = pec.accept(event) * 1000000;
			if (backpressure < 0 && !event.isTerminal()) {
				closeConsumer(pec, PushEvent.close());
				return -1;
			}
			return backpressure;
		} catch (Exception e) {
			// TODO log?
			if (!event.isTerminal()) {
				closeConsumer(pec, PushEvent.error(e));
			}
			return -1;
		}
	}

	@Override
	public void close() {
		close(PushEvent.close());
	}

	private void close(PushEvent<T> event) {
		List<PushEventConsumer< ? super T>> toClose;
		Deferred<Void> toFail = null;
		synchronized (lock) {
			if(!closed) {
				closed = true;
				
				toClose = new ArrayList<>(connected);
				connected.clear();
				queue.clear();

				if(connectPromise != null) {
					toFail = connectPromise;
					connectPromise = null;
				}
			} else {
				toClose = emptyList();
			}
		}

		toClose.stream().forEach(pec -> doSend(pec, event));

		if (toFail != null) {
			toFail.resolveWith(closedConnectPromise());
		}

		onClose.run();
	}

	@Override
	public void publish(T t) {
		enqueueEvent(PushEvent.data(t));
	}

	@Override
	public void endOfStream() {
		enqueueEvent(PushEvent.close());
	}

	@Override
	public void error(Exception e) {
		enqueueEvent(PushEvent.error(e));
	}

	private void enqueueEvent(PushEvent<T> event) {
		synchronized (lock) {
			if (closed || connected.isEmpty()) {
				return;
			}
		}

		try {
			queuePolicy.doOffer(queue, event);
			boolean start;
			synchronized (lock) {
				start = !waitForFinishes && semaphore.tryAcquire();
			}
			if (start) {
				startWorker();
			}
		} catch (Exception e) {
			close(PushEvent.error(e));
			throw new IllegalStateException(
					"The queue policy threw an exception", e);
		}
	}

	@SuppressWarnings({
			"unchecked", "boxing"
	})
	private void startWorker() {
		worker.execute(() -> {
			try {
				
				for(;;) {
					PushEvent<T> event;
					List<PushEventConsumer< ? super T>> toCall;
					boolean resetWait = false;
					synchronized (lock) {
						if(waitForFinishes) {
							semaphore.release();
							while(waitForFinishes) {
								lock.notifyAll();
								lock.wait();
							}
							semaphore.acquire();
						}

						event = (PushEvent<T>) queue.poll();
						
						if(event == null) {
							break;
						}

						toCall = new ArrayList<>(connected);
						if (event.isTerminal()) {
							waitForFinishes = true;
							resetWait = true;
							connected.clear();
							while (!semaphore.tryAcquire(parallelism - 1)) {
								lock.wait();
							}
						}
					}
					
					List<Promise<Long>> calls = toCall.stream().map(pec -> {
						if (semaphore.tryAcquire()) {
							try {
								return doSendWithBackPressure(pec, event);
							} finally {
								semaphore.release();
							}
						} else {
							return Promises.resolved(
									System.nanoTime() + safePush(pec, event));
						}
					}).collect(toList());

					long toWait = Promises.all(calls)
							.map(l -> l.stream()
									.max(Long::compareTo)
										.orElseGet(() -> System.nanoTime()))
							.getValue() - System.nanoTime();
					
					
					if (toWait > 0) {
						scheduler.schedule(this::startWorker, toWait,
								NANOSECONDS);
						return;
					}

					if (resetWait == true) {
						synchronized (lock) {
							waitForFinishes = false;
							lock.notifyAll();
						}
					}
				}

				semaphore.release();
			} catch (Exception e) {
				close(PushEvent.error(e));
			}
			if (queue.peek() != null && semaphore.tryAcquire()) {
				try {
					startWorker();
				} catch (Exception e) {
					close(PushEvent.error(e));
				}
			}
		});

	}

	@Override
	public boolean isConnected() {
		synchronized (lock) {
			return !connected.isEmpty();
		}
	}

	@Override
	public Promise<Void> connectPromise() {
		synchronized (lock) {
			if (closed) {
				return closedConnectPromise();
			}

			if (connected.isEmpty()) {
				if (connectPromise == null) {
					connectPromise = new Deferred<>();
				}
				return connectPromise.getPromise();
			} else {
				return Promises.resolved(null);
			}
		}
	}

	private Promise<Void> closedConnectPromise() {
		return Promises.failed(new IllegalStateException(
				"This SimplePushEventSource is closed"));
	}

}
