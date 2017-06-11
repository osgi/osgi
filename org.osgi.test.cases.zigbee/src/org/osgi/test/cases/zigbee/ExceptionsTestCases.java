/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.test.cases.zigbee;

import org.osgi.service.zigbee.APSException;
import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Tests the following exceptions classes;
 * 
 * <ul>
 * <li>{@link ZigBeeException}
 * <li>{@link ZCLException}
 * <li>{@link ZDPException}
 * <li>{@link APSException}
 * </ul>
 */
public final class ExceptionsTestCases extends DefaultTestBundleControl {

	private static final String message = "This is an error description.";

	/**
	 * Tests the {@link ZigBeeException} class.
	 */
	public void testZigBeeException() {

		String prefix = "ZigBeeException: check on 1 parameter constructor: ";

		ZigBeeException exception = new ZigBeeException(message);

		assertEquals(prefix + "ZigBeeException.getErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getErrorCode());
		assertEquals(prefix + "ZigBeeException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZigBeeException.getMessage()", message, exception.getMessage());
		assertEquals(prefix + "ZigBeeException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());

		prefix = "ZigBeeException: check on 2 parameters constructor: ";

		exception = new ZigBeeException(50, message);

		assertEquals(prefix + "ZigBeeException.getErrorCode()", 50, exception.getErrorCode());
		assertEquals(prefix + "ZigBeeException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZigBeeException.getMessage()", message, exception.getMessage());
		assertEquals(prefix + "ZigBeeException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());

		prefix = "ZigBeeException: check on the 3 parameters constructor: ";

		exception = new ZigBeeException(51, 60, message);

		assertEquals(prefix + "ZigBeeException.getErrorCode()", 51, exception.getErrorCode());
		assertEquals(prefix + "ZigBeeException.getZigBeeErrorCode()", 60, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZigBeeException.getMessage()", message, exception.getMessage());
		assertEquals(prefix + "ZigBeeException.hasZigBeeErrorCode()", true, exception.hasZigBeeErrorCode());
	}

	/**
	 * Tests the {@link ZCLException} class.
	 */
	public void testZCLException() {

		String prefix = "ZCLException: check on 1 parameter constructor: ";

		ZCLException exception = new ZCLException(message);

		assertEquals(prefix + "ZCLException.getErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getErrorCode());
		assertEquals(prefix + "ZCLException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZCLException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "ZCLException.getMessage()", message, exception.getMessage());

		prefix = "ZigBeeException: check on 2 parameters constructor: ";

		exception = new ZCLException(50, message);

		assertEquals(prefix + "ZCLException.getErrorCode()", 50, exception.getErrorCode());
		assertEquals(prefix + "ZCLException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZCLException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "ZCLException.getMessage()", message, exception.getMessage());

		prefix = "ZigBeeException: check on the 3 parameters constructor: ";

		exception = new ZCLException(51, 60, message);

		assertEquals(prefix + "ZCLException.getErrorCode()", 51, exception.getErrorCode());
		assertEquals(prefix + "ZCLException.getZigBeeErrorCode()", 60, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZCLException.hasZigBeeErrorCode()", true, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "ZCLException.getMessage()", message, exception.getMessage());
	}

	/**
	 * Tests the {@link ZDPException} class.
	 */
	public void testZDPException() {

		String prefix = "ZDPException: check on 1 parameter constructor: ";

		ZDPException exception = new ZDPException(message);

		assertEquals(prefix + "ZDPException.getErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getErrorCode());
		assertEquals(prefix + "ZDPException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZDPException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "ZDPException.getMessage()", message, exception.getMessage());

		prefix = "ZigBeeException: check on 2 parameters constructor: ";

		exception = new ZDPException(50, message);

		assertEquals(prefix + "ZDPException.getErrorCode()", 50, exception.getErrorCode());
		assertEquals(prefix + "ZDPException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZDPException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "ZDPException.getMessage()", message, exception.getMessage());

		prefix = "ZigBeeException: check on the 3 parameters constructor: ";

		exception = new ZDPException(51, 60, message);

		assertEquals(prefix + "ZDPException.getErrorCode()", 51, exception.getErrorCode());
		assertEquals(prefix + "ZDPException.getZigBeeErrorCode()", 60, exception.getZigBeeErrorCode());
		assertEquals(prefix + "ZDPException.hasZigBeeErrorCode()", true, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "ZDPException.getMessage()", message, exception.getMessage());
	}

	/**
	 * Tests the {@link APSException} class.
	 */
	public void testAPSException() {

		String prefix = "APSException: check on 1 parameter constructor: ";

		APSException exception = new APSException(message);

		assertEquals(prefix + "APSException.getErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getErrorCode());
		assertEquals(prefix + "APSException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "APSException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "APSException.getMessage()", message, exception.getMessage());

		prefix = "ZigBeeException: check on 2 parameters constructor: ";

		exception = new APSException(50, message);

		assertEquals(prefix + "APSException.getErrorCode()", 50, exception.getErrorCode());
		assertEquals(prefix + "APSException.getZigBeeErrorCode()", ZigBeeException.UNKNOWN_ERROR, exception.getZigBeeErrorCode());
		assertEquals(prefix + "APSException.hasZigBeeErrorCode()", false, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "APSException.getMessage()", message, exception.getMessage());

		prefix = "ZigBeeException: check on the 3 parameters constructor: ";

		exception = new APSException(51, 60, message);

		assertEquals(prefix + "APSException.getErrorCode()", 51, exception.getErrorCode());
		assertEquals(prefix + "APSException.getZigBeeErrorCode()", 60, exception.getZigBeeErrorCode());
		assertEquals(prefix + "APSException.hasZigBeeErrorCode()", true, exception.hasZigBeeErrorCode());
		assertEquals(prefix + "APSException.getMessage()", message, exception.getMessage());
	}
}
