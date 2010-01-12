/*
 * Copyright (c) OSGi Alliance (2000, 2010). All Rights Reserved.
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

package org.osgi.service.remoteserviceadmin;

import static org.osgi.service.remoteserviceadmin.RemoteConstants.*;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * A bundle's authority to export, import or read an Endpoint.
 * <ul>
 * <li>The <code>export</code> action allows a bundle to export a service as an
 * Endpoint.</li>
 * <li>The <code>import</code> action allows a bundle to import a service from
 * an Endpoint.</li>
 * <li>The <code>read</code> action allows a bundle to read references to an
 * Endpoint.</li>
 * </ul>
 * Permission to read an Endpoint is required in order to detect events
 * regarding an Endpoint. Untrusted bundles should not be able to detect the
 * presence of certain Endpoints unless they have the appropriate
 * <code>EndpointPermission</code> to read the specific service.
 * 
 * @ThreadSafe
 * @version $Revision$
 */

public final class EndpointPermission extends Permission {
	static final long					serialVersionUID	= -7662148639076511574L;
	/**
	 * The action string <code>read</code>.
	 */
	public final static String			READ				= "read";
	/**
	 * The action string <code>import</code>. The <code>import</code> action
	 * implies the <code>read</code> action.
	 */
	public final static String			IMPORT				= "import";
	/**
	 * The action string <code>export</code>. The <code>export</code> action
	 * implies the <code>read</code> action.
	 */
	public final static String			EXPORT				= "export";

	private final static int			ACTION_READ			= 0x00000001;
	private final static int			ACTION_IMPORT		= 0x00000002;
	private final static int			ACTION_EXPORT		= 0x00000004;
	private final static int			ACTION_ALL			= ACTION_EXPORT
																	| ACTION_IMPORT
																	| ACTION_READ;
	final static int					ACTION_NONE			= 0;

	/**
	 * The actions mask.
	 */
	transient int						action_mask;

	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private volatile String				actions				= null;

	/**
	 * The endpoint used by this EndpointPermission. Must be null if not
	 * constructed with a endpoint.
	 */
	transient final EndpointDescription					endpoint;
	
	/**
	 * This dictionary holds the properties of the permission, used to match a
	 * filter in implies.
	 */
	private transient final Dictionary<String, Object>	properties;

	/**
	 * If this EndpointPermission was not constructed with an
	 * EndpointDescription, this holds a Filter matching object used to evaluate
	 * the filter in implies or null for wildcard.
	 */
	transient Filter					filter;

	/**
	 * Create a new EndpointPermission with the specified filter.
	 * 
	 * <p>
	 * The filter will be evaluated against the endpoint properties of a
	 * requested EndpointPermission.
	 * 
	 * <p>
	 * There are three possible actions: <code>read</code>, <code>import</code>
	 * and <code>export</code>. The <code>read</code> action allows the owner of
	 * this permission to see the presence of distributed services. The
	 * <code>import</code> action allows the owner of this permission to import
	 * an endpoint. The <code>export</code> action allows the owner of this
	 * permission to export a service.
	 * 
	 * @param filterString The filter string or &quot;*&quot; to match all
	 *        endpoints.
	 * @param actions The actions <code>read</code>, <code>import</code>, or
	 *        <code>export</code>.
	 * @throws IllegalArgumentException If the filter has an invalid syntax or
	 *         the actions are not valid.
	 */
	public EndpointPermission(String filterString, String actions) {
		this(filterString, parseActions(actions));
	}

