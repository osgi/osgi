/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2002).
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
package org.osgi.service.permissionadmin;

/**
 * The Permission Admin service allows management agents to manage the
 * permissions of bundles. There is at most one Permission Admin service present
 * in the OSGi environment.
 * <p>
 * Access to the Permission Admin service is protected by corresponding
 * <tt>ServicePermission</tt>. In addition <tt>AdminPermission</tt> is
 * required to actually set permissions.
 * 
 * <p>
 * Bundle permissions are managed using a permission table. A bundle's location
 * serves as the key into this permission table. The value of a table entry is
 * the set of permissions (of type <tt>PermissionInfo</tt>) granted to the
 * bundle named by the given location. A bundle may have an entry in the
 * permission table prior to being installed in the Framework.
 * 
 * <p>
 * The permissions specified in <tt>setDefaultPermissions</tt> are used as the
 * default permissions which are granted to all bundles that do not have an
 * entry in the permission table.
 * 
 * <p>
 * Any changes to a bundle's permissions in the permission table will take
 * effect no later than when bundle's <tt>java.security.ProtectionDomain</tt>
 * is next involved in a permission check, and will be made persistent.
 * 
 * <p>
 * Only permission classes on the system classpath or from an exported package
 * are considered during a permission check. Additionally, only permission
 * classes that are subclasses of <tt>java.security.Permission</tt> and define
 * a 2-argument constructor that takes a <i>name </i> string and an <i>actions
 * </i> string can be used.
 * <p>
 * Permissions implicitly granted by the Framework (for example, a bundle's
 * permission to access its persistent storage area) cannot be changed, and are
 * not reflected in the permissions returned by <tt>getPermissions</tt> and
 * <tt>getDefaultPermissions</tt>.
 * 
 * @version $Revision$
 */
public interface PermissionAdmin {
	/**
	 * Gets the permissions assigned to the bundle with the specified location.
	 * 
	 * @param location The location of the bundle whose permissions are to be
	 *        returned.
	 * 
	 * @return The permissions assigned to the bundle with the specified
	 *         location, or <tt>null</tt> if that bundle has not been assigned
	 *         any permissions.
	 */
	PermissionInfo[] getPermissions(String location);

	/**
	 * Assigns the specified permissions to the bundle with the specified
	 * location.
	 * 
	 * @param location The location of the bundle that will be assigned the
	 *        permissions.
	 * @param permissions The permissions to be assigned, or <tt>null</tt> if
	 *        the specified location is to be removed from the permission table.
	 * @exception SecurityException if the caller does not have the
	 *            <tt>AdminPermission</tt>.
	 */
	void setPermissions(String location, PermissionInfo[] permissions);

	/**
	 * Returns the bundle locations that have permissions assigned to them, that
	 * is, bundle locations for which an entry exists in the permission table.
	 * 
	 * @return The locations of bundles that have been assigned any permissions,
	 *         or <tt>null</tt> if the permission table is empty.
	 */
	String[] getLocations();

	/**
	 * Gets the default permissions.
	 * 
	 * <p>
	 * These are the permissions granted to any bundle that does not have
	 * permissions assigned to its location.
	 * 
	 * @return The default permissions, or <tt>null</tt> if no default
	 *         permissions are set.
	 */
	PermissionInfo[] getDefaultPermissions();

	/**
	 * Sets the default permissions.
	 * 
	 * <p>
	 * These are the permissions granted to any bundle that does not have
	 * permissions assigned to its location.
	 * 
	 * @param permissions The default permissions, or <tt>null</tt> if the
	 *        default permissions are to be removed from the permission table.
	 * @exception SecurityException if the caller does not have the
	 *            <tt>AdminPermission</tt>.
	 */
	void setDefaultPermissions(PermissionInfo[] permissions);
}
