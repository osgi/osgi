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

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ImplementationDescription {
	protected ComponentDescription parent;
	protected String classname;

	public ImplementationDescription(ComponentDescription parent) {
		this.parent = parent;
	}

	/**
	 * @return Returns the classname.
	 */
	public String getClassname() {
		return classname;
	}
	/**
	 * @param classname The classname to set.
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}
}
