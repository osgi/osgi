package org.osgi.impl.service.useradmin;

/*
 * $Header$
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
import java.util.*;
import org.osgi.service.useradmin.*;

/**
 * {@link org.osgi.service.useradmin.User}implementation.
 *  
 */
public class UserImpl extends RoleImpl implements User {
	/**
	 * The user credentials.
	 */
	protected UACredentials	credentials;

	/**
	 * Provide a constructor to be used by extending classes.
	 */
	protected UserImpl(UserAdminImpl ua, String name, int type) {
		super(ua, name, type);
		credentials = new UACredentials(this);
	}

	/**
	 * User-specific constructor.
	 */
	protected UserImpl(UserAdminImpl ua, String name) {
		this(ua, name, Role.USER);
	}

	/**
	 * Gets the credentials of this role. Implementation of
	 * {@link org.osgi.service.useradmin.User#getCredentials}.
	 */
	public Dictionary getCredentials() {
		return credentials;
	}

	/**
	 * Checks if this user has the specified credential. Implementation of
	 * {@link org.osgi.service.useradmin.User#hasCredential}.
	 */
	public boolean hasCredential(String key, Object value) {
		Object rvalue = credentials.get(key);
		if (rvalue == null)
			return false;
		if (value instanceof String) {
			return rvalue instanceof String && value.equals(rvalue);
		}
		else
			if (value instanceof byte[]) {
				return rvalue instanceof byte[]
						&& Arrays.equals((byte[]) value, (byte[]) rvalue);
			}
			else
				throw new IllegalArgumentException(
						"value must be of type String or byte[]");
	}
}
