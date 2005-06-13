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
package org.eclipse.osgi.internal.resolver;

import org.eclipse.osgi.util.NLS;

public class StateMsg extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.osgi.internal.resolver.StateMessages"; //$NON-NLS-1$

	public static String COMMIT_INVALID_TIMESTAMP;
	public static String HEADER_REQUIRED;
	public static String HEADER_PACKAGE_DUPLICATES;
	public static String HEADER_PACKAGE_JAVA;
	public static String HEADER_VERSION_ERROR;
	public static String HEADER_EXPORT_ATTR_ERROR;
	public static String HEADER_DIRECTIVE_DUPLICATES;
	public static String HEADER_REEXPORT_USES;

	static {
		// initialize resource bundles
		NLS.initializeMessages(BUNDLE_NAME, StateMsg.class);
	}
}