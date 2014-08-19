/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.util.Map;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;

/**
 * Basic implementation of the property metadata.
 */
public final class PropertyMetadataImpl implements PropertyMetadata {

	private final Map		metadata;
	private final Object	resolution;
	private final FunctionData[]	enumValues;
	private final FunctionData	minValue;
	private final FunctionData	maxValue;

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
			FunctionData[] enumValues,
			FunctionData minValue,
			FunctionData maxValue) {
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

	public FunctionData[] getEnumValues(String unit) throws IllegalArgumentException {
		return this.enumValues;
	}

	public FunctionData getMinValue(String unit) throws IllegalArgumentException {
		return this.minValue;
	}

	public FunctionData getMaxValue(String unit) throws IllegalArgumentException {
		return this.maxValue;
	}

}
