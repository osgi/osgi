/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

package org.osgi.service.coordinator;

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
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * A bundle's authority to create or use a {@link Coordination}.
 * 
 * <p>
 * {@code CoordinationPermission} has three actions: {@code initiate},
 * {@code participate} and {@code admin}.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public final class CoordinationPermission extends BasicPermission {

	private static final long						serialVersionUID	= 1L;

	/**
	 * The action string {@code initiate}.
	 */
	public final static String						INITIATE			= "initiate";
	/**
	 * The action string {@code participate}.
	 */
	public final static String						PARTICIPATE			= "participate";
	/**
	 * The action string {@code admin}.
	 */
	public final static String						ADMIN				= "admin";

	private final static int						ACTION_INITIATE		= 0x00000001;
	private final static int						ACTION_PARTICIPATE	= 0x00000002;
	private final static int						ACTION_ADMIN		= 0x00000004;
	private final static int						ACTION_ALL			= ACTION_INITIATE | ACTION_PARTICIPATE | ACTION_ADMIN;
	final static int								ACTION_NONE			= 0;

	/**
	 * The actions mask.
	 */
	transient int									action_mask;

	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private volatile String							actions				= null;

	/**
	 * The bundle used by this CoordinationPermission.
	 */
	transient final Bundle							bundle;

	/**
	 * If this CoordinationPermission was constructed with a filter, this holds
	 * a Filter matching object used to evaluate the filter in implies.
	 */
	transient Filter								filter;

	/**
	 * This map holds the properties of the permission, used to match a filter
	 * in implies. This is not initialized until necessary, and then cached in
	 * this object.
	 */
	private transient volatile Map<String, Object>	properties;

	/**
	 * Creates a new granted {@code CoordinationPermission} object.
	 * 
	 * This constructor must only be used to create a permission that is going
	 * to be checked.
	 * <p>
	 * Examples:
	 * 
	 * <pre>
	 * (coordination.name=com.acme.*)
	 * (&amp;(signer=\*,o=ACME,c=US)(coordination.name=com.acme.*))
	 * (signer=\*,o=ACME,c=US)
	 * </pre>
	 * 
	 * <p>
	 * When a signer key is used within the filter expression the signer value
	 * must escape the special filter chars ('*', '(', ')').
	 * <p>
	 * The name is specified as a filter expression. The filter gives access to
	 * the following attributes:
	 * <ul>
	 * <li>signer - A Distinguished Name chain used to sign the exporting
	 * bundle. Wildcards in a DN are not matched according to the filter string
	 * rules, but according to the rules defined for a DN chain.</li>
	 * <li>location - The location of the exporting bundle.</li>
	 * <li>id - The bundle ID of the exporting bundle.</li>
	 * <li>name - The symbolic name of the exporting bundle.</li>
	 * <li>coordination.name - The name of the requested coordination.</li>
	 * </ul>
	 * Filter attribute names are processed in a case sensitive manner.
	 * 
	 * @param filter A filter expression. Filter attribute names are processed
	 *        in a case sensitive manner. A special value of {@code "*"} can be
	 *        used to match all coordinations.
	 * @param actions {@code admin}, {@code initiate} or {@code participate}
	 *        (canonical order).
	 * @throws IllegalArgumentException If the filter has an invalid syntax.
	 */
	public CoordinationPermission(String filter, String actions) {
		this(parseFilter(filter), parseActions(actions));
	}

	/**
	 * Creates a new requested {@code CoordinationPermission} object to be used
	 * by the code that must perform {@code checkPermission}.
	 * {@code CoordinationPermission} objects created with this constructor
	 * cannot be added to an {@code CoordinationPermission} permission
	 * collection.
	 * 
	 * @param coordinationName The name of the requested Coordination.
	 * @param coordinationBundle The bundle which
	 *        {@link Coordination#getBundle() created} the requested
	 *        Coordination.
	 * @param actions {@code admin}, {@code initiate} or {@code participate}
	 *        (canonical order).
	 */
	public CoordinationPermission(String coordinationName, Bundle coordinationBundle, String actions) {
		super(coordinationName);
		setTransients(null, parseActions(actions));
		this.bundle = coordinationBundle;
		if (coordinationName == null) {
			throw new NullPointerException("coordinationName must not be null");
		}
		if (coordinationBundle == null) {
			throw new NullPointerException("coordinationBundle must not be null");
		}
	}

	/**
	 * Package private constructor used by CoordinationPermissionCollection.
	 * 
	 * @param filter name filter
	 * @param mask action mask
	 */
	CoordinationPermission(Filter filter, int mask) {
		super((filter == null) ? "*" : filter.toString());
		setTransients(filter, mask);
		this.bundle = null;
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

			if (i >= 4 && (a[i - 4] == 'a' || a[i - 4] == 'A')
					&& (a[i - 3] == 'd' || a[i - 3] == 'D')
					&& (a[i - 2] == 'm' || a[i - 2] == 'M')
					&& (a[i - 1] == 'i' || a[i - 1] == 'I')
					&& (a[i] == 'n' || a[i] == 'N')) {
				matchlen = 5;
				mask |= ACTION_ADMIN;

			}
			else
				if (i >= 7 && (a[i - 7] == 'i' || a[i - 7] == 'I')
						&& (a[i - 6] == 'n' || a[i - 6] == 'N')
						&& (a[i - 5] == 'i' || a[i - 5] == 'I')
						&& (a[i - 4] == 't' || a[i - 4] == 'T')
						&& (a[i - 3] == 'i' || a[i - 3] == 'I')
						&& (a[i - 2] == 'a' || a[i - 2] == 'A')
						&& (a[i - 1] == 't' || a[i - 1] == 'T')
						&& (a[i] == 'e' || a[i] == 'E')) {
					matchlen = 8;
					mask |= ACTION_INITIATE;

				}
				else {
					if (i >= 10 && (a[i - 10] == 'p' || a[i - 10] == 'P')
							&& (a[i - 9] == 'a' || a[i - 9] == 'A')
							&& (a[i - 8] == 'r' || a[i - 8] == 'R')
							&& (a[i - 7] == 't' || a[i - 7] == 'T')
							&& (a[i - 6] == 'i' || a[i - 6] == 'I')
							&& (a[i - 5] == 'c' || a[i - 5] == 'C')
							&& (a[i - 4] == 'i' || a[i - 4] == 'I')
							&& (a[i - 3] == 'p' || a[i - 3] == 'P')
							&& (a[i - 2] == 'a' || a[i - 2] == 'A')
							&& (a[i - 1] == 't' || a[i - 1] == 'T')
							&& (a[i] == 'e' || a[i] == 'E')) {
						matchlen = 11;
						mask |= ACTION_PARTICIPATE;

					} else {
						// parse error
						throw new IllegalArgumentException("invalid permission: " + actions);
					}
				}

			// make sure we didn't just match the tail of a word
			// like "ackbarfadmin". Also, skip to the comma.
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
	 * @return a Filter for this bundle.
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
	 * Determines if the specified permission is implied by this object.
	 * 
	 * <p>
	 * This method checks that the filter of the target is implied by the
	 * coordination name of this object. The list of
	 * {@code CoordinationPermission} actions must either match or allow for the
	 * list of the target object to imply the target
	 * {@code CoordinationPermission} action.
	 * <p>
	 * 
	 * @param p The requested permission.
	 * @return {@code true} if the specified permission is implied by this
	 *         object; {@code false} otherwise.
	 */
	public boolean implies(Permission p) {
		if (!(p instanceof CoordinationPermission)) {
			return false;
		}
		CoordinationPermission requested = (CoordinationPermission) p;
		if (bundle != null) {
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
	 * @param requested The requested CoordinationPermission which has already
	 *        be validated as a proper argument. The requested
	 *        CoordinationPermission must not have a filter expression.
	 * @param effective The effective actions with which to start.
	 * @return {@code true} if the specified permission is implied by this
	 *         object; {@code false} otherwise.
	 */
	boolean implies0(CoordinationPermission requested, int effective) {
		/* check actions first - much faster */
		effective |= action_mask;
		final int desired = requested.action_mask;
		if ((effective & desired) != desired) {
			return false;
		}
		/* Get filter */
		Filter f = filter;
		if (f == null) {
			// it's "*"
			return true;
		}
		return f.matches(requested.getProperties());
	}

	/**
	 * Returns the canonical string representation of the
	 * {@code CoordinationPermission} actions.
	 * 
	 * <p>
	 * Always returns present {@code CoordinationPermission} actions in the
	 * following order: {@code admin}, {@code initiate}, {@code participate}.
	 * 
	 * @return Canonical string representation of the
	 *         {@code CoordinationPermission} actions.
	 */
	public String getActions() {
		String result = actions;
		if (result == null) {
			StringBuffer sb = new StringBuffer();
			boolean comma = false;

			int mask = action_mask;
			if ((mask & ACTION_ADMIN) == ACTION_ADMIN) {
				sb.append(ADMIN);
				comma = true;
			}

			if ((mask & ACTION_INITIATE) == ACTION_INITIATE) {
				if (comma)
					sb.append(',');
				sb.append(INITIATE);
				comma = true;
			}

			if ((mask & ACTION_PARTICIPATE) == ACTION_PARTICIPATE) {
				if (comma)
					sb.append(',');
				sb.append(PARTICIPATE);
				comma = true;
			}

			actions = result = sb.toString();
		}
		return result;
	}

	/**
	 * Returns a new {@code PermissionCollection} object suitable for storing
	 * {@code CoordinationPermission} objects.
	 * 
	 * @return A new {@code PermissionCollection} object.
	 */
	public PermissionCollection newPermissionCollection() {
		return new CoordinationPermissionCollection();
	}

	/**
	 * Determines the equality of two {@code CoordinationPermission} objects.
	 * 
	 * This method checks that specified permission has the same name and
	 * {@code CoordinationPermission} actions as this
	 * {@code CoordinationPermission} object.
	 * 
	 * @param obj The object to test for equality with this
	 *        {@code CoordinationPermission} object.
	 * @return {@code true} if {@code obj} is a {@code CoordinationPermission},
	 *         and has the same name and actions as this
	 *         {@code CoordinationPermission} object; {@code false} otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof CoordinationPermission)) {
			return false;
		}

		CoordinationPermission cp = (CoordinationPermission) obj;

		return (action_mask == cp.action_mask) && getName().equals(cp.getName()) && ((bundle == cp.bundle) || ((bundle != null) && bundle.equals(cp.bundle)));
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return A hash code value for this object.
	 */
	public int hashCode() {
		int h = 31 * 17 + getName().hashCode();
		h = 31 * h + getActions().hashCode();
		if (bundle != null) {
			h = 31 * h + bundle.hashCode();
		}
		return h;
	}

	/**
	 * WriteObject is called to save the state of this permission object to a
	 * stream. The actions are serialized, and the superclass takes care of the
	 * name.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
		if (bundle != null) {
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
		// Read in the action, then initialize the rest
		s.defaultReadObject();
		setTransients(parseFilter(getName()), parseActions(actions));
	}

	/**
	 * Called by {@link CoordinationPermission#implies(Permission)}. This method
	 * is only called on a requested permission which cannot have a filter set.
	 * 
	 * @return a map of properties for this permission.
	 */
	private Map<String, Object> getProperties() {
		Map<String, Object> result = properties;
		if (result != null) {
			return result;
		}
		final Map<String, Object> map = new HashMap<String, Object>(5);
		map.put("coordination.name", getName());
		if (bundle != null) {
			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					map.put("id", new Long(bundle.getBundleId()));
					map.put("location", bundle.getLocation());
					String name = bundle.getSymbolicName();
					if (name != null) {
						map.put("name", name);
					}
					SignerProperty signer = new SignerProperty(bundle);
					if (signer.isBundleSigned()) {
						map.put("signer", signer);
					}
					return null;
				}
			});
		}
		return properties = map;
	}
}

