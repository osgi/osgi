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
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import info.dmtree.spi.DataPlugin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class ConfigPluginActivator implements BundleActivator {
    static final String DMT_CONFIG_PLUGIN_SERVICE_PID = 
        "org.osgi.impl.service.dmt.config";
    
    static final String[] PLUGIN_ROOT_PATH = new String[] {
        ".", "OSGi", "Configuration"
    };
    static final String PLUGIN_ROOT_URI = "./OSGi/Configuration";

    private ServiceRegistration servReg;
	private ServiceTracker      configTracker;
    private ServiceTracker      logTracker;
	private ConfigPlugin        configPlugin;

    
	public void start(BundleContext bc) throws BundleException {
		System.out.println("Configuration plugin activation started.");
        
		// looking up the Configuration Admin and the Log service
        configTracker = 
            new ServiceTracker(bc, ConfigurationAdmin.class.getName(), null);
        configTracker.open();
        
        logTracker = new ServiceTracker(bc, LogService.class.getName(), null);
        logTracker.open();
        
		// creating the service
		configPlugin = new ConfigPlugin(configTracker, logTracker);
        
		// registering the service
		Hashtable properties = new Hashtable();
		properties.put("dataRootURIs", new String[] { PLUGIN_ROOT_URI });
        properties.put("service.pid", DMT_CONFIG_PLUGIN_SERVICE_PID);
        String[] services = new String[] {
                DataPlugin.class.getName(),
                ManagedService.class.getName()
        };
		servReg = bc.registerService(services, configPlugin, properties);
		System.out.println("Configuration plugin activation finished successfully.");
	}

	public void stop(BundleContext bc) throws BundleException {
        // unregistering the service
		servReg.unregister();
        
		// stopping the trackers
		configTracker.close();
		logTracker.close();
	}
}
