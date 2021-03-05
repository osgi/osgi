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
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.dal.functions.step.FunctionsTestSteps;

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
