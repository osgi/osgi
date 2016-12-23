/*
 * Copyright (c) OSGi Alliance (2005, 2016). All Rights Reserved.
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

package org.osgi.service.event;

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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * A bundle's authority to publish or subscribe to event on a topic.
 * 
 * <p>
 * A topic is a slash-separated string that defines a topic.
 * <p>
 * For example:
 * 
 * <pre>
 * org/osgi/service/foo/FooEvent/ACTION
 * </pre>
 * 
 * <p>
 * {@code TopicPermission} has two actions: {@code publish} and
 * {@code subscribe}.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public final class TopicPermission extends Permission {
	static final long					serialVersionUID	= -5855563886961618300L;
	/**
	 * The action string {@code publish}.
	 */
	public final static String			PUBLISH				= "publish";
	/**
	 * The action string {@code subscribe}.
	 */
	public final static String			SUBSCRIBE			= "subscribe";
	private final static int			ACTION_PUBLISH		= 0x00000001;
	private final static int			ACTION_SUBSCRIBE	= 0x00000002;
	private final static int			ACTION_ALL			= ACTION_PUBLISH | ACTION_SUBSCRIBE;
	private final static int			ACTION_NONE			= 0;
	/**
	 * The actions mask.
	 */
	private transient int				action_mask;

	/**
	 * prefix if the name is wildcarded.
	 */
	private transient volatile String	prefix;

	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private volatile String				actions				= null;

	/**
	 * Defines the authority to publish and/or subscribe to a topic within the
	 * EventAdmin service.
	 * <p>
	 * The name is specified as a slash-separated string. Wildcards may be used.
	 * For example:
	 * 
	 * <pre>
	 *    org/osgi/service/fooFooEvent/ACTION
	 *    com/isv/*
	 *    *
	 * </pre>
	 * 
	 * <p>
	 * A bundle that needs to publish events on a topic must have the
	 * appropriate {@code TopicPermission} for that topic; similarly, a bundle
	 * that needs to subscribe to events on a topic must have the appropriate
	 * {@code TopicPermssion} for that topic.
	 * <p>
	 * 
	 * @param name Topic name.
	 * @param actions {@code publish},{@code subscribe} (canonical order).
	 */
	public TopicPermission(String name, String actions) {
		this(name, parseActions(actions));
	}

	/**
	 * Package private constructor used by TopicPermissionCollection.
	 * 
	 * @param name class name
	 * @param mask action mask
	 */
	TopicPermission(String name, int mask) {
		super(name);
		setTransients(mask);
	}

	/**
	 * Called by constructors and when deserialized.
	 * 
	 * @param name topic name
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

		if (name.equals("*")) {
			prefix = "";
		} else {
			if (name.endsWith("/*")) {
				prefix = name.substring(0, name.length() - 1);
			} else {
				prefix = null;
			}
		}
	}

	/**
	 * Returns the current action mask.
	 * <p>
	 * Used by the TopicPermissionCollection class.
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
			while ((i != -1) && ((c = a[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))
				i--;
			// check for the known strings
			int matchlen;
			if (i >= 8 && (a[i - 8] == 's' || a[i - 8] == 'S')
					&& (a[i - 7] == 'u' || a[i - 7] == 'U')
					&& (a[i - 6] == 'b' || a[i - 6] == 'B')
					&& (a[i - 5] == 's' || a[i - 5] == 'S')
					&& (a[i - 4] == 'c' || a[i - 4] == 'C')
					&& (a[i - 3] == 'r' || a[i - 3] == 'R')
					&& (a[i - 2] == 'i' || a[i - 2] == 'I')
					&& (a[i - 1] == 'b' || a[i - 1] == 'B')
					&& (a[i] == 'e' || a[i] == 'E')) {
				matchlen = 9;
				mask |= ACTION_SUBSCRIBE;
			}
			else
				if (i >= 6 && (a[i - 6] == 'p' || a[i - 6] == 'P')
						&& (a[i - 5] == 'u' || a[i - 5] == 'U')
						&& (a[i - 4] == 'b' || a[i - 4] == 'B')
						&& (a[i - 3] == 'l' || a[i - 3] == 'L')
						&& (a[i - 2] == 'i' || a[i - 2] == 'I')
						&& (a[i - 1] == 's' || a[i - 1] == 'S')
						&& (a[i] == 'h' || a[i] == 'H')) {
					matchlen = 7;
					mask |= ACTION_PUBLISH;
				}
				else {
					// parse error
					throw new IllegalArgumentException("invalid permission: "
							+ actions);
				}
			// make sure we didn't just match the tail of a word
			// like "ackbarfpublish". Also, skip to the comma.
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
						throw new IllegalArgumentException("invalid permission: " + actions);
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
	 * This method checks that the topic name of the target is implied by the
	 * topic name of this object. The list of {@code TopicPermission} actions
	 * must either match or allow for the list of the target object to imply the
	 * target {@code TopicPermission} action.
	 * 
	 * <pre>
	 *    x/y/*,&quot;publish&quot; -&gt; x/y/z,&quot;publish&quot; is true
	 *    *,&quot;subscribe&quot; -&gt; x/y,&quot;subscribe&quot;   is true
	 *    *,&quot;publish&quot; -&gt; x/y,&quot;subscribe&quot;     is false
	 *    x/y,&quot;publish&quot; -&gt; x/y/z,&quot;publish&quot;   is false
	 * </pre>
	 * 
	 * @param p The target permission to interrogate.
	 * @return {@code true} if the specified {@code TopicPermission} action is
	 *         implied by this object; {@code false} otherwise.
	 */
	@Override
	public boolean implies(Permission p) {
		if (p instanceof TopicPermission) {
			TopicPermission requested = (TopicPermission) p;
			int requestedMask = requested.getActionsMask();
			if ((getActionsMask() & requestedMask) == requestedMask) {
				String requestedName = requested.getName();
				String pre = prefix;
				if (pre != null) {
					return requestedName.startsWith(pre);
				}

				return requestedName.equals(getName());
			}
		}
		return false;
	}

	/**
	 * Returns the canonical string representation of the
	 * {@code TopicPermission} actions.
	 * 
	 * <p>
	 * Always returns present {@code TopicPermission} actions in the following
	 * order: {@code publish},{@code subscribe}.
	 * 
	 * @return Canonical string representation of the {@code TopicPermission}
	 *         actions.
	 */
	@Override
	public String getActions() {
		String result = actions;
		if (result == null) {
			StringBuilder sb = new StringBuilder();
			boolean comma = false;
			int mask = getActionsMask();
			if ((mask & ACTION_PUBLISH) == ACTION_PUBLISH) {
				sb.append(PUBLISH);
				comma = true;
			}
			if ((mask & ACTION_SUBSCRIBE) == ACTION_SUBSCRIBE) {
				if (comma)
					sb.append(',');
				sb.append(SUBSCRIBE);
			}
			actions = result = sb.toString();
		}
		return result;
	}

	/**
	 * Returns a new {@code PermissionCollection} object suitable for storing
	 * {@code TopicPermission} objects.
	 * 
	 * @return A new {@code PermissionCollection} object.
	 */
	@Override
	public PermissionCollection newPermissionCollection() {
		return new TopicPermissionCollection();
	}

	/**
	 * Determines the equality of two {@code TopicPermission} objects.
	 * 
	 * This method checks that specified {@code TopicPermission} has the same
	 * topic name and actions as this {@code TopicPermission} object.
	 * 
	 * @param obj The object to test for equality with this
	 *        {@code TopicPermission} object.
	 * @return {@code true} if {@code obj} is a {@code TopicPermission}, and has
	 *         the same topic name and actions as this {@code TopicPermission}
	 *         object; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TopicPermission)) {
			return false;
		}
		TopicPermission tp = (TopicPermission) obj;
		return (getActionsMask() == tp.getActionsMask()) && getName().equals(tp.getName());
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
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
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
	private synchronized void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		// Read in the action, then initialize the rest
		s.defaultReadObject();
		setTransients(parseActions(actions));
	}
}

/**
 * Stores a set of {@code TopicPermission} permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */
