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

import org.osgi.impl.service.zigbee.basedriver.RegistratonInfo;
import org.osgi.impl.service.zigbee.basedriver.configuration.ParserUtils;
import org.osgi.impl.service.zigbee.util.Logger;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZigBeeEvent;

/**
 * When start() is issued generates ZigBeeEvents and send them to the listener.
 * 
 * @author $Id$
 */
public class ZigBeeEventSourceImpl implements Runnable {

	private static final String	TAG		= ZigBeeEventSourceImpl.class.getName();

	private ZigBeeEvent			event;
	private Thread				thread;
	private RegistratonInfo		registrationInfo;

	private int					maxReportInterval;

	private boolean				exit	= false;

	public ZigBeeEventSourceImpl(RegistratonInfo registrationInfo, ZigBeeEvent event) {
		this.registrationInfo = registrationInfo;
		this.event = event;

		maxReportInterval = ParserUtils.getParameter(registrationInfo.properties, ZCLEventListener.MAX_REPORT_INTERVAL, ParserUtils.OPTIONAL, 0);
	}

	/**
	 * Launch this testEventSource.
	 */
	public void start() {
		Logger.d(TAG, "start ZigBeeEvents source.");
		thread = new Thread(this);
		thread.start();
	}

	public void update() {
		Logger.d(TAG, "update ZigBeeEvents source.");
		maxReportInterval = ParserUtils.getParameter(registrationInfo.properties, ZCLEventListener.MAX_REPORT_INTERVAL, ParserUtils.OPTIONAL, 0);
		if (thread != null) {
			exit = false;
			thread.interrupt();
		}
	}

	/**
	 * Terminate this testEventSource.
	 * 
	 * @throws InterruptedException
	 */
	public void stop() throws InterruptedException {
		Logger.d(TAG, "stop ZigBeeEvents source.");
		if (thread != null) {
			exit = true;
			this.thread.interrupt();
			this.thread.join(10000);
		}
	}

	public synchronized void run() {
		while (!exit) {
			this.registrationInfo.eventListener.notifyEvent(event);
			try {
				if (Thread.interrupted() && exit) {
					return;
				}
				Thread.sleep(maxReportInterval * 1000);
			} catch (InterruptedException e) {
				if (exit) {
					return;
				}
			}
		}
	}
}
