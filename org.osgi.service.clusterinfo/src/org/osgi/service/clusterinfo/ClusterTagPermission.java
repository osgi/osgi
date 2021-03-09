/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.clusterinfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A bundle's authority to add a tag to a NodeStatus service.
 */
public final class ClusterTagPermission extends Permission {
	private static final long			serialVersionUID	= 1L;
	/**
	 * The action string {@code add}.
	 */
	public final static String			ADD					= "add";

	/**
	 * The add action mask
	 */
	private final static int			ACTION_ADD			= 0x00000001;
	private final static int			ACTION_ALL			= ACTION_ADD;
	final static int					ACTION_NONE			= 0;

	/**
	 * The actions mask.
	 */
	private transient int				action_mask;

	/**
	 * If the name is "*".
	 */
	private transient volatile boolean	all;

	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private volatile String				actions				= null;

	/**
	 * Defines the authority to add a tag to the NodeStatus service.
	 *
	 * @param tag Give permission to add this tag, use * wildcard to give
	 *            permission to add any tag.
	 * @param actions {@code add}.
	 */
	public ClusterTagPermission(String tag, String actions) {
		this(tag, parseActions(actions));
	}

	/**
	 * Package private constructor used by ClusterTagPermissionCollection.
	 * 
	 * @param name Tag name
	 * @param mask action mask
	 */
	ClusterTagPermission(String name, int mask) {
		super(name);
		setTransients(mask);
	}

	/**
	 * Called by constructors and when deserialized.
	 * 
	 * @param name Tag name
	 * @param mask action mask
	 */
	private synchronized void setTransients(final int mask) {
		final String name = getName();
		if ((name == null) || name.length() == 0) {
			throw new IllegalArgumentException("invalid name");
		}

		if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask)) {
			throw new IllegalArgumentException("invalid action string");
		}
		action_mask = mask;

		all = name.equals("*");
	}

	/**
	 * Returns the current action mask.
	 * <p>
	 * Used by the ClusterTagPermissionCollection class.
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
	private static int parseActions(final String actions) {
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
			while ((i != -1) && ((c = a[i]) == ' ' || c == '\r' || c == '\n'
					|| c == '\f' || c == '\t'))
				i--;
			// check for the known strings
			int matchlen;

			if (i >= 2 && (a[i - 2] == 'a' || a[i - 2] == 'A')
					&& (a[i - 1] == 'd' || a[i - 1] == 'D')
					&& (a[i] == 'd' || a[i] == 'D')) {
				matchlen = 3;
				mask = ACTION_ADD;

			} else {
				// parse error
				throw new IllegalArgumentException(
						"invalid actions: " + actions);
			}

			// make sure we didn't just match the tail of a word
			// like "ackbarfadd". Also, skip to the comma.
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
								"invalid actions: " + actions);
				}
				i--;
			}
			// point i at the location of the comma minus one (or -1).
			i -= matchlen;
		}
		if (seencomma) {
			throw new IllegalArgumentException("invalid actions: " + actions);
		}
		return mask;
	}

	/**
	 * Determines if the specified permission is implied by this object.
	 * <p>
	 * This method checks that the tag of the target is implied by the tag name
	 * of this object.
	 * 
	 * @param p The target permission to interrogate.
	 * @return {@code true} if the specified {@code ClusterTagPermission} action
	 *         is implied by this object; {@code false} otherwise.
	 */
	@Override
	public boolean implies(Permission p) {
		if (p instanceof ClusterTagPermission) {
			ClusterTagPermission requested = (ClusterTagPermission) p;
			int requestedMask = requested.getActionsMask();
			if ((getActionsMask() & requestedMask) == requestedMask) {
				if (all) {
					return true;
				}
				String requestedName = requested.getName();
				return requestedName.equals(getName());
			}
		}
		return false;
	}

	/**
	 * Returns the canonical string representation of the
	 * {@code ClusterTagPermission} action.
	 * <p>
	 * Always returns the ADD action.
	 * 
	 * @return Canonical string representation of the
	 *         {@code ClusterTagPermission} actions.
	 */
	@Override
	public String getActions() {
		String result = actions;
		if (result == null) {
			actions = result = ADD;
		}
		return result;
	}

	/**
	 * Returns a new {@code PermissionCollection} object suitable for storing
	 * {@code ClusterTagPermission} objects.
	 * 
	 * @return A new {@code PermissionCollection} object.
	 */
	@Override
	public PermissionCollection newPermissionCollection() {
		return new ClusterTagPermissionCollection();
	}

	/**
	 * Determines the equality of two {@code ClusterTagPermission} objects. This
	 * method checks that specified {@code ClusterTagPermission} has the same
	 * tag as this {@code ClusterTagPermission} object.
	 * 
	 * @param obj The object to test for equality with this
	 *            {@code ClusterTagPermission} object.
	 * @return {@code true} if {@code obj} is a {@code ClusterTagPermission},
	 *         and has the same tag as this {@code ClusterTagPermission} object;
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ClusterTagPermission)) {
			return false;
		}
		ClusterTagPermission p = (ClusterTagPermission) obj;
		return (getActionsMask() == p.getActionsMask())
				&& getName().equals(p.getName());
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return A hash code value for this object.
	 */
	@Override
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
 * Stores a set of {@code ClusterTagPermission} permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */
