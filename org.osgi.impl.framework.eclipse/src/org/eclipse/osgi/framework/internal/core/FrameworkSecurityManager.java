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

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
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
		// A non zero depth indicates that we are doing a recursive permission
		// check.
		int depth;
		Vector condSets;
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
		if (cc.depth > 0)
			return false; // no recursive checks
		if (cc.condSets == null) {
			cc.condSets = new Vector();
		}
		cc.condSets.add(condSet);
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
		if (cc != null) {
			cc.depth++;
		} else {
			cc = new CheckContext();
			localCheckContext.set(cc);
		}
		try {
			acc.checkPermission(perm);
			if (cc.depth == 0 && cc.condSets != null) {
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
				boolean passed = false;
				// We want to pop the first set of conditions and process them
				// The remainder we will process recursively.
				Condition conds[][] = (Condition[][]) cc.condSets.get(0);
				cc.condSets.removeElementAt(0);
				for (int i = 0; i < conds.length && !passed; i++) {
					passed = recursiveCheck(cc.condSets, conds[i], new Hashtable(2), condContextDict);
				}
				if (!passed)
					throw new SecurityException("Conditions not satisfied");
			}
		} finally {
			if (cc.depth == 0) {
				localCheckContext.set(null);
			}
			cc.depth--;
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
	 * @return true if a successful combination was found.
	 */
	private boolean recursiveCheck(Vector remainingSets, Condition[] conditions, Hashtable condDict, Hashtable condContextDict) {
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
			boolean passed = false;
			Condition conds[][] = (Condition[][]) remainingSets.get(0);
			Vector newSets = (Vector) remainingSets.clone();
			newSets.remove(0);
			for (int i = 0; i < conds.length; i++) {
				passed = recursiveCheck(newSets, conds[i], (Hashtable) condDict.clone(), condContextDict);
				if (passed)
					return true;
			}
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
			if (!condArray[0].isSatisfied(condArray, context))
				return false;
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
