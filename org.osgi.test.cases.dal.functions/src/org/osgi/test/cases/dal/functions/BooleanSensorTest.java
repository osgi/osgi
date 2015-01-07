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
	 * @throws DeviceException If operation error is available.
	 */
	public void testGetData() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BS,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BS);
		Function[] booleanSensors = super.getFunctions(BooleanSensor.class.getName());
		for (int i = 0; i < booleanSensors.length; i++) {
			BooleanSensor currentBooleanSensor = (BooleanSensor) booleanSensors[i];
			BooleanData currentData = currentBooleanSensor.getData();
			assertNotNull("The boolean data cannot be null!", currentData);
			super.assertEquals(currentData.getValue(), currentData);
		}
	}

	/**
	 * Checks {@code BooleanSensor} function events.
	 */
	public void testPropertyEvent() {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BS,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BS);
		super.checkPropertyEvent(
				BooleanSensor.class.getName(),
				BooleanSensor.PROPERTY_DATA,
				FunctionsTestSteps.STEP_ID_EVENT_BS,
				FunctionsTestSteps.STEP_MESSAGE_EVENT_BS);
	}
}
