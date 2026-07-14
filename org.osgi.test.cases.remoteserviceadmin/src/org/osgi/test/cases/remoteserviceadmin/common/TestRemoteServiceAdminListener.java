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

import java.util.LinkedList;
import java.util.Queue;

import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

/**
 * RemoteServiceAdminListener implementation, which collects and returns the
 * received events in order.
 *
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class TestRemoteServiceAdminListener implements
		RemoteServiceAdminListener {
	private final Queue<RemoteServiceAdminEvent> events = new LinkedList<RemoteServiceAdminEvent>();
	private final long timeout;

	public TestRemoteServiceAdminListener(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @see org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener#remoteAdminEvent(org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent)
	 */
	@Override
	public void remoteAdminEvent(final RemoteServiceAdminEvent event) {
		synchronized (events) {
			events.offer(event);
			events.notify();
		}
	}

	public RemoteServiceAdminEvent getNextEvent() {
		try {
			long end = System.currentTimeMillis() + timeout;
			while (true) {
				synchronized (events) {
					RemoteServiceAdminEvent event = events.poll();
					if (event != null) {
						return event;
					}
					long remaining = end - System.currentTimeMillis();
					if (remaining <= 0) {
						return null;
					}
					events.wait(remaining);
				}
			}
		} catch (InterruptedException e1) {
			return null;
		}
	}

	public int getEventCount() {
		synchronized (events) {
			return events.size();
		}
	}
}
