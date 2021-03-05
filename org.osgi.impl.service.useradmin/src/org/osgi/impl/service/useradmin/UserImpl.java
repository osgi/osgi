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

import java.util.Arrays;
import java.util.Dictionary;

import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

/**
 * {@link org.osgi.service.useradmin.User}implementation.
 *  
 */
public class UserImpl extends RoleImpl implements User {
	/**
	 * The user credentials.
	 */
	protected UACredentials	credentials;

	/**
	 * Provide a constructor to be used by extending classes.
	 */
	protected UserImpl(UserAdminImpl ua, String name, int type) {
		super(ua, name, type);
		credentials = new UACredentials(this);
	}

	/**
	 * User-specific constructor.
	 */
	protected UserImpl(UserAdminImpl ua, String name) {
		this(ua, name, Role.USER);
	}

	/**
	 * Gets the credentials of this role. Implementation of
	 * {@link org.osgi.service.useradmin.User#getCredentials}.
	 */
	@Override
	public Dictionary<String,Object> getCredentials() {
		return credentials;
	}

	/**
	 * Checks if this user has the specified credential. Implementation of
	 * {@link org.osgi.service.useradmin.User#hasCredential}.
	 */
	@Override
	public boolean hasCredential(String key, Object value) {
		Object rvalue = credentials.get(key);
		if (rvalue == null)
			return false;
		if (value instanceof String) {
			return rvalue instanceof String && value.equals(rvalue);
		}
		else
			if (value instanceof byte[]) {
				return rvalue instanceof byte[]
						&& Arrays.equals((byte[]) value, (byte[]) rvalue);
			}
			else
				throw new IllegalArgumentException(
						"value must be of type String or byte[]");
	}
}
