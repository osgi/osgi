/*
 * ============================================================================
 *  Copyright (c) IBM Corporation (2005)
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
package org.osgi.impl.service.event;

import org.osgi.framework.*;
import org.osgi.impl.service.event.mapper.EventRedeliverer;
import org.osgi.service.event.EventAdmin;

/**
 * The Bundle Activator class of the Event Admin service
 */
public class Activator implements BundleActivator {
	private BundleContext		bc;
	private ServiceRegistration	eventAdminServiceReg;
	private EventAdminImpl		eventAdminImpl;
	private EventRedeliverer	redeliverer;

	public Activator() {
		super();
	}

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		// check if EventAdmin is regiserted
		ServiceReference reference = bc.getServiceReference(EventAdmin.class
				.getName());
		if (reference != null)
			throw new BundleException("EventAdmin service already started!");
		eventAdminImpl = new EventAdminImpl(bc);
		//registering the EventAdmin service
		eventAdminServiceReg = bc.registerService(EventAdmin.class.getName(),
				eventAdminImpl, null);
		redeliverer = new EventRedeliverer(bc);
		redeliverer.open();
	}

	public void stop(BundleContext bc) throws Exception {
		redeliverer.close();
		redeliverer = null;
		eventAdminImpl.stop();
		eventAdminServiceReg.unregister();
		this.bc = null;
	}
}