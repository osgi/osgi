/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.security.Permission;
import java.util.*;

/**
 * A combination of two BundlePermissionCollection classes.
 *
 */
final class BundleCombinedPermissions extends BundlePermissionCollection {
	private BundlePermissionCollection assigned;
	private BundlePermissionCollection implied;

	/**
	 * Create a permission combiner class.
	 *
	 * @param implied The permissions a bundle always has.
	 */
	BundleCombinedPermissions(BundlePermissionCollection implied) {
		this.implied = implied;

		setReadOnly(); /* this doesn't really mean anything */
	}

	/**
	 * Assign the administrator defined permissions.
	 *
	 * @param assigned The permissions assigned by the administrator.
	 */
	void setAssignedPermissions(BundlePermissionCollection assigned) {
		this.assigned = assigned;
	}

	/**
	 * The Permission collection will unresolve the permissions in these packages.
	 *
	 * @param unresolvedPackages A list of the package which have been unresolved
	 * as a result of a packageRefresh
	 */
	void unresolvePermissions(Hashtable unresolvedPackages) {
		if (assigned != null) {
			assigned.unresolvePermissions(unresolvedPackages);
		}

		if (implied != null) {
			implied.unresolvePermissions(unresolvedPackages);
		}
	}

	/**
	 * Adds the argument to the collection.
	 *
	 * @param		permission java.security.Permission
	 *					the permission to add to the collection.
	 * @exception	SecurityException
	 *					if the collection is read only.
	 */
	public void add(Permission permission) {
		throw new SecurityException();
	}

	/**
	 * Answers an enumeration of the permissions
	 * in the receiver.
	 *
	 * @return		Enumeration
	 *					the permissions in the receiver.
	 */
	public Enumeration elements() {
		return new Enumeration() {
			private int i = 0;
			private Enumeration[] enums;

			{
				enums = new Enumeration[] {(assigned == null) ? null : assigned.elements(), (implied == null) ? null : implied.elements()};
			}

			/**
			 * Answers if this Enumeration has more elements.
			 *
			 * @return		true if there are more elements, false otherwise
			 *
			 * @see			#nextElement
			 */
			public boolean hasMoreElements() {
				while (i < enums.length) {
					Enumeration enum = enums[i];
					if ((enum != null) && enum.hasMoreElements()) {
						return true;
					}

					i++;
				}

				return false;
			}

			/**
			 * Answers the next element in this Enumeration.
			 *
			 * @return		the next element in this Enumeration
			 *
			 * @exception	NoSuchElementException when there are no more elements
			 *
			 * @see			#hasMoreElements
			 */
			public Object nextElement() {
				while (i < enums.length) {
					try {
						Enumeration enum = enums[i];
						if (enum != null) {
							return enum.nextElement();
						}
					} catch (NoSuchElementException e) {
					}
					i++;
				}

				throw new NoSuchElementException();
			}
		};
	}

	/**
	 * Indicates whether the argument permission is implied
	 * by the permissions contained in the receiver.
	 *
	 * @return		boolean
	 *					<code>true</code> if the argument permission
	 *					is implied by the permissions in the receiver,
	 *					and <code>false</code> if it is not.
	 * @param		permission java.security.Permission
	 *					the permission to check
	 */
	public boolean implies(Permission permission) {
		return ((assigned != null) && assigned.implies(permission)) || ((implied != null) && implied.implies(permission));
	}
}

