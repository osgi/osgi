/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.useradmin;

import java.util.Dictionary;

import org.osgi.service.useradmin.Role;

/**
 * {@link org.osgi.service.useradmin.Role}implementation.
 *  
 */
public class RoleImpl implements Role {
	/**
	 * The role properties.
	 */
	protected UAProperties	properties;
	/**
	 * Link to the UserAdmin controlling this role.
	 */
	protected UserAdminImpl	ua;
	/**
	 * The name of this role.
	 */
	protected String		name;
	/**
	 * The type of this group.
	 */
	protected int			type;

	protected RoleImpl(UserAdminImpl ua, String name, int type) {
		this.ua = ua;
		this.name = name;
		this.type = type;
		properties = new UAProperties(this);
	}

	/**
	 * Gets the name of this role. Implementation of
	 * {@link org.osgi.service.useradmin.Role#getName}.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Gets the type of this role. Implementation of
	 * {@link org.osgi.service.useradmin.Role#getType}.
	 */
	@Override
	public int getType() {
		return type;
	}

	/**
	 * Gets the properties of this role. Implementation of
	 * {@link org.osgi.service.useradmin.Role#getProperties}.
	 */
	@Override
	public Dictionary<String,Object> getProperties() {
		return properties;
	}

	/* -------------- Protected methods ----------------- */
	/**
	 * Checks if this role is implied by the specified authorization context.
	 */
	protected boolean impliedBy(AuthorizationImpl auth) {
		// Have we already checked this role?
		Boolean b = auth.cachedHaveRole(this);
		// b is now true (we are implied), false (we are not implied),
		// or null (we don't know yet).
		if (b != null)
			return b.booleanValue();
		// Default to say that the role is implied if the name of the
		// specified role is the same as the name of this role, or if
		// this role is "user.anyone".
		String rolename = auth.getName();
		boolean implied = (rolename != null && rolename.equals(name))
				|| name.equals(UserAdminImpl.ANYONE);
		// Cache and return the result.
		return auth.cacheHaveRole(this, implied);
	}

	/**
	 * Removes references to the specified role.
	 */
	protected void removeReferenceTo(RoleImpl role) {
		// empty
	}
}
