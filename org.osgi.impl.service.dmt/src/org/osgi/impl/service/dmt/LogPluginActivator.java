/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.dmt;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.log.*;

public class LogPluginActivator implements BundleActivator {
	private ServiceRegistration	servReg;
	private ServiceReference	logRef;
	private ServiceReference	logReaderRef;
	private ServiceReference	adminRef;
	private LogPlugin			logPlugin;
	static final String			PLUGIN_ROOT	= "./OSGi/log";

	public void start(BundleContext bc) throws BundleException {
		System.out.println("Log plugin activated.");
		//looking up the services needed
		logRef = bc.getServiceReference(LogService.class.getName());
		if (logRef == null)
			throw new BundleException("Cannot find Log Service.");
		LogService ls = (LogService) bc.getService(logRef);
		if (ls == null)
			throw new BundleException("Log Service no longer registered.");
		logReaderRef = bc.getServiceReference(LogReaderService.class.getName());
		if (logReaderRef == null)
			throw new BundleException("Cannot find Log Reader Service.");
		LogReaderService lrs = (LogReaderService) bc.getService(logReaderRef);
		if (lrs == null)
			throw new BundleException("Log Service no longer registered.");
		adminRef = bc.getServiceReference(DmtAdmin.class.getName());
		if (adminRef == null)
			throw new BundleException("Cannot find Dmt Admin Service.");
		DmtAdmin da = (DmtAdmin) bc.getService(adminRef);
		if (da == null)
			throw new BundleException("Dmt Admin Service no longer registered.");
		//creating the service
		//TODO make the plugin and activator the same so that we don't need to pass all these
		logPlugin = new LogPlugin(bc, ls, lrs, da);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] {PLUGIN_ROOT});
		props.put("execRootURIs", new String[] {PLUGIN_ROOT});
		String[] ifs = new String[] {DmtDataPlugin.class.getName(),
				DmtExecPlugin.class.getName()};
		servReg = bc.registerService(ifs, logPlugin, props);
	}

	public void stop(BundleContext bc) throws BundleException {
		//unregistering the service
		servReg.unregister();
		//releasing the used services
		bc.ungetService(logRef);
		bc.ungetService(logReaderRef);
		bc.ungetService(adminRef);
	}
}
