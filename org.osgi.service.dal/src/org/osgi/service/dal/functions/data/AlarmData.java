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
import org.osgi.service.dal.functions.Alarm;

/**
 * Device Function alarm data. It cares about the alarm type, severity,
 * timestamp and additional metadata. It doesn't support unit. The alarm type is
 * mapped to <code>DeviceFunctionData</code> value.
 * 
 * @see Alarm
 * @see DeviceFunctionData
 */
public class AlarmData extends DeviceFunctionData {

	/** The alarm type indicates that smoke is detected. */
	public static final int	TYPE_SMOKE		= 1;

	/** The alarm type indicates that temperature is too high. */
	public static final int	TYPE_HEAT		= 2;

	/** The alarm type indicates that temperature is too low. */
	public static final int	TYPE_COLD		= 3;

	/** The alarm type indicates that carbon dioxide is detected. */
	public static final int	TYPE_GAS_CO2	= 4;

	/** The alarm type indicates that carbon monoxide is detected. */
	public static final int	TYPE_GAS_CO		= 5;

	/** The alarm type indicates that water leak is detected. */
	public static final int	TYPE_WATER		= 6;

	/** The alarm type indicates a power cut. */
	public static final int	TYPE_POWER_FAIL	= 7;

	/** The alarm type indicates that there is hardware failure. */
	public static final int	TYPE_HW_FAIL	= 8;

	/** The alarm type indicates that there is software failure. */
	public static final int	TYPE_SW_FAIL	= 9;

	/**
	 * The severity constant indicates that there is no severity rating for this
	 * alarm.
	 */
	public static final int	SEVERITY_NONE	= 0;

	/**
	 * The severity rating indicates that there is an alarm with lowest
	 * priority.
	 */
	public static final int	SEVERITY_LOW	= 1;

	/**
	 * The severity rating indicates that there is an alarm with medium
	 * priority. The severity priority is higher than {@link #SEVERITY_LOW} and
	 * lower than {@link #SEVERITY_HIGH}.
	 */
	public static final int	SEVERITY_MEDIUM	= 2;	// Moderate, Normal

	/**
	 * The severity rating indicates that there is an alarm with high priority.
	 * The severity priority is higher than {@link #SEVERITY_MEDIUM} and lower
	 * than {@link #SEVERITY_URGENT}.
	 */
	public static final int	SEVERITY_HIGH	= 3;	// Important

	/**
	 * The severity rating indicates that there an urgent alarm. That severity
	 * has highest priority.
	 */
	public static final int	SEVERITY_URGENT	= 4;	// Critical

	/**
	 * Represents the alarm severity. The field is accessible with
	 * {@link #getSeverity()} getter. The vendor can define own alarm severity
	 * ratings with negative values.
	 */
	public final int		severity;

	/**
	 * Represents the alarm type. The field is accessible with
	 * {@link #getType()} getter. The vendor can define own alarm types with
	 * negative values.
	 */
	public final int		type;

	/**
	 * Constructs new <code>AlarmData</code> instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"severity"=Integer(1)...}. That map will initialize the "severity"
	 * field with 1.
	 * 
	 * @param fields Contains the new <code>AlarmData</code> instance field
	 *        values.
	 */
	public AlarmData(final Map fields) {
		super(fields);
		final Integer severity = (Integer) fields.get("severity");
		this.severity = (null != severity) ? severity.intValue() : SEVERITY_NONE;
		final Integer type = (Integer) fields.get("type");
		if (null == type) {
			throw new IllegalArgumentException("The alarm data type is missing.");
		}
		this.type = type.intValue();
	}

	/**
	 * Constructs new <code>AlarmData</code> instance with the specified
	 * arguments.
	 * 
	 * @param timestamp The alarm data timestamp.
	 * @param metadata The alarm data metadata.
	 * @param severity The alarm data severity.
	 * @param type The alarm data type.
	 */
	public AlarmData(long timestamp, Map metadata, int severity, int type) {
		super(timestamp, metadata);
		this.severity = severity;
		this.type = type;
	}

	/**
	 * Returns the alarm type. The type can be one of the predefined:
	 * <ul>
	 * <li>{@link #TYPE_SMOKE}</li>
	 * <li>{@link #TYPE_HEAT}</li>
	 * <li>{@link #TYPE_COLD}</li>
	 * <li>{@link #TYPE_GAS_CO}</li>
	 * <li>{@link #TYPE_GAS_CO2}</li>
	 * <li>{@link #TYPE_WATER}</li>
	 * <li>{@link #TYPE_POWER_FAIL}</li>
	 * <li>{@link #TYPE_HW_FAIL}</li>
	 * <li>{@link #TYPE_SW_FAIL}</li>
	 * </ul>
	 * The vendor can define own alarm types with negative values.
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

	public int compareTo(Object o) {
		// TODO: impl
		return 0;
	}

}
