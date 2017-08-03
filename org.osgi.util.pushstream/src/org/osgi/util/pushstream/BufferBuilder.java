package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Create a buffered section of a Push-based stream
 *
 * @param <R> The type of object being built
 * @param <T> The type of objects in the {@link PushEvent}
 * @param <U> The type of the Queue used in the user specified buffer
 */
@ProviderType
public interface BufferBuilder<R, T, U extends BlockingQueue<PushEvent<? extends T>>> {

	/**
	 * The BlockingQueue implementation to use as a buffer
	 * 
	 * @param queue
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withBuffer(U queue);

	/**
	 * Set the {@link QueuePolicy} of this Builder
	 * 
	 * @param queuePolicy
	 * @return this builder
	 */
	BufferBuilder<R,T,U> withQueuePolicy(QueuePolicy<T,U> queuePolicy);

	/**
	 * Set the {@link QueuePolicy} of this Builder
	 * 
	 * @param queuePolicyOption
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withQueuePolicy(QueuePolicyOption queuePolicyOption);

	/**
	 * Set the {@link PushbackPolicy} of this builder
	 * 
	 * @param pushbackPolicy
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withPushbackPolicy(PushbackPolicy<T, U> pushbackPolicy);

	/**
	 * Set the {@link PushbackPolicy} of this builder
	 * 
	 * @param pushbackPolicyOption
	 * @param time
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withPushbackPolicy(PushbackPolicyOption pushbackPolicyOption, long time);

	/**
	 * Set the maximum permitted number of concurrent event deliveries allowed
	 * from this buffer
	 * 
	 * @param parallelism
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withParallelism(int parallelism);

	/**
	 * Set the {@link Executor} that should be used to deliver events from this
	 * buffer
	 * 
	 * @param executor
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withExecutor(Executor executor);
	
	/**
	 * @return the object being built
	 */
	R build();

}
