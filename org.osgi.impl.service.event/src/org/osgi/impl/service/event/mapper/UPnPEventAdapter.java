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

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @version $Revision$
 */
public class UPnPEventAdapter extends EventAdapter {
	// constants for Event topic substring
	public static final String	TOPIC			= "org/osgi/service/upnp/UPnPEvent";
	// constants for Event properties
	public static final String	UPNP_DEVICEID	= "upnp.deviceId";
	public static final String	UPNP_SERVICEID	= "upnp.serviceId";
	public static final String	UPNP_EVENTS		= "upnp.events";
	private String				deviceId;
	private String				serviceId;
	private Dictionary			events;

	public UPnPEventAdapter(String deviceId, String serviceId,
			Dictionary events, EventAdmin eventAdmin) {
		super(eventAdmin);
		this.deviceId = deviceId;
		this.serviceId = serviceId;
		this.events = events;
	}

	/**
	 * @return
	 * @see org.osgi.impl.service.event.mapper.EventAdapter#convert()
	 */
	public Event convert() {
		Hashtable properties = new Hashtable();
		if (deviceId != null) {
			properties.put(UPNP_DEVICEID, deviceId);
		}
		if (serviceId != null) {
			properties.put(UPNP_SERVICEID, serviceId);
		}
		if (events != null) {
			properties.put(UPNP_EVENTS, events);
		}
		Event converted = new Event(TOPIC, properties);
		return converted;
	}
}