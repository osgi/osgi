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

package org.osgi.service.dal.functions.data;

import java.util.Map;
import org.osgi.service.dal.DeviceFunctionData;
import org.osgi.service.dal.functions.Keypad;

/**
 * Represents a keypad event data that is collected when a change with some key
 * from device keypad has occurred. The key code is mapped to
 * <code>DeviceFunctionData</code> value.
 * 
 * @see Keypad
 * @see DeviceFunctionData
 */
public class KeypadData extends DeviceFunctionData {

	/**
	 * Represents the key name field name. The field value is available with
	 * {@link #keyName} and {@link #getKeyName()}. The field type is
	 * <code>String</code>. The constant can be used as a key to
	 * {@link #KeypadData(Map)}.
	 */
	public static final String	FIELD_KEY_NAME					= "keyName";

	/**
	 * Represents the event type field name. The field value is available with
	 * {@link #eventType} and {@link #getEventType()}. The field type is
	 * <code>int</code>. The constant can be used as a key to
	 * {@link #KeypadData(Map)}.
	 */
	public static final String	FIELD_EVENT_TYPE				= "eventType";

	/**
	 * Represents the key code field name. The field value is available with
	 * {@link #keyCode} and {@link #getKeyCode()}. The field type is
	 * <code>int</code>. The constant can be used as a key to
	 * {@link #KeypadData(Map)}.
	 */
	public static final String	FIELD_KEY_CODE					= "keyCode";

	/** Represents an unknown keypad event type. */
	public static final int		EVENT_TYPE_UNKNOWN				= 0;

	/** Represents a keypad event type for a key pressed. */
	public static final int	EVENT_TYPE_PRESSED				= 1;

	/** Represents a keypad event type for a long key pressed. */
	public static final int	EVENT_TYPE_PRESSED_LONG			= 2;

	/** Represents a keypad event type for a double key pressed. */
	public static final int	EVENT_TYPE_PRESSED_DOUBLE		= 3;

	/** Represents a keypad event type for a double and long key pressed. */
	public static final int	EVENT_TYPE_PRESSED_DOUBLE_LONG	= 4;

	/** Represents a keypad event type for a key released. */
	public static final int	EVENT_TYPE_RELEASED				= 5;

	/**
	 * Represents the keypad event type. The vendor can define own event types
	 * with negative values. The field is accessible with
	 * {@link #getEventType()} getter.
	 */
	public final int		eventType;

	/**
	 * Represents the key name, if it's available. The field is accessible with
	 * {@link #getKeyName()} getter.
	 */
	public final String		keyName;

	/**
	 * Represents the key code. This field is mandatory and it holds the
	 * semantics(meaning) of the key. The field is accessible with
	 * {@link #getKeyCode()} getter.
	 */
	public final int		keyCode;

	/**
	 * Constructs new <code>KeypadData</code> instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"eventType"=Integer(1)...}. That map will initialize the
	 * {@link #FIELD_EVENT_TYPE} field with 1.
	 * <p>
	 * {@link #FIELD_EVENT_TYPE} field value type must be <code>Integer</code>.
	 * {@link #FIELD_KEY_CODE} field value type must be <code>Integer</code>.
	 * {@link #FIELD_KEY_NAME} field value type must be <code>String</code>.
	 * 
	 * @param fields Contains the new <code>KeypadData</code> instance field
	 *        values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws IllegalArgumentException If the event type or key code is
	 *         missing.
	 * @throws NullPointerException If the fields map is <code>null</code>.
	 */
	public KeypadData(final Map fields) {
		super(fields);
		final Integer eventTypeLocal = (Integer) fields.get(FIELD_EVENT_TYPE);
		if (null == eventTypeLocal) {
			throw new IllegalArgumentException("The event type is missing.");
		}
		this.eventType = eventTypeLocal.intValue();

		final Integer keyCodeLocal = (Integer) fields.get(FIELD_KEY_CODE);
		if (null == keyCodeLocal) {
			throw new IllegalArgumentException("The key code is missing.");
		}
		this.keyCode = keyCodeLocal.intValue();

		this.keyName = (String) fields.get(FIELD_KEY_NAME);
	}

	/**
	 * Constructs new <code>KeypadData</code> instance with the specified
	 * arguments.
	 * 
	 * @param timestamp The data timestamp.
	 * @param metadata The data metadata.
	 * @param eventType The data event type.
	 * @param keyCode The data key code.
	 * @param keyName The data key name.
	 */
	public KeypadData(long timestamp, Map metadata, int eventType, int keyCode, String keyName) {
		super(timestamp, metadata);
		this.eventType = eventType;
		this.keyCode = keyCode;
		this.keyName = keyName;
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
	 * Two <code>KeypadData</code> instances are equal if they contain equal
	 * metadata, timestamp, event type, key code and key name.
	 * 
	 * @param other The object to compare this data.
	 * 
	 * @return <code>true</code> if this object is equivalent to the specified
	 *         one.
	 * 
	 * @see org.osgi.service.dal.DeviceFunctionData#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof KeypadData)) {
			return false;
		}
		return 0 == compareToKeypadData((KeypadData) other);
	}

	/**
	 * Returns the hash code for this <code>KeypadData</code> object. The hash
	 * code is a sum of {@link DeviceFunctionData#hashCode()},
	 * {@link String#hashCode()}, event type and key code, where
	 * {@link String#hashCode()} represents the key name hash code if available.
	 * 
	 * @return The hash code of this <code>LevelData</code> object.
	 * 
	 * @see org.osgi.service.dal.DeviceFunctionData#hashCode()
	 */
	public int hashCode() {
		int hashCode = super.hashCode();
		if (null != this.keyName) {
			hashCode += this.keyName.hashCode();
		}
		return hashCode + this.eventType + this.keyCode;
	}

	/**
	 * Compares this <code>KeypadData</code> instance with the given argument.
	 * The argument can be:
	 * <ul>
	 * <li><code>KeypadData</code> - the method returns <code>-1</code> if
	 * metadata, timestamp, event type, key code or key name are not equivalent.
	 * 0 if all fields are equivalent.</li>
	 * <li><code>Map</code> - the map must be built according the rules of
	 * {@link #KeypadData(Map)}. Metadata, timestamp, event type, key code and
	 * key name are compared according <code>KeypadData</code> argument rules.</li>
	 * </ul>
	 * 
	 * @param o An argument to be compared.
	 * 
	 * @return -1 or 0 depending on the comparison rules.
	 * 
	 * @throws ClassCastException If the method is called with <code>Map</code>
	 *         and the field value types are not expected.
	 * @throws IllegalArgumentException If the method is called with
	 *         <code>Map</code> and the event type or key code is missing.
	 * @throws NullPointerException If the argument is <code>null</code>.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof KeypadData) {
			return compareToKeypadData((KeypadData) o);
		}
		return compareToMap((Map) o);
	}

	/*
	 * Compares this instance with KeypadData argument according to rules in
	 * compareTo method.
	 */
	private int compareToKeypadData(KeypadData otherData) {
		if ((!super.equals(otherData)) ||
				(this.eventType != otherData.eventType) ||
				(this.keyCode != otherData.keyCode)) {
			return -1;
		}
		if (null != this.keyName) {
			if (!this.keyName.equals(otherData.keyName)) {
				return -1;
			}
		} else if (null != otherData.keyName) {
			return -1;
		}
		return 0;
	}

	/*
	 * Compares this instance with Map argument according to rules in compareTo
	 * method.
	 */
	private int compareToMap(Map otherData) {
		return compareToKeypadData(new KeypadData(otherData));
	}

}
