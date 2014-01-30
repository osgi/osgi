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
import org.osgi.service.dal.DeviceFunction;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.dal.AbstractDeviceTest;

/**
 * Validates the <code>BooleanSensor</code> functions.
 */
public final class BooleanSensorTest extends AbstractDeviceTest {

	/**
	 * Checks {@link BooleanSensor#getData()} getter functionality.
	 * 
	 * @throws IllegalStateException If the device function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testGetData() throws IllegalStateException, DeviceException {
		DeviceFunction[] booleanSensors = null;
		try {
			booleanSensors = super.getDeviceFunctions(BooleanSensor.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < booleanSensors.length; i++) {
			final BooleanSensor currentBooleanSensor = (BooleanSensor) booleanSensors[i];
			try {
				BooleanData currentData = currentBooleanSensor.getData();
				check = true;
				assertNotNull("THe boolean data cannot be null!", currentData);
				super.assertEquals(currentData.getValue(), currentData);
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Sensor must support getData operation.", check);
	}

}
