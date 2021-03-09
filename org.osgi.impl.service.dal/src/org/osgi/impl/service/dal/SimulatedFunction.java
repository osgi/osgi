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
public abstract class SimulatedFunction extends SimulatedService implements Function {

	/** The property metadata. */
	private final Map<String,Object>					propertyMetadata;

	/** The operation metadata. */
	private final Map<String,Object>					operationMetadata;

	private final ServiceTracker<EventAdmin,EventAdmin>	eventAdminTracker;

	/**
	 * Constructs a new simulated function with the specified arguments.
	 * 
	 * @param propertyMetadata The property metadata.
	 * @param operationMetadata The operation metadata.
	 * @param eventAdminTracker The event admin tracker.
	 */
	public SimulatedFunction(
			Map<String,Object> propertyMetadata,
			Map<String,Object> operationMetadata,
			ServiceTracker<EventAdmin,EventAdmin> eventAdminTracker) {
		this.propertyMetadata = propertyMetadata;
		this.operationMetadata = operationMetadata;
		this.eventAdminTracker = eventAdminTracker;
	}

	@Override
	public PropertyMetadata getPropertyMetadata(String propertyName) {
		return (null == this.propertyMetadata) ? null :
				(PropertyMetadata) this.propertyMetadata.get(propertyName);
	}

	@Override
	public OperationMetadata getOperationMetadata(String operationName) {
		return (null == this.operationMetadata) ? null :
				(OperationMetadata) this.operationMetadata.get(operationName);
	}

	@Override
	public Object getServiceProperty(String propName) {
		Object value = super.serviceRef.getProperty(propName);
		if (null == value) {
			throw new IllegalArgumentException("The property name is missing: " + propName);
		}
		return value;
	}

	@Override
	public String[] getServicePropertyKeys() {
		return super.serviceRef.getPropertyKeys();
	}

	/**
	 * Unregisters the function from the service registry.
	 */
	public void remove() {
		super.serviceReg.unregister();
	}

	/**
	 * The child is responsible to publish an event with the current data.
	 * 
	 * @param propName The function property name.
	 * @throws IllegalArgumentException If the property is not supported.
	 */
	public abstract void publishEvent(String propName);

	/**
	 * Posts a new function property event through Event Admin service.
	 * 
	 * @param propName The function property name.
	 * @param propValue The function property value
	 */
	public void postEvent(String propName, FunctionData propValue) {
		EventAdmin eventAdmin = this.eventAdminTracker.getService();
		if (null == eventAdmin) {
			throw new IllegalStateException("The event cannot be posted because of missing Event Admin.");
		}
		FunctionEvent event = new FunctionEvent(
				FunctionEvent.TOPIC_PROPERTY_CHANGED,
				(String) this.getServiceProperty(Function.SERVICE_UID),
				propName,
				propValue);
		eventAdmin.postEvent(event);
	}
}
