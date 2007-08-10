/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2006). All Rights Reserved.
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

package org.osgi.service.power;

import java.io.IOException;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <p>
 * Permission to set the system or/and device power states managed repectively
 * by {@link PowerManager} and {@link DevicePower} services.
 * 
 * <p>
 * The permission name is the name (or name prefix). The naming convention
 * follows the hierarchical naming convention. Also, an asterisk may appear at
 * the end of the name, following a &quot;.&quot;, or by itself, to signify a
 * wildcard match. For example: &quot;org.osgi.a.b.*&quot; or &quot;*&quot; is
 * valid, but &quot;*a.b&quot; or &quot;a*b&quot; are not valid.
 * <code>PowerPermission</code> with the reserved name &quot;system&quot;
 * represents the system power. <code>PowerPermission</code> with the reserved
 * name &quot;*&quot; represents the permission required for setting any devices
 * or system.
 * 
 * <p>
 * There are the following possible actions: <code>setSystemPower</code> and
 * <code>setDevicePower</code>
 * <ul>
 * <li><code>setSystemPower</code> action allows a bundle to set the system
 * power state.
 * <li><code>setDevicePower</code> action allows a bundle to set the device
 * power under that name.
 * <li>"*" can be used to set power states on devices and system.
 * </ul>
 * 
 * @version $Revision$
 */
public class PowerPermission extends BasicPermission {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	static final long			serialVersionUID		= -3438892802457100066L;
	/**
	 * The permission name &quot;system&quot; reprenting the system power.
	 */
	public static final String	SYSTEM					= "system";
	/**
	 * The action string <code>setSystemPower</code> (Value is
	 * "setSystemPower").
	 */
	public final static String	SET_SYSTEM_POWER		= "setSystemPower";
	/**
	 * The action string <code>setDevicePower</code> (Value is
	 * "setDevicePower").
	 */
	public final static String	SET_DEVICE_POWER		= "setDevicePower";

	private static final int	ACTION_SET_SYSTEM_POWER	= 0x1;
	private static final int	ACTION_SET_DEVICE_POWER	= 0x2;
	private final static int	ACTION_ALL				= ACTION_SET_SYSTEM_POWER
																| ACTION_SET_DEVICE_POWER;
	private final static int	ACTION_NONE				= 0;
	/**
	 * The actions mask.
	 */
	private transient int		action_mask				= ACTION_NONE;
	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private String				actions;

	/**
	 * Create a new PowerPermission with the given name (may be wildcard) and
	 * actions.
	 * 
	 * @param name identification of the system or the device. It can contain
	 *        "*"
	 * @param actions <code>setSystemPower</code> or
	 *        <code>setDevicePower</code>.
	 */
	public PowerPermission(String name, String actions) {
		this(name, getMask(actions));
	}

	/**
	 * Package private constructor used by PowerPermissionCollection.
	 * 
	 * @param name class name
	 * @param mask action mask
	 */
	PowerPermission(String name, int mask) {
		super(name);
		init(mask);
	}

