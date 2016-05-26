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

package org.osgi.test.cases.zigbee.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Mocked test.
 * 
 * @author $Id$
 */
public class ZCLEventListenerImpl implements ZCLEventListener {

	private BundleContext		bc;
	private ServiceRegistration	sr;
	private ZigBeeEvent			lastReceivedZigBeeEvent	= null;

	/**
	 * @param bc
	 */
	public ZCLEventListenerImpl(BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Register the test event listener in the OSGi Service Registry.
	 */
	public void start() {
		bc.registerService(ZCLEventListener.class.getName(), this, null);
	}

	/**
	 * Unregister the test event listener from the OSGi Service Registry.
	 */
	public void stop() {
		if (sr != null) {
			sr.unregister();
		}
	}

	public void notifyEvent(ZigBeeEvent zigbeeEvent) {
		lastReceivedZigBeeEvent = zigbeeEvent;
		DefaultTestBundleControl.log(
				ZCLEventListener.class.getName() + " just received the following event: " + lastReceivedZigBeeEvent);
	}

	/**
	 * @return the lastReceivedZigBeeEvent.
	 */
	public ZigBeeEvent getLastReceivedZigBeeEvent() {
		return lastReceivedZigBeeEvent;
	}

	public void onFailure(ZCLException e) {
		// TODO Auto-generated method stub
	}

	public void notifyTimeOut(int timeout) {
		// TODO Auto-generated method stub
	}

}
