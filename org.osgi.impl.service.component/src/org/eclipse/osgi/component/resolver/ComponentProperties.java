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

package org.eclipse.osgi.component.resolver;

import java.io.IOException;

import org.eclipse.osgi.component.Main;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ComponentProperties {

	/** The ConfigurationAdmin class */
	public final static String CMADMIN_SERVICE_CLASS = "org.osgi.service.cm.ConfigurationAdmin";

	/* ServiceTracker for configurationAdmin */
	private ServiceTracker configAdminTracker;

	/**
	 * ComponentProperties 
	 * 
	 * @param main Main SCR class
	 */
	public ComponentProperties(Main main) {
		configAdminTracker = new ServiceTracker(main.context, CMADMIN_SERVICE_CLASS, null);
		configAdminTracker.open(true); //true for track all services
	}

	/**
	 * Cleanup
	 * 
	 */
	public void dispose() {
		configAdminTracker.close();
	}

	/**
	 * 
	 * 
	 */

	public ConfigurationAdmin getConfigurationAdmin() {
		return (ConfigurationAdmin) configAdminTracker.getService();
	}

	/**
	 * get the Configuration for this Service Component name
	 * 
	 * A Service Components name is used to locate its configured properties
	 * in the OSGi Configuration Admin service. The property elements provide
	 * default or supplemental property values if not overridden by the 
	 * properties retrieved from Configuration Admin.
	 * 
	 * @param componentName Service Component name
	 */
	public Configuration getConfiguration(String componentName) throws IOException {

		Configuration config = null;
		ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) configAdminTracker.getService();
		if (configurationAdmin != null) {
			config = configurationAdmin.getConfiguration(componentName);
		}
		return config;
	}
}