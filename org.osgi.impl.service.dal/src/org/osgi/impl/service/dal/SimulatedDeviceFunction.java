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


package org.osgi.impl.service.dal;

import java.util.Dictionary;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.service.dal.DeviceFunction;
import org.osgi.service.dal.DeviceFunctionData;
import org.osgi.service.dal.DeviceFunctionEvent;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Common implementation of the simulated device function.
 */
public class SimulatedDeviceFunction extends SimulatedService implements DeviceFunction {

	/** The property metadata. */
	protected final Map	propertyMetadata;

	/** The operation metadata. */
	protected final Map	operationMetadata;

	private final ServiceTracker	eventAdminTracker;

	/**
	 * COnstructs a new simulated device function with the specified arguments.
	 * 
	 * @param className The function class name.
	 * @param functionProps The function service properties.
	 * @param bc The bundle context to register the function.
	 * @param propertyMetadata The property metadata.
	 * @param operationMetadata The operation metadata.
	 * @param eventAdminTracker The event admin tracker.
	 */
	public SimulatedDeviceFunction(
			String className,
			Dictionary functionProps,
			BundleContext bc,
			Map propertyMetadata,
			Map operationMetadata,
			ServiceTracker eventAdminTracker) {
		super(className, functionProps, bc);
		this.propertyMetadata = propertyMetadata;
		this.operationMetadata = operationMetadata;
		this.eventAdminTracker = eventAdminTracker;
	}

	public PropertyMetadata getPropertyMetadata(String propertyName) throws IllegalArgumentException {
		return (PropertyMetadata) this.propertyMetadata.get(propertyName);
	}

	public OperationMetadata getOperationMetadata(String operationName) throws IllegalArgumentException {
		return (OperationMetadata) this.operationMetadata.get(operationName);
	}

	public Object getServiceProperty(String propName) throws IllegalArgumentException {
		Object value = super.serviceRef.getProperty(propName);
		if (null == value) {
			throw new IllegalArgumentException("The property name is missing: " + propName);
		}
		return value;
	}

	/**
	 * Unregisters the device function from the service registry.
	 */
	public void remove() {
		super.serviceReg.unregister();
	}

	/**
	 * Posts a new device function property event through Event Admin service.
	 * 
	 * @param propName The device function property name.
	 * @param propValue The device function property value
	 */
	protected void postEvent(String propName, DeviceFunctionData propValue) {
		final EventAdmin eventAdmin = (EventAdmin) this.eventAdminTracker.getService();
		if (null == eventAdmin) {
			throw new UnsupportedOperationException("The operation is not suported without Event Admin.");
		}
		DeviceFunctionEvent event = new DeviceFunctionEvent(
				DeviceFunctionEvent.TOPIC_PROPERTY_CHANGED,
				(String) this.getServiceProperty(DeviceFunction.SERVICE_UID),
				propName,
				propValue);
		eventAdmin.postEvent(event);
	}

}
