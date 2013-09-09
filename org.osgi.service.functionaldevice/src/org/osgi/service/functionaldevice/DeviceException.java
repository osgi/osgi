/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.functionaldevice;

import java.io.IOException;

/**
 * <code>DeviceExcpetion</code> is a special <code>IOException</code>, which is
 * thrown to indicate that there is a device operation fail. The error reason
 * can be located with {@link #getCode()} method.
 */
public class DeviceException extends IOException {

	private static final long serialVersionUID = 1L;

	/** An exception code indicates that the error is unknown. */
	public static final int CODE_UNKNOWN = 1;

	/** An exception code indicates that there is an error in the communication. */
	public static final int CODE_COMMUNICATION_ERROR = 2;

	/**
	 * An exception code indicates that the response is not produced within a
	 * given timeout.
	 */
	public static final int CODE_TIMEOUT = 3;

	/**
	 * An exception code indicates that the device is not initialized. It
	 * indicates that the device status is {@link Device#STATUS_NOT_INITIALIZED}
	 * .
	 */
	public static final int CODE_DEVICE_NOT_INITIALIZED = 4;

	/**
	 * An exception code indicates that the requested value is currently not
	 * available.
	 */
	public static final int CODE_NO_DATA = 5;

	/**
	 * Returns the exception error code. It indicates the reason for this error.
	 * 
	 * @return An exception code.
	 */
	public int getCode() {
		return CODE_UNKNOWN; // TODO: impl
	}

	/**
	 * Returns the cause for this throwable or <code>null</code> if the cause is
	 * missing. The cause can be protocol specific exception with an appropriate
	 * message and error code.
	 * 
	 * @return An throwable cause.
	 */
	public Throwable getCause() {
		return null; // TODO: impl
	}

}
