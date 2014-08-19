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
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * Validates the {@code Meter} functions.
 */
public final class MeterTest extends AbstractFunctionTest {

	/**
	 * Tests the total meter consumption.
	 * 
	 * @throws DeviceException If operation error is available.
	 * @throws IllegalStateException If the function is removed.
	 */
	public void testTotal() throws IllegalStateException, DeviceException {
		Function[] meters = super.getFunctions(Meter.class.getName());
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
		Function[] meters = super.getFunctions(Meter.class.getName());
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
		Function[] meters = super.getFunctions(Meter.class.getName());
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

	/**
	 * Checks {@code Meter} current consumption events.
	 */
	public void testCurrentPropertyEvent() {
		super.checkPropertyEvent(Meter.class.getName(), Meter.PROPERTY_CURRENT);
	}

	/**
	 * Checks {@code Meter} total consumption events.
	 */
	public void testTotalPropertyEvent() {
		super.checkPropertyEvent(Meter.class.getName(), Meter.PROPERTY_TOTAL);
	}

}
