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

import org.osgi.service.useradmin.UserAdminPermission;

/**
 * A dictionary with permissions checks for getting, setting, changing, and
 * deleting values.
 * <p>
 * User credentials are instances of this class. Changes to these objects are
 * propagated to the {@link UserAdmin}service and made persistent. Only keys of
 * the type <code>String</code> are supported, and only values of type
 * <code>String</code> or of type <code>byte[]</code> are supported. If an
 * attempt is made to store a value of a type other than what is supported, an
 * exception of type <code>IllegalArgumentException</code> will be raised.
 * <p>
 * A {@link UserAdminPermission}with the action <code>getCredential</code> is
 * required to getting dictionary values, and the action
 * <code>changeCredential</code> to set, change, or remove dictionary values.
 * The permission key should be the (or a prefix of) the credential key that is
 * looked up, created, changed, or deleted.
 */
public class UACredentials extends UAProperties {
	public UACredentials(RoleImpl role) {
		super(role);
	}

	/**
	 * The permission need to modify the credentials.
	 */
	@Override
	protected String getChangeAction() {
		return UserAdminPermission.CHANGE_CREDENTIAL;
	}

	@Override
	public Object get(Object key) {
		synchronized (role.ua.activator) {
			if (key instanceof String) {
				// Check that the caller are allowed to get the credential.
				role.ua.checkPermission(new UserAdminPermission((String) key,
						UserAdminPermission.GET_CREDENTIAL));
				return super.get(key);
			}
			else
				throw new IllegalArgumentException(
						"The key must be a String, got " + key.getClass());
		}
	}

	@Override
	public String toString() {
		return "#Credentials#";
	}
}
