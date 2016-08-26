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

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.util.promise.Promise;

/**
 * A Push Stream fulfills the same role as the Java 8 stream but it reverses the
 * control direction. The Java 8 stream is pull based and this is push based. A
 * Push Stream makes it possible to build a pipeline of transformations using a
 * builder kind of model. Just like streams, it provides a number of terminating
 * methods that will actually open the channel and perform the processing until
 * the channel is closed (The source sends a Close event). The results of the
 * processing will be send to a Promise, just like any error events. A stream
 * can be used multiple times. The Push Stream represents a pipeline. Upstream
 * is in the direction of the source, downstream is in the direction of the
 * terminating method. Events are sent downstream asynchronously with no
 * guarantee for ordering or concurrency. Methods are available to provide
 * serialization of the events and splitting in background threads.
 * 
 * @param <T> The Payload type
 */
@ProviderType
public interface PushStream<T> extends AutoCloseable {

	/**
	 * Must be run after the channel is closed. This handler will run after the
	 * downstream methods have processed the close event and before the upstream
	 * methods have closed.
	 * 
	 * @param closeHandler Will be called on close
	 * @return This stream
	 */
	PushStream<T> onClose(Runnable closeHandler);

	/**
	 * Must be run after the channel is closed. This handler will run after the
	 * downstream methods have processed the close event and before the upstream
	 * methods have closed.
	 * 
	 * @param closeHandler Will be called on close
	 * @return This stream
	 */
	PushStream<T> onError(Consumer< ? super Throwable> closeHandler);

	/**
	 * Only pass events downstream when the predicate tests true.
	 * 
	 * @param predicate The predicate that is tested (not null)
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> filter(Predicate< ? super T> predicate);

	/**
	 * Map a payload value.
	 * 
	 * @param mapper The map function
	 * @return Builder style (can be a new or the same object)
	 */
	<R> PushStream<R> map(Function< ? super T, ? extends R> mapper);

	/**
	 * Flat map the payload value (turn one event into 0..n events of
	 * potentially another type).
	 * 
	 * @param mapper The flat map function
	 * @return Builder style (can be a new or the same object)
	 */
	<R> PushStream<R> flatMap(
			Function< ? super T, ? extends PushStream< ? extends R>> mapper);

	/**
	 * Remove any duplicates. Notice that this can be expensive in a large
	 * stream since it must track previous payloads.
	 * 
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> distinct();

	/**
	 * Sorted the elements, assuming that T extends Comparable. This is of
	 * course expensive for large or infinite streams since it requires
	 * buffering the stream until close.
	 * 
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> sorted();

	/**
	 * Sorted the elements with the given comparator. This is of course
	 * expensive for large or infinite streams since it requires buffering the
	 * stream until close.
	 * 
	 * @param comparator
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> sorted(Comparator< ? super T> comparator);

	/**
	 * Automatically close the channel after the maxSize number of elements is
	 * received.
	 * 
	 * @param maxSize Maximum number of elements has been received
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> limit(long maxSize);

	/**
	 * Skip a number of events in the channel.
	 * 
	 * @param n number of elements to skip
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> skip(long n);

	/**
	 * Execute the downstream events in up to n background threads. If more
	 * requests are outstanding apply delay * nr of delayed threads back
	 * pressure. A downstream channel that is closed or throws an exception will
	 * cause all execution to cease and the stream to close
	 * 
	 * @param n number of simultaneous background threads to use
	 * @param delay Nr of ms/thread that is queued back pressure
	 * @param e an executor to use for the background threads.
	 * @return Builder style (can be a new or the same object)
	 * @throws IllegalArgumentException if the number of threads is < 1 or the
	 *             delay is < 0
	 * @throws NullPointerException if the Executor is null
	 */
	PushStream<T> fork(int n, int delay, Executor e)
			throws IllegalArgumentException, NullPointerException;

