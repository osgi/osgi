/*
 * Copyright (c) OSGi Alliance (2000, 2016). All Rights Reserved.
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

package org.osgi.service.subsystem;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * A bundle's authority to perform specific privileged administrative operations
 * on or to get sensitive information about a subsystem. The actions for this
 * permission are:
 * 
 * <pre>
 * Action    Methods
 * context   Subsystem.getBundleContext
 * execute   Subsystem.start
 *           Subsystem.stop
 * lifecycle Subsystem.install
 *           Subsystem.uninstall
 * metadata  Subsystem.getSubsystemHeaders
 *           Subsystem.getLocation
 * </pre>
 * 
 * <p>
 * The name of this permission is a filter expression. The filter gives access
 * to the following attributes:
 * <ul>
 * <li>location - The location of a subsystem.</li>
 * <li>id - The subsystem ID of the designated subsystem.</li>
 * <li>name - The symbolic name of a subsystem.</li>
 * </ul>
 * Filter attribute names are processed in a case sensitive manner.
 * 
 * @ThreadSafe
 * @author $Id$
 */

public final class SubsystemPermission extends BasicPermission {
	static final long								serialVersionUID	= 307051004521261705L;

	/**
	 * The action string {@code execute}.
	 */
	public final static String						EXECUTE				= "execute";
	/**
	 * The action string {@code lifecycle}.
	 */
	public final static String						LIFECYCLE			= "lifecycle";
	/**
	 * The action string {@code metadata}.
	 */
	public final static String						METADATA			= "metadata";
	/**
	 * The action string {@code context}.
	 */
	public final static String						CONTEXT				= "context";

	private final static int						ACTION_EXECUTE		= 0x00000001;
	private final static int						ACTION_LIFECYCLE	= 0x00000002;
	private final static int						ACTION_METADATA		= 0x00000004;
	private final static int						ACTION_CONTEXT		= 0x00000008;
	private final static int						ACTION_ALL			= ACTION_EXECUTE | ACTION_LIFECYCLE | ACTION_METADATA | ACTION_CONTEXT;
	final static int								ACTION_NONE			= 0;

	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private volatile String							actions				= null;

	/**
	 * The actions mask.
	 */
	transient int									action_mask;

	/**
	 * If this SubsystemPermission was constructed with a filter, this holds a
	 * Filter matching object used to evaluate the filter in implies.
	 */
	transient Filter								filter;

	/**
	 * The subsystem governed by this SubsystemPermission - only used if filter
	 * == null
	 */
	transient final Subsystem						subsystem;

	/**
	 * This map holds the properties of the permission, used to match a filter
	 * in implies. This is not initialized until necessary, and then cached in
	 * this object.
	 */
	private transient volatile Map<String, Object>	properties;

	/**
	 * ThreadLocal used to determine if we have recursively called
	 * getProperties.
	 */
	private static final ThreadLocal<Subsystem>		recurse				= new ThreadLocal<Subsystem>();

	/**
	 * Create a new SubsystemPermission.
	 * 
	 * This constructor must only be used to create a permission that is going
	 * to be checked.
	 * <p>
	 * Examples:
	 * 
	 * <pre>
	 * (name=com.acme.*)(location=http://www.acme.com/subsystems/*))
	 * (id&gt;=1)
	 * </pre>
	 * 
	 * @param filter A filter expression that can use, location, id, and name
	 *        keys. Filter attribute names are processed in a case sensitive
	 *        manner. A special value of {@code "*"} can be used to match all
	 *        subsystems.
	 * @param actions {@code execute}, {@code lifecycle}, {@code metadata}, or
	 *        {@code context}.
	 * @throws IllegalArgumentException If the filter has an invalid syntax.
	 */
	public SubsystemPermission(String filter, String actions) {
		this(parseFilter(filter), parseActions(actions));
	}

