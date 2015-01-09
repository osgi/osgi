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
import org.osgi.service.dal.functions.Alarm;

/**
 * Function alarm data. It cares about the alarm type, severity, timestamp and
 * additional metadata. It doesn't support unit. The alarm type is mapped to
 * {@code FunctionData} value.
 * 
 * @see Alarm
 * @see FunctionData
 */
public class AlarmData extends FunctionData {

	/**
	 * Represents the severity field name. The field value is available with
	 * {@link #getSeverity()}. The field type is {@code int}. The constant can
	 * be used as a key to {@link #AlarmData(Map)} .
	 */
	public static final String	FIELD_SEVERITY		= "severity";

	/**
	 * Represents the type field name. The field value is available with
	 * {@link #getType()}. The field type is {@code int}. The constant can be
	 * used as a key to {@link #AlarmData(Map)}.
	 */
	public static final String	FIELD_TYPE			= "type";

	/** The alarm type indicates that the type is not specified. */
	public static final int		TYPE_UNDEFINED		= 0;

	/**
	 * The alarm type indicates that there is access control issue. For example,
	 * the alarm can indicate that the door is unlocked.
	 */
	public static final int		TYPE_ACCESS_CONTROL	= 1;

	/**
	 * The alarm type indicates that there is a burglar notification. For
	 * example, the alarm can indicate that the glass is broken.
	 */
	public static final int		TYPE_BURGLAR		= 2;

	/** The alarm type indicates that temperature is too low. */
	public static final int		TYPE_COLD			= 3;

	/** The alarm type indicates that carbon monoxide (CO) is detected. */
	public static final int		TYPE_GAS_CO			= 4;

	/** The alarm type indicates that carbon dioxide (CO2) is detected. */
	public static final int		TYPE_GAS_CO2		= 5;

	/** The alarm type indicates that temperature is too high. */
	public static final int		TYPE_HEAT			= 6;

	/** The alarm type indicates that there is hardware failure. */
	public static final int		TYPE_HARDWARE_FAIL	= 7;

	/** The alarm type indicates a power cut. */
	public static final int		TYPE_POWER_FAIL		= 8;

	/** The alarm type indicates that smoke is detected. */
	public static final int		TYPE_SMOKE			= 9;

	/** The alarm type indicates that there is software failure. */
	public static final int		TYPE_SOFTWARE_FAIL	= 10;

	/** The alarm type for a tamper indication. */
	public static final int		TYPE_TAMPER			= 11;

	/** The alarm type indicates that a water leak is detected. */
	public static final int		TYPE_WATER			= 12;

	/**
	 * The severity constant indicates that there is no severity rating for this
	 * alarm.
	 */
	public static final int		SEVERITY_UNDEFINED	= 0;

	/**
	 * The severity rating indicates that there is a minor alarm. The severity
	 * priority is lower than {@link #SEVERITY_MAJOR} and
	 * {@link #SEVERITY_CRITICAL}.
	 */
	public static final int		SEVERITY_MINOR		= 1;

	/**
	 * The severity rating indicates that there is a major alarm. The severity
	 * priority is higher than {@link #SEVERITY_MINOR} and lower than
	 * {@link #SEVERITY_CRITICAL}.
	 */
	public static final int		SEVERITY_MAJOR		= 2;

	/**
	 * The severity rating indicates that there a critical alarm. The severity
	 * priority is higher than {@link #SEVERITY_MINOR} and
	 * {@link #SEVERITY_MAJOR}.
	 */
	public static final int		SEVERITY_CRITICAL	= 3;

	private final int			severity;
	private final int			type;

	/**
	 * Constructs new {@code AlarmData} instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"severity"=Integer(1)...}. That map will initialize the
	 * {@link #FIELD_SEVERITY} field with 1. If severity is missing,
	 * {@link #SEVERITY_UNDEFINED} is used.
	 * <ul>
	 * <li>{@link #FIELD_SEVERITY} - optional field. The value type must be
	 * {@code Integer}.</li>
	 * <li>{@link #FIELD_TYPE} - mandatory field. The value type must be
	 * {@code Integer}.</li>
	 * </ul>
	 * 
	 * @param fields Contains the new {@code AlarmData} instance field values.
	 * 
	 * @throws ClassCastException If the field value types are not expected.
	 * @throws IllegalArgumentException If the alarm type is missing.
	 * @throws NullPointerException If the fields map is {@code null}.
	 */
	public AlarmData(Map fields) {
		super(fields);
		Integer severityLocal = (Integer) fields.get(FIELD_SEVERITY);
		this.severity = (null != severityLocal) ? severityLocal.intValue() : SEVERITY_UNDEFINED;
		Integer typeLocal = (Integer) fields.get(FIELD_TYPE);
		if (null == typeLocal) {
			throw new IllegalArgumentException("The alarm data type is missing.");
		}
		this.type = typeLocal.intValue();
	}