final class TopicPermissionCollection extends PermissionCollection {
	static final long								serialVersionUID	= -614647783533924048L;
	/**
	 * Table of permissions.
	 * 
	 * @GuardedBy this
	 */
	private transient Map<String, TopicPermission>	permissions;
	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private boolean									all_allowed;

	/**
	 * Create an empty TopicPermissions object.
	 * 
	 */
	public TopicPermissionCollection() {
		permissions = new HashMap<String, TopicPermission>();
		all_allowed = false;
	}

	/**
	 * Adds a permission to the {@code TopicPermission} objects. The key for the
	 * hash is the name.
	 * 
	 * @param permission The {@code TopicPermission} object to add.
	 * 
	 * @throws IllegalArgumentException If the permission is not a
	 *         {@code TopicPermission} instance.
	 * 
	 * @throws SecurityException If this {@code TopicPermissionCollection}
	 *         object has been marked read-only.
	 */
	@Override
	public void add(final Permission permission) {
		if (!(permission instanceof TopicPermission)) {
			throw new IllegalArgumentException("invalid permission: " + permission);
		}
		if (isReadOnly()) {
			throw new SecurityException("attempt to add a Permission to a " + "readonly PermissionCollection");
		}
		final TopicPermission tp = (TopicPermission) permission;
		final String name = tp.getName();
		final int newMask = tp.getActionsMask();

		synchronized (this) {
			final TopicPermission existing = permissions.get(name);
			if (existing != null) {
				final int oldMask = existing.getActionsMask();
				if (oldMask != newMask) {
					permissions.put(name, new TopicPermission(name, oldMask | newMask));
				}
			} else {
				permissions.put(name, tp);
			}
			if (!all_allowed) {
				if (name.equals("*"))
					all_allowed = true;
			}
		}
	}

