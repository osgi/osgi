/* 
 * Copyright (c) 2008, 2009 Siemens Enterprise Communications GmbH & Co. KG, 
 * Germany. All rights reserved.
 *
 * Siemens Enterprise Communications GmbH & Co. KG is a Trademark Licensee 
 * of Siemens AG.
 *
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Siemens Enterprise Communications 
 * GmbH & Co. KG and its licensors. All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 */
package org.osgi.impl.service.discovery.slp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Thomas Kiesslich
 * 
 */
public class ServicePublicationTracker implements ServiceTrackerCustomizer {

	private BundleContext	context																	= null;

	private SLPHandlerImpl	discovery																= null;

	private Map				/* <ServiceReference, ServiceEndpointDescription> */publicationAndSED	= null;

	public ServicePublicationTracker(BundleContext ctx, SLPHandlerImpl disco) {
		context = ctx;
		discovery = disco;
		publicationAndSED = Collections.synchronizedMap(new HashMap());
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference arg0) {
		ServicePublication sp = publishServicePublication(arg0);
		return sp;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void modifiedService(ServiceReference arg0, Object arg1) {
		unpublishServicePublication(arg0);
		publishServicePublication(arg0);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference,
	 *      java.lang.Object)
	 */
	public void removedService(ServiceReference arg0, Object arg1) {
		unpublishServicePublication(arg0);
	}

	/**
	 * 
	 * @param arg0
	 * @return
	 */
	private ServicePublication publishServicePublication(ServiceReference arg0) {
		ServicePublication sp = (ServicePublication) context.getService(arg0);
		ServiceEndpointDescription sed = discovery
				.publishService(
						(Collection) arg0
								.getProperty(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME),
						(Collection) arg0
								.getProperty(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION),
						(Collection) arg0
								.getProperty(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME),
						(Map) arg0
								.getProperty(ServicePublication.PROP_KEY_SERVICE_PROPERTIES),
						SLPHandlerImpl.PROP_VAL_PUBLISH_STRATEGY_PUSH,
						(String) arg0
								.getProperty(ServicePublication.PROP_KEY_ENDPOINT_ID));
		publicationAndSED.put(arg0, sed);
		return sp;
	}

	/**
	 * 
	 * @param srvReference the given reference to the service to unpublish
	 */
	private void unpublishServicePublication(ServiceReference srvReference) {
		discovery
				.unpublishService((ServiceEndpointDescription) publicationAndSED
						.get(srvReference));
		publicationAndSED.remove(srvReference);
	}
}
