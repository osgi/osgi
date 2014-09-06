/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

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
