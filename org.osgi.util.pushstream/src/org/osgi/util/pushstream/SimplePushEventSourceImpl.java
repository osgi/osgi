/*
 * Copyright (c) OSGi Alliance (2015, 2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.util.pushstream;

import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

class SimplePushEventSourceImpl<T, U extends BlockingQueue<PushEvent< ? extends T>>>
		implements SimplePushEventSource<T> {
	
	private final Object								lock		= new Object();

	private final PromiseFactory						promiseFactory;
	private final PromiseFactory						sameThread;

	private final QueuePolicy<T,U>						queuePolicy;

	private final U										queue;

	private final int									parallelism;

	private final Semaphore								semaphore;

	private final List<PushEventConsumer< ? super T>>	connected	= new ArrayList<>();

	private final Runnable								onClose;

	private boolean										closed;
	
	private Deferred<Void>								connectPromise;

	private boolean										waitForFinishes;


	public SimplePushEventSourceImpl(PromiseFactory promiseFactory,
			QueuePolicy<T,U> queuePolicy,
			U queue, int parallelism, Runnable onClose) {
		this.promiseFactory = promiseFactory;
		this.sameThread = new PromiseFactory(
				PromiseFactory.inlineExecutor(),
				promiseFactory.scheduledExecutor());
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
			promiseFactory.executor().execute(() -> safePush(pec, event));
		} catch (RejectedExecutionException ree) {
			// TODO log?
			if (!event.isTerminal()) {
				close(PushEvent.error(ree));
			} else {
				safePush(pec, event);
			}
		}
	}

	private Promise<Long> doSendWithBackPressure(
			PushEventConsumer< ? super T> pec, PushEvent<T> event) {
		Deferred<Long> d = sameThread.deferred();
		try {
			promiseFactory.executor().execute(
					() -> d.resolve(Long.valueOf(
							System.nanoTime() + safePush(pec, event))));
		} catch (RejectedExecutionException ree) {
			// TODO log?

			if (!event.isTerminal()) {
				close(PushEvent.error(ree));
				d.resolve(Long.valueOf(System.nanoTime()));
			} else {
				d.resolve(
						Long.valueOf(System.nanoTime() + safePush(pec, event)));
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
			return event.isTerminal() ? -1 : backpressure;
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
	public void error(Throwable t) {
		enqueueEvent(PushEvent.error(t));
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
		promiseFactory.executor().execute(() -> {
			try {
				
				for(;;) {
					PushEvent<T> event;
					List<PushEventConsumer< ? super T>> toCall;
					boolean resetWait;
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

						if (connected.isEmpty()) {
							queue.clear();
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
						} else {
							resetWait = false;
						}
					}
					
					Promise<Long> backPressure = deliver(toCall, event);
					
					if (backPressure.isDone()) {
						handleReset(resetWait);

						long toWait = backPressure.getValue()
								- System.nanoTime();

						if (toWait > 0) {
							promiseFactory.scheduledExecutor().schedule(
									this::startWorker, toWait,
									NANOSECONDS);
							return;
						}
					} else {
						backPressure.then(p -> {
							handleReset(resetWait);
							long toWait = p.getValue() - System.nanoTime();

							if (toWait > 0) {
								promiseFactory.scheduledExecutor().schedule(
										this::startWorker, toWait,
										NANOSECONDS);
							} else {
								startWorker();
							}
							return p;
						}, p -> close(
								PushEvent.error(p.getFailure())));
						return;
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

	private void handleReset(boolean resetWait) {
		if (resetWait == true) {
			synchronized (lock) {
				waitForFinishes = false;
				lock.notifyAll();
			}
		}
	}

	private Promise<Long> deliver(List<PushEventConsumer< ? super T>> toCall,
			PushEvent<T> event) {
		if (toCall.size() == 1) {
			return doCall(event, toCall.get(0));
		} else {
			List<Promise<Long>> calls = toCall.stream().map(pec -> {
				if (semaphore.tryAcquire()) {
					return doSendWithBackPressure(pec, event)
							.onResolve(() -> semaphore.release());
				} else {
					return doCall(event, pec);
				}
			}).collect(toList());
			return sameThread.all(calls)
					.map(l -> l.stream().max(Long::compareTo).orElseGet(
							() -> Long.valueOf(System.nanoTime())));
		}
	}

	private Promise<Long> doCall(PushEvent<T> event,
			PushEventConsumer< ? super T> pec) {
		return sameThread.resolved(
				Long.valueOf(System.nanoTime() + safePush(pec, event)));
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
					connectPromise = promiseFactory.deferred();
				}
				return connectPromise.getPromise();
			} else {
				return promiseFactory.resolved(null);
			}
		}
	}

	private Promise<Void> closedConnectPromise() {
		return promiseFactory.failed(new IllegalStateException(
				"This SimplePushEventSource is closed"));
	}

}
