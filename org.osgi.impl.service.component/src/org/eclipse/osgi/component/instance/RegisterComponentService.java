/*
 * $Header$
 * 
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
import java.util.Enumeration;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ProvideDescription;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.ComponentException;

/**
 *
 * Register the Service Component's provided services
 * A ServiceFactory is used to enable lazy activation
 * 
 * @version $Revision$
 */

abstract class RegisterComponentService {

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
	static ServiceRegistration registerService(InstanceProcess ip, BundleContext bc, ComponentDescriptionProp cdp, boolean factory, Dictionary props) {
		//static ServiceRegistration registerService(String[] clazzes, Dictionary properties) {	

		final InstanceProcess instanceProcess = ip;
		final ComponentDescriptionProp component = cdp;
		final BundleContext bundleContext = bc;

		// process each provided service
		ProvideDescription[] provides = cdp.getComponentDescription().getService().getProvides();

		String[] interfaces = new String[provides.length];

		for (int i = 0; i < provides.length; i++) {
			interfaces[i] = provides[i].getInterfacename();
		}

		//get the xml properties
		Dictionary properties = new Hashtable((Hashtable) cdp.getProperties());

		//add required properties
		if (cdp.getComponentDescription().getFactory() == null)
			properties.put(ComponentConstants.COMPONENT_ID, new Long(instanceProcess.buildDispose.getNextComponentId()));

		//add additional properties
		if (props != null) {
			Enumeration keys = props.keys();
			while (keys.hasMoreElements()) {
				Object keyValue = keys.nextElement();
				properties.put(keyValue, props.get(keyValue));
			}
		}

		//	register the service using a ServiceFactory
		ServiceRegistration serviceRegistration = null;
		if (factory) {
			//	register the service using a ServiceFactory
			serviceRegistration = bundleContext.registerService(interfaces, new ServiceFactory() {
				/* map of instace:componentInstance*/
				Hashtable instances;

				//ServiceFactory.getService method.
				public Object getService(Bundle bundle, ServiceRegistration registration) {
					if (DEBUG)
						System.out.println("RegisterComponentServiceFactory:getService: registration:" + registration);
					ComponentInstance componentInstance = null;
					Object instance = null;
					try {
						componentInstance = instanceProcess.buildDispose.build(bundleContext, bundle, component, null);
						instance = componentInstance.getInstance();
					} catch (ComponentException e) {
						Log.log(1, "[SCR] Error attempting to register a Service Factory.", e);
					}
					
					if (componentInstance != null && instance != null) {
						//save so we can dispose later
						if (instances == null) {
							instances = new Hashtable();
						}
						instances.put(instance,componentInstance);
					}
					return instance;
				}

				// ServiceFactory.ungetService method.
				public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
					if (DEBUG)
						System.out.println("RegisterComponentServiceFactory:ungetService: registration = " + registration);
					
					((ComponentInstance)instances.get(service)).dispose();
					instances.remove(service);
					if (instances.isEmpty()) {
						instances = null;
					}
				}
			}, properties);
		} else {
			//servicefactory=false
			//always return the same instance
			serviceRegistration = bundleContext.registerService(interfaces, new ServiceFactory() {

				int references = 0;
				//if we create an instance, keep track of it
				ComponentInstance instance;

				//ServiceFactory.getService method.
				public Object getService(Bundle bundle, ServiceRegistration registration) {
					if (DEBUG)
						System.out.println("RegisterComponentService: getService: registration = " + registration);
					if (component.getInstances().isEmpty()) {
						try {
							instance = instanceProcess.buildDispose.build(bundleContext, null, component, null);
						} catch (ComponentException e) {
							Log.log(1, "[SCR] Error attempting to register a Service.", e);				
							return null;
						}
					}
					references++;
					return ((ComponentInstance) component.getInstances().get(0)).getInstance();
				}

				// ServiceFactory.ungetService method.
				public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
					if (DEBUG)
						System.out.println("RegisterComponentService: ungetService: registration = " + registration);
					references--;
					if (references == 0 && instance != null) {
						//if instance != null then we created it
						//dispose instance
						instance.dispose();
						instance = null;
					}
				}
			}, properties);
		}

		if (DEBUG)
			System.out.println("RegisterComponentService: register: " + serviceRegistration);

		return serviceRegistration;
	}

}
