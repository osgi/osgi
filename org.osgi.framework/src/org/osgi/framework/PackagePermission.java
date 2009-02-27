/*
 * Copyright (c) OSGi Alliance (2000, 2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.framework;

import java.io.IOException;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A bundle's authority to import or export a package.
 * 
 * <p>
 * A package is a dot-separated string that defines a fully qualified Java
 * package.
 * <p>
 * For example:
 * 
 * <pre>
 * org.osgi.service.http
 * </pre>
 * 
 * <p>
 * <code>PackagePermission</code> has three actions: <code>exportonly</code>,
 * <code>import</code> and <code>export</code>. The <code>export</code> action,
 * which is deprecated, implies the <code>import</code> action.
 * 
 * @ThreadSafe
 * @version $Revision$
 */

public final class PackagePermission extends BasicPermission {
	static final long			serialVersionUID	= -5107705877071099135L;

	/**
	 * The action string <code>export</code>. The <code>export</code> action
	 * implies the <code>import</code> action.
	 * 
	 * @deprecated Since 1.5. Use {@link #EXPORTONLY} instead.
	 */
	public final static String	EXPORT				= "export";

	/**
	 * The action string <code>exportonly</code>.
	 * 
	 * @since 1.5
	 */
	public final static String	EXPORTONLY			= "exportonly";

	/**
	 * The action string <code>import</code>.
	 */
	public final static String	IMPORT				= "import";

	private final static int	ACTION_EXPORT		= 0x00000001;
	private final static int	ACTION_IMPORT		= 0x00000002;
	private final static int	ACTION_ALL			= ACTION_EXPORT
															| ACTION_IMPORT;
	private final static int	ACTION_NONE			= 0;

	/**
	 * The actions mask.
	 * 
	 * @GuardedBy this
	 */
	private transient int		action_mask;

	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private volatile String		actions				= null;

	/**
	 * Defines the authority to import and/or export a package within the OSGi
	 * environment.
	 * <p>
	 * The name is specified as a normal Java package name: a dot-separated
	 * string. Wildcards may be used. For example:
	 * 
	 * <pre>
	 * org.osgi.service.http
	 * javax.servlet.*
	 * *
	 * </pre>
	 * 
	 * <p>
	 * Package Permissions are granted over all possible versions of a package.
	 * 
	 * A bundle that needs to export a package must have the appropriate
	 * <code>PackagePermission</code> for that package; similarly, a bundle that
	 * needs to import a package must have the appropriate
	 * <code>PackagePermssion</code> for that package.
	 * <p>
	 * Permission is granted for both classes and resources.
	 * 
	 * @param name Package name.
	 * @param actions <code>export</code>,<code>import</code> (canonical order).
	 */
	public PackagePermission(String name, String actions) {
		this(name, parseActions(actions));
	}

	/**
	 * Package private constructor used by PackagePermissionCollection.
	 * 
	 * @param name class name
	 * @param mask action mask
	 */
	PackagePermission(String name, int mask) {
		super(name);
		setTransients(mask);
	}

