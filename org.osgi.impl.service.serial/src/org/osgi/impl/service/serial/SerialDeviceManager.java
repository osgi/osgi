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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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

	void addSerialDevice(String comport, Properties props) {
		SerialDevice service = new SerialDeviceImpl(comport);
		ServiceRegistration reg = Activator.getContext().registerService(SerialDevice.class.getName(), service, props);

		propsMap.put(comport, props);
		regMap.put(comport, reg);
	}

	void modifySerialDevice(String comport) {
		Properties props = (Properties) propsMap.get(comport);
		ServiceRegistration reg = (ServiceRegistration) regMap.get(comport);
		reg.setProperties(props);
	}

	void removeSerialDevice(String comport) {
		ServiceRegistration reg = (ServiceRegistration) regMap.remove(comport);
		if (reg != null) {
			reg.unregister();
		}

		propsMap.remove(comport);
	}
}
