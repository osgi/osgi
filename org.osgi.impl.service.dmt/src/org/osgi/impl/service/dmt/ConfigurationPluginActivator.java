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
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.dmt.DmtDataPlugIn;

public class ConfigurationPluginActivator implements BundleActivator {
	private ServiceRegistration	servReg;
	private ServiceReference	configRef;
	private ConfigurationPlugin	configPlugin;
	static final String			PLUGIN_ROOT	= "./OSGi/cfg";

	public void start(BundleContext bc) throws BundleException {
		System.out.println("Configuration plugin activation started.");
		//looking up the Configuration Admin
		configRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
		if (configRef == null)
			throw new BundleException("Cannot find ConfigurationAdmin service.");
		ConfigurationAdmin ca = (ConfigurationAdmin) bc.getService(configRef);
		if (ca == null)
			throw new BundleException(
					"ConfigurationAdmin service no longer registered.");
		//creating the service
		configPlugin = new ConfigurationPlugin(bc, ca);
		Hashtable properties = new Hashtable();
		properties.put("dataRootURIs", new String[] {PLUGIN_ROOT});
		//registering the service
		servReg = bc.registerService(DmtDataPlugIn.class.getName(),
				configPlugin, properties);
		System.out
				.println("Configuration plugin activation finished successfully.");
	}

	public void stop(BundleContext bc) throws BundleException {
		//unregistering the service
		servReg.unregister();
		//releasing the Configuration Admin
		bc.ungetService(configRef);
	}
}
