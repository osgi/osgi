/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.typedevent;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The Type Safe Event service. Bundles wishing to publish events must obtain
 * this service and call one of the event delivery methods.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ProviderType
public interface TypedEventBus {
	/**
	 * Initiate asynchronous, ordered delivery of an event. This method returns
	 * to the caller before delivery of the event is completed. Events are
	 * delivered in the order that they are received by this method.
	 * <p>
	 * The topic for this event will be automatically set to the fully qualified
	 * type name for the supplied event object.
	 * <p>
	 * Logically equivalent to calling
	 * {@code deliver(event.getClass().getName(), event)}
	 * 
	 * @param event The event to send to all listeners which subscribe to the
	 *            topic of the event.
	 * @throws NullPointerException if the event object is null
	 */
	void deliver(Object event);

	/**
	 * Initiate asynchronous, ordered delivery of an event. This method returns
	 * to the caller before delivery of the event is completed. Events are
	 * delivered in the order that they are received by this method.
	 * 
	 * @param topic The topic to which this event should be sent.
	 * @param event The event to send to all listeners which subscribe to the
	 *            topic.
	 * @throws NullPointerException if the event object is null
	 * @throws IllegalArgumentException if the topic name is not valid
	 */
	void deliver(String topic, Object event);

	/**
	 * Initiate asynchronous, ordered delivery of event data. This method
	 * returns to the caller before delivery of the event is completed. Events
	 * are delivered in the order that they are received by this method.
	 * 
	 * @param topic The topic to which this event should be sent.
	 * @param event A Map representation of the event data to send to all
	 *            listeners which subscribe to the topic.
	 * @throws NullPointerException if the event map is null
	 * @throws IllegalArgumentException if the topic name is not valid
	 */
	void deliverUntyped(String topic, Map<String, ? > event);

}
