/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.osgi.framework.BundleException;

/**
 * The System Bundle's BundleLoader.  This BundleLoader is used by ImportClassLoaders
 * to load a resource that is exported by the System Bundle.
 */
public class SystemBundleLoader extends BundleLoader {

	ClassLoader classLoader;

	/**
	 * List of packages that the system bundle exports.
	 */
	static protected String[] systemPackages = null;

	protected synchronized static void clearSystemPackages() {
		systemPackages = null;
	}

	protected static void setSystemPackages(String[] value) {
		systemPackages = value;
	}

	protected static String[] getSystemPackages() {
		return systemPackages;
	}

	/**
	 * @param bundle The system bundle.
	 * @param proxy The BundleLoaderProxy for the system bundle
	 * @throws BundleException On any error.
	 */
	protected SystemBundleLoader(BundleHost bundle, BundleLoaderProxy proxy) throws BundleException {
		super(bundle, proxy);
		this.classLoader = getClass().getClassLoader();
	}

	/**
	 * The ClassLoader that loads OSGi framework classes is used to find the class.
	 */
	public Class findClass(String name) throws ClassNotFoundException {
		return classLoader.loadClass(name);
	}

	/**
	 * This method will always return null.
	 */
	public String findLibrary(String name) {
		return null;
	}

	/**
	 * The ClassLoader that loads OSGi framework classes is used to find the class. 
	 */
	protected Class findLocalClass(String name) {
		Class clazz = null;
		try {
			clazz = classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			// Do nothing, will return null
		}
		return clazz;
	}

	/**
	 * The ClassLoader that loads OSGi framework classes is used to find the resource.
	 */
	protected URL findLocalResource(String name) {
		return classLoader.getResource(name);
	}

	/**
	 * The ClassLoader that loads OSGi framework classes is used to find the resource.
	 */
	protected Enumeration findLocalResources(String name) {
		try {
			return classLoader.getResources(name);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * This is not supported by the System Bundle.
	 */
	public Object findObject(String object) {
		return null;
	}

	/**
	 * This is not supported by the System Bundle.
	 */
	protected Object findLocalObject(String object) {
		return null;
	}

	/**
	 * The ClassLoader that loads OSGi framework classes is used to find the resource.
	 */
	public URL findResource(String name) {
		return classLoader.getResource(name);
	}

	/**
	 * The ClassLoader that loads OSGi framework classes is used to find the resource.
	 */
	public Enumeration findResources(String name) throws IOException {
		return classLoader.getResources(name);
	}

	/**
	 * Do nothing on a close.
	 */
	protected void close() {
		// Do nothing.
	}

}