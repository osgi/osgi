/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.net.URL;
import java.util.Enumeration;

/**
 * This class is used to optimize finding provided-packages for a bundle.
 * If the package cannot be found in a list of required bundles then this class
 * is used to cache a null package source so that the search does not need to
 * be done again.
 */
public class NullPackageSource extends PackageSource {
	static KeyedHashSet sources;
	private NullPackageSource(String name) {
		super(name);
	}

	public SingleSourcePackage[] getSuppliers() {
		return null;
	}

	public boolean isNullSource() {
		return true;
	}

	public String toString() {
		return id + " -> null"; //$NON-NLS-1$
	}

	public Class loadClass(String name) {
		return null;
	}

	public URL getResource(String name) {
		return null;
	}

	public Enumeration getResources(String name) {
		return null;
	}

	public static synchronized NullPackageSource getNullPackageSource(String name) {
		if (sources == null)
			sources = new KeyedHashSet();
		NullPackageSource result = (NullPackageSource) sources.getByKey(name);
		if (result != null)
			return result;
		result = new NullPackageSource(name);
		sources.add(result);
		return result;
	}
}
