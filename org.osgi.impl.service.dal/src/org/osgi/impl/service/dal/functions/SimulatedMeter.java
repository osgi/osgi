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
import org.osgi.impl.service.dal.SimulatedFunction;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.Units;
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated <code>Meter</code>.
 */
public final class SimulatedMeter extends SimulatedFunction implements Meter {

	private static final String	MILLIS	= Units.PREFIX_MILLI + Units.SECOND;
	private static final String[]	MILLIS_ARRAY				= new String[] {MILLIS};
	private static final BigDecimal	CURRENT_MEASUREMENT	= new BigDecimal(1);
	private static final LevelData	CURRENT_MEASUREMENT_LEVEL	= new LevelData(Long.MIN_VALUE, null, MILLIS, CURRENT_MEASUREMENT);
	private static final Map		PROPERTY_METADATA;
	private static final Map		OPERATION_METADATA	= null;

	private long	startTime;

	static {
		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.PROPERTY_ACCESS,
				new Integer(PropertyMetadata.PROPERTY_ACCESS_READABLE));
		metadata.put(
				PropertyMetadata.UNITS,
				MILLIS_ARRAY);
		PropertyMetadata currentPropMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				CURRENT_MEASUREMENT_LEVEL,     // resolution
				new FunctionData[] {CURRENT_MEASUREMENT_LEVEL},// enumValues
				CURRENT_MEASUREMENT_LEVEL,     // minValue
				CURRENT_MEASUREMENT_LEVEL);    // maxValue
		PropertyMetadata totalPropMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				CURRENT_MEASUREMENT_LEVEL,     // resolution
				null,// enumValues
				new LevelData(Long.MIN_VALUE, null, MILLIS, new BigDecimal(0)),     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(Meter.PROPERTY_CURRENT, currentPropMetadata);
		PROPERTY_METADATA.put(Meter.PROPERTY_TOTAL, totalPropMetadata);
	}

	/**
	 * Constructs a new simulated meter.
	 * 
	 * @param functionProps The meter service properties.
	 * @param bc The bundle context used for the registration.
	 * @param eventAdminTracker The event admin tracker used for event
	 *        notifications.
	 */
	public SimulatedMeter(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.startTime = System.currentTimeMillis();
		super.register(Meter.class.getName(), addPropertyAndOperationNames(functionProps), bc);
	}

	public LevelData getCurrent() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		return new LevelData(System.currentTimeMillis(), null, MILLIS, CURRENT_MEASUREMENT);
	}

	public LevelData getTotal() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		long currentTime = System.currentTimeMillis();
		return new LevelData(currentTime, null, MILLIS, new BigDecimal(currentTime - this.startTime));
	}

	public void resetTotal() throws UnsupportedOperationException, IllegalStateException, DeviceException {
		this.startTime = System.currentTimeMillis();
	}

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				Function.SERVICE_PROPERTY_NAMES,
				new String[] {Meter.PROPERTY_CURRENT, Meter.PROPERTY_TOTAL});
		functionProps.put(
				Function.SERVICE_OPERATION_NAMES,
				new String[] {Meter.OPERATION_RESET_TOTAL});
		return functionProps;
	}

}
