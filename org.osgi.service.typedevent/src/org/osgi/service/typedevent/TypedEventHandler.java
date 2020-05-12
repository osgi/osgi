/*
 * Copyright (c) OSGi Alliance (2020). All Rights Reserved.
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

package org.osgi.service.typedevent;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Listener for Typed Events.
 * <p>
 * {@code TypedEventHandler} objects are registered with the Framework service
 * registry and are notified with an event object when an event is sent.
 * <p>
 * {@code TypedEventHandler} objects are expected to reify the type parameter
 * {@code T} with the type of object they wish to receive when implementing this
 * interface. This type can be overridden using the
 * {@link TypedEventConstants#TYPED_EVENT_TOPICS} service property.
 * <p>
 * {@code TypedEventHandler} objects may be registered with a service property
 * {@link TypedEventConstants#TYPED_EVENT_TOPICS} whose value is the list of
 * topics in which the event handler is interested.
 * <p>
 * For example:
 * 
 * <pre>
 * String[] topics = new String[] {
 * 		&quot;com/isv/*&quot;
 * };
 * Hashtable ht = new Hashtable();
 * ht.put(EventConstants.TYPE_SAFE_EVENT_TOPICS, topics);
 * context.registerService(TypedEventHandler.class, this, ht);
 * </pre>
 * 
 * @ThreadSafe
 * @author $Id$
 * @param <T> The type of the event to be received
 */
@ConsumerType
public interface TypedEventHandler<T> {
	/**
	 * Called by the {@link TypedEventBus} service to notify the listener of an
	 * event.
	 * 
	 * @param topic The topic to which the event was sent
	 * @param event The event that occurred.
	 */
	void notify(String topic, T event);
}