	/**
	 * Called by constructors and when deserialized.
	 * 
	 * @param mask action mask
	 */
	private void init(int mask) {
		if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask)) {
			throw new IllegalArgumentException("invalid action string");
		}
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

			if (i >= 13 && (a[i - 13] == 's' || a[i - 13] == 'S')
					&& (a[i - 12] == 'e' || a[i - 12] == 'E')
					&& (a[i - 11] == 't' || a[i - 11] == 'T')
					&& (a[i - 10] == 's' || a[i - 10] == 'S')
					&& (a[i - 9] == 'y' || a[i - 9] == 'Y')
					&& (a[i - 8] == 's' || a[i - 8] == 'S')
					&& (a[i - 7] == 't' || a[i - 7] == 'T')
					&& (a[i - 6] == 'e' || a[i - 6] == 'E')
					&& (a[i - 5] == 'm' || a[i - 5] == 'M')
					&& (a[i - 4] == 'p' || a[i - 4] == 'P')
					&& (a[i - 3] == 'o' || a[i - 3] == 'O')
					&& (a[i - 2] == 'w' || a[i - 2] == 'W')
					&& (a[i - 1] == 'e' || a[i - 1] == 'E')
					&& (a[i] == 'r' || a[i] == 'R')) {
				matchlen = 14;
				mask |= ACTION_SET_SYSTEM_POWER;

			}
			else
				if (i >= 13 && (a[i - 13] == 's' || a[i - 13] == 'S')
						&& (a[i - 12] == 'e' || a[i - 12] == 'E')
						&& (a[i - 11] == 't' || a[i - 11] == 'T')
						&& (a[i - 10] == 'd' || a[i - 10] == 'D')
						&& (a[i - 9] == 'e' || a[i - 9] == 'E')
						&& (a[i - 8] == 'v' || a[i - 8] == 'V')
						&& (a[i - 7] == 'i' || a[i - 7] == 'I')
						&& (a[i - 6] == 'c' || a[i - 6] == 'C')
						&& (a[i - 5] == 'e' || a[i - 5] == 'E')
						&& (a[i - 4] == 'p' || a[i - 4] == 'P')
						&& (a[i - 3] == 'o' || a[i - 3] == 'O')
						&& (a[i - 2] == 'w' || a[i - 2] == 'W')
						&& (a[i - 1] == 'e' || a[i - 1] == 'E')
						&& (a[i] == 'r' || a[i] == 'R')) {
					matchlen = 14;
					mask |= ACTION_SET_DEVICE_POWER;

				}
				else
					if (i >= 0 && (a[i] == '*')) {
						matchlen = 1;
						mask |= ACTION_ALL;
					}
					else {
						// parse error
						throw new IllegalArgumentException(
								"invalid permission: " + actions);
					}

			// make sure we didn't just match the tail of a word
			// like "ackbarfregister". Also, skip to the comma.
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
	 * Determines if a <code>PowerPermission</code> object "implies" the
	 * specified permission.
	 * 
	 * @param p The target permission to check.
	 * @return <code>true</code> if the specified permission is implied by
	 *         this object; <code>false</code> otherwise.
	 */
	public boolean implies(Permission p) {
		if (p instanceof PowerPermission) {
			PowerPermission target = (PowerPermission) p;
			return (((action_mask & target.action_mask) == target.action_mask) && super
					.implies(p));
		}

		return (false);
	}

	/**
	 * Returns the canonical string representation of the actions. Always
	 * returns present actions in the following order:
	 * <code>setSystemPower</code>, <code>setDevicePower</code>.
	 * 
	 * @return The canonical string representation of the actions.
	 */
	public String getActions() {
		if (actions == null) {
			StringBuffer sb = new StringBuffer();
			boolean comma = false;
			if ((action_mask & ACTION_SET_SYSTEM_POWER) == ACTION_SET_SYSTEM_POWER) {
				sb.append(SET_SYSTEM_POWER);
				comma = true;
			}
			if ((action_mask & ACTION_SET_DEVICE_POWER) == ACTION_SET_DEVICE_POWER) {
				if (comma)
					sb.append(',');
				sb.append(SET_DEVICE_POWER);
			}
			actions = sb.toString();
		}
		return actions;
	}

	/**
	 * Returns a new <code>PermissionCollection</code> object for storing
	 * <code>PowerPermission</code> objects.
	 * 
	 * @return A new <code>PermissionCollection</code> object suitable for
	 *         storing <code>PowerPermission</code> objects.
	 */
	public PermissionCollection newPermissionCollection() {
		return (new PowerPermissionCollection());
	}

	/**
	 * Determines the equalty of two PowerPermission objects.
	 * 
	 * Checks that specified object has the same class name and action as this
	 * <code>PowerPermission</code>.
	 * 
	 * @param obj The object to test for equality.
	 * @return true if obj is a <code>PowerPermission</code>, and has the
	 *         same class name and actions as this <code>PowerPermission</code>
	 *         object; <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return (true);
		}

		if (!(obj instanceof PowerPermission)) {
			return (false);
		}

		PowerPermission p = (PowerPermission) obj;

		return ((action_mask == p.action_mask) && getName().equals(p.getName()));
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return Hash code value for this object.
	 */

	public int hashCode() {
		return (getName().hashCode() ^ getActions().hashCode());
	}

	/**
	 * Returns the current action mask. Used by the PowerPermissionCollection
	 * object.
	 * 
	 * @return The actions mask.
	 */
	int getMask() {
		return (action_mask);
	}

	/**
	 * Returns a string describing this <code>PowerPermission</code>. The
	 * convention is to specify the class name, the permission name, and the
	 * actions in the following format: '(org.osgi.service.power.PowerPermission
	 * &quot;name&quot; &quot;actions&quot;)'.
	 * 
	 * @return information about this <code>Permission</code> object.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		sb.append(getClass().getName());
		sb.append(" \"");
		sb.append(getName());
		sb.append("\" \"");
		sb.append(getActions());
		sb.append("\")");
		return sb.toString();
	}

	/**
	 * WriteObject is called to save the state of this permission to a stream.
	 * The actions are serialized, and the superclass takes care of the name.
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
		init(getMask(actions));
	}
}

/**
 * Stores a set of <code>PowerPermission</code> permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */

