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

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.ChannelEvent;
import org.osgi.service.event.EventChannel;

/**
 * @version $Revision$
 */
public class ServiceEventAdapter extends EventAdapter {
	// constants for ChannelEvent topic substring
	public static final String	UNREGISTERING	= "UNREGISTERING";
	public static final String	MODIFIED		= "MODIFIED";
	public static final String	REGISTERED		= "REGISTERED";

	private ServiceEvent		event;

	/**
	 * @param channel
	 */
	public ServiceEventAdapter(ServiceEvent event, EventChannel channel) {
		super(channel);
		this.event = event;
	}

	// override super's method to force syncronous event delivery
	public void redeliver() {
		channel.sendEvent(convert());
	}

	/**
	 * @return
	 * @see org.osgi.impl.service.event.mapper.EventAdapter#convert()
	 */
	public ChannelEvent convert() {
		String typename = null;
		switch (event.getType()) {
			case ServiceEvent.REGISTERED :
				typename = REGISTERED;
				break;
			case ServiceEvent.MODIFIED :
				typename = MODIFIED;
				break;
			case ServiceEvent.UNREGISTERING :
				typename = UNREGISTERING;
				break;
			default :
				throw new RuntimeException("Invalid ServiceEvent type ("
						+ event.getType() + ")");
		}
		String topic = ServiceEvent.class.getName() + "." + typename;

		Hashtable properties = new Hashtable();
		ServiceReference ref = event.getServiceReference();

		if (ref == null) {
			throw new RuntimeException(
					"ServiceEvent.getServiceReference() is null");
		}
		putServiceReferenceProperties(properties, ref);

		properties.put(Constants.EVENT, event);
		ChannelEvent ce = new ChannelEvent(topic, properties);
		return ce;
	}
}
