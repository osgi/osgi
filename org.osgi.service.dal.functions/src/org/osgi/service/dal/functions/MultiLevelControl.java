/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.dal.functions;

import java.math.BigDecimal;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * {@code MultiLevelControl} function provides multi-level control support. The
 * eventable function level is accessible with {@link #getData()} getter and
 * {@link #setData(BigDecimal, String)} setter.
 * <p>
 * The control type can be:
 * <ul>
 * <li>{@link Types#LIGHT}</li>
 * <li>{@link Types#TEMPERATURE}</li>
 * <li>{@link Types#FLOW}</li>
 * <li>{@link Types#PRESSURE}</li>
 * <li>{@link Types#HUMIDITY}</li>
 * <li>{@link Types#GAS}</li>
 * <li>{@link Types#SMOKE}</li>
 * <li>{@link Types#DOOR}</li>
 * <li>{@link Types#WINDOW}</li>
 * <li>{@link Types#LIQUID}</li>
 * <li>{@link Types#POWER}</li>
 * <li>{@link Types#NOISINESS}</li>
 * <li>other type defined in {@link Types}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see LevelData
 */
public interface MultiLevelControl extends Function {

	/**
	 * Specifies the level property name. The eventable property can be read
	 * with {@link #getData()} getter and can be set with
	 * {@link #setData(BigDecimal, String)} setters.
	 */
	public static final String	PROPERTY_DATA	= "data";

	/**
	 * Returns {@code MultiLevelControl} level. It's a getter method for
	 * {@link #PROPERTY_DATA} property.
	 * 
	 * @return {@code MultiLevelControl} level.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see LevelData
	 */
	public LevelData getData() throws DeviceException;

	/**
	 * Sets {@code MultiLevelControl} level according to the specified unit.
	 * It's a setter method for {@link #PROPERTY_DATA} property.
	 * 
	 * @param level The new control level.
	 * @param unit The level unit.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * @throws IllegalArgumentException If there is an invalid argument.
	 */
	public void setData(BigDecimal level, String unit) throws DeviceException;
}
