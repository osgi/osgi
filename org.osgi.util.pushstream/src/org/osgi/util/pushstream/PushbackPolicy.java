package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
@FunctionalInterface
public interface PushbackPolicy<T, U extends BlockingQueue<PushEvent<? extends T>>> {
	
	/**
	 * Given the current state of the queue, determine the level of
	 * back pressure that should be applied
	 * 
	 * @param queue
	 * @return
	 */
	public long pushback(U queue) throws Exception;
	
}