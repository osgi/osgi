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

public abstract class PackageSource implements KeyedElement {
	protected String id;

	public PackageSource(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public abstract SingleSourcePackage[] getSuppliers();

	public boolean compare(KeyedElement other) {
		return id.equals(((PackageSource) other).getId());
	}

	public int getKeyHashCode() {
		return id.hashCode();
	}

	public Object getKey() {
		return id;
	}

	public boolean isNullSource() {
		return false;
	}

	public abstract Class loadClass(String name, String pkgName);
	public abstract URL getResource(String name, String pkgName);
	public abstract Enumeration getResources(String name, String pkgName) throws IOException;

	//TODO This does not handle properly the multiple source package properly
	//TODO See how this relates with FilteredSourcePackage. Overwriting or doing a double dispatch might be good.
	public boolean hasCommonSource(PackageSource other) {
		if (other == null)
			return false;
		if (this == other)
			return true;
		SingleSourcePackage[] suppliers1 = getSuppliers();
		SingleSourcePackage[] suppliers2 = other.getSuppliers();
		if (suppliers1 == null || suppliers2 == null)
			return false;
		for (int i = 0; i < suppliers1.length; i++) {
			for (int j = 0; j < suppliers2.length; j++)
				if (suppliers1[i].supplier == suppliers2[j].supplier)
					return true;
		}
		return false;
	}
}
