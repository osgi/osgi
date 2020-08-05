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

import java.math.BigDecimal;
import java.util.Map;

import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.MultiLevelSensor;

/**
 * Function level data wrapper. It supports all properties defined in
 * {@code FunctionData}.
 * 
 * @see MultiLevelControl
 * @see MultiLevelSensor
 * @see Meter
 * @see FunctionData
 */
public class LevelData extends FunctionData {

	/**
	 * Represents the level field name. The field value is available with
	 * {@link #getLevel()}. The field type is {@code BigDecimal}. The constant
	 * can be used as a key to {@link #LevelData(Map)}.
	 */
	public static final String	FIELD_LEVEL	= "level";

	/**
	 * Represents the unit field name. The field value is available with
	 * {@link #getUnit()}. The field type is {@code String}. The constant can be
	 * used as a key to {@link #LevelData(Map)}.
	 */
	public static final String	FIELD_UNIT	= "unit";

	private final BigDecimal	level;
	private final String		unit;

	/**
	 * Constructs new {@code LevelData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"level"=BigDecimal(1)...}. That map will initialize the
	 * {@link #FIELD_LEVEL} field with 1.
	 * <ul>
	 * <li>{@link #FIELD_LEVEL} - mandatory field. The value type must be
	 * {@code BigDecimal}.</li>
	 * <li>{@link #FIELD_UNIT} - optional field. The value type must be
	 * {@code String}.</li>
	 * </ul>
	 * 
	 * @param fields Contains the new {@code LevelData} instance field values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws IllegalArgumentException If the level is missing.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	public LevelData(Map<String, ? > fields) {
		super(fields);
		this.level = (BigDecimal) fields.get(FIELD_LEVEL);
		if (null == this.level) {
			throw new IllegalArgumentException("Level is missing.");
		}
		this.unit = (String) fields.get(FIELD_UNIT);
	}

	/**
	 * Constructs new {@code LevelData} instance with the specified arguments.
	 * 
	 * @param timestamp The data timestamp optional field.
	 * @param metadata The data metadata optional field.
	 * @param level The level value mandatory field.
	 * @param unit The data unit optional field.
	 *
	 * @throws NullPointerException If {@code level} is {@code null}.
	 */
	public LevelData(long timestamp, Map<String,Object> metadata,
			BigDecimal level, String unit) {
		super(timestamp, metadata);
		this.level = level;
		if (null == this.level) {
			throw new NullPointerException("Level is null.");
		}
		this.unit = unit;
	}

	/**
	 * Returns {@code LevelData} value. The value type is {@code BigDecimal}
	 * instead of {@code double} to guarantee value accuracy.
	 * 
	 * @return The {@code LevelData} value.
	 */
	public BigDecimal getLevel() {
		return this.level;
	}

	/**
	 * Returns {@code LevelData} unit as it's specified in
	 * {@link PropertyMetadata#UNITS} or {@code null} if the unit is missing.
	 * 
	 * @return The value unit or {@code null} if the unit is missing.
	 */
	public String getUnit() {
		return this.unit;
	}

	/**
	 * Two {@code LevelData} instances are equal if they contain equal metadata,
	 * timestamp, unit and level.
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
		if (!(o instanceof LevelData)) {
			return false;
		}
		try {
			if (0 != super.compareTo(o)) {
				return false;
			}
		} catch (ClassCastException cce) {
			return false;
		}
		LevelData other = (LevelData) o;
		if (!this.level.equals(other.level)) {
			return false;
		}
		return 0 == Comparator.compare(this.unit, other.unit);
	}

	/**
	 * Returns the hash code for this {@code LevelData} object. The hash code is
	 * a sum of {@link FunctionData#hashCode()}, {@link String#hashCode()} and
	 * {@link BigDecimal#hashCode()}, where {@link String#hashCode()} represents
	 * the unit hash code and {@link BigDecimal#hashCode()} represents the level
	 * hash code.
	 * 
	 * @return The hash code of this {@code LevelData} object.
	 * 
	 * @see org.osgi.service.dal.FunctionData#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		if (null != this.unit) {
			hashCode += this.unit.hashCode();
		}
		return hashCode + this.level.hashCode();
	}

	/**
	 * Compares this {@code LevelData} instance with the given argument. If the
	 * argument is not {@code LevelData}, it throws {@code ClassCastException}.
	 * Otherwise, this method returns:
	 * <ul>
	 * <li>{@code -1} if this instance field is less than a field of the
	 * specified argument.</li>
	 * <li>{@code 0} if all fields are equivalent.</li>
	 * <li>{@code 1} if this instance field is greater than a field of the
	 * specified argument.</li>
	 * </ul>
	 * The fields are compared in this order: timestamp, metadata, level, unit.
	 * 
	 * @param o {@code LevelData} to be compared.
	 * 
	 * @return {@code -1}, {@code 0} or {@code 1} depending on the comparison
	 *         rules.
	 * 
	 * @throws ClassCastException If the method argument is not of type
	 *         {@code LevelData}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		int result = super.compareTo(o);
		if (0 != result) {
			return result;
		}
		LevelData other = (LevelData) o;
		result = Comparator.compare(this.level, other.level);
		if (0 != result) {
			return result;
		}
		return Comparator.compare(this.unit, other.unit);
	}

	/**
	 * Returns the string representation of this level data.
	 *
	 * @return The string representation of this level data.
	 */
	@Override
	public String toString() {
		return getClass().getName() + " [level=" + level + ", unit=" + unit +
				", timestamp=" + super.getTimestamp() + "]";
	}
}
