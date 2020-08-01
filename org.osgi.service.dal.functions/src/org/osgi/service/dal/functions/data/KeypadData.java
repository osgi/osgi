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

package org.osgi.service.dal.functions.data;

import java.util.Map;

import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.Keypad;

/**
 * Represents a keypad event data that is collected when a change with some key
 * from the keypad has occurred.
 * <p>
 * The key pressed event is using {@link #TYPE_PRESSED} type, while the key
 * released event is using {@link #TYPE_RELEASED} type.
 * 
 * @see Keypad
 * @see FunctionData
 */
public class KeypadData extends FunctionData {

	/**
	 * Represents the event type field name. The field value is available with
	 * {@link #getType()}. The field type is {@code int}. The constant can be
	 * used as a key to {@link #KeypadData(Map)}.
	 */
	public static final String	FIELD_TYPE						= "type";

	/**
	 * Represents the event sub-type field name. The field value is available
	 * with {@link #getSubType()}. The field type is {@code int}. The constant
	 * can be used as a key to {@link #KeypadData(Map)}.
	 */
	public static final String	FIELD_SUB_TYPE					= "subType";

	/**
	 * Represents the key code field name. The field value is available with
	 * {@link #getKeyCode()}. The field type is {@code int} . The constant can
	 * be used as a key to {@link #KeypadData(Map)}.
	 */
	public static final String	FIELD_KEY_CODE					= "keyCode";

	/**
	 * Represents the key name field name. The field value is available with
	 * {@link #getKeyName()}. The field type is {@code String}. The constant can
	 * be used as a key to {@link #KeypadData(Map)}.
	 */
	public static final String	FIELD_KEY_NAME					= "keyName";

	/**
	 * Represents a keypad event type for a key pressed event.
	 */
	public static final int		TYPE_PRESSED					= 0;

	/**
	 * Represents a keypad event type for a key released event.
	 */
	public static final int		TYPE_RELEASED					= 1;

	/**
	 * Represents a keypad event sub-type for a normal key pressed event.
	 * Usually, there is a single press and the key is not held down. This
	 * sub-type is used with {@link #TYPE_PRESSED} type.
	 */
	public static final int		SUB_TYPE_PRESSED_NORMAL			= 1;

	/**
	 * Represents a keypad event sub-type for a long key pressed event. Usually,
	 * there is a single press and the key is held down. This sub-type is used
	 * with {@link #TYPE_PRESSED} type.
	 */
	public static final int		SUB_TYPE_PRESSED_LONG			= 2;

	/**
	 * Represents a keypad event sub-type for a double key pressed event.
	 * Usually, there are two press actions and the key is not held down after
	 * the second press. This sub-type is used with {@link #TYPE_PRESSED} type.
	 */
	public static final int		SUB_TYPE_PRESSED_DOUBLE			= 3;

	/**
	 * Represents a keypad event sub-type for a double long key pressed event.
	 * Usually, there are two press actions and the key is held down after the
	 * second press. This sub-type is used with {@link #TYPE_PRESSED} type.
	 */
	public static final int		SUB_TYPE_PRESSED_DOUBLE_LONG	= 4;

	private final int			type;
	private final int			subType;
	private final int			keyCode;
	private final String		keyName;

	/**
	 * Constructs new {@code KeypadData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"type"=Integer(1)...}. That map will initialize the
	 * {@link #FIELD_TYPE} field with 1.
	 * <ul>
	 * <li>{@link #FIELD_TYPE} - mandatory field. The value type must be
	 * {@code Integer}.</li>
	 * <li>{@link #FIELD_SUB_TYPE} - optional field. The value type must be
	 * {@code Integer}.</li>
	 * <li>{@link #FIELD_KEY_CODE} - mandatory field. The value type must be
	 * {@code Integer}.</li>
	 * <li>{@link #FIELD_KEY_NAME} - optional field. The value type must be
	 * {@code String}.</li>
	 * </ul>
	 * 
	 * @param fields Contains the new {@code KeypadData} instance field values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws IllegalArgumentException If the event type or key code is missing
	 *         or invalid arguments are specified.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	public KeypadData(Map<String, ? > fields) {
		super(fields);
		Integer eventTypeLocal = (Integer) fields.get(FIELD_TYPE);
		if (null == eventTypeLocal) {
			throw new IllegalArgumentException("The event type is missing.");
		}
		this.type = eventTypeLocal.intValue();

		Integer eventSubTypeLocal = (Integer) fields.get(FIELD_SUB_TYPE);
		this.subType = (null == eventSubTypeLocal) ? 0 : eventSubTypeLocal.intValue();

		Integer keyCodeLocal = (Integer) fields.get(FIELD_KEY_CODE);
		if (null == keyCodeLocal) {
			throw new IllegalArgumentException("The key code is missing.");
		}
		this.keyCode = keyCodeLocal.intValue();

		this.keyName = (String) fields.get(FIELD_KEY_NAME);
		validate();
	}

	/**
	 * Constructs new {@code KeypadData} instance with the specified arguments.
	 * 
	 * @param timestamp The data timestamp optional field.
	 * @param metadata The data metadata optional field.
	 * @param type The data event type mandatory field.
	 * @param subType The data event sub-type optional field or {@code 0} if
	 *        there is no sub-type.
	 * @param keyCode The data key code mandatory field.
	 * @param keyName The data key name optional field or {@code null} if there
	 *        is no key name.
	 */
	public KeypadData(long timestamp, Map<String,Object> metadata, int type,
			int subType, int keyCode, String keyName) {
		super(timestamp, metadata);
		this.type = type;
		this.subType = subType;
		this.keyCode = keyCode;
		this.keyName = keyName;
		validate();
	}

