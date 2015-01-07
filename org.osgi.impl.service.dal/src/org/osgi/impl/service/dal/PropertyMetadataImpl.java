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

	private final Map				metadata;
	private final FunctionData		step;
	private final FunctionData[]	enumValues;
	private final FunctionData		minValue;
	private final FunctionData		maxValue;

	/**
	 * Constructs the property metadata with the specified arguments.
	 * 
	 * @param metadata Additional metadata.
	 * @param step The step.
	 * @param enumValues The supported values, if any.
	 * @param minValue The minimum value, if any.
	 * @param maxValue The maximum value, if any.
	 */
	public PropertyMetadataImpl(
			Map metadata,
			FunctionData step,
			FunctionData[] enumValues,
			FunctionData minValue,
			FunctionData maxValue) {
		this.metadata = metadata;
		this.step = step;
		this.enumValues = enumValues;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public Map getMetadata(String unit) {
		return this.metadata;
	}

	public FunctionData getStep(String unit) {
		return this.step;
	}

	public FunctionData[] getEnumValues(String unit) {
		return this.enumValues;
	}

	public FunctionData getMinValue(String unit) {
		return this.minValue;
	}

	public FunctionData getMaxValue(String unit) {
		return this.maxValue;
	}
}
