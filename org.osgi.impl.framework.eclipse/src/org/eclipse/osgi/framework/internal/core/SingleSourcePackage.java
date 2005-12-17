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

public class SingleSourcePackage extends PackageSource {
	BundleLoaderProxy supplier;
	// this is the index of the ExportPackageDescription 
	// into the list of exported packages of the supplier
	// a valid of -1 indicates it is unknown or does not matter
	protected int expid;
	public SingleSourcePackage(String id, int expid, BundleLoaderProxy supplier) {
		super(id);
		this.expid = expid;
		this.supplier = supplier;
	}

	public SingleSourcePackage[] getSuppliers() {
		return new SingleSourcePackage[] {this};
	}

	public String toString() {
		return id + " -> " + supplier; //$NON-NLS-1$
	}

	public Class loadClass(String name) {
		return supplier.getBundleLoader().findLocalClass(name);
	}

	public URL getResource(String name) {
		return supplier.getBundleLoader().findLocalResource(name);
	}

	public Enumeration getResources(String name) {
		return supplier.getBundleLoader().findLocalResources(name);
	}

	public boolean equals(Object source) {
		if (this == source)
			return true;
		if (!(source instanceof SingleSourcePackage))
			return false;
		SingleSourcePackage singleSource = (SingleSourcePackage) source;
		return supplier == singleSource.supplier && expid == singleSource.expid;
	}
}
