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

package org.osgi.impl.service.dal.functions;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dal.OperationMetadataImpl;
import org.osgi.impl.service.dal.PropertyMetadataImpl;
import org.osgi.impl.service.dal.SimulatedFunction;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code BooleanControl}.
 */
public final class SimulatedBooleanControl extends SimulatedFunction implements BooleanControl { // NO_UCD

	private static final Map<String,Object>	PROPERTY_METADATA;
	private static final Map<String,Object>	OPERATION_METADATA;

	private BooleanData			data;

	static {
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(
						PropertyMetadata.ACCESS_READABLE |
								PropertyMetadata.ACCESS_WRITABLE |
								PropertyMetadata.ACCESS_EVENTABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // step
				null,     // enumValues
				null,     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap<>();
		PROPERTY_METADATA.put(PROPERTY_DATA, propMetadata);

		metadata = new HashMap<>();
		metadata.put(OperationMetadata.DESCRIPTION, "Simulator boolean control operation.");
		OperationMetadata opMetadata = new OperationMetadataImpl(metadata, null, null);
		OPERATION_METADATA = new HashMap<>();
		OPERATION_METADATA.put(OPERATION_INVERSE, opMetadata);
		OPERATION_METADATA.put(OPERATION_SET_FALSE, opMetadata);
		OPERATION_METADATA.put(OPERATION_SET_TRUE, opMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context to register the service.
	 * @param eventAdminTracker The event admin tracker to post events.
	 */
	public SimulatedBooleanControl(Dictionary<String,Object> functionProps,
			BundleContext bc,
			ServiceTracker<EventAdmin,EventAdmin> eventAdminTracker) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.data = new BooleanData(System.currentTimeMillis(), null, false);
		super.register(
				new String[] {BooleanControl.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	@Override
	public BooleanData getData() {
		return this.data;
	}

	@Override
	public void setData(boolean data) {
		if (this.data.getValue() == data) {
			return; // nothing to do
		}
		BooleanData newData = new BooleanData(System.currentTimeMillis(), null, data);
		this.data = newData;
		super.postEvent(PROPERTY_DATA, newData);
	}

	@Override
	public void inverse() {
		setData(this.data.getValue() ? false : true);
	}

	@Override
	public void setTrue() {
		setData(true);
	}

	@Override
	public void setFalse() {
		setData(false);
	}

	@Override
	public void publishEvent(String propName) {
		if (!PROPERTY_DATA.equals(propName)) {
			throw new IllegalArgumentException("The property is not supported: " + propName);
		}
		inverse();
	}

	private static Dictionary<String,Object> addPropertyAndOperationNames(
			Dictionary<String,Object> functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_DATA});
		functionProps.put(
				SERVICE_OPERATION_NAMES,
				new String[] {
						OPERATION_INVERSE,
						OPERATION_SET_FALSE,
						OPERATION_SET_TRUE});
		return functionProps;
	}
}
