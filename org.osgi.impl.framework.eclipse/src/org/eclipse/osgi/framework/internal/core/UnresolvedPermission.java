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

import java.lang.reflect.Constructor;
import java.security.Permission;
import java.security.PermissionCollection;
import org.eclipse.osgi.framework.debug.Debug;

/**
 * Holds permissions which are of an unknown type when a
 * policy file is read
 *
 */
final class UnresolvedPermission extends Permission {
	private static final long serialVersionUID = 3546358422783079475L;
	/**
	 * The type of permission this will be
	 */
	private String type;
	/**
	 * the action string
	 */
	private String actions;
	/**
	 * name of the permission
	 */
	private String name;

	private static Class[] constructorArgs;

	static {
		Class string = String.class;
		constructorArgs = new Class[] {string, string};
	}

	/**
	 * Constructs a new instance of this class with its
	 * type, name, and certificates set to the arguments
	 * by definition, actions are ignored
	 *
	 * @param 	type the type class of permission object
	 * @param	name the name of the permission
	 * @param	actions the actions
	 */
	UnresolvedPermission(String type, String name, String actions) {
		super(type);
		this.name = name;
		this.type = type;
		this.actions = actions;
	}

	/**
	 * Compares the argument to the receiver, and answers true
	 * if they represent the <em>same</em> object using a class
	 * specific comparison. In this case, the receiver and the
	 * object must have the same class, permission name,
	 * actions, and certificates
	 *
	 * @param		obj		the object to compare with this object
	 * @return		<code>true</code>
	 *					if the object is the same as this object
	 *				<code>false</code>
	 *					if it is different from this object
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof UnresolvedPermission)) {
			return false;
		}

		UnresolvedPermission perm = (UnresolvedPermission) obj;

		return type.equals(perm.type) && name.equals(perm.name) && actions.equals(perm.actions);
	}

	/**
	 * Indicates whether the argument permission is implied
	 * by the receiver. UnresolvedPermission objects imply
	 * nothing because nothing is known about them yet.
	 *
	 * @return		boolean always replies false
	 * @param		p java.security.Permission
	 *					the permission to check
	 */
	public boolean implies(Permission p) {
		return false;
	}

	/**
	 * Answers a new PermissionCollection for holding permissions
	 * of this class. Answer null if any permission collection can
	 * be used.
	 *
	 * @return		a new PermissionCollection or null
	 *
	 */
	public PermissionCollection newPermissionCollection() {
		return new UnresolvedPermissionCollection();
	}

	/**
	 * Answers the actions associated with the receiver.
	 * Since UnresolvedPermission objects have no actions, answer
	 * the empty string.
	 *
	 * @return		String
	 *					the actions associated with the receiver.
	 */
	public String getActions() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Answers an integer hash code for the receiver. Any two
	 * objects which answer <code>true</code> when passed to
	 * <code>equals</code> must answer the same value for this
	 * method.
	 *
	 * @return		int
	 *					the receiver's hash
	 *
	 */
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Answers a string containing a concise, human-readable
	 * description of the receiver.
	 *
	 * @return		String
	 *					a printable representation for the receiver.
	 */
	public String toString() {
		return "(unresolved " + type + " " + name + " " + actions + ")";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	Permission resolve(Class clazz) {
		if (clazz.getName().equals(type)) {
			try {
				Constructor constructor = clazz.getConstructor(constructorArgs);

				Permission permission = (Permission) constructor.newInstance(new Object[] {name, actions});

				if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
					Debug.println("Resolved " + this); //$NON-NLS-1$
				}

				return permission;
			} catch (Exception e) {
				/* Ignore any error trying to resolve the permission */
				if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
					Debug.println("Exception trying to resolve permission"); //$NON-NLS-1$
					Debug.printStackTrace(e);
				}
			}
		}

		return null;
	}
}
