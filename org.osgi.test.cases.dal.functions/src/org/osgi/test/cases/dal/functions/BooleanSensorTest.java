/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions;

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.data.BooleanData;

/**
 * Validates the {@code BooleanSensor} functions.
 */
public final class BooleanSensorTest extends AbstractFunctionTest {

	/**
	 * Checks {@link BooleanSensor#getData()} getter functionality.
	 * 
	 * @throws IllegalStateException If the function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testGetData() throws IllegalStateException, DeviceException {
		Function[] booleanSensors = super.getFunctions(BooleanSensor.class.getName());
		boolean check = false;
		for (int i = 0; i < booleanSensors.length; i++) {
			final BooleanSensor currentBooleanSensor = (BooleanSensor) booleanSensors[i];
			try {
				BooleanData currentData = currentBooleanSensor.getData();
				check = true;
				assertNotNull("The boolean data cannot be null!", currentData);
				super.assertEquals(currentData.getValue(), currentData);
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Sensor must support getData operation.", check);
	}

	/**
	 * Checks {@code BooleanSensor} function events.
	 */
	public void testPropertyEvent() {
		super.checkPropertyEvent(BooleanSensor.class.getName(), BooleanSensor.PROPERTY_DATA);
	}

}
