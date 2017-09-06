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
import org.osgi.service.dal.functions.Keypad;
import org.osgi.service.dal.functions.data.KeypadData;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Simulated {@code Keypad}.
 */
public final class SimulatedKeypad extends SimulatedFunction implements Keypad { // NO_UCD

	private static final Map	PROPERTY_METADATA;

	static {
		Map metadata = new HashMap();
		metadata.put(
				PropertyMetadata.ACCESS,
				Integer.valueOf(PropertyMetadata.ACCESS_EVENTABLE));
		PropertyMetadata propMetadata = new PropertyMetadataImpl(
				metadata, // metadata
				null,     // step
				null,     // enumValues
				null,     // minValue
				null);    // maxValue
		PROPERTY_METADATA = new HashMap();
		PROPERTY_METADATA.put(PROPERTY_KEY, propMetadata);
	}

	/**
	 * Constructs a new instance with the specified arguments.
	 * 
	 * @param functionProps The service properties.
	 * @param bc The bundle context to register the service.
	 * @param eventAdminTracker The event admin service tracker to post events.
	 */
	public SimulatedKeypad(Dictionary functionProps, BundleContext bc, ServiceTracker eventAdminTracker) {
		super(PROPERTY_METADATA, null, eventAdminTracker);
		super.register(
				new String[] {Keypad.class.getName(), Function.class.getName()},
				addPropertyAndOperationNames(functionProps), bc);
	}

	private static Dictionary addPropertyAndOperationNames(Dictionary functionProps) {
		functionProps.put(
				SERVICE_PROPERTY_NAMES,
				new String[] {PROPERTY_KEY});
		return functionProps;
	}

	public void publishEvent(String propName) {
		if (!PROPERTY_KEY.equals(propName)) {
			throw new IllegalArgumentException("The property is not supported: " + propName);
		}
		super.postEvent(propName,
				new KeypadData(
						System.currentTimeMillis(),
						null,
						KeypadData.TYPE_PRESSED,
						KeypadData.SUB_TYPE_PRESSED_NORMAL,
						64,
						"OSGi RI-test key"));
	}
}
