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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.osgi.util.pushstream.AbstractPushStreamImpl.State.CLOSED;
import static org.osgi.util.pushstream.PushEvent.*;
import static org.osgi.util.pushstream.PushbackPolicyOption.LINEAR;
import static org.osgi.util.pushstream.QueuePolicyOption.FAIL;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * A factory for {@link PushStream} instances, and utility methods for handling
 * {@link PushEventSource}s and {@link PushEventConsumer}s
 */
public final class PushStreamProvider {

	private final Lock					lock	= new ReentrantLock(true);

	private int							schedulerReferences;

	private ScheduledExecutorService	sharedScheduler;

	private ScheduledExecutorService acquireScheduler() {
		try {
			lock.lockInterruptibly();
			try {
				schedulerReferences += 1;

				if (schedulerReferences == 1) {
					sharedScheduler = Executors
							.newSingleThreadScheduledExecutor();
				}
				return sharedScheduler;
			} finally {
				lock.unlock();
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException("Unable to acquire the Scheduler",
					e);
		}
	}

	private void releaseScheduler() {
		try {
			lock.lockInterruptibly();
			try {
				schedulerReferences -= 1;

				if (schedulerReferences == 0) {
					sharedScheduler.shutdown();
					sharedScheduler = null;
				}
			} finally {
				lock.unlock();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create a stream with the default configured buffer, executor size, queue,
	 * queue policy and pushback policy. This is equivalent to calling
	 * 
	 * <code>
	 *   buildStream(source).create();
	 * </code>
	 * 
	 * <p>
	 * This stream will be buffered from the event producer, and will honour
	 * back pressure even if the source does not.
	 * 
	 * <p>
	 * Buffered streams are useful for "bursty" event sources which produce a
	 * number of events close together, then none for some time. These bursts
	 * can sometimes overwhelm downstream processors. Buffering will not,
	 * however, protect downstream components from a source which produces
	 * events faster (on average) than they can be consumed.
	 * 
	 * <p>
	 * Event delivery will not begin until a terminal operation is reached on
	 * the chain of AsyncStreams. Once a terminal operation is reached the
	 * stream will be connected to the event source.
	 * 
	 * @param eventSource
	 * @return A {@link PushStream} with a default initial buffer
	 */
	public <T> PushStream<T> createStream(PushEventSource<T> eventSource) {
		return createStream(eventSource, 1, null, null,
				new ArrayBlockingQueue<>(32),
				FAIL.getPolicy(), LINEAR.getPolicy(1000));
	}
	
	/**
	 * Builds a push stream with custom configuration.
	 * 
	 * <p>
	 * 
	 * The resulting {@link PushStream} may be buffered or unbuffered depending
	 * on how it is configured.
	 * 
	 * @param eventSource The source of the events
	 * 
	 * @return A {@link PushStreamBuilder} for the stream
	 */
	public <T, U extends BlockingQueue<PushEvent< ? extends T>>> PushStreamBuilder<T,U> buildStream(
			PushEventSource<T> eventSource) {
		return new PushStreamBuilderImpl<T,U>(this, null, null, eventSource);
	}
	
	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	<T, U extends BlockingQueue<PushEvent< ? extends T>>> PushStream<T> createStream(
			PushEventSource<T> eventSource, int parallelism, Executor executor,
			ScheduledExecutorService scheduler, U queue,
			QueuePolicy<T,U> queuePolicy,
			PushbackPolicy<T,U> pushbackPolicy) {

		if (eventSource == null) {
			throw new NullPointerException("There is no source of events");
		}

		if (parallelism < 0) {
			throw new IllegalArgumentException(
					"The supplied parallelism cannot be less than zero. It was "
							+ parallelism);
		} else if (parallelism == 0) {
			parallelism = 1;
		}

		boolean closeExecutorOnClose;
		Executor workerToUse;
		if (executor == null) {
			workerToUse = Executors.newFixedThreadPool(parallelism);
			closeExecutorOnClose = true;
		} else {
			workerToUse = executor;
			closeExecutorOnClose = false;
		}

		boolean releaseSchedulerOnClose;
		ScheduledExecutorService timerToUse;
		if (scheduler == null) {
			timerToUse = acquireScheduler();
			releaseSchedulerOnClose = true;
		} else {
			timerToUse = scheduler;
			releaseSchedulerOnClose = false;
		}

		if (queue == null) {
			queue = (U) new ArrayBlockingQueue(32);
		}

		if (queuePolicy == null) {
			queuePolicy = FAIL.getPolicy();
		}

		if (pushbackPolicy == null) {
			pushbackPolicy = LINEAR.getPolicy(1000);
		}

		PushStream<T> stream = new BufferedPushStreamImpl<>(this,
				new PushStreamExecutors(workerToUse, timerToUse), queue,
				parallelism, queuePolicy,
				pushbackPolicy, aec -> {
					try {
						return eventSource.open(aec);
					} catch (Exception e) {
						throw new RuntimeException(
								"Unable to connect to event source", e);
					}
				});

		return cleanupThreads(closeExecutorOnClose, workerToUse,
				releaseSchedulerOnClose, stream);
	}

	private <T> PushStream<T> cleanupThreads(boolean closeExecutorOnClose,
			Executor workerToUse, boolean releaseSchedulerOnClose,
			PushStream<T> stream) {
		if (closeExecutorOnClose || releaseSchedulerOnClose) {
			stream = stream.onClose(() -> {
				if (closeExecutorOnClose) {
					((ExecutorService) workerToUse).shutdown();
				}
				if (releaseSchedulerOnClose) {
					releaseScheduler();
				}
			}).map(x -> x);
		}
		return stream;
	}

	<T> PushStream<T> createUnbufferedStream(PushEventSource<T> eventSource,
			Executor executor, ScheduledExecutorService scheduler) {

		boolean closeExecutorOnClose;
		Executor workerToUse;
		if (executor == null) {
			workerToUse = Executors.newFixedThreadPool(2);
			closeExecutorOnClose = true;
		} else {
			workerToUse = executor;
			closeExecutorOnClose = false;
		}

		boolean releaseSchedulerOnClose;
		ScheduledExecutorService timerToUse;
		if (scheduler == null) {
			timerToUse = acquireScheduler();
			releaseSchedulerOnClose = true;
		} else {
			timerToUse = scheduler;
			releaseSchedulerOnClose = false;
		}
		PushStream<T> stream = new UnbufferedPushStreamImpl<>(this,
				new PushStreamExecutors(workerToUse, timerToUse),
				aec -> {
					try {
						return eventSource.open(aec);
					} catch (Exception e) {
						throw new RuntimeException(
								"Unable to connect to event source", e);
					}
				});

		return cleanupThreads(closeExecutorOnClose, workerToUse,
				releaseSchedulerOnClose, stream);
	}

	/**
	 * Convert an {@link PushStream} into an {@link PushEventSource}. The first
	 * call to {@link PushEventSource#open(PushEventConsumer)} will begin event
	 * processing.
	 * 
	 * The {@link PushEventSource} will remain active until the backing stream
	 * is closed, and permits multiple consumers to
	 * {@link PushEventSource#open(PushEventConsumer)} it.
	 * 
	 * This is equivalent to: <code>
	 *   buildEventSourceFromStream(stream).create();
	 * </code>
	 * 
	 * @param stream
	 * @return a {@link PushEventSource} backed by the {@link PushStream}
	 */
	public <T> PushEventSource<T> createEventSourceFromStream(
			PushStream<T> stream) {
		return buildEventSourceFromStream(stream).build();
	}

	/**
	 * Convert an {@link PushStream} into an {@link PushEventSource}. The first
	 * call to {@link PushEventSource#open(PushEventConsumer)} will begin event
	 * processing.
	 * 
	 * <p>
	 * The {@link PushEventSource} will remain active until the backing stream
	 * is closed, and permits multiple consumers to
	 * {@link PushEventSource#open(PushEventConsumer)} it. Note that this means
	 * the caller of this method is responsible for closing the supplied
	 * stream if it is not finite in length.
	 * 
	 * <p>Late joining
	 * consumers will not receive historical events, but will immediately
	 * receive the terminal event which closed the stream if the stream is
	 * already closed.
	 * 
	 * @param stream
	 * 
	 * @return a {@link PushEventSource} backed by the {@link PushStream}
	 */
	public <T, U extends BlockingQueue<PushEvent< ? extends T>>> BufferBuilder<PushEventSource<T>,T,U> buildEventSourceFromStream(
			PushStream<T> stream) {
		BufferBuilder<PushStream<T>,T,U> builder = stream.buildBuffer();
		
		return new BufferBuilder<PushEventSource<T>,T,U>() {

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withBuffer(U queue) {
				builder.withBuffer(queue);
				return this;
			}

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withQueuePolicy(
					QueuePolicy<T,U> queuePolicy) {
				builder.withQueuePolicy(queuePolicy);
				return this;
			}

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withQueuePolicy(
					QueuePolicyOption queuePolicyOption) {
				builder.withQueuePolicy(queuePolicyOption);
				return this;
			}

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withPushbackPolicy(
					PushbackPolicy<T,U> pushbackPolicy) {
				builder.withPushbackPolicy(pushbackPolicy);
				return this;
			}

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withPushbackPolicy(
					PushbackPolicyOption pushbackPolicyOption, long time) {
				builder.withPushbackPolicy(pushbackPolicyOption, time);
				return this;
			}

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withParallelism(
					int parallelism) {
				builder.withParallelism(parallelism);
				return this;
			}

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withExecutor(
					Executor executor) {
				builder.withExecutor(executor);
				return this;
			}

			@Override
			public BufferBuilder<PushEventSource<T>,T,U> withScheduler(
					ScheduledExecutorService scheduler) {
				builder.withScheduler(scheduler);
				return this;
			}

			@Override
			public PushEventSource<T> build() {
				
				AtomicBoolean connect = new AtomicBoolean();
				AtomicReference<PushEvent<T>> terminalEvent = new AtomicReference<>();
				
				CopyOnWriteArrayList<PushEventConsumer< ? super T>> consumers = new CopyOnWriteArrayList<>();
				
				return consumer -> {
					
					consumers.add(consumer);
					
					PushEvent<T> terminal = terminalEvent.get();
					if (terminal != null) {
						if (consumers.remove(consumer)) {
							// The stream is already done and we missed it
							consumer.accept(terminal);
						}
						return () -> {
								//Nothing to do, we have already sent the terminal event
							};
					}
					
					if(!connect.getAndSet(true)) {
						// connect
						builder.build()
								.forEachEvent(new MultiplexingConsumer<T>(
										terminalEvent, consumers));
					}

					return () -> {
						if (consumers.remove(consumer)) {
							try {
								consumer.accept(PushEvent.close());
							} catch (Exception ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
							}
						}
					};
				};
			}
		};
	}
	
	private static class MultiplexingConsumer<T> implements PushEventConsumer<T> {

		private final AtomicReference<PushEvent<T>> terminalEventStore;
		
		private final CopyOnWriteArrayList<PushEventConsumer<? super T>> consumers;
		
		public MultiplexingConsumer(
				AtomicReference<PushEvent<T>> terminalEventStore,
				CopyOnWriteArrayList<PushEventConsumer< ? super T>> consumers) {
			super();
			this.terminalEventStore = terminalEventStore;
			this.consumers = consumers;
		}

		@Override
		public long accept(PushEvent< ? extends T> event) throws Exception {
			boolean isTerminal = event.isTerminal();
			if(isTerminal) {
				if(!terminalEventStore.compareAndSet(null, event.nodata())) {
					// We got a duplicate terminal, silently ignore it
					return -1;
				}
				for (PushEventConsumer< ? super T> pushEventConsumer : consumers) {
					if(consumers.remove(pushEventConsumer)) {
						try {
							pushEventConsumer.accept(event);
						} catch (Exception ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
					}
				}
				return -1;
			} else {
				long maxBP = 0;
				for (PushEventConsumer< ? super T> pushEventConsumer : consumers) {
					try {
						long tmpBP = pushEventConsumer.accept(event);
						
						if(tmpBP < 0 && consumers.remove(pushEventConsumer)) {
							try {
								pushEventConsumer.accept(PushEvent.close());
							} catch (Exception ex) {
								// TODO Auto-generated catch block
								ex.printStackTrace();
							}
						} else if (tmpBP > maxBP) {
							maxBP = tmpBP;
						}
					} catch (Exception ex) {
						if(consumers.remove(pushEventConsumer)) {
							try {
								pushEventConsumer.accept(PushEvent.error(ex));
							} catch (Exception ex2) {
								// TODO Auto-generated catch block
								ex2.printStackTrace();
							}
						}
					}
				}
				return maxBP;
			}
		}
	}

	/**
	 * Create a {@link SimplePushEventSource} with the supplied type and default
	 * buffering behaviours. The SimplePushEventSource will respond to back
	 * pressure requests from the consumers connected to it.
	 * 
	 * This is equivalent to: <code>
	 *   buildSimpleEventSource(type).create();
	 * </code>
	 * 
	 * @param type
	 * @return a {@link SimplePushEventSource}
	 */
	public <T> SimplePushEventSource<T> createSimpleEventSource(Class<T> type) {
		return createSimplePushEventSource(1, null,
				new ArrayBlockingQueue<>(32),
				FAIL.getPolicy(), () -> { /* Nothing else to do */ });
	}
	
	/**
	 * 
	 * Build a {@link SimplePushEventSource} with the supplied type and custom
	 * buffering behaviours. The SimplePushEventSource will respond to back
	 * pressure requests from the consumers connected to it.
	 * 
	 * @param type
	 * 
	 * @return a {@link SimplePushEventSource}
	 */

	public <T, U extends BlockingQueue<PushEvent< ? extends T>>> BufferBuilder<SimplePushEventSource<T>,T,U> buildSimpleEventSource(
			Class<T> type) {
		return new AbstractBufferBuilder<SimplePushEventSource<T>,T,U>() {
			@Override
			public SimplePushEventSource<T> build() {
				return createSimplePushEventSource(concurrency, worker, buffer,
						bufferingPolicy, () -> { /* Nothing else to do */ });
			}
		};
	}
	
	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	<T, U extends BlockingQueue<PushEvent< ? extends T>>> SimplePushEventSource<T> createSimplePushEventSource(
			int parallelism, Executor executor, U queue,
			QueuePolicy<T,U> queuePolicy, Runnable onClose) {

		if (parallelism < 0) {
			throw new IllegalArgumentException(
					"The supplied parallelism cannot be less than zero. It was "
							+ parallelism);
		} else if (parallelism == 0) {
			parallelism = 1;
		}

		boolean closeExecutorOnClose;
		Executor toUse;
		if (executor == null) {
			toUse = Executors.newFixedThreadPool(parallelism);
			closeExecutorOnClose = true;
		} else {
			toUse = executor;
			closeExecutorOnClose = false;
		}

		if (queue == null) {
			queue = (U) new ArrayBlockingQueue(32);
		}

		if (queuePolicy == null) {
			queuePolicy = FAIL.getPolicy();
		}

		SimplePushEventSourceImpl<T,U> spes = new SimplePushEventSourceImpl<T,U>(
				new PushStreamExecutors(toUse, acquireScheduler()), queuePolicy,
				queue, parallelism,
				() -> {
					try {
						onClose.run();
					} catch (Exception e) {
						// TODO log this?
					}
					if (closeExecutorOnClose) {
						((ExecutorService) toUse).shutdown();
					}
					releaseScheduler();
				});
		return spes;
	}

	/**
	 * Create a buffered {@link PushEventConsumer} with the default configured
	 * buffer, executor size, queue, queue policy and pushback policy. This is
	 * equivalent to calling
	 * 
	 * <code>
	 *   buildBufferedConsumer(delegate).create();
	 * </code>
	 * 
	 * <p>
	 * The returned consumer will be buffered from the event source, and will
	 * honour back pressure requests from its delegate even if the event source
	 * does not.
	 * 
	 * <p>
	 * Buffered consumers are useful for "bursty" event sources which produce a
	 * number of events close together, then none for some time. These bursts
	 * can sometimes overwhelm the consumer. Buffering will not, however,
	 * protect downstream components from a source which produces events faster
	 * than they can be consumed.
	 * 
	 * @param delegate
	 * @return a {@link PushEventConsumer} with a buffer directly before it
	 */
	public <T> PushEventConsumer<T> createBufferedConsumer(
			PushEventConsumer<T> delegate) {
		return buildBufferedConsumer(delegate).build();
	}
	
	/**
	 * Build a buffered {@link PushEventConsumer} with custom configuration.
	 * <p>
	 * The returned consumer will be buffered from the event source, and will
	 * honour back pressure requests from its delegate even if the event source
	 * does not.
	 * <p>
	 * Buffered consumers are useful for "bursty" event sources which produce a
	 * number of events close together, then none for some time. These bursts
	 * can sometimes overwhelm the consumer. Buffering will not, however,
	 * protect downstream components from a source which produces events faster
	 * than they can be consumed.
	 * <p>
	 * Buffers are also useful as "circuit breakers". If a
	 * {@link QueuePolicyOption#FAIL} is used then a full buffer will request
	 * that the stream close, preventing an event storm from reaching the
	 * client.
	 * <p>
	 * Note that this buffered consumer will close when it receives a terminal
	 * event, or if the delegate returns negative backpressure. No further
	 * events will be propagated after this time.
	 * 
	 * @param delegate
	 * @return a {@link PushEventConsumer} with a buffer directly before it
	 */
	public <T, U extends BlockingQueue<PushEvent< ? extends T>>> BufferBuilder<PushEventConsumer<T>,T,U> buildBufferedConsumer(
			PushEventConsumer<T> delegate) {
		return new AbstractBufferBuilder<PushEventConsumer<T>,T,U>() {
			@Override
			public PushEventConsumer<T> build() {
				PushEventPipe<T> pipe = new PushEventPipe<>();
				
				createStream(pipe, concurrency, worker, timer, buffer,
						bufferingPolicy, backPressure)
					.forEachEvent(delegate);
				
				return pipe;
			}
		};
	}

	static final class PushEventPipe<T>
			implements PushEventConsumer<T>, PushEventSource<T> {

		volatile PushEventConsumer< ? super T> delegate;

		@Override
		public AutoCloseable open(PushEventConsumer< ? super T> pec)
				throws Exception {
			return () -> { /* Nothing else to do */ };
		}

		@Override
		public long accept(PushEvent< ? extends T> event) throws Exception {
			return delegate.accept(event);
		}

	}

	/**
	 * Create an Unbuffered {@link PushStream} from a Java {@link Stream} The
	 * data from the stream will be pushed into the PushStream synchronously as
	 * it is opened. This may make terminal operations blocking unless a buffer
	 * has been added to the {@link PushStream}. Care should be taken with
	 * infinite {@link Stream}s to avoid blocking indefinitely.
	 * 
	 * @param items The items to push into the PushStream
	 * @return A PushStream containing the items from the Java Stream
	 */
	public <T> PushStream<T> streamOf(Stream<T> items) {
		PushEventSource<T> pes = aec -> {
			AtomicBoolean closed = new AtomicBoolean(false);

			items.mapToLong(i -> {
				try {
					long returnValue = closed.get() ? -1 : aec.accept(data(i));
					if (returnValue < 0) {
						aec.accept(PushEvent.<T> close());
					}
					return returnValue;
				} catch (Exception e) {
					try {
						aec.accept(PushEvent.<T> error(e));
					} catch (Exception e2) {/* No further events needed */}
					return -1;
				}
			}).filter(i -> i < 0).findFirst().orElseGet(() -> {
				try {
					return aec.accept(PushEvent.<T> close());
				} catch (Exception e) {
					return -1;
				}
			});

			return () -> closed.set(true);
		};

		return this.<T> createUnbufferedStream(pes, null, null);
	}

	/**
	 * Create an Unbuffered {@link PushStream} from a Java {@link Stream} The
	 * data from the stream will be pushed into the PushStream asynchronously
	 * using the supplied Executor.
	 * 
	 * @param executor The worker to use to push items from the Stream into the
	 *            PushStream
	 * @param scheduler The scheduler to use to trigger timed events in the
	 *            PushStream
	 * @param items The items to push into the PushStream
	 * @return A PushStream containing the items from the Java Stream
	 */
	public <T> PushStream<T> streamOf(Executor executor,
			ScheduledExecutorService scheduler, Stream<T> items) {

		boolean closeExecutorOnClose;
		Executor workerToUse;
		if (executor == null) {
			workerToUse = Executors.newFixedThreadPool(2);
			closeExecutorOnClose = true;
		} else {
			workerToUse = executor;
			closeExecutorOnClose = false;
		}

		boolean releaseSchedulerOnClose;
		ScheduledExecutorService timerToUse;
		if (scheduler == null) {
			timerToUse = acquireScheduler();
			releaseSchedulerOnClose = true;
		} else {
			timerToUse = scheduler;
			releaseSchedulerOnClose = false;
		}

		PushStream<T> stream = new UnbufferedPushStreamImpl<T,BlockingQueue<PushEvent< ? extends T>>>(
				this, new PushStreamExecutors(workerToUse, timerToUse), aec -> {
					return () -> { /* No action to take */ };
				}) {

			@Override
			protected boolean begin() {
				if (super.begin()) {
					Iterator<T> it = items.iterator();

					executors.execute(() -> pushData(it));

					return true;
				}
				return false;
			}

			private void pushData(Iterator<T> it) {
				while (it.hasNext()) {
					try {
						long returnValue = closed.get() == CLOSED ? -1
								: handleEvent(data(it.next()));
						if (returnValue != 0) {
							if (returnValue < 0) {
								close();
								return;
							} else {
								executors.schedule(
										() -> executors
												.execute(() -> pushData(it)),
										returnValue, MILLISECONDS);
								return;
							}
						}
					} catch (Exception e) {
						close(error(e));
					}
				}
				close();
			}
		};

		return cleanupThreads(closeExecutorOnClose, workerToUse,
				releaseSchedulerOnClose, stream);
	}
}
