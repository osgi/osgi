/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal.functions;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dal.PropertyMetadataImpl;
import org.osgi.impl.service.dal.SimulatedFunction;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code BooleanSensor}.
 */
public final class SimulatedBooleanSensor extends SimulatedFunction implements BooleanSensor { // NO_UCD

	private static final Map	PROPERTY_METADATA;
	private static final Map	OPERATION_METADATA	= null;

	static {
		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(
						PropertyMetadata.ACCESS_READABLE |
								PropertyMetadata.ACCESS_EVENTABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // step
				null,     // enumValues
				null,     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(PROPERTY_DATA, propMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context to register the service.
	 * @param eventAdminTracker The event admin service tracker to post events.
	 */
	public SimulatedBooleanSensor(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker) {
		super(PROPERTY_METADATA, OPERATION_METADATA, eventAdminTracker);
		super.register(
				new String[] {Function.class.getName(), BooleanSensor.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_DATA});
		return functionProps;
	}

	public BooleanData getData() {
		return new BooleanData(System.currentTimeMillis(), null, true);
	}

	public void publishEvent(String propName) {
		if (!PROPERTY_DATA.equals(propName)) {
			throw new IllegalArgumentException("The property is not supported: " + propName);
		}
		super.postEvent(propName, getData());
	}
}
