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

package org.eclipse.osgi.impl.service.component;

import java.util.Dictionary;
import org.eclipse.osgi.component.instance.InstanceProcess;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * When a component is declared with the <code>factory</code> attribute on it's
 * <code>component</code> element, the Service Component Runtime will register a
 * ComponentFactory service to allow instances of the component to be created
 * rather than automatically create component instances as necessary.
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

		if (componentDescription.getService() != null)
			registerService = true;
	}

	/**
	 * Create a new instance of the component. Additional properties may be
	 * provided for the component instance.
	 * 
	 * @param properties Additional properties for the component instance.
	 * @return A ComponentInstance object encapsulating the component instance.
	 *         The returned component instance has been activated.
	 */
	public ComponentInstance newInstance(Dictionary properties) {

		ComponentInstance instance = null;
		try {
			instanceProcess.buildDispose.build(bundleContext, null, componentDescriptionProp, null, properties);

			if (registerService)
				instanceProcess.registerServices(bundleContext, componentDescriptionProp);
		} catch (Exception e) {
			//TODO what to do here?  we need to add throws to the interface
			System.err.println("Could not create instance of " + componentDescription + " with properties " + properties);
		}

		return instance;
	}
}
