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

import java.math.BigDecimal;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.DeviceFunction;
import org.osgi.service.dal.DeviceFunctionEvent;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.Units;
import org.osgi.service.dal.functions.WakeUp;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.AbstractDeviceTest;
import org.osgi.test.cases.dal.DeviceFunctionEventHandler;

/**
 * Validates the <code>WakeUp</code> functions.
 */
public final class WakeUpTest extends AbstractDeviceTest {

	private static final long	WAKE_UP_INTERVAL	= Long.getLong(
															"org.osgi.test.cases.dal.wakeUpInterval", 2000).longValue();

	private static final String	MILLIS	= Units.PREFIX_MILLI + Units.SECOND;

	/**
	 * Validates the wake up interval support.
	 * 
	 * @throws IllegalStateException If the device function is removed.
	 * @throws DeviceException If operation error is available.
	 */
	public void testWakeUpInterval() throws IllegalStateException, DeviceException {
		DeviceFunction[] wakeUpFunctions = null;
		try {
			wakeUpFunctions = super.getDeviceFunctions(WakeUp.class.getName(), null, null);
		} catch (InvalidSyntaxException e) {
			// not possible
			fail(null, e);
		}
		final DeviceFunctionEventHandler eventHandler = new DeviceFunctionEventHandler(super.getContext());
		boolean check = false;
		for (int i = 0; i < wakeUpFunctions.length; i++) {
			final WakeUp currentFunction = (WakeUp) wakeUpFunctions[i];
			eventHandler.register(
					(String) currentFunction.getServiceProperty(DeviceFunction.SERVICE_UID));
			try {
				currentFunction.setWakeUpInterval(
						new BigDecimal(getWakeUpInterval(currentFunction)), MILLIS);
				check = true;
				DeviceFunctionEvent wakeUpEvent = eventHandler.getEvents(1)[0];
				assertEquals(
						"The event property is not correct.",
						WakeUp.PROPERTY_AWAKE,
						wakeUpEvent.getFunctionPropertyName());
				assertTrue(
						"The event value type must be BooleanData.",
						wakeUpEvent.getFunctionPropertyValue() instanceof BooleanData);
				assertTrue(
						"The event value is not correct.",
						((BooleanData) wakeUpEvent.getFunctionPropertyValue()).getValue());
				currentFunction.sleep();
			} catch (IllegalArgumentException iae) {
				// try another function, unit is not supported
			} catch (UnsupportedOperationException uoe) {
				// expected, go ahead
			} finally {
				eventHandler.unregister();
			}
		}
		assertTrue("At least one Wake Up function must support setWakeUpInterval with ms.", check);
	}

	private static long getWakeUpInterval(WakeUp wakeUp) {
		final PropertyMetadata wakeUpIntervalMeta = wakeUp.getPropertyMetadata(WakeUp.PROPERTY_WAKE_UP_INTERVAL);
		if (null != wakeUpIntervalMeta) {
			try {
				LevelData minWakeUpInterval = (LevelData) wakeUpIntervalMeta.getMinValue(MILLIS);
				if (null != minWakeUpInterval) {
					long minWakeUpIntervalLong = minWakeUpInterval.getLevel().longValue();
					if (minWakeUpIntervalLong > 0) {
						return minWakeUpIntervalLong;
					}
				}
			} catch (IllegalArgumentException iae) {
				// the unit is not supported, go ahead
			}
		}
		return WAKE_UP_INTERVAL;
	}

}
