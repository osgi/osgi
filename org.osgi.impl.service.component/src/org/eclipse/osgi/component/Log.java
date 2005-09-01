/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Log {
	static private ServiceTracker logTracker;
	
	private Log() {};
	
	static void init(BundleContext bc) {
		logTracker = new ServiceTracker(bc,LogService.class.getName(),null);
		logTracker.open();
	}
	
	static void dispose() {
		if (logTracker != null) {
			logTracker.close();
		}
		logTracker = null;
	}
	
	public static void log(int level, String message) {
		log(level, message, null);
	}

	public static void log(int level, String message, Throwable e) {
		LogService logService = (LogService)logTracker.getService();
		if (logService != null) {
			logService.log(level,message,e);
		}
	}



}