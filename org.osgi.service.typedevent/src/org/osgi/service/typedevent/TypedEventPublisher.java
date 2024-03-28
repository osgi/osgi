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
 * A Typed Event publisher for a single topic. Bundles wishing to publish events
 * may obtain this object from the {@link TypedEventBus}.
 * <p>
 * All events published by this publisher are sent to the same topic, as
 * returned by {@link #getTopic()}.
 * <p>
 * This object should not be shared as it will not re-validate the caller's
 * permission to send to the target topic.
 * 
 * @ThreadSafe
 * @author $Id$
 * @param <T> The type of events that may be sent using this publisher
 * @since 1.1
 */

@ProviderType
public interface TypedEventPublisher<T> extends AutoCloseable {
	/**
	 * Initiate asynchronous, ordered delivery of an event. This method returns
	 * to the caller before delivery of the event is completed. Events are
	 * delivered in the order that they are received by this method.
	 * 
	 * @param event The event to send to all listeners which subscribe to the
	 *            topic of the event.
	 * @throws NullPointerException if the event object is null
	 * @throws IllegalStateException if the {@link TypedEventPublisher} has been
	 *             closed
	 */
	void deliver(T event);

	/**
	 * Initiate asynchronous, ordered delivery of event data. This method
	 * returns to the caller before delivery of the event is completed. Events
	 * are delivered in the order that they are received by this method.
	 * 
	 * @param event A Map representation of the event data to send to all
	 *            listeners which subscribe to the topic.
	 * @throws NullPointerException if the event map is null
	 * @throws IllegalStateException if the {@link TypedEventPublisher} has been
	 *             closed
	 */
	void deliverUntyped(Map<String, ? > event);

	/**
	 * Get the topic for this {@link TypedEventPublisher}. This topic is set
	 * when the {@link TypedEventPublisher} is created and cannot be changed.
	 * 
	 * @return The topic for this {@link TypedEventPublisher}
	 */
	String getTopic();

	/**
	 * Closes this {@link TypedEventPublisher} so that no further events can be
	 * sent. After closure all delivery methods throw
	 * {@link IllegalStateException}.
	 * <p>
	 * Calling {@link #close()} on a closed {@link TypedEventPublisher} has no
	 * effect.
	 */
	@Override
	void close();

	/**
	 * This method allows the caller to check whether the
	 * {@link TypedEventPublisher} has been closed.
	 * 
	 * @return <code>false</code> if the {@link TypedEventPublisher} has been
	 *         closed, <code>true</code> otherwise.
	 */
	boolean isOpen();
}
