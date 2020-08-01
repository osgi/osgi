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

package org.osgi.service.dal;

import java.util.Map;

/**
 * Contains metadata about a function property, a function operation parameter
 * or a function operation return value.
 * 
 * The access to the function properties is a bitmap value of {@link #ACCESS}
 * metadata key. Function properties can be accessed in three ways. Any
 * combinations between them are possible:
 * <ul>
 * <li>
 * {@link #ACCESS_READABLE} - available for all properties, which can be read.
 * Function must provide a getter method for an access to the property value.</li>
 * <li>
 * {@link #ACCESS_WRITABLE} - available for all properties, which can be
 * modified. Function must provide a setter method for a modification of the
 * property value.</li>
 * <li>
 * {@link #ACCESS_EVENTABLE} - available for all properties, which can report
 * the property value. {@link FunctionEvent}s are sent on property change.</li>
 * </ul>
 * 
 * @see Function
 * @see PropertyMetadata
 */
public interface PropertyMetadata {

	/**
	 * Marks the readable function properties. The flag can be used as a part of
	 * bitmap value of {@link #ACCESS}. The readable access mandates function to
	 * provide a property getter method.
	 * 
	 * @see Function
	 */
	public static final int		ACCESS_READABLE		= 1;

	/**
	 * Marks the writable function properties. The flag can be used as a part of
	 * bitmap value of {@link #ACCESS}. The writable access mandates function to
	 * provide a property setter methods.
	 * 
	 * @see Function
	 */
	public static final int		ACCESS_WRITABLE		= 2;

	/**
	 * Marks the eventable function properties. The flag can be used as a part
	 * of bitmap value of {@link #ACCESS}.
	 * 
	 * @see Function
	 */
	public static final int		ACCESS_EVENTABLE	= 4;

	/**
	 * Metadata key, which value represents the access to the function property.
	 * The property value is a bitmap of {@code Integer} type. The bitmap can be
	 * any combination of:
	 * <ul>
	 * <li>{@link #ACCESS_READABLE}</li>
	 * <li>{@link #ACCESS_WRITABLE}</li>
	 * <li>{@link #ACCESS_EVENTABLE}</li>
	 * </ul>
	 * For example, value {@code Integer(3)} means that the property is readable
	 * and writable, but not eventable.
	 * <p>
	 * The property access is available only for function properties and it's
	 * missing for the operation parameters.
	 */
	public static final String	ACCESS				= "access";

	/**
	 * Metadata key, which value represents the property description. The
	 * property value type is {@code java.lang.String}.
	 */
	public static final String	DESCRIPTION			= "description";

	/**
	 * Metadata key, which value represents the property supported units. The
	 * property value type is {@code java.lang.String[]}. The array first
	 * element at index {@code 0} represents the default unit. Each unit must
	 * follow those rules:
	 * <ul>
	 * <li>The International System of Units must be used where it's applicable.
	 * For example, kg for kilogram and km for kilometer.</li>
	 * <li>If the unit name matches to an Unicode symbol name, the Unicode
	 * symbol must be used. For example, the degree unit matches to the Unicode
	 * degree sign ({@code \u00B0}).</li>
	 * <li>If the unit name doesn't match to an Unicode symbol, the unit symbol
	 * must be built by Unicode Basic Latin block of characters, superscript and
	 * subscript characters. For example, watt per square meter steradian is
	 * built by {@code W/(m\u00B2 sr)}.</li>
	 * </ul>
	 * If those rules cannot be applied to the unit symbol, custom rules are
	 * allowed. A set of predefined unit symbols are available in
	 * {@link SIUnits} interface.
	 */
	public static final String	UNITS				= "units";

	/**
	 * Returns metadata about the function property or operation parameter. The
	 * keys of the {@code java.util.Map} result must be of
	 * {@code java.lang.String} type. Possible keys:
	 * <ul>
	 * <li>{@link #DESCRIPTION} - doesn't depend on the given unit.</li>
	 * <li>{@link #ACCESS} - available only for function property and missing
	 * for function operation parameters. It doesn't depend on the given unit.</li>
	 * <li>{@link #UNITS} - doesn't depend on the given unit.</li>
	 * <li>custom key - can depend on the unit. Organizations that want to use
	 * custom keys that do not clash with OSGi Alliance defined should prefix
	 * their keys in own namespace.</li>
	 * </ul>
	 * 
	 * @param unit The unit to align the metadata if it's applicable. It can be
	 *        null, which means that the default unit will be used.
	 * 
	 * @return The property metadata or {@code null} if no such metadata is
	 *         available.
	 */
	public Map<String, ? > getMetadata(String unit);

	/**
	 * Returns the difference between two values in series. For example, if the
	 * range is [0, 100], the step can be 10.
	 * 
	 * @param unit The unit to align the step, can be {@code null}.
	 * 
	 * @return The step according to the specified unit or {@code null} if no
	 *         step is supported.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public FunctionData getStep(String unit);

	/**
	 * Returns the property possible values according to the specified unit. If
	 * the unit is {@code null}, the values set is aligned to the default unit.
	 * If there is no such set of supported values, {@code null} is returned.
	 * The values must be sorted in increasing order.
	 * 
	 * @param unit The unit to align the supported values, can be {@code null}.
	 * 
	 * @return The supported values according to the specified unit or
	 *         {@code null} if no such values are supported. The values must be
	 *         sorted in increasing order.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public FunctionData[] getEnumValues(String unit);

	/**
	 * Returns the property minimum value according to the specified unit. If
	 * the unit is {@code null}, the minimum value is aligned to the default
	 * unit. If there is no minimum value, {@code null} is returned.
	 * 
	 * @param unit The unit to align the minimum value, can be {@code null} .
	 * 
	 * @return The minimum value according to the specified unit or {@code null}
	 *         if no minimum value is supported.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public FunctionData getMinValue(String unit);

	/**
	 * Returns the property maximum value according to the specified unit. If
	 * the unit is {@code null}, the maximum value is aligned to the default
	 * unit. If there is no maximum value, {@code null} is returned.
	 * 
	 * @param unit The unit to align the maximum value, can be {@code null} .
	 * 
	 * @return The maximum value according to the specified unit or {@code null}
	 *         if no maximum value is supported.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public FunctionData getMaxValue(String unit);
}
