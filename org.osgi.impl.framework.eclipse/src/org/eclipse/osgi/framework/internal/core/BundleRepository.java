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

import java.util.*;
import org.osgi.framework.Version;

/**
 * The BundleRepository holds all installed Bundle object for the
 * Framework.  The BundleRepository is also used to mark and unmark
 * bundle dependancies.
 *
 * <p> 
 * This class is not synchronized.  Any access to the bundle
 * repository must be synchronized by the caller.
 */
public class BundleRepository {
	/** bundles by install order */
	private ArrayList bundlesByInstallOrder;

	/** bundles keyed by bundle Id */
	private KeyedHashSet bundlesById;

	/** bundles keyed by SymbolicName */
	private HashMap bundlesBySymbolicName;

	/** PackageAdmin */
	private PackageAdminImpl packageAdmin;

	public BundleRepository(int initialCapacity, PackageAdminImpl packageAdmin) {
		bundlesByInstallOrder = new ArrayList(initialCapacity);
		bundlesById = new KeyedHashSet(initialCapacity, true);
		bundlesBySymbolicName = new HashMap(initialCapacity);
		this.packageAdmin = packageAdmin;
	}

	/**
	 * Gets a list of bundles ordered by install order.
	 * @return List of bundles by install order.
	 */
	public List getBundles() {
		return bundlesByInstallOrder;
	}

	/**
	 * Gets a bundle by its bundle Id.
	 * @param bundleId
	 * @return
	 */
	public AbstractBundle getBundle(long bundleId) {
		Long key = new Long(bundleId);
		return (AbstractBundle) bundlesById.getByKey(key);
	}

	public AbstractBundle[] getBundles(String symbolicName) {
		if (Constants.getInternalSymbolicName().equals(symbolicName))
			symbolicName = Constants.OSGI_SYSTEM_BUNDLE;
		return (AbstractBundle[]) bundlesBySymbolicName.get(symbolicName);
	}

	public AbstractBundle getBundle(String symbolicName, Version version) {
		AbstractBundle[] bundles = getBundles(symbolicName);
		if (bundles != null) {
			if (bundles.length > 0) {
				for (int i = 0; i < bundles.length; i++) {
					if (bundles[i].getVersion().equals(version)) {
						return bundles[i];
					}
				}
			}
		}
		return null;
	}

	public void add(AbstractBundle bundle) {
		bundlesByInstallOrder.add(bundle);
		bundlesById.add(bundle);
		String symbolicName = bundle.getSymbolicName();
		if (symbolicName != null) {
			AbstractBundle[] bundles = (AbstractBundle[]) bundlesBySymbolicName.get(symbolicName);
			if (bundles == null) {
				// making the initial capacity on this 1 since it
				// should be rare that multiple version exist
				bundles = new AbstractBundle[1];
				bundles[0] = bundle;
				bundlesBySymbolicName.put(symbolicName, bundles);
				return;
			}

			ArrayList list = new ArrayList(bundles.length + 1);
			// find place to insert the bundle
			Version newVersion = bundle.getVersion();
			boolean added = false;
			for (int i = 0; i < bundles.length; i++) {
				AbstractBundle oldBundle = bundles[i];
				Version oldVersion = oldBundle.getVersion();
				if (!added && newVersion.compareTo(oldVersion) >= 0) {
					added = true;
					list.add(bundle);
				}
				list.add(oldBundle);
			}
			if (!added) {
				list.add(bundle);
			}

			bundles = new AbstractBundle[list.size()];
			list.toArray(bundles);
			bundlesBySymbolicName.put(symbolicName, bundles);
		}
	}

	public boolean remove(AbstractBundle bundle) {
		// remove by bundle ID
		boolean found = bundlesById.remove(bundle);
		if (!found)
			return false;

		// remove by install order
		bundlesByInstallOrder.remove(bundle);
		// remove by symbolic name
		String symbolicName = bundle.getSymbolicName();
		if (symbolicName == null)
			return true;

		AbstractBundle[] bundles = (AbstractBundle[]) bundlesBySymbolicName.get(symbolicName);
		if (bundles == null)
			return true;

		// found some bundles with the global name.
		// remove all references to the specified bundle.
		int numRemoved = 0;
		for (int i = 0; i < bundles.length; i++) {
			if (bundle == bundles[i]) {
				numRemoved++;
				bundles[i] = null;
			}
		}
		if (numRemoved > 0) {
			if (bundles.length - numRemoved <= 0) {
				// no bundles left in the array remove the array from the hash
				bundlesBySymbolicName.remove(symbolicName);
			} else {
				// create a new array with the null entries removed.
				AbstractBundle[] newBundles = new AbstractBundle[bundles.length - numRemoved];
				int indexCnt = 0;
				for (int i = 0; i < bundles.length; i++) {
					if (bundles[i] != null) {
						newBundles[indexCnt] = bundles[i];
						indexCnt++;
					}
				}
				bundlesBySymbolicName.put(symbolicName, newBundles);
			}
		}

		return true;
	}

	public void removeAllBundles() {
		bundlesByInstallOrder.clear();
		bundlesById = new KeyedHashSet();
		bundlesBySymbolicName.clear();
	}
}
