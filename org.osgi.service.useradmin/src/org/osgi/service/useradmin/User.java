/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
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
 * A <tt>User</tt> role managed by a User Admin service.
 *
 * <p>In this context, the term &quot;user&quot; is not limited to just
 * human beings.
 * Instead, it refers to any entity that may have any number of
 * credentials associated with it that it may use to authenticate itself.
 * <p>
 * In general, <tt>User</tt>objects are associated with a specific User Admin
 * service (namely the one that created them), and cannot be used with other
 * User Admin services.
 * <p>
 * A <tt>User</tt>object may have credentials (and properties, inherited from the {@link Role}class)
 * associated with it. Specific {@link UserAdminPermission}objects are required to
 * read or change a <tt>User</tt> object's credentials.
 * <p>
 * Credentials are <tt>Dictionary</tt> objects and have semantics that are similar
 * to the properties in the <tt>Role</tt> class.
 * @version $Revision$
 */

public interface User extends Role {
  /**
   * Returns a <tt>Dictionary</tt> of the credentials of this <tt>User</tt> object. Any changes
   * to the returned <tt>Dictionary</tt> object will change the credentials of this <tt>User</tt> object.
   * This will cause a <tt>UserAdminEvent</tt> object of type
   * {@link UserAdminEvent#ROLE_CHANGED}to be broadcast to any
   * <tt>UserAdminListeners</tt> objects.
   *
   * <p>Only objects of type <tt>String</tt> may be used as credential keys, and only
   * objects of type <tt>String</tt> or of type <tt>byte[]</tt>
   * may be used as credential values. Any other types will cause an exception
   * of type <tt>IllegalArgumentException</tt> to be raised.
   *
   * <p>In order to retrieve a credential from the returned <tt>Dictionary</tt> object,
   * a {@link UserAdminPermission}named after the credential name (or
   * a prefix of it) with action <tt>getCredential</tt> is required.
   * <p>
   * In order to add or remove a credential from the returned <tt>Dictionary</tt> object,
   * a {@link UserAdminPermission}named after the credential name (or
   * a prefix of it) with action <code>changeCredential</code> is required.
   *
   * @return <tt>Dictionary</tt> object containing the credentials of this <tt>User</tt> object.
   */
  public Dictionary getCredentials();

  /**
   * Checks to see if this <tt>User</tt> object has a credential with the specified <tt>key</tt>
   * set to the specified <tt>value</tt>.
   *
   * <p>If the specified credential <tt>value</tt> is not of type <tt>String</tt> or
   * <tt>byte[]</tt>, it is ignored, that is, <tt>false</tt> is returned
   * (as opposed to an <tt>IllegalArgumentException</tt> being raised).
   *
   * @param key The credential <tt>key</tt>.
   * @param value The credential <tt>value</tt>.
   *
   * @return <tt>true</tt> if this user has the specified credential;
   * <tt>false</tt> otherwise.
   *
   * @throws SecurityException If a security manager exists and the caller
   * does not have the <tt>UserAdminPermission</tt> named after the credential
   * key (or a prefix of it) with action <tt>getCredential</tt>.
   */
  public boolean hasCredential(String key, Object value);
}
