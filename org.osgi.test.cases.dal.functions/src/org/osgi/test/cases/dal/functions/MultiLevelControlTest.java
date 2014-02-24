/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.dal.functions;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * Validates the <code>MultiLevelControl</code> functions.
 */
public final class MultiLevelControlTest extends AbstractFunctionTest {

	/**
	 * Checks {@link MultiLevelControl#setData(java.math.BigDecimal, String)}
	 * setter functionality.
	 * 
	 * @throws IllegalStateException If the function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetData() throws IllegalStateException, DeviceException {
		Function[] multiLevelControls = null;
		try {
			multiLevelControls = super.getFunctions(MultiLevelControl.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < multiLevelControls.length; i++) {
			final MultiLevelControl currentControl = (MultiLevelControl) multiLevelControls[i];
			final PropertyMetadata currentPropertyMetadata = currentControl.getPropertyMetadata(
					MultiLevelControl.PROPERTY_DATA);
			final LevelData[] levelData = getMultiLevelTestData(currentPropertyMetadata);
			if (null == levelData) {
				continue;
			}
			try {
				for (int ii = 0; ii < levelData.length; ii++) {
					currentControl.setData(levelData[ii].getLevel(), levelData[ii].getUnit());
					super.assertEquals(
							currentPropertyMetadata,
							levelData[ii].getLevel(),
							currentControl.getData());
				}
				check = true;
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Control must support setFalse operation.", check);
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
