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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * Device Function multi-level data wrapper. It can contain a
 * <code>BigDecimal</code> value, timestamp, measurement unit and additional
 * metadata.
 * 
 * @see MultiLevelControl
 * @see MultiLevelSensor
 * @see Meter
 */
public class MultiLevelData {

	/**
	 * Represents <code>MultiLevelData</code> value. The immutable field is
	 * accessible with {@link #getValue()} getter. The field type is
	 * <code>BigDecimal</code> instead of <code>double</code> to guarantee value
	 * accuracy.
	 */
	public final BigDecimal value;

	/**
	 * Represents <code>MultiLevelData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. If possible, the value should be
	 * provided by the device, otherwise the device driver can generate that
	 * info. {@link java.lang.Long#MIN_VALUE} value means no timestamp. The
	 * immutable field is accessible with {@link #getTimestamp()} getter.
	 */
	public final long timestamp;

	/**
	 * Represents <code>MultiLevelData</code> measurement unit as it's described
	 * in
	 * {@link org.osgi.service.functionaldevice.DeviceFunction#META_INFO_UNIT}.
	 * It's an immutable field.
	 */
	public final String unit;

	/**
	 * Represents <code>MultiLevelData</code> metadata in an unmodifiable
	 * <code>Map</code>. The immutable field is accessible with
	 * {@link #getMetadata()} getter.
	 */
	public final Map metadata;

	/**
	 * Constructs new <code>MultiLevelData</code> instance with the specified
	 * arguments.
	 * 
	 * @param value
	 *            The multi-level value.
	 * @param timestamp
	 *            The value timestamp.
	 * @param unit
	 *            The value measurement unit.
	 * @param metadata
	 *            The value metadata.
	 */
	public MultiLevelData(final BigDecimal value, final long timestamp,
			final String unit, final Map metadata) {
		this.value = value;
		this.timestamp = timestamp;
		this.unit = unit;
		this.metadata = (null == metadata) ? null : Collections
				.unmodifiableMap(metadata);
	}

	/**
	 * Returns <code>MultiLevelData</code> value. The value type is
	 * <code>BigDecimal</code> instead of <code>double</code> to guarantee value
	 * accuracy.
	 * 
	 * @return The <code>MultiLevelData</code> value.
	 */
	public BigDecimal getValue() {
		return this.value;
	}

	/**
	 * Returns <code>MultiLevelData</code> timestamp. The timestamp is the
	 * difference between the value collecting time and midnight, January 1,
	 * 1970 UTC. It's measured in milliseconds. If possible, the value should be
	 * provided by the device, otherwise the device driver can generate that
	 * info. {@link java.lang.Long#MIN_VALUE} value means no timestamp.
	 * 
	 * @return <code>MultiLevelData</code> timestamp.
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns <code>MultiLevelData</code> measurement unit as it's described in
	 * {@link org.osgi.service.functionaldevice.DeviceFunction#META_INFO_UNIT} .
	 * 
	 * @return The <code>MultiLevelData</code> measurement unit.
	 */
	public String getUnit() {
		return this.unit;
	}

	/**
	 * Returns <code>MultiLevelData</code> metadata.
	 * 
	 * @return <code>MultiLevelData</code> metadata.
	 */
	public Map getMetadata() {
		return this.metadata;
	}

}
