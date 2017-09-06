/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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

import org.osgi.service.device.Constants;

/**
 * Represents the device in the OSGi service registry. Note that {@code Device}
 * services are registered last. Before their registration, there is
 * {@code Function} services registration. The reverse order is used when the
 * services are unregistered. {@code Device} services are unregistered first
 * before {@code Function} services.
 */
public interface Device {

	/**
	 * Constant for the value of the {@link Constants#DEVICE_CATEGORY} service
	 * property. That category is used by all device services.
	 * 
	 * @see Constants#DEVICE_CATEGORY
	 */
	public static final String	DEVICE_CATEGORY							= "DAL";

	/**
	 * The service property value contains the device unique identifier. It's a
	 * mandatory property. The value type is {@code java.lang.String}. To
	 * simplify the unique identifier generation, the property value must follow
	 * the rule:
	 * <p>
	 * UID ::= driver-name ':' device-id
	 * <p>
	 * UID - device unique identifier
	 * <p>
	 * driver-name - the value of the {@link #SERVICE_DRIVER} service property
	 * <p>
	 * device-id - device unique identifier in the scope of the driver
	 */
	public static final String	SERVICE_UID								= "dal.device.UID";

	/**
	 * The service property value contains the reference device unique
	 * identifiers. It's an optional property. The value type is
	 * {@code java.lang.String[]}. It can be used to represent different
	 * relationships between the devices. For example, the EnOcean controller
	 * can have a reference to the USB dongle.
	 */
	public static final String	SERVICE_REFERENCE_UIDS					= "dal.device.reference.UIDs";

	/**
	 * The service property value contains the device driver name. For example,
	 * EnOcean, Z-Wave, Bluetooth, etc. It's a mandatory property. The value
	 * type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_DRIVER							= "dal.device.driver";

	/**
	 * The service property value contains the device name. It's an optional
	 * property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_NAME							= "dal.device.name";

	/**
	 * The service property value contains the device status. It's a mandatory
	 * property. The value type is {@code java.lang.Integer}. The possible
	 * values are:
	 * <ul>
	 * <li> {@link #STATUS_ONLINE}</li>
	 * <li> {@link #STATUS_OFFLINE}</li>
	 * <li> {@link #STATUS_REMOVED}</li>
	 * <li> {@link #STATUS_PROCESSING}</li>
	 * <li> {@link #STATUS_NOT_INITIALIZED}</li>
	 * <li> {@link #STATUS_NOT_CONFIGURED}</li>
	 * </ul>
	 */
	public static final String	SERVICE_STATUS							= "dal.device.status";

	/**
	 * The service property value contains the device status detail. It holds
	 * the reason for the current device status. It's an optional property. The
	 * value type is {@code java.lang.Integer}. There are two value categories:
	 * <ul>
	 * <li>positive values i.e. > 0 - those values contain details related to
	 * the current status. Examples: {@link #STATUS_DETAIL_CONNECTING} and
	 * {@link #STATUS_DETAIL_INITIALIZING}.</li>
	 * <li>negative values i.e. < 0 - those values contain errors related to the
	 * current status. Examples: {@link #STATUS_DETAIL_CONFIGURATION_UNAPPLIED},
	 * {@link #STATUS_DETAIL_BROKEN} and
	 * {@link #STATUS_DETAIL_COMMUNICATION_ERROR}.</li>
	 * </ul>
	 */
	public static final String	SERVICE_STATUS_DETAIL					= "dal.device.status.detail";

	/**
	 * The service property value contains the device hardware vendor. It's an
	 * optional property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_HARDWARE_VENDOR					= "dal.device.hardware.vendor";

	/**
	 * The service property value contains the device hardware version. It's an
	 * optional property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_HARDWARE_VERSION				= "dal.device.hardware.version";

	/**
	 * The service property value contains the device firmware vendor. It's an
	 * optional property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_FIRMWARE_VENDOR					= "dal.device.firmware.vendor";

	/**
	 * The service property value contains the device firmware version. It's an
	 * optional property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_FIRMWARE_VERSION				= "dal.device.firmware.version";

	/**
	 * The service property value contains the device types like DVD, TV, etc.
	 * It's an optional property. The value type is {@code java.lang.String[]}.
	 */
	public static final String	SERVICE_TYPES							= "dal.device.types";

	/**
	 * The service property value contains the device model. It's an optional
	 * property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_MODEL							= "dal.device.model";

	/**
	 * The service property value contains the device serial number. It's an
	 * optional property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_SERIAL_NUMBER					= "dal.device.serial.number";

	/**
	 * The service property value contains the device description. It's an
	 * optional property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_DESCRIPTION						= "dal.device.description";

	/**
	 * Device status indicates that the device has been removed from the
	 * network. That status must be set as the last device status. After that
	 * the device service can be unregistered from the service registry. It can
	 * be used as a value of {@link #SERVICE_STATUS} service property.
	 */
	public static final Integer	STATUS_REMOVED							= Integer.valueOf(1);

	/**
	 * Device status indicates that the device is currently not available for
	 * operations. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_OFFLINE							= Integer.valueOf(2);

	/**
	 * Device status indicates that the device is currently available for
	 * operations. The recent communication with the device has been passed
	 * through. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_ONLINE							= Integer.valueOf(3);

	/**
	 * Device status indicates that the device is currently busy with an
	 * operation. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_PROCESSING						= Integer.valueOf(4);

	/**
	 * Device status indicates that the device is currently not initialized.
	 * Some protocols don't provide device information right after the device is
	 * connected. The device can be initialized later when it's awakened. It can
	 * be used as a value of {@link #SERVICE_STATUS} service property.
	 */
	public static final Integer	STATUS_NOT_INITIALIZED					= Integer.valueOf(5);

