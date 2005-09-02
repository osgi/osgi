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

import java.util.Hashtable;
import java.util.List;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.ComponentInstance;

/**
 * Static utility class to register a Component Configuration's provided service.
 * A ServiceFactory is used to enable lazy activation
 * 
 * @version $Revision$
 */
abstract class RegisterComponentService {

	/* set this to true to compile in debug messages */
	static final boolean	DEBUG	= false;

	// cannot instantiate - this is a utility class
	private RegisterComponentService() {
	}

	/**
	 * registerService
	 * 
	 * @param ip - InstanceProcess
	 * @param cdp - ComponentDescription plus Properties
	 */
	static void registerService(InstanceProcess instanceProcess,
			ComponentDescriptionProp cdp) {

		ComponentDescription cd = cdp.getComponentDescription();
		
		//make final references for use by anonymous inner class
		final InstanceProcess finalInstanceProcess = instanceProcess;
		final ComponentDescriptionProp finalCDP = cdp;

		List servicesProvided = cd.getServicesProvided();
		String[] servicesProvidedArray = (String[]) servicesProvided
				.toArray(new String[servicesProvided.size()]);

		// register the service using a ServiceFactory
		ServiceRegistration serviceRegistration = null;
		if (cd.getService().isServicefactory()) {
			// register the service using a ServiceFactory
			serviceRegistration = cd.getBundleContext().registerService(
					servicesProvidedArray, new ServiceFactory() {
						// map of Bundle:componentInstance
						Hashtable	instances;

						// ServiceFactory.getService method.
						public Object getService(Bundle bundle,
								ServiceRegistration registration) {
							if (DEBUG)
								System.out
										.println("RegisterComponentServiceFactory:getService: registration:"
												+ registration);

							ComponentInstance componentInstance = null;
							try {
								componentInstance = finalInstanceProcess.buildDispose
										.build(bundle, finalCDP);
							}
							catch (ComponentException e) {
								Log
										.log(
												1,
												"[SCR] Error attempting to register a Service Factory.",
												e);
							}

							if (componentInstance != null) {
								// save so we can dispose later
								synchronized (this) {
									if (instances == null) {
										instances = new Hashtable();
									}
								}
								instances.put(bundle, componentInstance);
							}
							return componentInstance.getInstance();
						}

						// ServiceFactory.ungetService method.
						public void ungetService(Bundle bundle,
								ServiceRegistration registration, Object service) {
							if (DEBUG)
								System.out
										.println("RegisterComponentServiceFactory:ungetService: registration = "
												+ registration);
							((ComponentInstance) instances.get(bundle))
									.dispose();
							instances.remove(bundle);
							synchronized (this) {
								if (instances.isEmpty()) {
									instances = null;
								}
							}
						}
					}, cdp.getProperties());
		}
		else {
			// servicefactory=false
			// always return the same instance
			serviceRegistration = cd.getBundleContext().registerService(
					servicesProvidedArray, new ServiceFactory() {

						int					references	= 0;
						// if we create an instance, keep track of it
						ComponentInstance	instance;

						// ServiceFactory.getService method.
						public Object getService(Bundle bundle,
								ServiceRegistration registration) {
							if (DEBUG)
								System.out
										.println("RegisterComponentService: getService: registration = "
												+ registration);

							synchronized (this) {

								if (finalCDP.getInstances().isEmpty()) {
									try {
										instance = finalInstanceProcess.buildDispose
												.build(null, finalCDP);
									}
									catch (ComponentException e) {
										Log
												.log(
														1,
														"[SCR] Error attempting to register Service.",
														e);
										return null;
									}
								}
								references++;
							}
							return ((ComponentInstance) finalCDP.getInstances()
									.get(0)).getInstance();
						}

						// ServiceFactory.ungetService method.
						public void ungetService(Bundle bundle,
								ServiceRegistration registration, Object service) {
							if (DEBUG)
								System.out
										.println("RegisterComponentService: ungetService: registration = "
												+ registration);

							synchronized (this) {
								references--;
								if (references < 1 && instance != null) {
									// if instance != null then we created it
									// dispose instance
									instance.dispose();
									instance = null;
								}
							}
						}
					}, cdp.getProperties());
		}

		if (DEBUG)
			System.out.println("RegisterComponentService: register: "
					+ serviceRegistration);

		cdp.setServiceRegistration(serviceRegistration);
	}

}
