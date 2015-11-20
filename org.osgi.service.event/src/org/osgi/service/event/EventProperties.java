/*
 * Copyright (c) OSGi Alliance (2010, 2015). All Rights Reserved.
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

package org.osgi.service.event;

import static org.osgi.service.event.EventConstants.EVENT_TOPIC;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The properties for an {@link Event}. An event source can create an
 * EventProperties object if it needs to reuse the same event properties for
 * multiple events.
 * 
 * <p>
 * The keys are all of type {@code String}. The values are of type
 * {@code Object}. The key &quot;event.topics&quot; is ignored as event topics
 * can only be set when an {@link Event} is constructed.
 * 
 * <p>
 * Once constructed, an EventProperties object is unmodifiable. However, the
 * values of the map used to construct an EventProperties object are still
 * subject to modification as they are not deeply copied.
 * 
 * @Immutable
 * @since 1.3
 * @author $Id$
 */
public class EventProperties implements Map<String, Object> {
	/**
	 * The properties for an event. Keys are strings and values are objects. The
	 * object is unmodifiable.
	 */
	private final Map<String, Object>	properties;

	/**
	 * Create an EventProperties from the specified properties.
	 * 
	 * <p>
	 * The specified properties will be copied into this EventProperties.
	 * Properties whose key is not of type {@code String} will be ignored. A
	 * property with the key &quot;event.topics&quot; will be ignored.
	 * 
	 * @param properties The properties to use for this EventProperties object
	 *        (may be {@code null}).
	 */
	public EventProperties(Map<String, ?> properties) {
		int size = (properties == null) ? 0 : properties.size();
		Map<String, Object> p = new HashMap<String, Object>(size);
		if (size > 0) {
			for (Object key : (Set<?>) properties.keySet()) {
				if ((key instanceof String) && !EVENT_TOPIC.equals(key)) {
					Object value = properties.get(key);
					p.put((String) key, value);
				}
			}
		}
		// safely publish the map
		this.properties = Collections.unmodifiableMap(p);
	}

	/**
	 * Create an EventProperties from the specified dictionary.
	 * 
	 * <p>
	 * The specified properties will be copied into this EventProperties.
	 * Properties whose key is not of type {@code String} will be ignored. A
	 * property with the key &quot;event.topics&quot; will be ignored.
	 * 
	 * @param properties The properties to use for this EventProperties object
	 *        (may be {@code null}).
	 */
	EventProperties(Dictionary<String, ?> properties) {
		int size = (properties == null) ? 0 : properties.size();
		Map<String, Object> p = new HashMap<String, Object>(size);
		if (size > 0) {
			for (Enumeration<?> e = properties.keys(); e.hasMoreElements();) {
				Object key = e.nextElement();
				if ((key instanceof String) && !EVENT_TOPIC.equals(key)) {
					Object value = properties.get(key);
					p.put((String) key, value);
				}
			}
		}
		// safely publish the map
		this.properties = Collections.unmodifiableMap(p);
	}

	/**
	 * This method throws {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException if called.
	 */
	@Override
	public void clear() {
		properties.clear();
	}

	/**
	 * Indicates if the specified property is present.
	 * 
	 * @param name The property name.
	 * @return {@code true} If the property is present, {@code false} otherwise.
	 */
	@Override
	public boolean containsKey(Object name) {
		return properties.containsKey(name);
	}

	/**
	 * Indicates if the specified value is present.
	 * 
	 * @param value The property value.
	 * @return {@code true} If the value is present, {@code false} otherwise.
	 */
	@Override
	public boolean containsValue(Object value) {
		return properties.containsValue(value);
	}

	/**
	 * Return the property entries.
	 * 
	 * @return A set containing the property name/value pairs.
	 */
	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return properties.entrySet();
	}

	/**
	 * Return the value of the specified property.
	 * 
	 * @param name The name of the specified property.
	 * @return The value of the specified property.
	 */
	@Override
	public Object get(Object name) {
		return properties.get(name);
	}

	/**
	 * Indicate if this properties is empty.
	 * 
	 * @return {@code true} If this properties is empty, {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean isEmpty() {
		return properties.isEmpty();
	}

	/**
	 * Return the names of the properties.
	 * 
	 * @return The names of the properties.
	 */
	@Override
	public Set<String> keySet() {
		return properties.keySet();
	}

	/**
	 * This method throws {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException if called.
	 */
	@Override
	public Object put(String key, Object value) {
		return properties.put(key, value);
	}

	/**
	 * This method throws {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException if called.
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		properties.putAll(map);
	}

	/**
	 * This method throws {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException if called.
	 */
	@Override
	public Object remove(Object key) {
		return properties.remove(key);
	}

	/**
	 * Return the number of properties.
	 * 
	 * @return The number of properties.
	 */
	@Override
	public int size() {
		return properties.size();
	}

	/**
	 * Return the properties values.
	 * 
	 * @return The values of the properties.
	 */
	@Override
	public Collection<Object> values() {
		return properties.values();
	}

	/**
	 * Compares this {@code EventProperties} object to another object.
	 * 
	 * <p>
	 * The properties are compared using the {@code java.util.Map.equals()}
	 * rules which includes identity comparison for array values.
	 * 
	 * @param object The {@code EventProperties} object to be compared.
	 * @return {@code true} if {@code object} is a {@code EventProperties} and
	 *         is equal to this object; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof EventProperties)) {
			return false;
		}
		EventProperties other = (EventProperties) object;
		return properties.equals(other.properties);
	}

	/**
	 * Returns a hash code value for this object.
	 * 
	 * @return An integer which is a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return properties.hashCode();
	}

	/**
	 * Returns the string representation of this object.
	 * 
	 * @return The string representation of this object.
	 */
	@Override
	public String toString() {
		return properties.toString();
	}
}
