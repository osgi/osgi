/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.impl.service.zigbee.util.teststep;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.zigbee.util.Logger;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Mocked test event source.
 * 
 * @author $Id$
 */
public class ZigBeeEventSourceImpl implements Runnable {

	private static final String	TAG	= ZigBeeEventSourceImpl.class.getName();

	private BundleContext		bc;
	private ZigBeeEvent			zigbeeEvent;
	private Thread				thread;
	private ServiceTracker		serviceTracker;

	/**
	 * @param bc
	 * @param zigbeeEvent
	 */
	public ZigBeeEventSourceImpl(BundleContext bc, ZigBeeEvent zigbeeEvent) {
		this.bc = bc;
		this.zigbeeEvent = zigbeeEvent;
	}

	/**
	 * Launch this testEventSource.
	 */
	public void start() {
		Logger.d(TAG, "start");
		serviceTracker = new ServiceTracker(bc,
				ZCLEventListener.class.getName(),
				null);
		serviceTracker.open();
		thread = new Thread(this, ZigBeeEventSourceImpl.class.getName()
				+ " - Whiteboard");
		thread.start();
	}

	/**
	 * Terminate this testEventSource.
	 */
	public void stop() {
		Logger.d(TAG, "stop.");
		serviceTracker.close();
		thread = null;
	}

	public synchronized void run() {
		Logger.d(TAG, "run");
		Thread current = Thread.currentThread();
		int n = 0;
		while (current == thread) {
			Object[] listeners = serviceTracker.getServices();

			if (listeners != null && listeners.length > 0) {
				if (n >= listeners.length) {
					n = 0;
				}
				ZCLEventListener aZCLEventListener = (ZCLEventListener) listeners[n++];

				Logger.d(TAG, "is sending the following event: " + zigbeeEvent);

				aZCLEventListener.notifyEvent(zigbeeEvent);
			}
			try {
				int waitinms = 1000;
				Logger.d(TAG, "wait(" + waitinms + ")");
				wait(waitinms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
