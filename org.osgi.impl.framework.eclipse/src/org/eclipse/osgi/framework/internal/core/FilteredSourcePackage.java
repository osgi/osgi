/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
import org.eclipse.osgi.util.ManifestElement;

public class FilteredSourcePackage extends SingleSourcePackage {
	private static final char ALL = '*';
	String[] includes;
	String[] excludes;
	String[] friends;

	public FilteredSourcePackage(String name, int expid, BundleLoaderProxy supplier, String includes, String excludes, String[] friends) {
		super(name, expid, supplier);
		if (includes != null)
			this.includes = ManifestElement.getArrayFromList(includes);
		if (excludes != null)
			this.excludes = ManifestElement.getArrayFromList(excludes);
		this.friends = friends;
	}

	public boolean isFriend(String symbolicName) {
		if (friends == null)
			return true;
		for (int i = 0; i < friends.length; i++)
			if (friends[i].equals(symbolicName))
				return true;
		return false;
	}

	public URL getResource(String name) {
		if (isFiltered(name, getId()))
			return null;
		return super.getResource(name);
	}
	public Enumeration getResources(String name) {
		if (isFiltered(name, getId()))
			return null;
		return super.getResources(name);
	}
	public Class loadClass(String name) {
		if (isFiltered(name, getId()))
			return null;
		return super.loadClass(name);
	}

	private boolean isFiltered(String name, String pkgName) {
		String lastName = getName(name, pkgName);
		return !isIncluded(lastName) || isExcluded(lastName);
	}

	private String getName(String name, String pkgName) {
		if (!BundleLoader.DEFAULT_PACKAGE.equals(pkgName) && pkgName.length() + 1 <= name.length())
			return name.substring(pkgName.length() + 1);
		return name;
	}

	private boolean isIncluded(String name) {
		if (includes == null)
			return true;
		return isInList(name, includes);
	}

	private boolean isExcluded(String name) {
		if (excludes == null)
			return false;
		return isInList(name, excludes);
	}

	private boolean isInList(String name, String[] list) {
		for (int i = 0; i < list.length; i++) {
			int len = list[i].length();
			if (len == 0)
				continue;
			if (list[i].charAt(0) == ALL && len == 1)
				return true; // handles "*" wild card
			if (list[i].charAt(len-1) == ALL)
				if (name.startsWith(list[i].substring(0, len-1)))
					return true;
			if (name.equals(list[i]))
				return true;
			
		}
		return false;
	}
}
