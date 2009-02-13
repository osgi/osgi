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

import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.ServiceEndpointDescription;

/**
 * @author kt32483
 * 
 */
public class DiscoveredServiceNotificationImpl implements
		DiscoveredServiceNotification {

	private ServiceEndpointDescription descr;
	private int type;

	public DiscoveredServiceNotificationImpl(ServiceEndpointDescription sed,
			int t) {
		descr = sed;
		type = t;
	}

	/**
	 * @see org.osgi.service.discovery.DiscoveredServiceNotification#getServiceEndpointDescription()
	 */
	public ServiceEndpointDescription getServiceEndpointDescription() {
		return descr;
	}

	/**
	 * @see org.osgi.service.discovery.DiscoveredServiceNotification#getType()
	 */
	public int getType() {
		return type;
	}

}
