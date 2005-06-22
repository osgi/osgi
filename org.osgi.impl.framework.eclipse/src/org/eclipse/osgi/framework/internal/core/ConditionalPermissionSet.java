/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.security.*;
import java.util.Enumeration;
import java.util.HashMap;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * This class represents a PermissionCollection tied to a set of Conditions.
 * Before the permissions are actually used, isNonEmpty should be called.
 */
public class ConditionalPermissionSet extends PermissionCollection {
	private static final long serialVersionUID = 3258411750729920566L;
	ConditionalPermissionInfoImpl cpis[] = new ConditionalPermissionInfoImpl[0];
	HashMap cachedPermissionCollections = new HashMap();
	private boolean hasAllPermission = false;
	/**
	 * These are conditions that need to be satisfied in order to enable the
	 * permissions. If the array is empty, no conditions need to be satisfied.
	 * If <code>neededCondititions</code> is null,
	 */
	Condition neededConditions[];

	/*
	 * TODO: we need to validate the cpis[] to make sure they don't go away.
	 * Reset everything if they do. We also need to be able to add CPIs
	 */
	/**
	 * Construct a new ConditionalPermission set with an initial set of
	 * permissions.
	 */
	public ConditionalPermissionSet(ConditionalPermissionInfoImpl cpis[], Condition neededConditions[]) {
		this.cpis = cpis;
		this.neededConditions = neededConditions;
		checkForAllPermission();
	}

	/**
	 * Adds another ConditionalPermissionInfoImpl to this set. <b>Only a
	 * CondtitionalPermissionInfo whose Conditions are immutable and satisfied
	 * may be added. </b>
	 * 
	 * @param cpi the ConditionalPermissionInfoImpl to be added to this set.
	 *        <b>Only a CondtitionalPermissionInfo whose Conditions are
	 *        immutable and satisfied may be added. </b>
	 */
	void addConditionalPermissionInfo(ConditionalPermissionInfoImpl cpi) {
		if (neededConditions.length > 0) {
			throw new RuntimeException("Cannot add ConditionalPermissionInfoImpl to a non satisfied set");
		}
		ConditionalPermissionInfoImpl newcpis[] = new ConditionalPermissionInfoImpl[cpis.length + 1];
		System.arraycopy(cpis, 0, newcpis, 0, cpis.length);
		newcpis[cpis.length] = cpi;
		cpis = newcpis;
		/*
		 * TODO: I couldn't decide wether it is better to run through the cached
		 * PermissionCollections and add permissions from this cpi, or to just
		 * clear out and let them get rebuilt ondemand. The ondemand route is
		 * simpler and in the end may be more efficient.
		 */
		cachedPermissionCollections.clear();
		checkForAllPermission();
	}

	/**
	 * This runs through the PermissionInfos to see if there is an AllPermission in the mix.
	 */
	private void checkForAllPermission() {
		if (hasAllPermission) {
			return;
		}
		out: for (int i = 0; i < cpis.length; i++) {
			if (cpis[i] == null) // check for deletions
				continue;
			PermissionInfo perms[] = cpis[i].perms;
			for (int j = 0; j < perms.length; j++) {
				if (perms[j].getType().equals(AllPermission.class.getName())) {
					hasAllPermission = true;
					break out;
				}
			}
		}
	}

	/**
	 * Returns true if at least one of the ConditionalPermissionInfos in this
	 * set is still active. (Not deleted.)
	 * 
	 * @return true if there is at least one active ConditionalPermissionInfo.
	 */
	boolean isNonEmpty() {
		boolean nonEmpty = false;
		boolean forceAllPermCheck = false;
		for (int i = 0; i < cpis.length; i++) {
			if (cpis[i] != null) {
				if (cpis[i].isDeleted()) {
					cpis[i] = null;
					forceAllPermCheck = true;
					/*
					 * We don't have a way to remove from a collection; we can
					 * only add. Thus, we must clear out everything. TODO:
					 * Investigate if it would be more efficient to only clear
					 * the permission collections of the type stored by the cpi.
					 */
					cachedPermissionCollections.clear();
				} else {
					nonEmpty = true;
				}
			}
		}
		if (!nonEmpty)
			cpis = new ConditionalPermissionInfoImpl[0];
		if (forceAllPermCheck) {
			hasAllPermission = false;
			checkForAllPermission();
		}
		return nonEmpty;
	}

	/**
	 * Returns the conditions that need to be satisfied before the permissions
	 * embodied in this set can be used.
	 * 
	 * @return the array of conditions that need to be satisfied. If the array
	 *         is empty, no conditions need to be satisfied to use the
	 *         permissions. If null is returned, these permissions can never be
	 *         used.
	 */
	Condition[] getNeededConditions() {
		if (neededConditions == null || neededConditions.length == 0)
			return neededConditions;
		boolean foundNonNullCondition = false;
		/* We need to check to see if any conditions became immutable */
		for (int i = 0; i < neededConditions.length; i++) {
			Condition cond = neededConditions[i];
			if (cond == null)
				continue;
			if (!cond.isMutable()) {
				if (cond.isSatisfied()) {
					neededConditions[i] = null;
				} else {
					/*
					 * We now have an immutable unsatisfied condition, this set
					 * is no longer valid.
					 */
					neededConditions = null;
					break;
				}
			} else {
				foundNonNullCondition = true;
			}
		}
		if (neededConditions != null && !foundNonNullCondition)
			neededConditions = new Condition[0];
		return neededConditions;
	}

	/**
	 * We don't do anything here since this isn't a real PermissionCollection.
	 * 
	 * @param perm ignored.
	 * @see java.security.PermissionCollection#add(java.security.Permission)
	 */
	public void add(Permission perm) {
		// do nothing
	}

	/**
	 * Checks to see if the desired Permission is implied by this collection of
	 * ConditionalPermissionInfos.
	 * 
	 * @param perm Permission to check.
	 * @return true if this ConditionPermissionSet implies the passed
	 *         Permission.
	 * @see java.security.PermissionCollection#implies(java.security.Permission)
	 */
	public boolean implies(Permission perm) {
		if (hasAllPermission)
			return true;
		Class permClass = perm.getClass();
		PermissionCollection collection = (PermissionCollection) cachedPermissionCollections.get(permClass);
		if (collection == null) {
			collection = perm.newPermissionCollection();
			if (collection == null)
				collection = new PermissionsHash();
			for (int i = 0; i < cpis.length; i++) {
				try {
					ConditionalPermissionInfoImpl cpi = cpis[i];
					if (cpi != null)
						cpi.addPermissions(collection, permClass);
				} catch (Exception e) {
					// TODO: we should log this somewhere
					e.printStackTrace();
				}
			}
			cachedPermissionCollections.put(permClass, collection);
		}
		return collection.implies(perm);
	}

	/**
	 * We don't do anything here since this isn't a real PermissionCollection.
	 * 
	 * @return always returns null.
	 * @see java.security.PermissionCollection#elements()
	 */
	public Enumeration elements() {
		return null;
	}

	/**
	 * This method simply clears the resolved permission table. I think in both the 
	 * short-term and the amoritized case, this is more efficient than walking through 
	 * and clearing specific entries.
	 * 
	 * @param refreshedBundles not used.
	 */
	void unresolvePermissions(AbstractBundle[] refreshedBundles) {
		cachedPermissionCollections.clear();

	}
}
