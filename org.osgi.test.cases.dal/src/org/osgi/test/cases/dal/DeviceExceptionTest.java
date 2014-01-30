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


package org.osgi.test.cases.dal;

import java.io.IOException;
import org.osgi.service.dal.DeviceException;

/**
 * Validates {@link DeviceException} class.
 */
public final class DeviceExceptionTest extends AbstractDeviceTest {

	/**
	 * Tests the exception default constructor.
	 */
	public void testEmptyException() {
		DeviceException de = new DeviceException();
		assertNull("No exception message is expected.", de.getMessage());
		assertEquals("Unknown exception code is expected.",
				DeviceException.CODE_UNKNOWN, de.getCode());
		assertNull("No exception cause is expected.", de.getMessage());
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
				DeviceException.CODE_COMMUNICATION_ERROR,
				DeviceException.CODE_NO_DATA,
				DeviceException.CODE_NOT_INITIALIZED,
				DeviceException.CODE_TIMEOUT,
				DeviceException.CODE_UNKNOWN,
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

		de = new DeviceException(message, null, DeviceException.CODE_UNKNOWN);
		assertEquals("The exception message is not correct.", message, de.getMessage());
	}

	private void checkExceptionCause(Throwable cause) {
		DeviceException de = new DeviceException(null, cause);
		assertSame("The exception cause is not correct.", cause, de.getCause());

		de = new DeviceException(null, cause, DeviceException.CODE_UNKNOWN);
		assertEquals("The exception cause is not correct.", cause, de.getCause());
	}

}
