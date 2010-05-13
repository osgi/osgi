/*
 * $Id$
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

/**
 * LogService class.
 * 
 * @author BJ Hargrave (hargrave@us.ibm.com)
 * @version $Id$
 */
public class LogServiceImpl implements LogService {
	protected Log		log;
	protected Bundle	bundle;

	protected LogServiceImpl(Log log, Bundle bundle) {
		this.log = log;
		this.bundle = bundle;
	}

	protected void close() {
		log = null;
		bundle = null;
	}

	/*
	 * ----------------------------------------------------------------------
	 * LogService Interface implementation
	 * ----------------------------------------------------------------------
	 */
	public void log(int level, String message) {
		Log log = this.log;
		if (log == null) {
			return;
		}
		log.log(level, message, bundle, null, null);
	}

	public void log(int level, String message, Throwable exception) {
		Log log = this.log;
		if (log == null) {
			return;
		}
		log.log(level, message, bundle, null, exception);
	}

	public void log(ServiceReference reference, int level, String message) {
		Log log = this.log;
		if (log == null) {
			return;
		}
		log.log(level, message, bundle, reference, null);
	}

	public void log(ServiceReference reference, int level, String message,
			Throwable exception) {
		Log log = this.log;
		if (log == null) {
			return;
		}
		log.log(level, message, bundle, reference, exception);
	}
}
