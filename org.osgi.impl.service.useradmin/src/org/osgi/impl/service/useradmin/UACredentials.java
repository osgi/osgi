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
import org.osgi.service.useradmin.*;

/**
 * A dictionary with permissions checks for getting, setting, changing, and
 * deleting values.
 * <p>
 * User credentials are instances of this class. Changes to these objects are
 * propagated to the {@link UserAdmin}service and made persistent. Only keys of
 * the type <code>String</code> are supported, and only values of type
 * <code>String</code> or of type <code>byte[]</code> are supported. If an
 * attempt is made to store a value of a type other than what is supported, an
 * exception of type <code>IllegalArgumentException</code> will be raised.
 * <p>
 * A {@link UserAdminPermission}with the action <code>getCredential</code> is
 * required to getting dictionary values, and the action
 * <code>changeCredential</code> to set, change, or remove dictionary values.
 * The permission key should be the (or a prefix of) the credential key that is
 * looked up, created, changed, or deleted.
 */
public class UACredentials extends UAProperties {
	public UACredentials(RoleImpl role) {
		super(role);
	}

	/**
	 * The permission need to modify the credentials.
	 */
	protected String getChangeAction() {
		return UserAdminPermission.CHANGE_CREDENTIAL;
	}

	public Object get(Object key) {
		synchronized (role.ua.activator) {
			if (key instanceof String) {
				// Check that the caller are allowed to get the credential.
				role.ua.checkPermission(new UserAdminPermission((String) key,
						UserAdminPermission.GET_CREDENTIAL));
				return super.get(key);
			}
			else
				throw new IllegalArgumentException(
						"The key must be a String, got " + key.getClass());
		}
	}

	public String toString() {
		return "#Credentials#";
	}
}
