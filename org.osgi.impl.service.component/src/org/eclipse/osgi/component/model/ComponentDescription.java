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
 
package org.eclipse.osgi.component.model;

import java.util.ArrayList;

import org.osgi.framework.Bundle;

/**
 *
 *  Memory model of the Service Component xml
 * 
 * @version $Revision$
 */
public class ComponentDescription {
	protected Bundle bundle;
	protected String name;
	protected boolean autoenable;
	protected boolean enabled;
	protected boolean eligible;
	protected boolean reactivate;
	protected boolean valid;
	protected String factory;

	protected ImplementationDescription implementation;
	protected ArrayList properties;
	protected ServiceDescription service;
	protected ArrayList references;

	/**
	 * @param bundle The bundle to set.
	 */
	public ComponentDescription(Bundle bundle) {
		this.bundle = bundle;
		autoenable = true;
		reactivate = false;
		properties = new ArrayList();
		references = new ArrayList();
	}

	/**
	 * @return Returns the autoenable.
	 */
	public boolean isAutoenable() {
		return autoenable;
	}

	/**
	 * @param autoenable The autoenable to set.
	 */
	public void setAutoenable(boolean autoenable) {
		this.autoenable = autoenable;
	}

	/**
	 * @return Returns the bundle.
	 */
	public Bundle getBundle() {
		return bundle;
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
	 * @param service The service to set.
	 */
	public void setService(ServiceDescription service) {
		this.service = service;
	}

	/**
	 * @return Returns the references.
	 */
	public ReferenceDescription[] getReferences() {
		int size = references.size();
		ReferenceDescription[] result = new ReferenceDescription[size];
		references.toArray(result);
		return result;
	}

	/**
	 * @param reference The references to set.
	 */
	public void addReference(ReferenceDescription reference) {
		references.add(reference);
	}

	/**
	 * @return Returns true if enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enable set the value
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return Returns true if eligible
	 */
	synchronized public boolean isEligible() {
		return eligible;
	}

	/**
	 * @param eligible set the value
	 */
	synchronized public void setEligible(boolean eligible) {
		this.eligible = eligible;
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

	/**
	 * @param reactivate
	 */
	public void setReactivate(boolean reactivate) {
		this.reactivate = reactivate;
	}

	/**
	 * @return Returns true if needed to be reactivated
	 */
	public boolean isReactivate() {
		return reactivate;
	}

}
