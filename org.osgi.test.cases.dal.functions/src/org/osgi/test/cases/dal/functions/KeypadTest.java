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

import org.osgi.service.dal.functions.Keypad;
import org.osgi.test.cases.dal.functions.step.FunctionsTestSteps;

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
