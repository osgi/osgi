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

import java.util.Hashtable;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @version $Revision$
 */
public class ConfigurationEventAdapter extends EventAdapter {
	// constants for Event topic substring
	public static final String	HEADER			= "org/osgi/service/cm/ConfigurationEvent";
	public static final String	CM_UPDATED		= "CM_UPDATED";
	public static final String	CM_DELETED		= "CM_DELETED";
	// constants for Event properties
	public static final String	CM_FACTORY_PID	= "cm.factoryPid";
	public static final String	CM_PID			= "cm.pid";
	private ConfigurationEvent	event;

	public ConfigurationEventAdapter(ConfigurationEvent event,
			EventAdmin eventAdmin) {
		super(eventAdmin);
		this.event = event;
	}

	/**
	 * @return
	 * @see org.osgi.impl.service.event.mapper.EventAdapter#convert()
	 */
	public Event convert() {
		String typename = null;
		switch (event.getType()) {
			case ConfigurationEvent.CM_UPDATED :
				typename = CM_UPDATED;
				break;
			case ConfigurationEvent.CM_DELETED :
				typename = CM_DELETED;
				break;
			default :
				return null;
		}
		String topic = HEADER + Constants.TOPIC_SEPARATOR + typename;
		Hashtable properties = new Hashtable();
		ServiceReference ref = event.getReference();
		if (ref == null) {
			throw new RuntimeException(
					"ServiceEvent.getServiceReference() is null");
		}
		properties.put(CM_PID, event.getPid());
		if (event.getFactoryPid() != null) {
			properties.put(CM_FACTORY_PID, event.getFactoryPid());
		}
		putServiceReferenceProperties(properties, ref);
		// assert objectClass includes
		// "org.osgi.service.cm.ConfigurationAdmin"
		Event converted = new Event(topic, properties);
		return converted;
	}
}