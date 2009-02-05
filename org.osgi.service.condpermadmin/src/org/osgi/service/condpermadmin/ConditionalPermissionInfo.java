/*
 * Copyright (c) OSGi Alliance (2004, 2009). All Rights Reserved.
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

import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * A binding of a set of Conditions to a set of Permissions. Instances of this
 * interface are obtained from the Conditional Permission Admin service.
 * 
 * @Immutable
 * @version $Revision$
 */
public interface ConditionalPermissionInfo {
	/**
	 * This string is used to indicate that a row in the Conditional Permission
	 * Table should return a grant decision of &quot;allow&quot; if the
	 * conditions are all satisfied and at least one of the permissions is
	 * implied.
	 * 
	 * @since 1.1
	 */
	public final static String	ALLOW	= "allow";

	/**
	 * This string is used to indicate that a row in the Conditional Permission
	 * Table should return a grant decision of &quot;deny&quot; if the
	 * conditions are all satisfied and at least one of the permissions is
	 * implied.
	 * 
	 * @since 1.1
	 */
	public final static String	DENY	= "deny";

	/**
	 * Returns the Condition Infos for the Conditions that must be satisfied to
	 * enable the Permissions.
	 * 
	 * @return The Condition Infos for the Conditions in this Conditional
	 *         Permission Info.
	 */
	ConditionInfo[] getConditionInfos();

	/**
	 * Returns the Permission Infos for the Permission in this Conditional
	 * Permission Info.
	 * 
	 * @return The Permission Infos for the Permission in this Conditional
	 *         Permission Info.
	 */
	PermissionInfo[] getPermissionInfos();

	/**
	 * Removes this Conditional Permission Info from the Conditional Permission
	 * Table.
	 * <p>
	 * Since this method changes the underlying permission table any
	 * {@link ConditionalPermissionUpdate}s that were created prior to calling
	 * this method can no longer be committed.
	 * 
	 * @throws UnsupportedOperationException If this object was created by
	 *         {@link ConditionalPermissionAdmin#newConditionalPermissionInfo}
	 *         or obtained from a {@link ConditionalPermissionUpdate}. This
	 *         method only functions if this object was obtained from one of the
	 *         {@link ConditionalPermissionAdmin} methods deprecated in version
	 *         1.1.
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 * @deprecated Since 1.1. Use
	 *             {@link ConditionalPermissionAdmin#newConditionalPermissionUpdate()}
	 *             instead to manage the Conditional Permissions.
	 */
	void delete();

	/**
	 * Returns the name of this Conditional Permission Info.
	 * 
	 * @return The name of this Conditional Permission Info.
	 */
	String getName();

	/**
	 * Returns the grant decision for this Conditional Permission Info.
	 * 
	 * @return One of the following values:
	 *         <ul>
	 *         <li>{@link #ALLOW allow} - The grant decision is
	 *         &quot;allow&quot;.</li>
	 *         <li>{@link #DENY deny} - The grant decision is &quot;deny&quot;.</li>
	 *         </ul>
	 * @since 1.1
	 */
	String getGrantDecision();
}
