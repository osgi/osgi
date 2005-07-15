/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.cm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;

/**
 * Indicates a bundle's authority to set or get a Configuration.
 * 
 * This permission verifies if the caller has the authority to get the
 * configuration of a specific PID and to get set it. The target of this
 * permission is a filter expression. The filter gives access to the following
 * parameters:
 * <ul>
 * <li>signer - The signer of the bundle that receives the configuration</li>
 * <li>location - The location of the bundle that receives the configuration
 * </li>
 * <li>id - The id of the bundle that receives the configuration</li>
 * <li>name - The bundle symbolic name of the bundle that receives the
 * configuration</li>
 * <li>pid - The PID for a singleton configuration</li>
 * <li>factoryPid - The Factory PID for a factory configuration</li>
 * </ul>
 * The target bundle is not always known ahead of time. In that case, signer,
 * name and location will not be set. The target bundle can be found by the fact
 * that it has already registered a target service with the given PID, or the
 * setter has set the bundle location, which identifies the target bundle. The
 * filter can also check what PID (for singletons) or factory PID (for
 * factories) is used. <br/>The actions for this permission are:
 * <ul>
 * <li>SET - The right to update a configuration. Checked during the
 * <code>update</code> method. This implies READ</li>
 * <li>GET - The right to be updated. The Configuration Admin must verify that
 * the bundle that receives the configuration has permission with the
 * <code>hasPermission</code> method before it calls the right variation of
 * the <code>updated</code> method.</li>
 * <li>READ - The right to see a Configuration object. This action is needed
 * also when creation a new configuration.</li>
 * <li>REBIND - The right to change the binding of an existing bound
 * configuration.</li>
 * <li>PLUGIN - The right to be called as a plugin</li>
 * </ul>
 * Every bundle must implicitly given the permission to set its own
 * configuration.
 * 
 * @version $Revision$
 * @since 1.2
 */

public final class ConfigurationPermission extends Permission {
	static final long				serialVersionUID	= 5716868734811965383L;
	/**
	 * The action string <code>get</code>.
	 */
	public final static String		GET					= "get";
	/**
	 * The action string <code>set</code>. This implies ### what
	 */
	public final static String		SET					= "set";

	/**
	 * The action string <code>read</code>.
	 */
	public final static String		READ				= "read";

	/**
	 * The action string <code>rebind</code>.
	 */
	public static final String		REBIND				= "rebind";

	private final static int		ACTION_GET			= 0x00000001;
	private final static int		ACTION_SET			= 0x00000002;
	private final static int		ACTION_READ			= 0x00000004;
	private final static int		ACTION_REBIND		= 0x00000008;
	private final static int		ACTION_ALL			= ACTION_GET
																| ACTION_SET
																| ACTION_READ
																| ACTION_REBIND;
	private final static int		ACTION_NONE			= 0;
	private final static int		ACTION_ERROR		= 0x80000000;

	private final static int		ACTIONS[]			= {ACTION_GET,
			ACTION_SET, ACTION_READ, ACTION_REBIND		};
	private final static char[][][]	ACTION_NAMES		= {
		new char[][] {new char[] {'g', 'G'}, new char[] {'e', 'E'}, new char[] {'t', 'T'}},
		new char[][] {new char[] {'s', 'S'}, new char[] {'e', 'E'}, new char[] {'t', 'T'}},
		new char[][] {new char[] {'r', 'R'}, new char[] {'e', 'E'}, new char[] {'a', 'A'}, new char[] {'d', 'D'}},
		new char[][] {new char[] {'r', 'R'}, new char[] {'e', 'E'}, new char[] {'b', 'B'}, new char[] {'i', 'I'}, new char[] {'n', 'N'}, new char[]{'d', 'D'}}};

	private static String			KEY_SIGNER			= "signer";
	private static String			KEY_LOCATION		= "location";
	private static String			KEY_ID				= "id";
	private static String			KEY_NAME			= "name";
	private static String			KEY_PID				= "pid";
	private static String			KEY_FACTORYPID		= "factoryPid";

	private transient int			action_mask			= ACTION_NONE;
	private transient Filter		filter				= null;
	private transient Bundle		bundle				= null;
	private transient String		pid					= null;
	private transient String		f_pid				= null;
	private transient String		actions				= null;
	private transient Dictionary	properties			= null;

