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

/**
 * Implementation specific permission to read a bundle's resources.
 *
 */

final class BundleResourcePermission extends Permission {
	private static final long serialVersionUID = 3256728376969867573L;
	private long id;

	BundleResourcePermission(long id) {
		super(String.valueOf(id));

		this.id = id;
	}

	BundleResourcePermission(String id) {
		super(id);

		this.id = Long.parseLong(id);
	}

	/**
	 * Determines if the specified permission is implied by this object.
	 *
	 * @param p The target permission to interrogate.
	 * @return <tt>true</tt> if the specified permission is implied by
	 * this object; <tt>false</tt> otherwise.
	 */

	public boolean implies(Permission p) {
		if (p instanceof BundleResourcePermission) {
			BundleResourcePermission target = (BundleResourcePermission) p;

			return this.id == target.id;
		}

		return false;
	}

	/**
	 * Returns the empty String.
	 */

	public String getActions() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Determines the equality of two <tt>BundleResourcePermission</tt> objects.
	 *
	 * @param obj The object to test for equality with this object.
	 * @return <tt>true</tt> if <tt><i>obj</i></tt> is a <tt>BundleResourcePermission</tt>, and has the
	 * same bundle id this object; <tt>false</tt> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return (true);
		}

		if (!(obj instanceof BundleResourcePermission)) {
			return (false);
		}

		BundleResourcePermission target = (BundleResourcePermission) obj;

		return this.id == target.id;
	}

	/**
	 * Returns the hash code value for this object.
	 *
	 * @return A hash code value for this object.
	 */

	public int hashCode() {
		return getName().hashCode();
	}
}
