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
package org.osgi.impl.service.megcontainer;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.service.event.EventConstants;

/**
 * The Bundle Activator of the MEG container
 */
public class Activator extends Object implements
		BundleActivator {
	private BundleContext		bc;
	private ServiceRegistration	serviceReg;
	private ServiceRegistration	serviceListener;
	private MEGContainerImpl	megContainerImpl;

	public Activator() {
		super();
	}

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		megContainerImpl = new MEGContainerImpl(bc, "MEG");
		Dictionary properties = new Hashtable();
		properties.put("application_type", "MEG");
		properties.put("bundle_id", new Long(bc.getBundle().getBundleId())
				.toString());
		//registering the services
		serviceReg = bc.registerService(
				"org.osgi.service.application.ApplicationContainer",
				megContainerImpl, properties);
		Hashtable serviceListenerProps = new Hashtable();
		serviceListenerProps.put(EventConstants.EVENT_TOPIC, "*");
		serviceListener = bc.registerService(
				"org.osgi.service.event.ChannelListener", megContainerImpl,
				serviceListenerProps);
		System.out.println("MEG container started successfully!");
	}

	public void stop(BundleContext bc) throws Exception {
		//unregistering the service
		megContainerImpl.unregisterAllApplications();
		serviceListener.unregister();
		serviceReg.unregister();
		this.bc = null;
		System.out.println("MEG container stopped successfully!");
	}
}
