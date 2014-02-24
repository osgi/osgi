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
import org.osgi.service.dal.functions.MultiLevelSensor;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * Validates the <code>MultiLevelSensor</code> functions.
 */
public final class MultiLevelSensorTest extends AbstractFunctionTest {

	/**
	 * Checks {@link MultiLevelSensor#getData()} getter functionality.
	 * 
	 * @throws IllegalStateException If the function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testGetData() throws IllegalStateException, DeviceException {
		Function[] multiLevelSensors = null;
		try {
			multiLevelSensors = super.getFunctions(MultiLevelSensor.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
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

}
