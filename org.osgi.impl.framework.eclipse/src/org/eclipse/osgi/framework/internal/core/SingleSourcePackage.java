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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class SingleSourcePackage extends PackageSource {
	BundleLoaderProxy supplier;

	public SingleSourcePackage(String id, BundleLoaderProxy supplier) {
		super(id);
		this.supplier = supplier;
	}

	public SingleSourcePackage[] getSuppliers() {
		return new SingleSourcePackage[] {this};
	}

	public String toString() {
		return id + " -> " + supplier; //$NON-NLS-1$
	}

	public Class loadClass(String name, String pkgName) {
		return supplier.getBundleLoader().findLocalClass(name);
	}

	public URL getResource(String name, String pkgName) {
		return supplier.getBundleLoader().findLocalResource(name);
	}

	public Enumeration getResources(String name, String pkgName) throws IOException {
		return supplier.getBundleLoader().findLocalResources(name);
	}
}
