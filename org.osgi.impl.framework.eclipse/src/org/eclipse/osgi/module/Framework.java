/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.module;

import java.net.URL;

import org.eclipse.osgi.framework.internal.core.OSGi;
import org.osgi.framework.Bundle;

/**
 * This class provides a simple way of utilising the modularity features of
 * the OSGi framework. It supports non-intrusively overlaying existing jars with
 * a module structure.
 */
public class Framework {
	
	protected Adapter adapter;
	protected OSGi osgi;

	/**
	 * Create a module framework with the current class loader as its parent class loader.
	 * 
	 * @param args framework adapter arguments
	 */
	public Framework(String[] args) {
		adapter = new Adapter(args);
		osgi = new OSGi(adapter);
		osgi.launch();
	}

	/**
	 * Defines a bundle to the module framework. The bundle is not packaged as a jar file
	 * but is defined as a string manifest and a classpath directory which can be anywhere
	 * in the file system. This is useful for overlaying existing code with a module
	 * structure without having to re-package the code.
	 * 
	 * @param classPathDir a String containing the path relative to which the bundle's
	 *     classpath entries are defined
	 * @param manifest a String containing the manifest of the bundle
	 * @return the newly defined Bundle
	 */
	public Bundle defineBundle(String classPathDir, String manifest)
	{
		try {
			BundleManifest bundleManifest = new BundleManifest(manifest);
			String bsn = bundleManifest.getBundleSymbolicName();
			adapter.setManifest(bsn, bundleManifest.getManifest());
			URL url = new URL("reference:" + classPathDir);	
			return osgi.getBundleContext().installBundle(bsn, url.openStream());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
