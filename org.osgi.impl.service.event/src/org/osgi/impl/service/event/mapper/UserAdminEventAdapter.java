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

import java.util.Hashtable;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdminEvent;

/**
 * @version $Revision$
 */
public class UserAdminEventAdapter extends EventAdapter {
	// constants for Event topic substring
	public static final String	TOPIC			= "org/osgi/service/useradmin/UserAdminEvent";
	public static final String	ROLE_CREATED	= "ROLE_CREATED";
	public static final String	ROLE_CHANGED	= "ROLE_CHANGED";
	public static final String	ROLE_REMOVED	= "ROLE_REMOVED";
	// constants for Event properties
	public static final String	ROLE			= "role";
	public static final String	ROLE_NAME		= "role.name";
	public static final String	ROLE_TYPE		= "role.type";
	private UserAdminEvent		event;

	public UserAdminEventAdapter(UserAdminEvent event, EventAdmin eventAdmin) {
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
			case UserAdminEvent.ROLE_CREATED :
				typename = ROLE_CREATED;
				break;
			case UserAdminEvent.ROLE_CHANGED :
				typename = ROLE_CHANGED;
				break;
			case UserAdminEvent.ROLE_REMOVED :
				typename = ROLE_REMOVED;
				break;
			default :
				return null;
		}
		String topic = TOPIC + Constants.TOPIC_SEPARATOR + typename;
		Hashtable properties = new Hashtable();
		ServiceReference ref = event.getServiceReference();
		if (ref == null) {
			throw new RuntimeException(
					"UserAdminEvent's getServiceReference() returns null.");
		}
		putServiceReferenceProperties(properties, ref);
		Role role = event.getRole();
		if (role == null) {
			throw new RuntimeException(
					"UserAdminEvent's getRole() returns null.");
		}
		if (role != null) {
			properties.put(ROLE, role);
			properties.put(ROLE_NAME, role.getName());
			properties.put(ROLE_TYPE, new Integer(role.getType()));
		}
		properties.put(Constants.EVENT, event);
		Event converted = new Event(topic, properties);
		return converted;
	}
}