	/**
	 * Creates a new requested <code>EndpointPermission</code> object to be used
	 * by code that must perform <code>checkPermission</code>.
	 * <code>EndpointPermission</code> objects created with this constructor
	 * cannot be added to an <code>EndpointPermission</code> permission
	 * collection.
	 * 
	 * @param endpoint The requested endpoint.
	 * @param localFrameworkUUID The UUID of the local framework. This is used
	 *        to support matching the
	 *        {@link RemoteConstants#ENDPOINT_FRAMEWORK_UUID
	 *        endpoint.framework.uuid} endpoint property to the
	 *        <code>&lt;&lt;LOCAL&gt;&gt;</code> value in the filter expression.
	 * @param actions The actions <code>read</code>, <code>import</code>, or
	 *        <code>export</code>.
	 * @throws IllegalArgumentException If the endpoint is <code>null</code> or
	 *         the actions are not valid.
	 */
	public EndpointPermission(EndpointDescription endpoint,
			String localFrameworkUUID, String actions) {
		super(createName(endpoint));
		setTransients(null, parseActions(actions));
		Map<String, Object> props;
		if ((localFrameworkUUID != null)
				&& localFrameworkUUID.equals(endpoint.getFrameworkUUID())) {
			props = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
			props.putAll(endpoint.getProperties());
			props.put(ENDPOINT_FRAMEWORK_UUID, new String[] {
					endpoint.getFrameworkUUID(), "<<LOCAL>>"});
		}
		else {
			props = endpoint.getProperties();
		}
		this.endpoint = endpoint;
		this.properties = new EndpointDescription.UnmodifiableDictionary<String, Object>(
				props);
	}

