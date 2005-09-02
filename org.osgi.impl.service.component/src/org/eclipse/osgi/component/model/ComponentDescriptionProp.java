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

package org.eclipse.osgi.component.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentInstance;

/**
 * 
 * Component Description with properties
 * 
 * Component Configuration – A component configuration represents a component
 * description parameterized by component properties.
 * 
 * @version $Revision$
 */
public class ComponentDescriptionProp {

	/* set this to true to compile in debug messages */
	static final boolean			DEBUG	= false;

	protected ComponentDescription	cd;
	protected Hashtable				properties;
	protected ServiceRegistration	serviceRegistration;

	protected List					delayActivateCDPNames;
	protected List					references;
	protected List					instances;

	boolean							componentFactory;

	/**
	 * @param bundle The bundle to set.
	 */
	public ComponentDescriptionProp(ComponentDescription cd, List references,
			Hashtable properties, boolean componentFactory) {

		this.cd = cd;
		this.references = references;
		this.properties = properties;

		delayActivateCDPNames = new ArrayList();
		instances = new ArrayList();

		this.componentFactory = componentFactory;
	}

	/**
	 * getProperties
	 * 
	 * @return Dictionary properties
	 */
	public Hashtable getProperties() {
		return properties;
	}

	/**
	 * getComponentDescription
	 * 
	 * @return ComponentDescription
	 */
	public ComponentDescription getComponentDescription() {
		return cd;
	}

	public void setDelayActivateCDPName(String cdpName) {
		if (!delayActivateCDPNames.contains(cdpName))
			delayActivateCDPNames.add(cdpName);
	}

	public ServiceRegistration getServiceRegistration() {
		return serviceRegistration;
	}

	public void setServiceRegistration(ServiceRegistration serviceRegistration) {
		this.serviceRegistration = serviceRegistration;
	}

	public List getDelayActivateCDPNames() {
		return delayActivateCDPNames;
	}

	public void clearDelayActivateCDPNames() {
		delayActivateCDPNames.clear();
	}

	public List getReferences() {
		return references;
	}

	public void addInstance(ComponentInstance instance) {
		instances.add(instance);
	}

	public List getInstances() {
		return instances;
	}

	public void removeInstance(Object object) {
		instances.remove(object);
	}

	public void removeAllInstances() {
		instances.clear();
	}

	public boolean isComponentFactory() {
		return componentFactory;
	}
}
