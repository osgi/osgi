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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.event.Event;

/**
 * Asynchronous event, which marks a Device Function property value
 * modification. The event can be triggered when there is a new property value,
 * but it's possible to have events in series with no value change. The event
 * properties must contain:
 * <ul>
 * <li>{@link #PROPERTY_FUNCTION_UID} - the event source function unique
 * identifier.</li>
 * <li>{@link #PROPERTY_FUNCTION_PROPERTY_NAME} - the property name.</li>
 * <li>{@link #PROPERTY_FUNCTION_PROPERTY_VALUE} - the property value. The
 * property value type must be a subclass of DeviceFunctionData.</li>
 * </ul>
 */
public final class DeviceFunctionEvent extends Event {

	/**
	 * Represents the event package. That constant can be useful for the event
	 * handlers depending on the event filters.
	 */
	public static final String	EVENT_PACKAGE						= "org/osgi/services/dal/";

	/**
	 * Represents the event class. That constant can be useful for the event
	 * handlers depending on the event filters.
	 */
	public static final String	EVENT_CLASS							= EVENT_PACKAGE
																			+ "DeviceFunctionEvent/";

	/**
	 * Represents the event topic for the Device Function property changed.
	 */
	public static final String	TOPIC_PROPERTY_CHANGED				= EVENT_CLASS
																			+ "PROPERTY_CHANGED";

	/**
	 * Represents an event property key for Device Function UID. The property
	 * value type is <code>java.lang.String</code>. The value represents the
	 * property value change source function identifier.
	 */
	public static final String	PROPERTY_FUNCTION_UID				= "dal.function.UID";

	/**
	 * Represents an event property key for the Device Function property name.
	 * The property value type is <code>java.lang.String</code>. The value
	 * represents the property name.
	 */
	public static final String	PROPERTY_FUNCTION_PROPERTY_NAME		= "dal.function.property.name";

	/**
	 * Represents an event property key for the Device Function property value.
	 * The property value type is a subclass of <code>DeviceFunctionData</code>.
	 * The value represents the property value.
	 */
	public static final String	PROPERTY_FUNCTION_PROPERTY_VALUE	= "dal.function.property.value";

	/**
	 * Constructs a new event with the specified topic and properties.
	 * 
	 * @param topic The event topic.
	 * @param properties The event properties.
	 */
	public DeviceFunctionEvent(String topic, Dictionary properties) {
		super(topic, properties);
	}

	/**
	 * Constructs a new event with the specified topic and properties.
	 * 
	 * @param topic The event topic.
	 * @param properties The event properties.
	 */
	public DeviceFunctionEvent(String topic, Map properties) {
		super(topic, properties);
	}

	/**
	 * Constructs a new event with the specified topic, function UID, property
	 * name and property value.
	 * 
	 * @param topic The event topic.
	 * @param funtionUID The event source function UID.
	 * @param propName The event source property name.
	 * @param propValue The event source property value.
	 */
	public DeviceFunctionEvent(
			String topic,
			String funtionUID,
			String propName,
			DeviceFunctionData propValue) {
		super(topic, prepareEventProps(funtionUID, propName, propValue));
	}

	/**
	 * Returns the property value change source function identifier. The value
	 * is same as the value of {@link #PROPERTY_FUNCTION_UID} property.
	 * 
	 * @return The property value change source function.
	 */
	public String getFunctionUID() {
		return (String) super.getProperty(PROPERTY_FUNCTION_UID);
	}

	/**
	 * Returns the property name. The value is same as the value of
	 * {@link #PROPERTY_FUNCTION_PROPERTY_NAME}.
	 * 
	 * @return The property name.
	 */
	public String getFunctionPropertyName() {
		return (String) super.getProperty(PROPERTY_FUNCTION_PROPERTY_NAME);
	}

	/**
	 * Returns the property value. The value is same as the value of
	 * {@link #PROPERTY_FUNCTION_PROPERTY_VALUE}.
	 * 
	 * @return The property value.
	 */
	public DeviceFunctionData getFunctionPropertyValue() {
		return (DeviceFunctionData) super.getProperty(PROPERTY_FUNCTION_PROPERTY_VALUE);
	}

	private static Map prepareEventProps(
			String funtionUID,
			String propName,
			DeviceFunctionData propValue) {
		Map eventProps = new HashMap(3, 1F);
		eventProps.put(PROPERTY_FUNCTION_PROPERTY_NAME, propName);
		eventProps.put(PROPERTY_FUNCTION_PROPERTY_VALUE, propValue);
		eventProps.put(PROPERTY_FUNCTION_UID, funtionUID);
		return eventProps;
	}

}
