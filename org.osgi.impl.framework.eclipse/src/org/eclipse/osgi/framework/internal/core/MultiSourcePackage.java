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

public class MultiSourcePackage extends PackageSource {
	SingleSourcePackage[] suppliers;

	MultiSourcePackage(String id, SingleSourcePackage[] suppliers) {
		super(id);
		this.suppliers = suppliers;
	}

	public SingleSourcePackage[] getSuppliers() {
		return suppliers;
	}

	public Class loadClass(String name) {
		Class result = null;
		for (int i = 0; i < suppliers.length; i++) {
			result = suppliers[i].loadClass(name);
			if (result != null)
				return result;
		}
		return result;
	}

	public URL getResource(String name) {
		URL result = null;
		for (int i = 0; i < suppliers.length; i++) {
			result = suppliers[i].getResource(name);
			if (result != null)
				return result;
		}
		return result;
	}

	public Enumeration getResources(String name) {
		Enumeration result = null;
		for (int i = 0; i < suppliers.length; i++) {
			result = suppliers[i].getResources(name);
			if (result != null)
				return result;
		}
		return result;
	}
}
