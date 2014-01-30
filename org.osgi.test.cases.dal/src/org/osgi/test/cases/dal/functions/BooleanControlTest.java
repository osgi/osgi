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
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.dal.AbstractDeviceTest;

/**
 * Validates the <code>BooleanControl</code> functions.
 */
public final class BooleanControlTest extends AbstractDeviceTest {

	/**
	 * Checks {@link BooleanControl#setTrue()} operation functionality.
	 * 
	 * @throws IllegalStateException If the device function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetTrue() throws IllegalStateException, DeviceException {
		DeviceFunction[] booleanControls = null;
		try {
			booleanControls = super.getDeviceFunctions(BooleanControl.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < booleanControls.length; i++) {
			final BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			try {
				currentBooleanControl.setTrue();
				check = true;
				super.assertEquals(true, currentBooleanControl.getData());
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Control must support setTrue operation.", check);
	}

	/**
	 * Checks {@link BooleanControl#setFalse()} operation functionality.
	 * 
	 * @throws IllegalStateException If the device function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testSetFalse() throws IllegalStateException, DeviceException {
		DeviceFunction[] booleanControls = null;
		try {
			booleanControls = super.getDeviceFunctions(BooleanControl.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < booleanControls.length; i++) {
			final BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			try {
				currentBooleanControl.setFalse();
				check = true;
				super.assertEquals(false, currentBooleanControl.getData());
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Control must support setFalse operation.", check);
	}

	/**
	 * Checks {@link BooleanControl#reverse()} operation functionality.
	 * 
	 * @throws IllegalStateException If the device function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testReverse() throws IllegalStateException, DeviceException {
		DeviceFunction[] booleanControls = null;
		try {
			booleanControls = super.getDeviceFunctions(BooleanControl.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < booleanControls.length; i++) {
			final BooleanControl currentBooleanControl = (BooleanControl) booleanControls[i];
			BooleanData currentData = currentBooleanControl.getData();
			try {
				currentBooleanControl.reverse();
				check = true;
				super.assertEquals(currentData.getValue() ? false : true, currentBooleanControl.getData());
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Boolean Control must support reverse operation.", check);
	}

}
