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
	 * {@link #level} and {@link #getLevel()}. The field type is
	 * {@code BigDecimal}. The constant can be used as a key to
	 * {@link #LevelData(Map)}.
	 */
	public static final String	FIELD_LEVEL	= "level";

	/**
	 * Represents the unit field name. The field value is available with
	 * {@link #unit} and {@link #getUnit()}. The field type is {@code String}.
	 * The constant can be used as a key to {@link #LevelData(Map)}.
	 */
	public static final String	FIELD_UNIT	= "unit";

	/**
	 * Represent the unit as it's defined in
	 * {@link org.osgi.service.dal.PropertyMetadata#UNITS}. The field
	 * is optional. The field is accessible with {@link #getUnit()} getter.
	 */
	public final String		unit;

	/**
	 * Represents the current level. It's mandatory field. The field is
	 * accessible with {@link #getLevel()} getter.
	 */
	public final BigDecimal	level;

	/**
	 * Constructs new {@code LevelData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"level"=BigDecimal(1)...}. That map will initialize the
	 * {@link #FIELD_LEVEL} field with 1.
	 * <p>
	 * {@link #FIELD_UNIT} field value type must be {@code String}.
	 * {@link #FIELD_LEVEL} field value type must be {@code BigDecimal}.
	 * 
	 * @param fields Contains the new {@code LevelData} instance field values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws IllegalArgumentException If the level is missing.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	public LevelData(Map fields) {
		super(fields);
		this.unit = (String) fields.get(FIELD_UNIT);
		this.level = (BigDecimal) fields.get(FIELD_LEVEL);
		if (null == this.level) {
			throw new IllegalArgumentException("Level data is missing.");
		}
	}

	/**
	 * Constructs new {@code LevelData} instance with the specified arguments.
	 * 
	 * @param timestamp The data timestamp.
	 * @param metadata The data metadata.
	 * @param unit The data unit.
	 * @param level The level value.
	 */
	public LevelData(long timestamp, Map metadata, String unit, BigDecimal level) {
		super(timestamp, metadata);
		this.unit = unit;
		this.level = level;
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
	 * @param other The object to compare this data.
	 * 
	 * @return {@code true} if this object is equivalent to the specified one.
	 * 
	 * @see org.osgi.service.dal.FunctionData#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof LevelData)) {
			return false;
		}
		return 0 == compareToLevelData((LevelData) other);
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
	public int hashCode() {
		int hashCode = super.hashCode();
		if (null != this.unit) {
			hashCode += this.unit.hashCode();
		}
		return hashCode + this.level.hashCode();
	}

	/**
	 * Compares this {@code LevelData} instance with the given argument. The
	 * argument can be:
	 * <ul>
	 * <li>{@code BigDecimal} - the method returns the result of
	 * {@link BigDecimal#compareTo(Object)} for this instance level and the
	 * specified argument.</li>
	 * <li>{@code LevelData} - the method returns {@code -1} if metadata,
	 * timestamp or unit are not equivalent. Otherwise, the level is compared
	 * with the same rules as {@code BigDecimal} argument.</li>
	 * <li>{@code Map} - the map must be built according the rules of
	 * {@link #LevelData(Map)}. Metadata, timestamp, unit and level are compared
	 * according to {@code BigDecimal} and {@code LevelData} argument rules.</li>
	 * </ul>
	 * 
	 * @param o An argument to be compared.
	 * 
	 * @return {@code -1}, {@code 0} or {@code 1} depending on the comparison
	 *         rules.
	 * 
	 * @throws ClassCastException If the method is called with {@code Map} and
	 *         the field value types are not expected.
	 * @throws IllegalArgumentException If the method is called with {@code Map}
	 *         and the level is missing.
	 * @throws NullPointerException If the argument is {@code null}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof LevelData) {
			return compareToLevelData((LevelData) o);
		} else if (o instanceof BigDecimal) {
			return compareToBigDecimal((BigDecimal) o);
		}
		return compareToMap((Map) o);
	}

	/*
	 * Compares this instance with LevelData argument according to rules in
	 * compareTo method.
	 */
	private int compareToLevelData(LevelData otherData) {
		if (!super.equals(otherData)) {
			return -1;
		}
		if (null != this.unit) {
			if ((null == otherData.unit) || (!this.unit.equals(otherData.unit))) {
				return -1;
			}
		} else if (null != otherData.unit) {
			return -1;
		}
		return this.level.compareTo(otherData.level);
	}

	/*
	 * Compares this instance with BigDecimal argument according to rules in
	 * compareTo method.
	 */
	private int compareToBigDecimal(BigDecimal otherData) {
		return this.level.compareTo(otherData);
	}

	/*
	 * Compares this instance with Map argument according to rules in compareTo
	 * method.
	 */
	private int compareToMap(Map otherData) {
		return compareToLevelData(new LevelData(otherData));
	}

}
