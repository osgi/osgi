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

import java.net.URL;
import java.util.Enumeration;

/**
 * This class is used to optimize finding provided-packages for a bundle.
 * If the package cannot be found in a list of required bundles then this class
 * is used to cache a null package source so that the search does not need to
 * be done again.
 */
public class NullPackageSource extends PackageSource {
	public NullPackageSource(String name) {
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

	public Class loadClass(String name, String pkgName, boolean providePkg) {
		return null;
	}

	public URL getResource(String name, String pkgName, boolean providePkg) {
		return null;
	}

	public Enumeration getResources(String name, String pkgName, boolean providePkg) {
		return null;
	}

	public Object getObject(String name, String pkgName, boolean providePkg) {
		return null;
	}
}