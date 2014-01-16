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
 * Device Function service provides specific device operations and properties.
 * Each Device Function service must implement this interface. In additional to
 * this interface, the implementation can provide own:
 * <ul>
 * <li>properties;</li>
 * <li>operations.</li>
 * </ul>
 * The Device Function service can be registered in the service registry with
 * those service properties:
 * <ul>
 * <li>{@link #SERVICE_UID} - mandatory service property. The property value
 * contains the device function unique identifier.</li>
 * <li>{@link #SERVICE_DEVICE_UID} - optional service property. The property
 * value is the Functional Device identifiers. The Device Function belongs to
 * those devices.</li>
 * <li>{@link #SERVICE_REFERENCE_UIDS} - optional service property. The property
 * value contains the reference device function unique identifiers.</li>
 * <li>{@link #SERVICE_TYPE} - mandatory service property. The property value is
 * the function type.</li>
 * <li>{@link #SERVICE_VERSION} - optional service property. The property value
 * contains the function version.</li>
 * <li>{@link #SERVICE_DESCRIPTION} - optional service property. The property
 * value is the device function description.</li>
 * <li>{@link #SERVICE_OPERATION_NAMES} - optional service property. The
 * property value is the Device Function operation names.</li>
 * <li>{@link #SERVICE_PROPERTY_NAMES} - optional service property. The property
 * value is the Device Function property names.</li>
 * </ul>
 * The <code>DeviceFunction</code> services are registered before the
 * <code>Device</code> services. It's possible that {@link #SERVICE_DEVICE_UID}
 * point to missing services at the moment of the registration. The reverse
 * order is used when the services are unregistered. <code>DeviceFunction</code>
 * services are unregistered last after <code>Device</code> services.
 * <p>
 * Device Function service must be registered only under concrete Device
 * Function classes. It's not allowed to register Device Function service under
 * classes, which are not concrete Device Functions. For example, those
 * registrations are not allowed:
 * <ul>
 * <li>
 * <code>context.registerService(new String[] {ManagedService.class.getName(),
 * BooleanControl.class.getName()}, this, regProps);</code> -
 * <code>ManagedService</code> interface is not a Device Function interface;</li>
 * <li>
 * <code>context.registerService(new String[] {DeviceFunction.class.getName(),
 * BooleanControl.class.getName()}, this, regProps);</code> -
 * <code>DeviceFunction</code> interface is not concrete Device Function
 * interface.</li>
 * </ul>
 * That one is a valid registration: <code>context.registerService(new String[]
 * {Meter.class.getName(), BooleanControl.class.getName()}, this, regProps);</code>. <code>Meter</code> and <code>BooleanControl</code> are concrete Device
 * Function interfaces. That rule helps to the applications to find all
 * supported Device Function classes. Otherwise the Device Function services can
 * be accesses, but it's not clear which are the Device Function classes.
 * <p>
 * The Device Function properties must be integrated according to these rules:
 * <ul>
 * <li>Getter methods must be available for all properties with
 * {@link PropertyMetadata#META_INFO_PROPERTY_ACCESS_READABLE} access.</li>
 * <li>Getter method must return a subclass of {@link DeviceFunctionData}.</li>
 * <li>Setter methods must be available for all properties with
 * {@link PropertyMetadata#META_INFO_PROPERTY_ACCESS_WRITABLE} access.</li>
 * <li>Setter method must use {@link DeviceFunctionData} wrapped type. For
 * example, there is <code>MyFunctionData</code> with timestamp, unit and
 * <code>BigDecimal</code> value. The setter must accept as an argument the
 * value of type <code>BigDecimal</code>.</li>
 * <li>It's possible to have a second setter method, which accepts the value as
 * a first argument and the unit as a second argument.</li>
 * <li>No methods are required for properties with
 * {@link PropertyMetadata#META_INFO_PROPERTY_ACCESS_EVENTABLE} access.</li>
 * </ul>
 * The accessor method names must be defined according JavaBeans specification.
 * <p>
 * The Device Function operations are java methods, which cannot override the
 * property accessor methods. They can have zero or more parameters and zero or
 * one return value.
 * 
 * <p>
 * Operation arguments and Device Function properties are restricted by the same
 * set of rules. The data type can be one of the following types:
 * <ul>
 * <li>Java primitive type or corresponding reference type.</li>
 * <li><code>java.lang.String</code>.</li>
 * <li><code>Beans</code>, but the beans properties must use those rules. Java
 * Beans are defined in JavaBeans specification.</li>
 * <li>
 * <code>java.util.Map</code>s. The keys can be any reference type of Java
 * primitive types or <code>java.lang.String</code>. The values must use those
 * rules.</li>
 * <li>Arrays of defined types.</li>
 * </ul>
 * The properties metadata is accessible with
 * {@link #getPropertyMetadata(String)}. The operations metadata is accessible
 * with {@link #getOperationMetadata(String)}.
 * 
 * In order to provide common behavior, all Device Functions must follow a set
 * of common rules related to the implementation of their setters, getters,
 * operations and events:
 * <ul>
 * <li>
 * The setter method must be executed synchronously. If the underlying protocol
 * can return response to the setter call, it must be awaited. It simplifies the
 * property value modifications and doesn't require asynchronous callback.</li>
 * <li>
 * The operation method must be executed synchronously. If the underlying
 * protocol can return an operation confirmation or response, they must be
 * awaited. It simplifies the operation execution and doesn't require
 * asynchronous callback.</li>
 * <li>
 * The getter must return the last know cached property value. The device
 * implementation is responsible to keep that value up to date. It'll speed up
 * the applications when the Device Function property values are collected. The
 * same cached value can be shared between a few requests instead of a few calls
 * to the real device.</li>
 * <li>
 * If a given Device Function operation, getter or setter is not supported,
 * java.lang.UnsupportedOperationException must be thrown. It indicates that
 * Device Function is partially supported.</li>
 * <li>The Device Function operations, getters and setters must not override
 * <code>java.lang.Object</code> and this interface methods.</li>
 * </ul>
 */
public interface DeviceFunction {

	/**
	 * The service property value contains the device function unique
	 * identifier. It's a mandatory property. The value type is
	 * <code>java.lang.String</code>. To simplify the unique identifier
	 * generation, the property value must follow the rule:
	 * <p>
	 * function UID ::= device-id ':' function-id
	 * <p>
	 * function UID - device function unique identifier
	 * <p>
	 * device-id - the value of the {@link Device#SERVICE_UID} Functional Device
	 * service property
	 * <p>
	 * function-id - device function identifier in the scope of the device
	 */
	public static final String	SERVICE_UID				= "dal.function.UID";

	/**
	 * The service property value contains the device function type. It's an
	 * optional property. For example, the sensor function can have different
	 * types like temperature or pressure etc. The value type is
	 * <code>java.lang.String</code>.
	 * <p>
	 * Organizations that want to use device function types that do not clash
	 * with OSGi Alliance defined types should prefix their types in own
	 * namespace.
	 */
	public static final String	SERVICE_TYPE			= "dal.function.type";

	/**
	 * The service property value contains the device function version. That
	 * version can point to specific implementation version and vary in the
	 * different vendor implementations. It's an optional property. The value
	 * type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_VERSION			= "dal.function.version";

	/**
	 * The service property value contains the device unique identifier. The
	 * function belongs to this device. It's an optional property. The value
	 * type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_DEVICE_UID		= "dal.function.device.UID";

	/**
	 * The service property value contains the reference device function unique
	 * identifiers. It's an optional property. The value type is
	 * <code>java.lang.String[]</code>. It can be used to represent different
	 * relationships between the device functions.
	 */
	public static final String	SERVICE_REFERENCE_UIDS	= "dal.function.reference.UIDs";

	/**
	 * The service property value contains the device function description. It's
	 * an optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String	SERVICE_DESCRIPTION		= "dal.function.description";

	/**
	 * The service property value contains the device function operation names.
	 * It's an optional property. The value type is
	 * <code>java.lang.String[]</code>. It's not possible to exist two or more
	 * Device Function operations with the same name i.e. the operation
	 * overloading is not allowed.
	 */
	public static final String	SERVICE_OPERATION_NAMES	= "dal.function.operation.names";

	/**
	 * The service property value contains the device function property names.
	 * It's an optional property. The value type is
	 * <code>java.lang.String[]</code>. It's not possible to exist two or more
	 * Device Function properties with the same name.
	 */
	public static final String	SERVICE_PROPERTY_NAMES	= "dal.function.property.names";

	/**
	 * Provides metadata about the Device Function property specified with the
	 * name argument.
	 * <p>
	 * This method must continue to return the property metadata after the
	 * Device Function service has been unregistered.
	 * 
	 * @param propertyName The function property name, which metadata is
	 *        requested.
	 * 
	 * @return The property metadata for the given property name.
	 *         <code>null</code> if the property metadata is not supported.
	 * 
	 * @throws IllegalArgumentException If the function property with the
	 *         specified name is not supported.
	 */
	public PropertyMetadata getPropertyMetadata(String propertyName)
			throws IllegalArgumentException;

	/**
	 * Provides metadata about the Device Function operation.
	 * <p>
	 * This method must continue to return the operation metadata after the
	 * Device Function service has been unregistered.
	 * 
	 * @param operationName The function operation name, which metadata is
	 *        requested.
	 * 
	 * @return The operation metadata for the given operation name.
	 *         <code>null</code> if the operation metadata is not supported.
	 * 
	 * @throws IllegalArgumentException If the function operation with the
	 *         specified name is not supported.
	 */
	public OperationMetadata getOperationMetadata(String operationName)
			throws IllegalArgumentException;

	/**
	 * Returns the current value of the specified property. The method will
	 * return the same value as
	 * {@link org.osgi.framework.ServiceReference#getProperty(String)} for the
	 * service reference of this device function.
	 * <p>
	 * This method must continue to return property values after the device
	 * function service has been unregistered.
	 * 
	 * @param propName The property name.
	 * 
	 * @return The property value
	 * 
	 * @throws IllegalArgumentException If the property name cannot be mapped to
	 *         value.
	 */
	public Object getProperty(String propName) throws IllegalArgumentException;

}
