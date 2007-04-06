package org.osgi.impl.service.useradmin;

/*
 * $Id$
 *
 * OSGi UserAdmin Reference Implementation.
 *
 * OSGi Confidential.
 *
 * (c) Copyright Gatespace AB 2000-2001.
 *
 * This source code is owned by Gatespace AB (www.gatespace.com), and is 
 * being distributed to OSGi MEMBERS as MEMBER LICENSED MATERIALS under
 * the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 *
 * Convenient and safe access to the log service.
 *
 */
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class LogTracker extends ServiceTracker implements LogService {
	BundleContext	bc;

	public LogTracker(BundleContext bc) {
		super(bc, LogService.class.getName(), null);
		this.bc = bc;
		open();
	}

	public void error(String msg, Exception e) {
		log(LogService.LOG_ERROR, msg, e);
	}

	public void warning(String msg) {
		log(LogService.LOG_WARNING, msg, null);
	}

	public void info(String msg) {
		log(LogService.LOG_INFO, msg, null);
	}

	// Log implementation
	public void log(int level, String msg, Throwable e) {
		log(null, level, msg, e);
	}

	public void log(int level, String msg) {
		log(null, level, msg, null);
	}

	public void log(ServiceReference sr, int level, String msg) {
		log(sr, level, msg, null);
	}

	public void log(ServiceReference sr, int level, String msg, Throwable e) {
		LogService ls = (LogService) getService();
		if (ls != null) {
			if (sr == null)
				ls.log(level, msg, e);
			else
				ls.log(sr, level, msg, e);
		}
		else {
			System.err.println("[" + bc.getBundle().getLocation() + ":"
					+ ((sr == null) ? "" : (sr + ":")) + level + "] " + msg
					+ ((e == null) ? "" : e.toString()));
		}
	}
}
