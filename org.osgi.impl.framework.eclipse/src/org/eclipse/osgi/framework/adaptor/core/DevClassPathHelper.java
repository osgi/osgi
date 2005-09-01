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
package org.eclipse.osgi.framework.adaptor.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.eclipse.osgi.util.ManifestElement;

/**
 * This class provides helper methods to support developement classpaths.
 * @since 3.1
 */
public final class DevClassPathHelper {
	static private boolean inDevelopmentMode = false;
	static private String[] devDefaultClasspath;
	static private Dictionary devProperties = null;

	static {
		// Check the osgi.dev property to see if dev classpath entries have been defined.
		String osgiDev = System.getProperty("osgi.dev"); //$NON-NLS-1$
		if (osgiDev != null) {
			try {
				inDevelopmentMode = true;
				URL location = new URL(osgiDev);
				devProperties = load(location);
				if (devProperties != null)
					devDefaultClasspath = getArrayFromList((String) devProperties.get("*")); //$NON-NLS-1$
			} catch (MalformedURLException e) {
				devDefaultClasspath = getArrayFromList(osgiDev);
			}
		}
	}

	private static String[] getDevClassPath(String id, Dictionary properties, String[] defaultClasspath) {
		String[] result = null;
		if (id != null && properties != null) {
			String entry = (String) properties.get(id);
			if (entry != null)
				result = getArrayFromList(entry);
		}
		if (result == null)
			result = defaultClasspath;
		return result;
	}

	/**
	 * Returns a list of classpath elements for the specified bundle symbolic name.
	 * @param id a bundle symbolic name to get the development classpath for
	 * @param properties a Dictionary of properties to use or <code>null</code> if
	 * the default develoment classpath properties should be used
	 * @return a list of development classpath elements
	 */
	public static String[] getDevClassPath(String id, Dictionary properties) {
		if (properties == null)
			return getDevClassPath(id, devProperties, devDefaultClasspath);
		return getDevClassPath(id, properties, getArrayFromList((String) properties.get("*"))); //$NON-NLS-1$
	}

	/**
	 * Returns a list of classpath elements for the specified bundle symbolic name.
	 * @param id a bundle symbolic name to get the development classpath for
	 * @return a list of development classpath elements
	 */
	public static String[] getDevClassPath(String id) {
		return getDevClassPath(id, null);
	}

	/**
	 * Returns the result of converting a list of comma-separated tokens into an array
	 * 
	 * @return the array of string tokens
	 * @param prop the initial comma-separated string
	 */
	public static String[] getArrayFromList(String prop) {
		return ManifestElement.getArrayFromList(prop, ","); //$NON-NLS-1$
	}

	/**
	 * Indicates the development mode.
	 * @return true if in development mode; false otherwise
	 */
	public static boolean inDevelopmentMode() {
		return inDevelopmentMode;
	}

	/*
	 * Load the given properties file
	 */
	private static Properties load(URL url) {
		Properties props = new Properties();
		try {
			InputStream is = null;
			try {
				is = url.openStream();
				props.load(is);
			} finally {
				if (is != null)
					is.close();
			}
		} catch (IOException e) {
			// TODO consider logging here
		}
		return props;
	}
}
