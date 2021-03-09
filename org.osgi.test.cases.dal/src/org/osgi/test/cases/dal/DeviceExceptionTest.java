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

package org.osgi.test.cases.dal;

import java.io.IOException;
import org.osgi.service.dal.DeviceException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Validates {@link DeviceException} class.
 */
public final class DeviceExceptionTest extends DefaultTestBundleControl {

	/**
	 * Tests the exception default constructor.
	 */
	public void testEmptyException() {
		DeviceException de = new DeviceException();
		assertNull("No exception message is expected.", de.getMessage());
		assertEquals("Unknown exception code is expected.",
				DeviceException.UNKNOWN, de.getCode());
		assertNull("No exception cause is expected.", de.getCause());
	}

	/**
	 * Tests the exception message.
	 */
	public void testExceptionMessage() {
		checkExceptionMessage("test exception message");
		checkExceptionMessage(null);
	}

	/**
	 * Tests the exception cause.
	 */
	public void testExceptionCause() {
		checkExceptionCause(new IOException());
		checkExceptionCause(null);
	}

	/**
	 * Tests the exception codes.
	 */
	public void testExceptionCodes() {
		int[] exceptionCodes = new int[] {
				DeviceException.COMMUNICATION_ERROR,
				DeviceException.NO_DATA,
				DeviceException.NOT_INITIALIZED,
				DeviceException.TIMEOUT,
				DeviceException.UNKNOWN,
				Integer.MIN_VALUE,
		};
		for (int i = 0; i < exceptionCodes.length; i++) {
			DeviceException de = new DeviceException(null, null, exceptionCodes[i]);
			assertEquals("The exception code is not correct.", exceptionCodes[i], de.getCode());
		}
	}

	private void checkExceptionMessage(String message) {
		DeviceException de = new DeviceException(message);
		assertEquals("The exception message is not correct.", message, de.getMessage());

		de = new DeviceException(message, null);
		assertEquals("The exception message is not correct.", message, de.getMessage());

		de = new DeviceException(message, null, DeviceException.UNKNOWN);
		assertEquals("The exception message is not correct.", message, de.getMessage());
	}

	private void checkExceptionCause(Throwable cause) {
		DeviceException de = new DeviceException(null, cause);
		assertSame("The exception cause is not correct.", cause, de.getCause());

		de = new DeviceException(null, cause, DeviceException.UNKNOWN);
		assertEquals("The exception cause is not correct.", cause, de.getCause());
	}
}
