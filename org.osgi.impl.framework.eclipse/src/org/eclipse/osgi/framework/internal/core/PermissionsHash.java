/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A simple Hashtable based collection of Permission objects.
 * <p>
 * The class' .implies method simply scans each permission
 * individually and asks if the permission should be granted.
 * No addition semantics is provided by the collection, so it is
 * not possible to grant permissions whose "grantedness" is
 * split across multiple stored Permissions.
 * <p>
 * Instances of this class can be used to store heterogeneous
 * collections of permissions, as long as it is not necessary
 * to remember when multiple occurances of .equal permissions
 * are added.
 *
 */
class PermissionsHash extends PermissionCollection {
	private static final long serialVersionUID = 3258408426341284153L;
	/**
	 * A hashtable to store the elements of the collection.
	 */
	Hashtable perms = new Hashtable(8);

	/**
	 * Constructs a new instance of this class.
	 *
	 */
	public PermissionsHash() {
		super();
	}

	/**
	 * Adds the argument to the collection.
	 *
	 * @param		perm java.security.Permission
	 *					the permission to add to the collection.
	 * @exception	IllegalStateException
	 *					if the collection is read only.
	 */
	public void add(Permission perm) {
		if (isReadOnly()) {
			throw new SecurityException();
		}

		perms.put(perm, perm);
	}

	/**
	 * Answers an enumeration of the permissions
	 * in the receiver.
	 *
	 * @return		Enumeration
	 *					the permissions in the receiver.
	 */
	public Enumeration elements() {
		return perms.keys();
	}

	/**
	 * Indicates whether the argument permission is implied
	 * by the permissions contained in the receiver.
	 *
	 * @return		boolean
	 *					<code>true</code> if the argument permission
	 *					is implied by the permissions in the receiver,
	 *					and <code>false</code> if it is not.
	 * @param		perm java.security.Permission
	 *					the permission to check
	 */
	public boolean implies(Permission perm) {
		Permission p = (Permission) perms.get(perm);

		if ((p != null) && p.implies(perm)) {
			return true;
		}

		Enumeration permsEnum = elements();

		while (permsEnum.hasMoreElements()) {
			if (((Permission) permsEnum.nextElement()).implies(perm)) {
				return true;
			}
		}

		return false;
	}
}
