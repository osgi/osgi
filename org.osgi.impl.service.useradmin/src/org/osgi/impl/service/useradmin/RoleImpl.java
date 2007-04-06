package org.osgi.impl.service.useradmin;

/*
 * $Id$
 *
 * OSGi UserAdmin Reference Implementation.
 *
 * OSGi Confidential.
 *
 * (c) Copyright Gatespace AB 2000-2001.
 *
 * This source code is owned by Gatespace AB (www.gatespace.com), and is 
 * being distributed to OSGi MEMBERS as MEMBER LICENSED MATERIALS under
 * the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 *
 */
import java.util.Dictionary;
import org.osgi.service.useradmin.Role;

/**
 * {@link org.osgi.service.useradmin.Role}implementation.
 *  
 */
public class RoleImpl implements Role {
	/**
	 * The role properties.
	 */
	protected UAProperties	properties;
	/**
	 * Link to the UserAdmin controlling this role.
	 */
	protected UserAdminImpl	ua;
	/**
	 * The name of this role.
	 */
	protected String		name;
	/**
	 * The type of this group.
	 */
	protected int			type;

	protected RoleImpl(UserAdminImpl ua, String name, int type) {
		this.ua = ua;
		this.name = name;
		this.type = type;
		properties = new UAProperties(this);
	}

	/**
	 * Gets the name of this role. Implementation of
	 * {@link org.osgi.service.useradmin.Role#getName}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the type of this role. Implementation of
	 * {@link org.osgi.service.useradmin.Role#getType}.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the properties of this role. Implementation of
	 * {@link org.osgi.service.useradmin.Role#getProperties}.
	 */
	public Dictionary getProperties() {
		return properties;
	}

	/* -------------- Protected methods ----------------- */
	/**
	 * Checks if this role is implied by the specified authorization context.
	 */
	protected boolean impliedBy(AuthorizationImpl auth) {
		// Have we already checked this role?
		Boolean b = auth.cachedHaveRole(this);
		// b is now true (we are implied), false (we are not implied),
		// or null (we don't know yet).
		if (b != null)
			return b.booleanValue();
		// Default to say that the role is implied if the name of the
		// specified role is the same as the name of this role, or if
		// this role is "user.anyone".
		String rolename = auth.getName();
		boolean implied = (rolename != null && rolename.equals(name))
				|| name.equals(UserAdminImpl.ANYONE);
		// Cache and return the result.
		return auth.cacheHaveRole(this, implied);
	}

	/**
	 * Removes references to the specified role.
	 */
	protected void removeReferenceTo(RoleImpl role) {
	}
}
