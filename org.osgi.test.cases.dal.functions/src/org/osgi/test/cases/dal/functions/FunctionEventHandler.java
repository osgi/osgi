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

package org.osgi.test.cases.dal.functions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Test event handler for {@link FunctionEvent#TOPIC_PROPERTY_CHANGED} events.
 */
public final class FunctionEventHandler implements EventHandler {

	private static final long	WAIT_EVENT_TIMEOUT	= Long.getLong(
															"org.osgi.test.cases.dal.timeout", 10000).longValue();

	private final BundleContext	bc;
	private final List<Event>					events;

	private ServiceRegistration<EventHandler>	handlerSReg;

	/**
	 * Constructs a new test event handler with the specified bundle context.
	 * 
	 * @param bc The bundle context used for the handler registration.
	 */
	public FunctionEventHandler(BundleContext bc) {
		this.bc = bc;
		this.events = new ArrayList<>();
	}

	/**
	 * Registers the test handler for the specified function, if specified.
	 * 
	 * @param funtionUID The function identifier, can be {@code null}.
	 * @param propertyName The function property name, can be {@code null}.
	 */
	public void register(String funtionUID, String propertyName) {
		if (null != this.handlerSReg) {
			return;
		}
		Dictionary<String,Object> handlerRegProps = new Hashtable<>(2, 1F);
		handlerRegProps.put(EventConstants.EVENT_TOPIC, FunctionEvent.TOPIC_PROPERTY_CHANGED);
		String eventFilter = null;
		if (null != funtionUID) {
			eventFilter =
					'(' + FunctionEvent.FUNCTION_UID + '=' + funtionUID + ')';
		}
		if (null != propertyName) {
			String propertyNameFilter =
					'(' + FunctionEvent.PROPERTY_NAME + '=' + propertyName + ')';
			eventFilter = (null == eventFilter) ? propertyNameFilter :
					"(&" + eventFilter + propertyNameFilter + ')';
		}
		if (null != eventFilter) {
			handlerRegProps.put(EventConstants.EVENT_FILTER, eventFilter);
		}
		this.handlerSReg = this.bc.registerService(EventHandler.class, this,
				handlerRegProps);
	}

	/**
	 * Unregisters the test event handler.
	 */
	public void unregister() {
		if (null == this.handlerSReg) {
			return;
		}
		this.handlerSReg.unregister();
		this.handlerSReg = null;
	}

	/**
	 * Returns the required events. If the events are missing, the method will
	 * block for a given timeout.
	 * 
	 * @param eventsCount The desired events count.
	 * 
	 * @return The events required by the specified argument, the events count
	 *         can be greater than the argument.
	 */
	public FunctionEvent[] getEvents(int eventsCount) {
		synchronized (this.events) {
			long startTime = System.currentTimeMillis();
			long elapsedTime = 0;
			try {
				while ((this.events.size() < eventsCount) &&
						(elapsedTime < WAIT_EVENT_TIMEOUT)) {
					this.events.wait(WAIT_EVENT_TIMEOUT - elapsedTime);
					elapsedTime = System.currentTimeMillis() - startTime;
				}
			} catch (InterruptedException ie) {
				// go ahead
			}
			if (elapsedTime >= WAIT_EVENT_TIMEOUT) {
				throw new IllegalStateException(
						"The desired events are not received for: " + WAIT_EVENT_TIMEOUT + "ms.");
			}
			FunctionEvent[] result = new FunctionEvent[eventsCount];
			for (int i = 0; i < result.length; i++) {
				result[i] = (FunctionEvent) this.events.get(i);
			}
			return result;
		}
	}

	public void handleEvent(Event event) {
		synchronized (this.events) {
			this.events.add(event);
			this.events.notifyAll();
		}
	}
}
