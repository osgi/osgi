/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions;

import org.osgi.service.dal.functions.Keypad;

/**
 * Validates the {@code Keypad} functions.
 */
public final class KeypadTest extends AbstractFunctionTest {

	/**
	 * Checks {@code Keypad} function events.
	 */
	public void testPropertyEvent() {
		super.testStepProxy.execute(
				FunctionsTestSteps.STEP_ID_AVAILABLE_KEYPAD,
				FunctionsTestSteps.STEP_MESSAGE_AVAILABLE_KEYPAD);
		super.checkPropertyEvent(
				Keypad.class.getName(),
				Keypad.PROPERTY_KEY,
				FunctionsTestSteps.STEP_ID_EVENT_KEYPAD,
				FunctionsTestSteps.STEP_MESSAGE_EVENT_KEYPAD);
	}
}
