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

import java.util.Map;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Common implementation of the simulated function.
 */
public class SimulatedFunction extends SimulatedService implements Function {

	/** The property metadata. */
	protected final Map	propertyMetadata;

	/** The operation metadata. */
	protected final Map	operationMetadata;

	private final ServiceTracker	eventAdminTracker;

	/**
	 * Constructs a new simulated function with the specified arguments.
	 * 
	 * @param propertyMetadata The property metadata.
	 * @param operationMetadata The operation metadata.
	 * @param eventAdminTracker The event admin tracker.
	 */
	public SimulatedFunction(
			Map propertyMetadata,
			Map operationMetadata,
			ServiceTracker eventAdminTracker) {
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
	 * Unregisters the function from the service registry.
	 */
	public void remove() {
		super.serviceReg.unregister();
	}

	/**
	 * Posts a new function property event through Event Admin service.
	 * 
	 * @param propName The function property name.
	 * @param propValue The function property value
	 */
	public void postEvent(String propName, FunctionData propValue) {
		final EventAdmin eventAdmin = (EventAdmin) this.eventAdminTracker.getService();
		if (null == eventAdmin) {
			throw new UnsupportedOperationException("The operation is not suported without Event Admin.");
		}
		FunctionEvent event = new FunctionEvent(
				FunctionEvent.TOPIC_PROPERTY_CHANGED,
				(String) this.getServiceProperty(Function.SERVICE_UID),
				propName,
				propValue);
		eventAdmin.postEvent(event);
	}

}
