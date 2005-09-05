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

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * When a Service Component is declared with the <code>factory</code> attribute 
 * on its <code>component</code> element, the Service Component Runtime will 
 * register a ComponentFactory service to allow new component configurations 
 * to be created and activated.
 * 
 * @version $Revision$
 */
public class ComponentFactoryImpl implements ComponentFactory {

	/* set this to true to compile in debug messages */
	static final boolean				DEBUG	= true;

	private ComponentDescriptionProp	cdp;
	private Main						main;

	/**
	 * ComponentFactoryImpl
	 * 
	 * @param context the SC bundle context
	 * @param componentDescriptionProp the ComponentDescription Object with
	 *        Properties
	 * @param buildDispose
	 */
	public ComponentFactoryImpl(ComponentDescriptionProp cdp, Main main) {
		this.cdp = cdp;
		this.main = main;
	}

	/**
	 * Create and activate a new component configuration. Additional properties
	 * may be provided for the component configuration.
	 * 
	 * @param properties Additional properties for the component configuration.
	 * @return A ComponentInstance object encapsulating an instance of the 
	 *         component configuration. The returned Component Configuration 
	 *         instance has been activated and, if the Service Component 
	 *         specifies a <code>service</code> element, the Component 
	 *         Configuration has been registered as a service.
	 * @throws ComponentException If the Service Component Runtime is unable to
	 *         activate the Component Configuration instance.
	 */
	public ComponentInstance newInstance(Dictionary newProperties) {

		// merge properties
		Hashtable properties = (Hashtable) cdp.getProperties().clone();
		Enumeration propsEnum = newProperties.keys();
		while (propsEnum.hasMoreElements()) {
			Object key = propsEnum.nextElement();
			properties.put(key, newProperties.get(key));
		}

		// create a new cdp (adds to resolver enabledCDPs list)
		ComponentDescriptionProp newCDP = main.resolver.mapFactoryInstance(cdp
				.getComponentDescription(), properties);

		// try to resolve new cdp - adds to resolver's satisfied list
		if (!main.resolver.justResolve(newCDP)) {
			main.resolver.enabledCDPs.remove(newCDP); // was added by
														// mapFactoryInstance
			throw new ComponentException("Could not resolve instance of " + cdp
					+ " with properties " + properties);
		}

		// if new cdp resolves, send it to instance process (will register
		// service
		// if it has one)
		main.resolver.instanceProcess.registerComponentConfigs(Collections
				.singletonList(newCDP));

		// get instance of new cdp to return

		if (newCDP.getComponentDescription().isImmediate()) {
			// if cdp is immediate then instanceProcess created one
			return (ComponentInstance) newCDP.getInstances().get(0);
		}

		return main.resolver.instanceProcess.buildDispose.buildComponentConfigInstance(null, newCDP);
	}
}
