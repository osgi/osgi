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

package org.eclipse.osgi.component.instance;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ProvideDescription;
import org.osgi.framework.*;
import org.osgi.service.component.*;


/**
 *
 * Register the Service Component's provided services when servicefactory="true"
 *  
 * @version $Revision$
 */

public class RegisterComponentServiceFactory {

	/* set this to true to compile in debug messages */
	static final boolean		DEBUG	= false;
	
	/** the component description */
	protected ComponentDescription componentDescription;
	
	/* the component description plus properties */
	protected ComponentDescriptionProp component;
	
	/* List of all ServiceRegistrations */
	protected ArrayList serviceRegistrations = new ArrayList();
	
	/* the buildInstance class */
	protected BuildDispose instanceBuild;
		
	/**
	 * RegisterService - register the specified service in the service registry
	 * 
	 * @param resolveComponent - called by ResolveCompoent
	 */
	public RegisterComponentServiceFactory(BuildDispose buildDispose){
		instanceBuild = buildDispose;
	}
	
	/**
	 * Register Services
	 * 
	 * @param bundleContext
	 * @param component
	 * @return Array list of service registrations
	 */
	public ArrayList registerServices(BundleContext bundleContext, ComponentDescriptionProp component){
		
		this.component = component;
		componentDescription = component.getComponentDescription();
		
		ServiceRegistration serviceRegistration;
								
		// process each provided service
		ProvideDescription [] provides = componentDescription.getService().getProvides();
		
		for (int i = 0; i < provides.length; i++) {
			String serviceProvided = provides[i].getInterfacename();
			
			//Register the service
			serviceRegistration = register(bundleContext, serviceProvided, component.getProperties());
			
			//add to the saved list of service registrations
			serviceRegistrations.add(serviceRegistration);
			
		}
		return serviceRegistrations;
	}
				
	/**
	 * Register a ServiceFactory for the provided service
	 * 
	 * @param bundleContext
	 * @param serviceProvided
	 * @param properties
	 * @return the ServiceRegistration object
	 */
	public ServiceRegistration register(final BundleContext bundleContext, String serviceProvided,Dictionary properties ){
	
		// set the component.name
		if(properties == null){
			properties = new Hashtable(1);
			properties.put(ComponentConstants.COMPONENT_NAME,componentDescription.getName());
		} 
				
		//	register the service using a ServiceFactory
		ServiceRegistration serviceRegistration = bundleContext.registerService(
			serviceProvided,
			new ServiceFactory() {
				
				/* the instance created */
				Object instance;
				
				//ServiceFactory.getService method.
				public Object getService(Bundle bundle,
					ServiceRegistration registration) {
					
					if(DEBUG)
						System.out.println("RegisterComponentServiceFactory:getService: registration:"+registration);
					
					try {
						instance = instanceBuild.createInstance(componentDescription);
						instanceBuild.build(bundleContext, component, instance,null);
					} catch (Exception e) {
						//what to do here?
						System.err.println("Could not create instance of " + componentDescription);
					}
					
					return instance;
				}

				// ServiceFactory.ungetService method.
				public void ungetService(Bundle bundle,
					ServiceRegistration registration, Object service) {
						if(DEBUG)
							System.out.println("RegisterComponentServiceFactory:ungetService: registration = "+registration);
						
						instanceBuild.dispose(bundleContext, component);
						
				}
			}, properties);
		
		
		if (DEBUG)
			System.out.println("RegisterComponentServiceFactory:register: "+serviceRegistration);
		
		return serviceRegistration;
		}

	/**
	 * Unregister all services
	 *
	 */
	public void unregisterAllServices(){
		
		ServiceRegistration serviceRegistration;
		if(serviceRegistrations != null){
			Iterator it = serviceRegistrations.iterator();
			while(it.hasNext()){
				serviceRegistration = (ServiceRegistration)it.next();
				serviceRegistration.unregister();
				if(DEBUG)
					System.out.println("RegisterComponentServiceFactory: unregisterAllServices: serviceRegistration "+serviceRegistration);
			}
			serviceRegistrations = null;
		}
	}
	
}
