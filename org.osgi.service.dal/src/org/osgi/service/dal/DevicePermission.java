/*
 * Copyright (c) OSGi Alliance (2013, 2017). All Rights Reserved.
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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * A bundle's authority to perform specific privileged administrative operations
 * on the devices. The method {@link Device#remove()} is protected with
 * {@link #REMOVE} permission action.
 * <p>
 * The name of the permission is a filter based. See OSGi Core Specification,
 * Filter Based Permissions. The filter gives an access to all device service
 * properties. Filter attribute names are processed in a case sensitive manner.
 */
public class DevicePermission extends BasicPermission {

	private static final long		serialVersionUID	= -3020753566295420906L;

	/** A permission action to remove the device. */
	public static final String		REMOVE				= "remove";

	/**
	 * If the permission was built with filter, this filed contains LDAP filter
	 * used for matching.
	 */
	private transient Filter		filter;

	/**
	 * Initialized when the device permission is built with device instance.
	 * Such permissions are used only for security checks.
	 */
	transient Device				device;

	/**
	 * Device properties are initialized on the first request.
	 */
	private transient Dictionary<String, Object>	deviceProperties;

	/**
	 * Creates a new {@code DevicePermission} with the given filter and actions.
	 * The constructor must only be used to create a permission that is going to
	 * be checked.
	 * <p>
	 * A filter example: (dal.device.hardware.vendor=acme)
	 * <p>
	 * An action: remove
	 * 
	 * @param filter A filter expression that can use any device service
	 *        property. The filter attribute names are processed in a case
	 *        insensitive manner. A special value of "*" can be used to match
	 *        all devices.
	 * @param action {@link #REMOVE} action.
	 * 
	 * @throws IllegalArgumentException If the filter syntax is not correct or
	 *         invalid action is specified.
	 * @throws NullPointerException If the filter or action is null.
	 */
	public DevicePermission(String filter, String action) {
		super(filter);
		this.filter = parseFilter(filter);
		validateAction(action);
	}

	/**
	 * Creates a new {@code DevicePermission} with the given device and actions.
	 * The permission must be used for the security checks like:
	 * <p>
	 * {@code securityManager.checkPermission(new DevicePermission(this, "remove"))}
	 * . The permissions constructed by this constructor must not be added to
	 * the {@code DevicePermission} permission collections.
	 * 
	 * @param device The device that needs to be checked for a permission.
	 * @param action {@link #REMOVE} action.
	 * 
	 * @throws IllegalArgumentException If an invalid action is specified.
	 * @throws NullPointerException If the device or action is null.
	 */
	public DevicePermission(Device device, String action) {
		super(createPermissionName(device));
		validateAction(action);
		this.device = device;
	}

	/**
	 * Two {@code DevicePermission} instances are equal if:
	 * <ul>
	 * <li>Represents the same filter and action.</li>
	 * <li>Represents the same device (in respect to device unique identifier)
	 * and action.</li>
	 * </ul>
	 * 
	 * @param obj The object being compared for equality with this object.
	 * 
	 * @return {@code true} if two permissions are equal, {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DevicePermission)) {
			return false;
		}
		DevicePermission dp = (DevicePermission) obj;
		return (((null == this.device) && (null == dp.device)) ||
				((null != this.device) && (null != dp.device))) &&
				(getName().equals(dp.getName()));
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return Hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return 31 * 17 + getName().hashCode() +
				((null != this.device) ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
	}

	/**
	 * Returns the canonical string representation of {@link #REMOVE} action.
	 * 
	 * @return The canonical string representation of the actions.
	 */
	@Override
	public String getActions() {
		return REMOVE;
	}

	/**
	 * Determines if the specified permission is implied by this object. The
	 * method will return {@code false} if the specified permission was not
	 * constructed by {@link #DevicePermission(Device, String)}.
	 * 
	 * Returns {@code true} if the specified permission is a
	 * {@code DevicePermission} and this permission filter matches the specified
	 * permission device properties.
	 * 
	 * @param p The permission to be implied. It must be constructed by
	 *        {@link #DevicePermission(Device, String)}.
	 * 
	 * @return {@code true} if the specified permission is implied by this
	 *         permission, {@code false} otherwise.
	 * 
	 * @throws IllegalArgumentException If the specified permission is not
	 *         constructed by {@link #DevicePermission(Device, String)}.
	 */
	@Override
	public boolean implies(Permission p) {
		if (!(p instanceof DevicePermission)) {
			return false;
		}
		if (null != this.device) {
			return false; // this filter can match other permission device
		}
		DevicePermission requested = (DevicePermission) p;
		if (null == requested.device) {
			return false; // this filter can match other permission device
		}
		return implyDevicePermission(requested);
	}

	/**
	 * Returns a new {@code PermissionCollection} suitable for storing
	 * {@code DevicePermission} instances.
	 * 
	 * @return A new {@code PermissionCollection} instance.
	 */
	@Override
	public PermissionCollection newPermissionCollection() {
		return new DevicePermissionCollection();
	}

