/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

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
