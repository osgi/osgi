/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.useradmin;

import java.util.Dictionary;

/**
 * A <code>User</code> role managed by a User Admin service.
 * 
 * <p>
 * In this context, the term &quot;user&quot; is not limited to just human
 * beings. Instead, it refers to any entity that may have any number of
 * credentials associated with it that it may use to authenticate itself.
 * <p>
 * In general, <code>User</code> objects are associated with a specific User Admin
 * service (namely the one that created them), and cannot be used with other
 * User Admin services.
 * <p>
 * A <code>User</code> object may have credentials (and properties, inherited from
 * the {@link Role}class) associated with it. Specific
 * {@link UserAdminPermission}objects are required to read or change a
 * <code>User</code> object's credentials.
 * <p>
 * Credentials are <code>Dictionary</code> objects and have semantics that are
 * similar to the properties in the <code>Role</code> class.
 * 
 * @version $Revision$
 */
public interface User extends Role {
	/**
	 * Returns a <code>Dictionary</code> of the credentials of this <code>User</code>
	 * object. Any changes to the returned <code>Dictionary</code> object will
	 * change the credentials of this <code>User</code> object. This will cause a
	 * <code>UserAdminEvent</code> object of type
	 * {@link UserAdminEvent#ROLE_CHANGED}to be broadcast to any
	 * <code>UserAdminListeners</code> objects.
	 * 
	 * <p>
	 * Only objects of type <code>String</code> may be used as credential keys,
	 * and only objects of type <code>String</code> or of type <code>byte[]</code>
	 * may be used as credential values. Any other types will cause an exception
	 * of type <code>IllegalArgumentException</code> to be raised.
	 * 
	 * <p>
	 * In order to retrieve a credential from the returned <code>Dictionary</code>
	 * object, a {@link UserAdminPermission}named after the credential name (or
	 * a prefix of it) with action <code>getCredential</code> is required.
	 * <p>
	 * In order to add or remove a credential from the returned
	 * <code>Dictionary</code> object, a {@link UserAdminPermission}named after
	 * the credential name (or a prefix of it) with action
	 * <code>changeCredential</code> is required.
	 * 
	 * @return <code>Dictionary</code> object containing the credentials of this
	 *         <code>User</code> object.
	 */
	public Dictionary getCredentials();

	/**
	 * Checks to see if this <code>User</code> object has a credential with the
	 * specified <code>key</code> set to the specified <code>value</code>.
	 * 
	 * <p>
	 * If the specified credential <code>value</code> is not of type
	 * <code>String</code> or <code>byte[]</code>, it is ignored, that is,
	 * <code>false</code> is returned (as opposed to an
	 * <code>IllegalArgumentException</code> being raised).
	 * 
	 * @param key The credential <code>key</code>.
	 * @param value The credential <code>value</code>.
	 * 
	 * @return <code>true</code> if this user has the specified credential;
	 *         <code>false</code> otherwise.
	 * 
	 * @throws SecurityException If a security manager exists and the caller
	 *         does not have the <code>UserAdminPermission</code> named after the
	 *         credential key (or a prefix of it) with action
	 *         <code>getCredential</code>.
	 */
	public boolean hasCredential(String key, Object value);
}
