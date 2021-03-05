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

package org.osgi.test.cases.event.secure.service;

import java.util.List;

import org.osgi.service.event.Event;

/**
 * Service to receive events.
 * 
 * @author $Id$
 */
public interface ReceiverService {

	/**
	 * Sets the array with event topics in which the event handler is
	 * interested.
	 * 
	 * @param topics the array with event topics.
	 */
	public void setTopics(String... topics);

	/**
	 * Returns the array with all set event topics in which the event handler is
	 * interested.
	 * 
	 * @return the array with all set event topics
	 */
	public String[] getTopics();

	/**
	 * Returns the last received event and then the last event is set to null.
	 *
	 * @return last received event
	 */
	public Event getLastReceivedEvent();

	/**
	 * Returns the last received events and then elements in the vector with
	 * last events are removed. @see
	 * org.osgi.test.cases.event.secure.service.ReceiverService#
	 * getLastReceivedEvents()
	 * 
	 * @return list of received events.
	 */
	public List<Event> getLastReceivedEvents();
}