	/**
	 * Called by constructors and when deserialized.
	 * 
	 * @param mask action mask
	 */
	private synchronized void setTransients(int mask) {
		if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask)) {
			throw new IllegalArgumentException("invalid action string");
		}
		action_mask = mask;
	}

	/**
	 * Returns the current action mask.
	 * <p>
	 * Used by the PackagePermissionCollection class.
	 * 
	 * @return Current action mask.
	 */
	synchronized int getActionsMask() {
		return action_mask;
	}

	/**
	 * Parse action string into action mask.
	 * 
	 * @param actions Action string.
	 * @return action mask.
	 */
	private static int parseActions(String actions) {
		boolean seencomma = false;

		int mask = ACTION_NONE;

		if (actions == null) {
			return mask;
		}

		char[] a = actions.toCharArray();

		int i = a.length - 1;
		if (i < 0)
			return mask;

		while (i != -1) {
			char c;

			// skip whitespace
			while ((i != -1)
					&& ((c = a[i]) == ' ' || c == '\r' || c == '\n'
							|| c == '\f' || c == '\t'))
				i--;

			// check for the known strings
			int matchlen;

			if (i >= 5 && (a[i - 5] == 'i' || a[i - 5] == 'I')
					&& (a[i - 4] == 'm' || a[i - 4] == 'M')
					&& (a[i - 3] == 'p' || a[i - 3] == 'P')
					&& (a[i - 2] == 'o' || a[i - 2] == 'O')
					&& (a[i - 1] == 'r' || a[i - 1] == 'R')
					&& (a[i] == 't' || a[i] == 'T')) {
				matchlen = 6;
				mask |= ACTION_IMPORT;

			}
			else
				if (i >= 5 && (a[i - 5] == 'e' || a[i - 5] == 'E')
						&& (a[i - 4] == 'x' || a[i - 4] == 'X')
						&& (a[i - 3] == 'p' || a[i - 3] == 'P')
						&& (a[i - 2] == 'o' || a[i - 2] == 'O')
						&& (a[i - 1] == 'r' || a[i - 1] == 'R')
						&& (a[i] == 't' || a[i] == 'T')) {
					matchlen = 6;
					mask |= ACTION_EXPORT | ACTION_IMPORT;

				}
				else {
					if (i >= 9 && (a[i - 9] == 'e' || a[i - 9] == 'E')
							&& (a[i - 8] == 'x' || a[i - 8] == 'X')
							&& (a[i - 7] == 'p' || a[i - 7] == 'P')
							&& (a[i - 6] == 'o' || a[i - 6] == 'O')
							&& (a[i - 5] == 'r' || a[i - 5] == 'R')
							&& (a[i - 4] == 't' || a[i - 4] == 'T')
							&& (a[i - 3] == 'o' || a[i - 3] == 'O')
							&& (a[i - 2] == 'n' || a[i - 2] == 'N')
							&& (a[i - 1] == 'l' || a[i - 1] == 'L')
							&& (a[i] == 'y' || a[i] == 'Y')) {
						matchlen = 10;
						mask |= ACTION_EXPORT;

					}
					else {
						// parse error
						throw new IllegalArgumentException(
								"invalid permission: " + actions);
					}
				}

			// make sure we didn't just match the tail of a word
			// like "ackbarfimport". Also, skip to the comma.
			seencomma = false;
			while (i >= matchlen && !seencomma) {
				switch (a[i - matchlen]) {
					case ',' :
						seencomma = true;
						/* FALLTHROUGH */
					case ' ' :
					case '\r' :
					case '\n' :
					case '\f' :
					case '\t' :
						break;
					default :
						throw new IllegalArgumentException(
								"invalid permission: " + actions);
				}
				i--;
			}

			// point i at the location of the comma minus one (or -1).
			i -= matchlen;
		}

		if (seencomma) {
			throw new IllegalArgumentException("invalid permission: " + actions);
		}

		return mask;
	}

	/**
	 * Determines if the specified permission is implied by this object.
	 * 
	 * <p>
	 * This method checks that the package name of the target is implied by the
	 * package name of this object. The list of <code>PackagePermission</code>
	 * actions must either match or allow for the list of the target object to
	 * imply the target <code>PackagePermission</code> action.
	 * <p>
	 * The permission to export a package implies the permission to import the
	 * named package.
	 * 
	 * <pre>
	 * x.y.*,&quot;export&quot; -&gt; x.y.z,&quot;export&quot; is true
	 * *,&quot;import&quot; -&gt; x.y, &quot;import&quot;      is true
	 * *,&quot;export&quot; -&gt; x.y, &quot;import&quot;      is true
	 * x.y,&quot;export&quot; -&gt; x.y.z, &quot;export&quot;  is false
	 * </pre>
	 * 
	 * @param p The target permission to interrogate.
	 * @return <code>true</code> if the specified
	 *         <code>PackagePermission</code> action is implied by this
	 *         object; <code>false</code> otherwise.
	 */
	public boolean implies(Permission p) {
		if (p instanceof PackagePermission) {
			PackagePermission requested = (PackagePermission) p;

			int requestedMask = requested.getActionsMask();
			return ((getActionsMask() & requestedMask) == requestedMask)
					&& super.implies(p);
		}

		return false;
	}

	/**
	 * Returns the canonical string representation of the
	 * <code>PackagePermission</code> actions.
	 * 
	 * <p>
	 * Always returns present <code>PackagePermission</code> actions in the
	 * following order: <code>EXPORTONLY</code>,<code>IMPORT</code>.
	 * 
	 * @return Canonical string representation of the
	 *         <code>PackagePermission</code> actions.
	 */
	public String getActions() {
		String result = actions;
		if (result == null) {
			StringBuffer sb = new StringBuffer();
			boolean comma = false;

			int mask = getActionsMask();
			if ((mask & ACTION_EXPORT) == ACTION_EXPORT) {
				sb.append(EXPORTONLY);
				comma = true;
			}

			if ((mask & ACTION_IMPORT) == ACTION_IMPORT) {
				if (comma)
					sb.append(',');
				sb.append(IMPORT);
			}

			actions = result = sb.toString();
		}
		return result;
	}

	/**
	 * Returns a new <code>PermissionCollection</code> object suitable for
	 * storing <code>PackagePermission</code> objects.
	 * 
	 * @return A new <code>PermissionCollection</code> object.
	 */
	public PermissionCollection newPermissionCollection() {
		return new PackagePermissionCollection();
	}

	/**
	 * Determines the equality of two <code>PackagePermission</code> objects.
	 * 
	 * This method checks that specified package has the same package name and
	 * <code>PackagePermission</code> actions as this
	 * <code>PackagePermission</code> object.
	 * 
	 * @param obj The object to test for equality with this
	 *        <code>PackagePermission</code> object.
	 * @return <code>true</code> if <code>obj</code> is a
	 *         <code>PackagePermission</code>, and has the same package name
	 *         and actions as this <code>PackagePermission</code> object;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof PackagePermission)) {
			return false;
		}

		PackagePermission pp = (PackagePermission) obj;

		return (getActionsMask() == pp.getActionsMask())
				&& getName().equals(pp.getName());
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return A hash code value for this object.
	 */

	public int hashCode() {
		int h = 31 * 17 + getName().hashCode();
		h = 31 * h + getActions().hashCode();
		return h;
	}

	/**
	 * WriteObject is called to save the state of this permission object to a
	 * stream. The actions are serialized, and the superclass takes care of the
	 * name.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws IOException {
		// Write out the actions. The superclass takes care of the name
		// call getActions to make sure actions field is initialized
		if (actions == null)
			getActions();
		s.defaultWriteObject();
	}

	/**
	 * readObject is called to restore the state of this permission from a
	 * stream.
	 */
	private synchronized void readObject(java.io.ObjectInputStream s)
			throws IOException, ClassNotFoundException {
		// Read in the action, then initialize the rest
		s.defaultReadObject();
		setTransients(parseActions(actions));
	}
}