	/**
	 * Called when the permission state is saved to a stream. Permissions with
	 * device instance cannot be serialized.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
		if (null != this.device) {
			throw new NotSerializableException("Device permission with a device instance cannot be serialized.");
		}
		s.defaultWriteObject();
	}

	/**
	 * Called to restore the permission state from the stream.
	 */
	private synchronized void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		this.filter = parseFilter(getName());
	}

	boolean implyDevicePermission(DevicePermission dp) {
		if (null == this.filter) {
			return true; // it's "*"
		}
		Dictionary<String, Object> devicePropertiesLocal = dp.getDeviceProperties();
		if (null == devicePropertiesLocal) {
			return false;
		}
		return this.filter.matchCase(devicePropertiesLocal);
	}

	private Dictionary<String, Object> getDeviceProperties() {
		if (null != this.deviceProperties) {
			return this.deviceProperties;
		}
		if (null == this.device) {
			return null;
		}
		String[] devicePropKeys = this.device.getServicePropertyKeys();
		Dictionary<String, Object> devicePropertiesLocal = new Hashtable<>(devicePropKeys.length, 1F);
		for (int i = 0; i < devicePropKeys.length; i++) {
			Object currentDevicePropValue = this.device.getServiceProperty(devicePropKeys[i]);
			if (null != currentDevicePropValue) {
				devicePropertiesLocal.put(devicePropKeys[i], currentDevicePropValue);
			}
		}
		this.deviceProperties = devicePropertiesLocal;
		return this.deviceProperties;
	}

	private static String createPermissionName(Device device) {
		if (null == device) {
			throw new NullPointerException("The device cannot be null.");
		}
		final String deviceUID;
		try {
			deviceUID = (String) device.getServiceProperty(Device.SERVICE_UID);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException("The device UID must be string.");
		}
		if (null == deviceUID) {
			throw new IllegalArgumentException("There is no device UID.");
		}
		return '(' + Device.SERVICE_UID + '=' + device.getServiceProperty(Device.SERVICE_UID) + ')';
	}

	private static void validateAction(String action) {
		if (null == action) {
			throw new NullPointerException("The action is null.");
		}
		if (!REMOVE.equals(action)) {
			throw new IllegalArgumentException("The action must be: " + action);
		}
	}

	private static Filter parseFilter(String filter) {
		if ((1 == filter.length()) && ("*".equals(filter))) {
			return null;
		}
		try {
			return FrameworkUtil.createFilter(filter);
		} catch (InvalidSyntaxException ise) {
			IllegalArgumentException iae = new IllegalArgumentException("The filter is invalid: " + filter);
			iae.initCause(ise);
			throw iae;
		}
	}
}

/**
 * Device permission collection stores permissions, which are built with a
 * filter. Permissions with device instance are not accepted.
 */
final class DevicePermissionCollection extends PermissionCollection {

	private static final long	serialVersionUID	= 2631804209616986690L;

	/**
	 * The permission name is used as a key, the permission is used as a value.
	 */
	private transient HashMap<String, DevicePermission>	permissions;

	/**
	 * {@code true} means that "*" has been added to the collection.
	 */
	private transient boolean	implyAll;

	/**
	 * Creates an empty device permission collection.
	 */
	public DevicePermissionCollection() {
		this.permissions = new HashMap<>();
	}

	/**
	 * Adds a device permission to this device permission collection. Only
	 * device permissions with filters are accepted.
	 * 
	 * @param permission The {@code DevicePermission} object to add.
	 * @throws IllegalArgumentException If the specified permission is not an
	 *         {@code DevicePermission} instance or was constructed with a
	 *         {@code Device} object.
	 * @throws SecurityException If this {@code DevicePermissionCollection}
	 *         object has been marked read-only.
	 */
	@Override
	public void add(Permission permission) {
		if (!(permission instanceof DevicePermission)) {
			throw new IllegalArgumentException("Device permission is expected: " + permission);
		}
		if (isReadOnly()) {
			throw new SecurityException("The device permission collection is read-only.");
		}
		final DevicePermission dp = (DevicePermission) permission;
		if (null != dp.device) {
			throw new IllegalArgumentException("Only device permissions with filter can be added: " + dp);
		}
		final String name = dp.getName();
		synchronized (this) {
			this.permissions.put(name, dp);
			if (!implyAll && ("*".equals(name))) {
				implyAll = true;
			}
		}
	}

	/**
	 * Determines if the specified permissions implies the permissions expressed
	 * in {@code permission} argument.
	 * 
	 * @param permission The device permission object to compare with the
	 *        {@code DevicePermission} objects in this collection.
	 * @return {@code true} if {@code permission} is implied by an
	 *         {@code DevicePermission} in this collection, {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean implies(Permission permission) {
		if (!(permission instanceof DevicePermission)) {
			return false;
		}
		DevicePermission requested = (DevicePermission) permission;
		if (null == requested.device) {
			return false; // the requested must have device instance

		}
		final Collection<DevicePermission> perms;
		synchronized (this) {
			if (this.implyAll) {
				return true;
			}
			perms = this.permissions.values();
		}
		for (Iterator<DevicePermission> permsIter = perms.iterator(); permsIter.hasNext();/* empty */) {
			final DevicePermission currentDevicePerm = permsIter.next();
			if (currentDevicePerm.implyDevicePermission(requested)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an enumeration of all {@code DevicePermission} objects in the
	 * collection.
	 * 
	 * @return Enumeration of all {@code DevicePermission} objects.
	 */
	@Override
	public synchronized Enumeration<Permission> elements() {
		return Collections.enumeration(
				new ArrayList<Permission>(this.permissions.values()));
	}

	private static final ObjectStreamField[]	serialPersistentFields	= {
																		new ObjectStreamField("permissions", HashMap.class),
																		new ObjectStreamField("implyAll", Boolean.TYPE)};

	private synchronized void writeObject(ObjectOutputStream out) throws IOException {
		ObjectOutputStream.PutField pfields = out.putFields();
		pfields.put("permissions", this.permissions);
		pfields.put("implyAll", this.implyAll);
		out.writeFields();
	}

	private synchronized void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField gfields = in.readFields();
		@SuppressWarnings("unchecked")
		HashMap<String, DevicePermission> p = (HashMap<String, DevicePermission>) gfields.get("permissions", null);
		this.permissions = p;
		this.implyAll = gfields.get("implyAll", false);
	}
}
