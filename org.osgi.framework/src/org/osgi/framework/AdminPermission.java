/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.framework;

import java.security.*;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Indicates the caller's authority to perform lifecycle operations on or to get
 * sensitive information about a bundle.
 * <p>
 * ### needs to be updated for RFC 73!
 * <code>AdminPermission</code> has no actions or target.
 * <p>
 * The <code>hashCode()</code> method of <code>AdminPermission</code> is inherited
 * from <code>java.security.BasicPermission</code>. The hash code it returns is
 * the hash code of the name "AdminPermission", which is always the same for all
 * instances of <code>AdminPermission</code>.
 * <p>
 * 
 * @version $Revision$
 */

public final class AdminPermission extends BasicPermission {
	static final long	serialVersionUID	= 207051004521261705L;

	/**
	 * Creates a new <code>AdminPermission</code> object with its name set to
	 * "AdminPermission".
	 */
	public AdminPermission() {
		super("AdminPermission");
	}

	/**
	 * Creates a new <code>AdminPermission</code> object for use by the
	 * <code>Policy</code> object to instantiate new <code>Permission</code>
	 * objects.
	 * 
	 * @param name Ignored; always set to "AdminPermission".
	 * @param actions Ignored.
	 */
	public AdminPermission(String name, String actions) {
		this();
	}

	/**
	 * Determines if the specified permission is implied by this object.
	 * <p>
	 * This method returns <code>true</code> if the specified permission is an
	 * instance of <code>AdminPermission</code>.
	 * 
	 * @param p The permission to interrogate.
	 * 
	 * @return <code>true</code> if the permission is an instance of this class;
	 *         <code>false</code> otherwise.
	 */
	public boolean implies(Permission p) {
		return (p instanceof AdminPermission);
	}

	/**
	 * Determines the equality of two <code>AdminPermission</code> objects.
	 * <p>
	 * Two <code>AdminPermission</code> objects are always equal.
	 * 
	 * @param obj The object being compared for equality with this object.
	 * @return <code>true</code> if <code>obj</code> is an <code>AdminPermission</code>;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		return (obj instanceof AdminPermission);
	}

	/**
	 * Returns a new <code>PermissionCollection</code> object suitable for storing
	 * <code>AdminPermission</code>s.
	 * <p>
	 * 
	 * @return A new <code>PermissionCollection</code> object.
	 */

	public PermissionCollection newPermissionCollection() {
		return (new AdminPermissionCollection());
	}
}

/**
 * Stores a collection of <code>AdminPermission</code>s.
 *  
 */

final class AdminPermissionCollection extends PermissionCollection {
	static final long	serialVersionUID	= 3226083842806530691L;
	/**
	 * True if collection is non-empty.
	 * 
	 * @serial
	 */
	private boolean		hasElement;

	/**
	 * Creates an empty <code>AdminPermission</code> object.
	 *  
	 */
	public AdminPermissionCollection() {
		hasElement = false;
	}

	/**
	 * Adds the specified permission to the <code>AdminPermissionCollection</code>.
	 * The key for the hash is the interface name of the service.
	 * 
	 * @param permission The <code>Permission</code> object to add.
	 * 
	 * @exception IllegalArgumentException If the permission is not an
	 *            <code>AdminPermission</code>.
	 * 
	 * @exception SecurityException If this AdminPermissionCollection object has
	 *            been marked read-only.
	 */

	public void add(Permission permission) {
		if (!(permission instanceof AdminPermission)) {
			throw new IllegalArgumentException("invalid permission: "
					+ permission);
		}

		if (isReadOnly())
			throw new SecurityException("attempt to add a Permission to a "
					+ "readonly PermissionCollection");

		hasElement = true;
	}

	/**
	 * Determines if the specified set of permissions implies the permissions
	 * expressed in the parameter <code>permission</code>.
	 * 
	 * @param p The Permission object to compare.
	 * 
	 * @return true if permission is a proper subset of a permission in the set;
	 *         false otherwise.
	 */

	public boolean implies(Permission p) {
		return (hasElement && (p instanceof AdminPermission));
	}

	/**
	 * Returns an enumeration of an <code>AdminPermission</code> object.
	 * 
	 * @return Enumeration of an <code>AdminPermission</code> object.
	 */

	public Enumeration elements() {
		return (new Enumeration() {
			private boolean	more	= hasElement;

			public boolean hasMoreElements() {
				return (more);
			}

			public Object nextElement() {
				if (more) {
					more = false;

					return (new AdminPermission());
				}
				else {
					throw new NoSuchElementException();
				}
			}
		});
	}
}

