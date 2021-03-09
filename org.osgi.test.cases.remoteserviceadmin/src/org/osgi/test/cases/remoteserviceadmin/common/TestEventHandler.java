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
package org.osgi.test.cases.remoteserviceadmin.common;

import static junit.framework.TestCase.assertEquals;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;

/**
 * Simple EventHandler implementation that records all events received and
 * allows to request the events
 * */
public class TestEventHandler implements EventHandler {
	private final LinkedList<Event> eventlist = new LinkedList<Event>();
	private final Semaphore sem = new Semaphore(0);

	private final long m_timeout;

	public TestEventHandler(long timeout) {
		m_timeout = timeout;
	}

	/**
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		// System.out
		// .println("########################### TestEventHandler::handleEvent "
		// + event.getTopic()
		// + "  "
		// + " ( "
		// + Arrays.asList(event.getPropertyNames()) + "  )");

		synchronized (eventlist) {
			eventlist.add(event);
			sem.release();
		}

	}

	public Event getNextEventForTopic(String... topics) {
		if (topics.length == 0) {
			throw new IllegalArgumentException(
					"at least one topic must be provided");
		}
		try {
			int waited = 0;
			while (true) {
				synchronized (eventlist) {
					for (Event e : eventlist) {
						for (String topic : topics) {
							if (topic.equals(e.getTopic())) {
								eventlist.remove(e);
								return e;
							}
						}
					}
				}
				sem.tryAcquire(m_timeout / 10, TimeUnit.MILLISECONDS);
				++waited;
				if (waited >= 10)
					return null;
			}
		} catch (InterruptedException e1) {
			return null;
		}

	}

	public static RemoteServiceAdminEvent verifyBasicRsaEventProperties(
			ServiceReference<RemoteServiceAdmin> rsaRef, Event event) {
		assertEquals("122.7: bundle has to refer to RSA bundle",
				rsaRef.getBundle(), event.getProperty("bundle"));
		assertEquals(
				"122.7: bundle.symbolicname has to refer to RSA bundle SymbolicName",
				rsaRef.getBundle().getSymbolicName(),
				event.getProperty("bundle.symbolicname"));
		assertEquals("122.7: bundle.id has to refer to RSA bundle id",
				rsaRef.getBundle().getBundleId(),
				event.getProperty("bundle.id"));
		assertEquals(
				"122.7: bundle.version has to refer to RSA bundle version",
				rsaRef.getBundle().getVersion(),
				event.getProperty("bundle.version"));
		RemoteServiceAdminEvent rsaevent = (RemoteServiceAdminEvent) event
				.getProperty("event");
		return rsaevent;
	}

	public int getEventCount() {
		synchronized (eventlist) {
			return eventlist.size();
		}
	}
}