/**
 * Package private class used for filter matching on signer key during filter
 * expression evaluation in the permission implies method.
 * 
 * @Immutable
 */
final class SignerProperty {
	private final Bundle	bundle;
	private final String	pattern;

	/**
	 * String constructor used by the filter matching algorithm to construct a
	 * SignerProperty from the attribute value in a filter expression.
	 * 
	 * @param pattern Attribute value in the filter expression.
	 */
	public SignerProperty(String pattern) {
		this.pattern = pattern;
		this.bundle = null;
	}

	/**
	 * Used by the permission implies method to build the properties for a
	 * filter match.
	 * 
	 * @param bundle The bundle whose signers are to be matched.
	 */
	SignerProperty(Bundle bundle) {
		this.bundle = bundle;
		this.pattern = null;
	}

	/**
	 * Used by the filter matching algorithm. This methods does NOT satisfy the
	 * normal equals contract. Since the class is only used in filter expression
	 * evaluations, it only needs to support comparing an instance created with
	 * a Bundle to an instance created with a pattern string from the filter
	 * expression.
	 * 
	 * @param o SignerProperty to compare against.
	 * @return true if the DN name chain matches the pattern.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof SignerProperty))
			return false;
		SignerProperty other = (SignerProperty) o;
		Bundle matchBundle = bundle != null ? bundle : other.bundle;
		String matchPattern = bundle != null ? other.pattern : pattern;
		Map<X509Certificate, List<X509Certificate>> signers = matchBundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		for (List<X509Certificate> signerCerts : signers.values()) {
			List<String> dnChain = new ArrayList<String>(signerCerts.size());
			for (X509Certificate signerCert : signerCerts) {
				dnChain.add(signerCert.getSubjectDN().getName());
			}
			try {
				if (FrameworkUtil.matchDistinguishedNameChain(matchPattern, dnChain)) {
					return true;
				}
			} catch (IllegalArgumentException e) {
				continue; // bad pattern
			}
		}
		return false;
	}

	/**
	 * Since the equals method does not obey the general equals contract, this
	 * method cannot generate hash codes which obey the equals contract.
	 */
	public int hashCode() {
		return 31;
	}

