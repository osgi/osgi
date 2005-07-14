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

import java.io.IOException;
import java.security.*;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.PermissionStorage;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.condpermadmin.*;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * Implements ConditionalPermissionAdmin.
 */
public class ConditionalPermissionAdminImpl implements ConditionalPermissionAdmin {
	/**
	 * The Vector of current ConditionalPermissionInfos.  
	 */
	Vector condPerms;
	Framework framework;
	PermissionStorage storage;

	/**
	 * @param framework
	 * @param permissionStorage
	 */
	public ConditionalPermissionAdminImpl(Framework framework, PermissionStorage permissionStorage) {
		ConditionalPermissionInfoImpl.setConditionalPermissionAdminImpl(this);
		this.framework = framework;
		this.storage = permissionStorage;
		try {
			condPerms = permissionStorage.deserializeConditionalPermissionInfos();
		} catch (IOException e) {
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);
			condPerms = new Vector();
		}
	}

	/**
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionAdmin#addConditionalPermissionInfo(org.osgi.service.condpermadmin.ConditionInfo[], org.osgi.service.permissionadmin.PermissionInfo[])
	 */
	public ConditionalPermissionInfo addConditionalPermissionInfo(ConditionInfo[] conds, PermissionInfo[] perms) {
		return setConditionalPermissionInfo(null, conds, perms);
	}

	public ConditionalPermissionInfo setConditionalPermissionInfo(String name, ConditionInfo conds[], PermissionInfo perms[]) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new AllPermission());
		if (name == null)
			name = "generated_" + Long.toString(System.currentTimeMillis()); //$NON-NLS-1$
		ConditionalPermissionInfoImpl condPermInfo = new ConditionalPermissionInfoImpl(name, conds, perms);
		synchronized (condPerms) {
			ConditionalPermissionInfoImpl existing = (ConditionalPermissionInfoImpl) getConditionalPermissionInfo(condPermInfo.getName());
			if (existing != null) // do not call existing.delete() to avoid multiple saves
				existing.deleted = true;
			condPerms.add(condPermInfo);
			saveCondPermInfos();
		}
		AbstractBundle bundles[] = framework.getAllBundles();
		for (int i = 0; i < bundles.length; i++) {
			AbstractBundle bundle = bundles[i];
			if (bundle.domain == null) {
				continue;
			}
			BundleCombinedPermissions bcp = (BundleCombinedPermissions) bundle.domain.getPermissions();
			/* TODO I don't think we need this check */
			if (perms == null) {
				continue;
			}

			bcp.checkConditionalPermissionInfo(condPermInfo);
		}
		return condPermInfo;
	}

	public ConditionalPermissionInfo getConditionalPermissionInfo(String name) {
		for (Enumeration eCondPerms = condPerms.elements(); eCondPerms.hasMoreElements();) {
			ConditionalPermissionInfoImpl condPerm = (ConditionalPermissionInfoImpl) eCondPerms.nextElement();
			if (name.equals(condPerm.getName()))
				return condPerm;
		}
		return null;
	}

	/**
	 * Returns an Enumeration of current ConditionalPermissionInfos. Each element will be of type 
	 * ConditionalPermissionInfoImpl.
	 * @return an Enumeration of current ConditionalPermissionInfos.
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionAdmin#getConditionalPermissionInfos()
	 */
	public Enumeration getConditionalPermissionInfos() {
		synchronized (condPerms) {
			return condPerms.elements();
		}
	}

	void deleteConditionalPermissionInfo(ConditionalPermissionInfo cpi) {
		synchronized (condPerms) {
			condPerms.remove(cpi);
			saveCondPermInfos();
		}
	}

	private void saveCondPermInfos() {
		try {
			storage.serializeConditionalPermissionInfos(condPerms);
		} catch (IOException e) {
			e.printStackTrace();
			framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);
		}
	}

	/**
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionAdmin#getAccessControlContext(java.lang.String[])
	 */
	public AccessControlContext getAccessControlContext(String[] signers) {
		Enumeration infos = getConditionalPermissionInfos();
		ArrayList permissionInfos = new ArrayList();
		if (infos != null) {
			while (infos.hasMoreElements()) {
				ConditionalPermissionInfoImpl condPermInfo = (ConditionalPermissionInfoImpl) infos.nextElement();
				ConditionInfo[] condInfo = condPermInfo.getConditionInfos();
				boolean match = true;
				for (int i = 0; i < condInfo.length; i++) {
					if (BundleSignerCondition.class.getName().equals(condInfo[i].getType())) {
						String[] args = condInfo[i].getArgs();
						for (int j = 0; j < args.length; j++)
							if (!framework.adaptor.matchDNChain(args[j], signers)) {
								match = false;
								break;
							}
					} else {
						match = false;
						break;
					}
				}
				if (match) {
					PermissionInfo[] addPermInfos = condPermInfo.getPermissionInfos();
					for (int i = 0; i < addPermInfos.length; i++)
						permissionInfos.add(addPermInfos[i]);
				}
			}
		}
		BundlePermissionCollection collection = framework.permissionAdmin.createPermissions((PermissionInfo[]) permissionInfos.toArray(new PermissionInfo[permissionInfos.size()]), null);
		return new AccessControlContext(collection == null ? new ProtectionDomain[0] : new ProtectionDomain[] {new ProtectionDomain(null, collection)});
	}
}
