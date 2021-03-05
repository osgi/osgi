/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.dal;

/**
 * Function service provides specific device operations and properties. Each
 * function service must implement this interface. In additional to this
 * interface, the implementation can provide own:
 * <ul>
 * <li>properties;</li>
 * <li>operations.</li>
 * </ul>
 * <p>
 * The function service is registered in the service registry with these service
 * properties:
 * <ul>
 * <li>{@link #SERVICE_UID} - mandatory service property. The property value
 * contains the function unique identifier.</li>
 * <li>{@link #SERVICE_DEVICE_UID} - optional service property. The property
 * value is the Functional Device identifiers. The function belongs to those
 * devices.</li>
 * <li>{@link #SERVICE_REFERENCE_UIDS} - optional service property. The property
 * value contains the reference function unique identifiers.</li>
 * <li>{@link #SERVICE_TYPE} - mandatory service property. The property value is
 * the function type.</li>
 * <li>{@link #SERVICE_VERSION} - optional service property. The property value
 * contains the function version.</li>
 * <li>{@link #SERVICE_DESCRIPTION} - optional service property. The property
 * value is the function description.</li>
 * <li>{@link #SERVICE_OPERATION_NAMES} - optional service property. The
 * property is missing when there are no function operations and property must
 * be set when there are function operations. The property value is the function
 * operation names.</li>
 * <li>{@link #SERVICE_PROPERTY_NAMES} - optional service property. The property
 * is missing when there are no function properties and property must be set
 * when there are function properties. The property value is the function
 * property names.</li>
 * </ul>
 * <p>
 * On start up, the {@code Function} services are registered before the
 * {@code Device} services. It's possible that {@link #SERVICE_DEVICE_UID} point
 * to missing services at the moment of the registration. The reverse order is
 * used when the services are unregistered. {@code Function} services are
 * unregistered last after {@code Device} services.
 * <p>
 * The {@code Function} service should be registered only under the function
 * class hierarchy. Other classes can be used if there are no ambiguous
 * representations. For example, an ambiguous representation can be a function
 * registered under two independent function classes like {@code BinarySwitch}
 * and {@code Meter}. In this example, both functions support the same property
 * {@code state} with different meaning.
 * {@code getPropertyMetadata(String propertyName)} method cannot determinate
 * which property is requested. It can be <code>BinarySwitch state</code> or
 * <code>Meter state</code>.
 * <p>
 * To simplify the generic function discovery, the {@code Function} interface
 * must be used for the service registration. In this way, the generic
 * applications can easily find all services, which are functions in the service
 * registry. Because of this rule, this registration is not allowed:
 * <p>
 * <code>context.registerService(MeterV1.class.getName(), this, regProps);</code>
 * <p>
 * If the implementation would like to mark that there is a function, but no
 * specific function interface exists, the registration can be:
 * <p>
 * <code>context.registerService(Function.class.getName(), this, regProps);</code>
 * <p>
 * Note that such functions usually don't have operations and properties.
 * <p>
 * The function properties must be integrated according to these rules:
 * <ul>
 * <li>Getter methods must be available for all properties with
 * {@link PropertyMetadata#ACCESS_READABLE} access.</li>
 * <li>Getter method must return a subclass of {@link FunctionData}.</li>
 * <li>Setter methods must be available for all properties with
 * {@link PropertyMetadata#ACCESS_WRITABLE} access.</li>
 * <li>
 * Setter methods can be any combination of:
 * <ul>
 * <li>Setter method which accepts a subclass of {@link FunctionData}.</li>
 * <li>
 * Setter method which accepts the values used by the {@link FunctionData}
 * subclass, if there are no equal types.</li>
 * </ul>
 * It's possible to have only one or both of them. Examples:
 * <ul>
 * <li>
 * There is {@code MyFunctionData} bean with {@code BigDecimal} value for a
 * {@code data} property. Valid setters are {@code setData(MyFunctionData data)}
 * and {@code setData(BigDecimal data)}.</li>
 * <li>
 * There is {@code MySecondFunctionData} bean with {@code BigDecimal} prefix and
 * {@code BigDecimal} suffix for a {@code data} property. The prefix and suffix
 * are using equal types and we cannot have a setter with the values used by
 * {@code MySecondFunctionData}. The only one possible setter is
 * {@code setData(MySecondFunctionData data)}.</li>
 * </ul>
 * </li>
 * <li>No methods are required for properties with
 * {@link PropertyMetadata#ACCESS_EVENTABLE} access.</li>
 * </ul>
 * <p>
 * The accessor method names must be defined according JavaBeans specification.
 * <p>
 * The function operations are java methods, which cannot override the property
 * accessor methods. They can have zero or more parameters and zero or one
 * return value.
 * <p>
 * Operation arguments and function properties are restricted by the same set of
 * rules. The data type can be one of the following types:
 * <ul>
 * <li>Java primitive type or corresponding reference type.</li>
 * <li>{@code java.lang.String}.</li>
 * <li>Numerical type i.e. the type which extends <code>java.lang.Number</code>.
 * The numerical type must follow these conventions:
 * <ul>
 * <li>The type must provide a public static method called {@code valueOf} that
 * returns an instance of the given type and takes a single {@code String}
 * argument or a public constructor which takes a single {@code String}
 * argument.</li>
 * <li>
 * The {@code String} argument from the previous bullet can be provided by
 * {@code toString()} method of the instance.</li>
 * </ul>
 * </li>
 * <li>{@code Beans}, but the beans properties must use those rules. Java Beans
 * are defined in JavaBeans specification.</li>
 * <li>
 * {@code java.util.Map}s. The keys can be {@code java.lang.String}. The values
 * of a single type follow these rules.</li>
 * <li>Arrays of defined types.</li>
 * </ul>
 * <p>
 * The properties metadata is accessible with
 * {@link #getPropertyMetadata(String)}. The operations metadata is accessible
 * with {@link #getOperationMetadata(String)}.
 * <p>
 * In order to provide common behavior, all functions must follow a set of
 * common rules related to the implementation of their setters, getters,
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
 * the applications when the function property values are collected. The same
 * cached value can be shared between a few requests instead of a few calls to
 * the real device.</li>
 * <li>The function operations, getters and setters must not override
 * {@code java.lang.Object} and this interface methods.</li>
 * </ul>
 */
