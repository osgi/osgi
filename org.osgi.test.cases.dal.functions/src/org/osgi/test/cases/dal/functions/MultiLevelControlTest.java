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
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.functions.step.FunctionsTestSteps;

/**
 * Validates the {@code MultiLevelControl} functions.
 */
public final class MultiLevelControlTest extends AbstractFunctionTest {

	/**
	 * Checks {@link MultiLevelControl#setData(java.math.BigDecimal, String)}
	 * setter functionality.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetData() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_MLC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_MLC);
		Function[] multiLevelControls = super.getFunctions(MultiLevelControl.class.getName());
		for (int i = 0; i < multiLevelControls.length; i++) {
			MultiLevelControl currentControl = (MultiLevelControl) multiLevelControls[i];
			PropertyMetadata currentPropertyMetadata = currentControl.getPropertyMetadata(
					MultiLevelControl.PROPERTY_DATA);
			LevelData[] levelData = getMultiLevelTestData(currentPropertyMetadata);
			if (null == levelData) {
				continue;
			}
			for (int ii = 0; ii < levelData.length; ii++) {
				currentControl.setData(levelData[ii].getLevel(), levelData[ii].getUnit());
				super.assertEquals(
						currentPropertyMetadata,
						levelData[ii].getLevel(),
						currentControl.getData());
			}
		}
	}

	/**
	 * Checks {@code MultiLevelControl} function events.
	 */
	public void testPropertyEvent() {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_MLC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_MLC);
		super.checkPropertyEvent(
				MultiLevelControl.class.getName(),
				MultiLevelControl.PROPERTY_DATA,
				FunctionsTestSteps.STEP_ID_EVENT_MLC,
				FunctionsTestSteps.STEP_MESSAGE_EVENT_MLC);
	}

	private LevelData[] getMultiLevelTestData(PropertyMetadata propertyMetadata) {
		if (null == propertyMetadata) {
			return null;
		}
		FunctionData[] testData = propertyMetadata.getEnumValues(null);
		if (null != testData) {
			LevelData[] testLevelData = new LevelData[testData.length];
			for (int i = 0; i < testLevelData.length; i++) {
				testLevelData[i] = (LevelData) testData[i];
			}
			return testLevelData;
		}
		FunctionData minData = propertyMetadata.getMinValue(null);
		FunctionData maxData = propertyMetadata.getMaxValue(null);
		if (null == minData) {
			return (null == maxData) ? null : new LevelData[] {(LevelData) maxData};
		} else {
			return (null == maxData) ? new LevelData[] {(LevelData) minData} :
					new LevelData[] {(LevelData) minData, (LevelData) maxData};
		}
	}
}