	/**
	 * Device status indicates that the device is currently not configured. The
	 * device can require additional actions to become completely connected to
	 * the network. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_NOT_CONFIGURED					= Integer.valueOf(6);

	/**
	 * Device status detail indicates that the device is currently connecting to
	 * the network. It can be used as a value of {@link #SERVICE_STATUS_DETAIL}
	 * service property. The device status must be {@link #STATUS_PROCESSING}.
	 */
	public static final Integer	STATUS_DETAIL_CONNECTING				= Integer.valueOf(1);

	/**
	 * Device status detail indicates that the device is currently in process of
	 * initialization. It can be used as a value of
	 * {@link #SERVICE_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_PROCESSING}.
	 */
	public static final Integer	STATUS_DETAIL_INITIALIZING				= Integer.valueOf(2);

	/**
	 * Device status detail indicates that the device is leaving the network. It
	 * can be used as a value of {@link #SERVICE_STATUS_DETAIL} service
	 * property. The device status must be {@link #STATUS_PROCESSING}.
	 */
	public static final Integer	STATUS_DETAIL_REMOVING					= Integer.valueOf(3);

	/**
	 * Device status detail indicates that the device firmware is updating. It
	 * can be used as a value of {@link #SERVICE_STATUS_DETAIL} service
	 * property. The device status must be {@link #STATUS_PROCESSING}.
	 */
	public static final Integer	STATUS_DETAIL_FIRMWARE_UPDATING			= Integer.valueOf(4);

	/**
	 * Device status detail indicates that the device configuration is not
	 * applied. It can be used as a value of {@link #SERVICE_STATUS_DETAIL}
	 * service property. The device status must be
	 * {@link #STATUS_NOT_CONFIGURED}.
	 */
	public static final Integer	STATUS_DETAIL_CONFIGURATION_UNAPPLIED	= Integer.valueOf(-1);

	/**
	 * Device status detail indicates that the device is broken. It can be used
	 * as a value of {@link #SERVICE_STATUS_DETAIL} service property. The device
	 * status must be {@link #STATUS_OFFLINE}.
	 */
	public static final Integer	STATUS_DETAIL_BROKEN					= Integer.valueOf(-2);

	/**
	 * Device status detail indicates that the device communication is
	 * problematic. It can be used as a value of {@link #SERVICE_STATUS_DETAIL}
	 * service property. The device status must be {@link #STATUS_ONLINE} or
	 * {@link #STATUS_NOT_INITIALIZED}.
	 */
	public static final Integer	STATUS_DETAIL_COMMUNICATION_ERROR		= Integer.valueOf(-3);

	/**
	 * Device status detail indicates that the device doesn't provide enough
	 * information and cannot be determined. It can be used as a value of
	 * {@link #SERVICE_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_NOT_INITIALIZED}.
	 */
	public static final Integer	STATUS_DETAIL_DATA_INSUFFICIENT			= Integer.valueOf(-4);

	/**
	 * Device status detail indicates that the device is not accessible and
	 * further communication is not possible. It can be used as a value of
	 * {@link #SERVICE_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_OFFLINE}.
	 */
	public static final Integer	STATUS_DETAIL_INACCESSIBLE				= Integer.valueOf(-5);

	/**
	 * Device status detail indicates that the device cannot be configured. It
	 * can be used as a value of {@link #SERVICE_STATUS_DETAIL} service
	 * property. The device status must be {@link #STATUS_NOT_CONFIGURED}.
	 */
	public static final Integer	STATUS_DETAIL_CONFIGURATION_ERROR		= Integer.valueOf(-6);

	/**
	 * Device status detail indicates that the device is in duty cycle. It can
	 * be used as a value of {@link #SERVICE_STATUS_DETAIL} service property.
	 * The device status must be {@link #STATUS_OFFLINE}.
	 */
	public static final Integer	STATUS_DETAIL_DUTY_CYCLE				= Integer.valueOf(-7);

	/**
	 * Returns the current value of the specified property. The method will
	 * return the same value as {@code ServiceReference.getProperty(String)} for
	 * the service reference of this device.
	 * <p>
	 * This method must continue to return property values after the device
	 * service has been unregistered.
	 * 
	 * @param propKey The property key.
	 * 
	 * @return The property value or {@code null} if the property key cannot be
	 *         mapped to a value.
	 */
	public Object getServiceProperty(String propKey);

	/**
	 * Returns an array with all device service property keys. The method will
	 * return the same value as {@code ServiceReference.getPropertyKeys()} for
	 * the service reference of this device. The result cannot be {@code null}.
	 * 
	 * @return An array with all device service property keys, cannot be
	 *         {@code null}.
	 */
	public String[] getServicePropertyKeys();

	/**
	 * Removes this device.
	 * <p>
	 * The method must synchronously:
	 * <ul>
	 * <li>Remove the device from the device network.</li>
	 * <li>Set the device status to {@link #STATUS_REMOVED}.</li>
	 * <li>Unregister the device service from the OSGi service registry.</li>
	 * </ul>
	 * The caller should release the device service after successful execution,
	 * because the device will not be operational.
	 * 
	 * @throws DeviceException If an operation error is available.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         {@code DevicePermission(this device, }
	 *         {@link DevicePermission#REMOVE}{@code )} and the Java Runtime
	 *         Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void remove() throws DeviceException;
}
