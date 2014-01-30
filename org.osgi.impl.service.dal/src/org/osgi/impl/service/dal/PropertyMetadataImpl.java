/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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


package org.osgi.impl.service.dal;

import java.util.Map;
import org.osgi.service.dal.DeviceFunctionData;
import org.osgi.service.dal.PropertyMetadata;

/**
 * Basic implementation of the property metadata.
 */
public final class PropertyMetadataImpl implements PropertyMetadata {

	private final Map		metadata;
	private final Object	resolution;
	private final DeviceFunctionData[]	enumValues;
	private final DeviceFunctionData	minValue;
	private final DeviceFunctionData	maxValue;

	/**
	 * Constructs the property metadata with the specified arguments.
	 * 
	 * @param metadata Additional metadata.
	 * @param resolution The resolution.
	 * @param enumValues The supported values, if any.
	 * @param minValue The minimum value, if any.
	 * @param maxValue The maximum value, if any.
	 */
	public PropertyMetadataImpl(
			Map metadata,
			Object resolution,
			DeviceFunctionData[] enumValues,
			DeviceFunctionData minValue,
			DeviceFunctionData maxValue) {
		this.metadata = metadata;
		this.resolution = resolution;
		this.enumValues = enumValues;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public Map getMetadata(String unit) {
		return this.metadata;
	}

	public Object getResolution(String unit) throws IllegalArgumentException {
		return this.resolution;
	}

	public DeviceFunctionData[] getEnumValues(String unit) throws IllegalArgumentException {
		return this.enumValues;
	}

	public DeviceFunctionData getMinValue(String unit) throws IllegalArgumentException {
		return this.minValue;
	}

	public DeviceFunctionData getMaxValue(String unit) throws IllegalArgumentException {
		return this.maxValue;
	}

}