	/**
	 * Buffer the events in a queue using default values for the queue size and
	 * other behaviours. Buffered work will be processed asynchronously in the
	 * rest of the chain. Buffering also blocks the transmission of back
	 * pressure to previous elements in the chain, although back pressure is
	 * honoured by the buffer.
	 * <p>
	 * Buffers are useful for "bursty" event sources which produce a number of
	 * events close together, then none for some time. These bursts can
	 * sometimes overwhelm downstream event consumers. Buffering will not,
	 * however, protect downstream components from a source which produces
	 * events faster than they can be consumed. For fast sources
	 * {@link #filter(Predicate)} and {@link #coalesce(int, Function)}
	 * {@link #fork(int, int, Executor)} are better choices.
	 * 
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> buffer();

	/**
	 * Build a buffer to enqueue events in a queue using custom values for the
	 * queue size and other behaviours. Buffered work will be processed
	 * asynchronously in the rest of the chain. Buffering also blocks the
	 * transmission of back pressure to previous elements in the chain, although
	 * back pressure is honoured by the buffer.
	 * <p>
	 * Buffers are useful for "bursty" event sources which produce a number of
	 * events close together, then none for some time. These bursts can
	 * sometimes overwhelm downstream event consumers. Buffering will not,
	 * however, protect downstream components from a source which produces
	 * events faster than they can be consumed. For fast sources
	 * {@link #filter(Predicate)} and {@link #coalesce(int, Function)}
	 * {@link #fork(int, int, Executor)} are better choices.
	 * <p>
	 * Buffers are also useful as "circuit breakers" in the pipeline. If a
	 * {@link QueuePolicyOption#FAIL} is used then a full buffer will trigger
	 * the stream to close, preventing an event storm from reaching the client.
	 * 
	 * @param parallelism
	 * @param executor
	 * @param queue
	 * @param queuePolicy
	 * @param pushbackPolicy
	 * @return Builder style (can be a new or the same object)
	 */
	<U extends BlockingQueue<PushEvent< ? extends T>>> PushStreamBuilder<T,U> buildBuffer();

	/**
	 * Merge in the events from another source. The resulting channel is not
	 * closed until this channel and the channel from the source are closed.
	 * 
	 * @param source The source to merge in.
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> merge(PushEventSource< ? extends T> source);

	/**
	 * Merge in the events from another PushStream. The resulting channel is not
	 * closed until this channel and the channel from the source are closed.
	 * 
	 * @param source The source to merge in.
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> merge(PushStream< ? extends T> source);

	/**
	 * Split the events to different streams based on a predicate. If the
	 * predicate is true, the event is dispatched to that channel on the same
	 * position. All predicates are tested for every event.
	 * <p>
	 * This method differs from other methods of AsyncStream in three
	 * significant ways:
	 * <ul>
	 * <li>The return value contains multiple streams.</li>
	 * <li>This stream will only close when all of these child streams have
	 * closed.</li>
	 * <li>Event delivery is made to all open children that accept the event.
	 * </li>
	 * </ul>
	 * 
	 * @param predicates the predicates to test
	 * @return streams that map to the predicates
	 */
	@SuppressWarnings("unchecked")
	PushStream<T>[] split(Predicate< ? super T>... predicates);

	/**
	 * Ensure that any events are delivered sequentially. That is, no
	 * overlapping calls downstream. This can be used to turn a forked stream
	 * (where for example a heavy conversion is done in multiple threads) back
	 * into a sequential stream so a reduce is simple to do.
	 * 
	 * @return Builder style (can be a new or the same object)
	 */
	PushStream<T> sequential();

	/**
	 * Coalesces a number of events into a new type of event. The input events
	 * are forwarded to a accumulator function. This function returns an
	 * Optional. If the optional is present, it's value is send downstream,
	 * otherwise it is ignored.
	 * 
	 * @param f
	 * @return Builder style (can be a new or the same object)
	 */
	<R> PushStream<R> coalesce(Function< ? super T,Optional<R>> f);

	/**
	 * Coalesces a number of events into a new type of event. A fixed number of
	 * input events are forwarded to a accumulator function. This function
	 * returns new event data to be forwarded on.
	 * 
	 * @param count
	 * @param f
	 * @return Builder style (can be a new or the same object)
	 */
	public <R> PushStream<R> coalesce(int count, Function<Collection<T>,R> f);

	/**
	 * Coalesces a number of events into a new type of event. A variable number
	 * of input events are forwarded to a accumulator function. The number of
	 * events to be forwarded is determined by calling the count function. The
	 * accumulator function then returns new event data to be forwarded on.
	 * 
	 * @param count
	 * @param f
	 * @return Builder style (can be a new or the same object)
	 */
	public <R> PushStream<R> coalesce(IntSupplier count,
			Function<Collection<T>,R> f);

