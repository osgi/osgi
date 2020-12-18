/*
 * Copyright (c) OSGi Alliance (2015, 2020). All Rights Reserved.
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

package org.osgi.impl.service.serial;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.serial.SerialDevice;

final class SerialDeviceManager {

	private static SerialDeviceManager	instance	= new SerialDeviceManager();

	private Map<String,Dictionary<String,Object>>			propsMap	= new HashMap<>();
	private Map<String,ServiceRegistration<SerialDevice>>	regMap		= new HashMap<>();

	private SerialDeviceManager() {
	}

	static SerialDeviceManager getInstance() {
		return instance;
	}

	String addSerialDevice(Dictionary<String,Object> props) {
		SerialDevice service = new SerialDeviceImpl(
				(String)
				props.get(SerialDevice.SERIAL_COMPORT));
		ServiceRegistration<SerialDevice> reg = Activator.getContext()
				.registerService(SerialDevice.class, service, props);

		String id = ((Long) reg.getReference()
				.getProperty(Constants.SERVICE_ID)).toString();

		propsMap.put(id, props);
		regMap.put(id, reg);

		return id;
	}

	void modifySerialDevice(String id) {
		Dictionary<String,Object> props = propsMap.get(id);
		ServiceRegistration<SerialDevice> reg = regMap.get(id);
		reg.setProperties(props);
	}

	void removeSerialDevice(String id) {
		ServiceRegistration<SerialDevice> reg = regMap.remove(id);
		if (reg != null) {
			reg.unregister();
		}

		propsMap.remove(id);
	}
}
