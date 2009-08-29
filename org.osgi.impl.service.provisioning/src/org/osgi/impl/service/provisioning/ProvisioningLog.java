package org.osgi.impl.service.provisioning;

import java.util.*;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A Simple Event tracker for the ProvisioningService. If a LogService could be
 * present, it should be linked to that. For the reference implementation we
 * just track things locally.
 * 
 * @author breed
 */
public class ProvisioningLog extends ServiceTracker {
	Vector	log	= new Vector();

	ProvisioningLog( BundleContext context ) {
		super(context, LogService.class.getName(), null );
		open();
	}
	
	/** Adds a message to the long and timestamps it. */
	synchronized void log(String message) {
		String msg = new Date().toString() + ": " + message;
		log.add(0, msg);
		LogService log = (LogService) getService();
		if ( log != null )
			log.log(LogService.LOG_WARNING, msg );
		else
			System.out.println(msg);
	}

	/** Returns the full log. Most recent messages will be first. */
	Enumeration getLog() {
		return log.elements();
	}
}
