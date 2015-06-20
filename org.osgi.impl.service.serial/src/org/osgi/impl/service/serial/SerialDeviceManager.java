/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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
import java.util.Properties;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.serial.SerialDevice;

final class SerialDeviceManager {

	private static SerialDeviceManager	instance	= new SerialDeviceManager();

	private Map							propsMap	= new HashMap();
	private Map							regMap		= new HashMap();

	private SerialDeviceManager() {
	}

	static SerialDeviceManager getInstance() {
		return instance;
	}

	String addSerialDevice(Properties props) {
		SerialDevice service = new SerialDeviceImpl(props.getProperty(SerialDevice.SERIAL_COMPORT));
		ServiceRegistration reg = Activator.getContext().registerService(SerialDevice.class.getName(), service, (Dictionary)props);

		String id = ((Long) reg.getReference().getProperty(Constants.SERVICE_ID)).toString();

		propsMap.put(id, props);
		regMap.put(id, reg);

		return id;
	}

	void modifySerialDevice(String id) {
		Properties props = (Properties) propsMap.get(id);
		ServiceRegistration reg = (ServiceRegistration) regMap.get(id);
		reg.setProperties(props);
	}

	void removeSerialDevice(String id) {
		ServiceRegistration reg = (ServiceRegistration) regMap.remove(id);
		if (reg != null) {
			reg.unregister();
		}

		propsMap.remove(id);
	}
}
