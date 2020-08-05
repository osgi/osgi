/*
 * OSGi UserAdmin Reference Implementation.
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
package org.osgi.impl.service.useradmin;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class LogTracker extends ServiceTracker<LogService,LogService>
		implements LogService {
	BundleContext	bc;

	public LogTracker(BundleContext bc) {
		super(bc, LogService.class, null);
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
	@Override
	public void log(int level, String msg, Throwable e) {
		log(null, level, msg, e);
	}

	@Override
	public void log(int level, String msg) {
		log(null, level, msg, null);
	}

	@Override
	public void log(@SuppressWarnings("rawtypes") ServiceReference sr,
			int level, String msg) {
		log(sr, level, msg, null);
	}

	@Override
	public void log(@SuppressWarnings("rawtypes") ServiceReference sr,
			int level, String msg, Throwable e) {
		LogService ls = getService();
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
