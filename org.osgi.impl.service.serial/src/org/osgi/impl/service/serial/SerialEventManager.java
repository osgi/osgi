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

import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.SerialEvent;
import org.osgi.service.serial.SerialEventListener;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class SerialEventManager {
	private BundleContext	context;
	private List			list	= new LinkedList();

	public SerialEventManager(BundleContext context) {
		this.context = context;
		ServiceTracker tracker = new ServiceTracker(context, SerialEventListener.class.getName(), new SerialCustomizer());
		tracker.open();
	}

	public void sendEvent(String id) {
		try {
			String serialDeviceFilter = "(service.id=" + id + ")";
			ServiceReference[] serialDeviceRefs = context.getServiceReferences(SerialDevice.class.getName(), serialDeviceFilter);
			String comPort = (String) serialDeviceRefs[0].getProperty(SerialDevice.SERIAL_COMPORT);

			SerialEvent event = new SerialEventImpl(comPort, SerialEvent.DATA_AVAILABLE);
			for (int i = 0; i < list.size(); i++) {
				ServiceReference ref = (ServiceReference) list.get(i);
				String filter = (String) ref.getProperty(SerialEventListener.SERIAL_COMPORT);
				if (filter == null) {
					SerialEventListener listener = (SerialEventListener) context.getService(ref);
					listener.notifyEvent(event);
				} else if (filter.equals(comPort)) {
					SerialEventListener listener = (SerialEventListener) context.getService(ref);
					listener.notifyEvent(event);
				}
			}

		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	private class SerialCustomizer implements ServiceTrackerCustomizer {

		public Object addingService(ServiceReference reference) {
			list.add(reference);
			return reference;
		}

		public void modifiedService(ServiceReference reference, Object service) {
		}

		public void removedService(ServiceReference reference, Object service) {
			list.remove(reference);
		}
	}
}