/**
 * Stores a set of <code>PackagePermission</code> permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */

final class PackagePermissionCollection extends PermissionCollection {
	static final long		serialVersionUID	= -3350758995234427603L;
	/**
	 * Table of permissions.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private final Hashtable	permissions;

	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private boolean			all_allowed;

	/**
	 * Create an empty PackagePermissions object.
	 */

	public PackagePermissionCollection() {
		permissions = new Hashtable();
		all_allowed = false;
	}

	/**
	 * Adds a permission to the <code>PackagePermission</code> objects. The
	 * key for the hash is the name.
	 * 
	 * @param permission The <code>PackagePermission</code> object to add.
	 * 
	 * @throws IllegalArgumentException If the permission is not a
	 *         <code>PackagePermission</code> instance.
	 * 
	 * @throws SecurityException If this
	 *         <code>PackagePermissionCollection</code> object has been marked
	 *         read-only.
	 */

	public void add(final Permission permission) {
		if (!(permission instanceof PackagePermission)) {
			throw new IllegalArgumentException("invalid permission: "
					+ permission);
		}
		if (isReadOnly()) {
			throw new SecurityException("attempt to add a Permission to a "
					+ "readonly PermissionCollection");
		}

		final PackagePermission pp = (PackagePermission) permission;
		final String name = pp.getName();

		synchronized (this) {
			final PackagePermission existing = (PackagePermission) permissions
					.get(name);

			if (existing != null) {
				final int oldMask = existing.getActionsMask();
				final int newMask = pp.getActionsMask();
				if (oldMask != newMask) {
					permissions.put(name, new PackagePermission(name, oldMask
							| newMask));

				}
			}
			else {
				permissions.put(name, pp);
			}

			if (!all_allowed) {
				if (name.equals("*")) {
					all_allowed = true;
				}
			}
		}
	}

	/**
	 * Determines if the specified permissions implies the permissions expressed
	 * in <code>permission</code>.
	 * 
	 * @param permission The Permission object to compare with this
	 *        <code>PackagePermission</code> object.
	 * 
	 * @return <code>true</code> if <code>permission</code> is a proper
	 *         subset of a permission in the set; <code>false</code>
	 *         otherwise.
	 */

	public boolean implies(final Permission permission) {
		if (!(permission instanceof PackagePermission)) {
			return false;
		}
		final PackagePermission requested = (PackagePermission) permission;
		String name = requested.getName();
		final int desired = requested.getActionsMask();
		PackagePermission x;
		int effective = 0;

		synchronized (this) {
			// short circuit if the "*" Permission was added
			if (all_allowed) {
				x = (PackagePermission) permissions.get("*");
				if (x != null) {
					effective |= x.getActionsMask();
					if ((effective & desired) == desired) {
						return true;
					}
				}
			}
			x = (PackagePermission) permissions.get(name);
		}
		// strategy:
		// Check for full match first. Then work our way up the
		// name looking for matches on a.b.*
		if (x != null) {
			// we have a direct hit!
			effective |= x.getActionsMask();
			if ((effective & desired) == desired) {
				return true;
			}
		}
		// work our way up the tree...
		int last;
		int offset = name.length() - 1;
		while ((last = name.lastIndexOf(".", offset)) != -1) {
			name = name.substring(0, last + 1) + "*";
			synchronized (this) {
				x = (PackagePermission) permissions.get(name);
			}
			if (x != null) {
				effective |= x.getActionsMask();
				if ((effective & desired) == desired) {
					return true;
				}
			}
			offset = last - 1;
		}
		// we don't have to check for "*" as it was already checked
		// at the top (all_allowed), so we just return false
		return false;
	}

	/**
	 * Returns an enumeration of all <code>PackagePermission</code> objects in
	 * the container.
	 * 
	 * @return Enumeration of all <code>PackagePermission</code> objects.
	 */

	public Enumeration elements() {
		return permissions.elements();
	}
}
