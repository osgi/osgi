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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * Provides methods to access the information contained in an individual Log
 * Service log entry.
 * 
 * <p>
 * A <code>LogEntry</code> object may be acquired from the
 * <code>LogReaderService.getLog</code> method or by registering a
 * <code>LogListener</code> object.
 * 
 * @version $Revision$
 * @see LogReaderService#getLog
 * @see LogListener
 */
public interface LogEntry {
	/**
	 * Returns the bundle that created this <code>LogEntry</code> object.
	 * 
	 * @return The bundle that created this <code>LogEntry</code> object;
	 *         <code>null</code> if no bundle is associated with this
	 *         <code>LogEntry</code> object.
	 */
	public Bundle getBundle();

	/**
	 * Returns the <code>ServiceReference</code> object for the service associated
	 * with this <code>LogEntry</code> object.
	 * 
	 * @return <code>ServiceReference</code> object for the service associated
	 *         with this <code>LogEntry</code> object; <code>null</code> if no
	 *         <code>ServiceReference</code> object was provided.
	 */
	public ServiceReference getServiceReference();

	/**
	 * Returns the severity level of this <code>LogEntry</code> object.
	 * 
	 * <p>
	 * This is one of the severity levels defined by the <code>LogService</code>
	 * interface.
	 * 
	 * @return Severity level of this <code>LogEntry</code> object.
	 * 
	 * @see LogService#LOG_ERROR
	 * @see LogService#LOG_WARNING
	 * @see LogService#LOG_INFO
	 * @see LogService#LOG_DEBUG
	 */
	public int getLevel();

	/**
	 * Returns the human readable message associated with this <code>LogEntry</code>
	 * object.
	 * 
	 * @return <code>String</code> containing the message associated with this
	 *         <code>LogEntry</code> object.
	 */
	public String getMessage();

	/**
	 * Returns the exception object associated with this <code>LogEntry</code>
	 * object.
	 * 
	 * <p>
	 * In some implementations, the returned exception may not be the original
	 * exception. To avoid references to a bundle defined exception class, thus
	 * preventing an uninstalled bundle from being garbage collected, the Log
	 * Service may return an exception object of an implementation defined
	 * Throwable subclass. The returned object will attempt to provide as much
	 * information as possible from the original exception object such as the
	 * message and stack trace.
	 * 
	 * @return <code>Throwable</code> object of the exception associated with this
	 *         <code>LogEntry</code>;<code>null</code> if no exception is
	 *         associated with this <code>LogEntry</code> object.
	 */
	public Throwable getException();

	/**
	 * Returns the value of <code>currentTimeMillis()</code> at the time this
	 * <code>LogEntry</code> object was created.
	 * 
	 * @return The system time in milliseconds when this <code>LogEntry</code>
	 *         object was created.
	 * @see "System.currentTimeMillis()"
	 */
	public long getTime();
}