public interface Function {

	/**
	 * The service property value contains the function unique identifier. It's
	 * a mandatory property. The value type is {@code java.lang.String}. To
	 * simplify the unique identifier generation, the property value must follow
	 * the rule:
	 * <p>
	 * function UID ::= device-id ':' function-id
	 * <p>
	 * function UID - function unique identifier
	 * <p>
	 * device-id - the value of the {@link Device#SERVICE_UID} Device service
	 * property
	 * <p>
	 * function-id - function identifier in the scope of the device
	 * <p>
	 * If the function is not bound to a device, the function unique identifier
	 * can be device independent.
	 */
	public static final String	SERVICE_UID				= "dal.function.UID";

	/**
	 * The service property value contains the function type. It's an optional
	 * property. For example, the sensor function can have different types like
	 * temperature, pressure, etc. The value type is {@code java.lang.String}.
	 * <p>
	 * Organizations that want to use function types that do not clash with OSGi
	 * Alliance defined types should prefix their types in own namespace.
	 * <p>
	 * The type doesn't mandate specific function interface. It can be used with
	 * different functions.
	 */
	public static final String	SERVICE_TYPE			= "dal.function.type";

	/**
	 * The service property value contains the function version. That version
	 * can point to specific implementation version and vary in the different
	 * vendor implementations. It's an optional property. The value type is
	 * {@code java.lang.String}.
	 */
	public static final String	SERVICE_VERSION			= "dal.function.version";

	/**
	 * The service property value contains the device unique identifier. The
	 * function belongs to this device. It's an optional property. The value
	 * type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_DEVICE_UID		= "dal.function.device.UID";

	/**
	 * The service property value contains the reference function unique
	 * identifiers. It's an optional property. The value type is
	 * {@code java.lang.String[]}. It can be used to represent different
	 * relationships between the functions.
	 */
	public static final String	SERVICE_REFERENCE_UIDS	= "dal.function.reference.UIDs";

	/**
	 * The service property value contains the function description. It's an
	 * optional property. The value type is {@code java.lang.String}.
	 */
	public static final String	SERVICE_DESCRIPTION		= "dal.function.description";

	/**
	 * The service property value contains the function property names. It's an
	 * optional property. The property is missing when there are no function
	 * properties and property must be set when there are function properties.
	 * The value type is {@code java.lang.String[]}. It's not possible to exist
	 * two or more function properties with the same name.
	 */
	public static final String	SERVICE_PROPERTY_NAMES	= "dal.function.property.names";

	/**
	 * The service property value contains the function operation names. It's an
	 * optional property. The property is missing when there are no function
	 * operations and property must be set when there are function operations.
	 * The value type is {@code java.lang.String[]}. It's not possible to exist
	 * two or more function operations with the same name i.e. the operation
	 * overloading is not allowed.
	 */
	public static final String	SERVICE_OPERATION_NAMES	= "dal.function.operation.names";

	/**
	 * Provides metadata about the function property.
	 * <p>
	 * This method must continue to return the property metadata after the
	 * function service has been unregistered.
	 * 
	 * @param propertyName The function property name, for which metadata is
	 *        requested.
	 * 
	 * @return The property metadata for the given property name. {@code null}
	 *         if the property metadata is not available.
	 * 
	 * @throws IllegalArgumentException If the function property with the
	 *         specified name is not available.
	 */
	public PropertyMetadata getPropertyMetadata(String propertyName);

	/**
	 * Provides metadata about the function operation.
	 * <p>
	 * This method must continue to return the operation metadata after the
	 * function service has been unregistered.
	 * 
	 * @param operationName The function operation name, for which metadata is
	 *        requested.
	 * 
	 * @return The operation metadata for the given operation name. {@code null}
	 *         if the operation metadata is not available.
	 * 
	 * @throws IllegalArgumentException If the function operation with the
	 *         specified name is not available.
	 */
	public OperationMetadata getOperationMetadata(String operationName);

	/**
	 * Returns the current value of the specified property. The method will
	 * return the same value as {@code ServiceReference.getProperty(String)} for
	 * the service reference of this function.
	 * <p>
	 * This method must continue to return property values after the device
	 * function service has been unregistered.
	 * 
	 * @param propKey The property key.
	 * 
	 * @return The property value or {@code null} if the property key cannot be
	 *         mapped to a value.
	 */
	public Object getServiceProperty(String propKey);

	/**
	 * Returns an array with all function service property keys. The method will
	 * return the same value as {@code ServiceReference.getPropertyKeys()} for
	 * the service reference of this function. The result cannot be {@code null}
	 * .
	 * 
	 * @return An array with all function service property keys, cannot be
	 *         {@code null}.
	 */
	public String[] getServicePropertyKeys();
}