	/**
	 * Create a new ConfigurationPermission.
	 * 
	 * This constructor must only be used to create a permission that is going
	 * to be checked. Configuration Admin service must use the alternate
	 * constructor.
	 * 
	 * <p>
	 * Examples:
	 * 
	 * <pre>
	 *          (signer=*,o=ACME,c=US)   
	 *          (pid=com.acme.*)
	 *          (factoryPid=com.acme.*)
	 *          (&amp;(signer=*,o=ACME,c=US)(pid=com.acme.*))   
	 * </pre>
	 * 
	 * <p>
	 * There are the following actions: <code>get</code>,<code>set</code>,
	 * and <code>read</code>. The <code>set</code> action allows a bundle
	 * to create, list, get, update and delete configurations for the target.
	 * The <code>get</code> action allows a bundle to receive a configuration
	 * object. The target bundle is itself in that case. The <code>read</code>
	 * action permits listing the configurations. The <code>set</code> action
	 * implies <code>read</code>.
	 * 
	 * @param filter A filter expression that can use signer, location, name,
	 *        pid or factoryPid
	 * @param actions <code>get</code>, <code>set</code>,
	 *        <code>read</code> (canonical order)
	 */

	public ConfigurationPermission(String filter, String actions) {
		this(filter, getMask(actions));
	}

	private ConfigurationPermission(String filter, int mask) {
		super(filter);
		init(mask);
		try {
			filter = escapeSigner(filter);
			this.filter = FrameworkUtil.createFilter(filter);
		}
		catch (InvalidSyntaxException e) {
			// TODO not sure what to do here; for now this will
			// cause filter to be null which makes this permission
			// always return false from implies. Could throw an
			// IllegalArgumentException
		}
	}

	private String escapeSigner(String sFilter) {
		int pos = sFilter.indexOf(KEY_SIGNER);
		if (pos < 0)
			return sFilter;

		// there may be a signer attribute
		StringBuffer filterBuf = new StringBuffer(sFilter);
		int numAsteriskFound = 0; // use as offset to replace in buffer
		int walkbackPos; // temp pos
		// find occurences of (signer= and escape out *'s
		while (pos != -1) {
			// walk back and look for '(' to see if this is an attr
			walkbackPos = pos - 1;
			// consume whitespace
			while (walkbackPos >= 0
					&& Character.isWhitespace(sFilter.charAt(walkbackPos)))
				walkbackPos--;
			if (walkbackPos < 0)
				// filter is invalid - Filter creation will throw error
				break;

			// check to see if we have unescaped '('
			if (sFilter.charAt(walkbackPos) != '('
					|| (walkbackPos > 0 && sFilter.charAt(walkbackPos - 1) == '\\')) {
				// '(' was escaped or not there
				pos = sFilter.indexOf(KEY_SIGNER, pos + KEY_SIGNER.length()); //$NON-NLS-1$
				continue;
			}
			pos += KEY_SIGNER.length(); // skip over 'signer'

			// found signer - consume whitespace before '='
			while (Character.isWhitespace(sFilter.charAt(pos)))
				pos++;

			// look for '='
			if (sFilter.charAt(pos) != '=') {
				// attr was signerx - keep looking
				pos = sFilter.indexOf(KEY_SIGNER, pos); //$NON-NLS-1$
				continue;
			}
			pos++; // skip over '='

			// found signer value - escape '*'s
			while (!(sFilter.charAt(pos) == ')' && sFilter.charAt(pos - 1) != '\\')) {
				if (sFilter.charAt(pos) == '*') {
					filterBuf.insert(pos + numAsteriskFound, '\\');
					numAsteriskFound++;
				}
				pos++;
			}

			// end of signer value - look for more?
			pos = sFilter.indexOf(KEY_SIGNER, pos); //$NON-NLS-1$
		} // end while (pos != -1)
		return filterBuf.toString();
	}

	/**
	 * Create a new Configuration Permission. This constructor is the check
	 * version. It must be used by the Configuration Admin service to check the
	 * permission.
	 * 
	 * @param bundle The target bundle. This must be the bundle that will
	 *        receive the configuration.
	 * @param pid The PID in question (for factories, the PID is generated and
	 *        not checkeable).
	 * @param factoryPid The PID of the factory if this is a factory
	 *        configuration
	 * @param action The action (GET, SET, READ or REBIND [no combinations
	 *        allowed])
	 */
	public ConfigurationPermission(Bundle bundle, String pid,
			String factoryPid, String action) {
		super(Long.toString(bundle.getBundleId()));
		this.bundle = bundle;
		this.pid = pid;
		this.f_pid = factoryPid;
		init(getMask(action));
	}

	/**
	 * Determines if a <code>ConfigurationPermission</code> object "implies"
	 * the specified permission.
	 * 
	 * @param p The target permission to check.
	 * @return <code>true</code> if the specified permission is implied by
	 *         this object; <code>false</code> otherwise.
	 */