	/**
	 * Buffers a number of events over a fixed time interval and then forwards
	 * the events to an accumulator function. This function returns new event
	 * data to be forwarded on. Note that:
	 * <ul>
	 * <li>The collection forwarded to the accumulator function will be empty if
	 * no events arrived during the time interval.</li>
	 * <li>The accumulator function will be run and the forwarded event
	 * delivered as a different task, (and therefore potentially on a different
	 * thread) from the one that delivered the event to this {@link PushStream}.
	 * </li>
	 * <li>Due to the buffering and asynchronous delivery required, this method
	 * prevents the propagation of back-pressure to earlier stages</li>
	 * </ul>
	 * 
	 * @param d
	 * @param f
	 * @return Builder style (can be a new or the same object)
	 */
	<R> PushStream<R> window(Duration d, Function<Collection<T>,R> f);

	/**
	 * Buffers a number of events over a fixed time interval and then forwards
	 * the events to an accumulator function. This function returns new event
	 * data to be forwarded on. Note that:
	 * <ul>
	 * <li>The collection forwarded to the accumulator function will be empty if
	 * no events arrived during the time interval.</li>
	 * <li>The accumulator function will be run and the forwarded event
	 * delivered by a task given to the supplied executor.</li>
	 * <li>Due to the buffering and asynchronous delivery required, this method
	 * prevents the propagation of back-pressure to earlier stages</li>
	 * </ul>
	 * 
	 * @param d
	 * @param executor
	 * @param f
	 * @return Builder style (can be a new or the same object)
	 */
	<R> PushStream<R> window(Duration d, Executor executor,
			Function<Collection<T>,R> f);

	/**
	 * Buffers a number of events over a variable time interval and then
	 * forwards the events to an accumulator function. The length of time over
	 * which events are buffered is determined by the time function. A maximum
	 * number of events can also be requested, if this number of events is
	 * reached then the accumulator will be called early. The accumulator
	 * function returns new event data to be forwarded on. It is also given the
	 * length of time for which the buffer accumulated data. This may be less
	 * than the requested interval if the buffer reached the maximum number of
	 * requested events early. Note that:
	 * <ul>
	 * <li>The collection forwarded to the accumulator function will be empty if
	 * no events arrived during the time interval.</li>
	 * <li>The accumulator function will be run and the forwarded event
	 * delivered as a different task, (and therefore potentially on a different
	 * thread) from the one that delivered the event to this {@link PushStream}.
	 * </li>
	 * <li>Due to the buffering and asynchronous delivery required, this method
	 * prevents the propagation of back-pressure to earlier stages</li>
	 * <li>If the window finishes by hitting the maximum number of events then
	 * the remaining time in the window will be applied as back-pressure to the
	 * previous stage, attempting to slow the producer to the expected windowing
	 * threshold.</li>
	 * </ul>
	 * 
	 * @param timeSupplier
	 * @param maxEvents
	 * @param f
	 * @return Builder style (can be a new or the same object)
	 */
	<R> PushStream<R> window(Supplier<Duration> timeSupplier,
			IntSupplier maxEvents, BiFunction<Long,Collection<T>,R> f);

	/**
	 * Buffers a number of events over a variable time interval and then
	 * forwards the events to an accumulator function. The length of time over
	 * which events are buffered is determined by the time function. A maximum
	 * number of events can also be requested, if this number of events is
	 * reached then the accumulator will be called early. The accumulator
	 * function returns new event data to be forwarded on. It is also given the
	 * length of time for which the buffer accumulated data. This may be less
	 * than the requested interval if the buffer reached the maximum number of
	 * requested events early. Note that:
	 * <ul>
	 * <li>The collection forwarded to the accumulator function will be empty if
	 * no events arrived during the time interval.</li>
	 * <li>The accumulator function will be run and the forwarded event
	 * delivered as a different task, (and therefore potentially on a different
	 * thread) from the one that delivered the event to this {@link PushStream}.
	 * </li>
	 * <li>If the window finishes by hitting the maximum number of events then
	 * the remaining time in the window will be applied as back-pressure to the
	 * previous stage, attempting to slow the producer to the expected windowing
	 * threshold.</li>
	 * </ul>
	 * 
	 * @param timeSupplier
	 * @param maxEvents
	 * @param executor
	 * @param f
	 * @return Builder style (can be a new or the same object)
	 */
	<R> PushStream<R> window(Supplier<Duration> timeSupplier,
			IntSupplier maxEvents, Executor executor,
			BiFunction<Long,Collection<T>,R> f);

	/**
	 * Execute the action for each event received until the channel is closed.
	 * This is a terminating method, the returned promise is resolved when the
	 * channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param action The action to perform
	 * @return A promise that is resolved when the channel closes.
	 */
	Promise<Void> forEach(Consumer< ? super T> action);

