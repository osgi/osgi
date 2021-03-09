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

import java.util.Map;

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.dal.functions.step.FunctionsTestSteps;

/**
 * Validates the {@code BooleanControl} functions.
 */
public final class BooleanControlTest extends AbstractFunctionTest {

	/**
	 * Checks {@link BooleanControl#setTrue()} operation functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetTrue() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		for (int i = 0; i < booleanControls.length; i++) {
			BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			currentBooleanControl.setTrue();
			super.assertEquals(true, currentBooleanControl.getData());
			checkMetadata(currentBooleanControl.getOperationMetadata(BooleanControl.OPERATION_SET_TRUE));
		}
	}

	/**
	 * Checks {@link BooleanControl#setFalse()} operation functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetFalse() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		for (int i = 0; i < booleanControls.length; i++) {
			BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			currentBooleanControl.setFalse();
			super.assertEquals(false, currentBooleanControl.getData());
			checkMetadata(currentBooleanControl.getOperationMetadata(BooleanControl.OPERATION_SET_FALSE));
		}
	}

	/**
	 * Checks {@link BooleanControl#inverse()} operation functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testReverse() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] booleanControls = super.getFunctions(BooleanControl.class.getName());
		for (int i = 0; i < booleanControls.length; i++) {
			BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			BooleanData currentData = currentBooleanControl.getData();
			currentBooleanControl.inverse();
			super.assertEquals(currentData.getValue() ? false : true, currentBooleanControl.getData());
			checkMetadata(currentBooleanControl.getOperationMetadata(BooleanControl.OPERATION_INVERSE));
		}
	}

	/**
	 * Checks {@code BooleanControl} function events.
	 * 
	 * @throws DeviceException If an error is available while executing the
	 *         operation.
	 */
	public void testPropertyEvent() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_BC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_BC);
		Function[] functions = getFunctions(
				BooleanControl.class.getName(), PropertyMetadata.ACCESS_EVENTABLE);
		BooleanControl booleanControl = (BooleanControl) functions[0];
		String functionUID = (String) booleanControl.getServiceProperty(Function.SERVICE_UID);
		FunctionEventHandler eventHandler = new FunctionEventHandler(super.getContext());
		eventHandler.register(functionUID, BooleanControl.PROPERTY_DATA);
		FunctionEvent functionEvent;
		BooleanData currentData = booleanControl.getData();
		try {
			booleanControl.inverse();
			functionEvent = eventHandler.getEvents(1)[0];
		} finally {
			eventHandler.unregister();
		}
		BooleanData propertyData = (BooleanData) functionEvent.getFunctionPropertyValue();
		super.assertEquals(!currentData.getValue(), propertyData);
		assertEquals(
				"The event function identifier is not correct!",
				functionUID,
				functionEvent.getFunctionUID());
		assertEquals(
				"The property name is not correct!",
				BooleanControl.PROPERTY_DATA,
				functionEvent.getFunctionPropertyName());
	}

	private void checkMetadata(OperationMetadata opMetadata) {
		if (null == opMetadata) {
			return;
		}
		assertNull("BooleanControl operation doesn't have parameters.",
				opMetadata.getParametersMetadata());
		assertNull("BooleanControl operation doesn't have return value.",
				opMetadata.getReturnValueMetadata());
		Map<String, ? > metadata = opMetadata.getMetadata();
		if (null == metadata) {
			return;
		}
		Object description = metadata.get(OperationMetadata.DESCRIPTION);
		if (null != description) {
			assertTrue("The operation description must be a string.", description instanceof String);
		}
	}
}
