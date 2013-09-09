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

import java.util.Map;

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
 * <li>{@link #PROPERTY_DEVICE_UID} - optional service property. The property
 * value is the Functional Device identifiers. The Device Function belongs to
 * those devices.</li>
 * <li>{@link #PROPERTY_DESCRIPTION} - optional service property. The property
 * value is the device function description.</li>
 * <li>{@link #PROPERTY_OPERATION_NAMES} - optional service property. The
 * property value is the Device Function operation names.</li>
 * <li>{@link #PROPERTY_PROPERTY_NAMES} - optional service property. The
 * property value is the Device Function property names.</li>
 * </ul>
 * The <code>DeviceFunction</code> services are registered before the
 * <code>Device</code> services. It's possible that {@link #PROPERTY_DEVICE_UID}
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
 * BinaryControl.class.getName()}, this, regProps);</code> -
 * <code>ManagedService</code> interface is not a Device Function interface;</li>
 * <li>
 * <code>context.registerService(new String[] {DeviceFunction.class.getName(),
 * BinaryControl.class.getName()}, this, regProps);</code> -
 * <code>DeviceFunction</code> interface is not concrete Device Function.</li>
 * </ul>
 * That one is valid <code>context.registerService(new String[]
 * {Meter.class.getName(), BinaryControl.class.getName()}, this, regProps);</code>. <code>Meter</code> and <code>BinaryControl</code> are concrete Device
 * Function interfaces. That rule helps to the applications to find all
 * supported Device Function classes. Otherwise the Device Function services can
 * be accesses, but it's not clear which are the Device Function classes.
 * <p>
 * The Device Function properties must be integrated according to these rules:
 * <ul>
 * <li>getter methods must be available for all properties with
 * {@link #META_INFO_PROPERTY_ACCESS_READABLE} access;</li>
 * <li>setter methods must be available for all properties with
 * {@link #META_INFO_PROPERTY_ACCESS_WRITABLE} access;</li>
 * <li>no methods are required for properties with
 * {@link #META_INFO_PROPERTY_ACCESS_EVENTABLE} access.</li>
 * </ul>
 * The accessor methods must be defined according JavaBeans specification.
 * <p>
 * The Device Function operations are java methods, which cannot override the
 * property accessor methods. They can have zero or more input arguments and
 * zero or one output argument.
 * 
 * <p>
 * Operation arguments share the same metadata with Device Function properties.
 * The data type can be one of the following types:
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
 * The properties and the operation arguments have some common metadata. It's
 * provided with:
 * <ul>
 * <li>{@link #META_INFO_DESCRIPTION}</li>
 * <li>{@link #META_INFO_UNIT}</li>
 * <li>{@link #META_INFO_MIN_VALUE}</li>
 * <li>{@link #META_INFO_MAX_VALUE}</li>
 * <li>{@link #META_INFO_RESOLUTION}</li>
 * <li>{@link #META_INFO_VALUES}</li>
 * </ul>
 * 
 * The access to the Device Function properties is a bitmap value of
 * {@link #META_INFO_PROPERTY_ACCESS} meta data key. Device Function properties
 * can be accessed in three ways. Any combinations between them are possible:
 * <ul>
 * <li>
 * {@link #META_INFO_PROPERTY_ACCESS_READABLE} - available for all properties,
 * which can be read. Device Function must provide a getter method for an access
 * to the property value.</li>
 * <li>
 * {@link #META_INFO_PROPERTY_ACCESS_WRITABLE} - available for all properties,
 * which can be modified. Device Function must provide a setter method for a
 * modification of the property value.</li>
 * <li>
 * {@link #META_INFO_PROPERTY_ACCESS_EVENTABLE} - available for all properties,
 * which can report the property value. {@link DeviceFunctionEvent}s are sent on
 * property change.</li>
 * </ul>
 * 
 * In order to provide common behavior, all Device Functions must follow a set
 * of common rules related to the implementation of their setters, getters,
 * operations and events:
 * <ul>
 * <li>
 * The setter method must be executed synchronously. If the underlying protocol
 * can return response to the setter call, it must be awaited. It simplifies the
 * property value modifications and doesn't require asynchronous call back.</li>
 * <li>
 * The operation method must be executed synchronously. If the underlying
 * protocol can return an operation confirmation or response, they must be
 * awaited. It simplifies the operation execution and doesn't require
 * asynchronous call back.</li>
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
	 * Marks the readable Device Function properties. The flag can be used as a
	 * part of bitmap value of {@link #META_INFO_PROPERTY_ACCESS}. The readable
	 * access mandates Device Function to provide a property getter method.
	 */
	public static final int META_INFO_PROPERTY_ACCESS_READABLE = 1;

	/**
	 * Marks the writable Device Function properties. The flag can be used as a
	 * part of bitmap value of {@link #META_INFO_PROPERTY_ACCESS}. The writable
	 * access mandates Device Function to provide a property setter method.
	 */
	public static final int META_INFO_PROPERTY_ACCESS_WRITABLE = 2;

	/**
	 * Marks the eventable Device Function properties. The flag can be used as a
	 * part of bitmap value of {@link #META_INFO_PROPERTY_ACCESS}.
	 */
	public static final int META_INFO_PROPERTY_ACCESS_EVENTABLE = 4;

	/**
	 * Meta data key, which value represents the Device Function property, the
	 * operation argument or operation description. The property value type is
	 * <code>java.lang.String</code>.
	 * 
	 * @see #getPropertyMetaData(String)
	 * @see #getOperationMetaData(String)
	 */
	public static final String META_INFO_DESCRIPTION = "description";

	/**
	 * Meta data key, which value represents the Device Function property or the
	 * operation argument unit. The property value type is
	 * <code>java.lang.String</code>. These rules must be applied to unify the
	 * representation:
	 * <ul>
	 * <li>SI units (The International System of Units) must be used where it's
	 * applicable.</li>
	 * <li>The unit must use Unicode symbols normalized with NFKD (Compatibility
	 * Decomposition) normalization form. (see Unicode Standard Annex #15,
	 * Unicode Normalization Forms)</li>
	 * </ul>
	 * For example, degrees Celsius will not be represent as U+2103 (degree
	 * celsius), but will be U+00B0 degree sign + U+0043 latin capital letter c.
	 * 
	 * @see #getPropertyMetaData(String)
	 */
	public static final String META_INFO_UNIT = "unit";

	/**
	 * Meta data key, which value represents the access to the Device Function
	 * property. The property value is a bitmap of <code>Integer</code> type.
	 * The bitmap can be any combination of:
	 * <ul>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS_READABLE}</li>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS_WRITABLE}</li>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS_EVENTABLE}</li>
	 * </ul>
	 * For example, value Integer(3) means that the property is readable and
	 * writable, but not eventable.
	 * 
	 * @see #getPropertyMetaData(String)
	 */
	public static final String META_INFO_PROPERTY_ACCESS = "property.access";

	/**
	 * Meta data key, which value represents the Device Function property or the
	 * operation argument minimum value. The property value type depends on the
	 * property or argument type.
	 * 
	 * @see #getPropertyMetaData(String)
	 */
	public static final String META_INFO_MIN_VALUE = "min.value";

	/**
	 * Meta data key, which value represents the Device Function property or the
	 * operation argument maximum value. The property value type depends on the
	 * property or argument type.
	 * 
	 * @see #getPropertyMetaData(String)
	 */
	public static final String META_INFO_MAX_VALUE = "max.value";

	/**
	 * Meta data key, which value represents the resolution value of specific
	 * range of the Device Function property or the operation argument. The
	 * property value type depends on the property or argument type. For
	 * example, if the range is [0, 100], the resolution can be 10. That's the
	 * difference between two values in series.
	 * 
	 * @see #getPropertyMetaData(String)
	 */
	public static final String META_INFO_RESOLUTION = "resolution";

	/**
	 * Meta data key, which value represents the Device Function property or the
	 * operation argument possible values. The property value type is
	 * <code>java.util.Map</code>, where the keys are the possible values and
	 * the values are their string representations.
	 * 
	 * @see #getPropertyMetaData(String)
	 */
	public static final String META_INFO_VALUES = "values";

	/**
	 * Meta data key prefix, which key value represents the operation input
	 * argument metadata. The property value type is <code>java.util.Map</code>.
	 * The value map key can be one of:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>{@link #META_INFO_UNIT}</li>
	 * <li>{@link #META_INFO_MIN_VALUE}</li>
	 * <li>{@link #META_INFO_MAX_VALUE}</li>
	 * <li>{@link #META_INFO_RESOLUTION}</li>
	 * <li>{@link #META_INFO_VALUES}</li>
	 * <li>custom key</li>
	 * </ul>
	 * The prefix must be used in the form:
	 * <p>
	 * operation input argument name ::= value of
	 * {@link #META_INFO_OPERATION_ARGS_IN_PREFIX}argument-index
	 * <p>
	 * argument-index - input argument index. For example,
	 * device.function.operation.arguments.in.1 can be used for the first
	 * operation input argument.
	 * 
	 * 
	 * @see #getOperationMetaData(String)
	 */
	public static final String META_INFO_OPERATION_ARGS_IN_PREFIX = "operation.arguments.in.";

	/**
	 * Meta data key, which value represents the operation output argument
	 * metadata. The property value type is <code>java.util.Map</code>. The
	 * value map key can be one of:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>{@link #META_INFO_UNIT}</li>
	 * <li>{@link #META_INFO_MIN_VALUE}</li>
	 * <li>{@link #META_INFO_MAX_VALUE}</li>
	 * <li>{@link #META_INFO_RESOLUTION}</li>
	 * <li>{@link #META_INFO_VALUES}</li>
	 * <li>custom key</li>
	 * </ul>
	 * 
	 * @see #getOperationMetaData(String)
	 */
	public static final String META_INFO_OPERATION_ARG_OUT = "operation.argument.out";

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
	 * device-id - the value of the {@link Device#PROPERTY_UID} Functional
	 * Device service property
	 * <p>
	 * function-id - device function identifier in the scope of the device
	 */
	public static final String PROPERTY_UID = "device.function.UID";

	/**
	 * The service property value contains the function device unique
	 * identifier. The function belongs to this device. It's an optional
	 * property. The value type is <code>java.lang.String</code>.
	 */
	public static final String PROPERTY_DEVICE_UID = "device.function.device.UID";

	/**
	 * The service property value contains the reference device function unique
	 * identifiers. It's an optional property. The value type is
	 * <code>java.lang.String[]</code>. The property value cannot be set. It can
	 * be used to represent different relationships between the device
	 * functions.
	 */
	public static final String PROPERTY_REFERENCE_UIDS = "functional.device.reference.UIDs";

	/**
	 * The service property value contains the device function description. It's
	 * an optional property. The value type is <code>java.lang.String</code>.
	 */
	public static final String PROPERTY_DESCRIPTION = "device.function.description";

	/**
	 * The service property value contains the device function operation names.
	 * It's an optional property. The value type is
	 * <code>java.lang.String[]</code>. It's not possible to exist two or more
	 * Device Function operations with the same name i.e. the operation
	 * overloading is not allowed.
	 */
	public static final String PROPERTY_OPERATION_NAMES = "device.function.operation.names";

	/**
	 * The service property value contains the device function property names.
	 * It's an optional property. The value type is
	 * <code>java.lang.String[]</code>. It's not possible to exist two or more
	 * Device Function properties with the same name.
	 */
	public static final String PROPERTY_PROPERTY_NAMES = "device.function.property.names";

	/**
	 * Provides meta data about the given function property. The keys of the
	 * <code>java.util.Map</code> result must be of
	 * <code>java.lang.String</code> type. Possible keys:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS}</li>
	 * <li>{@link #META_INFO_UNIT}</li>
	 * <li>{@link #META_INFO_MIN_VALUE}</li>
	 * <li>{@link #META_INFO_MAX_VALUE}</li>
	 * <li>{@link #META_INFO_RESOLUTION}</li>
	 * <li>{@link #META_INFO_VALUES}</li>
	 * <li>custom key</li>
	 * </ul>
	 * 
	 * <p>
	 * This method must continue to return the operation names after the device
	 * service has been unregistered.
	 * 
	 * @param propertyName
	 *            The function property name, which meta data is requested.
	 * 
	 * @return The property meta data for the given property name.
	 *         <code>null</code> if the property meta data is not supported.
	 * @throws IllegalArgumentException
	 *             If the function property with the specified name is not
	 *             supported.
	 */
	public Map getPropertyMetaData(String propertyName)
			throws IllegalArgumentException;

	/**
	 * Provides meta data about the given function operation. The keys of the
	 * <code>java.util.Map</code> result must be of
	 * <code>java.lang.String</code> type. Possible keys:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION}</li>
	 * <li>{@link #META_INFO_OPERATION_ARG_OUT}</li>
	 * <li>Different input arguments with prefix
	 * {@link #META_INFO_OPERATION_ARGS_IN_PREFIX}</li>
	 * <li>custom key</li>
	 * </ul>
	 * 
	 * <p>
	 * This method must continue to return the operation names after the device
	 * service has been unregistered.
	 * 
	 * @param operationName
	 *            The function operation name, which meta data is requested.
	 * 
	 * @return The operation meta data for the given operation name.
	 *         <code>null</code> if the operation meta data is not supported.
	 * @throws IllegalArgumentException
	 *             If the function operation with the specified name is not
	 *             supported.
	 */
	public Map getOperationMetaData(String operationName)
			throws IllegalArgumentException;

}
