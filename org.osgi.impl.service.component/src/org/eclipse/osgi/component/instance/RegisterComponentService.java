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
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ProvideDescription;
import org.osgi.framework.*;
import org.osgi.service.component.*;



/**
 *
 * Register the Service Component's provided services
 * A ServiceFactory is used to enable lazy activation
 * 
 * @version $Revision$
 */

abstract public class RegisterComponentService {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	//cannot instantiate - this is a utility class
	private RegisterComponentService() {
	}
	/**
	 * registerService
	 * 
	 * @param ip - InstanceProcess
	 * @param bc - BundleContext
	 * @param cdp - ComponentDescription plus Properties
	 * @param factory - boolean 
	 * @return ServiceRegistration
	 */
	static public ServiceRegistration registerService(InstanceProcess ip,BundleContext bc, ComponentDescriptionProp cdp, boolean factory) {

		final InstanceProcess instanceProcess = ip;
		final ComponentDescriptionProp component = cdp;
		final BundleContext bundleContext = bc;

		// process each provided service
		ProvideDescription[] provides = cdp.getComponentDescription().getService().getProvides();

		String [] interfaces = new String [provides.length];
		
		for (int i = 0; i < provides.length; i++) {
			interfaces[i] = provides[i].getInterfacename();
		}

		//Register the service
		// set the component.name
		Dictionary properties = cdp.getProperties();
		if (properties == null) {
			properties = new Hashtable(2);
		}
		properties.put(ComponentConstants.COMPONENT_NAME, cdp.getComponentDescription().getName());
		properties.put(ComponentConstants.COMPONENT_ID, new Long(instanceProcess.buildDispose.getNextComponentId()));

		//	register the service using a ServiceFactory
		ServiceRegistration serviceRegistration = null;
		if (factory) {
			//	register the service using a ServiceFactory
			serviceRegistration = bundleContext.registerService(interfaces, new ServiceFactory() {

				/* the instance created */
				Object instance;

				//ServiceFactory.getService method.
				public Object getService(Bundle bundle, ServiceRegistration registration) {

					if (DEBUG)
						System.out.println("RegisterComponentServiceFactory:getService: registration:" + registration);

					try {
						instance = instanceProcess.buildDispose.createInstance(component.getComponentDescription());
						instanceProcess.buildDispose.build(bundleContext, bundle, component, instance, null);
					} catch (Exception e) {
						//what to do here?
						System.err.println("Could not create instance of " + component.getComponentDescription());
					}

					return instance;
				}

				// ServiceFactory.ungetService method.
				public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
					if (DEBUG)
						System.out.println("RegisterComponentServiceFactory:ungetService: registration = " + registration);

					instanceProcess.buildDispose.disposeComponent(component);

				}
			}, properties);

		} else {
			serviceRegistration = bundleContext.registerService(interfaces, new ServiceFactory() {
	
				private Object instance;
				private int useCount = 0;
	
				//ServiceFactory.getService method.
				public Object getService(Bundle bundle, ServiceRegistration registration) {
	
					try {
						if (instance == null) {
							//instance = instanceBuild.createInstance(componentDescription);
							instance = instanceProcess.getInstance(component.getComponentDescription());
							instanceProcess.buildDispose.build(bundleContext, null, component, instance, null);
						}
						useCount++;
					} catch (Exception e) {
						//TODO handle error
						System.err.println("Could not create instance of " + component.getComponentDescription());
					}
	
					if (DEBUG)
						System.out.println("RegisterComponentService: getService: registration:" + registration);
	
					return instance;
				}
	
				// ServiceFactory.ungetService method.
				public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
					if (DEBUG)
						System.out.println("RegisterComponentService: ungetService: registration = " + registration);
					useCount--;
					if (useCount == 0)
						instanceProcess.buildDispose.disposeComponent(component);
					//registration.unregister();
				}
			}, properties);
		}	

		if (DEBUG)
			System.out.println("RegisterComponentService: register: " + serviceRegistration);
		
		return serviceRegistration;
	}

}
