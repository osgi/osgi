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

import java.util.Enumeration;

/**
 * Provides methods to retrieve <code>LogEntry</code> objects from the log.
 * <p>
 * There are two ways to retrieve <code>LogEntry</code> objects:
 * <ul>
 * <li>The primary way to retrieve <code>LogEntry</code> objects is to register a
 * <code>LogListener</code> object whose <code>LogListener.logged</code> method will
 * be called for each entry added to the log.
 * <li>To retrieve past <code>LogEntry</code> objects, the <code>getLog</code>
 * method can be called which will return an <code>Enumeration</code> of all
 * <code>LogEntry</code> objects in the log.
 * 
 * @version $Revision$
 * @see LogEntry
 * @see LogListener
 * @see LogListener#logged(LogEntry)
 */
public abstract interface LogReaderService {
	/**
	 * Subscribes to <code>LogEntry</code> objects.
	 * 
	 * <p>
	 * This method registers a <code>LogListener</code> object with the Log Reader
	 * Service. The <code>LogListener.logged(LogEntry)</code> method will be
	 * called for each <code>LogEntry</code> object placed into the log.
	 * 
	 * <p>
	 * When a bundle which registers a <code>LogListener</code> object is stopped
	 * or otherwise releases the Log Reader Service, the Log Reader Service must
	 * remove all of the bundle's listeners.
	 * 
	 * <p>
	 * If this Log Reader Service's list of listeners already contains a
	 * listener <code>l</code> such that <code>(l==listener)</code>, this method
	 * does nothing.
	 * 
	 * @param listener A <code>LogListener</code> object to register; the
	 *        <code>LogListener</code> object is used to receive <code>LogEntry</code>
	 *        objects.
	 * @see LogListener
	 * @see LogEntry
	 * @see LogListener#logged(LogEntry)
	 */
	public abstract void addLogListener(LogListener listener);

	/**
	 * Unsubscribes to <code>LogEntry</code> objects.
	 * 
	 * <p>
	 * This method unregisters a <code>LogListener</code> object from the Log
	 * Reader Service.
	 * 
	 * <p>
	 * If <code>listener</code> is not contained in this Log Reader Service's list
	 * of listeners, this method does nothing.
	 * 
	 * @param listener A <code>LogListener</code> object to unregister.
	 * @see LogListener
	 */
	public abstract void removeLogListener(LogListener listener);

	/**
	 * Returns an <code>Enumeration</code> of all <code>LogEntry</code> objects in
	 * the log.
	 * 
	 * <p>
	 * Each element of the enumeration is a <code>LogEntry</code> object, ordered
	 * with the most recent entry first. Whether the enumeration is of all
	 * <code>LogEntry</code> objects since the Log Service was started or some
	 * recent past is implementation-specific. Also implementation-specific is
	 * whether informational and debug <code>LogEntry</code> objects are included
	 * in the enumeration.
	 */
	public abstract Enumeration getLog();
}
