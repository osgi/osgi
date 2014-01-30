/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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


package org.osgi.test.cases.dal;

import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceFunction;

/**
 * Test listener to track device and device function related service events.
 */
final class DeviceServiceListener implements ServiceListener {

	private static final String	DEVICE_FILTER	=
														"(|(" + Constants.OBJECTCLASS + '=' + Device.class.getName() + ")(" +
																Constants.OBJECTCLASS + '=' + DeviceFunction.class.getName() + "))";

	private final List			events;
	private final BundleContext	bc;

	public DeviceServiceListener(BundleContext bc) {
		this.bc = bc;
		this.events = new ArrayList();
	}

	public void register() throws InvalidSyntaxException {
		this.bc.addServiceListener(this, DEVICE_FILTER);
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
			return (ServiceEvent[]) this.events.toArray(new ServiceEvent[this.events.size()]);
		}
	}

	public void clear() {
		synchronized (this.events) {
			this.events.clear();
		}
	}

}
