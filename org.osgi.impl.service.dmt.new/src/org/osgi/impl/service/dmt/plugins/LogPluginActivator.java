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
package org.osgi.impl.service.dmt.plugins;

import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import info.dmtree.spi.DataPlugin;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

public class LogPluginActivator implements BundleActivator {
	static final String[] PLUGIN_ROOT_PATH = 
        new String[] { ".", "OSGi", "Log" };
    static final String PLUGIN_ROOT_URI = "./OSGi/Log";

    private ServiceRegistration servReg;
	private ServiceTracker      logReaderTracker;
    private LogPlugin           logPlugin;

	public void start(BundleContext bc) throws BundleException {
        System.out.println("Log plugin activated.");
		// setting up the needed trackers
        logReaderTracker = 
            new ServiceTracker(bc, LogReaderService.class.getName(), null);
        logReaderTracker.open();

		// creating the service
		logPlugin = new LogPlugin(bc, logReaderTracker);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] { PLUGIN_ROOT_URI });
		String[] ifs = new String[] {DataPlugin.class.getName()};
		servReg = bc.registerService(ifs, logPlugin, props);
	}

	public void stop(BundleContext bc) throws BundleException {
        // closing the used trackers
		logReaderTracker.close();
        
		// unregistering the service
		servReg.unregister();
	}
}
