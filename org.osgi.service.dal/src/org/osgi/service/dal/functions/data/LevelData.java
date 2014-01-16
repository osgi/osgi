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
import org.osgi.service.dal.DeviceFunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.MultiLevelSensor;

/**
 * Device Function level data wrapper. It supports all properties defined in
 * <code>DeviceFunctionData</code>.
 * 
 * @see MultiLevelControl
 * @see MultiLevelSensor
 * @see Meter
 * @see DeviceFunctionData
 */
public class LevelData extends DeviceFunctionData {

	/**
	 * Represent the unit as it's defined in
	 * {@link org.osgi.service.dal.PropertyMetadata#META_INFO_UNITS}. The field
	 * is optional. The field is accessible with {@link #getUnit()} getter.
	 */
	public final String		unit;

	/**
	 * Represents the current level. It's mandatory field. The field is
	 * accessible with {@link #getLevel()} getter.
	 */
	public final BigDecimal	level;

	/**
	 * Constructs new <code>LevelData</code> instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"level"=BigDecimal(1)...}. That map will initialize the "level"
	 * field with 1.
	 * 
	 * @param fields Contains the new <code>LevelData</code> instance field
	 *        values.
	 */
	public LevelData(Map fields) {
		super(fields);
		this.unit = (String) fields.get("unit");
		this.level = (BigDecimal) fields.get("level");
		if (null == this.level) {
			throw new IllegalArgumentException("Level data is missing.");
		}
	}

	/**
	 * Constructs new <code>LevelData</code> instance with the specified
	 * arguments.
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
	 * Returns <code>LevelData</code> value. The value type is
	 * <code>BigDecimal</code> instead of <code>double</code> to guarantee value
	 * accuracy.
	 * 
	 * @return The <code>LevelData</code> value.
	 */
	public BigDecimal getLevel() {
		return this.level;
	}

	/**
	 * Returns <code>LevelData</code> unit as it's specified in
	 * {@link PropertyMetadata#META_INFO_UNITS} or <code>null</code> if the unit
	 * is missing.
	 * 
	 * @return The value unit or <code>null</code> if the unit is missing.
	 */
	public String getUnit() {
		return this.unit;
	}

	public int compareTo(Object o) {
		return 0; // TODO: impl
	}

}
