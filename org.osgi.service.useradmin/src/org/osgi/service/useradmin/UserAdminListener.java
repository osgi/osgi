/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.useradmin;

/**
 * Listener for UserAdminEvents.
 * 
 * <p>
 * <code>UserAdminListener</code> objects are registered with the Framework
 * service registry and notified with a <code>UserAdminEvent</code> object when a
 * <code>Role</code> object has been created, removed, or modified.
 * <p>
 * <code>UserAdminListener</code> objects can further inspect the received
 * <code>UserAdminEvent</code> object to determine its type, the <code>Role</code>
 * object it occurred on, and the User Admin service that generated it.
 * 
 * @see UserAdmin
 * @see UserAdminEvent
 * 
 * @version $Revision$
 */
public interface UserAdminListener {
	/**
	 * Receives notification that a <code>Role</code> object has been created,
	 * removed, or modified.
	 * 
	 * @param event The <code>UserAdminEvent</code> object.
	 */
	public void roleChanged(UserAdminEvent event);
}
