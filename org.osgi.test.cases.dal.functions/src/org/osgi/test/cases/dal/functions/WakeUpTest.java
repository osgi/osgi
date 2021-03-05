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

import java.math.BigDecimal;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.WakeUp;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.functions.step.FunctionsTestSteps;

/**
 * Validates the {@code WakeUp} functions.
 */
public final class WakeUpTest extends AbstractFunctionTest {

	private static final long	WAKE_UP_INTERVAL	= Long.getLong(
															"org.osgi.test.cases.dal.wakeUpInterval", 2000).longValue();

	/**
	 * Validates the wake up interval support.
	 * 
	 * @throws DeviceException If operation error is available.
	 */
	public void testWakeUpInterval() throws DeviceException {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_WAKE_UP,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_WAKE_UP);
		Function[] wakeUpFunctions = super.getFunctions(WakeUp.class.getName());
		FunctionEventHandler eventHandler = new FunctionEventHandler(super.getContext());
		boolean check = false;
		for (int i = 0; i < wakeUpFunctions.length; i++) {
			WakeUp currentFunction = (WakeUp) wakeUpFunctions[i];
			eventHandler.register(
					(String) currentFunction.getServiceProperty(Function.SERVICE_UID),
					WakeUp.PROPERTY_AWAKE);
			try {
				currentFunction.setWakeUpInterval(
						new BigDecimal(getWakeUpInterval(currentFunction)), null);
				check = true;
				FunctionEvent wakeUpEvent = eventHandler.getEvents(1)[0];
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
			} finally {
				eventHandler.unregister();
			}
		}
		assertTrue("At least one Wake Up function must support setWakeUpInterval with ms.", check);
	}

	private static long getWakeUpInterval(WakeUp wakeUp) {
		PropertyMetadata wakeUpIntervalMeta = wakeUp.getPropertyMetadata(WakeUp.PROPERTY_WAKE_UP_INTERVAL);
		if (null != wakeUpIntervalMeta) {
			LevelData minWakeUpInterval = (LevelData) wakeUpIntervalMeta.getMinValue(null);
			if (null != minWakeUpInterval) {
				long minWakeUpIntervalLong = minWakeUpInterval.getLevel().longValue();
				if (minWakeUpIntervalLong > 0) {
					return minWakeUpIntervalLong;
				}
			}
		}
		return WAKE_UP_INTERVAL;
	}
}
