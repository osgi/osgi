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
import org.osgi.service.dal.functions.MultiLevelSensor;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated <code>MultiLevelSensor</code>.
 */
public final class SimulatedMultiLevelSensor extends SimulatedDeviceFunction implements MultiLevelSensor {

	private static final Map	PROPERTY_METADATA;
	private static final Map	OPERATION_METADATA	= null;
	private static final BigDecimal VALUE = new BigDecimal(1);
	private static final LevelData LEVEL_DATA = new LevelData(Long.MIN_VALUE, null, null, VALUE);
		
	static {
		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.PROPERTY_ACCESS,
				new Integer(
						PropertyMetadata.PROPERTY_ACCESS_READABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // resolution
				new DeviceFunctionData[] {LEVEL_DATA},// enumValues
				LEVEL_DATA,     // minValue
				LEVEL_DATA);    // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(MultiLevelSensor.PROPERTY_DATA, propMetadata);
	}
	
	/**
	 * COnstructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context used to register the service.
	 * @param eventAdminTracker The event admin service tracker to post events.
	 */
	public SimulatedMultiLevelSensor(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker) {
		super(
				MultiLevelSensor.class.getName(),
				addPropertyAndOperationNames(functionProps),
				bc,
				PROPERTY_METADATA,
				OPERATION_METADATA,
				eventAdminTracker);
	}

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				DeviceFunction.SERVICE_PROPERTY_NAMES,
				new String[] {MultiLevelSensor.PROPERTY_DATA});
		return functionProps;
	}

	public LevelData getData() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		return new LevelData(System.currentTimeMillis(), null, null, VALUE);
	}

}
