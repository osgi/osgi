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

import org.osgi.framework.ServiceReference;

/**
 * {@code Role} change event.
 * <p>
 * {@code UserAdminEvent} objects are delivered asynchronously to any
 * {@code UserAdminListener} objects when a change occurs in any of the
 * {@code Role} objects managed by a User Admin service.
 * 
 * <p>
 * A type code is used to identify the event. The following event types are
 * defined: {@link #ROLE_CREATED} type, {@link #ROLE_CHANGED} type, and
 * {@link #ROLE_REMOVED} type. Additional event types may be defined in the
 * future.
 * 
 * @see UserAdmin
 * @see UserAdminListener
 * 
 * @author $Id$
 */
public class UserAdminEvent {
	private ServiceReference<UserAdmin>	ref;
	private int					type;
	private Role				role;
	/**
	 * A {@code Role} object has been created.
	 * 
	 * <p>
	 * The value of {@code ROLE_CREATED} is 0x00000001.
	 */
	public static final int		ROLE_CREATED	= 0x00000001;
	/**
	 * A {@code Role} object has been modified.
	 * 
	 * <p>
	 * The value of {@code ROLE_CHANGED} is 0x00000002.
	 */
	public static final int		ROLE_CHANGED	= 0x00000002;
	/**
	 * A {@code Role} object has been removed.
	 * 
	 * <p>
	 * The value of {@code ROLE_REMOVED} is 0x00000004.
	 */
	public static final int		ROLE_REMOVED	= 0x00000004;

	/**
	 * Constructs a {@code UserAdminEvent} object from the given
	 * {@code ServiceReference} object, event type, and {@code Role} object.
	 * 
	 * @param ref The {@code ServiceReference} object of the User Admin service
	 *        that generated this event.
	 * @param type The event type.
	 * @param role The {@code Role} object on which this event occurred.
	 */
	public UserAdminEvent(ServiceReference<UserAdmin> ref, int type, Role role) {
		this.ref = ref;
		this.type = type;
		this.role = role;
	}

	/**
	 * Gets the {@code ServiceReference} object of the User Admin service that
	 * generated this event.
	 * 
	 * @return The User Admin service's {@code ServiceReference} object.
	 */
	public ServiceReference<UserAdmin> getServiceReference() {
		return ref;
	}

	/**
	 * Returns the type of this event.
	 * 
	 * <p>
	 * The type values are {@link #ROLE_CREATED} type, {@link #ROLE_CHANGED}
	 * type, and {@link #ROLE_REMOVED} type.
	 * 
	 * @return The event type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the {@code Role} object this event was generated for.
	 * 
	 * @return The {@code Role} object this event was generated for.
	 */
	public Role getRole() {
		return role;
	}
}
