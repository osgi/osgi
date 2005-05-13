/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.component;

/**
 * Defines standard names for Service Component constants.
 * 
 * @version $Revision$
 */
public interface ComponentConstants {
	/**
	 * Manifest header (named &quot;Service-Component&quot;) specifying the XML
	 * documents within a bundle that contain the bundle's Service Component
	 * descriptions.
	 * <p>
	 * The attribute value may be retrieved from the <code>Dictionary</code>
	 * object returned by the <code>Bundle.getHeaders</code> method.
	 */
	public static final String	SERVICE_COMPONENT		= "Service-Component";

	/**
	 * A component property for a Service Component that contains the name of
	 * the Service Component as specified in the <code>name</code> attribute
	 * of the <code>component</code> element. The type of this property must
	 * be <code>String</code>.
	 */
	public final static String	COMPONENT_NAME			= "component.name";

	/**
	 * A component property for a Service Component that contains the generated
	 * id for the Service Component instance. The type of this property must be
	 * <code>Long</code>.
	 * 
	 * <p>
	 * The value of this property is assigned by the Service Component Runtime
	 * when a component instance is created. The Service Component Runtime
	 * assigns a unique value that is larger than all previously assigned values
	 * since the Service Component Runtime was started. These values are NOT
	 * persistent across restarts of the Service Component Runtime.
	 */
	public final static String	COMPONENT_ID			= "component.id";

	/**
	 * A service registration property for a Service Component Factory that
	 * contains the value of the <code>factory</code> attribute. The type of
	 * this property must be <code>String</code>.
	 */
	public final static String	COMPONENT_FACTORY		= "component.factory";

	/**
	 * A suffix for a component property for a reference target. It contains the
	 * filter to select the target services for a reference. The type of this
	 * property must be <code>String</code>.
	 */
	public final static String	REFERENCE_TARGET_SUFFIX	= ".target";
}