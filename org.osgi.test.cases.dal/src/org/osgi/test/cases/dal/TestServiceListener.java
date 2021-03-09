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

package org.osgi.test.cases.dal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;

/**
 * Test listener to track device and function related service events.
 */
final class TestServiceListener implements ServiceListener {

	public static final String	DEVICE_FILTER			= '(' + Constants.OBJECTCLASS + '=' + Device.class.getName() + ')';

	public static final String	DEVICE_FUNCTION_FILTER	= "(|" + DEVICE_FILTER +
																'(' + Constants.OBJECTCLASS + '=' + Function.class.getName() + "))";

	private final List<ServiceEvent>	events;
	private final BundleContext	bc;

	public TestServiceListener(BundleContext bc, String filter) throws InvalidSyntaxException {
		this.bc = bc;
		this.events = new ArrayList<>();
		this.bc.addServiceListener(this, filter);
	}

	public void unregister() {
		this.bc.removeServiceListener(this);
	}

	public void serviceChanged(ServiceEvent event) {
		synchronized (this.events) {
			this.events.add(event);
		}
	}

	public ServiceEvent[] getEvents() {
		synchronized (this.events) {
			return this.events.toArray(new ServiceEvent[0]);
		}
	}

	public void clear() {
		synchronized (this.events) {
			this.events.clear();
		}
	}
}
