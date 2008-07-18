/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
 * @since 1.1
 * @version $Revision$
 */
public interface ConditionalPermissionInfoBase {
	/**
	 * This string is used to indicate that a row in the conditional permission
	 * admin table should return a grant decision of ALLOW if the conditions are
	 * all satisfied and at least one of the permissions is implied.
	 */
	public final static String	ALLOW	= "allow";

	/**
	 * This string is used to indicate that a row in the conditional permission
	 * admin table should return a grant decision of DENY if the conditions are
	 * all satisfied and at least one of the permissions is implied.
	 */
	public final static String	DENY	= "deny";

	/**
	 * Returns the Condition Infos for the Conditions that must be satisfied to
	 * enable the Permissions.
	 * 
	 * @return The Condition Infos for the Conditions in this Conditional
	 *         Permission Info.
	 */
	public ConditionInfo[] getConditionInfos();

	/**
	 * Returns the Permission Infos for the Permission in this Conditional
	 * Permission Info.
	 * 
	 * @return The Permission Infos for the Permission in this Conditional
	 *         Permission Info.
	 */
	public PermissionInfo[] getPermissionInfos();

	/**
	 * Returns the grant decision for this Conditional Permission Info.
	 * 
	 * @return One of the following values:
	 *         <ul>
	 *         <li>{@link #ALLOW allow} - The grant decision is allow.</li>
	 * 
	 *         <li>{@link #DENY deny} - The grant decision is DENY.</li>
	 *         </ul>
	 */
	public String getGrantDecision();

	/**
	 * Returns the name of this Conditional Permission Info.
	 * 
	 * @return The name of this Conditional Permission Info.
	 */
	public String getName();
}
