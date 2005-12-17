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
package org.eclipse.osgi.framework.adaptor.core;

import org.eclipse.osgi.util.NLS;

/**
 * Commom framework adaptor messages.
 * <p>
 * Clients may not extend this class
 * </p>
 * @since 3.1
 */
public class AdaptorMsg extends NLS {
	
	public static String ADAPTER_FILEEXIST_EXCEPTION;
	public static String ADAPTOR_DATA_AREA_NOT_SET;
	public static String ADAPTOR_DIRECTORY_CREATE_EXCEPTION;
	public static String ADAPTOR_DIRECTORY_EXCEPTION;
	public static String ADAPTOR_DIRECTORY_REMOVE_EXCEPTION;
	public static String ADAPTOR_ERROR_GETTING_MANIFEST;	
	public static String ADAPTOR_EXTENSION_IMPORT_ERROR;
	public static String ADAPTOR_EXTENSION_NATIVECODE_ERROR;
	public static String ADAPTOR_EXTENSION_REQUIRE_ERROR;
	public static String ADAPTOR_SAME_REF_UPDATE;
	public static String ADAPTOR_STORAGE_EXCEPTION;
	public static String ADAPTOR_URL_CREATE_EXCEPTION;	
	
	public static String BUNDLE_CLASSPATH_ENTRY_NOT_FOUND_EXCEPTION;	
	public static String BUNDLE_CLASSPATH_PROPERTIES_ERROR;	
	public static String BUNDLE_NATIVECODE_EXCEPTION;
	public static String BUNDLE_READ_EXCEPTION;
	
	public static String MANIFEST_NOT_FOUND_EXCEPTION;
	
	public static String RESOURCE_NOT_FOUND_EXCEPTION;
	
	public static String SYSTEMBUNDLE_MISSING_MANIFEST;
	
	public static String URL_INVALID_BUNDLE_ID;
	public static String URL_NO_BUNDLE_FOUND;	
	public static String URL_NO_BUNDLE_ID;

	private static final String BUNDLE_NAME = "org.eclipse.osgi.framework.adaptor.core.ExternalMessages"; //$NON-NLS-1$	
	
	static {
		// initialize resource bundles
		NLS.initializeMessages(BUNDLE_NAME, AdaptorMsg.class);
	}
	
}