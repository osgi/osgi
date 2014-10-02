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
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * Validates the {@code MultiLevelControl} functions.
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
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_MLC,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_MLC);
		Function[] multiLevelControls = super.getFunctions(MultiLevelControl.class.getName());
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
