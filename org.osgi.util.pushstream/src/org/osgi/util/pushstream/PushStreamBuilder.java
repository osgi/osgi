package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;

/**
 * A Builder for a PushStream. This Builder extends the support of a standard
 * BufferBuilder by allowing the PushStream to be unbuffered.
 * 
 *
 * @param <T> The type of objects in the {@link PushEvent}
 * @param <U> The type of the Queue used in the user specified buffer
 */
public interface PushStreamBuilder<T, U extends BlockingQueue<PushEvent<? extends T>>> extends BufferBuilder<PushStream<T>, T, U> {

	/**
	 * Tells this {@link PushStreamBuilder} to create an unbuffered stream which
	 * delivers events directly to its consumer using the incoming delivery
	 * thread.
	 * 
	 * <p>
	 * No further configuration is possible after this point, so a
	 * {@link Createable} is returned
	 * 
	 * @return the builder
	 */
	Createable<PushStream<T>> unbuffered();
}
