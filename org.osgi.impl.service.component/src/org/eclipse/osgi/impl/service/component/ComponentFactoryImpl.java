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
package org.eclipse.osgi.impl.service.component;

import java.util.Dictionary;
import org.eclipse.osgi.component.instance.InstanceProcess;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * When a component is declared with the <code>factory</code> attribute on its
 * <code>component</code> element, the Service Component Runtime will register
 * a ComponentFactory service to allow new component configurations to be
 * created and activated rather than automatically creating and activating
 * component configuration as necessary.
 * 
 * @version $Revision$
 */
public class ComponentFactoryImpl implements ComponentFactory {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = true;

	ComponentDescriptionProp componentDescriptionProp;
	ComponentDescription componentDescription;
	ServiceRegistration serviceRegistration;
	BundleContext bundleContext;
	InstanceProcess instanceProcess;
	boolean registerService = false;

	/**
	 * ComponentFactoryImpl
	 * 
	 * @param context the SC bundle context
	 * @param componentDescriptionProp the ComponentDescription Object with Properties
	 * @param buildDispose 
	 */
	public ComponentFactoryImpl(BundleContext bundleContext, ComponentDescriptionProp component, InstanceProcess instanceProcess) {
		this.componentDescriptionProp = component;
		this.componentDescription = component.getComponentDescription();
		this.bundleContext = bundleContext;
		this.instanceProcess = instanceProcess;

		//if the Component Description includes a Service 
		if (componentDescription.getService() != null)
			registerService = true;

	}

	/**
	 * Create and activate a new component configuration. Additional properties
	 * may be provided for the component configuration.
	 * 
	 * @param properties Additional properties for the component configuration.
	 * @return A ComponentInstance object encapsulating the component
	 *         configuration. The returned component configuration has been
	 *         activated and, if the component specifies a <code>service</code>
	 *         element, the component configuration has been registered as a
	 *         service.
	 * @throws ComponentException If the Service Component Runtime is unable to
	 *         activate the component configuration.
	 */
	public ComponentInstance newInstance(Dictionary properties) {

		ComponentInstance instance = null;
		try {
			instance = instanceProcess.buildDispose.build(null, componentDescriptionProp, properties);

			if (registerService) {
				
				serviceRegistration = instanceProcess.registerServices(bundleContext, componentDescriptionProp,properties);
				ComponentInstanceImpl componentInstanceImpl = (ComponentInstanceImpl) instance;
				componentInstanceImpl.setServiceRegistration(serviceRegistration);
			}
		} catch (ComponentException e) {
			System.err.println("Could not create instance of " + componentDescription + " with properties " + properties);
			e.printStackTrace();
			throw e;
		}

		return instance;
	}
}
