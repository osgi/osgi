/*
 * $Id$
 *
 * OSGi Log Service Reference Implementation.
 *
 * OSGi Confidential.
 *
 * (C) Copyright IBM Corp. 2000-2001.
 *
 * This source code is owned by IBM Corporation, and is being
 * distributed to OSGi MEMBERS as MEMBER LICENSED MATERIALS under
 * the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.log;

import org.osgi.framework.*;
import org.osgi.service.log.*;

/**
 * LogEntry class.
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @version $Id$
 */
public class LogEntryImpl implements LogEntry {
	protected int				level;
	protected String			message;
	protected Bundle			bundle;
	protected ServiceReference	reference;
	protected Throwable			exception;
	protected long				time;

	protected LogEntryImpl(int level, String message, Bundle bundle,
			ServiceReference reference, Throwable exception) {
		this.time = System.currentTimeMillis();
		this.level = level;
		this.message = message;
		this.bundle = bundle;
		this.reference = reference;
		this.exception = exception;
	}

	/** The bundle that created the message */
	public Bundle getBundle() {
		return (bundle);
	}

	/** The service that this message is associated with */
	public ServiceReference getServiceReference() {
		return (reference);
	}

	/**
	 * The severity of the log entry. Should be one of the four levels defined
	 * in LogService.
	 */
	public int getLevel() {
		return (level);
	}

	/** The human readable message describing the condition */
	public String getMessage() {
		return (message);
	}

	/** An exception that reflects the condition */
	public Throwable getException() {
		return (exception);
	}

	/** Time log entry was created */
	public long getTime() {
		return (time);
	}
}
