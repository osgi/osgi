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
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * Validates the <code>Meter</code> functions.
 */
public final class MeterTest extends AbstractFunctionTest {

	/**
	 * Tests the total meter consumption.
	 * 
	 * @throws DeviceException If operation error is available.
	 * @throws IllegalStateException If the function is removed.
	 */
	public void testTotal() throws IllegalStateException, DeviceException {
		Function[] meters = null;
		try {
			meters = super.getFunctions(Meter.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < meters.length; i++) {
			final Meter currentMeter = (Meter) meters[i];
			try {
				LevelData total = currentMeter.getTotal();
				check = true;
				assertNotNull("No total metering info.", total);
				super.assertEquals(
						currentMeter.getPropertyMetadata(Meter.PROPERTY_TOTAL),
						total.getLevel(), total);
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Meter must support getTotal.", check);
	}

	/**
	 * Tests the current meter consumption.
	 * 
	 * @throws DeviceException If operation error is available.
	 * @throws IllegalStateException If the function is removed.
	 */
	public void testCurrent() throws IllegalStateException, DeviceException {
		Function[] meters = null;
		try {
			meters = super.getFunctions(Meter.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < meters.length; i++) {
			final Meter currentMeter = (Meter) meters[i];
			try {
				LevelData current = currentMeter.getCurrent();
				check = true;
				assertNotNull("No total metering info.", current);
				super.assertEquals(
						currentMeter.getPropertyMetadata(Meter.PROPERTY_CURRENT),
						current.getLevel(), current);
				assertTrue(
						"The current value is greater than the total value.",
						current.getLevel().compareTo(currentMeter.getTotal().getLevel()) <= 0);
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Meter must support getCurrent.", check);
	}

	/**
	 * Tests reset total operation.
	 * 
	 * @throws DeviceException If operation error is available.
	 * @throws IllegalStateException If the function is removed.
	 */
	public void testResetTotal() throws IllegalStateException, DeviceException {
		Function[] meters = null;
		try {
			meters = super.getFunctions(Meter.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		boolean check = false;
		for (int i = 0; i < meters.length; i++) {
			final Meter currentMeter = (Meter) meters[i];
			try {
				currentMeter.resetTotal();
				check = true;
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			}
		}
		assertTrue("At least one Meter must support resetTotal.", check);
	}

}
