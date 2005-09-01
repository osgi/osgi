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

package org.eclipse.osgi.component.parser;

/**
 * 
 * Define Constants
 * 
 * @version $Revision$
 */

public interface ParserConstants {

	// Define the Service Component XML Tags
	static final String	SCR_NAMESPACE				= "http://www.osgi.org/xmlns/scr/v1.0.0";
	static final String	COMPONENT_ELEMENT			= "component";
	static final String	IMPLEMENTATION_ELEMENT		= "implementation";
	static final String	REFERENCE_ELEMENT			= "reference";
	static final String	SERVICE_ELEMENT				= "service";
	static final String	PROPERTY_ELEMENT			= "property";
	static final String	PROPERTIES_ELEMENT			= "properties";
	static final String	NAME_ATTRIBUTE				= "name";
	static final String	IMMEDIATE_ATTRIBUTE			= "immediate";
	static final String	VALUE_ATTRIBUTE				= "value";
	static final String	TYPE_ATTRIBUTE				= "type";
	static final String	ENTRY_ATTRIBUTE				= "entry";
	static final String	PROVIDE_ELEMENT				= "provide";
	static final String	ENABLED_ATTRIBUTE			= "enabled";
	static final String	FACTORY_ATTRIBUTE			= "factory";
	static final String	SERVICEFACTORY_ATTRIBUTE	= "servicefactory";
	static final String	CLASS_ATTRIBUTE				= "class";
	static final String	INTERFACE_ATTRIBUTE			= "interface";
	static final String	CARDINALITY_ATTRIBUTE		= "cardinality";
	static final String	POLICY_ATTRIBUTE			= "policy";
	static final String	TARGET_ATTRIBUTE			= "target";
	static final String	BIND_ATTRIBUTE				= "bind";
	static final String	UNBIND_ATTRIBUTE			= "unbind";

	/* SAX Parser class name */
	static final String	SAX_FACTORY_CLASS			= "javax.xml.parsers.SAXParserFactory";	//$NON-NLS-1$
}
