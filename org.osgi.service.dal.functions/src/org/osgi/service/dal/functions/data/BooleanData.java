/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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
	 * {@link #value} and {@link #getValue()}. The field type is {@code boolean}
	 * . The constant can be used as a key to {@link #BooleanData(Map)}.
	 */
	public static final String	FIELD_VALUE	= "value";

	/**
	 * Represents the boolean value. The field is accessible with
	 * {@link #getValue()} getter.
	 */
	public final boolean		value;

	/**
	 * Constructs new {@code BooleanData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"value"=Boolean(true)...}. That map will initialize the
	 * {@link #FIELD_VALUE} field with {@code true}.
	 * <p>
	 * {@link #FIELD_VALUE} field value type must be {@code Boolean}.
	 * 
	 * @param fields Contains the new {@code BooleanData} instance field values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws IllegalArgumentException If the value is missing.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	public BooleanData(Map fields) {
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
	 * @param timestamp The boolean data timestamp.
	 * @param metadata The boolean data metadata.
	 * @param value The boolean value.
	 */
	public BooleanData(long timestamp, Map metadata, boolean value) {
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
	 * @param other The object to compare this data.
	 * 
	 * @return {@code true} if this object is equivalent to the specified one.
	 * 
	 * @see org.osgi.service.dal.FunctionData#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof BooleanData)) {
			return false;
		}
		return 0 == compareToBooleanData((BooleanData) other);
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
	public int hashCode() {
		return super.hashCode() + (this.value ?
				Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
	}

	/**
	 * Compares this {@code BooleanData} instance with the given argument. The
	 * argument can be:
	 * <ul>
	 * <li>{@code Boolean} - the method returns {@code 0} if this instance
	 * contains equivalent boolean value. {@code -1} if this instance contains
	 * {@code false} and the argument is {@code true}. {@code 1} if this
	 * instance contains {@code true} and the argument is {@code false}.</li>
	 * <li>{@code BooleanData} - the method returns {@code -1} if metadata or
	 * timestamp are not equivalent. Otherwise, the boolean value is compared
	 * with the same rules as {@code Boolean} argument.</li>
	 * <li>{@code Map} - the map must be built according the rules of
	 * {@link #BooleanData(Map)}. Metadata, timestamp and value are compared
	 * according to {@code BooleanData} and {@code Boolean} argument rules.</li>
	 * </ul>
	 * 
	 * @param o An argument to be compared.
	 * 
	 * @return {@code -1}, {@code 0} or {@code 1} depending on the comparison
	 *         rules.
	 * 
	 * @throws ClassCastException If the method is called with {@code Map} and
	 *         field value types are not expected.
	 * @throws IllegalArgumentException If the method is called with {@code Map}
	 *         and the value is missing.
	 * @throws NullPointerException If the argument is {@code null}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof BooleanData) {
			return compareToBooleanData((BooleanData) o);
		} else if (o instanceof Boolean) {
			return compareToBoolean((Boolean) o);
		}
		return compareToMap((Map) o);
	}

	/*
	 * Compares this instance with BooleanData argument according to rules in
	 * compareTo method.
	 */
	private int compareToBooleanData(BooleanData otherData) {
		if (!super.equals(otherData)) {
			return -1;
		}
		return compareToBoolean(otherData.getValue() ? Boolean.TRUE : Boolean.FALSE);
	}

	/*
	 * Compares this instance with Boolean argument according to rules in
	 * compareTo method.
	 */
	private int compareToBoolean(Boolean otherData) {
		if (otherData.booleanValue()) {
			return (this.value) ? 0 : -1;
		} else {
			return (this.value) ? 1 : 0;
		}
	}

	/*
	 * Compares this instance with Map argument according to rules in compareTo
	 * method.
	 */
	private int compareToMap(Map otherData) {
		return compareToBooleanData(new BooleanData(otherData));
	}
}
