/*
 * $Header$
 *
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
package org.osgi.impl.service.event.mapper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @version $Revision$
 */
public class LogReaderServiceTracker extends ServiceTracker {
	private final LogReaderServiceListener	listener;

	public LogReaderServiceTracker(BundleContext context,
			LogReaderServiceListener listener) {
		super(context, LogReaderService.class.getName(), null);
		this.listener = listener;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		listener.logReaderServiceModified(reference, service);
	}

	public void removedService(ServiceReference reference, Object service) {
		listener.logReaderServiceModified(reference, service);
		super.removedService(reference, service);
		//this method calls ungetService()
	}
}