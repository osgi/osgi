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
package org.osgi.impl.service.useradmin;

import java.util.Vector;

import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;

/**
 * The class manages the event queue to asynchronously deliver UserAdminEvents
 * to UserAdminListeners.
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @author Benjamin Reed (breed@almaden.ibm.com)
 * @author $Id$
 */
public class EventQueue extends Thread {
	/** event queue */
	protected Vector<Object[]>	queue;
	/** if false the thread must terminate */
	private volatile boolean	running;

	/**
	 * Constructor for the event queue. The queue is created empty and the
	 * thread is started.
	 *  
	 */
	EventQueue() {
		running = true;
		queue = new Vector<>(10, 10);
		setDaemon(true); /* Mark thread as daemon thread */
		start(); /* Start thread */
	}

	/**
	 * Destroy thread.
	 */
	void close() {
		running = false;
		interrupt();
	}

	/**
	 * 
	 * This method is the event queue dispatcher thread. It pulls events from
	 * the queue and dispatches them.
	 *  
	 */
	@Override
	public void run() {
		while (running) {
			try {
				Object[] item = getNextEvent();
				deliverEvent((Object[]) item[0], (UserAdminEvent) item[1]);
			}
			catch (Throwable t) {
				// ignored
			}
		}
	}

	/**
	 * This method is called to publish an Event
	 *  
	 */
	synchronized void publishEvent(Object listeners, UserAdminEvent event) {
		Object[] item = new Object[] {listeners, event};
		queue.addElement(item);
		notify();
	}

	/**
	 * This method waits until an item is in the queue and then removes it from
	 * the queue and returns it.
	 */
	protected synchronized Object[] getNextEvent() throws InterruptedException {
		while (running && queue.isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// ignored
			}
		}
		if (!running) /* if we are stopping */
		{
			throw new InterruptedException(); /* throw an exception */
		}
		Object[] item = queue.elementAt(0);
		queue.removeElementAt(0);
		return (item);
	}

	/**
	 * Deliver the logentry to the list of listeners.
	 */
	private void deliverEvent(Object listeners[], UserAdminEvent event) {
		if (listeners == null)
			return;
		for (int i = 0; i < listeners.length; i++) {
			try {
				((UserAdminListener) listeners[i]).roleChanged(event);
			}
			catch (Throwable t) { // Bad Bad Listener }
			}
		}
	}
}
