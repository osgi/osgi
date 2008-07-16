/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2007). All Rights Reserved.
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
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface ConditionalPermissionAdmin {
	/**
	 * Creates an  update.  The update is a working
	 * copy of the current Conditional Permission Table.  If the running
	 * Conditional Permission Table is modified before commit is called
	 * on this Update then the call to commit will fail (i.e., the
	 * commit method will return false and no change will be made
	 * to the running Conditional Permission Table).  There is no
	 * requirement that commit is eventually called on an Update.
	 *   
	 * @return A working copy Update based on the current Conditional
	 * Permission table
	 */
	public ConditionalPermissionAdminUpdate createUpdate();

	/**
	 * Create a new Conditional Permission Info.
	 * <p>
	 * The Conditional Permission Info will be given a unique, never reused
	 * name.  This entry will be added at the beginning of the table with a
	 * decision string of "ConditionalPermissionInfoBase.ALLOW".
	 * <p>
	* Since this method changes the underlying permission table any Updates
	* that were created prior to calling this method can no longer
	* commit.
	 * 
	 * @param conds The Conditions that need to be satisfied to enable the
	 *        corresponding Permissions.
	 * @param perms The Permissions that are enable when the corresponding
	 *        Conditions are satisfied.
	 * @return The ConditionalPermissionInfo for the specified Conditions and
	 *         Permissions.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 */
	public ConditionalPermissionInfo addConditionalPermissionInfo(
			ConditionInfo conds[], PermissionInfo perms[]);

	/**
	 * Set or create a Conditional Permission Info with a specified name.
	 * <p>
	 * If the specified name is <code>null</code>, a new Conditional
	 * Permission Info must be created and will be given a unique, never reused
	 * name. If there is currently no Conditional Permission Info with the
	 * specified name, a new Conditional Permission Info must be created with
	 * the specified name. Otherwise, the Conditional Permission Info with the
	 * specified name must be updated with the specified Conditions and
	 * Permissions.  If a new entry was created in the table it will be added
	* at the beginning of the table with a decision
	* of "ConditionalPermissionInfoBase.ALLOW".
	* <p>
	* Since this method changes the underlying permission table any Updates
	* that were created prior to calling this method can no longer
	* commit.
	 * 
	 * @param name The name of the Conditional Permission Info, or
	 *        <code>null</code>.
	 * @param conds The Conditions that need to be satisfied to enable the
	 *        corresponding Permissions.
	 * @param perms The Permissions that are enable when the corresponding
	 *        Conditions are satisfied.
	 * @return The ConditionalPermissionInfo that for the specified name,
	 *         Conditions and Permissions.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 */
	public ConditionalPermissionInfo setConditionalPermissionInfo(String name,
			ConditionInfo conds[], PermissionInfo perms[]);

	/**
	 * Returns the Conditional Permission Infos that are currently managed by
	 * Conditional Permission Admin. Calling
	 * {@link ConditionalPermissionInfo#delete()} will remove the Conditional
	 * Permission Info from Conditional Permission Admin.
	 * <p>
	 * The Enumeration returned will return elements in the order of the
	 * most significant Conditional Permission Info (i.e. those with
	 * the least index) to the least significant (i.e. those with the
	 * greatest index).
	 * <p>
	 * The Enumeration returned is based on a copy of the Conditional
	 * Permission table, and therefore will not throw exceptions if
	 * the underlying Conditional Permission table is changed during the
	 * course of reading elements from the Enumeration
	 * 
	 * @return An enumeration of the Conditional Permission Infos that are
	 *         currently managed by Conditional Permission Admin.
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
	* This creates a ConditionalPermissionInfoBase
	* with the given fields.
	* @param name The name of this object.  Null implies that this
	*        object will be replaced during a commit operation
	*        with another ConditionalPermissionInfoBase with all of
	*        the same fields but with a name that is unique and
	*        which has never been used.
	* @param conditions The Conditions that need to be satisfied to enable the
	*        corresponding Permissions.
	* @param permissions The Permissions that are enable when the corresponding
	*        Conditions are satisfied.
	* @param decision One of the following values:<UL>
	* <LI>ConditionalPermissionInfoBase.ALLOW - The right should be granted</LI>
	* <LI>ConditionalPermissionInfoBase.DENY - The right should NOT be granted</LI>
	* </UL>
	* @return A ConditionalPermissionInfoBase object.
	* @throws IllegalArgumentException if name is null or
	* the decision string is invalid
	*/
	public ConditionalPermissionInfoBase createInfoBase(String name, ConditionInfo conditions[], PermissionInfo permissions[], String decision);

	/**
	 * Returns the Access Control Context that corresponds to the specified
	 * signers.
	 * 
	 * @param signers The signers for which to return an Access Control Context.
	 * @return An <code>AccessControlContext</code> that has the Permissions
	 *         associated with the signer.
	 */
	public AccessControlContext getAccessControlContext(String[] signers);
}