final class PowerPermissionCollection extends PermissionCollection {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long	serialVersionUID	= 8057161290964099751L;
	/**
	 * Table of permissions.
	 * 
	 * @serial
	 */
	private Hashtable			permissions;

	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 */
	private boolean				all_allowed;

	/**
	 * Create a new <code>PowerPermissionCollection</code> object.
	 * 
	 */
	public PowerPermissionCollection() {
		permissions = new Hashtable();
		all_allowed = false;
	}

	/**
	 * Adds a permission to the <code>PowerPermission</code> objects using the
	 * key for the hash as the name.
	 * 
	 * @param permission The Permission object to add.
	 * @exception IllegalArgumentException If the permission is not a
	 *            PowerPermission object.
	 * @exception SecurityException If this
	 *            <code>PowerPermissionCollection</code> object has been
	 *            marked read-only.
	 */
	public void add(Permission permission) {
		if (!(permission instanceof PowerPermission))
			throw new IllegalArgumentException("invalid permission: "
					+ permission);
		if (isReadOnly())
			throw new SecurityException("attempt to add a Permission to a "
					+ "readonly PermissionCollection");
		PowerPermission p = (PowerPermission) permission;
		String name = p.getName();
		PowerPermission existing = (PowerPermission) permissions.get(name);
		if (existing != null) {
			int oldMask = existing.getMask();
			int newMask = p.getMask();
			if (oldMask != newMask) {
				permissions.put(name, new PowerPermission(name, oldMask
						| newMask));
			}
		}
		else {
			permissions.put(name, permission);
		}
		if (!all_allowed) {
			if (name.equals("*"))
				all_allowed = true;
		}
	}

	/**
	 * Determines if a set of permissions implies the permissions expressed in
	 * <code>permission</code>.
	 * 
	 * @param permission The Permission object to compare.
	 * 
	 * @return <code>true</code> if
	 *         <code>permission</tt> is a proper subset of a
	 *         permission in the set; <code>false</tt> otherwise.
	 */
	public boolean implies(Permission permission) {
		if (!(permission instanceof PowerPermission))
			return false;
		PowerPermission p = (PowerPermission) permission;
		PowerPermission x;
		int desired = p.getMask();
		int effective = 0;
		// short circuit if the "*" Permission was added
		if (all_allowed) {
			x = (PowerPermission) permissions.get("*");
			if (x != null) {
				effective |= x.getMask();
				if ((effective & desired) == desired)
					return true;
			}
		}
		// strategy:
		// Check for full match first. Then work our way up the
		// name looking for matches on a.b.*
		String name = p.getName();
		x = (PowerPermission) permissions.get(name);
		if (x != null) {
			// we have a direct hit!
			effective |= x.getMask();
			if ((effective & desired) == desired)
				return true;
		}
		// work our way up the tree...
		int last, offset;
		offset = name.length() - 1;
		while ((last = name.lastIndexOf(".", offset)) != -1) {
			name = name.substring(0, last + 1) + "*";
			x = (PowerPermission) permissions.get(name);
			if (x != null) {
				effective |= x.getMask();
				if ((effective & desired) == desired)
					return (true);
			}
			offset = last - 1;
		}
		// we don't have to check for "*" as it was already checked
		// at the top (all_allowed), so we just return false
		return false;
	}

	/**
	 * Returns an enumeration of all <code>PowerPermission</tt> objects in the
	 * container.
	 * 
	 * @return Enumeration of all <code>PowerPermission</code> objects.
	 */
	public Enumeration elements() {
		return (permissions.elements());
	}
}
