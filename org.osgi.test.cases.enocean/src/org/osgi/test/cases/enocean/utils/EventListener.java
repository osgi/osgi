/*
 * Copyright (c) OSGi Alliance (2014, 2020). All Rights Reserved.
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

package org.osgi.test.cases.enocean.utils;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.support.OSGiTestCaseProperties;

/**
 * @author $Id$
 */
public class EventListener implements EventHandler {

	@SuppressWarnings("unused")
	static private final String TAG = "EventListener";

	private Semaphore			waiter;
	private LinkedList<Event>					lastEvents;
	private BundleContext		bc;
	private ServiceRegistration<EventHandler>	sReg;

	/**
	 * @param bc
	 * @param topics
	 * @param filter
	 */
	public EventListener(BundleContext bc, String[] topics, String filter) {
		this.bc = bc;
		lastEvents = new LinkedList<>();
		Dictionary<String,Object> ht = new Hashtable<>();
		ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, topics);
		if (filter != null) {
			ht.put(org.osgi.service.event.EventConstants.EVENT_FILTER, filter);
		}
		sReg = this.bc.registerService(EventHandler.class, this, ht);
		waiter = new Semaphore(0);
	}

	public void handleEvent(Event event) {
		lastEvents.addFirst(event);
		waiter.release();
	}

	/**
	 * Waits for an event to occur.
	 * 
	 * @param timeout
	 * @return the event type (added, modified, removed) or null.
	 * @throws InterruptedException
	 */
	public Event waitForEvent(long timeout) throws InterruptedException {
		if (!lastEvents.isEmpty()) {
			return lastEvents.removeLast();
		}
		if (waiter.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
			return lastEvents.isEmpty() ? null : (Event) lastEvents
					.removeLast();
		}
		return null;
	}

	/**
	 * @return event.
	 * @throws InterruptedException
	 */
	public Event waitForEvent() throws InterruptedException {
		return waitForEvent(OSGiTestCaseProperties.getTimeout());
	}

	/**
	 * 
	 */
	public void close() {
		lastEvents.clear();
		sReg.unregister();
		waiter.release();
	}

}
