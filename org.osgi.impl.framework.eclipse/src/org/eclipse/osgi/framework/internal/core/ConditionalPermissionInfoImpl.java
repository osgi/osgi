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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.security.PermissionCollection;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
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
	public boolean isDeleted() {
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
		return conds;
	}

	/* Used to find permission constructors in addPermissions */
	static private Class twoStringClassArray[] = new Class[] {String.class, String.class};
	/* Used to find condition constructors getConditions */
	static private Class condClassArray[][];
	static {
		condClassArray = new Class[3][];
		for (int i = 0; i < condClassArray.length; i++) {
			condClassArray[i] = new Class[i + 1];
			condClassArray[i][0] = Bundle.class;
			for (int j = 1; j <= i; j++) {
				condClassArray[i][j] = String.class;
			}
		}
	}

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
	public int addPermissions(PermissionCollection collection, Class permClass) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
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
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Condition[] getConditions(Bundle bundle) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
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
			Constructor constructor;
			int argCount = conds[i].getArgs().length;
			if (argCount < condClassArray.length) {
				constructor = clazz.getConstructor(condClassArray[argCount]);
			} else {
				Class clazzes[] = new Class[argCount];
				clazzes[0] = Bundle.class;
				for (int j = 1; j <= argCount; j++) {
					clazzes[j] = String.class;
				}
				constructor = clazz.getConstructor(clazzes);
			}
			String strArgs[] = conds[i].getArgs();
			Object args[] = new Object[strArgs.length + 1];
			args[0] = bundle;
			System.arraycopy(strArgs, 0, args, 1, strArgs.length);
			conditions[i] = (Condition) constructor.newInstance(args);
		}
		return conditions;
	}

	/**
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionInfo#getPermissionInfos()
	 */
	public PermissionInfo[] getPermissionInfos() {
		return perms;
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
	static void setConditionalPermissionAdminImpl(ConditionalPermissionAdminImpl condAmin) {
		ConditionalPermissionInfoImpl.condAdmin = condAdmin;
	}
}
