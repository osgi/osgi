/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
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
