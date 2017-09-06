/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

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
import org.osgi.service.dal.SIUnits;
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code Meter}.
 */
public final class SimulatedMeter extends SimulatedFunction implements Meter { // NO_UCD

	private static final String		MILLIS						= SIUnits.PREFIX_MILLI + SIUnits.SECOND;
	private static final String[]	MILLIS_ARRAY				= new String[] {MILLIS};
	private static final BigDecimal	CURRENT_MEASUREMENT			= new BigDecimal(1);
	private static final LevelData	CURRENT_MEASUREMENT_LEVEL	= new LevelData(Long.MIN_VALUE, null, CURRENT_MEASUREMENT, MILLIS);
	private static final Map		PROPERTY_METADATA;
	private static final Map		OPERATION_METADATA			= null;

	private final long				startTime;

	static {
		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(PropertyMetadata.ACCESS_READABLE |
						PropertyMetadata.ACCESS_EVENTABLE));
		metadata.put(
				PropertyMetadata.UNITS,
				MILLIS_ARRAY);
		PropertyMetadata currentPropMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				CURRENT_MEASUREMENT_LEVEL, // step
				new FunctionData[] {CURRENT_MEASUREMENT_LEVEL}, // enumValues
				CURRENT_MEASUREMENT_LEVEL, // minValue
				CURRENT_MEASUREMENT_LEVEL); // maxValue
		PropertyMetadata totalPropMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				CURRENT_MEASUREMENT_LEVEL, // step
				null, // enumValues
				new LevelData(Long.MIN_VALUE, null, new BigDecimal(0), MILLIS), // minValue
				null); // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(PROPERTY_CURRENT, currentPropMetadata);
		PROPERTY_METADATA.put(PROPERTY_TOTAL, totalPropMetadata);
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
		super.register(
				new String[] {Meter.class.getName(), Function.class.getName()},
				addPropertyNames(functionProps), bc);
	}

	public LevelData getCurrent() {
		return new LevelData(System.currentTimeMillis(), null, CURRENT_MEASUREMENT, MILLIS);
	}

	public LevelData getTotal() {
		long currentTime = System.currentTimeMillis();
		return new LevelData(currentTime, null, new BigDecimal(currentTime - this.startTime), MILLIS);
	}

	public void publishEvent(String propName) {
		if (PROPERTY_CURRENT.equals(propName)) {
			super.postEvent(propName, getCurrent());
		} else
			if (PROPERTY_TOTAL.equals(propName)) {
				super.postEvent(propName, getTotal());
			} else {
				throw new IllegalArgumentException("The property is not supported: " + propName);
			}
	}

	private static Dictionary addPropertyNames(Dictionary functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_CURRENT, PROPERTY_TOTAL});
		return functionProps;
	}
}
