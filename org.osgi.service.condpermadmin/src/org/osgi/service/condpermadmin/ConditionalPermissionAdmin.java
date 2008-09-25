/*
 * Copyright (c) OSGi Alliance (2005, 2008). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.condpermadmin;

import java.security.AccessControlContext;
import java.util.Enumeration;

import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * Framework service to administer Conditional Permissions. Conditional
 * Permissions can be added to, retrieved from, and removed from the framework.
 * Conditional Permissions are conceptually managed in an ordered table called
 * the Conditional Permission Table.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ConditionalPermissionAdmin {
	/**
	 * Create a new Conditional Permission Info in the Conditional Permission
	 * Table.
	 * <p>
	 * The Conditional Permission Info will be given a unique, never reused
	 * name. This entry will be added at the beginning of the Conditional
	 * Permission Table with a grant decision of
	 * {@link ConditionalPermissionInfoBase#ALLOW ALLOW}.
	 * <p>
	 * Since this method changes the Conditional Permission Table any
	 * {@link ConditionalPermissionsUpdate}s that were created prior to
	 * calling this method can no longer be committed.
	 * 
	 * @param conds The Conditions that need to be satisfied to enable the
	 *        corresponding Permissions.
	 * @param perms The Permissions that are enabled when the corresponding
	 *        Conditions are satisfied.
	 * @return The ConditionalPermissionInfo for the specified Conditions and
	 *         Permissions.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @deprecated Since 1.1. Use {@link ConditionalPermissionsUpdate}
	 *             instead.
	 */
	public ConditionalPermissionInfo addConditionalPermissionInfo(
			ConditionInfo conds[], PermissionInfo perms[]);

	/**
	 * Set or create a Conditional Permission Info with a specified name in the
	 * Conditional Permission Table.
	 * <p>
	 * If the specified name is <code>null</code>, a new Conditional Permission
	 * Info must be created and will be given a unique, never reused name. If
	 * there is currently no Conditional Permission Info with the specified
	 * name, a new Conditional Permission Info must be created with the
	 * specified name. Otherwise, the Conditional Permission Info with the
	 * specified name must be updated with the specified Conditions and
	 * Permissions. If a new entry was created in the Conditional Permission
	 * Table it will be added at the beginning of the table with a grant
	 * decision of {@link ConditionalPermissionInfoBase#ALLOW ALLOW}.
	 * <p>
	 * Since this method changes the underlying permission table any
	 * {@link ConditionalPermissionsUpdate}s that were created prior to
	 * calling this method can no longer be committed.
	 * 
	 * @param name The name of the Conditional Permission Info, or
	 *        <code>null</code>.
	 * @param conds The Conditions that need to be satisfied to enable the
	 *        corresponding Permissions.
	 * @param perms The Permissions that are enabled when the corresponding
	 *        Conditions are satisfied.
	 * @return The ConditionalPermissionInfo that for the specified name,
	 *         Conditions and Permissions.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @deprecated Since 1.1. Use {@link ConditionalPermissionsUpdate}
	 *             instead.
	 */
	public ConditionalPermissionInfo setConditionalPermissionInfo(String name,
			ConditionInfo conds[], PermissionInfo perms[]);

	/**
	 * Returns the Conditional Permission Infos from the Conditional Permission
	 * <p>
	 * The returned Enumeration will return elements in the order they are kept
	 * in the Conditional Permission Table.
	 * <p>
	 * The Enumeration returned is based on a copy of the Conditional Permission
	 * Table and therefore will not throw exceptions if the Conditional
	 * Permission Table is changed during the course of reading elements from
	 * the Enumeration.
	 * 
	 * @return An enumeration of the Conditional Permission Infos that are
	 *         currently in the Conditional Permission Table.
	 */
	public Enumeration getConditionalPermissionInfos();

	/**
	 * Return the Conditional Permission Info with the specified name.
	 * 
	 * @param name The name of the Conditional Permission Info to be returned.
	 * @return The Conditional Permission Info with the specified name.
	 */
	public ConditionalPermissionInfo getConditionalPermissionInfo(String name);

	/**
	 * Returns the Access Control Context that corresponds to the specified
	 * signers.
	 * 
	 * @param signers The signers for which to return an Access Control Context.
	 * @return An <code>AccessControlContext</code> that has the Permissions
	 *         associated with the signer.
	 */
	public AccessControlContext getAccessControlContext(String[] signers);

	/**
	 * Creates an update for the Conditional Permission Table. The update is a
	 * working copy of the current Conditional Permission Table. If the running
	 * Conditional Permission Table is modified before commit is called on the
	 * returned update, then the call to commit will fail. That is, the commit
	 * method will return false and no change will be made to the running
	 * Conditional Permission Table. There is no requirement that commit is
	 * eventually called on the returned update.
	 * 
	 * @return An update for the Conditional Permission Table.
	 * @since 1.1
	 */
	public ConditionalPermissionsUpdate createConditionalPermissionsUpdate();

	/**
	 * Creates a ConditionalPermissionInfoBase with the specified fields.
	 * 
	 * @param name The name of the created ConditionalPermissionInfoBase or
	 *        <code>null</code> to have a unique name generated when the created
	 *        ConditionalPermissionInfoBase is committed in an update to the
	 *        Conditional Permission Table.
	 * @param conditions The Conditions that need to be satisfied to enable the
	 *        corresponding Permissions.
	 * @param permissions The Permissions that are enabled when the
	 *        corresponding Conditions are satisfied.
	 * @param decision One of the following values:
	 *        <ul>
	 *        <li>{@link ConditionalPermissionInfoBase#ALLOW allow}</li>
	 * 
	 *        <li>{@link ConditionalPermissionInfoBase#DENY deny}</li>
	 *        </ul>
	 * @return A ConditionalPermissionInfoBase object suitable for insertion in
	 *         a {@link ConditionalPermissionsUpdate}.
	 * @throws IllegalArgumentException If the decision string is invalid.
	 * @since 1.1
	 */
	public ConditionalPermissionInfoBase createConditionalPermissionInfoBase(
			String name,
			ConditionInfo conditions[], PermissionInfo permissions[],
			String decision);
}
