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

/**
 * Represents the device in the OSGi service registry. Note that
 * <code>Device</code> services are registered last. Before their registration,
 * there is <code>DeviceFunction</code> services registration. The reverse order
 * is used when the services are unregistered. <code>Device</code> services are
 * unregistered first before <code>DeviceFunction</code> services.
 */
public interface Device {

	/**
	 * Constant for the value of the
	 * {@link org.osgi.service.device.Constants#DEVICE_CATEGORY} service
	 * property. That category is used by all device services.
	 * 
	 * @see org.osgi.service.device.Constants#DEVICE_CATEGORY
	 */
	public static final String	DEVICE_CATEGORY								= "DAL";

	/**
	 * The service property value contains the device unique identifier. It's a
	 * mandatory property. The value type is <code>java.lang.String</code>. To
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
	public static final String	SERVICE_UID									= "dal.device.UID";

	/**
	 * The service property value contains the reference device unique
	 * identifiers. It's an optional property. The value type is
	 * <code>java.lang.String[]</code>. It can be used to represent different
	 * relationships between the devices. For example, the ZigBee controller can
	 * have a reference to the USB dongle.
	 */
	public static final String	SERVICE_REFERENCE_UIDS						= "dal.device.reference.UIDs";

	/**
	 * The service property value contains the device driver name. For example,
	 * ZigBee, Z-Wave, Bluetooth etc. It's a mandatory property. The value type
	 * is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_DRIVER								= "dal.device.driver";

	/**
	 * The service property value contains the device name. It's an optional
	 * property. The value type is <code>java.lang.String</code>. The property
	 * value can be set with {@link #setName(String)} method.
	 */
	public static final String	SERVICE_NAME								= "dal.device.name";

	/**
	 * The service property value contains the device status. It's a mandatory
	 * property. The value type is <code>java.lang.Integer</code>. The possible
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
	public static final String	SERVICE_STATUS								= "dal.device.status";

	/**
	 * The service property value contains the device status detail. It holds
	 * the reason for the current device status. It's an optional property. The
	 * value type is <code>java.lang.Integer</code>. There are two value
	 * categories:
	 * <ul>
	 * <li>positive values i.e. > 0</li> - Those values contain details related
	 * to the current status. Examples: {@link #STATUS_DETAIL_CONNECTING} and
	 * {@link #STATUS_DETAIL_INITIALIZING}.
	 * <li>negative values i.e. < 0</li> - Those values contain errors related
	 * to the current status. Examples:
	 * {@link #STATUS_DETAIL_CONFIGURATION_NOT_APPLIED},
	 * {@link #STATUS_DETAIL_DEVICE_BROKEN} and
	 * {@link #STATUS_DETAIL_DEVICE_COMMUNICATION_ERROR}.
	 * </ul>
	 */
	public static final String	SERVICE_STATUS_DETAIL						= "dal.device.status.detail";

	/**
	 * The service property value contains the device hardware vendor. It's an
	 * optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_HARDWARE_VENDOR						= "dal.device.hardware.vendor";

	/**
	 * The service property value contains the device hardware version. It's an
	 * optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_HARDWARE_VERSION					= "dal.device.hardware.version";

	/**
	 * The service property value contains the device firmware vendor. It's an
	 * optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_FIRMWARE_VENDOR						= "dal.device.firmware.vendor";

	/**
	 * The service property value contains the device firmware version. It's an
	 * optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_FIRMWARE_VERSION					= "dal.device.firmware.version";

	/**
	 * The service property value contains the device types like DVD, TV etc.
	 * It's an optional property. The value type is
	 * <code>java.lang.String[]</code>.
	 */
	public static final String	SERVICE_TYPES								= "dal.device.types";

	/**
	 * The service property value contains the device model. It's an optional
	 * property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_MODEL								= "dal.device.model";

	/**
	 * The service property value contains the device serial number. It's an
	 * optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_SERIAL_NUMBER						= "dal.device.serial.number";

	/**
	 * The service property value contains the device description. It's an
	 * optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_DESCRIPTION							= "dal.device.description";

	/**
	 * Device status indicates that the device is removed from the network. That
	 * status must be set as the last device status and after that the device
	 * service can be unregistered from the service registry. It can be used as
	 * a value of {@link #SERVICE_STATUS} service property.
	 */
	public static final Integer	STATUS_REMOVED								= new Integer(1);

	/**
	 * Device status indicates that the device is currently not available for
	 * operations. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_OFFLINE								= new Integer(2);

	/**
	 * Device status indicates that the device is currently available for
	 * operations. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_ONLINE								= new Integer(3);

	/**
	 * Device status indicates that the device is currently busy with an
	 * operation. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_PROCESSING							= new Integer(4);

	/**
	 * Device status indicates that the device is currently not initialized.
	 * Some protocols don't provide device information right after the device is
	 * connected. The device can be initialized later when it's awakened. It can
	 * be used as a value of {@link #SERVICE_STATUS} service property.
	 */
	public static final Integer	STATUS_NOT_INITIALIZED						= new Integer(5);

