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

import java.util.Collection;
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
	List<ServiceReference<SerialEventListener>>	list	= new LinkedList<>();

	public SerialEventManager(BundleContext context) {
		this.context = context;
		ServiceTracker<SerialEventListener,ServiceReference<SerialEventListener>> tracker = new ServiceTracker<>(
				context, SerialEventListener.class, new SerialCustomizer());
		tracker.open();
	}

	public void sendEvent(String id) {
		try {
			String serialDeviceFilter = "(service.id=" + id + ")";
			Collection<ServiceReference<SerialDevice>> serialDeviceRefs = context
					.getServiceReferences(SerialDevice.class,
							serialDeviceFilter);
			String comPort = (String) serialDeviceRefs.iterator()
					.next()
					.getProperty(SerialDevice.SERIAL_COMPORT);

			SerialEvent event = new SerialEventImpl(comPort, SerialEvent.DATA_AVAILABLE);
			for (int i = 0; i < list.size(); i++) {
				ServiceReference<SerialEventListener> ref = list.get(i);
				String filter = (String) ref.getProperty(SerialEventListener.SERIAL_COMPORT);
				if (filter == null) {
					SerialEventListener listener = context.getService(ref);
					listener.notifyEvent(event);
				} else if (filter.equals(comPort)) {
					SerialEventListener listener = context.getService(ref);
					listener.notifyEvent(event);
				}
			}

		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	private class SerialCustomizer implements
			ServiceTrackerCustomizer<SerialEventListener,ServiceReference<SerialEventListener>> {

		SerialCustomizer() {
			super();
		}
		@Override
		public ServiceReference<SerialEventListener> addingService(
				ServiceReference<SerialEventListener> reference) {
			list.add(reference);
			return reference;
		}

		@Override
		public void modifiedService(
				ServiceReference<SerialEventListener> reference,
				ServiceReference<SerialEventListener> service) {
			// empty
		}

		@Override
		public void removedService(
				ServiceReference<SerialEventListener> reference,
				ServiceReference<SerialEventListener> service) {
			list.remove(reference);
		}
	}
}
