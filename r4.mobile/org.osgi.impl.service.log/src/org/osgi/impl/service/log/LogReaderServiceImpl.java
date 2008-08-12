/*
 * $Header$
 *
 * OSGi Log Service Reference Implementation.
 *

 *
 * (C) Copyright IBM Corporation 2000-2001.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.log;

import org.osgi.framework.*;
import org.osgi.service.log.*;
import java.util.*;

/**
 * LogReaderService class.
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @version $Revision$
 */
public class LogReaderServiceImpl implements LogReaderService {
	protected Log		log;
	protected Bundle	bundle;
	protected Vector	listeners;

	protected LogReaderServiceImpl(Log log, Bundle bundle) {
		this.log = log;
		this.bundle = bundle;
		this.listeners = new Vector(10);
	}

	protected void close() {
		log = null;
		bundle = null;
		listeners = null;
	}

	/**
	 * Deliver the logentry to a listener.
	 */
	protected void deliverLogEntry(LogListener listener, LogEntry logentry) {
		Log log = this.log;
		if (log == null) {
			return;
		}
		listener.logged(logentry);
	}

	/**
	 * Subscribe to log events. The LogListener will get a callback each time a
	 * message is logged. The requester must have Admin permission.
	 */
	public void addLogListener(LogListener listener) {
		Log log = this.log;
		if (log == null) {
			return;
		}
		synchronized (log) {
			if (!listeners.contains(listener)) {
				listeners.addElement(listener);
			}
		}
	}

	/**
	 * Unsubscribe to log events. The requester must have Admin permission.
	 */
	public void removeLogListener(LogListener listener) {
		Log log = this.log;
		if (log == null) {
			return;
		}
		synchronized (log) {
			listeners.removeElement(listener);
		}
	}

	/**
	 * Returns an enumeration of the last log messages. Each element will be of
	 * type LogEntry. Whether the enumeration is of all the logs since bootup or
	 * the recent past is implementation specific. Also whether informational
	 * and debug entries are included in the logging interval is implementation
	 * specific. The requester must have Admin permission.
	 */
	public Enumeration getLog() {
		Log log = this.log;
		if (log == null) {
			return (new Vector(0).elements());
		}
		return (log.logEntries());
	}
}
