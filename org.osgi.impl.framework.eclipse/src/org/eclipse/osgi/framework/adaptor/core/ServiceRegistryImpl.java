/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import java.util.ArrayList;
import java.util.HashMap;
import org.osgi.framework.*;

/**
 * A default implementation of the ServiceRegistry.
 */
public class ServiceRegistryImpl implements org.eclipse.osgi.framework.adaptor.ServiceRegistry {

	/** Published services by class name. Key is a String class name; Value is a ArrayList of ServiceRegistrations */
	protected HashMap publishedServicesByClass;
	/** All published services. Value is ServiceRegistrations */
	protected ArrayList allPublishedServices;
	/** Published services by BundleContext.  Key is a BundleContext; Value is a ArrayList of ServiceRegistrations*/
	protected HashMap publishedServicesByContext;

	/**
	 * Initializes the internal data structures of this ServiceRegistry.
	 *
	 */
	public void initialize() {
		publishedServicesByClass = new HashMap(50);
		publishedServicesByContext = new HashMap(50);
		allPublishedServices = new ArrayList(50);
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.ServiceRegistry#publishService(BundleContext, ServiceRegistration)
	 */
	public void publishService(BundleContext context, ServiceRegistration serviceReg) {

		// Add the ServiceRegistration to the list of Services published by BundleContext.
		ArrayList contextServices = (ArrayList) publishedServicesByContext.get(context);
		if (contextServices == null) {
			contextServices = new ArrayList(10);
			publishedServicesByContext.put(context, contextServices);
		}
		contextServices.add(serviceReg);

		// Add the ServiceRegistration to the list of Services published by Class Name.
		String[] clazzes = (String[]) serviceReg.getReference().getProperty(Constants.OBJECTCLASS);
		int size = clazzes.length;

		for (int i = 0; i < size; i++) {
			String clazz = clazzes[i];

			ArrayList services = (ArrayList) publishedServicesByClass.get(clazz);

			if (services == null) {
				services = new ArrayList(10);
				publishedServicesByClass.put(clazz, services);
			}

			services.add(serviceReg);
		}

		// Add the ServiceRegistration to the list of all published Services.
		allPublishedServices.add(serviceReg);
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.ServiceRegistry#unpublishService(BundleContext, ServiceRegistration)
	 */
	public void unpublishService(BundleContext context, ServiceRegistration serviceReg) {

		// Remove the ServiceRegistration from the list of Services published by BundleContext.
		ArrayList contextServices = (ArrayList) publishedServicesByContext.get(context);
		if (contextServices != null) {
			contextServices.remove(serviceReg);
		}

		// Remove the ServiceRegistration from the list of Services published by Class Name.
		String[] clazzes = (String[]) serviceReg.getReference().getProperty(Constants.OBJECTCLASS);
		int size = clazzes.length;

		for (int i = 0; i < size; i++) {
			String clazz = clazzes[i];
			ArrayList services = (ArrayList) publishedServicesByClass.get(clazz);
			services.remove(serviceReg);
		}

		// Remove the ServiceRegistration from the list of all published Services.
		allPublishedServices.remove(serviceReg);

	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.ServiceRegistry#unpublishServices(BundleContext)
	 */
	public void unpublishServices(BundleContext context) {
		// Get all the Services published by the BundleContext.
		ArrayList serviceRegs = (ArrayList) publishedServicesByContext.get(context);
		if (serviceRegs != null) {
			// Remove this list for the BundleContext
			publishedServicesByContext.remove(context);
			int size = serviceRegs.size();
			for (int i = 0; i < size; i++) {
				ServiceRegistration serviceReg = (ServiceRegistration) serviceRegs.get(i);
				// Remove each service from the list of all published Services
				allPublishedServices.remove(serviceReg);

				// Remove each service from the list of Services published by Class Name. 
				String[] clazzes = (String[]) serviceReg.getReference().getProperty(Constants.OBJECTCLASS);
				int numclazzes = clazzes.length;

				for (int j = 0; j < numclazzes; j++) {
					String clazz = clazzes[j];
					ArrayList services = (ArrayList) publishedServicesByClass.get(clazz);
					services.remove(serviceReg);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.ServiceRegistry#lookupServiceReferences(String, Filter)
	 */
	public ServiceReference[] lookupServiceReferences(String clazz, Filter filter) {
		int size;
		ArrayList references;
		ArrayList serviceRegs;
		if (clazz == null) /* all services */
			serviceRegs = allPublishedServices;
		else
			/* services registered under the class name */
			serviceRegs = (ArrayList) publishedServicesByClass.get(clazz);

		if (serviceRegs == null)
			return (null);

		size = serviceRegs.size();

		if (size == 0)
			return (null);

		references = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			ServiceRegistration registration = (ServiceRegistration) serviceRegs.get(i);

			ServiceReference reference = registration.getReference();
			if ((filter == null) || filter.match(reference)) {
				references.add(reference);
			}
		}

		if (references.size() == 0) {
			return null;
		}

		return (ServiceReference[]) references.toArray(new ServiceReference[references.size()]);

	}

	/**
	 * @see org.eclipse.osgi.framework.adaptor.ServiceRegistry#lookupServiceReferences(BundleContext)
	 */
	public ServiceReference[] lookupServiceReferences(BundleContext context) {
		int size;
		ArrayList references;
		ArrayList serviceRegs = (ArrayList) publishedServicesByContext.get(context);

		if (serviceRegs == null) {
			return (null);
		}

		size = serviceRegs.size();

		if (size == 0) {
			return (null);
		}

		references = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			ServiceRegistration registration = (ServiceRegistration) serviceRegs.get(i);

			ServiceReference reference = registration.getReference();
			references.add(reference);
		}

		if (references.size() == 0) {
			return null;
		}

		return (ServiceReference[]) references.toArray(new ServiceReference[references.size()]);
	}

}
