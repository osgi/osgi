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

package org.osgi.service.dal;

import java.util.Map;

/**
 * Contains metadata about Device Function property or Device Function operation
 * parameter.
 * 
 * The access to the Device Function properties is a bitmap value of
 * {@link #META_INFO_PROPERTY_ACCESS} metadata key. Device Function properties
 * can be accessed in three ways. Any combinations between them are possible:
 * <ul>
 * <li>
 * {@link #META_INFO_PROPERTY_ACCESS_READABLE} - available for all properties,
 * which can be read. Device Function must provide a getter method for an access
 * to the property value.</li>
 * <li>
 * {@link #META_INFO_PROPERTY_ACCESS_WRITABLE} - available for all properties,
 * which can be modified. Device Function must provide a setter method for a
 * modification of the property value.</li>
 * <li>
 * {@link #META_INFO_PROPERTY_ACCESS_EVENTABLE} - available for all properties,
 * which can report the property value. {@link DeviceFunctionEvent}s are sent on
 * property change.</li>
 * </ul>
 * 
 * @see DeviceFunction
 * @see PropertyMetadata
 */
public interface PropertyMetadata {

	/**
	 * Marks the readable Device Function properties. The flag can be used as a
	 * part of bitmap value of {@link #META_INFO_PROPERTY_ACCESS}. The readable
	 * access mandates Device Function to provide a property getter method.
	 * 
	 * @see DeviceFunction
	 */
	public static final int		META_INFO_PROPERTY_ACCESS_READABLE	= 1;

	/**
	 * Marks the writable Device Function properties. The flag can be used as a
	 * part of bitmap value of {@link #META_INFO_PROPERTY_ACCESS}. The writable
	 * access mandates Device Function to provide a property setter methods.
	 * 
	 * @see DeviceFunction
	 */
	public static final int		META_INFO_PROPERTY_ACCESS_WRITABLE	= 2;

	/**
	 * Marks the eventable Device Function properties. The flag can be used as a
	 * part of bitmap value of {@link #META_INFO_PROPERTY_ACCESS}.
	 * 
	 * @see DeviceFunction
	 */
	public static final int		META_INFO_PROPERTY_ACCESS_EVENTABLE	= 4;

	/**
	 * Metadata key, which value represents the access to the Device Function
	 * property. The property value is a bitmap of <code>Integer</code> type.
	 * The bitmap can be any combination of:
	 * <ul>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS_READABLE}</li>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS_WRITABLE}</li>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS_EVENTABLE}</li>
	 * </ul>
	 * For example, value Integer(3) means that the property is readable and
	 * writable, but not eventable.
	 * <p>
	 * The property access is available only for Device Function properties and
	 * it's missing for the operation parameters.
	 */
	public static final String	META_INFO_PROPERTY_ACCESS			= "property.access";

	/**
	 * Metadata key, which value represents the property description. The
	 * property value type is <code>java.lang.String</code>.
	 */
	public static final String	META_INFO_DESCRIPTION				= "description";

	/**
	 * Metadata key, which value represents the property supported units. The
	 * property value type is <code>java.lang.String[]</code>. Each unit must
	 * follow those rules:
	 * <ul>
	 * <li>The International System of Units must be used where it's applicable.
	 * For example, kg for kilogram and km for kilometre.</li>
	 * <li>If the unit name matches to an Unicode symbol name, the Unicode
	 * symbol must be used. For example, the degree unit matches to the Unicode
	 * degree sign (\u00B0).</li>
	 * <li>If the unit name doesn't match to an Unicode symbol, the unit symbol
	 * must be built by Unicode Basic Latin block of characters, superscript and
	 * subscript characters. For example, watt per square metre steradian is
	 * built by W/(m\u00B2 sr), where \u00B2 is Unicode superscript two.</li>
	 * </ul>
	 * If those rules cannot be applied to the unit symbol, custom rules are
	 * allowed. A set of predefined unit symbols are available in {@link Units}
	 * interface.
	 */
	public static final String	META_INFO_UNITS						= "units";

	/**
	 * Returns metadata about the Device Function property or operation
	 * parameter. The keys of the <code>java.util.Map</code> result must be of
	 * <code>java.lang.String</code> type. Possible keys:
	 * <ul>
	 * <li>{@link #META_INFO_DESCRIPTION} - doesn't depend on the given unit.</li>
	 * <li>{@link #META_INFO_PROPERTY_ACCESS} - available only for Device
	 * Function property and missing for Device FUnction operation parameters.
	 * It doesn't depend on the given unit.</li>
	 * <li>{@link #META_INFO_UNITS} - doesn't depend on the given unit.</li>
	 * <li>custom key - can depend on the unit.</li>
	 * </ul>
	 * 
	 * @param unit The unit to align the metadata if it's applicable. It can be
	 *        null, which means that the default unit will be used.
	 * 
	 * @return The property metadata or <code>null</code> if no such metadata is
	 *         available.
	 */
	public Map getMetadata(String unit);

	/**
	 * Returns the resolution value of specific range. For example, if the range
	 * is [0, 100], the resolution can be 10. That's the different between two
	 * values in series. The resolution type depends on the property type. If
	 * the property is using data bean like
	 * {@link org.osgi.service.dal.functions.data.LevelData}, the
	 * resolution will the <code>BigDecimal</code>.
	 * 
	 * @param unit The unit to align the resolution, can be <code>null</code>.
	 * 
	 * @return The resolution according to the specified unit or
	 *         <code>null</code> if no resolution is supported.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public Object getResolution(String unit) throws IllegalArgumentException;

	/**
	 * Returns the property possible values according to the specified unit. If
	 * the unit is <code>null</code>, the values set is aligned to the default
	 * unit. If there is no such set of supported values, <code>null</code> is
	 * returned. The values must be sorted in increasing order.
	 * 
	 * @param unit The unit to align the supported values, can be
	 *        <code>null</code>.
	 * 
	 * @return The supported values according to the specified unit or
	 *         <code>null</code> if no such values are supported. The values
	 *         must be sorted in increasing order.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public DeviceFunctionData[] getEnumValues(String unit) throws IllegalArgumentException;

	/**
	 * Returns the property minimum value according to the specified unit. If
	 * the unit is <code>null</code>, the minimum value is aligned to the
	 * default unit. If there is no minimum value, <code>null</code> is
	 * returned.
	 * 
	 * @param unit The unit to align the minimum value, can be <code>null</code>
	 *        .
	 * 
	 * @return The minimum value according to the specified unit or
	 *         <code>null</code> if no minimum value is supported.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public DeviceFunctionData getMinValue(String unit) throws IllegalArgumentException;

	/**
	 * Returns the property maximum value according to the specified unit. If
	 * the unit is <code>null</code>, the maximum value is aligned to the
	 * default unit. If there is no maximum value, <code>null</code> is
	 * returned.
	 * 
	 * @param unit The unit to align the maximum value, can be <code>null</code>
	 *        .
	 * 
	 * @return The maximum value according to the specified unit or
	 *         <code>null</code> if no maximum value is supported.
	 * 
	 * @throws IllegalArgumentException If the unit is not supported.
	 */
	public DeviceFunctionData getMaxValue(String unit) throws IllegalArgumentException;

}
