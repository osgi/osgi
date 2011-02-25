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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.dmt.plugins.ConfigPluginActivator;
import org.osgi.impl.service.dmt.plugins.LogPluginActivator;

/**
 * Merges Dmt Admin, Log Plugin and Configuration Plugin activators.
 */
public class Activator implements BundleActivator {
	DmtAdminActivator dmtAdminActivator = new DmtAdminActivator();
	LogPluginActivator logPluginActivator = new LogPluginActivator();
	ConfigPluginActivator configurationPluginActivator = new ConfigPluginActivator();
	
	public void start(BundleContext context) throws Exception {
		dmtAdminActivator.start(context);
		logPluginActivator.start(context);
		configurationPluginActivator.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		configurationPluginActivator.stop(context);
		logPluginActivator.stop(context);
		dmtAdminActivator.stop(context);
	}
}
