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
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @version $Revision$
 */
public abstract class EventAdapter {
	final EventAdmin	eventAdmin;

	/**
	 * @param event
	 * @param eventAdmin
	 */
	public EventAdapter(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	/**
	 * @return
	 */
	public abstract Event convert();

	public void redeliver() {
		Event converted = convert();
		if (converted != null) {
			redeliverInternal(converted);
		}
	}

	/**
	 * subclasses should override this method if it wants to use sendEvent()
	 * instead.
	 */
	protected void redeliverInternal(Event converted) {
		eventAdmin.postEvent(converted);
	}

	public void putBundleProperties(Hashtable properties, Bundle bundle) {
		// assertion bundle != null
		properties.put(Constants.BUNDLE_ID, new Long(bundle.getBundleId()));
		String symbolicName = bundle.getSymbolicName();
		if (symbolicName != null) {
			properties.put(Constants.BUNDLE_SYMBOLICNAME, symbolicName);
		}
		properties.put(Constants.BUNDLE, bundle);
	}

	public void putExceptionProperties(Hashtable properties, Throwable t) {
		// assertion t != null
		properties.put(Constants.EXCEPTION, t);
		properties.put(Constants.EXCEPTION_CLASS, t.getClass().getName());
		String message = t.getMessage();
		if (message != null) {
			properties.put(Constants.EXCEPTION_MESSAGE, t.getMessage());
		}
	}

	public void putServiceReferenceProperties(Hashtable properties,
			ServiceReference ref) {
		// assertion ref != null
		properties.put(Constants.SERVICE, ref);
		properties.put(Constants.SERVICE_ID, ref
				.getProperty(org.osgi.framework.Constants.SERVICE_ID));
		Object o = ref.getProperty(org.osgi.framework.Constants.SERVICE_PID);
		if ((o != null) && (o instanceof String)) {
			properties.put(Constants.SERVICE_PID, (String) o);
		}
		Object o2 = ref.getProperty(org.osgi.framework.Constants.OBJECTCLASS);
		if ((o2 != null) && (o2 instanceof String[])) {
			properties.put(Constants.SERVICE_OBJECTCLASS, (String[]) o2);
		}
	}

	/*
	 * Utility function for converting classes into strings
	 */
	public String[] classes2strings(Class classes[]) {
		if ((classes == null) || (classes.length == 0))
			return null;
		String[] strings = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			strings[i] = classes[i].getName();
		}
		return strings;
	}
}