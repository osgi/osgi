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

import java.security.*;
import java.util.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.osgi.framework.Bundle;

/**
 * A heterogeneous collection of permissions for a bundle.
 *
 */
final class BundlePermissions extends BundlePermissionCollection {
	private static final long serialVersionUID = 3257844389794428984L;

	/**
	 * Maps a Permission's class to an appropriate PermissionCollection.
	 * Class => PermissionCollection
	 */
	private Hashtable collections = new Hashtable(8); //TODO How dynamic is this? 

	/**
	 * Set to an AllPermissionCollection if this Permissions contains AllPermission.
	 */
	private PermissionCollection allPermission;

	/** Used to load classes for unresolved permissions */
	private PackageAdminImpl packageAdmin;

	/**
	 * Constructs a new instance of this class.
	 *
	 */
	BundlePermissions(PackageAdminImpl packageAdmin) {
		super();

		this.packageAdmin = packageAdmin;
	}

	/**
	 * Adds the argument to the collection.
	 *
	 * @param		permission java.security.Permission
	 *					the permission to add to the collection
	 */
	public void add(Permission permission) {
		if (isReadOnly()) {
			throw new SecurityException();
		}

		PermissionCollection collection;

		synchronized (collections) {
			collection = findCollection(permission);

			if (collection == null) {
				collection = newPermissionCollection(permission);
			}
		}

		if (permission instanceof AllPermission) {
			allPermission = collection;
		}

		collection.add(permission);
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
			Enumeration enumMap = collections.elements();
			PermissionCollection c;
			Enumeration enumC;
			Permission next = findNextPermission();

			public boolean hasMoreElements() {
				return (next != null);
			}

			public Object nextElement() {
				if (next == null) {
					throw new NoSuchElementException();
				} else {
					Object answer = next;
					next = findNextPermission();
					return (answer);
				}
			}

			// This method is the important one. It looks for and
			// answers the next available permission. If there are
			// no permissions left to return, it answers null.
			private Permission findNextPermission() {
				// Loop until we get a collection with at least one element.
				while (c == null && enumMap.hasMoreElements()) {
					c = (PermissionCollection) enumMap.nextElement();
					enumC = c.elements();
					if (!enumC.hasMoreElements())
						c = null;
				}
				// At this point, c == null if there are no more elements,
				// and otherwise is the first collection with a free element
				// (with enumC set up to return that element).
				if (c == null) {
					// no more elements, so return null;
					return (null);
				} else {
					Permission answer = (Permission) enumC.nextElement();
					if (!enumC.hasMoreElements())
						c = null;
					return (answer);
				}
			}
		};
	}

	/**
	 * Find the appropriate permission collection to use for
	 * the given permission.
	 *
	 * @param		permission Permission
	 *					the permission to find a collection for
	 * @return		PermissionCollection
	 *					the collection to use with the permission.
	 */
	private PermissionCollection findCollection(Permission permission) {
		Class clazz = permission.getClass();

		PermissionCollection collection = (PermissionCollection) collections.get(clazz);

		if (collection == null) {
			synchronized (collections) {
				collection = (PermissionCollection) collections.get(clazz);

				if (collection == null) {
					collection = resolvePermissions(permission);
				}
			}
		}

		return collection;
	}

	/**
	 * This method will attempt to resolve unresolved permissions of the
	 * type of the specified permission.
	 *
	 * This method should only be called while holding the collections lock.
	 *
	 * @param permission Permission whose type we shall attempt to resolve.
	 * @return A PermissionCollection for the resolved permissions or
	 * <tt>null</tt> if the permissions cannot currently be resolved.
	 */
	private PermissionCollection resolvePermissions(Permission permission) {
		UnresolvedPermissionCollection unresolvedCollection = (UnresolvedPermissionCollection) collections.get(UnresolvedPermission.class);

		if (unresolvedCollection != null) {
			String name = permission.getClass().getName();

			Vector permissions = unresolvedCollection.getPermissions(name);

			if (permissions != null) {
				PermissionCollection collection = null;
				Class clazz;

				// TODO need to evaluate if this still applies with multiple versions of a package
				// We really need to resolve the permission
				// by loading it only from the proper classloader,
				// i.e. the system classloader or and exporting bundle's
				// classloader. Otherwise there is a security hole.
				// clazz = packageAdmin.loadServiceClass(name, null);
				clazz = permission.getClass();
				if (clazz == null) {
					return null;
				}

				Enumeration permsEnum = permissions.elements();

				while (permsEnum.hasMoreElements()) {
					Permission resolved = ((UnresolvedPermission) permsEnum.nextElement()).resolve(clazz);

					if (resolved != null) {
						if (collection == null) {
							collection = newPermissionCollection(resolved);
						}

						collection.add(resolved);
					}
				}

				return collection;
			}
		}

		return null;
	}

	/**
	 * Creates a PermissionCollection suitable to hold the specified permission.
	 * The created collection is added to the collections Hashtable.
	 *
	 * This method should only be called while holding the collections lock.
	 */
	private PermissionCollection newPermissionCollection(Permission permission) {
		PermissionCollection collection = permission.newPermissionCollection();

		if (collection == null) {
			collection = new PermissionsHash();
		}

		collections.put(permission.getClass(), collection);

		return collection;
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
		if ((allPermission != null) && allPermission.implies(perm)) {
			return true;
		}

		PermissionCollection collection = findCollection(perm);

		if (collection == null) {
			return false;
		}

		return collection.implies(perm);
	}

	/**
	 * The Permission collection will unresolve the permissions in these packages.
	 *
	 * @param refreshedBundles A list of the bundles which have been refreshed
	 * as a result of a packageRefresh
	 */
	void unresolvePermissions(AbstractBundle[] refreshedBundles) {
		synchronized (collections) {
			int size = collections.size();

			Class[] clazzes = new Class[size];
			Enumeration keysEnum = collections.keys();

			for (int i = 0; i < size; i++) {
				clazzes[i] = (Class) keysEnum.nextElement();
			}

			for (int i = 0; i < size; i++) {
				Class clazz = clazzes[i];
				Bundle bundle = packageAdmin.getBundle(clazz);
				if (bundle == null)
					continue;
				for (int j = 0; j < refreshedBundles.length; j++)
					if (refreshedBundles[j] == bundle) {
						if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
							Debug.println("  Unresolving permission class " + clazz.getName()); //$NON-NLS-1$
						}
						collections.remove(clazz);
						continue;
					}
			}
		}
	}
}
