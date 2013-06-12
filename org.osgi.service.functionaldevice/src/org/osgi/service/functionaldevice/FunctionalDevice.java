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

package org.osgi.service.functionaldevice;

/**
 * Represents the functional device in the service registry.
 */
public interface FunctionalDevice {

	/**
	 * Constant for the value of the
	 * {@link org.osgi.service.device.Constants#DEVICE_CATEGORY} service
	 * property. That category is used by all abstract devices.
	 * 
	 * @see org.osgi.service.device.Constants#DEVICE_CATEGORY
	 */
	public static final String	DEVICE_CATEGORY								= "FunctionalDevice";

	/**
	 * The service property value contains the device unique identifier. It's a
	 * mandatory property. The value type is <code>java.lang.String</code>. The
	 * property value cannot be set. To simplify the unique identifier
	 * generation, the property value must follow the rule:
	 * <p>
	 * UID ::= communication-type ':' device-id
	 * <p>
	 * UID - device unique identifier
	 * <p>
	 * communication-type - the value of the {@link #PROPERTY_COMMUNICATION}
	 * service property
	 * <p>
	 * device-id - device unique identifier in the scope of the communication
	 * type
	 */
	public static final String	PROPERTY_UID								= "functional.device.UID";

	/**
	 * The service property value contains the parent device unique identifier.
	 * It's an optional property. The value type is
	 * <code>java.lang.String</code>. The property value cannot be set.
	 */
	public static final String	PROPERTY_PARENT_UID							= "functional.device.parent.UID";

	/**
	 * The service property value contains the child device unique identifiers.
	 * It's an optional property. The value type is <code>String+</code>. The
	 * property value cannot be set.
	 */
	public static final String	PROPERTY_CHILD_UIDS							= "functional.device.child.UIDs";

	/**
	 * The service property value contains the reference device unique
	 * identifiers. It's an optional property. The value type is
	 * <code>String+</code>. The property value cannot be set. It can be used to
	 * represent different relationships between the devices. For example, the
	 * ZigBee controller can have a reference to the USB dongle.
	 */
	public static final String	PROPERTY_REFERENCE_UIDS						= "functional.device.reference.UIDs";

	/**
	 * The service property value contains the device communication possibility.
	 * It can vary depending on the device. On protocol level, it can represent
	 * the used protocol. The peripheral device property can explore the used
	 * communication interface. It's a mandatory property. The value type is
	 * <code>java.lang.String</code>. The property value cannot be set.
	 */
	public static final String	PROPERTY_COMMUNICATION						= "functional.device.communication";

	/**
	 * The service property value contains the device name. It's an optional
	 * property. The value type is <code>java.lang.String</code>. The property
	 * value can be read and set.
	 */
	public static final String	PROPERTY_NAME								= "functional.device.name";

	/**
	 * The service property value contains the device status. It's a mandatory
	 * property. The value type is <code>java.lang.Integer</code>. The property
	 * value cannot be set. The possible values are:
	 * <ul>
	 * <li> {@link #STATUS_ONLINE}</li>
	 * <li> {@link #STATUS_OFFLINE}</li>
	 * <li> {@link #STATUS_REMOVED}</li>
	 * <li> {@link #STATUS_PROCESSING}</li>
	 * <li> {@link #STATUS_DISABLED}</li>
	 * <li> {@link #STATUS_NOT_INITIALIZED}</li>
	 * <li> {@link #STATUS_NOT_CONFIGURED}</li>
	 * </ul>
	 */
	public static final String	PROPERTY_STATUS								= "functional.device.status";

	/**
	 * The service property value contains the device status detail. It holds
	 * the reason for the current device status. It's an optional property. The
	 * value type is <code>java.lang.Integer</code>. The property value cannot
	 * be set. There are two value categories:
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
	public static final String	PROPERTY_STATUS_DETAIL						= "functional.device.status.detail";

	/**
	 * The service property value contains the device hardware vendor. It's an
	 * optional property. The value type is <code>java.lang.String</code>. The
	 * property value can be read and set.
	 */
	public static final String	PROPERTY_HARDWARE_VENDOR					= "functional.device.hardware.vendor";

	/**
	 * The service property value contains the device hardware version. It's an
	 * optional property. The value type is <code>java.lang.String</code>. The
	 * property value can be read and set.
	 */
	public static final String	PROPERTY_HARDWARE_VERSION					= "functional.device.hardware.version";

	/**
	 * The service property value contains the device firmware vendor. It's an
	 * optional property. The value type is <code>java.lang.String</code>. The
	 * property value can be read and set.
	 */
	public static final String	PROPERTY_FIRMWARE_VENDOR					= "functional.device.firmware.vendor";

	/**
	 * The service property value contains the device firmware version. It's an
	 * optional property. The value type is <code>java.lang.String</code>. The
	 * property value can be read and set.
	 */
	public static final String	PROPERTY_FIRMWARE_VERSION					= "functional.device.firmware.version";

