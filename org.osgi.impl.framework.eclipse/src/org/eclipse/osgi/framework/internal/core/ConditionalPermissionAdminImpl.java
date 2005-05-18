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
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new AllPermission());
		ConditionalPermissionInfoImpl condPermInfo = new ConditionalPermissionInfoImpl(conds, perms);
		synchronized (condPerms) {
			condPerms.add(condPermInfo);
			try {
				storage.serializeConditionalPermissionInfos(condPerms);
			} catch (IOException e) {
				e.printStackTrace();
				framework.publishFrameworkEvent(FrameworkEvent.ERROR, framework.systemBundle, e);
			}
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

	/**
	 * Returns an Enumeration of current ConditionalPermissionInfos. Each element will be of type 
	 * ConditionalPermissionInfoImpl.
	 * @return an Enumeration of current ConditionalPermissionInfos.
	 * @see org.osgi.service.condpermadmin.ConditionalPermissionAdmin#getConditionalPermissionInfos()
	 */
	public Enumeration getConditionalPermissionInfos() {
		return condPerms.elements();
	}

	void deleteConditionalPermissionInfo(ConditionalPermissionInfo cpi) {
		synchronized (condPerms) {
			condPerms.remove(cpi);
			try {
				storage.serializeConditionalPermissionInfos(condPerms);
			} catch (IOException e) {
				// TODO We need to log this
			}
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
					if (BundleSignerCondition.class.getName().equals(condInfo[i].getType())){
						String[] args = condInfo[i].getArgs();
						for (int j = 0; j < args.length; j++)
							if (!framework.adaptor.matchDNChain(args[j], signers)) {
								match = false;
								break;
							}
					}
					else {
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
