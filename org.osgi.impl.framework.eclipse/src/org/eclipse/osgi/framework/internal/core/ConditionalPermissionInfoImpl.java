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

import java.io.Serializable;
import java.lang.reflect.*;
import java.security.Permission;
import java.security.PermissionCollection;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.*;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * 
 * This is a runtime embodiment of the data stored in ConditionalPermissionInfo.
 * It has methods to facilitate the management of Conditions and Permissions at
 * runtime.
 */
public class ConditionalPermissionInfoImpl implements ConditionalPermissionInfo, Serializable {
	private static final long serialVersionUID = 3258130245704825139L;
	/**
	 * The permissions enabled by the associated Conditions.
	 */
	PermissionInfo perms[];
	/**
	 * The conditions that must be satisfied to enable the corresponding
	 * permissions.
	 */
	ConditionInfo conds[];
	/**
	 * When true, this object has been deleted and any information retrieved
	 * from it should be discarded.
	 */
	boolean deleted = false;

	/**
	 * When true, this object has been deleted and any information retrieved
	 * from it should be discarded.
	 */
	boolean isDeleted() {
		return deleted;
	}

	public ConditionalPermissionInfoImpl(ConditionInfo conds[], PermissionInfo perms[]) {
		this.conds = conds;
		this.perms = perms;
	}

	/**
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionInfo#getConditionInfos()
	 */
	public ConditionInfo[] getConditionInfos() {
		if (conds == null)
			return null;
		ConditionInfo[] results = new ConditionInfo[conds.length];
		System.arraycopy(conds, 0, results, 0, conds.length);
		return results;
	}

	/* Used to find permission constructors in addPermissions */
	static private Class twoStringClassArray[] = new Class[] {String.class, String.class};
	/* Used to find condition constructors getConditions */
	static private Class[] condClassArray = new Class[] {Bundle.class, ConditionInfo.class};

	/**
	 * Adds the permissions of the given type (if any) that are part of this
	 * ConditionalPermissionInfo to the specified collection. The Permission
	 * instances are constructed using the specified permClass.
	 * 
	 * @param collection the collection to add to.
	 * @param permClass the class to use to construct Permission instances.
	 * @return the number of Permissions added.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 */
	int addPermissions(PermissionCollection collection, Class permClass) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		String permClassName = permClass.getName();
		Constructor constructor = permClass.getConstructor(twoStringClassArray);
		int count = 0;
		/*
		 * TODO: We need to cache the constructed permissions to enhance
		 * performance.
		 */
		for (int i = 0; i < perms.length; i++) {
			if (perms[i].getType().equals(permClassName)) {
				count++;
				String args[] = new String[2];
				args[0] = perms[i].getName();
				args[1] = perms[i].getActions();
				collection.add((Permission) constructor.newInstance(args));
			}
		}
		return count;
	}

	/**
	 * Returns the Condition objects associated with this ConditionalPermissionInfo.
	 * 
	 * @param bundle the bundle to be used to construct the Conditions.
	 * 
	 * @return the array of Conditions that must be satisfied before permissions
	 *         in the ConditionPermissionInfoImpl can be used.
	 */
	Condition[] getConditions(Bundle bundle) {
		Condition conditions[] = new Condition[conds.length];
		for (int i = 0; i < conds.length; i++) {
			/*
			 * TODO: I think we can pre-get the Constructors in our own
			 * constructor
			 */
			Class clazz;
			try {
				clazz = Class.forName(conds[i].getType());
			} catch (ClassNotFoundException e) {
				/* If the class isn't there, we fail */
				return null;
			}
			Constructor constructor = null;
			Method method = null;
			try {
				method = clazz.getMethod("getCondition", condClassArray); //$NON-NLS-1$
				if ((method.getModifiers() & Modifier.STATIC) == 0)
					method = null;
			} catch (NoSuchMethodException e) {
				// This is a normal case
			}
			if (method == null)
				try {
					constructor = clazz.getConstructor(condClassArray);
				} catch (NoSuchMethodException e) {
					// TODO should post a FrameworkEvent of type error here
					conditions[i] = Condition.FALSE;
					continue;
				}

			Object args[] = {bundle, conds[i]};
			try {
				if (method != null)
					conditions[i] = (Condition) method.invoke(null, args);
				else
					conditions[i] = (Condition) constructor.newInstance(args);
			} catch (Throwable t) {
				// TODO should post a FrameworkEvent of type error here
				conditions[i] = Condition.FALSE;
			}
		}
		return conditions;
	}

	/**
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionInfo#getPermissionInfos()
	 */
	public PermissionInfo[] getPermissionInfos() {
		if (perms == null)
			return null;
		PermissionInfo[] results = new PermissionInfo[perms.length];
		System.arraycopy(perms, 0, results, 0, perms.length);
		return results;
	}

	/**
	 * 
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionInfo#delete()
	 */
	public void delete() {
		deleted = true;
		condAdmin.deleteConditionalPermissionInfo(this);
	}

	static ConditionalPermissionAdminImpl condAdmin;

	static void setConditionalPermissionAdminImpl(ConditionalPermissionAdminImpl condAdmin) {
		ConditionalPermissionInfoImpl.condAdmin = condAdmin;
	}
}
