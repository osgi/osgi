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
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogService;

/**
 * @version $Revision$
 */
public class LogEntryAdapter extends EventAdapter {
	// constants for Event topic substring
	public static final String	TOPIC		= "org/osgi/service/log/LogEntry";
	// constants for Event properties
	public static final String	TIMESTAMP	= "timestamp";
	public static final String	MESSAGE		= "message";
	public static final String	LOG_LEVEL	= "log.level";
	public static final String	LOG_ENTRY	= "log.entry";
	private LogEntry			entry;

	public LogEntryAdapter(LogEntry entry, EventAdmin eventAdmin) {
		super(eventAdmin);
		this.entry = entry;
	}

	/**
	 * @return
	 * @see org.osgi.impl.service.event.mapper.EventAdapter#convert()
	 */
	public Event convert() {
		String topic = TOPIC;
		int level = entry.getLevel();
		switch (level) {
			case LogService.LOG_ERROR:
			case LogService.LOG_WARNING:
			case LogService.LOG_INFO:
			case LogService.LOG_DEBUG:
				break;
			default:
				// other log levels are represented by their decimal value
				topic += Constants.TOPIC_SEPARATOR + level;
		}
		Hashtable properties = new Hashtable();
		Bundle bundle = entry.getBundle();
		if (bundle == null) {
			throw new RuntimeException("LogEntry.getBundle() returns null");
		}
		putBundleProperties(properties, bundle);
		Throwable t = entry.getException();
		if (t != null) {
			putExceptionProperties(properties, t);
		}
		ServiceReference ref = entry.getServiceReference();
		if (ref != null) {
			putServiceReferenceProperties(properties, ref);
		}
		properties.put(LOG_ENTRY, entry);
		properties.put(LOG_LEVEL, new Integer(entry.getLevel()));
		if (entry.getMessage() != null)
			properties.put(MESSAGE, entry.getMessage());
		properties.put(TIMESTAMP, new Long(entry.getTime()));
		Event converted = new Event(topic, properties);
		return converted;
	}
}