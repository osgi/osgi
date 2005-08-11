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

import java.io.IOException;
import java.security.*;
import java.util.*;

/**
 * Indicates a bundle's authority to configure bundles.
 * 
 * This permission has only a single action: CONFIGURE.
 * 
 * 
 * @version $Revision$
 * @since 1.2
 */

public final class ConfigurationPermission extends Permission {
	static final long			serialVersionUID	= 5716868734811965383L;
	/**
	 * The action string <code>configure</code>.
	 */
	public final static String	CONFIGURE			= "configure";

	/**
	 * The mask for a configuration action.
	 */
	public static int	ACTION_CONFIGURE	= 0x00000001;
	private final static int	ACTION_ALL			= ACTION_CONFIGURE;
	private final static int	ACTION_NONE			= 0;

	private transient int		action_mask			= ACTION_NONE;
	private String				actions				= null;

	/**
	 * Create a new ConfigurationPermission.
	 * 
	 * @param name This parameter is not used
	 * @param actions <code>configure</code> (canonical order)
	 */

	public ConfigurationPermission(String name, String actions) {
		super(name);
		init(getMask(actions));
	}

	/**
	 * Create a new ConfigurationPermission used to do the check.
	 * 
	 * @param actions
	 */
	public ConfigurationPermission(int actions) {
		super("");
		init(actions);
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
		ConfigurationPermission target = (ConfigurationPermission) p;
		return ((action_mask & target.action_mask) == target.action_mask);
	}

	/**
	 * Returns the canonical string representation of the
	 * <code>ConfigurationPermission</code> actions.
	 * 
	 * <p>
	 * Always returns present <code>ConfigurationPermission</code> actions in
	 * the following order: <code>CONFIGURE</code>
	 * 
	 * @return Canonical string representation of the
	 *         <code>ConfigurationPermission</code> actions.
	 */
	public String getActions() {
		if (actions == null) {
			if ((action_mask & ACTION_CONFIGURE) == ACTION_CONFIGURE) {
				return CONFIGURE;
			}
		}
		return "";
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
		return ((action_mask == p.action_mask));
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
		if ( actions.trim().equalsIgnoreCase(CONFIGURE))
			return ACTION_CONFIGURE;		
		else 
			throw new IllegalArgumentException("invalid permission: " + actions);
	}
	
	/**
	 * Returns the current action mask.
	 * <p>
	 * Used by the PermissionCollection class.
	 * 
	 * @return Current action mask.
	 */
	int getMask() {
		return action_mask;
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
 * Stores a set of <code>ConfigurationPermission</code> permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */
final class ConfigurationPermissionCollection extends PermissionCollection {
	static final long	serialVersionUID	= -6917638867081695839L;
	Vector				elements			= new Vector();

	/**
	 * Creates an empty ConfigurationPermissions object.
	 * 
	 */
	public ConfigurationPermissionCollection() {
	}

	/**
	 * Adds a permission to the <code>ConfigurationPermission</code> objects
	 * using the key for the hash as the name.
	 * 
	 * @param permission The Permission object to add.
	 * 
	 * @throws IllegalArgumentException If the permission is not a
	 *         ConfigurationPermission object.
	 * 
	 * @throws SecurityException If this
	 *         <code>ConfigurationPermissionCollection</code> object has been
	 *         marked read-only.
	 */

	public void add(Permission permission) {
		elements.add(permission);
	}

	/**
	 * Determines if a set of permissions implies the permissions expressed in
	 * <code>permission</code>.
	 * 
	 * @param permission The Permission object to compare.
	 * 
	 * @return <code>true</code> if <code>permission</code> is a proper
	 *         subset of a permission in the set; <code>false</code>
	 *         otherwise.
	 */

	public boolean implies(Permission permission) {
		return !elements.isEmpty();
	}

	public Enumeration elements() {
		return elements.elements();
	}

}
