/*
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

package org.eclipse.osgi.component;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;

public class Log implements ServiceListener {
	protected BundleContext bc;
	private static ServiceReference logReference = null;
	private static LogService logService = null;
	
	static final String LOGSERVICE_NAME = LogService.class.getName();
	
	Log(BundleContext bc) {
		this.bc = bc;
	    logReference = bc.getServiceReference(LogService.class.getName());
		if (logReference != null) {
			logService = (LogService) bc.getService(logReference);
		}
		addServiceListener();
	}
	
	public static void log(int severity, String message) {
		log(severity, message, null);
	}

	public static void log(int severity, String message, Throwable e) {
		if (logService != null) {
			logService.log(severity, message, e);
		}
	}

	public void shutdown() {
		if (logService != null) {
			bc.ungetService(logReference);
			logService = null;
		}
		removeServiceListener();
	}

	/**
	 * addService Listener - Listen for changes in the referenced services
	 * 
	 */
	public void addServiceListener() {
		try {
			bc.addServiceListener(this, "(objectclass=" + LOGSERVICE_NAME + ")");
		} catch ( InvalidSyntaxException e){
		}

	}

	/**
	 * removeServiceListener -
	 * 
	 */
	public void removeServiceListener() {
		bc.removeServiceListener(this);
	}

	/**
	 * Listen for service change events
	 * 
	 * @param event
	 */
	public void serviceChanged(ServiceEvent event) {

		ServiceReference logReference = event.getServiceReference();
		int eventType = event.getType();

		switch (eventType) {

			case ServiceEvent.REGISTERED :
				if (logService == null) {
					logReference = event.getServiceReference();
					logService = (LogService) bc.getService(logReference);
				}
				break;
			case ServiceEvent.UNREGISTERING :
				if (event.getServiceReference().equals(logReference)) {
					bc.ungetService(logReference);
					logService = null;
				}
				break;
		}

	}


}