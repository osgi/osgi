/*
 * $Header$
 * 
 * Copyright (c) IBM Corporation (2005)
 * 
 * These materials have been contributed to the OSGi Alliance as "MEMBER
 * LICENSED MATERIALS" as defined in, and subject to the terms of, the OSGi
 * Member Agreement, specifically including but not limited to, the license
 * rights and warranty disclaimers as set forth in Sections 3.2 and 12.1
 * thereof, and the applicable Statement of Work.
 * 
 * All company, brand and product names contained within this document may be
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.event;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class LogProxy extends ServiceTracker implements LogService {
	private final String			header			= "EventAdmin_LogProxy: ";
	private final BundleContext		bc;
	private boolean					useLogService	= true;
	private static final boolean	DEBUG			= false;

	protected LogProxy(BundleContext bc) {
		super(bc, LogService.class.getName(), null);
		this.bc = bc;
		open();
	}

	private LogService getLogService() {
		return (LogService) getService();
	}

	public void log(int level, String message) {
		if ((!DEBUG) && (level != LogService.LOG_ERROR)) {
			return;
		}
		LogService logService = getLogService();
		if ((logService != null) && useLogService) {
			logService.log(level, message);
		}
		else {
			System.out.println(header + level2string(level) + " " + message);
		}
	}

	public void log(int level, String message, Throwable exception) {
		if ((!DEBUG) && (level != LogService.LOG_ERROR)) {
			return;
		}
		LogService logService = getLogService();
		if ((logService != null) && useLogService) {
			logService.log(level, message, exception);
		}
		else {
			System.out.println(header + level2string(level) + " " + message);
			System.out.println(header + "         exception = " + exception);
			exception.printStackTrace();
		}
	}

	public void log(ServiceReference sr, int level, String message) {
		if ((!DEBUG) && (level != LogService.LOG_ERROR)) {
			return;
		}
		LogService logService = getLogService();
		if ((logService != null) && useLogService) {
			logService.log(sr, level, message);
		}
		else {
			System.out.println(header + level2string(level) + " " + message);
			System.out.println(header + "    ServiceReference = " + sr);
		}
	}

	public void log(ServiceReference sr, int level, String message,
			Throwable exception) {
		if ((!DEBUG) && (level != LogService.LOG_ERROR)) {
			return;
		}
		LogService logService = getLogService();
		if ((logService != null) && useLogService) {
			logService.log(sr, level, message, exception);
		}
		else {
			System.out.println(header + level2string(level) + " " + message);
			System.out.println(header + "    ServiceReference = " + sr);
			System.out.println(header + "           exception = " + exception);
			exception.printStackTrace();
		}
	}

	private String level2string(int level) {
		switch (level) {
			case LogService.LOG_INFO :
				return "INFO";
			case LogService.LOG_DEBUG :
				return "DEBUG";
			case LogService.LOG_ERROR :
				return "ERROR";
			case LogService.LOG_WARNING :
				return "WARNING";
			default :
				return "";
		}
	}

	public void setUseLogService(boolean useLogService) {
		this.useLogService = useLogService;
	}
}