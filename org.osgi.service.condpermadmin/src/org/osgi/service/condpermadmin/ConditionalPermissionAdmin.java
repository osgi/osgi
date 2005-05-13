/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.condpermadmin;

import java.security.AccessControlContext;
import java.util.Enumeration;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * This is a framework service that allows ConditionalPermissionInfos to be
 * added to, retrieved from, and removed from the framework.
 * 
 * @version $Revision$
 */
public interface ConditionalPermissionAdmin {
	/**
	 * This is a framework service that allows ConditionalPermissionInfos to be
	 * added to, retrieved from, and removed from the framework.
	 * 
	 * @param conds
	 *            the Conditions that need to be satisfied to enable the
	 *            corresponding Permissions.
	 * @param perms
	 *            the Permissions that are enable when the corresponding
	 *            Conditions are satisfied.
	 * @return the ConditionalPermissionInfo that for the newly added Conditions
	 *         and Permissions.
	 */
	ConditionalPermissionInfo addConditionalPermissionInfo(
			ConditionInfo conds[], PermissionInfo perms[]);

	/**
	 * Returns the ConditionalPermissionInfos that are currently managed by
	 * ConditionalPermissionAdmin. The Enumeration is made up of
	 * ConditionalPermissionInfos. Calling ConditionalPermissionInfo.delete()
	 * will remove the ConditionalPermissionInfo from
	 * ConditionalPermissionAdmin.
	 * 
	 * @return the ConditionalPermissionInfos that are currently managed by
	 *         ConditionalPermissionAdmin. The Enumeration is made up of
	 *         ConditionalPermissionInfos.
	 */
	Enumeration getConditionalPermissionInfos();

	/**
	 * Returns the AccessControlContext that corresponds to the given signers.
	 * 
	 * @param signers
	 *            the signers that will be checked agains BundleSignerCondition.
	 * @return an AccessControlContext that has the Permissions associated with
	 *         the signer.
	 */
	AccessControlContext getAccessControlContext(String signers[]);
}
