/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
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
 * Provides methods to retrieve <tt>LogEntry</tt> objects from the log.
 * <p>There are two ways to retrieve <tt>LogEntry</tt> objects:
 * <ul>
 * <li>The primary way to retrieve <tt>LogEntry</tt> objects is to register a
 * <tt>LogListener</tt> object whose <tt>LogListener.logged</tt>
 * method will be called for each entry added to the log.
 * <li>To retrieve past <tt>LogEntry</tt> objects, the <tt>getLog</tt> method can
 * be called which will return an <tt>Enumeration</tt> of all <tt>LogEntry</tt> objects in the log.
 *
 * @version $Revision$
 * @see LogEntry
 * @see LogListener
 * @see LogListener#logged(LogEntry)
 */
public abstract interface LogReaderService
{

    /**
     * Subscribes to <tt>LogEntry</tt> objects.
     *
     * <p>This method registers a <tt>LogListener</tt> object with the Log Reader Service.
     * The <tt>LogListener.logged(LogEntry)</tt>
     * method will be called for each <tt>LogEntry</tt> object placed into the log.
     *
     * <p>When a bundle which registers a <tt>LogListener</tt> object is stopped
     * or otherwise releases the Log Reader Service, the
     * Log Reader Service must remove all of the bundle's listeners.
     *
     * <p>If this Log Reader Service's list of listeners already contains a
     * listener <tt>l</tt> such that <tt>(l==listener)</tt>, this method does
     * nothing.
     *
     * @param listener A <tt>LogListener</tt> object to register; the
     * <tt>LogListener</tt> object is used to receive <tt>LogEntry</tt> objects.
     * @see LogListener
     * @see LogEntry
     * @see LogListener#logged(LogEntry)
     */
    public abstract void addLogListener(LogListener listener);

    /**
     * Unsubscribes to <tt>LogEntry</tt> objects.
     *
     * <p>This method unregisters a <tt>LogListener</tt> object from the Log Reader Service.
     *
     * <p>If <tt>listener</tt> is not contained in
     * this Log Reader Service's list of listeners,
     * this method does nothing.
     *
     * @param   listener A <tt>LogListener</tt> object to unregister.
     * @see LogListener
     */
    public abstract void removeLogListener(LogListener listener);

    /**
     * Returns an <tt>Enumeration</tt> of all <tt>LogEntry</tt> objects in the log.
     *
     * <p>Each element of the enumeration is a <tt>LogEntry</tt> object, ordered
     * with the most recent entry first. Whether the enumeration is of all <tt>LogEntry</tt>
     * objects since the Log Service was started or some recent past is implementation-specific.
     * Also implementation-specific is whether informational and debug
     * <tt>LogEntry</tt> objects are included in the enumeration.
     */
    public abstract Enumeration getLog();
}


