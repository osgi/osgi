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
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
	private LinkedList<RemoteServiceAdminEvent> eventlist = new LinkedList<RemoteServiceAdminEvent>();
	private Semaphore sem = new Semaphore(0);
	private long timeout;

	public TestRemoteServiceAdminListener(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @see org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener#remoteAdminEvent(org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent)
	 */
	@Override
	public void remoteAdminEvent(final RemoteServiceAdminEvent event) {
		eventlist.add(event);
		sem.release();
	}

	public RemoteServiceAdminEvent getNextEvent() {
		try {
			sem.tryAcquire(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			return null;
		}

		try {
			return eventlist.removeFirst();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public int getEventCount() {
		return eventlist.size();
	}
}
