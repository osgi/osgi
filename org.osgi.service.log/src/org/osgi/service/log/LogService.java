/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.log;

import org.osgi.framework.ServiceReference;

/**
 * Provides methods for bundles to write messages to the log.
 * 
 * <p>
 * <code>LogService</code> methods are provided to log messages; optionally with a
 * <code>ServiceReference</code> object or an exception.
 * 
 * <p>
 * Bundles must log messages in the OSGi environment with a severity level
 * according to the following hierarchy:
 * <ol>
 * <li>{@link #LOG_ERROR}
 * <li>{@link #LOG_WARNING}
 * <li>{@link #LOG_INFO}
 * <li>{@link #LOG_DEBUG}
 * </ol>
 * 
 * @version $Revision$
 */
public abstract interface LogService {
	/**
	 * An error message (Value 1).
	 * 
	 * <p>
	 * This log entry indicates the bundle or service may not be functional.
	 */
	public static final int	LOG_ERROR	= 1;
	/**
	 * A warning message (Value 2).
	 * 
	 * <p>
	 * This log entry indicates a bundle or service is still functioning but may
	 * experience problems in the future because of the warning condition.
	 */
	public static final int	LOG_WARNING	= 2;
	/**
	 * An informational message (Value 3).
	 * 
	 * <p>
	 * This log entry may be the result of any change in the bundle or service
	 * and does not indicate a problem.
	 */
	public static final int	LOG_INFO	= 3;
	/**
	 * A debugging message (Value 4).
	 * 
	 * <p>
	 * This log entry is used for problem determination and may be irrelevant to
	 * anyone but the bundle developer.
	 */
	public static final int	LOG_DEBUG	= 4;

	/**
	 * Logs a message.
	 * 
	 * <p>
	 * The <code>ServiceReference</code> field and the <code>Throwable</code> field
	 * of the <code>LogEntry</code> object will be set to <code>null</code>.
	 * 
	 * @param level The severity of the message. This should be one of the
	 *        defined log levels but may be any integer that is interpreted in a
	 *        user defined way.
	 * @param message Human readable string describing the condition or
	 *        <code>null</code>.
	 * @see #LOG_ERROR
	 * @see #LOG_WARNING
	 * @see #LOG_INFO
	 * @see #LOG_DEBUG
	 */
	public abstract void log(int level, String message);

	/**
	 * Logs a message with an exception.
	 * 
	 * <p>
	 * The <code>ServiceReference</code> field of the <code>LogEntry</code> object
	 * will be set to <code>null</code>.
	 * 
	 * @param level The severity of the message. This should be one of the
	 *        defined log levels but may be any integer that is interpreted in a
	 *        user defined way.
	 * @param message The human readable string describing the condition or
	 *        <code>null</code>.
	 * @param exception The exception that reflects the condition or
	 *        <code>null</code>.
	 * @see #LOG_ERROR
	 * @see #LOG_WARNING
	 * @see #LOG_INFO
	 * @see #LOG_DEBUG
	 */
	public abstract void log(int level, String message, Throwable exception);

	/**
	 * Logs a message associated with a specific <code>ServiceReference</code>
	 * object.
	 * 
	 * <p>
	 * The <code>Throwable</code> field of the <code>LogEntry</code> will be set to
	 * <code>null</code>.
	 * 
	 * @param sr The <code>ServiceReference</code> object of the service that this
	 *        message is associated with or <code>null</code>.
	 * @param level The severity of the message. This should be one of the
	 *        defined log levels but may be any integer that is interpreted in a
	 *        user defined way.
	 * @param message Human readable string describing the condition or
	 *        <code>null</code>.
	 * @see #LOG_ERROR
	 * @see #LOG_WARNING
	 * @see #LOG_INFO
	 * @see #LOG_DEBUG
	 */
	public abstract void log(ServiceReference sr, int level, String message);

	/**
	 * Logs a message with an exception associated and a
	 * <code>ServiceReference</code> object.
	 * 
	 * @param sr The <code>ServiceReference</code> object of the service that this
	 *        message is associated with.
	 * @param level The severity of the message. This should be one of the
	 *        defined log levels but may be any integer that is interpreted in a
	 *        user defined way.
	 * @param message Human readable string describing the condition or
	 *        <code>null</code>.
	 * @param exception The exception that reflects the condition or
	 *        <code>null</code>.
	 * @see #LOG_ERROR
	 * @see #LOG_WARNING
	 * @see #LOG_INFO
	 * @see #LOG_DEBUG
	 */
	public abstract void log(ServiceReference sr, int level, String message,
			Throwable exception);
}
