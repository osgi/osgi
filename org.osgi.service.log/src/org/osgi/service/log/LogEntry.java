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
public abstract interface LogEntry {
	/**
	 * Returns the bundle that created this <code>LogEntry</code> object.
	 * 
	 * @return The bundle that created this <code>LogEntry</code> object;
	 *         <code>null</code> if no bundle is associated with this
	 *         <code>LogEntry</code> object.
	 */
	public abstract Bundle getBundle();

	/**
	 * Returns the <code>ServiceReference</code> object for the service associated
	 * with this <code>LogEntry</code> object.
	 * 
	 * @return <code>ServiceReference</code> object for the service associated
	 *         with this <code>LogEntry</code> object; <code>null</code> if no
	 *         <code>ServiceReference</code> object was provided.
	 */
	public abstract ServiceReference getServiceReference();

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
	public abstract int getLevel();

	/**
	 * Returns the human readable message associated with this <code>LogEntry</code>
	 * object.
	 * 
	 * @return <code>String</code> containing the message associated with this
	 *         <code>LogEntry</code> object.
	 */
	public abstract String getMessage();

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
	public abstract Throwable getException();

	/**
	 * Returns the value of <code>currentTimeMillis()</code> at the time this
	 * <code>LogEntry</code> object was created.
	 * 
	 * @return The system time in milliseconds when this <code>LogEntry</code>
	 *         object was created.
	 * @see "System.currentTimeMillis()"
	 */
	public abstract long getTime();
}
