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
import java.util.List;

/**
 * 
 * This class models the service element.
 * The service element is optional. It describes the service information to be
 * used when a component configuration is to be registered as a service. * 
 * 
 * @version $Revision$
 */
public class ServiceDescription {
	protected ComponentDescription	parent;
	protected boolean				servicefactory;
	protected List					provides;

	public ServiceDescription(ComponentDescription parent) {
		this.parent = parent;
		servicefactory = false;
		provides = new ArrayList();
	}

	/**
	 * @param servicefactory indicates if servicefactory is set
	 */
	public void setServicefactory(boolean servicefactory) {
		this.servicefactory = servicefactory;
	}

	/**
	 * @return Returns true is servicefactory is set
	 */
	public boolean isServicefactory() {
		return servicefactory;
	}

	/**
	 * @param provide add this provide element to the array of provide elements.
	 */
	public void addProvide(ProvideDescription provide) {
		provides.add(provide);
	}

	/**
	 * @return Returns the array of provide elements.
	 */
	public ProvideDescription[] getProvides() {
		int size = provides.size();
		ProvideDescription[] result = new ProvideDescription[size];
		provides.toArray(result);
		return result;
	}
}
