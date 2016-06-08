/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

import static org.osgi.util.pushstream.PushbackPolicyOption.LINEAR;
import static org.osgi.util.pushstream.QueuePolicyOption.FAIL;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * A factory for {@link PushStream} instances, and utility methods for handling
 * {@link PushEventSource}s and {@link PushEventConsumer}s
 */
public final class PushStreamProvider {

	private final Lock					lock	= new ReentrantLock(true);

	private int							schedulerReferences;

	private ScheduledExecutorService	scheduler;

	private ScheduledExecutorService acquireScheduler() {
		try {
			lock.lockInterruptibly();
			try {
				schedulerReferences += 1;

				if (schedulerReferences == 1) {
					scheduler = Executors.newSingleThreadScheduledExecutor();
				}
				return scheduler;
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
					scheduler.shutdown();
					scheduler = null;
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
		return createStream(eventSource, 2, null, new ArrayBlockingQueue<>(32),
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
		return new PushStreamBuilderImpl<T,U>(this, null, eventSource);
	}
	
	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	<T, U extends BlockingQueue<PushEvent< ? extends T>>> PushStream<T> createStream(
			PushEventSource<T> eventSource, int parallelism, Executor executor,
			U queue, QueuePolicy<T,U> queuePolicy,
			PushbackPolicy<T,U> pushbackPolicy) {

		if (eventSource == null) {
			throw new NullPointerException("There is no source of events");
		}

		if (parallelism <= 0) {
			parallelism = 2;
		}

		boolean closeExecutorOnClose;
		Executor toUse;
		if (executor == null) {
			toUse = Executors.newFixedThreadPool(2);
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

		if (pushbackPolicy == null) {
			pushbackPolicy = LINEAR.getPolicy(1000);
		}

		@SuppressWarnings("resource")
		PushStream<T> stream = new BufferedPushStreamImpl<>(this,
				acquireScheduler(), queue, parallelism, toUse, queuePolicy,
				pushbackPolicy, aec -> {
					try {
						return eventSource.open(aec);
					} catch (Exception e) {
						throw new RuntimeException(
								"Unable to connect to event source", e);
					}
				});

		stream = stream.onClose(() -> {
			if (closeExecutorOnClose) {
				((ExecutorService) toUse).shutdown();
			}
			releaseScheduler();
		}).map(Function.identity());
		return stream;
	}

	<T> PushStream<T> createUnbufferedStream(PushEventSource<T> eventSource,
			Executor executor) {

		boolean closeExecutorOnClose;
		Executor toUse;
		if (executor == null) {
			toUse = Executors.newFixedThreadPool(2);
			closeExecutorOnClose = true;
		} else {
			toUse = executor;
			closeExecutorOnClose = false;
		}

		@SuppressWarnings("resource")
		PushStream<T> stream = new UnbufferedPushStreamImpl<>(this, executor,
				acquireScheduler(), aec -> {
					try {
						return eventSource.open(aec);
					} catch (Exception e) {
						throw new RuntimeException(
								"Unable to connect to event source", e);
					}
				});

		stream = stream.onClose(() -> {
			if (closeExecutorOnClose) {
				((ExecutorService) toUse).shutdown();
			}
			releaseScheduler();
		}).map(Function.identity());

		return stream;
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
		throw new UnsupportedOperationException("Not yet implemented");
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
	 * @param stream
	 * 
	 * @return a {@link PushEventSource} backed by the {@link PushStream}
	 */
	public <T, U extends BlockingQueue<PushEvent< ? extends T>>> BufferBuilder<PushEventSource<T>,T,U> buildEventSourceFromStream(
			PushStream<T> stream) {
		throw new UnsupportedOperationException("Not yet implemented");
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
		throw new UnsupportedOperationException("Not yet implemented");
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
		throw new UnsupportedOperationException("Not yet implemented");
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
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 * Build a buffered {@link PushEventConsumer} with custom configuration.
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
	 * <p>
	 * Buffers are also useful as "circuit breakers". If a
	 * {@link QueuePolicyOption#FAIL} is used then a full buffer will request
	 * that the stream close, preventing an event storm from reaching the
	 * client.
	 * 
	 * @param delegate
	 * 
	 * @return a {@link PushEventConsumer} with a buffer directly before it
	 */
	public <T, U extends BlockingQueue<PushEvent< ? extends T>>> BufferBuilder<PushEventConsumer<T>,T,U> buildBufferedConsumer(
			PushEventConsumer<T> delegate) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