	/**
	 * The service property value contains the device types like DVD, TV etc.
	 * It's an optional property. The value type is <code>String+</code>. The
	 * property value can be read and set.
	 */
	public static final String	PROPERTY_TYPES								= "functional.device.types";

	/**
	 * The service property value contains the device model. It's an optional
	 * property. The value type is <code>java.lang.String</code>. The property
	 * value can be read and set.
	 */
	public static final String	PROPERTY_MODEL								= "functional.device.model";

	/**
	 * The service property value contains the device serial number. It's an
	 * optional property. The value type is <code>java.lang.String</code>. The
	 * property value can be read and set.
	 */
	public static final String	PROPERTY_SERIAL_NUMBER						= "functional.device.serial.number";

	/**
	 * The service property value contains the supported Device Function names.
	 * It's an optional property. The value type is <code>String+</code>.
	 */
	public static final String	PROPERTY_FUNCTIONS							= "functional.device.functions";

	/**
	 * The service property value contains the device description. It's an
	 * optional property. The value type is <code>java.lang.String</code>. The
	 * property value can be read and set.
	 */
	public static final String	PROPERTY_DESCRIPTION						= "functional.device.description";

	/**
	 * Device status indicates that the device is removed. It can be used as a
	 * value of {@link #PROPERTY_STATUS} service property.
	 */
	public static final int		STATUS_REMOVED								= 0;

	/**
	 * Device status indicates that the device is currently not available for
	 * operations. It can be used as a value of {@link #PROPERTY_STATUS} service
	 * property.
	 */
	public static final int		STATUS_OFFLINE								= 2;

	/**
	 * Device status indicates that the device is currently available for
	 * operations. It can be used as a value of {@link #PROPERTY_STATUS} service
	 * property.
	 */
	public static final int		STATUS_ONLINE								= 3;

	/**
	 * Device status indicates that the device is currently busy with an
	 * operation. It can be used as a value of {@link #PROPERTY_STATUS} service
	 * property.
	 */
	public static final int		STATUS_PROCESSING							= 5;

	/**
	 * Device status indicates that the device is currently disabled. It can be
	 * used as a value of {@link #PROPERTY_STATUS} service property.
	 */
	public static final int		STATUS_DISABLED								= 6;

	/**
	 * Device status indicates that the device is currently not initialized.
	 * Some protocols don't provide device information right after the device is
	 * connected. The device can be initialized later when it's awakened. It can
	 * be used as a value of {@link #PROPERTY_STATUS} service property.
	 */
	public static final int		STATUS_NOT_INITIALIZED						= 7;

	/**
	 * Device status indicates that the device is currently not configured. The
	 * device can require additional actions to become completely connected to
	 * the network. It can be used as a value of {@link #PROPERTY_STATUS}
	 * service property.
	 */
	public static final int		STATUS_NOT_CONFIGURED						= 8;

	/**
	 * Device status detail indicates that the device is currently connecting to
	 * the network. It can be used as a value of {@link #PROPERTY_STATUS_DETAIL}
	 * service property. The device status must be {@link #STATUS_PROCESSING}.
	 */
	public static final int		STATUS_DETAIL_CONNECTING					= 1;

	/**
	 * Device status detail indicates that the device is currently in process of
	 * initialization. It can be used as a value of
	 * {@link #PROPERTY_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_PROCESSING}.
	 */
	public static final int		STATUS_DETAIL_INITIALIZING					= 2;

	/**
	 * Device status detail indicates that the device configuration is not
	 * applied. It can be used as a value of {@link #PROPERTY_STATUS_DETAIL}
	 * service property. The device status must be
	 * {@link #STATUS_NOT_CONFIGURED}.
	 */
	public static final int		STATUS_DETAIL_CONFIGURATION_NOT_APPLIED		= -1;

	/**
	 * Device status detail indicates that the device is broken. It can be used
	 * as a value of {@link #PROPERTY_STATUS_DETAIL} service property. The
	 * device status must be {@link #STATUS_OFFLINE}.
	 */
	public static final int		STATUS_DETAIL_DEVICE_BROKEN					= -2;

	/**
	 * Device status detail indicates that the device communication is
	 * problematic. It can be used as a value of {@link #PROPERTY_STATUS_DETAIL}
	 * service property. The device status must be {@link #STATUS_ONLINE} or
	 * {@link #STATUS_NOT_INITIALIZED}.
	 */
	public static final int		STATUS_DETAIL_DEVICE_COMMUNICATION_ERROR	= -3;

	/**
	 * Device status detail indicates that the device doesn't provide enough
	 * information and cannot be determined. It can be used as a value of
	 * {@link #PROPERTY_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_NOT_INITIALIZED}.
	 */
	public static final int		STATUS_DETAIL_DEVICE_DATA_INSUFFICIENT		= -4;

