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
import org.osgi.impl.service.dal.PropertyMetadataImpl;
import org.osgi.impl.service.dal.SimulatedFunction;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.Alarm;
import org.osgi.service.dal.functions.data.AlarmData;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code Alarm}.
 */
public final class SimulatedAlarm extends SimulatedFunction implements Alarm { // NO_UCD

	private static final Map<String,Object> PROPERTY_METADATA;

	static {
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(PropertyMetadata.ACCESS_EVENTABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // step
				null,     // enumValues
				null,     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap<>();
		PROPERTY_METADATA.put(PROPERTY_ALARM, propMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context to register the service.
	 * @param eventAdminTracker The event admin service tracker to post events.
	 */
	public SimulatedAlarm(Dictionary<String,Object> functionProps,
			BundleContext bc,
			ServiceTracker<EventAdmin,EventAdmin> eventAdminTracker) {
		super(PROPERTY_METADATA, null, eventAdminTracker);
		super.register(
				new String[] {Alarm.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	private static Dictionary<String,Object> addPropertyAndOperationNames(
			Dictionary<String,Object> functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_ALARM});
		return functionProps;
	}

	@Override
	public void publishEvent(String propName) {
		if (!PROPERTY_ALARM.equals(propName)) {
			throw new IllegalArgumentException("The property is not supported: " + propName);
		}
		super.postEvent(propName,
				new AlarmData(System.currentTimeMillis(), null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD));
	}
}
