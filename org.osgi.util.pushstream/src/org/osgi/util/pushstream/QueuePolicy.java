package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
@FunctionalInterface
public interface QueuePolicy<T, U extends BlockingQueue<PushEvent<? extends T>>> { 
	
	/**
	 * Enqueue the event and return the remaining 
	 * capacity available for events
	 * @param queue
	 * @param event
	 * @return
	 */
	public void doOffer(U queue, PushEvent<? extends T> event) throws Exception;
	
}