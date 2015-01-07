/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

package org.osgi.service.dal;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * {@code DeviceException} is a special {@code IOException}, which is thrown to
 * indicate that there is a device operation fail. The error reason can be
 * located with {@link #getCode()} method. The cause is available with
 * {@link #getCause()}.
 */
public class DeviceException extends IOException {

	/** An exception code indicates that the error is unknown. */
	public static final int		UNKNOWN				= 0;

	/** An exception code indicates that there is an error in the communication. */
	public static final int		COMMUNICATION_ERROR	= 1;

	/**
	 * An exception code indicates that there is expired timeout without any
	 * processing.
	 */
	public static final int		TIMEOUT				= 2;

	/**
	 * An exception code indicates that the device is not initialized. The
	 * device status is {@link Device#STATUS_NOT_INITIALIZED} or
	 * {@link Device#STATUS_PROCESSING}.
	 */
	public static final int		NOT_INITIALIZED		= 3;

	/**
	 * An exception code indicates that the requested value is currently not
	 * available.
	 */
	public static final int		NO_DATA				= 4;

	private static final long	serialVersionUID	= -1876565249188512600L;
	private static final String	CAUSED_BY			= "Caused by: ";

	private final int			code;
	private final Throwable		cause;

	/**
	 * Construct a new device exception with {@code null} message. The cause is
	 * not initialized and the exception code is set to {@link #UNKNOWN}.
	 */
	public DeviceException() {
		this(null, null, UNKNOWN);
	}

	/**
	 * Constructs a new device exception with the given message. The cause is
	 * not initialized and the exception code is set to {@link #UNKNOWN}.
	 * 
	 * @param message The exception message.
	 */
	public DeviceException(String message) {
		this(message, null, UNKNOWN);
	}

	/**
	 * Constructs a new device exception with the given message and cause. The
	 * exception code is set to {@link #UNKNOWN}.
	 * 
	 * @param message The exception message.
	 * @param cause The exception cause.
	 */
	public DeviceException(String message, Throwable cause) {
		this(message, cause, UNKNOWN);
	}

	/**
	 * Constructs a new device exception with the given message, cause and code.
	 * 
	 * @param message The exception message.
	 * @param cause The exception cause.
	 * @param code The exception code.
	 */
	public DeviceException(String message, Throwable cause, int code) {
		super(message);
		this.cause = cause;
		this.code = code;
	}

	/**
	 * Returns the exception code. It indicates the reason for this exception.
	 * The code can be:
	 * <ul>
	 * <li>{@link #UNKNOWN}</li>
	 * <li>{@link #COMMUNICATION_ERROR}</li>
	 * <li>{@link #TIMEOUT}</li>
	 * <li>{@link #NOT_INITIALIZED}</li>
	 * <li>{@link #NO_DATA}</li>
	 * <li>custom code</li>
	 * </ul>
	 * Zero and positive values are reserved for this definition and further
	 * extensions of the device exception codes. Custom codes can be used only
	 * as negative values to prevent potential collisions.
	 * 
	 * @return An exception code.
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Returns the cause for this exception or {@code null} if the cause is
	 * missing. The cause can be protocol specific exception with an appropriate
	 * message and error code.
	 * 
	 * @return An throwable cause.
	 */
	public Throwable getCause() {
		return this.cause;
	}

	/**
	 * Prints the exception stack trace to the standard error stream.
	 * 
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 * Prints the exception stack trace to the given stream.
	 * 
	 * @param s The stream used for the output.
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			super.printStackTrace(s);
			if (null != this.cause) {
				s.println(CAUSED_BY);
				this.cause.printStackTrace(s);
			}
		}
	}

	/**
	 * Prints the exception stack trace to the given writer.
	 * 
	 * @param s The writer used for the output.
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			super.printStackTrace(s);
			if (null != this.cause) {
				s.println(CAUSED_BY);
				this.cause.printStackTrace(s);
			}
		}
	}
}