	/**
	 * Device status detail indicates that the device is not accessible and
	 * further communication is not possible. It can be used as a value of
	 * {@link #PROPERTY_STATUS_DETAIL} service property. The device status must
	 * be {@link #STATUS_OFFLINE}.
	 */
	public static final int		STATUS_DETAIL_DEVICE_NOT_ACCESSIBLE			= -5;

	/**
	 * Device status detail indicates that the device cannot be configured. It
	 * can be used as a value of {@link #PROPERTY_STATUS_DETAIL} service
	 * property. The device status must be {@link #STATUS_NOT_CONFIGURED}.
	 */
	public static final int		STATUS_DETAIL_ERROR_APPLYING_CONFIGURATION	= -6;

	/**
	 * Device status detail indicates that the device is in duty cycle. It can
	 * be used as a value of {@link #PROPERTY_STATUS_DETAIL} service property.
	 * The device status must be {@link #STATUS_OFFLINE}.
	 */
	public static final int		STATUS_DETAIL_IN_DUTY_CYCLE					= -7;

	/**
	 * Device type indicates that the device is peripheral. Usually, those
	 * devices are base and contains some meta information. It can be used as a
	 * value of {@link #PROPERTY_TYPES} service property.
	 */
	public static final String	TYPE_PERIPHERAL								= "type.peripheral";

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
	 * Sets the given property name to the given property value. The method can
	 * be used for:
	 * <ul>
	 * <li>Update - if the property name exists, the value will be updated.</li>
	 * <li>Add - if the property name doesn't exists, a new property will be
	 * added.</li>
	 * <li>Remove - if the property name exists and the given property value is
	 * <code>null</code>, then the property will be removed.</li>
	 * </ul>
	 * 
	 * @param propName The property name.
	 * @param propValue The property value.
	 * 
	 * @throws FunctionalDeviceException If an operation error is available.
	 * @throws IllegalArgumentException If the property name or value aren't
	 *         correct.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>FunctionalDevicePermission[this device, {@link FunctionalDevicePermission#ACTION_PROPERTY}]</code>
	 *         and the Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void setProperty(String propName, Object propValue) throws FunctionalDeviceException, IllegalArgumentException, UnsupportedOperationException, SecurityException, IllegalStateException;

	/**
	 * Sets the given property names to the given property values. The method is
	 * similar to {@link #setProperty(String, Object)}, but can update all
	 * properties with one bulk operation.
	 * 
	 * @param propNames The property names.
	 * @param propValues The property values.
	 * 
	 * @throws FunctionalDeviceException If an operation error is available.
	 * @throws IllegalArgumentException If the property values or names aren't
	 *         correct.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>FunctionalDevicePermission[this device, {@link FunctionalDevicePermission#ACTION_PROPERTY}]</code>
	 *         and the Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void setProperties(String[] propNames, Object[] propValues) throws FunctionalDeviceException, IllegalArgumentException, UnsupportedOperationException, SecurityException,
			IllegalStateException;

	/**
	 * Removes this device. The method must synchronously remove the device from
	 * the device network.
	 * 
	 * @throws FunctionalDeviceException If an operation error is available.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>FunctionalDevicePermission[this device, {@link FunctionalDevicePermission#ACTION_REMOVE}]</code>
	 *         and the Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void remove() throws FunctionalDeviceException, UnsupportedOperationException, SecurityException, IllegalStateException;

	/**
	 * Disables this device. The disabled device status is set to
	 * {@link #STATUS_DISABLED}. The device is not available for operations.
	 * 
	 * @throws FunctionalDeviceException If an operation error is available.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>FunctionalDevicePermission[this device, {@link FunctionalDevicePermission#ACTION_DISABLE}]</code>
	 *         and the Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void disable() throws FunctionalDeviceException, UnsupportedOperationException, IllegalStateException;

	/**
	 * Enables this this. The device is available for operations.
	 * 
	 * @throws FunctionalDeviceException If an operation error is available.
	 * @throws UnsupportedOperationException If the operation is not supported
	 *         over this device.
	 * @throws SecurityException If the caller does not have the appropriate
	 *         <code>FunctionalDevicePermission[this device, {@link FunctionalDevicePermission#ACTION_ENABLE}]</code>
	 *         and the Java Runtime Environment supports permissions.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 */
	public void enable() throws FunctionalDeviceException, UnsupportedOperationException, SecurityException, IllegalStateException;
	
	/**
	 * Returns the Device Function instance according to the given function.
	 * <code>null</code> if the function is not supported.
	 * 
	 * @param functionName The Device Function name.
	 * @return The function instance or <code>null</code> if the function is not
	 *         supported.
	 */
	public DeviceFunction getDeviceFunction(String functionName);

	/**
	 * Returns all Device Function instances or <code>null</code> if no
	 * functions are supported.
	 * 
	 * @return The Device Function instances or <code>null</code> if no
	 *         functions are supported.
	 */
	public DeviceFunction[] getDeviceFunctions();

}
