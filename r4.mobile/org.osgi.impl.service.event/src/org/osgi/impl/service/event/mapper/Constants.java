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
package org.osgi.impl.service.event.mapper;

/**
 * @version $Revision$
 */
public interface Constants {
	// constants for Event common properties; event specific properties are
	// defined in the corresponding event adapter.
	public static final String	BUNDLE				= "bundle";
	public static final String	BUNDLE_ID			= "bundle.id";
	public static final String	BUNDLE_SYMBOLICNAME	= "bundle.symbolicName";
	public static final String	EVENT				= "event";
	public static final String	EXCEPTION			= "exception";
	public static final String	EXCEPTION_CLASS		= "exception.class";
	public static final String	EXCEPTION_MESSAGE	= "exception.message";
	public static final String	MESSAGE				= "message";
	public static final String	SERVICE				= "service";
	public static final String	SERVICE_ID			= "service.id";
	public static final String	SERVICE_OBJECTCLASS	= "service.objectClass";
	public static final String	SERVICE_PID			= "service.pid";
	public static final String	TIMESTAMP			= "timestamp";
	public static final char	TOPIC_SEPARATOR		= '/';
}