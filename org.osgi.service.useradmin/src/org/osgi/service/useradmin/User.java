/*
 * Copyright (c) OSGi Alliance (2001, 2015). All Rights Reserved.
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

package org.osgi.service.useradmin;

import java.util.Dictionary;

/**
 * A {@code User} role managed by a User Admin service.
 * 
 * <p>
 * In this context, the term &quot;user&quot; is not limited to just human
 * beings. Instead, it refers to any entity that may have any number of
 * credentials associated with it that it may use to authenticate itself.
 * <p>
 * In general, {@code User} objects are associated with a specific User Admin
 * service (namely the one that created them), and cannot be used with other
 * User Admin services.
 * <p>
 * A {@code User} object may have credentials (and properties, inherited from
 * the {@link Role} class) associated with it. Specific
 * {@link UserAdminPermission} objects are required to read or change a
 * {@code User} object's credentials.
 * <p>
 * Credentials are {@code Dictionary} objects and have semantics that are
 * similar to the properties in the {@code Role} class.
 * 
 * @noimplement
 * @author $Id$
 */
public interface User extends Role {
	/**
	 * Returns a {@code Dictionary} of the credentials of this {@code User}
	 * object. Any changes to the returned {@code Dictionary} object will change
	 * the credentials of this {@code User} object. This will cause a
	 * {@code UserAdminEvent} object of type {@link UserAdminEvent#ROLE_CHANGED}
	 * to be broadcast to any {@code UserAdminListeners} objects.
	 * 
	 * <p>
	 * Only objects of type {@code String} may be used as credential keys, and
	 * only objects of type {@code String} or of type {@code byte[]} may be used
	 * as credential values. Any other types will cause an exception of type
	 * {@code IllegalArgumentException} to be raised.
	 * 
	 * <p>
	 * In order to retrieve a credential from the returned {@code Dictionary}
	 * object, a {@link UserAdminPermission} named after the credential name (or
	 * a prefix of it) with action {@code getCredential} is required.
	 * <p>
	 * In order to add or remove a credential from the returned
	 * {@code Dictionary} object, a {@link UserAdminPermission} named after the
	 * credential name (or a prefix of it) with action {@code changeCredential}
	 * is required.
	 * 
	 * @return {@code Dictionary} object containing the credentials of this
	 *         {@code User} object.
	 */
	public Dictionary<String, Object> getCredentials();

	/**
	 * Checks to see if this {@code User} object has a credential with the
	 * specified {@code key} set to the specified {@code value}.
	 * 
	 * <p>
	 * If the specified credential {@code value} is not of type {@code String}
	 * or {@code byte[]}, it is ignored, that is, {@code false} is returned (as
	 * opposed to an {@code IllegalArgumentException} being raised).
	 * 
	 * @param key The credential {@code key}.
	 * @param value The credential {@code value}.
	 * 
	 * @return {@code true} if this user has the specified credential;
	 *         {@code false} otherwise.
	 * 
	 * @throws SecurityException If a security manager exists and the caller
	 *         does not have the {@code UserAdminPermission} named after the
	 *         credential key (or a prefix of it) with action
	 *         {@code getCredential}.
	 */
	public boolean hasCredential(String key, Object value);
}
