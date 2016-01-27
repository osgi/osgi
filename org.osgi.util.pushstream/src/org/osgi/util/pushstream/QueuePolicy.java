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

import java.util.concurrent.BlockingQueue;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.util.pushstream.PushEvent.EventType;

/**
 * A {@link QueuePolicy} is used to control how events should be queued in the
 * current buffer. The {@link QueuePolicy} will be called when an event has
 * arrived.
 * 
 * @see QueuePolicyOption
 * 
 *
 * @param <T> The type of the data
 * @param <U> The type of the queue
 */

@ConsumerType
@FunctionalInterface
public interface QueuePolicy<T, U extends BlockingQueue<PushEvent<? extends T>>> { 
	
	/**
	 * Enqueue the event and return the remaining capacity available for events
	 * 
	 * @param queue
	 * @param event
	 * @throws Exception If an error ocurred adding the event to the queue. This
	 *         exception will cause the connection between the
	 *         {@link PushEventSource} and the {@link PushEventConsumer} to be
	 *         closed with an {@link EventType#ERROR}
	 */
	public void doOffer(U queue, PushEvent<? extends T> event) throws Exception;
	
}
