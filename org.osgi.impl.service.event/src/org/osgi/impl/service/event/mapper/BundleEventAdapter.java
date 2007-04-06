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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @version $Revision$
 */
public class BundleEventAdapter extends EventAdapter {
	// constants for Event topic substring
	public static final String	HEADER		= "org/osgi/framework/BundleEvent";
	public static final String	INSTALLED	= "INSTALLED";
	public static final String	STOPPED		= "STOPPED";
	public static final String	STARTED		= "STARTED";
	public static final String	UPDATED		= "UPDATED";
	public static final String	UNINSTALLED	= "UNINSTALLED";
	public static final String	RESOLVED	= "RESOLVED";
	public static final String	UNRESOLVED	= "UNRESOLVED";
	private BundleEvent			event;

	public BundleEventAdapter(BundleEvent event, EventAdmin eventAdmin) {
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
			case BundleEvent.INSTALLED :
				typename = INSTALLED;
				break;
			case BundleEvent.STOPPED :
				typename = STOPPED;
				break;
			case BundleEvent.STARTED :
				typename = STARTED;
				break;
			case BundleEvent.UPDATED :
				typename = UPDATED;
				break;
			case BundleEvent.UNINSTALLED :
				typename = UNINSTALLED;
				break;
			case BundleEvent.RESOLVED :
				typename = RESOLVED;
				break;
			case BundleEvent.UNRESOLVED :
				typename = UNRESOLVED;
				break;
			default :
				// unknown events must be send as their decimal value
				typename = ""+event.getType();
		}
		String topic = HEADER + Constants.TOPIC_SEPARATOR + typename;
		Hashtable properties = new Hashtable();
		Bundle bundle = event.getBundle();
		if (bundle == null) {
			throw new RuntimeException("BundleEvent.getBundle() returns null");
		}
		else {
			putBundleProperties(properties, bundle);
		}
		properties.put(Constants.EVENT, event);
		Event converted = new Event(topic, properties);
		return converted;
	}
}