	/**
	 * Creates a new requested {@code SubsystemPermission} object to be used by
	 * the code that must perform {@code checkPermission}.
	 * {@code SubsystemPermission} objects created with this constructor cannot
	 * be added to an {@code SubsystemPermission} permission collection.
	 * 
	 * @param subsystem A subsystem.
	 * @param actions {@code execute}, {@code lifecycle}, {@code metadata}, or
	 *        {@code context}.
	 */
	public SubsystemPermission(Subsystem subsystem, String actions) {
		super(createName(subsystem));
		setTransients(null, parseActions(actions));
		this.subsystem = subsystem;
	}

	/**
	 * Create a permission name from a Subsystem
	 * 
	 * @param subsystem Subsystem to use to create permission name.
	 * @return permission name.
	 */
	private static String createName(Subsystem subsystem) {
		if (subsystem == null) {
			throw new IllegalArgumentException("subsystem must not be null");
		}
		StringBuilder sb = new StringBuilder("(id=");
		sb.append(subsystem.getSubsystemId());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Package private constructor used by SubsystemPermissionCollection.
	 * 
	 * @param filter name filter or {@code null} for wildcard.
	 * @param mask action mask
	 */
	SubsystemPermission(Filter filter, int mask) {
		super((filter == null) ? "*" : filter.toString());
		setTransients(filter, mask);
		this.subsystem = null;
	}

	/**
	 * Called by constructors and when deserialized.
	 * 
	 * @param filter Permission's filter or {@code null} for wildcard.
	 * @param mask action mask
	 */
	private void setTransients(Filter filter, int mask) {
		this.filter = filter;
		if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask)) {
			throw new IllegalArgumentException("invalid action string");
		}
		this.action_mask = mask;
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
			while ((i != -1) && ((c = a[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))
				i--;

			// check for the known strings
			int matchlen;

			if (i >= 6 && (a[i - 6] == 'e' || a[i - 6] == 'E')
					&& (a[i - 5] == 'x' || a[i - 5] == 'X')
					&& (a[i - 4] == 'e' || a[i - 4] == 'E')
					&& (a[i - 3] == 'c' || a[i - 3] == 'C')
					&& (a[i - 2] == 'u' || a[i - 2] == 'U')
					&& (a[i - 1] == 't' || a[i - 1] == 'T')
					&& (a[i] == 'e' || a[i] == 'E')) {
				matchlen = 7;
				mask |= ACTION_EXECUTE;
			} else
				if (i >= 8 && (a[i - 8] == 'l' || a[i - 8] == 'L')
						&& (a[i - 7] == 'i' || a[i - 7] == 'I')
						&& (a[i - 6] == 'f' || a[i - 6] == 'F')
						&& (a[i - 5] == 'e' || a[i - 5] == 'E')
						&& (a[i - 4] == 'c' || a[i - 4] == 'C')
						&& (a[i - 3] == 'y' || a[i - 3] == 'Y')
						&& (a[i - 2] == 'c' || a[i - 2] == 'C')
						&& (a[i - 1] == 'l' || a[i - 1] == 'L')
						&& (a[i] == 'e' || a[i] == 'E')) {
					matchlen = 9;
					mask |= ACTION_LIFECYCLE;
				} else
					if (i >= 7
							&& (a[i - 7] == 'm' || a[i - 7] == 'M')
							&& (a[i - 6] == 'e' || a[i - 6] == 'E')
							&& (a[i - 5] == 't' || a[i - 5] == 'T')
							&& (a[i - 4] == 'a' || a[i - 4] == 'A')
							&& (a[i - 3] == 'd' || a[i - 3] == 'D')
							&& (a[i - 2] == 'a' || a[i - 2] == 'A')
							&& (a[i - 1] == 't' || a[i - 1] == 'T')
							&& (a[i] == 'a' || a[i] == 'A')) {
						matchlen = 8;
						mask |= ACTION_METADATA;
					} else
						if (i >= 6
								&& (a[i - 6] == 'c' || a[i - 6] == 'C')
								&& (a[i - 5] == 'o' || a[i - 5] == 'O')
								&& (a[i - 4] == 'n' || a[i - 4] == 'N')
								&& (a[i - 3] == 't' || a[i - 3] == 'T')
								&& (a[i - 2] == 'e' || a[i - 2] == 'E')
								&& (a[i - 1] == 'x' || a[i - 1] == 'X')
								&& (a[i] == 't' || a[i] == 'T')) {
							matchlen = 7;
							mask |= ACTION_CONTEXT;
						} else {
							// parse error
							throw new IllegalArgumentException("invalid permission: " + actions);
						}

			// make sure we didn't just match the tail of a word
			// like "ackbarfexecute". Also, skip to the comma.
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
	 * Parse filter string into a Filter object.
	 * 
	 * @param filterString The filter string to parse.
	 * @return a Filter for this subsystem. If the specified filterString equals
	 *         "*", then {@code null} is returned to indicate a wildcard.
	 * @throws IllegalArgumentException If the filter syntax is invalid.
	 */
	private static Filter parseFilter(String filterString) {
		filterString = filterString.trim();
		if (filterString.equals("*")) {
			return null;
		}

		try {
			return FrameworkUtil.createFilter(filterString);
		} catch (InvalidSyntaxException e) {
			IllegalArgumentException iae = new IllegalArgumentException("invalid filter");
			iae.initCause(e);
			throw iae;
		}
	}

	/**
	 * Determines if the specified permission is implied by this object. This
	 * method throws an exception if the specified permission was not
	 * constructed with a subsystem.
	 * 
	 * <p>
	 * This method returns {@code true} if the specified permission is a
	 * SubsystemPermission AND
	 * <ul>
	 * <li>this object's filter matches the specified permission's subsystem ID,
	 * subsystem symbolic name, and subsystem location OR</li>
	 * <li>this object's filter is "*"</li>
	 * </ul>
	 * AND this object's actions include all of the specified permission's
	 * actions.
	 * <p>
	 * Special case: if the specified permission was constructed with "*"
	 * filter, then this method returns {@code true} if this object's filter is
	 * "*" and this object's actions include all of the specified permission's
	 * actions
	 * 
	 * @param p The requested permission.
	 * @return {@code true} if the specified permission is implied by this
	 *         object; {@code false} otherwise.
	 */
	@Override
	public boolean implies(Permission p) {
		if (!(p instanceof SubsystemPermission)) {
			return false;
		}
		SubsystemPermission requested = (SubsystemPermission) p;
		if (subsystem != null) {
			return false;
		}
		// if requested permission has a filter, then it is an invalid argument
		if (requested.filter != null) {
			return false;
		}
		return implies0(requested, ACTION_NONE);
	}

	/**
	 * Internal implies method. Used by the implies and the permission
	 * collection implies methods.
	 * 
	 * @param requested The requested SubsystemPermision which has already been
	 *        validated as a proper argument. The requested SubsystemPermission
	 *        must not have a filter expression.
	 * @param effective The effective actions with which to start.
	 * @return {@code true} if the specified permission is implied by this
	 *         object; {@code false} otherwise.
	 */
	boolean implies0(SubsystemPermission requested, int effective) {
		/* check actions first - much faster */
		effective |= action_mask;
		final int desired = requested.action_mask;
		if ((effective & desired) != desired) {
			return false;
		}

		/* Get our filter */
		Filter f = filter;
		if (f == null) {
			// it's "*"
			return true;
		}
		/* is requested a wildcard filter? */
		if (requested.subsystem == null) {
			return false;
		}
		Map<String, Object> requestedProperties = requested.getProperties();
		if (requestedProperties == null) {
			/*
			 * If the requested properties are null, then we have detected a
			 * recursion getting the subsystem location. So we return true to
			 * permit the subsystem location request in the SubsystemPermission
			 * check up the stack to succeed.
			 */
			return true;
		}
		return f.matches(requestedProperties);
	}

	/**
	 * Returns the canonical string representation of the
	 * {@code SubsystemPermission} actions.
	 * 
	 * <p>
	 * Always returns present {@code SubsystemPermission} actions in the
	 * following order: {@code execute}, {@code lifecycle}, {@code metadata},
	 * {@code context}.
	 * 
	 * @return Canonical string representation of the
	 *         {@code SubsystemPermission} actions.
	 */
	@Override
	public String getActions() {
		String result = actions;
		if (result == null) {
			StringBuilder sb = new StringBuilder();

			int mask = action_mask;

			if ((mask & ACTION_EXECUTE) == ACTION_EXECUTE) {
				sb.append(EXECUTE);
				sb.append(',');
			}

			if ((mask & ACTION_LIFECYCLE) == ACTION_LIFECYCLE) {
				sb.append(LIFECYCLE);
				sb.append(',');
			}

			if ((mask & ACTION_METADATA) == ACTION_METADATA) {
				sb.append(METADATA);
				sb.append(',');
			}

			if ((mask & ACTION_CONTEXT) == ACTION_CONTEXT) {
				sb.append(CONTEXT);
				sb.append(',');
			}

			// remove trailing comma
			if (sb.length() > 0) {
				sb.setLength(sb.length() - 1);
			}

			actions = result = sb.toString();
		}
		return result;
	}

	/**
	 * Returns a new {@code PermissionCollection} object suitable for storing
	 * {@code SubsystemPermission}s.
	 * 
	 * @return A new {@code PermissionCollection} object.
	 */
	@Override
	public PermissionCollection newPermissionCollection() {
		return new SubsystemPermissionCollection();
	}

	/**
	 * Determines the equality of two {@code SubsystemPermission} objects.
	 * 
	 * @param obj The object being compared for equality with this object.
	 * @return {@code true} if {@code obj} is equivalent to this
	 *         {@code SubsystemPermission}; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof SubsystemPermission)) {
			return false;
		}

		SubsystemPermission sp = (SubsystemPermission) obj;

		return (action_mask == sp.action_mask) && ((subsystem == sp.subsystem) || ((subsystem != null) && subsystem.equals(sp.subsystem)))
				&& (filter == null ? sp.filter == null : filter.equals(sp.filter));
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return Hash code value for this object.
	 */
	@Override
	public int hashCode() {
		int h = 31 * 17 + getName().hashCode();
		h = 31 * h + getActions().hashCode();
		if (subsystem != null) {
			h = 31 * h + subsystem.hashCode();
		}
		return h;
	}

	/**
	 * WriteObject is called to save the state of this permission object to a
	 * stream. The actions are serialized, and the superclass takes care of the
	 * name.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
		if (subsystem != null) {
			throw new NotSerializableException("cannot serialize");
		}
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
		// Read in the data, then initialize the transients
		s.defaultReadObject();
		setTransients(parseFilter(getName()), parseActions(actions));
	}

	/**
	 * Called by {@code implies0} on an SubsystemPermission which was
	 * constructed with a Subsystem. This method loads a map with the
	 * filter-matchable properties of this subsystem. The map is cached so this
	 * lookup only happens once.
	 * 
	 * This method should only be called on an SubsystemPermission which was
	 * constructed with a subsystem
	 * 
	 * @return a map of properties for this subsystem
	 */
	private Map<String, Object> getProperties() {
		Map<String, Object> result = properties;
		if (result != null) {
			return result;
		}
		/*
		 * We may have recursed here due to the Subsystem.getLocation call in
		 * the doPrivileged below. If this is the case, return null to allow
		 * implies to return true.
		 */
		final Object mark = recurse.get();
		if (mark == subsystem) {
			return null;
		}
		recurse.set(subsystem);
		try {
			final Map<String, Object> map = new HashMap<String, Object>(4);
			AccessController.doPrivileged(new PrivilegedAction<Void>() {
				@Override
				public Void run() {
					map.put("id", Long.valueOf(subsystem.getSubsystemId()));
					map.put("location", subsystem.getLocation());
					map.put("name", subsystem.getSymbolicName());
					return null;
				}
			});
			return properties = map;
		} finally {
			recurse.set(null);
		}
	}
}

/**
 * Stores a collection of {@code SubsystemPermission}s.
 */
final class SubsystemPermissionCollection extends PermissionCollection {
	private static final long							serialVersionUID	= 3906372644575328048L;
	/**
	 * Collection of permissions.
	 * 
	 * @GuardedBy this
	 */
	private transient Map<String, SubsystemPermission>	permissions;

	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private boolean										all_allowed;

	/**
	 * Create an empty SubsystemPermissionCollection object.
	 * 
	 */
	public SubsystemPermissionCollection() {
		permissions = new HashMap<String, SubsystemPermission>();
	}

	/**
	 * Adds a permission to this permission collection.
	 * 
	 * @param permission The {@code SubsystemPermission} object to add.
	 * @throws IllegalArgumentException If the specified permission is not an
	 *         {@code SubsystemPermission} instance or was constructed with a
	 *         Subsystem object.
	 * @throws SecurityException If this {@code SubsystemPermissionCollection}
	 *         object has been marked read-only.
	 */
	@Override
	public void add(Permission permission) {
		if (!(permission instanceof SubsystemPermission)) {
			throw new IllegalArgumentException("invalid permission: " + permission);
		}
		if (isReadOnly()) {
			throw new SecurityException("attempt to add a Permission to a " + "readonly PermissionCollection");
		}
		final SubsystemPermission sp = (SubsystemPermission) permission;
		if (sp.subsystem != null) {
			throw new IllegalArgumentException("cannot add to collection: " + sp);
		}
		final String name = sp.getName();
		synchronized (this) {
			Map<String, SubsystemPermission> pc = permissions;
			SubsystemPermission existing = pc.get(name);
			if (existing != null) {
				int oldMask = existing.action_mask;
				int newMask = sp.action_mask;

				if (oldMask != newMask) {
					pc.put(name, new SubsystemPermission(existing.filter, oldMask | newMask));
				}
			} else {
				pc.put(name, sp);
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
	 * @param permission The Permission object to compare with the
	 *        {@code SubsystemPermission} objects in this collection.
	 * @return {@code true} if {@code permission} is implied by an
	 *         {@code SubsystemPermission} in this collection, {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean implies(Permission permission) {
		if (!(permission instanceof SubsystemPermission)) {
			return false;
		}

		SubsystemPermission requested = (SubsystemPermission) permission;
		// if requested permission has a filter, then it is an invalid argument
		if (requested.filter != null) {
			return false;
		}
		int effective = SubsystemPermission.ACTION_NONE;
		Collection<SubsystemPermission> perms;
		synchronized (this) {
			Map<String, SubsystemPermission> pc = permissions;
			// short circuit if the "*" Permission was added
			if (all_allowed) {
				SubsystemPermission sp = pc.get("*");
				if (sp != null) {
					effective |= sp.action_mask;
					final int desired = requested.action_mask;
					if ((effective & desired) == desired) {
						return true;
					}
				}
			}
			perms = pc.values();
		}

		// just iterate one by one
		for (SubsystemPermission perm : perms) {
			if (perm.implies0(requested, effective)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an enumeration of all {@code SubsystemPermission} objects in the
	 * container.
	 * 
	 * @return Enumeration of all {@code SubsystemPermission} objects.
	 */
	@Override
	public synchronized Enumeration<Permission> elements() {
		List<Permission> all = new ArrayList<Permission>(permissions.values());
		return Collections.enumeration(all);
	}

	/* serialization logic */
	private static final ObjectStreamField[]	serialPersistentFields	= {new ObjectStreamField("permissions", HashMap.class), new ObjectStreamField("all_allowed", Boolean.TYPE)};

	private synchronized void writeObject(ObjectOutputStream out) throws IOException {
		ObjectOutputStream.PutField pfields = out.putFields();
		pfields.put("permissions", permissions);
		pfields.put("all_allowed", all_allowed);
		out.writeFields();
	}

	private synchronized void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField gfields = in.readFields();
		@SuppressWarnings("unchecked")
		HashMap<String, SubsystemPermission> p = (HashMap<String, SubsystemPermission>) gfields.get("permissions", null);
		permissions = p;
		all_allowed = gfields.get("all_allowed", false);
	}
}