	public boolean implies(Permission p) {
		if (!(p instanceof ConfigurationPermission))
			return false;
		ConfigurationPermission target = (ConfigurationPermission) p;
		// check actions first - much faster
		if ((action_mask & target.action_mask) != target.action_mask)
			return false;
		// if passed in a filter bail now
		if (target.filter != null)
			throw new RuntimeException("Cannot imply a filter");
		return filter != null && filter.match(target.getProperties());
	}

	/**
	 * Returns the canonical string representation of the
	 * <code>ConfigurationPermission</code> actions.
	 * 
	 * <p>
	 * Always returns present <code>ConfigurationPermission</code> actions in
	 * the following order: <code>GET</code>, <code>SET</code>,
	 * <code>READ</code>, <code>REBIND</code>
	 * 
	 * @return Canonical string representation of the
	 *         <code>ConfigurationPermission</code> actions.
	 */
	public String getActions() {
		if (actions == null) {
			StringBuffer sb = new StringBuffer();
			boolean comma = false;

			if ((action_mask & ACTION_GET) == ACTION_GET) {
				sb.append(GET);
				comma = true;
			}
			if ((action_mask & ACTION_SET) == ACTION_SET) {
				if (comma)
					sb.append(',');
				sb.append(SET);
				comma = true;
			}
			if ((action_mask & ACTION_READ) == ACTION_READ) {
				if (comma)
					sb.append(',');
				sb.append(READ);
				comma = true;
			}
			if ((action_mask & ACTION_REBIND) == ACTION_REBIND) {
				if (comma)
					sb.append(',');
				sb.append(REBIND);
				comma = true;
			}
			actions = sb.toString();
		}
		return actions;
	}

	/**
	 * Returns a new <code>PermissionCollection</code> object suitable for
	 * storing <code>ConfigurationPermission</code>s.
	 * 
	 * @return A new <code>PermissionCollection</code> object.
	 */
	public PermissionCollection newPermissionCollection() {
		return new ConfigurationPermissionCollection();
	}

	/**
	 * Determines the equality of two <code>ConfigurationPermission</code>
	 * objects.
	 * <p>
	 * Two <code>ConfigurationPermission</code> objects are equal.
	 * 
	 * @param obj The object being compared for equality with this object.
	 * @return <code>true</code> if <code>obj</code> is equivalent to this
	 *         <code>ConfigurationPermission</code>; <code>false</code>
	 *         otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ConfigurationPermission))
			return false;
		ConfigurationPermission p = (ConfigurationPermission) obj;
		// use == to compare bundle objects because they must be the
		// same object to be equal.
		return ((action_mask == p.action_mask)&& 
				(bundle == p.bundle) &&
				(pid == null ? p.pid == null : pid.equals(p.pid)) &&
				(f_pid == null ? p.f_pid == null : f_pid.equals(p.f_pid)) &&
				(filter == null ? p.filter == null	: filter.equals(filter)));
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return Hash code value for this object.
	 */

	public int hashCode() {
		int result = action_mask;
		if (filter != null)
			result = result ^ filter.hashCode();
		if (bundle != null)
			result = result ^ getProperties().hashCode();
		return result;
	}

