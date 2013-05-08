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

package org.osgi.services.functionaldevice;

import java.util.Dictionary;
import java.util.Map;
import org.osgi.service.event.Event;

/**
 * Asynchronous event, which marks a device function property value
 * modification. The event can be triggered when there is a new property value,
 * but it's possible to have events in series with no value change. The event
 * properties must contain all device properties and:
 * <ul>
 * <li>{@link #PROPERTY_DEVICE_FUNCTION} - the event source function.</li>
 * <li>{@link #PROPERTY_DEVICE_FUNCTION_PROPERTY_NAME} - the property name.</li>
 * <li>{@link #PROPERTY_DEVICE_FUNCTION_PROPERTY_VALUE} - the property value.</li>
 * </ul>
 */
public final class DeviceFunctionEvent extends Event {

	/**
	 * Represents the event package. That constant can be useful for the event
	 * handlers depending on the event filters.
	 */
	public static final String	EVENT_PACKAGE							= "org/osgi/services/abstractdevice/";

	/**
	 * Represents the event class. That constant can be useful for the event
	 * handlers depending on the event filters.
	 */
	public static final String	EVENT_CLASS								= EVENT_PACKAGE + "DeviceFunctionEvent/";

	/**
	 * Represents the event topic for the device function property changed.
	 */
	public static final String	TOPIC_PROPERTY_CHANGED					= EVENT_CLASS + "PROPERTY_CHANGED";

	/**
	 * Represents an event property key for the device function. The property
	 * value type is <code>java.lang.String</code>. The value represents the
	 * property value change source function.
	 */
	public static final String	PROPERTY_DEVICE_FUNCTION				= "device.function";

	/**
	 * Represents an event property key for the device function property name.
	 * The property value type is <code>java.lang.String</code>. The value
	 * represents the property name.
	 */
	public static final String	PROPERTY_DEVICE_FUNCTION_PROPERTY_NAME	= "device.function.property.name";

	/**
	 * Represents an event property key for the device function property value.
	 * The property value type depends on the property type. The value
	 * represents the property value.
	 */
	public static final String	PROPERTY_DEVICE_FUNCTION_PROPERTY_VALUE	= "device.function.property.value";

	/**
	 * Constructs a new event with the specified topic and properties.
	 * 
	 * @param topic The event topic.
	 * @param properties The event properties.
	 */
	public DeviceFunctionEvent(String topic, Dictionary properties) {
		// TODO Auto-generated constructor stub
		super(topic, properties);
	}

	/**
	 * Constructs a new event with the specified topic and properties.
	 * 
	 * @param topic The event topic.
	 * @param properties The event properties.
	 */
	public DeviceFunctionEvent(String topic, Map properties) {
		// TODO Auto-generated constructor stub
		super(topic, properties);
	}

	/**
	 * Returns the property value change source function. The value is same as
	 * the value of {@link #PROPERTY_DEVICE_FUNCTION} property.
	 * 
	 * @return The property value change source function.
	 */
	public String getFunction() {
		return null;
		// TODO: impl
	}

	/**
	 * Returns the property name. The value is same as the value of
	 * {@link #PROPERTY_DEVICE_FUNCTION_PROPERTY_NAME}.
	 * 
	 * @return The property name.
	 */
	public String getPropertyName() {
		return null;
		// TODO: impl
	}

	/**
	 * Returns the property value. The value is same as the value of
	 * {@link #PROPERTY_DEVICE_FUNCTION_PROPERTY_VALUE}.
	 * 
	 * @return The property value.
	 */
	public Object getPropertyValue() {
		return null;
		// TODO: impl
	}

}