	/**
	 * Determines if the specified permissions implies the permissions expressed
	 * in {@code permission}.
	 * 
	 * @param permission The Permission object to compare with this
	 *        {@code TopicPermission} object.
	 * 
	 * @return {@code true} if {@code permission} is a proper subset of a
	 *         permission in the set; {@code false} otherwise.
	 */
	@Override
	public boolean implies(final Permission permission) {
		if (!(permission instanceof TopicPermission)) {
			return false;
		}
		final TopicPermission requested = (TopicPermission) permission;
		String name = requested.getName();
		final int desired = requested.getActionsMask();
		int effective = 0;

		TopicPermission x;
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
		// strategy:
		// Check for full match first. Then work our way up the
		// name looking for matches on a/b/*
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
		while ((last = name.lastIndexOf("/", offset)) != -1) {
			name = name.substring(0, last + 1) + "*";
			synchronized (this) {
				x = permissions.get(name);
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
	 * Returns an enumeration of all {@code TopicPermission} objects in the
	 * container.
	 * 
	 * @return Enumeration of all {@code TopicPermission} objects.
	 */
	@Override
	public synchronized Enumeration<Permission> elements() {
		List<Permission> all = new ArrayList<Permission>(permissions.values());
		return Collections.enumeration(all);
	}

	/* serialization logic */
	private static final ObjectStreamField[]	serialPersistentFields	= {new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE)};

	private synchronized void writeObject(ObjectOutputStream out) throws IOException {
		Hashtable<String, TopicPermission> hashtable = new Hashtable<String, TopicPermission>(permissions);
		ObjectOutputStream.PutField pfields = out.putFields();
		pfields.put("permissions", hashtable);
		pfields.put("all_allowed", all_allowed);
		out.writeFields();
	}

	private synchronized void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField gfields = in.readFields();
		@SuppressWarnings("unchecked")
		Hashtable<String, TopicPermission> hashtable = (Hashtable<String, TopicPermission>) gfields.get("permissions", null);
		permissions = new HashMap<String, TopicPermission>(hashtable);
		all_allowed = gfields.get("all_allowed", false);
	}
}