	/**
	 * Check if the bundle is signed.
	 * 
	 * @return true if constructed with a bundle that is signed.
	 */
	boolean isBundleSigned() {
		if (bundle == null) {
			return false;
		}
		Map<X509Certificate, List<X509Certificate>> signers = bundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		return !signers.isEmpty();
	}
}

/**
 * Stores a set of {@code CoordinationPermission} permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */

final class CoordinationPermissionCollection extends PermissionCollection {
	static final long							serialVersionUID	= -3350758995234427603L;
	/**
	 * Collection of permissions.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private Map<String, CoordinationPermission>	permissions;

	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 * @GuardedBy this
	 */
	private boolean								all_allowed;

	/**
	 * Create an empty CoordinationPermissions object.
	 */
	public CoordinationPermissionCollection() {
		permissions = new HashMap<String, CoordinationPermission>();
		all_allowed = false;
	}

	/**
	 * Adds a permission to this permission collection.
	 * 
	 * @param permission The {@code CoordinationPermission} object to add.
	 * @throws IllegalArgumentException If the specified permission is not a
	 *         {@code CoordinationPermission} instance or was constructed with a
	 *         Bundle object.
	 * @throws SecurityException If this
	 *         {@code CoordinationPermissionCollection} object has been marked
	 *         read-only.
	 */
	public void add(final Permission permission) {
		if (!(permission instanceof CoordinationPermission)) {
			throw new IllegalArgumentException("invalid permission: " + permission);
		}
		if (isReadOnly()) {
			throw new SecurityException("attempt to add a Permission to a " + "readonly PermissionCollection");
		}

		final CoordinationPermission cp = (CoordinationPermission) permission;
		if (cp.bundle != null) {
			throw new IllegalArgumentException("cannot add to collection: " + cp);
		}

		final String name = cp.getName();
		synchronized (this) {
			Map<String, CoordinationPermission> pc = permissions;
			final CoordinationPermission existing = pc.get(name);
			if (existing != null) {
				final int oldMask = existing.action_mask;
				final int newMask = cp.action_mask;
				if (oldMask != newMask) {
					pc.put(name, new CoordinationPermission(existing.filter, oldMask | newMask));

				}
			} else {
				pc.put(name, cp);
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
	 *        {@code CoordinationPermission} object.
	 * @return {@code true} if {@code permission} is a proper subset of a
	 *         permission in the set; {@code false} otherwise.
	 */
	public boolean implies(final Permission permission) {
		if (!(permission instanceof CoordinationPermission)) {
			return false;
		}
		final CoordinationPermission requested = (CoordinationPermission) permission;
		/* if requested permission has a filter, then it is an invalid argument */
		if (requested.filter != null) {
			return false;
		}

		int effective = CoordinationPermission.ACTION_NONE;

		Collection<CoordinationPermission> perms;
		synchronized (this) {
			Map<String, CoordinationPermission> pc = permissions;
			/* short circuit if the "*" Permission was added */
			if (all_allowed) {
				CoordinationPermission cp = pc.get("*");
				if (cp != null) {
					effective |= cp.action_mask;
					final int desired = requested.action_mask;
					if ((effective & desired) == desired) {
						return true;
					}
				}
			}
			perms = pc.values();
		}
		/* iterate one by one over filteredPermissions */
		for (CoordinationPermission perm : perms) {
			if (perm.implies0(requested, effective)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an enumeration of all {@code CoordinationPermission} objects in
	 * the container.
	 * 
	 * @return Enumeration of all {@code CoordinationPermission} objects.
	 */
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
		permissions = (HashMap<String, CoordinationPermission>) gfields.get("permissions", null);
		all_allowed = gfields.get("all_allowed", false);
	}
}
