/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2001).
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

import org.osgi.framework.*;

/**
 * This interface is used to manage a database of named <tt>Role</tt> objects, which can
 * be used for authentication and authorization purposes.
 *
 * <p>This version of the User Admin service defines two types of <tt>Role</tt> objects: "User" and
 * "Group". Each type of role is represented by an <tt>int</tt> constant and an
 * interface. The range of positive integers is reserved for new types of
 * roles that may be added in the future. When defining proprietary role
 * types, negative constant values must be used.
 *
 * <p>Every role has a name and a type.
 *
 * <p>A {@link User}object can be configured with credentials (e.g., a password)
 * and properties (e.g., a street address, phone number, etc.).
 * <p>
 * A {@link Group}object represents an aggregation of {@link User} and
 * {@link Group}objects. In other words, the members of a <tt>Group</tt> object are roles themselves.
 * <p>
 * Every User Admin service manages and maintains its own
 * namespace of <tt>Role</tt> objects, in which each <tt>Role</tt> object has a unique name.
 *
 * @version $Revision$
 */

public interface UserAdmin {

  /**
   * Creates a <tt>Role</tt> object with the given name and of the given type.
   *
   * <p>If a <tt>Role</tt> object was created, a <tt>UserAdminEvent</tt> object of type
   * {@link UserAdminEvent#ROLE_CREATED}is broadcast to any
   * <tt>UserAdminListener</tt> object.
   *
   * @param name The <tt>name</tt> of the <tt>Role</tt> object to create.
   * @param type The type of the <tt>Role</tt> object to create. Must be either
   * a {@link Role#USER}type or {@link Role#GROUP}type.
   *
   * @return The newly created <tt>Role</tt> object, or <tt>null</tt> if a role with
   * the given name already exists.
   *
   * @throws IllegalArgumentException if <tt>type</tt> is invalid.
   *
   * @throws SecurityException If a security manager exists and the caller
   * does not have the <tt>UserAdminPermission</tt> with name <tt>admin</tt>.
   */
  public Role createRole(String name, int type);

  /**
   * Removes the <tt>Role</tt> object with the given name from this User Admin service.
   *
   * <p> If the <tt>Role</tt> object was removed, a <tt>UserAdminEvent</tt> object of type
   * {@link UserAdminEvent#ROLE_REMOVED}is broadcast to any
   * <tt>UserAdminListener</tt> object.
   *
   * @param name The name of the <tt>Role</tt> object to remove.
   *
   * @return <tt>true</tt> If a <tt>Role</tt> object with the given name is present in this
   * User Admin service and could be removed, otherwise <tt>false</tt>.
   *
   * @throws SecurityException If a security manager exists and the caller
   * does not have the <tt>UserAdminPermission</tt> with name <tt>admin</tt>.
   */
  public boolean removeRole(String name);

  /**
   * Gets the <tt>Role</tt> object with the given <tt>name</tt> from this User Admin service.
   *
   * @param name The name of the <tt>Role</tt> object to get.
   *
   * @return The requested <tt>Role</tt> object, or <tt>null</tt> if this User Admin service does
   * not have a <tt>Role</tt> object with the given <tt>name</tt>.
   */
  public Role getRole(String name);

  /**
   * Gets the <tt>Role</tt> objects managed by this User Admin service that have properties matching
   * the specified LDAP filter criteria. See
   * <tt>org.osgi.framework.Filter</tt> for a
   * description of the filter syntax. If a <tt>null</tt> filter is
   * specified, all Role objects managed by this User Admin service are returned.
   *
   * @param filter The filter criteria to match.
   *
   * @return The <tt>Role</tt> objects managed by this User Admin service whose properties
   * match the specified filter criteria, or all <tt>Role</tt> objects if a
   * <tt>null</tt> filter is specified.  If no roles match the
   * filter, <tt>null</tt> will be returned.
   *
   */
  public Role[] getRoles(String filter) throws InvalidSyntaxException;

  /**
   * Gets the user with the given property <tt>key</tt>-<tt>value</tt> pair from the User Admin
   * service database. This is a convenience method for retrieving a <tt>User</tt> object based on
   * a property for which every <tt>User</tt> object is supposed to have a unique value
   * (within the scope of this User Admin service), such as for example a
   * X.500 distinguished name.
   *
   * @param key The property key to look for.
   * @param value The property value to compare with.
   *
   * @return A matching user, if <em>exactly</em> one is found. If zero or
   * more than one matching users are found, <tt>null</tt> is returned.
   */
  public User getUser(String key, String value);

  /**
   * Creates an <tt>Authorization</tt> object that encapsulates the specified <tt>User</tt> object
   * and the <tt>Role</tt> objects it possesses. The <tt>null</tt> user is interpreted
   * as the anonymous user.  The anonymous user represents a user that has
   * not been authenticated.  An <tt>Authorization</tt> object for an anonymous user
   * will be unnamed, and will only imply groups that user.anyone
   * implies.
   *
   * @param user The <tt>User</tt> object to create an <tt>Authorization</tt> object for, or
   * <tt>null</tt> for the anonymous user.
   *
   * @return the <tt>Authorization</tt> object for the specified <tt>User</tt> object.
   */
  public Authorization getAuthorization(User user);
}