	/**
	 * Called by constructors.
	 * 
	 * @param mask action mask
	 */
	private void init(int mask) {
		if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask))
			throw new IllegalArgumentException("invalid action string");
		action_mask = mask;
	}

	/**
	 * Parse action string into action mask.
	 * 
	 * @param actions Action string.
	 * @return action mask.
	 */
	private static int getMask(String actions) {
		boolean seencomma = false;
		int mask = ACTION_NONE;
		if (actions == null)
			return mask;
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
			int matchlen = -1;
			action: for (int j = 0; j < ACTION_NAMES.length; j++) {
				if (i >= ACTION_NAMES[j].length - 1) {
					for (int k = 0; k < ACTION_NAMES[j].length; k++)
						if (a[i - k] != ACTION_NAMES[j][ACTION_NAMES[j].length - 1 - k][0]
								&& a[i - k] != ACTION_NAMES[j][ACTION_NAMES[j].length - 1 - k][1])
							continue action;
					matchlen = ACTION_NAMES[j].length;
					mask |= ACTIONS[j];
					break action;
				}
			}
			if (matchlen == -1)
				// parse error
				throw new IllegalArgumentException("invalid permission: "
						+ actions);

			// make sure we didn't just match the tail of a word
			// like "ackbarfstartlevel". Also, skip to the comma.
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

		if (seencomma)
			throw new IllegalArgumentException("invalid permission: " + actions);

		return mask;
	}

	private synchronized Dictionary getProperties() {
		if (properties == null) {
			properties = new Hashtable();
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					if (bundle != null) {
						// set ID
						properties.put(KEY_ID, new Long(bundle.getBundleId()));
						// set location
						properties.put(KEY_LOCATION, bundle.getLocation());
						// set name
						if (bundle.getSymbolicName() != null)
							properties.put(KEY_NAME, bundle.getSymbolicName());
						// set signers
						properties.put(KEY_SIGNER, new SignerWrapper(bundle));
					}
					// set pid
					if (pid != null)
						properties.put(KEY_PID, pid);
					// set factoryPid
					if (f_pid != null)
						properties.put(KEY_FACTORYPID, f_pid);
					return properties;
				}
			});
		}
		return properties;
	}

	private final class ConfigurationPermissionCollection extends
			PermissionCollection {
		static final long	serialVersionUID	= -6917638867081695839L;
		/**
		 * Table of permissions.
		 * 
		 * @serial
		 */
		private Hashtable	permissions;

		/**
		 * Creates an empty ConfigurationPermissions object.
		 * 
		 */
		public ConfigurationPermissionCollection() {
			permissions = new Hashtable();
		}

		/**
		 * Adds a permission to the <code>ConfigurationPermission</code>
		 * objects using the key for the hash as the name.
		 * 
		 * @param permission The Permission object to add.
		 * 
		 * @exception IllegalArgumentException If the permission is not a
		 *            ConfigurationPermission object.
		 * 
		 * @exception SecurityException If this
		 *            <code>ConfigurationPermissionCollection</code> object
		 *            has been marked read-only.
		 */

		public void add(Permission permission) {
			if (!(permission instanceof ConfigurationPermission))
				throw new IllegalArgumentException("invalid permission: "
						+ permission);
			if (isReadOnly())
				throw new SecurityException("attempt to add a Permission to a "
						+ "readonly PermissionCollection");

			ConfigurationPermission sp = (ConfigurationPermission) permission;
			String name = sp.getName();

			ConfigurationPermission existing = (ConfigurationPermission) permissions
					.get(name);

			if (existing != null) {
				int oldMask = existing.action_mask;
				int newMask = sp.action_mask;
				if (oldMask != newMask) {
					permissions.put(name, new ConfigurationPermission(name,
							oldMask | newMask));
				}
			}
			else {
				permissions.put(name, permission);
			}
		}

		/**
		 * Determines if a set of permissions implies the permissions expressed
		 * in <code>permission</code>.
		 * 
		 * @param permission The Permission object to compare.
		 * 
		 * @return <code>true</code> if <code>permission</code> is a proper
		 *         subset of a permission in the set; <code>false</code>
		 *         otherwise.
		 */

		public boolean implies(Permission permission) {
			if (!(permission instanceof ConfigurationPermission))
				return (false);
			// just iterate one by one
			Iterator permItr = permissions.values().iterator();
			while (permItr.hasNext())
				if (((Permission) permItr.next()).implies(permission))
					return true;
			return false;
		}

		/**
		 * Returns an enumeration of all the
		 * <code>ConfigurationPermission</code> objects in the container.
		 * 
		 * @return Enumeration of all the ConfigurationPermission objects.
		 */

		public Enumeration elements() {
			return (permissions.elements());
		}
	}

	private static class SignerWrapper {
		private static Method		GET_CONDITION;
		private static Constructor	INFO_CONSTRUCTOR;
		private static Object		TRUE;
		static {
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					try {
						Class bundleSignerCondition = Class
								.forName("org.osgi.service.condpermadmin.BundleSignerCondition");
						Class condition = Class
								.forName("org.osgi.service.condpermadmin.Condition");
						Class info = Class
								.forName("org.osgi.service.condpermadmin.ConditionInfo");
						GET_CONDITION = bundleSignerCondition.getMethod(
								"getCondition",
								new Class[] {Bundle.class, info});
						TRUE = condition.getField("TRUE").get(null);
						INFO_CONSTRUCTOR = info.getConstructor(new Class[] {
								String.class, String[].class});
					}
					catch (Throwable e) {
						e.printStackTrace();
						// no signer support
					}
					return null;
				}
			});
		}
		private Bundle				bundle;
		private Object				info;

		public SignerWrapper(String pattern) {
			try {
				this.info = INFO_CONSTRUCTOR.newInstance(new Object[] {
						"org.osgi.service.condpermadmin.BundleSignerCondition",
						new String[] {pattern}});
			}
			catch (Exception e) {
			}
		}

		SignerWrapper(Bundle bundle) {
			this.bundle = bundle;
		}

		public boolean equals(Object o) {
			if (!(o instanceof SignerWrapper))
				return false;
			SignerWrapper other = (SignerWrapper) o;
			try {
				return GET_CONDITION != null
						&& other.info != null
						&& TRUE == GET_CONDITION.invoke(null, new Object[] {
								bundle, other.info});
			}
			catch (Exception e) {
			}
			return false;
		}
	}
}