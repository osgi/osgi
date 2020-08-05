/*
 * Copyright (c) OSGi Alliance (2013, 2020). All Rights Reserved.
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
 * Asynchronous event, which marks a function property value modification. The
 * event can be triggered when there is a new property value, but it's possible
 * to have events in series with no value change. The event properties must
 * contain:
 * <ul>
 * <li>{@link #FUNCTION_UID} - the event source function unique identifier.</li>
 * <li>{@link #PROPERTY_NAME} - the property name.</li>
 * <li>{@link #PROPERTY_VALUE} - the property value. The property value type
 * must be a subclass of FunctionData.</li>
 * </ul>
 */
public class FunctionEvent extends Event {

	/**
	 * Represents the event package. That constant can be useful for the event
	 * handlers depending on the event filters.
	 */
	public static final String	EVENT_PACKAGE			= "org/osgi/service/dal/";

	/**
	 * Represents the event class. That constant can be useful for the event
	 * handlers depending on the event filters.
	 */
	public static final String	EVENT_CLASS				= EVENT_PACKAGE + "FunctionEvent/";

	/**
	 * Represents the event topic for the function property changed.
	 */
	public static final String	TOPIC_PROPERTY_CHANGED	= EVENT_CLASS + "PROPERTY_CHANGED";

	/**
	 * Represents an event property key for function UID. The property value
	 * type is {@code java.lang.String}. The value represents the property value
	 * change source function identifier.
	 */
	public static final String	FUNCTION_UID			= "dal.function.UID";

	/**
	 * Represents an event property key for the function property name. The
	 * property value type is {@code java.lang.String}. The value represents the
	 * property name.
	 */
	public static final String	PROPERTY_NAME			= "dal.function.property.name";

	/**
	 * Represents an event property key for the function property value. The
	 * property value type is a subclass of {@code FunctionData}. The value
	 * represents the property value.
	 */
	public static final String	PROPERTY_VALUE			= "dal.function.property.value";

	/**
	 * Constructs a new event with the specified topic and properties.
	 * 
	 * @param topic The event topic.
	 * @param properties The event properties.
	 */
	public FunctionEvent(String topic, Dictionary<String, ? > properties) {
		super(topic, properties);
	}

	/**
	 * Constructs a new event with the specified topic and properties.
	 * 
	 * @param topic The event topic.
	 * @param properties The event properties.
	 */
	public FunctionEvent(String topic, Map<String, ? > properties) {
		super(topic, properties);
	}

	/**
	 * Constructs a new event with the specified topic, function UID, property
	 * name and property value.
	 * 
	 * @param topic The event topic.
	 * @param functionUID The event source function UID.
	 * @param propName The event source property name.
	 * @param propValue The event source property value.
	 */
	public FunctionEvent(
			String topic,
			String functionUID,
			String propName,
			FunctionData propValue) {
		super(topic, prepareEventProps(functionUID, propName, propValue));
	}

	/**
	 * Returns the property value change source function identifier. The value
	 * is same as the value of {@link #FUNCTION_UID} property.
	 * 
	 * @return The property value change source function.
	 */
	public String getFunctionUID() {
		return (String) super.getProperty(FUNCTION_UID);
	}

	/**
	 * Returns the property name. The value is same as the value of
	 * {@link #PROPERTY_NAME}.
	 * 
	 * @return The property name.
	 */
	public String getFunctionPropertyName() {
		return (String) super.getProperty(PROPERTY_NAME);
	}

	/**
	 * Returns the property value. The value is same as the value of
	 * {@link #PROPERTY_VALUE}.
	 * 
	 * @return The property value.
	 */
	public FunctionData getFunctionPropertyValue() {
		return (FunctionData) super.getProperty(PROPERTY_VALUE);
	}

	private static Map<String, ? > prepareEventProps(
			String funtionUID,
			String propName,
			FunctionData propValue) {
		Map<String,Object> eventProps = new HashMap<>(3, 1F);
		eventProps.put(PROPERTY_NAME, propName);
		eventProps.put(PROPERTY_VALUE, propValue);
		eventProps.put(FUNCTION_UID, funtionUID);
		return eventProps;
	}
}