	/**
	 * Create a permission name from a EndpointDescription.
	 * 
	 * @param endpoint EndpointDescription to use to create permission name.
	 * @return permission name.
	 */
	private static String createName(EndpointDescription endpoint) {
		if (endpoint == null) {
			throw new IllegalArgumentException("invalid endpoint: null");
		}
		StringBuffer sb = new StringBuffer("(" + ENDPOINT_ID + "=");
		sb.append(endpoint.getId());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Package private constructor used by EndpointPermissionCollection.
	 * 
	 * @param name class name
	 * @param mask action mask
	 */
	EndpointPermission(String name, int mask) {
		super(name);
		setTransients(parseFilter(name), mask);
		this.endpoint = null;
		this.properties = null;
	}

	/**
	 * Called by constructors and when deserialized.
	 * 
	 * @param mask action mask
	 */
	private void setTransients(Filter f, int mask) {
		if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask)) {
			throw new IllegalArgumentException("invalid action string");
		}
		action_mask = mask;
		filter = f;
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
				mask |= ACTION_IMPORT | ACTION_READ;

			}
			else
				if (i >= 5 && (a[i - 5] == 'e' || a[i - 5] == 'E')
						&& (a[i - 4] == 'x' || a[i - 4] == 'X')
						&& (a[i - 3] == 'p' || a[i - 3] == 'P')
						&& (a[i - 2] == 'o' || a[i - 2] == 'O')
						&& (a[i - 1] == 'r' || a[i - 1] == 'R')
						&& (a[i] == 't' || a[i] == 'T')) {
					matchlen = 6;
					mask |= ACTION_EXPORT | ACTION_READ;

				}
				else {
					if (i >= 3 && (a[i - 3] == 'r' || a[i - 3] == 'R')
							&& (a[i - 2] == 'e' || a[i - 2] == 'E')
							&& (a[i - 1] == 'a' || a[i - 1] == 'A')
							&& (a[i] == 'd' || a[i] == 'D')) {
						matchlen = 4;
						mask |= ACTION_READ;

					}
					else {
						// parse error
						throw new IllegalArgumentException(
								"invalid permission: " + actions);
					}
				}

			// make sure we didn't just match the tail of a word
			// like "ackbarfread". Also, skip to the comma.
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
	 * Parse filter string into a Filter object.
	 * 
	 * @param filterString The filter string to parse.
	 * @return a Filter for this bundle.
	 * @throws IllegalArgumentException If the filter syntax is invalid.
	 */
	private static Filter parseFilter(String filterString) {
		if (filterString == null) {
			throw new IllegalArgumentException("invalid filter: null");
		}
		filterString = filterString.trim();
		if (filterString.equals("*")) {
			return null; // wildcard
		}
		try {
			return FrameworkUtil.createFilter(filterString);
		}
		catch (InvalidSyntaxException e) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"invalid filter");
			iae.initCause(e);
			throw iae;
		}
	}

	/**
	 * Determines if a <code>EndpointPermission</code> object "implies" the
	 * specified permission.
	 * 
	 * @param p The target permission to check.
	 * @return <code>true</code> if the specified permission is implied by this
	 *         object; <code>false</code> otherwise.
	 */
	public boolean implies(Permission p) {
		if (!(p instanceof EndpointPermission)) {
			return false;
		}
		EndpointPermission requested = (EndpointPermission) p;
		if (endpoint != null) {
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
	 * @param requested The requested EndpointPermission which has already be
	 *        validated as a proper argument. The requested EndpointPermission
	 *        must not have a filter expression.
	 * @param effective The effective actions with which to start.
	 * @return <code>true</code> if the specified permission is implied by this
	 *         object; <code>false</code> otherwise.
	 */
	boolean implies0(EndpointPermission requested, int effective) {
		/* check actions first - much faster */
		effective |= action_mask;
		final int desired = requested.action_mask;
		if ((effective & desired) != desired) {
			return false;
		}
		/* if we have no filter */
		Filter f = filter;
		if (f == null) {
			// it's "*"
			return true;
		}
		return f.matchCase(requested.getProperties());
	}

	/**
	 * Returns the canonical string representation of the actions. Always
	 * returns present actions in the following canonical order:
	 * <code>read</code>, <code>import</code>, <code>export</code>.
	 * 
	 * @return The canonical string representation of the actions.
	 */
	public String getActions() {
		String result = actions;
		if (result == null) {
			StringBuffer sb = new StringBuffer();
			boolean comma = false;

			int mask = action_mask;
			if ((mask & ACTION_READ) == ACTION_READ) {
				sb.append(READ);
				comma = true;
			}

			if ((mask & ACTION_IMPORT) == ACTION_IMPORT) {
				if (comma)
					sb.append(',');
				sb.append(IMPORT);
			}

			if ((mask & ACTION_EXPORT) == ACTION_EXPORT) {
				if (comma)
					sb.append(',');
				sb.append(EXPORT);
			}

			actions = result = sb.toString();
		}

		return result;
	}

	/**
	 * Returns a new <code>PermissionCollection</code> object for storing
	 * <code>EndpointPermission<code> objects.
	 * 
	 * @return A new <code>PermissionCollection</code> object suitable for
	 *         storing <code>EndpointPermission</code> objects.
	 */
	public PermissionCollection newPermissionCollection() {
		return new EndpointPermissionCollection();
	}

	/**
	 * Determines the equality of two EndpointPermission objects.
	 * 
	 * Checks that specified object has the same name, actions and endpoint as
	 * this <code>EndpointPermission</code>.
	 * 
	 * @param obj The object to test for equality.
	 * @return true if obj is a <code>EndpointPermission</code>, and has the
	 *         same name, actions and endpoint as this
	 *         <code>EndpointPermission</code> object; <code>false</code>
	 *         otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof EndpointPermission)) {
			return false;
		}

		EndpointPermission ep = (EndpointPermission) obj;

		return (action_mask == ep.action_mask)
				&& getName().equals(ep.getName())
				&& ((endpoint == ep.endpoint) || ((endpoint != null)
						&& (ep.endpoint != null) && endpoint
						.equals(ep.endpoint)));
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return Hash code value for this object.
	 */
	public int hashCode() {
		int h = 31 * 17 + getName().hashCode();
		h = 31 * h + getActions().hashCode();
		if (endpoint != null) {
			h = 31 * h + endpoint.hashCode();
		}
		return h;
	}

	/**
	 * WriteObject is called to save the state of this permission to a stream.
	 * The actions are serialized, and the superclass takes care of the name.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws IOException {
		if (endpoint != null) {
			throw new NotSerializableException("cannot serialize");
		}
		// Write out the actions. The superclass takes care of the name
		// call getActions to make sure actions field is initialized
		if (actions == null) {
			getActions();
		}
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
		setTransients(parseFilter(getName()), parseActions(actions));
	}

	/**
	 * Called by <code><@link EndpointPermission#implies(Permission)></code>.
	 * 
	 * @return a dictionary of properties for this permission.
	 */
	private Dictionary<String, Object> getProperties() {
		return properties;
	}
}

/**
 * Stores a set of EndpointPermission permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */
final class EndpointPermissionCollection extends PermissionCollection {
	static final long						serialVersionUID	= 662615640374640621L;
	/**
	 * Table of permissions.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private Map<String, EndpointPermission>	permissions;

	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private boolean							all_allowed;

	/**
	 * Creates an empty EndpointPermissions object.
	 */
	public EndpointPermissionCollection() {
		permissions = new HashMap<String, EndpointPermission>();
		all_allowed = false;
	}

	/**
	 * Adds a permission to this permission collection.
	 * 
	 * @param permission The Permission object to add.
	 * @throws IllegalArgumentException If the specified permission is not a
	 *         EndpointPermission object.
	 * @throws SecurityException If this
	 *         <code>EndpointPermissionCollection</code> object has been marked
	 *         read-only.
	 */
	public void add(final Permission permission) {
		if (!(permission instanceof EndpointPermission)) {
			throw new IllegalArgumentException("invalid permission: "
					+ permission);
		}
		if (isReadOnly()) {
			throw new SecurityException("attempt to add a Permission to a "
					+ "readonly PermissionCollection");
		}

		final EndpointPermission ep = (EndpointPermission) permission;
		if (ep.endpoint != null) {
			throw new IllegalArgumentException("cannot add to collection: "
					+ ep);
		}

		final String name = ep.getName();
		synchronized (this) {
			/* select the bucket for the permission */
			Map<String, EndpointPermission> pc = permissions;
			final EndpointPermission existing = (EndpointPermission) pc
					.get(name);

			if (existing != null) {
				final int oldMask = existing.action_mask;
				final int newMask = ep.action_mask;
				if (oldMask != newMask) {
					pc.put(name,
							new EndpointPermission(name, oldMask | newMask));
				}
			}
			else {
				pc.put(name, ep);
			}

			if (!all_allowed) {
				if (name.equals("*")) {
					all_allowed = true;
				}
			}
		}
	}

	/**
	 * Determines if a set of permissions implies the permissions expressed in
	 * <code>permission</code>.
	 * 
	 * @param permission The Permission object to compare.
	 * @return <code>true</code> if <code>permission</code> is a proper subset
	 *         of a permission in the set; <code>false</code> otherwise.
	 */
	public boolean implies(final Permission permission) {
		if (!(permission instanceof EndpointPermission)) {
			return false;
		}
		final EndpointPermission requested = (EndpointPermission) permission;
		/* if requested permission has a filter, then it is an invalid argument */
		if (requested.filter != null) {
			return false;
		}
		int effective = EndpointPermission.ACTION_NONE;
		Collection<EndpointPermission> perms;
		synchronized (this) {
			final int desired = requested.action_mask;
			/* short circuit if the "*" Permission was added */
			if (all_allowed) {
				EndpointPermission ep = permissions.get("*");
				if (ep != null) {
					effective |= ep.action_mask;
					if ((effective & desired) == desired) {
						return true;
					}
				}
			}
			perms = permissions.values();
		}

		/* iterate one by one over permissions */
		for (Iterator<EndpointPermission> iter = perms.iterator(); iter
				.hasNext();) {
			if (iter.next().implies0(requested, effective)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an enumeration of all the <code>EndpointPermission</code> objects
	 * in the container.
	 * 
	 * @return Enumeration of all the EndpointPermission objects.
	 */
	public synchronized Enumeration<Permission> elements() {
		List<Permission> all = new ArrayList<Permission>(permissions.values());
		return Collections.enumeration(all);
	}

	/* serialization logic */
	private static final ObjectStreamField[]	serialPersistentFields	= {
			new ObjectStreamField("permissions", HashMap.class),
			new ObjectStreamField("all_allowed", Boolean.TYPE)			};

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
		permissions = (HashMap<String, EndpointPermission>) gfields.get(
				"permissions", new HashMap<String, EndpointPermission>());
		all_allowed = gfields.get("all_allowed", false);
	}
}
