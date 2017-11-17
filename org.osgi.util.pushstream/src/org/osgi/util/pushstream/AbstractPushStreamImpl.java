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
import static org.osgi.util.pushstream.AbstractPushStreamImpl.State.*;
import static org.osgi.util.pushstream.PushEventConsumer.*;

import java.time.Duration;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;
import org.osgi.util.promise.TimeoutException;
import org.osgi.util.pushstream.PushEvent.EventType;

abstract class AbstractPushStreamImpl<T> implements PushStream<T> {
	
	private final Function<T,T> IDENTITY = x -> x;

	static enum State {
		BUILDING, STARTED, CLOSED
	}
	
	protected final PushStreamProvider								psp;
	
	protected final PromiseFactory									promiseFactory;

	protected final AtomicReference<State> closed = new AtomicReference<>(BUILDING);
	
	protected final AtomicReference<PushEventConsumer<T>>			next			= new AtomicReference<>();
	
	protected final AtomicReference<Runnable> onCloseCallback = new AtomicReference<>();
	protected final AtomicReference<Consumer<? super Throwable>> onErrorCallback = new AtomicReference<>();

	protected abstract boolean begin();
	
	protected abstract void upstreamClose(PushEvent< ? > close);

	AbstractPushStreamImpl(PushStreamProvider psp,
			PromiseFactory promiseFactory) {
		this.psp = psp;
		this.promiseFactory = promiseFactory;
	}

