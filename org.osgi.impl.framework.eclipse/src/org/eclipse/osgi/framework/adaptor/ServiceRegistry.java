/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor;

import org.osgi.framework.*;

/**
 * The ServiceRegistry interface is used by the framework to store and
 * lookup currently registered services.
 */
public interface ServiceRegistry {

	/**
	 * Publishes a service to this ServiceRegistry.
	 * @param context the BundleContext that registered the service.
	 * @param serviceReg the ServiceRegistration to register.
	 */
	public void publishService(BundleContext context, ServiceRegistration serviceReg);

	/**
	 * Unpublishes a service from this ServiceRegistry
	 * @param context the BundleContext that registered the service. 
	 * @param serviceReg the ServiceRegistration to unpublish.
	 */
	public void unpublishService(BundleContext context, ServiceRegistration serviceReg);

	/**
	 * Unpublishes all services from this ServiceRegistry that the
	 * specified BundleContext registered.
	 * @param context the BundleContext to unpublish all services for.
	 */
	public void unpublishServices(BundleContext context);

	/**
	 * Performs a lookup for ServiceReferences that are bound to this 
	 * ServiceRegistry. If both clazz and filter are null then all bound
	 * ServiceReferences are returned.
	 * @param clazz A fully qualified class name.  All ServiceReferences that
	 * reference an object that implement this class are returned.  May be
	 * null.
	 * @param filter Used to match against published Services.  All 
	 * ServiceReferences that match the filter are returned.  If a clazz is
	 * specified then all ServiceReferences that match the clazz and the
	 * filter parameter are returned. May be null.
	 * @return An array of all matching ServiceReferences or null
	 * if none exist.
	 */
	public ServiceReference[] lookupServiceReferences(String clazz, Filter filter);

	/**
	 * Performs a lookup for ServiceReferences that are bound to this 
	 * ServiceRegistry using the specified BundleContext.
	 * @param context The BundleContext to lookup the ServiceReferences on.
	 * @return An array of all matching ServiceReferences or null if none
	 * exist.
	 */
	public ServiceReference[] lookupServiceReferences(BundleContext context);

}
