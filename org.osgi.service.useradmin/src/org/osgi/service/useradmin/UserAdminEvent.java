/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.useradmin;

import org.osgi.framework.ServiceReference;

/**
 * <code>Role</code> change event.
 * <p>
 * <code>UserAdminEvent</code> objects are delivered asynchronously to any
 * <code>UserAdminListener</code> objects when a change occurs in any of the
 * <code>Role</code> objects managed by a User Admin service.
 * 
 * <p>
 * A type code is used to identify the event. The following event types are
 * defined: {@link #ROLE_CREATED}type, {@link #ROLE_CHANGED}type, and
 * {@link #ROLE_REMOVED}type. Additional event types may be defined in the
 * future.
 * 
 * @see UserAdmin
 * @see UserAdminListener
 * 
 * @version $Revision$
 */
public class UserAdminEvent {
	private ServiceReference	ref;
	private int					type;
	private Role				role;
	/**
	 * A <code>Role</code> object has been created.
	 * 
	 * <p>
	 * The value of <code>ROLE_CREATED</code> is 0x00000001.
	 */
	public static final int		ROLE_CREATED	= 0x00000001;
	/**
	 * A <code>Role</code> object has been modified.
	 * 
	 * <p>
	 * The value of <code>ROLE_CHANGED</code> is 0x00000002.
	 */
	public static final int		ROLE_CHANGED	= 0x00000002;
	/**
	 * A <code>Role</code> object has been removed.
	 * 
	 * <p>
	 * The value of <code>ROLE_REMOVED</code> is 0x00000004.
	 */
	public static final int		ROLE_REMOVED	= 0x00000004;

	/**
	 * Constructs a <code>UserAdminEvent</code> object from the given
	 * <code>ServiceReference</code> object, event type, and <code>Role</code>
	 * object.
	 * 
	 * @param ref The <code>ServiceReference</code> object of the User Admin
	 *        service that generated this event.
	 * @param type The event type.
	 * @param role The <code>Role</code> object on which this event occurred.
	 */
	public UserAdminEvent(ServiceReference ref, int type, Role role) {
		this.ref = ref;
		this.type = type;
		this.role = role;
	}

	/**
	 * Gets the <code>ServiceReference</code> object of the User Admin service
	 * that generated this event.
	 * 
	 * @return The User Admin service's <code>ServiceReference</code> object.
	 */
	public ServiceReference getServiceReference() {
		return ref;
	}

	/**
	 * Returns the type of this event.
	 * 
	 * <p>
	 * The type values are {@link #ROLE_CREATED}type, {@link #ROLE_CHANGED}
	 * type, and {@link #ROLE_REMOVED}type.
	 * 
	 * @return The event type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the <code>Role</code> object this event was generated for.
	 * 
	 * @return The <code>Role</code> object this event was generated for.
	 */
	public Role getRole() {
		return role;
	}
}
