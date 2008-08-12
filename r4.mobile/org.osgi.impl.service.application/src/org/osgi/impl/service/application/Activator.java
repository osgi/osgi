/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors.
 * All rights are reserved.
 *
 * These materials have been contributed  to the Open Services Gateway
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject
 * to the terms of, the OSGi Member Agreement specifically including, but not
 * limited to, the license rights and warranty disclaimers as set forth in
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work.
 * All company, brand and product names contained within this document may be
 * trademarks that are the sole property of the respective owners.
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.application;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;

/**
 *
 * The Bundle Activator of the MEG container
 */
public class Activator extends Object implements
		BundleActivator {
	static  Scheduler					scheduler;
	static  BundleContext 		bc;
	private ApplicationPlugin appPlugin;

	public Activator() {
		super();
	}

	public void start(BundleContext bc) throws Exception {
		Activator.bc = bc;
		
		scheduler = new Scheduler(bc);
		
		appPlugin = new ApplicationPlugin();
		appPlugin.start( bc );
		
		System.out.println("Application service started successfully!");
	}

	public void stop(BundleContext bc) throws Exception {
		appPlugin.stop( bc );
		appPlugin = null;
		
		scheduler.stop();
		scheduler = null;
		
		Activator.bc = null;
		
		System.out.println("Application service stopped successfully!");
	}

	static boolean log( int severity, String message,	Throwable throwable) {
		System.out.println("Serverity:" + severity + " Message:" + message
				+ " Throwable:" + throwable);

		ServiceReference serviceRef = bc
				.getServiceReference("org.osgi.service.log.LogService");
		if (serviceRef != null) {
			LogService logService = (LogService) bc.getService(serviceRef);
			if (logService != null) {
				try {
					logService.log(severity, message, throwable);
					return true;
				}
				finally {
					bc.ungetService(serviceRef);
				}
			}
		}
		return false;
	}
}
