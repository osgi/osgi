/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import org.eclipse.osgi.util.NLS;

public class Msg extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.osgi.framework.internal.core.ExternalMessages"; //$NON-NLS-1$

	public static String BUNDLE_CLASSPATH_ENTRY_NOT_FOUND_EXCEPTION;
	public static String MANIFEST_INVALID_HEADER_EXCEPTION;
	public static String BUNDLE_STATE_CHANGE_EXCEPTION;
	public static String BUNDLE_UNINSTALLED_EXCEPTION;
	public static String BUNDLE_UNRESOLVED_EXCEPTION;
	public static String BUNDLE_UNRESOLVED_STATE_CONFLICT;
	public static String BUNDLE_UNRESOLVED_NOT_CHOSEN_EXCEPTION;
	public static String BUNDLE_UNRESOLVED_PACKAGE;
	public static String BUNDLE_UNRESOLVED_HOST;
	public static String BUNDLE_UNRESOLVED_BUNDLE;
	public static String BUNDLE_UNRESOLVED_UNSATISFIED_CONSTRAINT_EXCEPTION;
	public static String BUNDLE_INVALID_ACTIVATOR_EXCEPTION;
	public static String BUNDLE_EXTENSION_PERMISSION;
	
	public static String SERVICE_ARGUMENT_NULL_EXCEPTION;
	public static String SERVICE_EMPTY_CLASS_LIST_EXCEPTION;
	public static String SERVICE_NOT_INSTANCEOF_CLASS_EXCEPTION;
	public static String BUNDLE_ACTIVATOR_EXCEPTION;
	public static String BUNDLE_CONTEXT_INVALID_EXCEPTION;
	
	public static String BUNDLE_READ_EXCEPTION;
	public static String BUNDLE_FRAGMENT_CNFE;
	public static String BUNDLE_FRAGMENT_START;
	public static String BUNDLE_FRAGMENT_STOP;
	
	public static String BUNDLE_CNFE_NOT_RESOLVED;
	public static String BUNDLE_LOADER_ATTACHMENT_ERROR;
	
	public static String BUNDLE_FRAGMENT_IMPORT_CONFLICT;
	public static String BUNDLE_NO_CLASSPATH_MATCH;
	public static String FILTER_INVALID;
	
	public static String BUNDLE_NATIVECODE_INVALID_FILTER;
	
	public static String FILTER_TERMINATED_ABRUBTLY;
	public static String FILTER_TRAILING_CHARACTERS;
	public static String FILTER_MISSING_LEFTPAREN;
	public static String FILTER_MISSING_RIGHTPAREN;
	public static String FILTER_INVALID_OPERATOR;
	public static String FILTER_MISSING_ATTR;
	public static String FILTER_INVALID_VALUE;
	public static String FILTER_MISSING_VALUE;
	
	public static String OSGI_SYSTEMBUNDLE_CREATE_EXCEPTION;
	public static String BUNDLE_INSTALL_RECURSION_EXCEPTION;
	public static String BUNDLE_NATIVECODE_MATCH_EXCEPTION;
	public static String BUNDLE_INSTALL_REQUIRED_EE_EXCEPTION;
	public static String BUNDLE_INSTALL_SAME_UNIQUEID;
	
	public static String ECLIPSE_OSGI_NAME;
	public static String ECLIPSE_OSGI_VERSION;
	public static String OSGI_VERSION;
	public static String ECLIPSE_COPYRIGHT;
	
	public static String OSGI_INTERNAL_ERROR;
	public static String BUNDLE_NOT_IN_FRAMEWORK;
	public static String BUNDLE_REFRESH_FAILURE;
	public static String OSGI_SYSTEMBUNDLE_DESCRIPTION_ERROR;
	
	public static String SERVICE_ALREADY_UNREGISTERED_EXCEPTION;
	
	public static String SERVICE_FACTORY_EXCEPTION;
	public static String SERVICE_OBJECT_NULL_EXCEPTION;
	
	public static String PROPERTIES_INVALID_FW_STARTLEVEL;
	public static String STARTLEVEL_EXCEPTION_INVALID_REQUESTED_STARTLEVEL;
	public static String STARTLEVEL_CANT_CHANGE_SYSTEMBUNDLE_STARTLEVEL;
	
	public static String BUNDLE_SYSTEMBUNDLE_UNINSTALL_EXCEPTION;
	
	public static String LAUNCHER_ADAPTOR_ERROR;
	public static String LAUNCHER_COMPONENT_JAR;
	public static String LAUNCHER_COMPONENT_MISSING;
	public static String LAUNCHER_INVALID_PORT;
	
	public static String HEADER_DUPLICATE_KEY_EXCEPTION;
	public static String MANIFEST_INVALID_SPACE;
	public static String MANIFEST_INVALID_LINE_NOCOLON;
	public static String MANIFEST_IOEXCEPTION;

	static {
		// initialize resource bundles
		NLS.initializeMessages(BUNDLE_NAME, Msg.class);
	}
}