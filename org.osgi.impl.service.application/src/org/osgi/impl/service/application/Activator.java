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
package org.osgi.impl.service.scheduler;

import org.osgi.framework.*;
import org.osgi.impl.service.application.SchedulerImpl;

/**
 * The Bundle Activator of the MEG container
 */
public class Activator extends Object implements
		BundleActivator {
	private BundleContext		bc;
	private ServiceRegistration	schedulerService;
	private SchedulerImpl		scheduler;

	public Activator() {
		super();
	}

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		scheduler = new SchedulerImpl(bc);
		schedulerService = bc.registerService(
				"org.osgi.service.application.Scheduler", scheduler,
				null);
		System.out.println("Scheduler service started successfully!");
	}

	public void stop(BundleContext bc) throws Exception {
		//unregistering the service
		scheduler.stop();
		schedulerService.unregister();
		this.bc = null;
		System.out.println("Scheduler service stopped successfully!");
	}
}
