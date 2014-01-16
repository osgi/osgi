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
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.BooleanSensor;

/**
 * Device Function boolean data wrapper. It can contain a boolean value,
 * timestamp and additional metadata. It doesn't support measurement unit.
 * 
 * @see BooleanControl
 * @see BooleanSensor
 * @see DeviceFunctionData
 */
public class BooleanData extends DeviceFunctionData {

	/**
	 * Represents the boolean value. The field is accessible with
	 * {@link #getValue()} getter.
	 */
	public final boolean	value;

	/**
	 * Constructs new <code>BooleanData</code> instance with the specified field
	 * values. The map keys must match to the field names. The map values will
	 * be assigned to the appropriate class fields. For example, the maps can
	 * be: {"data"=Boolean(true)...}. That map will initialize the "data" field
	 * with <code>true</code>.
	 * 
	 * @param fields Contains the new <code>BooleanData</code> instance field
	 *        values.
	 */
	public BooleanData(final Map fields) {
		super(fields);
		final Boolean booleanValue = (Boolean) fields.get("value");
		if (null == booleanValue) {
			throw new IllegalArgumentException("The boolean value is missing.");
		}
		this.value = booleanValue.booleanValue();
	}

	/**
	 * Constructs new <code>BooleanData</code> instance with the specified
	 * arguments.
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
	 * Returns <code>BooleanData</code> value.
	 * 
	 * @return <code>BooleanData</code> value.
	 */
	public boolean getValue() {
		return this.value;
	}

	public int compareTo(Object o) {
		// TODO: impl
		return 0;
	}

}
