/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.service.systembundle;

import java.net.URL;
import java.util.Locale;

/**
 * The System Bundle uses an EntryLocator service to find resource URLs 
 * to NLS properties files.  The System Bundle does not have the ablity
 * to host fragment bundles.  The EntryLocator service allows a bundle
 * installed in the Framework to provide NLS properties files to the 
 * System Bundle.
 */
public interface EntryLocator {
	/**
	 * Returns the URL for the specified properies file and locale.
	 * The value of null is returned if the properties file could not be
	 * found. 
	 * @param basename the base name of the properties file to get
	 * @param locale the locale to use when searching for the properites file
	 * @return the URL for the properties file or null if one could not be 
	 * found
	 */
	public URL getProperties(String basename, Locale locale);
}
