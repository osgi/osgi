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

/**
 * A named grouping of roles (<tt>Role</tt> objects).
 * <p>
 * Whether or not a given <tt>Authorization</tt> context implies a <tt>Group</tt> object
 * depends on the members of that <tt>Group</tt> object.
 * <p>
 * A <tt>Group</tt> object can have two kinds of members: <i>basic</i> and
 * <i>required</i>.
 * A <tt>Group</tt> object is implied by an <tt>Authorization</tt> context if all of
 * its required members are implied
 * and at least one of its basic members is implied.
 * <p>
 * A <tt>Group</tt> object must contain at least one basic member in order
 * to be implied. In other words, a <tt>Group</tt> object without any basic member
 * roles is never implied by any <tt>Authorization</tt> context.
 * <p>
 * A <tt>User</tt> object always implies itself.
 * <p>
 * No loop detection is performed when adding members to <tt>Group</tt> objects, which
 * means that it is possible to create circular implications. Loop
 * detection is instead done when roles are checked. The semantics is that
 * if a role depends on itself (i.e., there is an implication loop), the
 * role is not implied.
 * <p>
 * The rule that a <tt>Group</tt> object must have at least one basic member to be implied
 * is motivated by the following example:
 * <pre>
 * group foo
 *   required members: marketing
 *   basic members: alice, bob
 * </pre>
 * Privileged operations that require membership in "foo" can be performed
 * only by "alice" and "bob", who are in marketing.
 * <p>
 * If "alice" and "bob" ever transfer to a different department, anybody in
 * marketing will be able to assume the "foo" role, which certainly must be
 * prevented.
 * Requiring that "foo" (or any <tt>Group</tt> object for that matter) must have at least
 * one basic member accomplishes that.
 * <p>
 * However, this would make it impossible for a <tt>Group</tt> object to be implied by just
 * its required members. An example where this implication might be useful
 * is the following declaration: "Any citizen who is an adult is allowed to
 * vote."
 * An intuitive configuration of "voter" would be:
 *
 * <pre>
 * group voter
 *   required members: citizen, adult
 *      basic members:
 * </pre>
 *
 * However, according to the above rule, the "voter" role could never be
 * assumed by anybody, since it lacks any basic members.
 * In order to address this issue a predefined role named
 * "user.anyone" can be specified, which is always implied.
 * The desired implication of the "voter" group can then be achieved by
 * specifying "user.anyone" as its basic member, as follows:
 *
 * <pre>
 * group voter
 *   required members: citizen, adult
 *      basic members: user.anyone
 * </pre>
 *
 * @version $Revision$
 */

public interface Group extends User {

  /**
   * Adds the specified <tt>Role</tt> object as a basic member to this <tt>Group</tt> object.
   *
   * @param role The role to add as a basic member.
   *
   * @return <tt>true</tt> if the given role could be added as a basic
   * member,
   * and <tt>false</tt> if this <tt>Group</tt> object already contains a <tt>Role</tt> object whose name
   * matches that of the specified role.
   *
   * @throws SecurityException If a security manager exists and the caller
   * does not have the <tt>UserAdminPermission</tt> with name <tt>admin</tt>.
   */
  public boolean addMember(Role role);

  /**
   * Adds the specified <tt>Role</tt> object as a required member to this <tt>Group</tt> object.
   *
   * @param role The <tt>Role</tt> object to add as a required member.
   *
   * @return <tt>true</tt> if the given <tt>Role</tt> object could be added as a required
   * member, and <tt>false</tt> if this <tt>Group</tt> object already contains a <tt>Role</tt> object
   * whose name matches that of the specified role.
   *
   * @throws SecurityException If a security manager exists and the caller
   * does not have the <tt>UserAdminPermission</tt> with name <tt>admin</tt>.
   */
  public boolean addRequiredMember(Role role);

  /**
   * Removes the specified <tt>Role</tt> object from this <tt>Group</tt> object.
   *
   * @param role The <tt>Role</tt> object to remove from this <tt>Group</tt> object.
   *
   * @return <tt>true</tt> if the <tt>Role</tt> object could be removed,
   * otherwise <tt>false</tt>.
   *
   * @throws SecurityException If a security manager exists and the caller
   * does not have the <tt>UserAdminPermission</tt> with name <tt>admin</tt>.
   */
  public boolean removeMember(Role role);

  /**
   * Gets the basic members of this <tt>Group</tt> object.
   *
   * @return The basic members of this <tt>Group</tt> object, or <tt>null</tt> if this
   * <tt>Group</tt> object does not contain any basic members.
   */
  public Role[] getMembers();

  /**
   * Gets the required members of this <tt>Group</tt> object.
   *
   * @return The required members of this <tt>Group</tt> object, or <tt>null</tt> if this
   * <tt>Group</tt> object does not contain any required members.
   */
  public Role[] getRequiredMembers();
}
