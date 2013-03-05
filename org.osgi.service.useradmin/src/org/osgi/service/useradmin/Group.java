/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package org.osgi.service.useradmin;

/**
 * A named grouping of roles ({@code Role} objects).
 * <p>
 * Whether or not a given {@code Authorization} context implies a {@code Group}
 * object depends on the members of that {@code Group} object.
 * <p>
 * A {@code Group} object can have two kinds of members: <i>basic </i> and
 * <i>required </i>. A {@code Group} object is implied by an
 * {@code Authorization} context if all of its required members are implied and
 * at least one of its basic members is implied.
 * <p>
 * A {@code Group} object must contain at least one basic member in order to be
 * implied. In other words, a {@code Group} object without any basic member
 * roles is never implied by any {@code Authorization} context.
 * <p>
 * A {@code User} object always implies itself.
 * <p>
 * No loop detection is performed when adding members to {@code Group} objects,
 * which means that it is possible to create circular implications. Loop
 * detection is instead done when roles are checked. The semantics is that if a
 * role depends on itself (i.e., there is an implication loop), the role is not
 * implied.
 * <p>
 * The rule that a {@code Group} object must have at least one basic member to
 * be implied is motivated by the following example:
 * 
 * <pre>
 * 
 *  group foo
 *    required members: marketing
 *    basic members: alice, bob
 *  
 * </pre>
 * 
 * Privileged operations that require membership in "foo" can be performed only
 * by "alice" and "bob", who are in marketing.
 * <p>
 * If "alice" and "bob" ever transfer to a different department, anybody in
 * marketing will be able to assume the "foo" role, which certainly must be
 * prevented. Requiring that "foo" (or any {@code Group} object for that matter)
 * must have at least one basic member accomplishes that.
 * <p>
 * However, this would make it impossible for a {@code Group} object to be
 * implied by just its required members. An example where this implication might
 * be useful is the following declaration: "Any citizen who is an adult is
 * allowed to vote." An intuitive configuration of "voter" would be:
 * 
 * <pre>
 * 
 *  group voter
 *    required members: citizen, adult
 *       basic members:
 *  
 * </pre>
 * 
 * However, according to the above rule, the "voter" role could never be assumed
 * by anybody, since it lacks any basic members. In order to address this issue
 * a predefined role named "user.anyone" can be specified, which is always
 * implied. The desired implication of the "voter" group can then be achieved by
 * specifying "user.anyone" as its basic member, as follows:
 * 
 * <pre>
 * 
 *  group voter
 *    required members: citizen, adult
 *       basic members: user.anyone
 *  
 * </pre>
 * 
 * @noimplement
 * @author $Id$
 */
public interface Group extends User {
	/**
	 * Adds the specified {@code Role} object as a basic member to this
	 * {@code Group} object.
	 * 
	 * @param role The role to add as a basic member.
	 * 
	 * @return {@code true} if the given role could be added as a basic member,
	 *         and {@code false} if this {@code Group} object already contains a
	 *         {@code Role} object whose name matches that of the specified
	 *         role.
	 * 
	 * @throws SecurityException If a security manager exists and the caller
	 *         does not have the {@code UserAdminPermission} with name
	 *         {@code admin}.
	 */
	public boolean addMember(Role role);

	/**
	 * Adds the specified {@code Role} object as a required member to this
	 * {@code Group} object.
	 * 
	 * @param role The {@code Role} object to add as a required member.
	 * 
	 * @return {@code true} if the given {@code Role} object could be added as a
	 *         required member, and {@code false} if this {@code Group} object
	 *         already contains a {@code Role} object whose name matches that of
	 *         the specified role.
	 * 
	 * @throws SecurityException If a security manager exists and the caller
	 *         does not have the {@code UserAdminPermission} with name
	 *         {@code admin}.
	 */
	public boolean addRequiredMember(Role role);

	/**
	 * Removes the specified {@code Role} object from this {@code Group} object.
	 * 
	 * @param role The {@code Role} object to remove from this {@code Group}
	 *        object.
	 * 
	 * @return {@code true} if the {@code Role} object could be removed,
	 *         otherwise {@code false}.
	 * 
	 * @throws SecurityException If a security manager exists and the caller
	 *         does not have the {@code UserAdminPermission} with name
	 *         {@code admin}.
	 */
	public boolean removeMember(Role role);

	/**
	 * Gets the basic members of this {@code Group} object.
	 * 
	 * @return The basic members of this {@code Group} object, or {@code null}
	 *         if this {@code Group} object does not contain any basic members.
	 */
	public Role[] getMembers();

	/**
	 * Gets the required members of this {@code Group} object.
	 * 
	 * @return The required members of this {@code Group} object, or
	 *         {@code null} if this {@code Group} object does not contain any
	 *         required members.
	 */
	public Role[] getRequiredMembers();
}
