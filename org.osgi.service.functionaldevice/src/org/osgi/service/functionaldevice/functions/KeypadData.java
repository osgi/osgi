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

package org.osgi.service.functionaldevice.functions;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a keypad event data that is collected when a change with some key
 * from device keypad has occurred.
 * 
 * @see Keypad
 */
public class KeypadData {

	/** Represents a keypad event type for a key pressed. */
	public static final int EVENT_TYPE_PRESSED = 1;

	/** Represents a keypad event type for a long key pressed. */
	public static final int EVENT_TYPE_PRESSED_LONG = 2;

	/** Represents a keypad event type for a double key pressed. */
	public static final int EVENT_TYPE_PRESSED_DOUBLE = 3;

	/** Represents a keypad event type for a double and long key pressed. */
	public static final int EVENT_TYPE_PRESSED_DOUBLE_LONG = 4;

	/** Represents a keypad event type for a key released. */
	public static final int EVENT_TYPE_RELEASED = 5;

	/**
	 * Represents the keypad event type. The vendor can define own event types
	 * with negative values. The immutable field is accessible with
	 * {@link #getEventType()} getter.
	 */
	public final int eventType;

	/**
	 * Represents the key code. The immutable field is accessible with
	 * {@link #getKeyCode()} getter.
	 */
	public final int keyCode;

	/**
	 * Represents the key name, if it's available. The immutable field is
	 * accessible with {@link #getKeyName()} getter.
	 */
	public final String keyName;

	/**
	 * Represents <code>KeypadData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. If possible, the value should be
	 * provided by the device, otherwise the device driver can generate that
	 * info. {@link java.lang.Long#MIN_VALUE} value means no timestamp. The
	 * immutable field is accessible with {@link #getTimestamp()} getter.
	 */
	public final long timestamp;

	/**
	 * Represents <code>KeypadData</code> metadata in an unmodifiable
	 * <code>Map</code>. The immutable field is accessible with
	 * {@link #getMetadata()} getter.
	 */
	public final Map metadata;

	/**
	 * Constructs new <code>KeypadData</code> instance with the specified
	 * arguments.
	 * 
	 * @param eventType
	 *            The event type.
	 * @param keyCode
	 *            The key code.
	 * @param keyName
	 *            The key name, if available.
	 * @param timestamp
	 *            The event timestamp.
	 * @param metadata
	 *            The <code>KeypadData</code> metadata.
	 */
	public KeypadData(int eventType, int keyCode, String keyName,
			long timestamp, Map metadata) {
		this.eventType = eventType;
		this.keyCode = keyCode;
		this.keyName = keyName;
		this.timestamp = timestamp;
		this.metadata = (null == metadata) ? null : Collections
				.unmodifiableMap(metadata);
	}

	/**
	 * Returns the event type. The vendor can define own event types with
	 * negative values.
	 * 
	 * @return The event type.
	 */
	public int getEventType() {
		return this.eventType;
	}

	/**
	 * The code of the key. This field is mandatory and it holds the
	 * semantics(meaning) of the key.
	 * 
	 * @return The key code.
	 */
	public int getKeyCode() {
		return this.keyCode;
	}

	/**
	 * Represents a human readable name of the corresponding key code. This
	 * field is optional and sometimes it could be missed(might be
	 * <code>null</code>).
	 * 
	 * @return A string with the name of the key or <code>null</code> if not
	 *         specified.
	 */
	public String getKeyName() {
		return this.keyName;
	}

	/**
	 * Returns <code>KeypadData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. If possible, the value should be
	 * provided by the device, otherwise the device driver can generate that
	 * info. {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 * 
	 * @return <code>KeypadData</code> timestamp.
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns <code>KeypadData</code> metadata.
	 * 
	 * @return <code>KeypadData</code> metadata.
	 */
	public Map getMetadata() {
		return this.metadata;
	}

}
