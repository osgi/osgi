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

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ServiceDescription {
	protected ComponentDescription parent;
	protected boolean servicefactory;
	protected ArrayList provides;

	public ServiceDescription(ComponentDescription parent) {
		this.parent = parent;
		servicefactory = false;
		provides = new ArrayList();
	}
	/**
	 * @param servicefactory The servicefactory to set.
	 */
	public void setServicefactory(boolean servicefactory) {
		this.servicefactory = servicefactory;
	}
	/**
	 * @return Returns the servicefactory.
	 */
	public boolean isServicefactory() {
		return servicefactory;
	}

	public void addProvide(ProvideDescription provide) {
		provides.add(provide);
	}
	/**
	 * @return Returns the provides.
	 */
	public ProvideDescription[] getProvides() {
		int size = provides.size();
		ProvideDescription[] result = new ProvideDescription[size];
		provides.toArray(result);
		return result;
	}
}
