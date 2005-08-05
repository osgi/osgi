/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.condpermadmin;

import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * A binding of a set of Conditions to a set of Permissions. Instances of this
 * interface are obtained from the Conditional Permission Admin service.
 * 
 * @version $Revision$
 */
public interface ConditionalPermissionInfo {
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
	 * Removes this Conditional Permission Info from the Conditional Permission
	 * Admin.
	 * 
	 * @throws SecurityException If the caller does not have
	 *         <code>AllPermission</code>.
	 */
	public void delete();

	/**
	 * Returns the name of this Conditional Permission Info.
	 * 
	 * @return The name of this Conditional Permission Info.
	 */
	public String getName();
}
