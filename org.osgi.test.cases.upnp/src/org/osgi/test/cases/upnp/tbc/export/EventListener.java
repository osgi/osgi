package org.osgi.test.cases.upnp.tbc.export;

import java.util.*;
import org.osgi.service.upnp.*;

/**
 * 
 * 
 * @author
 * @version
 * @since
 */
public class EventListener implements UPnPEventListener {
	public Vector	evs;
	private String	udn;
	private String	service;

	public EventListener(String udn, String service) {
		this.udn = udn;
		this.service = service;
	}

	public void notifyUPnPEvent(String deviceID, String serviceID,
			Dictionary events) {
		if (!udn.equals(deviceID) || !service.equals(serviceID)) {
			System.out
					.println("EXPORT: Received event is not for current device or service");
			return;
		}
		evs.addElement(events);
	}
}