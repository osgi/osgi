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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.dmt.spi.DataPluginFactory;
import org.osgi.util.tracker.ServiceTracker;

public class ConfigurationPluginActivator implements BundleActivator {
    static final String[] PLUGIN_ROOT_PATH = new String[] {
        ".", "OSGi", "Configuration"
    };

    private ServiceRegistration servReg;
	private ServiceTracker      configTracker;
	private ConfigurationPlugin configPlugin;
	

	public void start(BundleContext bc) throws BundleException {
		System.out.println("Configuration plugin activation started.");
        
		// looking up the Configuration Admin
        configTracker = 
            new ServiceTracker(bc, ConfigurationAdmin.class.getName(), null);
        configTracker.open();
        
		// creating the service
        String pluginRoot = Node.convertPathToUri(PLUGIN_ROOT_PATH);
		configPlugin = new ConfigurationPlugin(bc, configTracker);
		Hashtable properties = new Hashtable();
		properties.put("dataRootURIs", new String[] { pluginRoot });
        
		// registering the service
		servReg = bc.registerService(DataPluginFactory.class.getName(),
				configPlugin, properties);
		System.out.println("Configuration plugin activation finished successfully.");
	}

	public void stop(BundleContext bc) throws BundleException {
        // unregistering the service
		servReg.unregister();
        
		// stopping the Configuration Admin tracker
		configTracker.close();
	}
}
