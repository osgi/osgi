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

package org.osgi.test.cases.dal.secure.step;

/**
 * Contains all test steps used by this test case.
 */
public final class SecureDeviceTestSteps {

	private SecureDeviceTestSteps() {
		/* prevent object instantiation */
	}

	/**
	 * Step identifier guarantees that at least one device will be available in
	 * the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_DEVICE		= "dal.secure.available.device";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_DEVICE} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_DEVICE	= "At least one device should be available in the registry.";
}
