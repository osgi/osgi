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
package org.eclipse.osgi.internal.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.osgi.service.resolver.BundleDescription;

public class VersionHashMap {
	private HashMap internal = new HashMap();

	Object put(VersionSupplier versionSupplier) {
		VersionSupplier[] existing = (VersionSupplier[]) internal.get(versionSupplier.getName());
		if (existing == null) {
			VersionSupplier[] vss = new VersionSupplier[1];
			vss[0] = versionSupplier;
			internal.put(versionSupplier.getName(), vss);
		} else {
			for (int i = 0; i < existing.length; i++) {
				VersionSupplier e = existing[i];
				if (e == versionSupplier)
					return e;
				if (versionSupplier.getBundle().isResolved() && !e.getBundle().isResolved()) {
					// Put resolved bundles ahead of unresolved bundles
					internal.put(versionSupplier.getName(), add(i, versionSupplier, existing));
					return null;
				} else if (versionSupplier.getBundle().isResolved() == e.getBundle().isResolved()) {
					if (versionSupplier.getVersion().compareTo(e.getVersion()) > 0) {
						// Put highest versions first
						internal.put(versionSupplier.getName(), add(i, versionSupplier, existing));
						return null;
					} else if (e.getVersion().equals(versionSupplier.getVersion())) {
						if (versionSupplier.getBundle().getBundleId() < e.getBundle().getBundleId()) {
							// Versions match - order by bundle ID
							internal.put(versionSupplier.getName(), add(i, versionSupplier, existing));
							return e;
						}
					}
				}
			}
			// Lowest version, so add at end
			internal.put(versionSupplier.getName(), add(existing.length, versionSupplier, existing));
		}
		return null;
	}

	void put(VersionSupplier[] versionSuppliers) {
		for (int i = 0; i < versionSuppliers.length; i++) {
			put(versionSuppliers[i]);
		}
	}

	private VersionSupplier[] add(int index, VersionSupplier versionSupplier, VersionSupplier[] existing) {
		VersionSupplier[] newVS = new VersionSupplier[existing.length + 1];
		for (int i = 0; i < index; i++) {
			newVS[i] = existing[i];
		}
		newVS[index] = versionSupplier;
		for (int i = index + 1; i < newVS.length; i++) {
			newVS[i] = existing[i - 1];
		}
		return newVS;
	}

	VersionSupplier[] getArray(String supplierName) {
		VersionSupplier[] existing = (VersionSupplier[]) internal.get(supplierName);
		if (existing != null)
			return existing;
		else
			return new VersionSupplier[0];
	}

	boolean contains(VersionSupplier vs) {
		VersionSupplier[] existing = (VersionSupplier[]) internal.get(vs.getName());
		if (existing == null)
			return false;
		for (int i = 0; i < existing.length; i++) {
			if (existing[i] == vs)
				return true;
		}
		return false;
	}

	private void remove(VersionSupplier[] existing, String name, int index) {
		if (existing.length == 1) {
			internal.remove(name);
		} else {
			VersionSupplier[] newVS = new VersionSupplier[existing.length - 1];
			for (int i = 0; i < index; i++) {
				newVS[i] = existing[i];
			}
			for (int i = index + 1; i < existing.length; i++) {
				newVS[i - 1] = existing[i];
			}
			internal.put(name, newVS);
		}
	}

	Object remove(VersionSupplier toBeRemoved) {
		VersionSupplier[] existing = (VersionSupplier[]) internal.get(toBeRemoved.getName());
		if (existing != null) {
			for (int i = 0; i < existing.length; i++) {
				if (toBeRemoved == existing[i]) {
					remove(existing, toBeRemoved.getName(), i);
					return toBeRemoved;
				}
			}
		}
		return null;
	}

	void remove(VersionSupplier[] versionSuppliers) {
		for (int i = 0; i < versionSuppliers.length; i++) {
			remove(versionSuppliers[i]);
		}
	}

	// Once we have resolved bundles, we need to make sure that exports
	// from these are ahead of those from unresolved bundles
	void reorder() {
		Iterator it = internal.values().iterator();
		while (it.hasNext()) {
			ArrayList toBeReordered = new ArrayList();
			VersionSupplier[] existing = (VersionSupplier[]) it.next();
			if (existing == null || existing.length <= 1)
				continue;
			// Find any VersionSuppliers that need to be reordered
			VersionSupplier vs1 = (VersionSupplier) existing[0];
			for (int i = 1; i < existing.length; i++) {
				VersionSupplier vs2 = (VersionSupplier) existing[i];
				BundleDescription b1 = vs1.getBundle();
				BundleDescription b2 = vs2.getBundle();
				if (b2.isResolved() && !b1.isResolved()) {
					toBeReordered.add(vs2);
				} else if (b2.isResolved() == b1.isResolved()) {
					int versionDiff = vs2.getVersion().compareTo(vs1.getVersion());
					if (versionDiff > 0 || (b2.getBundleId() < b1.getBundleId() && versionDiff == 0)) {
						toBeReordered.add(vs2);
					}
				}
				vs1 = vs2;
			}
			// Reorder them
			for (int i = 0; i < toBeReordered.size(); i++) {
				VersionSupplier vs = (VersionSupplier) toBeReordered.get(i);
				remove(vs);
				put(vs);
			}
		}
	}

}
