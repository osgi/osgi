/*
 * $Id$
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
	private ServiceReference				reference;

	public LogReaderServiceTracker(BundleContext context,
			LogReaderServiceListener listener) {
		super(context, LogReaderService.class.getName(), null);
		this.listener = listener;
	}

	public Object addingService(ServiceReference reference) {
		Object object = super.addingService(reference);
		if ((object != null) && (this.reference == null)
				&& (object instanceof LogReaderService)) {
			this.reference = reference;
			listener.logReaderServiceAdding(reference,
					(LogReaderService) object);
		}
		return object;
	}

	public void removedService(ServiceReference reference, Object service) {
		if ((service != null) && (this.reference.equals(reference))
				&& (service instanceof LogReaderService)) {
			listener.logReaderServiceRemoved(reference,
					(LogReaderService) service);
			this.reference = null;
		}
		super.removedService(reference, service);
		//this method calls ungetService()
	}
}