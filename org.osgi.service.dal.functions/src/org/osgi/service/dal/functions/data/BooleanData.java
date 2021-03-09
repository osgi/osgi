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

package org.osgi.service.dal.functions.data;

import java.util.Map;

import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.BooleanSensor;

/**
 * Function boolean data wrapper. It can contain a boolean value, timestamp and
 * additional metadata. It doesn't support measurement unit.
 * 
 * @see BooleanControl
 * @see BooleanSensor
 * @see FunctionData
 */
public class BooleanData extends FunctionData {

	/**
	 * Represents the value field name. The field value is available with
	 * {@link #getValue()}. The field type is {@code boolean} . The constant can
	 * be used as a key to {@link #BooleanData(Map)}.
	 */
	public static final String	FIELD_VALUE	= "value";

	private final boolean		value;

	/**
	 * Constructs new {@code BooleanData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"value"=Boolean(true)...}. That map will initialize the
	 * {@link #FIELD_VALUE} field with {@code true}.
	 * <p>
	 * {@link #FIELD_VALUE} - mandatory field. The value type must be
	 * {@code Boolean}.
	 * 
	 * @param fields Contains the new {@code BooleanData} instance field values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws IllegalArgumentException If the value is missing.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	public BooleanData(Map<String, ? > fields) {
		super(fields);
		Boolean booleanValue = (Boolean) fields.get(FIELD_VALUE);
		if (null == booleanValue) {
			throw new IllegalArgumentException("The boolean value is missing.");
		}
		this.value = booleanValue.booleanValue();
	}

	/**
	 * Constructs new {@code BooleanData} instance with the specified arguments.
	 * 
	 * @param timestamp The boolean data timestamp optional field.
	 * @param metadata The boolean data metadata optional field.
	 * @param value The boolean value mandatory field.
	 */
	public BooleanData(long timestamp, Map<String, ? > metadata,
			boolean value) {
		super(timestamp, metadata);
		this.value = value;
	}

	/**
	 * Returns {@code BooleanData} value.
	 * 
	 * @return {@code BooleanData} value.
	 */
	public boolean getValue() {
		return this.value;
	}

	/**
	 * Two {@code BooleanData} instances are equal if they contain equal
	 * metadata, timestamp and boolean value.
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
		if (!(o instanceof BooleanData)) {
			return false;
		}
		try {
			if (0 != super.compareTo(o)) {
				return false;
			}
		} catch (ClassCastException cce) {
			return false;
		}
		return this.value == ((BooleanData) o).value;
	}

	/**
	 * Returns the hash code for this {@code BooleanData} object. The hash code
	 * is a sum of {@link FunctionData#hashCode()} and
	 * {@link Boolean#hashCode()}, where {@link Boolean#hashCode()} represents
	 * the boolean value hash code.
	 * 
	 * @return The hash code of this {@code BooleanData} object.
	 * 
	 * @see org.osgi.service.dal.FunctionData#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() + (this.value ?
				Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
	}

	/**
	 * Compares this {@code BooleanData} instance with the given argument. If
	 * the argument is not {@code BooleanData}, it throws
	 * {@code ClassCastException}. Otherwise, this method returns:
	 * <ul>
	 * <li>{@code -1} if this instance field is less than a field of the
	 * specified argument.</li>
	 * <li>{@code 0} if all fields are equivalent.</li>
	 * <li>{@code 1} if this instance field is greater than a field of the
	 * specified argument.</li>
	 * </ul>
	 * The fields are compared in this order: timestamp, metadata, value.
	 * 
	 * @param o {@code BooleanData} to be compared.
	 * 
	 * @return {@code -1}, {@code 0} or {@code 1} depending on the comparison
	 *         rules.
	 * 
	 * @throws ClassCastException If the method argument is not of type
	 *         {@code BooleanData}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		int result = super.compareTo(o);
		if (0 != result) {
			return result;
		}
		return Comparator.compare(this.value, ((BooleanData) o).value);
	}

	/**
	 * Returns the string representation of this boolean data.
	 *
	 * @return The string representation of this boolean data.
	 */
	@Override
	public String toString() {
		return getClass().getName() + " [value=" + value + ", timestamp=" + super.getTimestamp() + ']';
	}
}