final class ClusterTagPermissionCollection extends PermissionCollection {
	private static final long							serialVersionUID	= 1L;
	/**
	 * Table of permissions.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private Map<String,ClusterTagPermission>	permissions;
	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private boolean										all_allowed;

	/**
	 * Create an empty ClusterTagPermission object.
	 */
	public ClusterTagPermissionCollection() {
		permissions = new HashMap<String,ClusterTagPermission>();
		all_allowed = false;
	}

	/**
	 * Adds a permission to the {@code ClusterTagPermission} objects. The key
	 * for the hash is the name.
	 * 
	 * @param permission The {@code ClusterTagPermission} object to add.
	 * @throws IllegalArgumentException If the permission is not a
	 *             {@code ClusterTagPermission} instance.
	 * @throws SecurityException If this {@code ClusterTagPermissionCollection}
	 *             object has been marked read-only.
	 */
	@Override
	public void add(final Permission permission) {
		if (!(permission instanceof ClusterTagPermission)) {
			throw new IllegalArgumentException(
					"invalid permission: " + permission);
		}
		if (isReadOnly()) {
			throw new SecurityException("attempt to add a Permission to a "
					+ "readonly PermissionCollection");
		}
		final ClusterTagPermission p = (ClusterTagPermission) permission;
		final String name = p.getName();

		synchronized (this) {
			final ClusterTagPermission existing = permissions.get(name);
			if (existing != null) {
				final int newMask = p.getActionsMask();
				final int oldMask = existing.getActionsMask();
				if (oldMask != newMask) {
					permissions.put(name,
							new ClusterTagPermission(name, oldMask | newMask));
				}
			} else {
				permissions.put(name, p);
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
	 * in {@code permission}.
	 * 
	 * @param permission The Permission object to compare with this
	 *            {@code ClusterTagPermission} object.
	 * @return {@code true} if {@code permission} is a proper subset of a
	 *         permission in the set; {@code false} otherwise.
	 */
	@Override
	public boolean implies(final Permission permission) {
		if (!(permission instanceof ClusterTagPermission)) {
			return false;
		}
		final ClusterTagPermission requested = (ClusterTagPermission) permission;
		String name = requested.getName();
		final int desired = requested.getActionsMask();
		int effective = ClusterTagPermission.ACTION_NONE;

		ClusterTagPermission x;
		// short circuit if the "*" Permission was added
		synchronized (this) {
			if (all_allowed) {
				x = permissions.get("*");
				if (x != null) {
					effective |= x.getActionsMask();
					if ((effective & desired) == desired) {
						return true;
					}
				}
			}
			x = permissions.get(name);
		}
		if (x != null) {
			// we have a direct hit!
			effective |= x.getActionsMask();
			if ((effective & desired) == desired) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns an enumeration of all {@code ClusterTagPermission} objects in the
	 * container.
	 * 
	 * @return Enumeration of all {@code ClusterTagPermission} objects.
	 */
	@Override
	public synchronized Enumeration<Permission> elements() {
		List<Permission> all = new ArrayList<Permission>(permissions.values());
		return Collections.enumeration(all);
	}

	/* serialization logic */
	private static final ObjectStreamField[] serialPersistentFields = {
			new ObjectStreamField("permissions", HashMap.class),
			new ObjectStreamField("all_allowed", Boolean.TYPE)
	};

	private synchronized void writeObject(ObjectOutputStream out)
			throws IOException {
		ObjectOutputStream.PutField pfields = out.putFields();
		pfields.put("permissions", permissions);
		pfields.put("all_allowed", all_allowed);
		out.writeFields();
	}

	private synchronized void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField gfields = in.readFields();
		@SuppressWarnings("unchecked")
		HashMap<String,ClusterTagPermission> p = (HashMap<String,ClusterTagPermission>) gfields
				.get("permissions", null);
		permissions = p;
		all_allowed = gfields.get("all_allowed", false);
	}
}
