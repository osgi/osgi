/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.dal.functions;

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.MultiLevelSensor;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.functions.step.FunctionsTestSteps;

/**
 * Validates the {@code MultiLevelSensor} functions.
 */
public final class MultiLevelSensorTest extends AbstractFunctionTest {

	/**
	 * Checks {@link MultiLevelSensor#getData()} getter functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testGetData() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_MLS,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_MLS);
		Function[] multiLevelSensors = super.getFunctions(MultiLevelSensor.class.getName());
		for (int i = 0; i < multiLevelSensors.length; i++) {
			MultiLevelSensor currentSensor = (MultiLevelSensor) multiLevelSensors[i];
			LevelData currentData = currentSensor.getData();
			assertNotNull("The level data cannot be null!", currentData);
			super.assertEquals(
					currentSensor.getPropertyMetadata(MultiLevelSensor.PROPERTY_DATA),
					currentData.getLevel(), currentData);
		}
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