	protected long handleEvent(PushEvent< ? extends T> event) {
		if(closed.get() != CLOSED) {
			try {
				if(event.isTerminal()) {
					close(event.nodata());
					return ABORT;
				} else {
					PushEventConsumer<T> consumer = next.get();
					long val;
					if(consumer == null) {
						//TODO log a warning
						val = CONTINUE;
					} else {
						val = consumer.accept(event);
					}
					if(val < 0) {
						close();
					}
					return val;
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		}
		return ABORT;
	}
	
	@Override
	public void close() {
		PushEvent<T> close = PushEvent.close();
		if (close(close, true)) {
			upstreamClose(close);
		}
	}
	
	protected boolean close(PushEvent<T> event) {
		return close(event, true);
	}

	protected boolean close(PushEvent<T> event, boolean sendDownStreamEvent) {
		if(!event.isTerminal()) {
			throw new IllegalArgumentException("The event " + event  + " is not a close event.");
		}
		if(closed.getAndSet(CLOSED) != CLOSED) {
			PushEventConsumer<T> aec = next.getAndSet(null);
			if (sendDownStreamEvent && aec != null) {
				try {
					aec.accept(event);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Runnable handler = onCloseCallback.getAndSet(null);
			if(handler != null) {
				try {
					handler.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (event.getType() == EventType.ERROR) {
				Consumer<? super Throwable> errorHandler = onErrorCallback.getAndSet(null);
				if(errorHandler != null) {
					try {
						errorHandler.accept(event.getFailure());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public PushStream<T> onClose(Runnable closeHandler) {
		if(onCloseCallback.compareAndSet(null, closeHandler)) {
			if(closed.get() == State.CLOSED && onCloseCallback.compareAndSet(closeHandler, null)) {
				closeHandler.run();
			}
		} else {
			throw new IllegalStateException("A close handler has already been defined for this stream object");
		}
		return this;
	}

	@Override
	public PushStream<T> onError(Consumer< ? super Throwable> closeHandler) {
		if(onErrorCallback.compareAndSet(null, closeHandler)) {
			if(closed.get() == State.CLOSED) { 
				//TODO log already closed
				onErrorCallback.set(null);
			}
		} else {
			throw new IllegalStateException("A close handler has already been defined for this stream object");
		}
		return this;
	}

	private void updateNext(PushEventConsumer<T> consumer) {
		if(!next.compareAndSet(null, consumer)) {
			throw new IllegalStateException("This stream has already been chained");
		} else if(closed.get() == CLOSED && next.compareAndSet(consumer, null)) {
			try {
				consumer.accept(PushEvent.close());
			} catch (Exception e) {
				//TODO log
				e.printStackTrace();
			}
		}
	}

	@Override
	public PushStream<T> filter(Predicate< ? super T> predicate) {
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		updateNext((event) -> {
			try {
				if (!event.isTerminal()) {
					if (predicate.test(event.getData())) {
						return eventStream.handleEvent(event);
					} else {
						return CONTINUE;
					}
				}
				return eventStream.handleEvent(event);
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public <R> PushStream<R> map(Function< ? super T, ? extends R> mapper) {
		
		AbstractPushStreamImpl<R> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		updateNext(event -> {
			try {
				if (!event.isTerminal()) {
					return eventStream.handleEvent(
							PushEvent.data(mapper.apply(event.getData())));
				} else {
					return eventStream.handleEvent(event.nodata());
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public <R> PushStream<R> asyncMap(int n, int delay,
			Function< ? super T,Promise< ? extends R>> mapper)
			throws IllegalArgumentException, NullPointerException {

		AbstractPushStreamImpl<R> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		Semaphore s = new Semaphore(n);
		updateNext(event -> {
			try {
				if (event.isTerminal()) {
					s.acquire(n);
					eventStream.close(event.nodata());
					return ABORT;
				}

				s.acquire(1);

				Promise< ? extends R> p = mapper.apply(event.getData());
				p.thenAccept(d -> promiseFactory.executor().execute(() -> {
					try {
							if (eventStream
									.handleEvent(PushEvent.data(d)) < 0) {
								PushEvent<R> close = PushEvent.close();
								eventStream.close(close);
								// Upstream close is needed as we have no direct
								// backpressure
								upstreamClose(close);
							}
					} finally {
						s.release();
					}
				})).onFailure(t -> promiseFactory.executor().execute(() -> {
					PushEvent<T> error = PushEvent.error(t);
					close(error);
					// Upstream close is needed as we have no direct
					// backpressure
					upstreamClose(error);
				}));

				// The number active before was one less than the active number
				int activePromises = Math.max(0, n - s.availablePermits() - 1);
				return (activePromises + s.getQueueLength()) * delay;
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});

		return eventStream;
	}

	@Override
	public <R> PushStream<R> flatMap(
			Function< ? super T, ? extends PushStream< ? extends R>> mapper) {
		AbstractPushStreamImpl<R> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);

		PushEventConsumer<R> consumer = e -> {
			switch (e.getType()) {
				case ERROR :
					close(e.nodata());
					return ABORT;
				case CLOSE :
					// Close should allow the next flat mapped entry
					// without closing the stream;
					return ABORT;
				case DATA :
					long returnValue = eventStream.handleEvent(e);
					if (returnValue < 0) {
						close();
						return ABORT;
					}
					return returnValue;
				default :
					throw new IllegalArgumentException(
							"The event type " + e.getType() + " is unknown");
			}
		};

		updateNext(event -> {
			try {
				if (!event.isTerminal()) {
					PushStream< ? extends R> mappedStream = mapper
							.apply(event.getData());

					return mappedStream.forEachEvent(consumer)
							.getValue()
							.longValue();
				} else {
					return eventStream.handleEvent(event.nodata());
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public PushStream<T> distinct() {
		Set<T> set = Collections.<T>newSetFromMap(new ConcurrentHashMap<>());
		return filter(set::add);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PushStream<T> sorted() {
		return sorted((Comparator)Comparator.naturalOrder());
	}

	@Override
	public PushStream<T> sorted(Comparator< ? super T> comparator) {
		List<T> list = Collections.synchronizedList(new ArrayList<>());
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		updateNext(event -> {
			try {
				switch(event.getType()) {
					case DATA : 
						list.add(event.getData());
						return CONTINUE;
					case CLOSE :
						list.sort(comparator);
						sorted: for (T t : list) {
							if (eventStream
									.handleEvent(PushEvent.data(t)) < 0) {
								break sorted;
							}
						}
						// Fall through
					case ERROR :
						eventStream.handleEvent(event);
						return ABORT;
				}
				return eventStream.handleEvent(event.nodata());
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public PushStream<T> limit(long maxSize) {
		if(maxSize <= 0) {
			throw new IllegalArgumentException("The limit must be greater than zero");
		}
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		AtomicLong counter = new AtomicLong(maxSize);
		updateNext(event -> {
			try {
				if (!event.isTerminal()) {
					long count = counter.decrementAndGet();
					if (count > 0) {
						return eventStream.handleEvent(event);
					} else if (count == 0) {
						eventStream.handleEvent(event);
					}
					return ABORT;
				} else {
					return eventStream.handleEvent(event.nodata());
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}
	
	@Override
	public PushStream<T> limit(Duration maxTime) {
		
		Runnable start = () -> promiseFactory.scheduledExecutor().schedule(
				() -> close(),
				maxTime.toNanos(), NANOSECONDS);

		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<T>(
				psp, promiseFactory, this) {
			@Override
			protected void beginning() {
				start.run();
			}
		};
		updateNext((event) -> {
			try {
				return eventStream.handleEvent(event);
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public PushStream<T> timeout(Duration maxTime) {

		AtomicLong lastTime = new AtomicLong();
		long timeout = maxTime.toNanos();

		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<T>(
				psp, promiseFactory, this) {
			@Override
			protected void beginning() {
				lastTime.set(System.nanoTime());
				promiseFactory.scheduledExecutor().schedule(
						() -> check(lastTime, timeout), timeout,
						NANOSECONDS);
			}
		};
		updateNext((event) -> {
			try {
				return eventStream.handleEvent(event);
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	void check(AtomicLong lastTime, long timeout) {
		long now = System.nanoTime();

		long elapsed = now - lastTime.get();

		if (elapsed < timeout) {
			promiseFactory.scheduledExecutor().schedule(
					() -> check(lastTime, timeout),
					timeout - elapsed, NANOSECONDS);
		} else {
			PushEvent<T> error = PushEvent.error(new TimeoutException());
			close(error);
			// Upstream close is needed as we have no direct backpressure
			upstreamClose(error);
		}
	}

	@Override
	public PushStream<T> skip(long n) {
		if (n < 0) {
			throw new IllegalArgumentException(
					"The number to skip must be greater than or equal to zero");
		}
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		AtomicLong counter = new AtomicLong(n);
		updateNext(event -> {
			try {
				if (!event.isTerminal()) {
					if (counter.get() > 0 && counter.decrementAndGet() >= 0) {
						return CONTINUE;
					} else {
						return eventStream.handleEvent(event);
					} 				
				} else {
					return eventStream.handleEvent(event.nodata());
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public PushStream<T> fork(int n, int delay, Executor ex) {
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, new PromiseFactory(Objects.requireNonNull(ex),
						promiseFactory.scheduledExecutor()),
				this);
		Semaphore s = new Semaphore(n);
		updateNext(event -> {
			try {
				if (event.isTerminal()) {
					s.acquire(n);
					eventStream.close(event.nodata());
					return ABORT;
				}
	
				s.acquire(1);
	
				ex.execute(() -> {
					try {
						if (eventStream.handleEvent(event) < 0) {
							PushEvent<T> close = PushEvent.close();
							eventStream.close(close);
							// Upstream close is needed as we have no direct
							// backpressure
							upstreamClose(close);
						}
					} catch (Exception e1) {
						PushEvent<T> error = PushEvent.error(e1);
						close(error);
						// Upstream close is needed as we have no direct
						// backpressure
						upstreamClose(error);
					} finally {
						s.release(1);
					}
				});
	
				return s.getQueueLength() * delay;
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});

		return eventStream;
	}
	
	@Override
	public PushStream<T> buffer() {
		return psp.createStream(c -> {
			forEachEvent(c);
			return this;
		});
	}

	@Override
	public <U extends BlockingQueue<PushEvent< ? extends T>>> PushStreamBuilder<T,U> buildBuffer() {
		return psp.buildStream(c -> {
			forEachEvent(c);
			return this;
		});
	}

	@Override
	public PushStream<T> merge(
			PushEventSource< ? extends T> source) {
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		AtomicInteger count = new AtomicInteger(2);
		PushEventConsumer<T> consumer = event -> {
			try {
				if (!event.isTerminal()) {
					return eventStream.handleEvent(event);
				}
	
				if (count.decrementAndGet() == 0) {
					eventStream.handleEvent(event.nodata());
					return ABORT;
				}
				return CONTINUE;
			} catch (Exception e) {
				PushEvent<T> error = PushEvent.error(e);
				close(error);
				eventStream.close(event.nodata());
				return ABORT;
			}
		};
		updateNext(consumer);
		AutoCloseable second;
		try {
			second = source.open((PushEvent< ? extends T> event) -> {
				return consumer.accept(event);
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(
					"Unable to merge events as the event source could not be opened.",
					e);
		}
		
		return eventStream.onClose(() -> {
			try {
				second.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}).map(IDENTITY);
	}

	@Override
	public PushStream<T> merge(PushStream< ? extends T> source) {

		AtomicInteger count = new AtomicInteger(2);
		Consumer<AbstractPushStreamImpl<T>> start = downstream -> {
			PushEventConsumer<T> consumer = e -> {
				long toReturn;
				try {
					if (!e.isTerminal()) {
						toReturn = downstream.handleEvent(e);
					} else if (count.decrementAndGet() == 0) {
						downstream.handleEvent(e);
						toReturn = ABORT;
					} else {
						return ABORT;
					}
				} catch (Exception ex) {
					try {
						downstream.handleEvent(PushEvent.error(ex));
					} catch (Exception ex2) { /* Just ignore this */}
					toReturn = ABORT;
				}
				if (toReturn < 0) {
					try {
						close();
					} catch (Exception ex2) { /* Just ignore this */}
					try {
						source.close();
					} catch (Exception ex2) { /* Just ignore this */}
				}
				return toReturn;
			};
			forEachEvent(consumer);
			source.forEachEvent(consumer);
		};

		@SuppressWarnings("resource")
		AbstractPushStreamImpl<T> eventStream = new AbstractPushStreamImpl<T>(
				psp, promiseFactory) {
			@Override
			protected boolean begin() {
				if (closed.compareAndSet(BUILDING, STARTED)) {
					start.accept(this);
					return true;
				}
				return false;
			}

			@Override
			protected void upstreamClose(PushEvent< ? > close) {
				AbstractPushStreamImpl.this.upstreamClose(close);
				source.close();
			}
		};
		

		return eventStream.onClose(() -> {
			try {
				close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				source.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}).map(IDENTITY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PushStream<T>[] split(Predicate< ? super T>... predicates) {
		Predicate<? super T>[] tests = Arrays.copyOf(predicates, predicates.length);
		AbstractPushStreamImpl<T>[] rsult = new AbstractPushStreamImpl[tests.length];
		for(int i = 0; i < tests.length; i++) {
			rsult[i] = new IntermediatePushStreamImpl<>(psp, promiseFactory, this);
		}

		Boolean[] array = new Boolean[tests.length];
		Arrays.fill(array, Boolean.TRUE);
		AtomicReferenceArray<Boolean> off = new AtomicReferenceArray<>(array);

		AtomicInteger count = new AtomicInteger(tests.length);
		updateNext(event -> {
			if (!event.isTerminal()) {
				long delay = CONTINUE;
				for (int i = 0; i < tests.length; i++) {
					try {
						if (off.get(i).booleanValue()
								&& tests[i].test(event.getData())) {
							long accept = rsult[i].handleEvent(event);
							if (accept < 0) {
								off.set(i, Boolean.TRUE);
								count.decrementAndGet();
							} else if (accept > delay) {
								accept = delay;
							}
						}
					} catch (Exception e) {
						try {
							rsult[i].close(PushEvent.error(e));
						} catch (Exception e2) {
							//TODO log
						}
						off.set(i, Boolean.TRUE);
					}
				}
				if (count.get() == 0)
					return ABORT;

				return delay;
			}
			for (AbstractPushStreamImpl<T> as : rsult) {
				try {
					as.handleEvent(event.nodata());
				} catch (Exception e) {
					try {
						as.close(PushEvent.error(e));
					} catch (Exception e2) {
						//TODO log
					}
				}
			}
			return ABORT;
		});
		return Arrays.copyOf(rsult, tests.length);
	}

	@Override
	public PushStream<T> sequential() {
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		Lock lock = new ReentrantLock();
		updateNext((event) -> {
			try {
				lock.lock();
				try {
					return eventStream.handleEvent(event);
				} finally {
					lock.unlock();
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public <R> PushStream<R> coalesce(
			Function< ? super T,Optional<R>> accumulator) {
		AbstractPushStreamImpl<R> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		updateNext((event) -> {
			try {
				if (!event.isTerminal()) {
					Optional<PushEvent<R>> coalesced = accumulator
							.apply(event.getData()).map(PushEvent::data);
					if (coalesced.isPresent()) {
						try {
							return eventStream.handleEvent(coalesced.get());
						} catch (Exception ex) {
							close(PushEvent.error(ex));
							return ABORT;
						}
					} else {
						return CONTINUE;
					}
				}
				return eventStream.handleEvent(event.nodata());
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public <R> PushStream<R> coalesce(int count, Function<Collection<T>,R> f) {
		if (count <= 0)
			throw new IllegalArgumentException(
					"A coalesce operation must collect a positive number of events");
		// This could be optimised to only use a single collection queue.
		// It would save some GC, but is it worth it?
		return coalesce(() -> count, f);
	}

	@Override
	public <R> PushStream<R> coalesce(IntSupplier count,
			Function<Collection<T>,R> f) {
		AtomicReference<Queue<T>> queueRef = new AtomicReference<Queue<T>>(
				null);

		Runnable init = () -> queueRef
				.set(getQueueForInternalBuffering(count.getAsInt()));

		@SuppressWarnings("resource")
		AbstractPushStreamImpl<R> eventStream = new IntermediatePushStreamImpl<R>(
				psp, promiseFactory, this) {
			@Override
			protected void beginning() {
				init.run();
			}
		};

		AtomicBoolean endPending = new AtomicBoolean();
		Object lock = new Object();
		updateNext((event) -> {
			try {
				Queue<T> queue;
				if (!event.isTerminal()) {
					synchronized (lock) {
						for (;;) {
							queue = queueRef.get();
							if (queue == null) {
								if (endPending.get()) {
									return ABORT;
								} else {
									continue;
								}
							} else if (queue.offer(event.getData())) {
								return CONTINUE;
							} else {
								queueRef.lazySet(null);
								break;
							}
						}
					}

					queueRef.set(
							getQueueForInternalBuffering(count.getAsInt()));

					// This call is on the same thread and so must happen
					// outside
					// the synchronized block.
					return aggregateAndForward(f, eventStream, event,
							queue);
				} else {
					synchronized (lock) {
						queue = queueRef.get();
						queueRef.lazySet(null);
						endPending.set(true);
					}
					if (queue != null) {
						eventStream.handleEvent(
								PushEvent.data(f.apply(queue)));
					}
				}
				return eventStream.handleEvent(event.nodata());
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	private <R> long aggregateAndForward(Function<Collection<T>,R> f,
			AbstractPushStreamImpl<R> eventStream,
			PushEvent< ? extends T> event, Queue<T> queue) throws Exception {
		if (!queue.offer(event.getData())) {
			((ArrayQueue<T>) queue).forcePush(event.getData());
		}
		return eventStream.handleEvent(PushEvent.data(f.apply(queue)));
	}
	
	
	@Override
	public <R> PushStream<R> window(Duration time,
			Function<Collection<T>,R> f) {
		return window(time, promiseFactory.executor(), f);
	}

	@Override
	public <R> PushStream<R> window(Duration time, Executor executor,
			Function<Collection<T>,R> f) {
		return window(() -> time, () -> 0, executor, (t, c) -> {
			try {
				return f.apply(c);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public <R> PushStream<R> window(Supplier<Duration> time,
			IntSupplier maxEvents,
			BiFunction<Long,Collection<T>,R> f) {
		return window(time, maxEvents, promiseFactory.executor(), f);
	}

	@Override
	public <R> PushStream<R> window(Supplier<Duration> time,
			IntSupplier maxEvents, Executor ex,
			BiFunction<Long,Collection<T>,R> f) {

		AtomicLong timestamp = new AtomicLong();
		AtomicLong previousWindowSize = new AtomicLong();
		AtomicLong counter = new AtomicLong();
		Object lock = new Object();
		AtomicReference<Queue<T>> queueRef = new AtomicReference<Queue<T>>(
				null);

		// This code is declared as a separate block to avoid any confusion
		// about which instance's methods and variables are in scope
		Consumer<AbstractPushStreamImpl<R>> begin = p -> {

			synchronized (lock) {
				timestamp.lazySet(System.nanoTime());
				long count = counter.get();


				long windowSize = time.get().toNanos();
				previousWindowSize.set(windowSize);
				promiseFactory.scheduledExecutor().schedule(
						getWindowTask(p, f, time, maxEvents, lock, count,
								queueRef, timestamp, counter,
								previousWindowSize, ex),
						windowSize, NANOSECONDS);
			}

			queueRef.set(getQueueForInternalBuffering(maxEvents.getAsInt()));
		};

		@SuppressWarnings("resource")
		AbstractPushStreamImpl<R> eventStream = new IntermediatePushStreamImpl<R>(
				psp, new PromiseFactory(Objects.requireNonNull(ex),
						promiseFactory.scheduledExecutor()),
				this) {
			@Override
			protected void beginning() {
				begin.accept(this);
			}
		};

		AtomicBoolean endPending = new AtomicBoolean(false);
		updateNext((event) -> {
			try {
				if (eventStream.closed.get() == CLOSED) {
					return ABORT;
				}
				Queue<T> queue;
				if (!event.isTerminal()) {
					long elapsed;
					long newCount;
					synchronized (lock) {
						for (;;) {
							queue = queueRef.get();
							if (queue == null) {
								if (endPending.get()) {
									return ABORT;
								} else {
									continue;
								}
							} else if (queue.offer(event.getData())) {
								return CONTINUE;
							} else {
								queueRef.lazySet(null);
								break;
							}
						}

						long now = System.nanoTime();
						elapsed = now - timestamp.get();
						timestamp.lazySet(now);
						newCount = counter.get() + 1;
						counter.lazySet(newCount);

						// This is a non-blocking call, and must happen in the
						// synchronized block to avoid re=ordering the executor
						// enqueue with a subsequent incoming close operation
						aggregateAndForward(f, eventStream, event, queue,
								ex, elapsed);
					}
					// These must happen outside the synchronized block as we
					// call out to user code
					queueRef.set(
							getQueueForInternalBuffering(maxEvents.getAsInt()));
					long nextWindow = time.get().toNanos();
					long backpressure = previousWindowSize.getAndSet(nextWindow)
							- elapsed;
					promiseFactory.scheduledExecutor().schedule(
							getWindowTask(eventStream, f, time, maxEvents, lock,
									newCount, queueRef, timestamp, counter,
									previousWindowSize, ex),
							nextWindow, NANOSECONDS);

					return backpressure < 0 ? CONTINUE
							: NANOSECONDS.toMillis(backpressure);
				} else {
					long elapsed;
					synchronized (lock) {
						queue = queueRef.get();
						queueRef.lazySet(null);
						endPending.set(true);
						long now = System.nanoTime();
						elapsed = now - timestamp.get();
						counter.lazySet(counter.get() + 1);
					}
					Collection<T> collected = queue == null ? emptyList()
							: queue;
					ex.execute(() -> {
						try {
							eventStream
									.handleEvent(PushEvent.data(f.apply(
											Long.valueOf(NANOSECONDS
													.toMillis(elapsed)),
											collected)));
						} catch (Exception e) {
							PushEvent<T> error = PushEvent.error(e);
							close(error);
							// Upstream close is needed as we have no direct
							// backpressure
							upstreamClose(error);
						}
					});
				}
				ex.execute(() -> eventStream.handleEvent(event.nodata()));
				return ABORT;
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	protected Queue<T> getQueueForInternalBuffering(int size) {
		if (size == 0) {
			return new LinkedList<T>();
		} else {
			return new ArrayQueue<>(size - 1);
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * A special queue that keeps one element in reserve and can have that last
	 * element set using forcePush. After the element is set the capacity is
	 * permanently increased by one and cannot grow further.
	 * 
	 * @param <E> The element type
	 */
	private static class ArrayQueue<E> extends AbstractQueue<E>
			implements Queue<E> {

		final Object[]	store;

		int				normalLength;

		int				nextIndex;

		int				size;

		ArrayQueue(int capacity) {
			store = new Object[capacity + 1];
			normalLength = store.length - 1;
		}

		@Override
		public boolean offer(E e) {
			if (e == null)
				throw new NullPointerException("Null values are not supported");
			if (size < normalLength) {
				store[nextIndex] = e;
				size++;
				nextIndex++;
				nextIndex = nextIndex % normalLength;
				return true;
			}
			return false;
		}

		public void forcePush(E e) {
			store[normalLength] = e;
			normalLength++;
			size++;
		}

		@Override
		public E poll() {
			if (size == 0) {
				return null;
			} else {
				int idx = nextIndex - size;
				if (idx < 0) {
					idx += normalLength;
				}
				E value = (E) store[idx];
				store[idx] = null;
				size--;
				return value;
			}
		}

		@Override
		public E peek() {
			if (size == 0) {
				return null;
			} else {
				int idx = nextIndex - size;
				if (idx < 0) {
					idx += normalLength;
				}
				return (E) store[idx];
			}
		}

		@Override
		public Iterator<E> iterator() {
			final int previousNext = nextIndex;
			return new Iterator<E>() {

				int idx;

				int	remaining	= size;

				{
					idx = nextIndex - size;
					if (idx < 0) {
						idx += normalLength;
					}
				}

				@Override
				public boolean hasNext() {
					if (nextIndex != previousNext) {
						throw new ConcurrentModificationException(
								"The queue was concurrently modified");
					}
					return remaining > 0;
				}

				@Override
				public E next() {
					if (!hasNext()) {
						throw new NoSuchElementException(
								"The iterator has no more values");
					}
					E value = (E) store[idx];
					idx++;
					remaining--;
					if (idx == normalLength) {
						idx = 0;
					}
					return value;
				}

			};
		}

		@Override
		public int size() {
			return size;
		}

	}

	private <R> Runnable getWindowTask(AbstractPushStreamImpl<R> eventStream,
			BiFunction<Long,Collection<T>,R> f, Supplier<Duration> time,
			IntSupplier maxEvents, Object lock, long expectedCounter,
			AtomicReference<Queue<T>> queueRef, AtomicLong timestamp,
			AtomicLong counter, AtomicLong previousWindowSize,
			Executor executor) {
		return () -> {

			Queue<T> queue = null;
			long elapsed;
			synchronized (lock) {
				
				if (counter.get() != expectedCounter) {
					return;
				}
				counter.lazySet(expectedCounter + 1);

				long now = System.nanoTime();
				elapsed = now - timestamp.get();
				timestamp.lazySet(now);

				queue = queueRef.get();
				queueRef.lazySet(null);

				// This is a non-blocking call, and must happen in the
				// synchronized block to avoid re=ordering the executor
				// enqueue with a subsequent incoming close operation

				Collection<T> collected = queue == null ? emptyList() : queue;
				executor.execute(() -> {
					try {
						eventStream.handleEvent(PushEvent.data(f.apply(
								Long.valueOf(NANOSECONDS.toMillis(elapsed)),
								collected)));
					} catch (Exception e) {
						PushEvent<T> error = PushEvent.error(e);
						close(error);
						// Upstream close is needed as we have no direct
						// backpressure
						upstreamClose(error);
					}
				});
			}

			// These must happen outside the synchronized block as we
			// call out to user code
			long nextWindow = time.get().toNanos();
			previousWindowSize.set(nextWindow);
			queueRef.set(getQueueForInternalBuffering(maxEvents.getAsInt()));
			promiseFactory.scheduledExecutor().schedule(
					getWindowTask(eventStream, f, time, maxEvents, lock,
							expectedCounter + 1, queueRef, timestamp, counter,
							previousWindowSize, executor),
					nextWindow, NANOSECONDS);
		};
	}

	private <R> void aggregateAndForward(BiFunction<Long,Collection<T>,R> f,
			AbstractPushStreamImpl<R> eventStream,
			PushEvent< ? extends T> event, Queue<T> queue, Executor executor,
			long elapsed) {
		executor.execute(() -> {
			try {
				if (!queue.offer(event.getData())) {
					((ArrayQueue<T>) queue).forcePush(event.getData());
				}
				long result = eventStream.handleEvent(PushEvent.data(
						f.apply(Long.valueOf(NANOSECONDS.toMillis(elapsed)),
								queue)));
				if (result < 0) {
					close();
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
			}
		});
	}

	@Override
	public PushStream<T> adjustBackPressure(LongUnaryOperator adjustment) {
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		updateNext(event -> {
			try {
				long bp = eventStream.handleEvent(event);
				if (event.isTerminal()) {
					return ABORT;
				} else {
					return bp < 0 ? bp : adjustment.applyAsLong(bp);
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public PushStream<T> adjustBackPressure(
			ToLongBiFunction<T,Long> adjustment) {
		AbstractPushStreamImpl<T> eventStream = new IntermediatePushStreamImpl<>(
				psp, promiseFactory, this);
		updateNext(event -> {
			try {
				long bp = eventStream.handleEvent(event);
				if (event.isTerminal()) {
					return ABORT;
				} else {
					return bp < 0 ? bp
							: adjustment.applyAsLong(event.getData(),
									Long.valueOf(bp));
				}
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		return eventStream;
	}

	@Override
	public Promise<Void> forEach(Consumer< ? super T> action) {
		Deferred<Void> d = promiseFactory.deferred();
		updateNext((event) -> {
				try {
					switch(event.getType()) {
						case DATA:
							action.accept(event.getData());
							return CONTINUE;
						case CLOSE:
							d.resolve(null);
							break;
						case ERROR:
							d.fail(event.getFailure());
							break;
					}
				close(event.nodata());
					return ABORT;
				} catch (Exception e) {
				close(PushEvent.error(e));
					return ABORT;
				}
			});
		begin();
		return d.getPromise();
	}

	@Override
	public Promise<Object[]> toArray() {
		return collect(Collectors.toList())
				.map(List::toArray);
	}

	@Override
	public <A extends T> Promise<A[]> toArray(IntFunction<A[]> generator) {
		return collect(Collectors.toList())
				.map(l -> l.toArray(generator.apply(l.size())));
	}

	@Override
	public Promise<T> reduce(T identity, BinaryOperator<T> accumulator) {
		Deferred<T> d = promiseFactory.deferred();
		AtomicReference<T> iden = new AtomicReference<T>(identity);

		updateNext(event -> {
			try {
				switch(event.getType()) {
					case DATA:
						iden.accumulateAndGet(event.getData(), accumulator);
						return CONTINUE;
					case CLOSE:
						d.resolve(iden.get());
						break;
					case ERROR:
						d.fail(event.getFailure());
						break;
				}
				close(event.nodata());
				return ABORT;
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		begin();
		return d.getPromise();
	}

	@Override
	public Promise<Optional<T>> reduce(BinaryOperator<T> accumulator) {
		Deferred<Optional<T>> d = promiseFactory.deferred();
		AtomicReference<T> iden = new AtomicReference<T>(null);

		updateNext(event -> {
			try {
				switch(event.getType()) {
					case DATA:
						if (!iden.compareAndSet(null, event.getData()))
							iden.accumulateAndGet(event.getData(), accumulator);
						return CONTINUE;
					case CLOSE:
						d.resolve(Optional.ofNullable(iden.get()));
						break;
					case ERROR:
						d.fail(event.getFailure());
						break;
				}
				close(event.nodata());
				return ABORT;
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		begin();
		return d.getPromise();
	}

	@Override
	public <U> Promise<U> reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
		Deferred<U> d = promiseFactory.deferred();
		AtomicReference<U> iden = new AtomicReference<>(identity);

		updateNext(event -> {
			try {
				switch(event.getType()) {
					case DATA:
						iden.updateAndGet((e) -> accumulator.apply(e, event.getData()));
						return CONTINUE;
					case CLOSE:
						d.resolve(iden.get());
						break;
					case ERROR:
						d.fail(event.getFailure());
						break;
				}
				close(event.nodata());
				return ABORT;
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		begin();
		return d.getPromise();
	}

	@Override
	public <R, A> Promise<R> collect(Collector<? super T, A, R> collector) {
		A result = collector.supplier().get();
		BiConsumer<A, ? super T> accumulator = collector.accumulator();
		Deferred<R> d = promiseFactory.deferred();
		PushEventConsumer<T> consumer;

		if (collector.characteristics()
				.contains(Collector.Characteristics.CONCURRENT)) {
			consumer = event -> {
				try {
					switch (event.getType()) {
						case DATA :
							accumulator.accept(result, event.getData());
							return CONTINUE;
						case CLOSE :
							d.resolve(collector.finisher().apply(result));
							break;
						case ERROR :
							d.fail(event.getFailure());
							break;
					}
					close(event.nodata());
					return ABORT;
				} catch (Exception e) {
					close(PushEvent.error(e));
					return ABORT;
				}
			};
		} else {
			consumer = event -> {
				try {
					switch (event.getType()) {
						case DATA :
							synchronized (result) {
								accumulator.accept(result, event.getData());
							}
							return CONTINUE;
						case CLOSE :
							d.resolve(collector.finisher().apply(result));
							break;
						case ERROR :
							d.fail(event.getFailure());
							break;
					}
					close(event.nodata());
					return ABORT;
				} catch (Exception e) {
					close(PushEvent.error(e));
					return ABORT;
				}
			};
		}

		updateNext(consumer);
		begin();
		return d.getPromise();
	}

	@Override
	public Promise<Optional<T>> min(Comparator<? super T> comparator)  {
		return reduce((a, b) -> comparator.compare(a, b) <= 0 ? a : b);
	}

	@Override
	public Promise<Optional<T>> max(Comparator<? super T> comparator) {
		return reduce((a, b) -> comparator.compare(a, b) > 0 ? a : b);
	}

	@Override
	public Promise<Long> count() {
		Deferred<Long> d = promiseFactory.deferred();
		LongAdder counter = new LongAdder();
		updateNext((event) -> {
				try {
					switch(event.getType()) {
						case DATA:
						counter.add(1);
							return CONTINUE;
						case CLOSE:
						d.resolve(Long.valueOf(counter.sum()));
							break;
						case ERROR:
							d.fail(event.getFailure());
							break;
					}
					close(event.nodata());
					return ABORT;
				} catch (Exception e) {
				close(PushEvent.error(e));
					return ABORT;
				}
			});
		begin();
		return d.getPromise();
	}

	@Override
	public Promise<Boolean> anyMatch(Predicate<? super T> predicate) {
		return filter(predicate).findAny()
			.map(Optional::isPresent);
	}

	@Override
	public Promise<Boolean> allMatch(Predicate<? super T> predicate) {
		return filter(x -> !predicate.test(x)).findAny()
				.map(o -> Boolean.valueOf(!o.isPresent()));
	}

	@Override
	public Promise<Boolean> noneMatch(Predicate<? super T> predicate) {
		return filter(predicate).findAny()
				.map(o -> Boolean.valueOf(!o.isPresent()));
	}

	@Override
	public Promise<Optional<T>> findFirst() {
		Deferred<Optional<T>> d = promiseFactory.deferred();
		updateNext((event) -> {
				try {
					Optional<T> o = null;
					switch(event.getType()) {
						case DATA:
							o = Optional.of(event.getData());
							break;
						case CLOSE:
							o = Optional.empty();
							break;
						case ERROR:
							d.fail(event.getFailure());
							return ABORT;
					}
					if(!d.getPromise().isDone())
						d.resolve(o);
					return ABORT;
				} catch (Exception e) {
				close(PushEvent.error(e));
					return ABORT;
				}
			});
		begin();
		return d.getPromise();
	}

	@Override
	public Promise<Optional<T>> findAny() {
		return findFirst();
	}

	@Override
	public Promise<Long> forEachEvent(PushEventConsumer< ? super T> action) {
		Deferred<Long> d = promiseFactory.deferred();
		LongAdder la = new LongAdder();
		updateNext((event) -> {
			try {
				switch(event.getType()) {
					case DATA:
						long value = action.accept(event);
						la.add(value);
						return value;
					case CLOSE:
						try {
							action.accept(event);
							d.resolve(Long.valueOf(la.sum()));
						} catch (Exception e) {
							d.fail(e);
						}
						break;
					case ERROR:
						try {
							action.accept(event);
							d.fail(event.getFailure());
						} catch (Exception e) {
							d.fail(e);
						}
						break;
				}
				return ABORT;
			} catch (Exception e) {
				close(PushEvent.error(e));
				return ABORT;
			}
		});
		begin();
		return d.getPromise();
	}

}
