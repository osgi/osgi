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
import java.util.*;
import org.osgi.service.condpermadmin.Condition;

/**
 * 
 * This security manager implements the ConditionalPermission processing for
 * OSGi. It is to be used with ConditionalPermissionAdmin.
 * 
 */
public class FrameworkSecurityManager extends SecurityManager {
	/* 
	 * This is super goofy, but we need to make sure that the CheckContext and
	 * CheckPermissionAction classes load early. Otherwise, we run into problems later.
	 */
	static {
		Class c;
		c = CheckPermissionAction.class;
		c = CheckContext.class;
	}

	static class CheckContext {
		// A non zero depth indicates that we are doing a recursive permission check.
		ArrayList depthCondSets = new ArrayList(2);
		ArrayList CondClassSet;

		public int getDepth() {
			return depthCondSets.size() - 1;
		}
	}

	/**
	 * Adds an array of Condition[] for a ProtectionDomain.
	 * 
	 * @param condSet the array of Condition[] for the ProtectionDomain.
	 * @return true if the condSet was added. false if this is a recursive check
	 *         and ConditionalPermissions cannot be used.
	 */
	boolean addConditionsForDomain(Condition condSet[][]) {
		CheckContext cc = (CheckContext) localCheckContext.get();
		if (cc == null) {
			// We are being invoked in a weird way. Perhaps the ProtectionDomain is
			// getting invoked directly.
			return false;
		}
		Vector condSets = (Vector) cc.depthCondSets.get(cc.getDepth());
		if (condSets == null) {
			condSets = new Vector(2);
			cc.depthCondSets.set(cc.getDepth(), condSets);
		}
		condSets.add(condSet);
		return true;
	}

	ThreadLocal localCheckContext = new ThreadLocal();

	static class CheckPermissionAction implements PrivilegedAction {
		Permission perm;
		Object context;
		FrameworkSecurityManager fsm;

		CheckPermissionAction(FrameworkSecurityManager fsm, Permission perm, Object context) {
			this.fsm = fsm;
			this.perm = perm;
			this.context = context;
		}

		public Object run() {
			fsm.internalCheckPermission(perm, context);
			return null;
		}
	}

	public void checkPermission(Permission perm, Object context) {
		AccessController.doPrivileged(new CheckPermissionAction(this, perm, context));
	}

	public void internalCheckPermission(Permission perm, Object context) {
		AccessControlContext acc = (AccessControlContext) context;
		CheckContext cc = (CheckContext) localCheckContext.get();
		if (cc == null) {
			cc = new CheckContext();
			localCheckContext.set(cc);
		}
		cc.depthCondSets.add(null); // initialize postponed condition set to null
		try {
			acc.checkPermission(perm);
			// We want to pop the first set of postponed conditions and process them
			Vector remainingSets = (Vector) cc.depthCondSets.get(cc.getDepth());
			if (remainingSets != null) {
				/*
				 * In this bit of code we have to try every possible combination
				 * of conditional permissions that still need to be evaluated. We
				 * do this using recursion to keep track of the different
				 * combinations we have tried. The top call will setup the different
				 * combinations for the first protection domain with unevaluated 
				 * conditions. The final call will actually evaluate the condition.
				 * If a good combination is found, it will immediately bubble
				 * up.
				 */
				Hashtable condContextDict = new Hashtable(2);
				// The remainder we will process recursively.
				Condition conds[][] = (Condition[][]) remainingSets.remove(0);
				for (int i = 0; i < conds.length; i++)
					if (recursiveCheck(remainingSets, conds[i], null, condContextDict, cc))
						return; // found a pass return without SecurityException
				throw new SecurityException("Conditions not satisfied"); //$NON-NLS-1$
			}
		} finally {
			cc.depthCondSets.remove(cc.getDepth());
		}
	}

	/**
	 * Checks that at least one combination of the Condition[] in the Vector can
	 * be satisfied along with the passed array of Conditions.
	 * 
	 * @param remainingSets the Vector of Condition[][] to check.
	 * @param conditions the conditions that must be satisfied.
	 * @param condDict a dictionary that will be filled with the Conditions to
	 *        check.
	 * @param condContextDict a Dictionary of Dictionaries that will be passed
	 *        to Condition.isSatisfied when performing repeated check.
	 * @param cc the CheckContext
	 * @return true if a successful combination was found.
	 */
	private boolean recursiveCheck(Vector remainingSets, Condition[] conditions, Hashtable condDict, Hashtable condContextDict, CheckContext cc) {
		// clone condDict and clone each Vector in the condDict
		if (condDict == null) {
			condDict = new Hashtable(2);
		} else {
			Hashtable copyCondDict = new Hashtable(2);
			for (Enumeration keys = condDict.keys(); keys.hasMoreElements();) {
				Object key = keys.nextElement();
				copyCondDict.put(key, ((Vector) condDict.get(key)).clone());
			}
			condDict = copyCondDict;
		}
		for (int i = 0; i < conditions.length; i++) {
			if (conditions[i] == null)
				continue;
			Vector condList = (Vector) condDict.get(conditions[i].getClass());
			if (condList == null) {
				condList = new Vector();
				condDict.put(conditions[i].getClass(), condList);
			}
			condList.add(conditions[i]);
		}
		if (remainingSets.size() > 0) {
			Condition conds[][] = (Condition[][]) remainingSets.get(0);
			Vector newSets = (Vector) remainingSets.clone();
			newSets.remove(0);
			for (int i = 0; i < conds.length; i++)
				if (recursiveCheck(newSets, conds[i], condDict, condContextDict, cc))
					return true;
			return false;
		}
		Enumeration keys = condDict.keys();
		while (keys.hasMoreElements()) {
			Class key = (Class) keys.nextElement();
			Vector conds = (Vector) condDict.get(key);
			if (conds.size() == 0)
				continue; // This should never happen since we only add to the condDict if there is a condition
			Condition condArray[] = (Condition[]) conds.toArray(new Condition[0]);
			Dictionary context = (Dictionary) condContextDict.get(key);
			if (context == null) {
				context = new Hashtable(2);
				condContextDict.put(key, context);
			}
			if (cc.CondClassSet == null)
				cc.CondClassSet = new ArrayList(2);
			if (cc.CondClassSet.contains(condArray[0].getClass()))
				return false; // doing recursion into same condition class
			cc.CondClassSet.add(condArray[0].getClass());
			try {
				if (!condArray[0].isSatisfied(condArray, context))
					return false;
			} finally {
				cc.CondClassSet.remove(condArray[0].getClass());
			}
		}
		return true;
	}

	public void checkPermission(Permission perm) {
		checkPermission(perm, getSecurityContext());
	}

	public Object getSecurityContext() {
		return AccessController.getContext();
	}
}
