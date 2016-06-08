package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * A Builder for a PushStream. This Builder extends the support of a standard
 * BufferBuilder by allowing the PushStream to be unbuffered.
 * 
 *
 * @param <T> The type of objects in the {@link PushEvent}
 * @param <U> The type of the Queue used in the user specified buffer
 */
public interface PushStreamBuilder<T, U extends BlockingQueue<PushEvent< ? extends T>>>
		extends BufferBuilder<PushStream<T>,T,U> {

	/**
	 * Tells this {@link PushStreamBuilder} to create an unbuffered stream which
	 * delivers events directly to its consumer using the incoming delivery
	 * thread.
	 * 
	 * @return the builder
	 */
	PushStreamBuilder<T,U> unbuffered();

	/*
	 * Overridden methods to allow the covariant return of a PushStreamBuilder
	 */

	@Override
	PushStreamBuilder<T,U> withBuffer(U queue);

	@Override
	PushStreamBuilder<T,U> withQueuePolicy(QueuePolicy<T,U> queuePolicy);

	@Override
	PushStreamBuilder<T,U> withQueuePolicy(QueuePolicyOption queuePolicyOption);

	@Override
	PushStreamBuilder<T,U> withPushbackPolicy(
			PushbackPolicy<T,U> pushbackPolicy);

	@Override
	PushStreamBuilder<T,U> withPushbackPolicy(
			PushbackPolicyOption pushbackPolicyOption, long time);

	@Override
	PushStreamBuilder<T,U> withParallelism(int parallelism);

	@Override
	PushStreamBuilder<T,U> withExecutor(Executor executor);
}
