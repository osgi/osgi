/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
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

import java.util.EventListener;

/**
 * Subscribes to <tt>LogEntry</tt> objects from the <tt>LogReaderService</tt>.
 * 
 * <p>
 * A <tt>LogListener</tt> object may be registered with the Log Reader Service
 * using the <tt>LogReaderService.addLogListener</tt> method. After the
 * listener is registered, the <tt>logged</tt> method will be called for each
 * <tt>LogEntry</tt> object created. The <tt>LogListener</tt> object may be
 * unregistered by calling the <tt>LogReaderService.removeLogListener</tt>
 * method.
 * 
 * @version $Revision$
 * @see LogReaderService
 * @see LogEntry
 * @see LogReaderService#addLogListener(LogListener)
 * @see LogReaderService#removeLogListener(LogListener)
 */
public abstract interface LogListener extends EventListener {
	/**
	 * Listener method called for each LogEntry object created.
	 * 
	 * <p>
	 * As with all event listeners, this method should return to its caller as
	 * soon as possible.
	 * 
	 * @param entry A <tt>LogEntry</tt> object containing log information.
	 * @see LogEntry
	 */
	public abstract void logged(LogEntry entry);
}
