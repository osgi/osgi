package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface PushStreamProvider {

	/**
	 * Create a stream with the default configured buffer, executor size, 
	 * queue, queue policy and pushback policy.
	 * 
	 * <p>
	 * This stream will be buffered from the event producer, and will honour
	 * back pressure even if the source does not.
	 * 
	 * <p>
	 * Buffered streams are useful for "bursty" event sources which produce a 
	 * number of events close together, then none for some time. These
	 * bursts can sometimes overwhelm downstream processors. Buffering
	 * will not, however, protect downstream components from a source
	 * which produces events faster than they can be consumed.
	 * 
	 * <p>
	 * Event delivery will not begin until a terminal operation is reached on the
	 * chain of AsyncStreams. Once a terminal operation is reached the stream
	 * will be connected to the event source.
	 * 
	 * @param eventSource
	 * @return
	 */
	public <T> PushStream<T> createStream(PushEventSource<T> eventSource);
	
	/**
	 * Create a buffered stream with custom configuration.
	 * 
	 * <p>
	 * Buffered streams are useful for "bursty" event sources which produce a 
	 * number of events close together, then none for some time. These
	 * bursts can sometimes overwhelm downstream processors. Buffering
	 * will not, however, protect downstream components from a source
	 * which produces events faster than they can be consumed.
	 * 
	 * <p>
	 * Buffers are also useful as "circuit breakers" in the pipeline. If a
	 * {@link PushbackPolicyOption#ON_FULL_CLOSE} or {@link QueuePolicyOption#FAIL}
	 * is used then a full buffer will trigger the stream to close, preventing an
	 * event storm from reaching the client.
	 * 
	 * <p>
	 * This stream will be buffered from the event producer, and will honour
	 * back pressure even if the source does not.
	 * 
	 * @param eventSource
	 * @param parallelism
	 * @param executor
	 * @param queue
	 * @param overflowPolicy
	 * @param pushbackPolicy
	 * @return
	 */
	public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushStream<T> createStream(
			PushEventSource<T> eventSource, int parallelism, Executor executor, U queue, 
			QueuePolicy<T, U> queuePolicy, PushbackPolicy<T,U> pushbackPolicy);
	
	/**
	 * Create an unbuffered stream. This stream will use the producer's thread(s)
	 * to process the events and will directly provide back pressure to the source.
	 * 
	 * <p>
	 * <strong>N.B.</strong> If the {@link PushEventSource} does not respond to the
	 * backpressure responses then the stream may become overloaded. Consider using
	 * a buffered stream for anything other than trivial event processing.
	 * 
	 * <p>
	 * Event delivery will not begin until a terminal operation is reached on the
	 * chain of AsyncStreams. Once a terminal operation is reached the stream
	 * will be connected to the event source.
	 * 
	 * @param eventSource
	 * @return
	 */
	public <T> PushStream<T> createUnbufferedStream(PushEventSource<T> eventSource);
	
	/**
	 * Convert an {@link PushStream} into an {@link PushEventSource}. The first call 
	 * to {@link PushEventSource#open(PushEventConsumer)} will begin event processing.
	 * 
	 * The {@link PushEventSource} will remain active until the backing stream is closed,
	 * and permits multiple consumers to {@link PushEventSource#open(PushEventConsumer)} it.
	 * 
	 * @param stream
	 * @return
	 */
	public <T> PushEventSource<T> asEventSource(PushStream<T> stream);

	/**
	 * Convert an {@link PushStream} into an {@link PushEventSource}. The first call 
	 * to {@link PushEventSource#open(PushEventConsumer)} will begin event processing.
	 * 
	 * The {@link PushEventSource} will remain active until the backing stream is closed,
	 * and permits multiple consumers to {@link PushEventSource#open(PushEventConsumer)} it.
	 * 
	 * @param stream
	 * @param parallelism
	 * @param executor
	 * @param queueFactory
	 * @param queuePolicy
	 * @param pushbackPolicy
	 * @return
	 */
	public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushEventSource<T> asEventSource(
			PushStream<T> stream, int parallelism, Executor executor, Supplier<U> queueFactory, 
			QueuePolicy<T, U> queuePolicy, PushbackPolicy<T,U> pushbackPolicy);
	

	/**
	 * Create a {@link SimplePushEventSource} with the supplied type and default buffering
	 * behaviours. The SimpleAsyncEventSource will respond to back pressure requests from 
	 * the consumers connected to it.
	 * @param type
	 * @return
	 */
	public <T> SimplePushEventSource<T> createSimpleEventSource(Class<T> type);
	
	/**
	 * 
	 * Create a {@link SimplePushEventSource} with the supplied type and custom buffering
	 * behaviours. The SimpleAsyncEventSource will respond to back pressure requests from 
	 * the consumers connected to it.
	 * 
	 * @param type
	 * @param parallelism
	 * @param executor
	 * @param queueFactory A factory used to create a queue for each connected consumer
	 * @param queuePolicy
	 * @param pushbackPolicy
	 * @return
	 */

	public <T, U extends BlockingQueue<PushEvent<? extends T>>> SimplePushEventSource<T> 
			createSimpleEventSource(Class<T> type, int parallelism, Executor executor, 
					Supplier<U> queueFactory, QueuePolicy<T, U> queuePolicy, 
					PushbackPolicy<T,U> pushbackPolicy);
	
	/**
	 * Create a buffered {@link PushEventConsumer} with the default configured 
	 * buffer, executor size, queue, queue policy and pushback policy.
	 * 
	 * <p>
	 * The returned consumer will be buffered from the event source, and will 
	 * honour back pressure requests from its delegate even if the event source does not.
	 * 
	 * <p>
	 * Buffered consumers are useful for "bursty" event sources which produce a 
	 * number of events close together, then none for some time. These
	 * bursts can sometimes overwhelm the consumer. Buffering
	 * will not, however, protect downstream components from a source
	 * which produces events faster than they can be consumed.
	 * 
	 * @param delegate
	 * @return
	 */
	public <T> PushEventConsumer<T> buffer(PushEventConsumer<T> delegate);
	
	/**
	 * Create a buffered {@link PushEventConsumer} with custom configuration.
	 * 
	 * <p>
	 * The returned consumer will be buffered from the event source, and will 
	 * honour back pressure requests from its delegate even if the event source does not.
	 * 
	 * <p>
	 * Buffered consumers are useful for "bursty" event sources which produce a 
	 * number of events close together, then none for some time. These
	 * bursts can sometimes overwhelm the consumer. Buffering
	 * will not, however, protect downstream components from a source
	 * which produces events faster than they can be consumed.
	 * 
	 * <p>
	 * Buffers are also useful as "circuit breakers". If a
	 * {@link PushbackPolicyOption#ON_FULL_CLOSE} or {@link QueuePolicyOption#FAIL}
	 * is used then a full buffer will request that the stream close, preventing an
	 * event storm from reaching the client.
	 * 
	 * @param delegate
	 * @param parallelism
	 * @param executor
	 * @param queue
	 * @param overflowPolicy
	 * @param pushbackPolicy
	 * @return
	 */
	public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushEventConsumer<T> buffer(
			PushEventConsumer<T> delegate, int parallelism, Executor executor, U queue, 
			QueuePolicy<T, U> queuePolicy, PushbackPolicy<T,U> pushbackPolicy);
}
