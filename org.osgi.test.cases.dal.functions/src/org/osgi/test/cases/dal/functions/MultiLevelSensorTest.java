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
import org.osgi.service.dal.functions.MultiLevelSensor;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * Validates the {@code MultiLevelSensor} functions.
 */
public final class MultiLevelSensorTest extends AbstractFunctionTest {

	/**
	 * Checks {@link MultiLevelSensor#getData()} getter functionality.
	 * 
	 * @throws IllegalStateException If the function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testGetData() throws IllegalStateException, DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_MLS,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_MLS);
		Function[] multiLevelSensors = super.getFunctions(MultiLevelSensor.class.getName());
		boolean check = false;
		for (int i = 0; i < multiLevelSensors.length; i++) {
			final MultiLevelSensor currentSensor = (MultiLevelSensor) multiLevelSensors[i];
			try {
				LevelData currentData = currentSensor.getData();
				check = true;
				assertNotNull("The level data cannot be null!", currentData);
				super.assertEquals(
						currentSensor.getPropertyMetadata(MultiLevelSensor.PROPERTY_DATA),
						currentData.getLevel(), currentData);
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Sensor must support getData operation.", check);
	}

	/**
	 * Checks {@code MultiLevelSensor} function events.
	 */
	public void testPropertyEvent() {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_MLS,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_MLS);
		super.checkPropertyEvent(
				MultiLevelSensor.class.getName(),
				MultiLevelSensor.PROPERTY_DATA,
				FunctionsTestSteps.STEP_ID_EVENT_MLS,
				FunctionsTestSteps.STEP_MESSAGE_EVENT_MLS);
	}

}
