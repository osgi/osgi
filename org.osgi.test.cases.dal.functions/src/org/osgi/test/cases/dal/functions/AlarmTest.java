/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions;

import org.osgi.service.dal.functions.Alarm;

/**
 * Validates the {@code Alarm} functions.
 */
public final class AlarmTest extends AbstractFunctionTest {

	/**
	 * Checks {@code Alarm} function events.
	 */
	public void testPropertyEvent() {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_ALARM,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_ALARM);
		super.checkPropertyEvent(
				Alarm.class.getName(),
				Alarm.PROPERTY_ALARM,
				FunctionsTestSteps.STEP_ID_EVENT_ALARM,
				FunctionsTestSteps.STEP_MESSAGE_EVENT_ALARM);
	}

}