	/**
	 * Returns the event type. The type represents the main reason for this
	 * event. It can be one of:
	 * <ul>
	 * <li>{@link #TYPE_PRESSED}</li>
	 * <li>{@link #TYPE_RELEASED}</li>
	 * </ul>
	 * 
	 * @return The event type.
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Returns the event sub-type. The sub-type provides additional details
	 * about the event. The sub-type can be one of:
	 * <ul>
	 * <li>{@link #SUB_TYPE_PRESSED_NORMAL}</li>
	 * <li>{@link #SUB_TYPE_PRESSED_LONG}</li>
	 * <li>{@link #SUB_TYPE_PRESSED_DOUBLE}</li>
	 * <li>{@link #SUB_TYPE_PRESSED_DOUBLE_LONG}</li>
	 * <li>custom sub-type</li>
	 * </ul>
	 * Zero and positive values are reserved for this definition and further
	 * extensions of the sub-types. Custom sub-types can be used only as
	 * negative values to prevent potential collisions.
	 *
	 * @return The event sub-type.
	 */
	public int getSubType() {
		return this.subType;
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
	 * field is optional and sometimes it could be missed(might be {@code null}
	 * ).
	 * 
	 * @return A string with the name of the key or {@code null} if not
	 *         specified.
	 */
	public String getKeyName() {
		return this.keyName;
	}

	/**
	 * Two {@code KeypadData} instances are equal if they contain equal
	 * metadata, timestamp, event type, key code and key name.
	 * 
	 * @param o The object to compare this data.
	 * 
	 * @return {@code true} if this object is equivalent to the specified one.
	 * 
	 * @see org.osgi.service.dal.FunctionData#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof KeypadData)) {
			return false;
		}
		try {
			if (0 != super.compareTo(o)) {
				return false;
			}
		} catch (ClassCastException cce) {
			return false;
		}
		KeypadData other = (KeypadData) o;
		if ((this.type != other.type) ||
				(this.subType != other.subType) ||
				(this.keyCode != other.keyCode)) {
			return false;
		}
		return 0 == Comparator.compare(this.keyName, other.keyName);
	}

	/**
	 * Returns the hash code for this {@code KeypadData} object. The hash code
	 * is a sum of {@link FunctionData#hashCode()}, {@link String#hashCode()},
	 * event type, event sub-type and key code, where {@link String#hashCode()}
	 * represents the key name hash code if available.
	 * 
	 * @return The hash code of this {@code LevelData} object.
	 * 
	 * @see org.osgi.service.dal.FunctionData#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		if (null != this.keyName) {
			hashCode += this.keyName.hashCode();
		}
		return hashCode + this.type + this.subType + this.keyCode;
	}

	/**
	 * Compares this {@code KeypadData} instance with the given argument. If the
	 * argument is not {@code KeypadData}, it throws {@code ClassCastException}.
	 * Otherwise, this method returns:
	 * <ul>
	 * <li>{@code -1} if this instance field is less than a field of the
	 * specified argument.</li>
	 * <li>{@code 0} if all fields are equivalent.</li>
	 * <li>{@code 1} if this instance field is greater than a field of the
	 * specified argument.</li>
	 * </ul>
	 * The fields are compared in this order: timestamp, metadata, type,
	 * sub-type, key code, key name.
	 * 
	 * @param o {@code KeypadData} to be compared.
	 * 
	 * @return {@code -1}, {@code 0} or {@code 1} depending on the comparison
	 *         rules.
	 * 
	 * @throws ClassCastException If the method argument is not of type
	 *         {@code KeypadData}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		int result = super.compareTo(o);
		if (0 != result) {
			return result;
		}
		KeypadData other = (KeypadData) o;
		result = Comparator.compare(this.type, other.type);
		if (0 != result) {
			return result;
		}
		result = Comparator.compare(this.subType, other.subType);
		if (0 != result) {
			return result;
		}
		result = Comparator.compare(this.keyCode, other.keyCode);
		if (0 != result) {
			return result;
		}
		return Comparator.compare(this.keyName, other.keyName);
	}

	/**
	 * Returns the string representation of this keypad data.
	 *
	 * @return The string representation of this keypad data.
	 */
	@Override
	public String toString() {
		return getClass().getName() + " [type=" + getTypeAsString() +
				", subType=" + subType + ", keyCode=" + keyCode +
				", keyName=" + keyName + ", timestamp=" + super.getTimestamp() + "]";
	}

	private String getTypeAsString() {
		switch (this.type) {
			case TYPE_PRESSED :
				return "pressed";
			case TYPE_RELEASED :
				return "released";
			default :
				throw new IllegalStateException("Unknow type: " + this.type);
		}
	}

	/*
	 * Validates the instance consistency.
	 */
	private void validate() {
		switch (this.type) {
			case TYPE_PRESSED :
				break; // nothing to validate
			case TYPE_RELEASED :
				switch (this.subType) {
					case SUB_TYPE_PRESSED_NORMAL :
					case SUB_TYPE_PRESSED_LONG :
					case SUB_TYPE_PRESSED_DOUBLE :
					case SUB_TYPE_PRESSED_DOUBLE_LONG :
						throw new IllegalArgumentException(
								"The pressed sub-type: " + this.subType + " is used with released event type.");
					default : // it's ok, go ahead
				}
				break;
			default :
				throw new IllegalArgumentException("Unknown event type: " + this.type);
		}
	}
}
