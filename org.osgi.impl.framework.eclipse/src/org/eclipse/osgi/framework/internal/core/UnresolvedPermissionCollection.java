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
import java.util.*;

/**
 * Holds permissions which are of an unknown type when a
 * policy file is read
 *
 */
final class UnresolvedPermissionCollection extends PermissionCollection {
	private static final long serialVersionUID = 3618703006602703161L;
	/** hash of permission class names => Vectors of UnresolvedPermissions for that class */
	Hashtable permissions = new Hashtable(8);

	UnresolvedPermissionCollection() {
		super();
	}

	public void add(Permission permission) {
		if (isReadOnly()) {
			throw new IllegalStateException();
		}

		String name = permission.getName();

		Vector elements;

		synchronized (permissions) {
			elements = (Vector) permissions.get(name);

			if (elements == null) {
				elements = new Vector(10, 10);

				permissions.put(name, elements);
			}
		}

		elements.addElement(permission);
	}

	public Enumeration elements() {
		return (new Enumeration() {
			Enumeration vEnum, pEnum = permissions.elements();
			Object next = findNext();

			private Object findNext() {
				if (vEnum != null) {
					if (vEnum.hasMoreElements())
						return (vEnum.nextElement());
				}
				if (!pEnum.hasMoreElements())
					return (null);
				vEnum = ((Vector) pEnum.nextElement()).elements();
				return (vEnum.nextElement());
			}

			public boolean hasMoreElements() {
				return (next != null);
			}

			public Object nextElement() {
				Object result = next;
				next = findNext();
				return (result);
			}
		});
	}

	public boolean implies(Permission permission) {
		return false;
	}

	Vector getPermissions(String name) {
		return (Vector) permissions.get(name);
	}
}
