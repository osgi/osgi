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

package org.osgi.test.cases.zigbee.mock;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZigBeeEvent;

/**
 * Mocked test.
 * 
 * @author $Id$
 */
public class ZCLEventListenerImpl implements ZCLEventListener {

	@SuppressWarnings("unused")
	private static final String	TAG		= ZCLEventListenerImpl.class.getName();

	private BundleContext		bc;
	private ServiceRegistration<ZCLEventListener>	sReg;

	private StreamQueueImpl<ZigBeeEvent>			stream	= null;

	private ZCLException		failure;

	public ZCLEventListenerImpl(
			BundleContext bc) {
		this.bc = bc;
	}

	/**
	 * Register the test event listener in the OSGi Service Registry.
	 */
	public void start(Dictionary<String,Object> properties) {
		sReg = bc.registerService(ZCLEventListener.class, this, properties);
	}

	/**
	 * Unregister the test event listener from the OSGi Service Registry.
	 */
	public void stop() {
		synchronized (this) {
			if (sReg != null) {
				sReg.unregister();
			}
		}
	}

	public void notifyEvent(ZigBeeEvent zigbeeEvent) {
		synchronized (this) {
			if (stream != null) {
				stream.add(zigbeeEvent);
			}
		}
	}

	public ZCLException getFailure() {
		synchronized (this) {
			return failure;
		}
	}

	public void onFailure(ZCLException e) {
		synchronized (this) {
			this.failure = e;
		}
	}

	public void notifyTimeOut(int timeout) {

	}

	public void update(Dictionary<String,Object> newProperties) {
		synchronized (this) {
			this.failure = null;
			if (sReg != null) {
				sReg.setProperties(newProperties);
			}
		}
	}

	public StreamQueue<ZigBeeEvent> getStreamQueue() {
		synchronized (this) {
			stream = new StreamQueueImpl<>();
			return stream;
		}
	}
}
