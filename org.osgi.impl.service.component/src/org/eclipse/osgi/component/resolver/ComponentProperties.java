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
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import org.eclipse.osgi.component.*;


/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ComponentProperties implements ServiceTrackerCustomizer {

	/* set this to true to compile in debug messages */
	static final boolean				DEBUG	= false;
	
	/** The ConfigurationAdmin class */
	public final static String CMADMIN_SERVICE_CLASS = "org.osgi.service.cm.ConfigurationAdmin";

	/** ConfigurationAdmin Service */
	protected ConfigurationAdmin 		configurationAdmin;

	/** Bundle Context */
	protected BundleContext 			bundleContext;
	
	/** Main SCR class */
	protected Main 						main;
	
	/* ServiceTracker for configurationAdmin */
	private ServiceTracker				configAdminTracker;
	

	/**
	 * ComponentProperties 
	 * 
	 * @param main Main SCR class
	 */
	public ComponentProperties(Main main){
		
		this.main = main;
		configAdminTracker = new ServiceTracker(main.context, CMADMIN_SERVICE_CLASS, this);
		configAdminTracker.open();
		
	}
	
	/**
	 * Cleanup
	 * 
	 */
	public void dispose(){
		configAdminTracker.close();
	}

	/**
	 * 
	 * 
	 */
	
	public ConfigurationAdmin getConfigurationAdmin(){
		return configurationAdmin;
	}
	
	
	/**
	 * get the Configuration for this Service Component name
	 * 
	 * @param componentName Service Component name
	 */
	public Configuration getConfiguration(String componentName)throws IOException {
		
		Configuration config = null;
		if(configurationAdmin != null){
			config = configurationAdmin.getConfiguration(componentName);
		}	
		return config;
	}
	
	/** 
	 * Get the ConfigurationAdmin properties for this Service Component
	 * 
	 * A Service Components name is used to locate its configured properties
	 * in the OSGi Configuration Admin service. The property elements provide
	 * default or supplemental property values if not overridden by the 
	 * properties retrieved from Configuration Admin.
	 * 
	 * @param componentName - Service Component's name
	 * 
	 */
	public Dictionary getConfigurationAdminProperties(String componentName) throws IOException {
		
		Dictionary properties = null;
		
		if(configurationAdmin != null){
			Configuration config = configurationAdmin.getConfiguration(componentName);
			properties = config.getProperties();
		}	
				
		return properties;
		
	}
				
	
	 /**
     * A ConfigurationAdmin Service is being added to the ServiceTracker object.
     *
     * @param reference Reference to service being added to the <tt>ServiceTracker</tt> object.
     * @return The service object to be tracked for the
     * <tt>ServiceReference</tt> object or <tt>null</tt> if the <tt>ServiceReference</tt> object should not
     * be tracked.
     */
	public Object addingService(ServiceReference ref) {
		System.out.println("ServiceReference = "+ref);
		configurationAdmin = (ConfigurationAdmin) main.context.getService(ref);
		return configurationAdmin;
	}
	

	/**
     * A ConfigurationAdmin Service tracked by the ServiceTracker object has been modified.
     *
     * @param reference Reference to service that has been modified.
     * @param service The service object for the modified service.
     */
	public void modifiedService(ServiceReference ref,Object object) {
		
	}
	
	/**
     * The ConfigurationAdmin Service tracked by the Service Tracker has been removed
     *
     * <p>This method is called after a service is no longer being tracked
     * by the <tt>ServiceTracker</tt> object.
     *
     * @param reference Reference to service that has been removed.
     * @param service The service object for the removed service.
     */
	public void removedService(ServiceReference reference, Object object)	{
		 bundleContext.ungetService(reference);
	}
	
}