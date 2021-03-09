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

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dal.PropertyMetadataImpl;
import org.osgi.impl.service.dal.SimulatedFunction;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code MultiLevelControl}.
 */
public final class SimulatedMultiLevelControl extends SimulatedFunction implements MultiLevelControl { // NO_UCD

	private static final Map<String,Object>	PROPERTY_METADATA;
	private static final Map<String,Object>	OPERATION_METADATA	= null;
	private static final BigDecimal[]	VALUES;
	private static final LevelData[]	LEVEL_DATA;

	private LevelData					currentLevel;

	static {
		VALUES = new BigDecimal[100];
		LEVEL_DATA = new LevelData[VALUES.length];
		FunctionData[] enumValues = new LevelData[VALUES.length];
		for (int i = 0; i < VALUES.length; i++) {
			VALUES[i] = new BigDecimal(i);
			LEVEL_DATA[i] = new LevelData(Long.MIN_VALUE, null, VALUES[i], null);
			enumValues[i] = LEVEL_DATA[i];
		}

		Map<String,Object> metadata = new HashMap<>();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(
						PropertyMetadata.ACCESS_READABLE |
								PropertyMetadata.ACCESS_WRITABLE |
								PropertyMetadata.ACCESS_EVENTABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null, // step
				enumValues, // enumValues
				LEVEL_DATA[0], // minValue
				LEVEL_DATA[LEVEL_DATA.length - 1]); // maxValue
		PROPERTY_METADATA = new HashMap<>();
		PROPERTY_METADATA.put(PROPERTY_DATA, propMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context used to register the service.
	 * @param eventAdminTracker The event amdin service tracker to post events.
	 */
	public SimulatedMultiLevelControl(Dictionary<String,Object> functionProps,
			BundleContext bc,
			ServiceTracker<EventAdmin,EventAdmin> eventAdminTracker) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.currentLevel = new LevelData(System.currentTimeMillis(), null, VALUES[0], null);
		super.register(
				new String[] {MultiLevelControl.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	private static Dictionary<String,Object> addPropertyAndOperationNames(
			Dictionary<String,Object> functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_DATA});
		return functionProps;
	}

	@Override
	public LevelData getData() {
		return this.currentLevel;
	}

	@Override
	public void setData(BigDecimal level, String unit) {
		if (this.currentLevel.getLevel().equals(level)) {
			return; // nothing to do
		}
		checkLevel(level);
		LevelData newLevel = new LevelData(System.currentTimeMillis(), null, level, null);
		this.currentLevel = newLevel;
		super.postEvent(PROPERTY_DATA, newLevel);
	}

	@Override
	public void publishEvent(String propName) {
		if (!PROPERTY_DATA.equals(propName)) {
			throw new IllegalArgumentException("The property is not supported: " + propName);
		}
		BigDecimal newValue = this.currentLevel.getLevel().equals(VALUES[0]) ?
				VALUES[VALUES.length - 1] : VALUES[0];
		setData(newValue, null);
	}

	private static void checkLevel(BigDecimal level) {
		if ((VALUES[0].compareTo(level) > 0) ||
				(VALUES[VALUES.length - 1].compareTo(level) < 0)) {
			throw new IllegalArgumentException("The value is not supported: " + level);
		}
	}
}
