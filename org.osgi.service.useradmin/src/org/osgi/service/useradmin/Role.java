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
 * The base interface for <code>Role</code> objects managed by the User Admin
 * service.
 * 
 * <p>
 * This interface exposes the characteristics shared by all <code>Role</code>
 * classes: a name, a type, and a set of properties.
 * <p>
 * Properties represent public information about the <code>Role</code> object that
 * can be read by anyone. Specific {@link UserAdminPermission}objects are
 * required to change a <code>Role</code> object's properties.
 * <p>
 * <code>Role</code> object properties are <code>Dictionary</code> objects. Changes
 * to these objects are propagated to the User Admin service and made
 * persistent.
 * <p>
 * Every User Admin service contains a set of predefined <code>Role</code> objects
 * that are always present and cannot be removed. All predefined <code>Role</code>
 * objects are of type <code>ROLE</code>. This version of the
 * <code>org.osgi.service.useradmin</code> package defines a single predefined
 * role named &quot;user.anyone&quot;, which is inherited by any other role.
 * Other predefined roles may be added in the future. Since
 * &quot;user.anyone&quot; is a <code>Role</code> object that has properties
 * associated with it that can be read and modified. Access to these properties
 * and their use is application specific and is controlled using
 * <code>UserAdminPermission</code> in the same way that properties for other
 * <code>Role</code> objects are.
 * 
 * @version $Revision$
 */
public interface Role {
	/**
	 * The name of the predefined role, user.anyone, that all users and groups
	 * belong to.
	 */
	public static final String	USER_ANYONE	= "user.anyone";
	/**
	 * The type of a predefined role.
	 * 
	 * <p>
	 * The value of <code>ROLE</code> is 0.
	 */
	public static final int		ROLE		= 0;
	/**
	 * The type of a {@link User}role.
	 * 
	 * <p>
	 * The value of <code>USER</code> is 1.
	 */
	public static final int		USER		= 1;
	/**
	 * The type of a {@link Group}role.
	 * 
	 * <p>
	 * The value of <code>GROUP</code> is 2.
	 */
	public static final int		GROUP		= 2;

	/**
	 * Returns the name of this role.
	 * 
	 * @return The role's name.
	 */
	public String getName();

	/**
	 * Returns the type of this role.
	 * 
	 * @return The role's type.
	 */
	public int getType();

	/**
	 * Returns a <code>Dictionary</code> of the (public) properties of this
	 * <code>Role</code> object. Any changes to the returned <code>Dictionary</code>
	 * will change the properties of this <code>Role</code> object. This will
	 * cause a <code>UserAdminEvent</code> object of type
	 * {@link UserAdminEvent#ROLE_CHANGED}to be broadcast to any
	 * <code>UserAdminListener</code> objects.
	 * 
	 * <p>
	 * Only objects of type <code>String</code> may be used as property keys, and
	 * only objects of type <code>String</code> or <code>byte[]</code> may be used
	 * as property values. Any other types will cause an exception of type
	 * <code>IllegalArgumentException</code> to be raised.
	 * 
	 * <p>
	 * In order to add, change, or remove a property in the returned
	 * <code>Dictionary</code>, a {@link UserAdminPermission}named after the
	 * property name (or a prefix of it) with action <code>changeProperty</code>
	 * is required.
	 * 
	 * @return <code>Dictionary</code> containing the properties of this
	 *         <code>Role</code> object.
	 */
	public Dictionary getProperties();
}
