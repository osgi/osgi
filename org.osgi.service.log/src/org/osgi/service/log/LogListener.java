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

import java.util.EventListener;

/**
 * Subscribes to <code>LogEntry</code> objects from the <code>LogReaderService</code>.
 * 
 * <p>
 * A <code>LogListener</code> object may be registered with the Log Reader Service
 * using the <code>LogReaderService.addLogListener</code> method. After the
 * listener is registered, the <code>logged</code> method will be called for each
 * <code>LogEntry</code> object created. The <code>LogListener</code> object may be
 * unregistered by calling the <code>LogReaderService.removeLogListener</code>
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
	 * @param entry A <code>LogEntry</code> object containing log information.
	 * @see LogEntry
	 */
	public abstract void logged(LogEntry entry);
}
