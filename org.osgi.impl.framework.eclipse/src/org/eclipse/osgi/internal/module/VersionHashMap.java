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
package org.eclipse.osgi.internal.module;

import java.util.*;

public class VersionHashMap extends MappedList implements Comparator {

	// sorts using the Comparator#compare method to sort
	protected void sort(Object[] values) {
		Arrays.sort(values, this);
	}

	public void put(VersionSupplier[] versionSuppliers) {
		for (int i = 0; i < versionSuppliers.length; i++)
			put(versionSuppliers[i].getName(), versionSuppliers[i]);
	}

	public void put(Object key, Object value) {
		super.put(key, value);
		((VersionSupplier) value).setDropped(false);
	}

	public boolean contains(VersionSupplier vs) {
		return contains(vs, false) != null;
	}

	private VersionSupplier contains(VersionSupplier vs, boolean remove) {
		Object[] existing = (Object[]) internal.get(vs.getName());
		if (existing == null)
			return null;
		for (int i = 0; i < existing.length; i++)
			if (existing[i] == vs) {
				if (remove) {
					vs.setDropped(true);
					if (existing.length == 1) {
						internal.remove(vs.getName());
						return vs;
					}
					Object[] newExisting = new Object[existing.length - 1];
					System.arraycopy(existing, 0, newExisting, 0, i);
					if (i + 1 < existing.length)
						System.arraycopy(existing, i + 1, newExisting, i, existing.length - i - 1);
					internal.put(vs.getName(), newExisting);
				}
				return vs;
			}
		return null;
	}

	public Object remove(VersionSupplier toBeRemoved) {
		return contains(toBeRemoved, true);
	}

	public void remove(VersionSupplier[] versionSuppliers) {
		for (int i = 0; i < versionSuppliers.length; i++)
			remove(versionSuppliers[i]);
	}

	public Object[] remove(Object key) {
		Object[] results = super.remove(key);
		for (int i = 0; i < results.length; i++)
			((VersionSupplier) results[i]).setDropped(true);
		return results;
	}

	// Once we have resolved bundles, we need to make sure that version suppliers
	// from the resolved bundles are ahead of those from unresolved bundles
	void reorder() {
		for (Iterator it = internal.values().iterator(); it.hasNext();) {
			Object[] existing = (Object[]) it.next();
			if (existing.length <= 1)
				continue;
			sort(existing);
		}
	}

	// Compares two VersionSuppliers for descending ordered sorts.
	// The VersionSuppliers are sorted by the following priorities
	// First the resolution status of the supplying bundle.
	// Second is the supplier version.
	// Third is the bundle id of the supplying bundle.
	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof VersionSupplier) || !(o2 instanceof VersionSupplier))
			throw new IllegalArgumentException();
		VersionSupplier vs1 = (VersionSupplier) o1;
		VersionSupplier vs2 = (VersionSupplier) o2;
		if (vs1.getBundle().isResolved() != vs2.getBundle().isResolved())
			return vs1.getBundle().isResolved() ? -1 : 1;
		int versionCompare = -(vs1.getVersion().compareTo(vs2.getVersion()));
		if (versionCompare != 0)
			return versionCompare;
		return vs1.getBundle().getBundleId() < vs2.getBundle().getBundleId() ? -1 : 1;
	}
}
