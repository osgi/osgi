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

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @version $Revision$
 */
public class FrameworkEventAdapter extends EventAdapter {
	// constants for Event topic substring
	public static final String	STARTLEVEL_CHANGED	= "STARTLEVEL_CHANGED";
	public static final String	STARTED				= "STARTED";
	public static final String	PACKAGES_REFRESHED	= "PACKAGES_REFRESHED";
	public static final String	ERROR				= "ERROR";

	protected FrameworkEvent	event;

	/**
	 * @param event
	 * @param channel
	 */
	public FrameworkEventAdapter(FrameworkEvent event, EventAdmin channel) {
		super(channel);
		this.event = event;
	}

	/**
	 * @return
	 * @see org.osgi.impl.service.event.mapper.EventAdapter#convert()
	 */
	public Event convert() {
		String typename = null;
		switch (event.getType()) {
			case FrameworkEvent.ERROR :
				typename = ERROR;
				break;
			case FrameworkEvent.PACKAGES_REFRESHED :
				typename = PACKAGES_REFRESHED;
				break;
			case FrameworkEvent.STARTED :
				typename = STARTED;
				break;
			case FrameworkEvent.STARTLEVEL_CHANGED :
				typename = STARTLEVEL_CHANGED;
				break;
			default :
				throw new RuntimeException("Invalid FrameworkEvent type ("
						+ event.getType() + ")");
		}
		String topic = FrameworkEvent.class.getName() + "." + typename;

		Hashtable properties = new Hashtable();
		Bundle bundle = event.getBundle();
		if (bundle == null) {
			throw new RuntimeException(
					"FrameworkEvent.getBundle() returns null");
		}
		putBundleProperties(properties, bundle);
		Throwable t = event.getThrowable();
		if (t != null) {
			putExceptionProperties(properties, t);
		}
		properties.put(Constants.EVENT, event);
		Event ce = new Event(topic, properties);
		return ce;
	}
}
