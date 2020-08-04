/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.impl.service.usbinfo;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.service.usbinfo.USBInfoDevice;

class USBTracker {

	private static USBTracker	instance	= new USBTracker();

	private Map<String,ServiceRegistration< ? >>	usbMap		= new HashMap<>();

	private USBTracker() {
	}

	public static USBTracker getInstance() {
		return instance;
	}

	void open() {
		// empty
	}

	void close() {
		usbMap.clear();
	}

	synchronized String addUsb(Dictionary<String,Object> props) {

		Device device = new USBInfoDeviceImpl();
		String[] clazzes = new String[] {
				Device.class.getName(),
				USBInfoDevice.class.getName()
		};
		ServiceRegistration< ? > reg = Activator.getContext()
				.registerService(clazzes, device, props);
		String id = ((Long) reg.getReference().getProperty(Constants.SERVICE_ID)).toString();

		usbMap.put(id, reg);

		return id;
	}

	synchronized void removeUsb(String id) {

		ServiceRegistration< ? > reg = usbMap.remove(id);
		reg.unregister();
	}
}