	/**
	 * Collect the payloads in an Object array after the channel is closed. This
	 * is a terminating method, the returned promise is resolved when the
	 * channel is closed.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @return A promise that is resolved with all the payloads received over
	 *         the channel
	 */
	Promise<Object[]> toArray();

	/**
	 * Collect the payloads in an Object array after the channel is closed. This
	 * is a terminating method, the returned promise is resolved when the
	 * channel is closed. The type of the array is handled by the caller using a
	 * generator function that gets the length of the desired array.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param generator
	 * @return A promise that is resolved with all the payloads received over
	 *         the channel
	 */
	<A extends T> Promise<A[]> toArray(IntFunction<A[]> generator);

	/**
	 * Standard reduce, see Stream. The returned promise will be resolved when
	 * the channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param identity The identity/begin value
	 * @param accumulator The accumulator
	 * @return A
	 */
	Promise<T> reduce(T identity, BinaryOperator<T> accumulator);

	/**
	 * Standard reduce without identity, so the return is an Optional. The
	 * returned promise will be resolved when the channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param accumulator The accumulator
	 * @return an Optional
	 */
	Promise<Optional<T>> reduce(BinaryOperator<T> accumulator);

	/**
	 * Standard reduce with identity, accumulator and combiner. The returned
	 * promise will be resolved when the channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param identity
	 * @param accumulator
	 * @param combiner combines to U's into one U (e.g. how combine two lists)
	 * @return The promise
	 */
	<U> Promise<U> reduce(U identity, BiFunction<U, ? super T,U> accumulator,
			BinaryOperator<U> combiner);

	/**
	 * See Stream. Will resolve onces the channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param collector
	 * @return A Promise representing the collected results
	 */
	<R, A> Promise<R> collect(Collector< ? super T,A,R> collector);

	/**
	 * See Stream. Will resolve onces the channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param comparator
	 * @return A Promise representing the minimum value, or null if no values
	 *         are seen before the end of the stream
	 */
	Promise<Optional<T>> min(Comparator< ? super T> comparator);

	/**
	 * See Stream. Will resolve onces the channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param comparator
	 * @return A Promise representing the maximum value, or null if no values
	 *         are seen before the end of the stream
	 */
	Promise<Optional<T>> max(Comparator< ? super T> comparator);

	/**
	 * See Stream. Will resolve onces the channel closes.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @return A Promise representing the number of values in the stream
	 */
	Promise<Long> count();

	/**
	 * Close the channel and resolve the promise with true when the predicate
	 * matches a payload. If the channel is closed before the predicate matches,
	 * the promise is resolved with false.
	 * <p>
	 * This is a <strong>short circuiting terminal operation</strong>
	 * 
	 * @param predicate
	 * @return A Promise that will resolve when an event matches the predicate,
	 *         or the end of the stream is reached
	 */
	Promise<Boolean> anyMatch(Predicate< ? super T> predicate);

	/**
	 * Closes the channel and resolve the promise with false when the predicate
	 * does not matches a pay load.If the channel is closed before, the promise
	 * is resolved with true.
	 * <p>
	 * This is a <strong>short circuiting terminal operation</strong>
	 * 
	 * @param predicate
	 * @return A Promise that will resolve when an event fails to match the
	 *         predicate, or the end of the stream is reached
	 */
	Promise<Boolean> allMatch(Predicate< ? super T> predicate);

	/**
	 * Closes the channel and resolve the promise with false when the predicate
	 * matches any pay load. If the channel is closed before, the promise is
	 * resolved with true.
	 * <p>
	 * This is a <strong>short circuiting terminal operation</strong>
	 * 
	 * @param predicate
	 * @return A Promise that will resolve when an event matches the predicate,
	 *         or the end of the stream is reached
	 */
	Promise<Boolean> noneMatch(Predicate< ? super T> predicate);

	/**
	 * Close the channel and resolve the promise with the first element. If the
	 * channel is closed before, the Optional will have no value.
	 * 
	 * @return a promise
	 */
	Promise<Optional<T>> findFirst();

	/**
	 * Close the channel and resolve the promise with the first element. If the
	 * channel is closed before, the Optional will have no value.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @return a promise
	 */
	Promise<Optional<T>> findAny();

	/**
	 * Pass on each event to another consumer until the stream is closed.
	 * <p>
	 * This is a <strong>terminal operation</strong>
	 * 
	 * @param action
	 * @return a promise
	 */
	Promise<Long> forEachEvent(PushEventConsumer< ? super T> action);

}
