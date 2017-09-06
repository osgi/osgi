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
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code MultiLevelControl}.
 */
public final class SimulatedMultiLevelControl extends SimulatedFunction implements MultiLevelControl { // NO_UCD

	private static final Map			PROPERTY_METADATA;
	private static final Map			OPERATION_METADATA	= null;
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

		Map metadata = new HashMap();
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
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(PROPERTY_DATA, propMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context used to register the service.
	 * @param eventAdminTracker The event amdin service tracker to post events.
	 */
	public SimulatedMultiLevelControl(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		this.currentLevel = new LevelData(System.currentTimeMillis(), null, VALUES[0], null);
		super.register(
				new String[] {MultiLevelControl.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_DATA});
		return functionProps;
	}

	public LevelData getData() {
		return this.currentLevel;
	}

	public void setData(BigDecimal level, String unit) {
		if (this.currentLevel.getLevel().equals(level)) {
			return; // nothing to do
		}
		checkLevel(level);
		LevelData newLevel = new LevelData(System.currentTimeMillis(), null, level, null);
		this.currentLevel = newLevel;
		super.postEvent(PROPERTY_DATA, newLevel);
	}

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
