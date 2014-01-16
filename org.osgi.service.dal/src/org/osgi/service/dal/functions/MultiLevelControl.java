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

package org.osgi.service.dal.functions;

import java.math.BigDecimal;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.DeviceFunction;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * <code>MultiLevelControl</code> Device Function provides multi-level control
 * support. The function level is accessible with {@link #getData()} getter,
 * {@link #setData(BigDecimal)} setter and {@link #setData(BigDecimal, String)}
 * setter.
 * <p>
 * As an example, the function is easily mappable to ZigBee Level Control and
 * Z-Wave Multilevel Switch command class.
 * 
 * The control type can be:
 * <ul>
 * <li>{@link Types#TYPE_LIGHT}</li>
 * <li>{@link Types#TYPE_TEMPERATURE}</li>
 * <li>{@link Types#TYPE_FLOW}</li>
 * <li>{@link Types#TYPE_PRESSURE}</li>
 * <li>{@link Types#TYPE_HUMIDITY}</li>
 * <li>{@link Types#TYPE_GAS}</li>
 * <li>{@link Types#TYPE_SMOKE}</li>
 * <li>{@link Types#TYPE_DOOR}</li>
 * <li>{@link Types#TYPE_WINDOW}</li>
 * <li>{@link Types#TYPE_LIQUID}</li>
 * <li>{@link Types#TYPE_POWER}</li>
 * <li>{@link Types#TYPE_NOISINESS}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see LevelData
 */
public interface MultiLevelControl extends DeviceFunction {

	/**
	 * Specifies the level property name. The property can be read with
	 * {@link #getData()} getter and can be set with
	 * {@link #setData(BigDecimal)} or {@link #setData(BigDecimal, String)}
	 * setters.
	 */
	public static final String	PROPERTY_DATA	= "data";

	/**
	 * Returns <code>MultiLevelControl</code> level. It's a getter method for
	 * {@link #PROPERTY_DATA} property.
	 * 
	 * @return <code>MultiLevelControl</code> level.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device function service object has
	 *         already been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see LevelData
	 */
	public LevelData getData() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets <code>MultiLevelControl</code> level to the specified value. It's a
	 * setter method for {@link #PROPERTY_DATA} property.
	 * 
	 * @param level The new control level.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device function service object has
	 *         already been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void setData(BigDecimal level) throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets <code>MultiLevelControl</code> level and unit to the specified
	 * values. It's a setter method for {@link #PROPERTY_DATA} property.
	 * 
	 * @param level The new control level.
	 * @param unit The level unit.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device function service object has
	 *         already been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void setData(BigDecimal level, String unit) throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

}
