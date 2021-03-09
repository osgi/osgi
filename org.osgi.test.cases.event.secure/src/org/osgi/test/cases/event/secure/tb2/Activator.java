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

package org.osgi.test.cases.event.secure.tb2;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.event.secure.service.ReceiverService;

/**
 * A bundle that registers a ReceiverService service.
 *
 * @author $Id$
 */
public class Activator implements BundleActivator, ReceiverService, EventHandler {

	BundleContext									context;
	private ServiceRegistration<ReceiverService>	receiverServiceReg;
	private ServiceRegistration<EventHandler>		handlerServiceReg;

	private final LinkedList<Event>	lastEvents	= new LinkedList<Event>();
	private String[]				topics;

	public void start(BundleContext c) throws Exception {
		this.context = c;
		receiverServiceReg = context.registerService(ReceiverService.class, this, null);
	}

	public void stop(BundleContext c) throws Exception {
		receiverServiceReg.unregister();
		if (handlerServiceReg != null) {
			handlerServiceReg.unregister();
		}
	}

	/**
	 * Sets the array with event topics in which the event handler is
	 * interested.
	 * 
	 * @see ReceiverService#setTopics(java. lang.String[])
	 */
	public void setTopics(String... topics) {
		this.topics = topics;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, (topics.length == 1) ? topics[0] : topics);
		if (handlerServiceReg == null) {
			handlerServiceReg = AccessController
					.doPrivileged(new PrivilegedAction<ServiceRegistration<EventHandler>>() {
						public ServiceRegistration<EventHandler> run() {
							return context.registerService(EventHandler.class, Activator.this, properties);
						}
					});
		}
		else {
			handlerServiceReg.setProperties(properties);
		}
	}

	/**
	 * Returns the array with all set event topics in which the event handler is
	 * interested.
	 * 
	 * @see ReceiverService#getTopics()
	 */
	public String[] getTopics() {
		return topics;
	}

	/**
	 * Called by the {@code EventAdmin} service to notify the listener of an
	 * event.
	 * 
	 * @param event The event that occurred.
	 */
	public void handleEvent(Event event) {
		synchronized (lastEvents) {
			lastEvents.addLast(event);
		}
	}

	/**
	 * Returns the last received event and then elements in the list with last
	 * events are removed.
	 * 
	 * @see ReceiverService#getLastReceivedEvent()
	 */
	public Event getLastReceivedEvent() {
		Event event = null;
		synchronized (lastEvents) {
			if (!lastEvents.isEmpty()) {
				event = lastEvents.removeLast();
				lastEvents.clear();
			}
		}
		return event;
	}

	/**
	 * Returns the last received events and then elements in the list with last
	 * events are removed.
	 * 
	 * @see ReceiverService#getLastReceivedEvents()
	 */
	public List<Event> getLastReceivedEvents() {
		List<Event> events = new ArrayList<Event>();
		synchronized (lastEvents) {
			if (!lastEvents.isEmpty()) {
				events.addAll(lastEvents);
				lastEvents.clear();
			}
		}
		return events;
	}
}