	/**
	 * Device status indicates that the device is currently not configured. The
	 * device can require additional actions to become completely connected to
	 * the network. It can be used as a value of {@link #SERVICE_STATUS} service
	 * property.
	 */
	public static final Integer	STATUS_NOT_CONFIGURED						= new Integer(6);

	/**
	 * Device status detail indicates that the device is currently connecting to
	 * the network. It can be used as a value of {@link #SERVICE_STATUS_DETAIL}
	 * service property. The device status must be {@link #STATUS_PROCESSING}.
	 */
	public static final Integer	STATUS_DETAIL_CONNECTING					= new Integer(1);

	/**
	 * Device status detail indicates that the device is currently in process of
	 * initialization. It can be used as a value of
	 * {@link #SERVICE_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_PROCESSING}.
	 */
	public static final Integer	STATUS_DETAIL_INITIALIZING					= new Integer(2);

	/**
	 * Device status detail indicates that the device is leaving the network. It
	 * can be used as a value of {@link #SERVICE_STATUS_DETAIL} service
	 * property. The device status must be {@link #STATUS_PROCESSING}.
	 */
	public static final Integer	STATUS_DETAIL_REMOVING						= new Integer(3);

	/**
	 * Device status detail indicates that the device configuration is not
	 * applied. It can be used as a value of {@link #SERVICE_STATUS_DETAIL}
	 * service property. The device status must be
	 * {@link #STATUS_NOT_CONFIGURED}.
	 */
	public static final Integer	STATUS_DETAIL_CONFIGURATION_NOT_APPLIED		= new Integer(-1);

	/**
	 * Device status detail indicates that the device is broken. It can be used
	 * as a value of {@link #SERVICE_STATUS_DETAIL} service property. The device
	 * status must be {@link #STATUS_OFFLINE}.
	 */
	public static final Integer	STATUS_DETAIL_DEVICE_BROKEN					= new Integer(-2);

	/**
	 * Device status detail indicates that the device communication is
	 * problematic. It can be used as a value of {@link #SERVICE_STATUS_DETAIL}
	 * service property. The device status must be {@link #STATUS_ONLINE} or
	 * {@link #STATUS_NOT_INITIALIZED}.
	 */
	public static final Integer	STATUS_DETAIL_DEVICE_COMMUNICATION_ERROR	= new Integer(-3);

	/**
	 * Device status detail indicates that the device doesn't provide enough
	 * information and cannot be determined. It can be used as a value of
	 * {@link #SERVICE_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_NOT_INITIALIZED}.
	 */
	public static final Integer	STATUS_DETAIL_DEVICE_DATA_INSUFFICIENT		= new Integer(-4);

	/**
	 * Device status detail indicates that the device is not accessible and
	 * further communication is not possible. It can be used as a value of
	 * {@link #SERVICE_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_OFFLINE}.
	 */
	public static final Integer	STATUS_DETAIL_DEVICE_NOT_ACCESSIBLE			= new Integer(-5);

	/**
	 * Device status detail indicates that the device cannot be configured. It
	 * can be used as a value of {@link #SERVICE_STATUS_DETAIL} service
	 * property. The device status must be {@link #STATUS_NOT_CONFIGURED}.
	 */
	public static final Integer	STATUS_DETAIL_ERROR_APPLYING_CONFIGURATION	= new Integer(-6);

	/**
	 * Device status detail indicates that the device is in duty cycle. It can
	 * be used as a value of {@link #SERVICE_STATUS_DETAIL} service property.
	 * The device status must be {@link #STATUS_OFFLINE}.
	 */
	public static final Integer	STATUS_DETAIL_IN_DUTY_CYCLE					= new Integer(-7);

	/**
	 * Returns the current value of the specified property. The method will
	 * return the same value as
	 * {@link org.osgi.framework.ServiceReference#getProperty(String)} for the
	 * service reference of this device.
	 * <p>
	 * This method must continue to return property values after the device
	 * service has been unregistered.
	 * 
	 * @param propName The property name.
	 * 
	 * @return The property value
	 * 
	 * @throws IllegalArgumentException If the property name cannot be mapped to
	 *         value.
	 */
	public Object getProperty(String propName) throws IllegalArgumentException;

	/**
	 * Sets the device name. The method must synchronously update the
	 * {@link #SERVICE_NAME} service property of this device. The new name must
	 * be persistently stored. It'll set after framework restart.
	 * <code>null</code> name will clean up the current device name.
	 * 
	 * @param name The new device name or <code>null</code> to clean up the
	 *        name.
	 * 
	 * @throws DeviceException If an operation error is available.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>FunctionalDevicePermission[this device, {@link DevicePermission#ACTION_SET_NAME}]</code>
	 *         and the Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void setName(String name) throws DeviceException, UnsupportedOperationException,
			SecurityException, IllegalStateException;

	/**
	 * Removes this device. The method must synchronously remove the device from
	 * the device network.
	 * 
	 * @throws DeviceException If an operation error is available.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>FunctionalDevicePermission[this device, {@link DevicePermission#ACTION_REMOVE}]</code>
	 *         and the Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void remove() throws DeviceException, UnsupportedOperationException,
			SecurityException, IllegalStateException;

}