	/**
	 * Constructs new {@code AlarmData} instance with the specified arguments.
	 * 
	 * @param timestamp The alarm data timestamp optional field.
	 * @param metadata The alarm data metadata optional field.
	 * @param severity The alarm data severity optional field.
	 * @param type The alarm data type mandatory field.
	 */
	public AlarmData(long timestamp, Map metadata, int severity, int type) {
		super(timestamp, metadata);
		this.severity = severity;
		this.type = type;
	}

	/**
	 * Returns the alarm type. The type can be one of the predefined:
	 * <ul>
	 * <li>{@link #TYPE_UNDEFINED}</li>
	 * <li>{@link #TYPE_SMOKE}</li>
	 * <li>{@link #TYPE_HEAT}</li>
	 * <li>{@link #TYPE_COLD}</li>
	 * <li>{@link #TYPE_GAS_CO}</li>
	 * <li>{@link #TYPE_GAS_CO2}</li>
	 * <li>{@link #TYPE_WATER}</li>
	 * <li>{@link #TYPE_POWER_FAIL}</li>
	 * <li>{@link #TYPE_HARDWARE_FAIL}</li>
	 * <li>{@link #TYPE_SOFTWARE_FAIL}</li>
	 * <li>vendor specific</li>
	 * </ul>
	 * Zero and positive values are reserved for this definition and further
	 * extensions of the alarm types. Custom types can be used only as negative
	 * values to prevent potential collisions.
	 * 
	 * @return The alarm type.
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Returns the alarm severity.
	 * 
	 * @return The alarm severity.
	 */
	public int getSeverity() {
		return this.severity;
	}

	/**
	 * Two {@code AlarmData} instances are equal if they contain equal metadata,
	 * timestamp, type and severity.
	 * 
	 * @param other The object to compare this data.
	 * 
	 * @return {@code true} if this object is equivalent to the specified one.
	 * 
	 * @see org.osgi.service.dal.FunctionData#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof AlarmData)) {
			return false;
		}
		return 0 == compareToAlarmData((AlarmData) other);
	}

	/**
	 * Returns the hash code for this {@code AlarmData} object. The hash code is
	 * a sum of {@link FunctionData#hashCode()}, the alarm severity and the
	 * alarm type.
	 * 
	 * @return The hash code of this {@code AlarmData} object.
	 * 
	 * @see org.osgi.service.dal.FunctionData#hashCode()
	 */
	public int hashCode() {
		return super.hashCode() + this.severity + this.type;
	}

	/**
	 * Compares this {@code AlarmData} instance with the given argument. The
	 * argument can be:
	 * <ul>
	 * <li>{@code AlarmData} - the method returns {@code -1} if metadata,
	 * timestamp, type or severity are not equivalent. {@code 0} if all fields
	 * are equivalent. {@code 1} if all fields are equivalent and this instance
	 * severity is greater than the severity of the specified argument.</li>
	 * <li>{@code Map} - the map must be built according the rules of
	 * {@link #AlarmData(Map)}. Metadata, timestamp, type and severity are
	 * compared according to {@code AlarmData} argument rules.</li>
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
	 *         and the alarm type is missing.
	 * @throws NullPointerException If the argument is {@code null}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof AlarmData) {
			return compareToAlarmData((AlarmData) o);
		}
		return compareToMap((Map) o);
	}

	/*
	 * Compares this instance with AlarmData argument according to rules in
	 * compareTo method.
	 */
	private int compareToAlarmData(AlarmData otherData) {
		if ((!super.equals(otherData)) ||
				(this.type != otherData.type) ||
				(this.severity < otherData.severity)) {
			return -1;
		}
		return (this.severity == otherData.severity) ? 0 : 1;
	}

	/*
	 * Compares this instance with Map argument according to rules in compareTo
	 * method.
	 */
	private int compareToMap(Map otherData) {
		return compareToAlarmData(new AlarmData(otherData));
	}
}
