/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import java.util.*;

/**
 * A combination of two BundlePermissionCollection classes.
 *
 */
final class BundleCombinedPermissions extends BundlePermissionCollection {
	private static final long serialVersionUID = 4049357526208360496L;
	private BundlePermissionCollection assigned;
	private BundlePermissionCollection implied;
	private ConditionalPermissions conditional;
	private ConditionalPermissionSet restrictedPermissions;
	private boolean isDefault;

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
	 * @param isDefault If true, the assigned permissions are the default permissions.
	 */
	void setAssignedPermissions(BundlePermissionCollection assigned, boolean isDefault) {
		this.assigned = assigned;
		this.isDefault = isDefault;
	}

	/**
	 * Assign the conditional permissions
	 * 
	 * @param conditional The conditional permissions assigned by the administrator
	 */
	void setConditionalPermissions(ConditionalPermissions conditional) {
		this.conditional = conditional;
	}

	void checkConditionalPermissionInfo(ConditionalPermissionInfoImpl cpi) {
		if (conditional != null) {
			conditional.checkConditionalPermissionInfo(cpi);
		}
	}

	/**
	 * The Permission collection will unresolve the permissions in these packages.
	 *
	 * @param refreshedBundles A list of the bundles which have been refreshed
	 * as a result of a packageRefresh
	 */
	void unresolvePermissions(AbstractBundle[] refreshedBundles) {
		if (assigned != null) {
			assigned.unresolvePermissions(refreshedBundles);
		}

		if (implied != null) {
			implied.unresolvePermissions(refreshedBundles);
		}

		if (conditional != null) {
			conditional.unresolvePermissions(refreshedBundles);
		}

		if (restrictedPermissions != null) {
			restrictedPermissions.unresolvePermissions(refreshedBundles);
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
					Enumeration perms = enums[i];
					if ((perms != null) && perms.hasMoreElements()) {
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
						Enumeration perms = enums[i];
						if (perms != null) {
							return perms.nextElement();
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
		if ((implied != null) && implied.implies(permission))
			return true;

		/* We must be allowed by the restricted permissions to have any hope of
		 * passing the check */
		if ((restrictedPermissions != null) && !restrictedPermissions.implies(permission)) {
			return false;
		}

		/* If we aren't using the default permissions, then the assigned
		 * permission are the exact permissions the bundle has. */
		if (!isDefault && assigned != null)
			return assigned.implies(permission);
		if (conditional != null) {
			boolean conditionalImplies = conditional.implies(permission);
			if (!conditional.isEmpty()) {
				return conditionalImplies;
			}
		}
		/* If there aren't any conditional permissions that apply, we use
		 * the default. */
		return assigned.implies(permission);
	}

	/**
	 * Sets the restricted Permissions of the Bundle. This set of Permissions limit the
	 * Permissions available to the Bundle.
	 * 
	 * @param restrictedPermissions the maximum set of permissions allowed to the Bundle 
	 * irrespective of the actual permissions assigned to it.
	 */
	public void setRestrictedPermissions(ConditionalPermissionSet restrictedPermissions) {
		this.restrictedPermissions = restrictedPermissions;
	}
}
