/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.dal;

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

/**
 * A bundle's authority to perform specific privileged administrative operations
 * on the devices. The actions for this permission are:
 * <table>
 * <tr>
 * <td>Action</td>
 * <td>Method</td>
 * </tr>
 * <tr>
 * <td>{@link #ACTION_REMOVE}</td>
 * <td>{@link Device#remove()}</td>
 * </tr>
 * </table>
 * 
 * The name of the permission is a filter based. See OSGi Core Specification,
 * Filter Based Permissions. The filter gives an access to all device service
 * properties. The service property names are case insensitive. The filter
 * attribute names are processed in a case insensitive manner.
 */
public final class DevicePermission extends BasicPermission {

	private static final long	serialVersionUID	= -3020753566295420906L;

	/** A permission action to remove the device. */
	public static final String	ACTION_REMOVE		= "remove";

	/**
	 * Creates a new <code>FunctionalDevicePermission</code> with the given
	 * filter and actions. The constructor must only be used to create a
	 * permission that is going to be checked.
	 * <p>
	 * An filter example: (dal.device.hardware.vendor=acme)
	 * <p>
	 * An action list example: property, remove
	 * 
	 * @param filter A filter expression that can use any device service
	 *        property. The filter attribute names are processed in a case
	 *        insensitive manner. A special value of "*" can be used to match
	 *        all devices.
	 * @param action {@link #ACTION_REMOVE} action.
	 * 
	 * @throws IllegalArgumentException If the filter syntax is not correct or
	 *         invalid actions are specified.
	 */
	public DevicePermission(String filter, String action) {
		super(filter, action);
		// TODO: impl
	}

	/**
	 * Creates a new <code>FunctionalDevicePermission</code> with the given
	 * device and actions. The permission must be used for the security checks
	 * like:
	 * <p>
	 * <code>securityManager.checkPermission(new FunctionalDevicePermission(this, "remove"));</code>
	 * . The permissions constructed by this constructor must not be added to
	 * the <code>FunctionalDevicePermission</code> permission collections.
	 * 
	 * @param device The permission device.
	 * @param action {@link #ACTION_REMOVE} action.
	 */
	public DevicePermission(Device device, String action) {
		super(null);
		// TODO: impl
	}

	/**
	 * Two <code>FunctionalDevicePermission</code> instances are equal if:
	 * <ul>
	 * <li>represents the same filter and actions</li>
	 * <li>represents the same device and actions</li>
	 * </ul>
	 * 
	 * @param obj The object being compared for equality with this object.
	 * 
	 * @return <code>true</code> if two permissions are equal,
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		return false;
		// TODO: impl
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return Hash code value for this object.
	 */
	public int hashCode() {
		return -1;
		// TODO: impl
	}

	/**
	 * Returns the canonical string representation of {@link #ACTION_REMOVE}
	 * action.
	 * 
	 * @return The canonical string representation of the actions.
	 */
	public String getActions() {
		return null;
		// TODO: impl
	}

	/**
	 * Determines if the specified permission is implied by this object. The
	 * method will throw an exception if the specified permission was not
	 * constructed by {@link #DevicePermission(Device, String)}.
	 * 
	 * Returns <code>true</code> if the specified permission is a
	 * <code>FunctionalDevicePermission</code> and this permission filter
	 * matches the specified permission device properties.
	 * 
	 * @param p The permission to be implied. It must be constructed by
	 *        {@link #DevicePermission(Device, String)}.
	 * 
	 * @return <code>true</code> if the specified permission is implied by this
	 *         permission, <code>false</code> otherwise.
	 * 
	 * @throws IllegalArgumentException If the specified permission is not
	 *         constructed by {@link #DevicePermission(Device, String)}.
	 */
	public boolean implies(Permission p) {
		return false;
		// TODO: impl
	}

	/**
	 * Returns a new <code>PermissionCollection</code> suitable for storing
	 * <code>FunctionalDevicePermission</code> instances.
	 * 
	 * @return A new <code>PermissionCollection</code> instance.
	 */
	public PermissionCollection newPermissionCollection() {
		// TODO Auto-generated method stub
		return super.newPermissionCollection();
	}

}
