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
 * Function service provides specific device operations and properties. Each
 * function service must implement this interface. In additional to this
 * interface, the implementation can provide own:
 * <ul>
 * <li>properties;</li>
 * <li>operations.</li>
 * </ul>
 * The function service can be registered in the service registry with those
 * service properties:
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
 * The {@code Function} services are registered before the {@code Device}
 * services. It's possible that {@link #SERVICE_DEVICE_UID} point to missing
 * services at the moment of the registration. The reverse order is used when
 * the services are unregistered. {@code Function} services are unregistered
 * last after {@code Device} services.
 * <p>
 * Function service must be registered under the function class hierarchy. Other
 * interfaces are not allowed. All classes from the function class hierarchy
 * must participate as registration classes in the order from child to parent.
 * The {@code Function} interface must be the last one in the list. For example,
 * {@code MeterV2 extends MeterV1 extends Function} are function interfaces. If
 * the implementation would like to provide {@code MeterV2} functionality, the
 * registration is:
 * <code>context.registerService(new String[]{MeterV2.class.getName(), MeterV1.class.getName(), Function.class.getName()}, this, regProps);</code>
 * {@code MeterV2} is the last child in the class hierarchy and it's on the
 * first position. {@code MeterV1} is a parent of {@code MeterV2} and child of
 * {@code Function}. {@code MeterV1} position is between {@code MeterV2} and
 * {@code Function} in the registration classes. If the implementation would
 * like to provide {@code MeterV1} functionality, the registration is:
 * <code>context.registerService(new String[]{MeterV1.class.getName(), Function.class.getName()}, this, regProps);</code>
 * If the implementation would like to mark that there is a function, but no
 * specific function interface exists, the registration can be:
 * <code>context.registerService(new String[]{Function.class.getName()}, this, regProps);</code>
 * Note that such functions usually don't have operations and properties.
 * <p>
 * Some examples of not allowed registrations:
 * <ul>
 * <li>
 * <code>context.registerService(new String[] {ManagedService.class.getName(), Function.class.getName()}, this, regProps);</code>
 * - {@code ManagedService} interface doesn't participate in a function class
 * hierarchy.</li>
 * <li>
 * <code>context.registerService(new String[] {MeterV1.class.getName()}, this, regProps);</code>
 * - {@code Function} interface is missing.</li>
 * <li>
 * <code>context.registerService(new String[] {MeterV1.class.getName(), Alarm.class.getName(), Function.class.getName()}, this, regProps);</code>
 * , where {@code MeterV1 extends Function} and {@code Alarm extends Function} -
 * {@code MeterV1} and {@code Alarm} are from different function class
 * hierarchies.</li>
 * </ul>
 * <p>
 * That registration rule helps to the applications to find the supported
 * function classes and to identify the metadata. Otherwise the function
 * services can be accesses, but it's not clear which are the function classes
 * and metadata.
 * <p>
 * The function properties must be integrated according to these rules:
 * <ul>
 * <li>Getter methods must be available for all properties with
 * {@link PropertyMetadata#PROPERTY_ACCESS_READABLE} access.</li>
 * <li>Getter method must return a subclass of {@link FunctionData}.</li>
 * <li>Setter methods must be available for all properties with
 * {@link PropertyMetadata#PROPERTY_ACCESS_WRITABLE} access.</li>
 * <li>Setter method must use {@link FunctionData} wrapped type. For example,
 * there is {@code MyFunctionData} with timestamp, unit and {@code BigDecimal}
 * value. The setter must accept as an argument the value of type
 * {@code BigDecimal}.</li>
 * <li>It's possible to have a second setter method, which accepts the value as
 * a first argument and the unit as a second argument.</li>
 * <li>No methods are required for properties with
 * {@link PropertyMetadata#PROPERTY_ACCESS_EVENTABLE} access.</li>
 * </ul>
 * The accessor method names must be defined according JavaBeans specification.
 * <p>
 * The function operations are java methods, which cannot override the property
 * accessor methods. They can have zero or more parameters and zero or one
 * return value.
 * 
 * <p>
 * Operation arguments and function properties are restricted by the same set of
 * rules. The data type can be one of the following types:
 * <ul>
 * <li>Java primitive type or corresponding reference type.</li>
 * <li>{@code java.lang.String}.</li>
 * <li>{@code Beans}, but the beans properties must use those rules. Java Beans
 * are defined in JavaBeans specification.</li>
 * <li>
 * {@code java.util.Map}s. The keys can be any reference type of Java primitive
 * types or {@code java.lang.String}. The values must use those rules.</li>
 * <li>Arrays of defined types.</li>
 * </ul>
 * The properties metadata is accessible with
 * {@link #getPropertyMetadata(String)}. The operations metadata is accessible
 * with {@link #getOperationMetadata(String)}.
 * 
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
 * <li>
 * If a given function operation, getter or setter is not supported,
 * java.lang.UnsupportedOperationException must be thrown. It indicates that
 * function is partially supported.</li>
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
	 * temperature or pressure etc. The value type is {@code java.lang.String}.
	 * <p>
	 * Organizations that want to use function types that do not clash with OSGi
	 * Alliance defined types should prefix their types in own namespace.
	 * <p>
	 * The type does'nt mandate specific function interface. It can be used with
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
	 * The service property value contains the function operation names. It's an
	 * optional property. The property is missing when there are no function
	 * operations and property must be set when there are function operations.
	 * The value type is {@code java.lang.String[]}. It's not possible to exist
	 * two or more function operations with the same name i.e. the operation
	 * overloading is not allowed.
	 */
	public static final String	SERVICE_OPERATION_NAMES	= "dal.function.operation.names";

	/**
	 * The service property value contains the function property names. It's an
	 * optional property. The property is missing when there are no function
	 * properties and property must be set when there are function properties.
	 * The value type is {@code java.lang.String[]}. It's not possible to exist
	 * two or more function properties with the same name.
	 */
	public static final String	SERVICE_PROPERTY_NAMES	= "dal.function.property.names";

	/**
	 * Provides metadata about the function property specified with the name
	 * argument.
	 * <p>
	 * This method must continue to return the property metadata after the
	 * function service has been unregistered.
	 * 
	 * @param propertyName The function property name, which metadata is
	 *        requested.
	 * 
	 * @return The property metadata for the given property name. {@code null}
	 *         if the property metadata is not supported.
	 * 
	 * @throws IllegalArgumentException If the function property with the
	 *         specified name is not supported.
	 */
	public PropertyMetadata getPropertyMetadata(String propertyName)
			throws IllegalArgumentException;

	/**
	 * Provides metadata about the function operation.
	 * <p>
	 * This method must continue to return the operation metadata after the
	 * function service has been unregistered.
	 * 
	 * @param operationName The function operation name, which metadata is
	 *        requested.
	 * 
	 * @return The operation metadata for the given operation name. {@code null}
	 *         if the operation metadata is not supported.
	 * 
	 * @throws IllegalArgumentException If the function operation with the
	 *         specified name is not supported.
	 */
	public OperationMetadata getOperationMetadata(String operationName)
			throws IllegalArgumentException;

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
