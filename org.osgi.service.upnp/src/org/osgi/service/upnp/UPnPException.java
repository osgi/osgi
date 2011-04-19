/*
 * Copyright (c) OSGi Alliance (2005, 2010). All Rights Reserved.
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
package org.osgi.service.upnp;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * There are several defined error situations describing UPnP problems while a
 * control point invokes actions to UPnPDevices.
 * 
 * @since 1.1
 * @version $Id$
 */
public class UPnPException extends Exception {

	static final long			serialVersionUID		= -262013318122195146L;

	/**
	 * No Action found by that name at this service.
	 */
	public final static int		INVALID_ACTION			= 401;

	/**
	 * Not enough arguments, too many arguments with a specific name, or one of
	 * more of the arguments are of the wrong type.
	 */
	public final static int		INVALID_ARGS			= 402;

	/**
	 * The different end-points are no longer in synchronization.
	 */
	public final static int		INVALID_SEQUENCE_NUMBER	= 403;

	/**
	 * Refers to a non existing variable.
	 */
	public final static int		INVALID_VARIABLE		= 404;

	/**
	 * The invoked action failed during execution.
	 */
	public final static int		DEVICE_INTERNAL_ERROR	= 501;

	private static final String	CAUSED_BY				= "Caused by: ";

	/**
	 * Key for an error information that is an int type variable and that is
	 * used to identify occurred errors.
	 */
	private final int			errorCode;

	/**
	 * The throwable that caused this UPnP exception. <code>null</code>
	 * indicates that the throwable cause is unknown.
	 */
	private Throwable			errorCause;

	/**
	 * This constructor creates a <code>UPnPException</code> on the specified
	 * error code and error description.
	 * 
	 * @param errorCode error code which defined by UPnP Device Architecture
	 *        V1.0.
	 * @param errorDesc error description which explain the type of problem.
	 */
	public UPnPException(int errorCode, String errorDesc) {
		this(errorCode, errorDesc, null);
	}

	/**
	 * This constructor creates a <code>UPnPException</code> on the specified
	 * error code, error description and error cause.
	 * 
	 * @param errorCode error code which defined by UPnP Device Architecture
	 *        V1.0.
	 * @param errorDescription error description which explain the type of the
	 *        problem.
	 * @param errorCause cause of that <code>UPnPException</code>.
	 * 
	 * @since 1.2
	 */
	public UPnPException(int errorCode, String errorDescription,
			Throwable errorCause) {
		super(errorDescription);
		this.errorCode = errorCode;
		this.errorCause = errorCause;
	}

	/**
	 * Returns the UPnP Error Code occurred by UPnPDevices during invocation.
	 * 
	 * @return The UPnPErrorCode defined by a UPnP Forum working committee or
	 *         specified by a UPnP vendor.
	 * 
	 * @since 1.2
	 */
	public int getUPnPErrorCode() {
		return getUPnPError_Code();
	}

	/**
	 * Returns the error cause of that throwable. <code>null</code> value
	 * indicates that the cause throwable is unknown.
	 * 
	 * @return The cause of that exception or <code>null</code> if the throwable
	 *         cause is unknown.
	 * 
	 * @since 1.2
	 */
	public Throwable getCause() {
		synchronized (this) {
			return errorCause;
		}
	}

	/**
	 * Prints the exception stack trace to the "standard" error output stream.
	 * 
	 * @since 1.2
	 * 
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() {
		this.printStackTrace(System.err);
	}

	/**
	 * Prints the exception stack trace to the specified print stream.
	 * 
	 * @param s the print stream used for output.
	 * 
	 * @since 1.2
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(PrintStream ps) {
		synchronized (this) {
			Throwable errorCauseLocal = errorCause;
			errorCause = null; // prevent duplicated messages
			synchronized (ps) {
				super.printStackTrace(ps);
				errorCause = errorCauseLocal; // restore the error cause
				if (null != errorCause) {
					ps.print(CAUSED_BY);
					errorCause.printStackTrace(ps);
				}
			}
		}
	}

	/**
	 * Prints the exception stack trace to the specified print writer.
	 * 
	 * @param s the print writer used for output.
	 * 
	 * @since 1.2
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(PrintWriter pw) {
		synchronized (this) {
			Throwable errorCauseLocal = errorCause;
			errorCause = null; // prevent duplicated messages
			synchronized (pw) {
				super.printStackTrace(pw);
				errorCause = errorCauseLocal; // restore the error cause
				if (null != errorCause) {
					pw.print(CAUSED_BY);
					errorCause.printStackTrace(pw);
				}
			}
		}
	}

	/**
	 * Returns the UPnPError Code occurred by UPnPDevices during invocation.
	 * 
	 * @return The UPnPErrorCode defined by a UPnP Forum working committee or
	 *         specified by a UPnP vendor.
	 * @deprecated As of version 1.2, replaced by {@link #getUPnPErrorCode()}
	 */
	public int getUPnPError_Code() {
		return errorCode;
	}
}
