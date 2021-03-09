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
package org.osgi.test.cases.wireadmin.junit;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdminEvent;
import org.osgi.service.wireadmin.WireAdminListener;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Used to test the correct event dispatchment within the wire admin as required
 * 
 * @author Vasil Panushev
 * @version 1.1 Aug 2005
 */
public class TestWireAdminListener implements WireAdminListener {
	private final WireAdminControl	wac;
	private final boolean	dummy;
	private final List<Object>		valuesReceived	= new ArrayList<>();
	private boolean					called			= false;

	public TestWireAdminListener(WireAdminControl wac, boolean dummy) {
		this.wac = wac;
		this.dummy = dummy;
	}

	/**
	 * Called whenever event is dispatched in the wire admin
	 * 
	 * @param event describes the fired event
	 */
	@Override
	public void wireAdminEvent(WireAdminEvent event) {
		Wire wire = event.getWire();
		if (wire != null) {
			// check for right test case
			if (!wac.getName().equals(
					wire.getProperties().get("org.osgi.test.wireadmin"))) {
				DefaultTestBundleControl
						.log("received an event from a different test case");
				return;
			}
		}
		try {
			int type = event.getType();
			if (dummy) {
				DefaultTestBundleControl.log("Dummy received an event! Error");
				synchronized (this) {
					valuesReceived.add(Integer.valueOf(type));
				}
				return;
			}
			DefaultTestBundleControl.log("received event " + getEventName(type));
			if (wire != null) {
				Dictionary<String,Object> dict = event.getWire()
						.getProperties();
				String prop = (String) dict
						.get("org.osgi.test.wireadmin.property");
				if ("42".equals(prop)) {
					DefaultTestBundleControl.log("wire is OK");
					synchronized (this) {
						valuesReceived.add(Integer.valueOf(type));
					}
				}
				else {
					DefaultTestBundleControl.log("wire is other than expected");
				}
			}
			else {
				DefaultTestBundleControl
						.log("event.getWire() returned null. No specific wire was responsible for the event");
			}

			if ((event.getType() & (WireAdminEvent.CONSUMER_EXCEPTION | WireAdminEvent.PRODUCER_EXCEPTION)) != 0) {
				Throwable t = event.getThrowable();
				if (t == null) {
					DefaultTestBundleControl.log("Throwable not passed! Error");
					synchronized (this) {
						valuesReceived.add("no throwable received");
					}
				}
				else {
					synchronized (this) {
						valuesReceived.add(t.getMessage());
					}
					if ("testing".equals(t.getMessage())) {
						DefaultTestBundleControl.log("correct Throwable passed! OK");
					}
					else {
						DefaultTestBundleControl.log("wrong Throwable passed! Error");
					}
				}
			}
		}
		finally {
			synchronized (this) {
				called = true;
				notify();
			}
		}
	}

	synchronized void waitForCall(final long timeout) {
		final long endTime = timeout + System.currentTimeMillis();
		while (!called) {
			final long waitTime = endTime - System.currentTimeMillis();
			if (waitTime <= 0) {
				break;
			}
			try {
				DefaultTestBundleControl.log("WireAdminListener waiting for call: "
						+ waitTime);
				wait(waitTime);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				OSGiTestCase.fail("Unexepected interruption.", e);
			}
		}
	}

	synchronized List<Object> resetValuesReceived() {
		List<Object> result = new ArrayList<>(valuesReceived);
		valuesReceived.clear();
		called = false;
		return result;
	}

	private String getEventName(int type) {
		switch (type) {
			case WireAdminEvent.PRODUCER_EXCEPTION :
				return "PRODUCER_EXCEPTION";
			case WireAdminEvent.CONSUMER_EXCEPTION :
				return "CONSUMER_EXCEPTION";
			case WireAdminEvent.WIRE_CREATED :
				return "WIRE_CREATED";
			case WireAdminEvent.WIRE_UPDATED :
				return "WIRE_UPDATED";
			case WireAdminEvent.WIRE_DELETED :
				return "WIRE_DELETED";
			case WireAdminEvent.WIRE_CONNECTED :
				return "WIRE_CONNECTED";
			case WireAdminEvent.WIRE_DISCONNECTED :
				return "WIRE_DISCONNECTED";
			case WireAdminEvent.WIRE_TRACE :
				return "WIRE_TRACE";
			default :
				return "UNKNONW";
		}
	}
}
