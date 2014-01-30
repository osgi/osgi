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


package org.osgi.impl.service.dal.functions;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dal.PropertyMetadataImpl;
import org.osgi.impl.service.dal.SimulatedDeviceFunction;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.DeviceFunction;
import org.osgi.service.dal.DeviceFunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.MultiLevelSensor;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated <code>MultiLevelControl</code>.
 */
public final class SimulatedMultiLevelControl extends SimulatedDeviceFunction implements MultiLevelControl {

	private static final Map	PROPERTY_METADATA;
	private static final Map	OPERATION_METADATA	= null;
	private static final BigDecimal[]	VALUES;
	private static final LevelData[]	LEVEL_DATA;

	private LevelData					currentLevel;

	static {
		VALUES = new BigDecimal[100];
		LEVEL_DATA = new LevelData[VALUES.length];
		DeviceFunctionData[] enumValues = new LevelData[VALUES.length];
		for (int i = 0; i < VALUES.length; i++) {
			VALUES[i] = new BigDecimal(i);
			LEVEL_DATA[i] = new LevelData(Long.MIN_VALUE, null, null, VALUES[i]);
			enumValues[i] = LEVEL_DATA[i];
		}

		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.PROPERTY_ACCESS,
				new Integer(
						PropertyMetadata.PROPERTY_ACCESS_READABLE |
								PropertyMetadata.PROPERTY_ACCESS_WRITABLE |
								PropertyMetadata.PROPERTY_ACCESS_EVENTABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // resolution
				enumValues,// enumValues
				LEVEL_DATA[0],     // minValue
				LEVEL_DATA[LEVEL_DATA.length - 1]);    // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(MultiLevelSensor.PROPERTY_DATA, propMetadata);
	}
	
	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context used to register the service.
	 * @param eventAdminTracker The event amdin service tracker to post events.
	 */
	public SimulatedMultiLevelControl(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker) {
		super(
				MultiLevelControl.class.getName(),
				addPropertyAndOperationNames(functionProps),
				bc,
				PROPERTY_METADATA,
				OPERATION_METADATA,
				eventAdminTracker);
		this.currentLevel = new LevelData(System.currentTimeMillis(), null, null, VALUES[0]);
	}

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				DeviceFunction.SERVICE_PROPERTY_NAMES,
				new String[] {MultiLevelSensor.PROPERTY_DATA});
		return functionProps;
	}

	public LevelData getData() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		return this.currentLevel;
	}

	public void setData(BigDecimal level) throws UnsupportedOperationException, IllegalStateException, DeviceException {
		checkLevel(level);
		LevelData newLevel = new LevelData(System.currentTimeMillis(), null, null, level);
		this.currentLevel = newLevel;
		super.postEvent(MultiLevelControl.PROPERTY_DATA, newLevel);
	}

	public void setData(BigDecimal level, String unit) throws UnsupportedOperationException, IllegalStateException, DeviceException {
		setData(level);
	}

	private static void checkLevel(BigDecimal level) {
		if ((VALUES[0].compareTo(level) > 0) ||
				(VALUES[VALUES.length - 1].compareTo(level) < 0)) {
			throw new IllegalArgumentException("The value is not supported: " + level);
		}
	}

}
