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
 * The <code>Authorization</code> interface encapsulates an authorization context
 * on which bundles can base authorization decisions, where appropriate.
 * <p>
 * Bundles associate the privilege to access restricted resources or operations
 * with roles. Before granting access to a restricted resource or operation, a
 * bundle will check if the <code>Authorization</code> object passed to it possess
 * the required role, by calling its <code>hasRole</code> method.
 * <p>
 * Authorization contexts are instantiated by calling the
 * {@link UserAdmin#getAuthorization}method.
 * 
 * <p>
 * <i>Trusting Authorization objects </i>
 * <p>
 * There are no restrictions regarding the creation of <code>Authorization</code>
 * objects. Hence, a service must only accept <code>Authorization</code> objects
 * from bundles that has been authorized to use the service using code based (or
 * Java 2) permissions.
 * 
 * <p>
 * In some cases it is useful to use <code>ServicePermission</code> to do the code
 * based access control. A service basing user access control on
 * <code>Authorization</code> objects passed to it, will then require that a
 * calling bundle has the <code>ServicePermission</code> to get the service in
 * question. This is the most convenient way. The OSGi environment will do the
 * code based permission check when the calling bundle attempts to get the
 * service from the service registry.
 * <p>
 * Example: A servlet using a service on a user's behalf. The bundle with the
 * servlet must be given the <code>ServicePermission</code> to get the Http
 * Service.
 * <p>
 * However, in some cases the code based permission checks need to be more
 * fine-grained. A service might allow all bundles to get it, but require
 * certain code based permissions for some of its methods.
 * <p>
 * Example: A servlet using a service on a user's behalf, where some service
 * functionality is open to anyone, and some is restricted by code based
 * permissions. When a restricted method is called (e.g., one handing over an
 * <code>Authorization</code> object), the service explicitly checks that the
 * calling bundle has permission to make the call.
 * 
 * @version $Revision$
 */
public interface Authorization {
	/**
	 * Gets the name of the {@link User}that this <code>Authorization</code>
	 * context was created for.
	 * 
	 * @return The name of the {@link User}object that this
	 *         <code>Authorization</code> context was created for, or
	 *         <code>null</code> if no user was specified when this
	 *         <code>Authorization</code> context was created.
	 */
	public String getName();

	/**
	 * Checks if the role with the specified name is implied by this
	 * <code>Authorization</code> context.
	 * <p>
	 * 
	 * Bundles must define globally unique role names that are associated with
	 * the privilege of accessing restricted resources or operations. Operators
	 * will grant users access to these resources, by creating a {@link Group}
	 * object for each role and adding {@link User}objects to it.
	 * 
	 * @param name The name of the role to check for.
	 * 
	 * @return <code>true</code> if this <code>Authorization</code> context implies
	 *         the specified role, otherwise <code>false</code>.
	 */
	public boolean hasRole(String name);

	/**
	 * Gets the names of all roles encapsulated by this <code>Authorization</code>
	 * context.
	 * 
	 * @return The names of all roles encapsulated by this
	 *         <code>Authorization</code> context, or <code>null</code> if no roles
	 *         are in the context. The predefined role <code>user.anyone</code>
	 *         will not be included in this list.
	 */
	public String[] getRoles();
}
