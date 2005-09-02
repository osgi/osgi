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
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * 
 * Memory model of the Service Component xml
 * 
 * @version $Revision$
 */
public class ComponentDescription {
	protected BundleContext				bundleContext;
	protected String					name;
	protected boolean					autoenable;
	protected boolean					immediate;
	protected boolean					enabled;
	protected boolean					satisfied;
	protected boolean					valid;
	protected String					factory;

	protected ImplementationDescription	implementation;
	protected List						properties;
	protected ServiceDescription		service;
	protected List						servicesProvided;

	protected List						referenceDescriptions;

	protected List						componentDescriptionProps;
	
	/**
	 * Map of Component Configurations created for this Service Component keyed
	 * by ConfigurationAdmin PID
	 */
	protected Map						cdpsByPID;

	/**
	 * Constructor
	 * 
	 * @param bundleContext The bundle to set.
	 */
	public ComponentDescription(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		autoenable = true;
		immediate = false;
		properties = new ArrayList();
		referenceDescriptions = new ArrayList();
		componentDescriptionProps = new ArrayList();
		cdpsByPID = new Hashtable();
		servicesProvided = Collections.EMPTY_LIST;

	}

	/**
	 * Return true if autoenable is set
	 * 
	 * @return Returns autoenable
	 */
	public boolean isAutoenable() {
		return autoenable;
	}

	/**
	 * @param autoenable The autoenable value to set.
	 */
	public void setAutoenable(boolean autoenable) {
		this.autoenable = autoenable;
	}

	/**
	 * @return Returns true if immediate is set
	 */
	public boolean isImmediate() {
		return immediate;
	}

	/**
	 * Set the specified value for immediate
	 * 
	 * @param immediate
	 */
	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}

	/**
	 * @return Returns the bundleContext
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * @return Returns the factory.
	 */
	public String getFactory() {
		return factory;
	}

	/**
	 * @param factory The factory to set.
	 */
	public void setFactory(String factory) {
		this.factory = factory;
	}

	/**
	 * @return Returns the implementation.
	 */
	public ImplementationDescription getImplementation() {
		return implementation;
	}

	/**
	 * @param implementation The implementation to set.
	 */
	public void setImplementation(ImplementationDescription implementation) {
		this.implementation = implementation;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the properties.
	 */
	public PropertyDescription[] getProperties() {
		int size = properties.size();
		PropertyDescription[] result = new PropertyDescription[size];
		properties.toArray(result);
		return result;
	}

	/**
	 * @param property The properties to set.
	 */
	public void addProperty(PropertyDescription property) {
		properties.add(property);
	}

	/**
	 * @return Returns the service.
	 */
	public ServiceDescription getService() {
		return service;
	}

	/**
	 * return a handly list of the serviceInterfaces provided
	 */
	public List getServicesProvided() {
		return servicesProvided;
	}

	/**
	 * @param service The service to set.
	 */
	public void setService(ServiceDescription service) {
		this.service = service;

		if (service != null) {
			servicesProvided = new ArrayList();
			ProvideDescription[] provideDescription = service.getProvides();
			for (int i = 0; i < provideDescription.length; i++) {
				servicesProvided.add(provideDescription[i].getInterfacename());
			}
		}
		else {
			servicesProvided = Collections.EMPTY_LIST;
		}

	}

	/**
	 * @return Returns the reference Descriptions.
	 */
	public List getReferenceDescriptions() {
		return referenceDescriptions;
	}

	/**
	 * @param reference The references to set.
	 */
	public void addReferenceDescription(ReferenceDescription reference) {
		referenceDescriptions.add(reference);
	}

	/**
	 * @return Returns true if enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled set the value
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return Returns true if satisfied
	 */
	synchronized public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * @param satisfied set the value
	 */
	synchronized public void setSatisfied(boolean eligible) {
		this.satisfied = eligible;
	}

	/**
	 * @return Returns true if valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid - set the value
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void addComponentDescriptionProp(ComponentDescriptionProp cdp) {
		componentDescriptionProps.add(cdp);
		String pid = (String) cdp.getProperties().get(Constants.SERVICE_PID);
		if (pid != null) {
			cdpsByPID.put(pid, cdp);
		}
	}

	
	public List getComponentDescriptionProps() {
		return componentDescriptionProps;
	}

	public ComponentDescriptionProp getComponentDescriptionPropByPID(String pid) {
		return (ComponentDescriptionProp) cdpsByPID.get(pid);
	}

	public void clearComponentDescriptionProps() {
		componentDescriptionProps.clear();
		cdpsByPID.clear();
	}

	public void removeComponentDescriptionProp(ComponentDescriptionProp cdp) {
		componentDescriptionProps.remove(cdp);
	}
}
