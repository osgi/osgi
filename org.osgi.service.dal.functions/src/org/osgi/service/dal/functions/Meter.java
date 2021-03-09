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

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * {@code Meter} function can measure metering information. The function
 * provides these properties:
 * <ul>
 * <li>{@link #PROPERTY_CURRENT} - eventable property accessible with
 * {@link #getCurrent()} getter;</li>
 * <li>{@link #PROPERTY_TOTAL} - eventable property accessible with
 * {@link #getTotal()} getter.</li>
 * </ul>
 * <p>
 * The sensor type can be:
 * <ul>
 * <li>{@link Types#PRESSURE}</li>
 * <li>{@link Types#GAS}</li>
 * <li>{@link Types#POWER}</li>
 * <li>{@link Types#WATER}</li>
 * <li>{@link Types#HEAT}</li>
 * <li>{@link Types#COLD}</li>
 * <li>other type defined in {@link Types}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see LevelData
 */
public interface Meter extends Function {

	/**
	 * Represents the metering consumption flow. It can be used as
	 * {@link #SERVICE_FLOW} property value.
	 */
	public static final String	FLOW_IN				= "in";

	/**
	 * Represents the metering production flow. It can be used as
	 * {@link #SERVICE_FLOW} property value.
	 */
	public static final String	FLOW_OUT			= "out";

	/**
	 * The service property value contains the metering flow. It's an optional
	 * property and available only if it's supported by the meter. The value
	 * type is {@code java.lang.String}. Possible property values:
	 * <ul>
	 * <li>{@link #FLOW_IN}</li>
	 * <li>{@link #FLOW_OUT}</li>
	 * </ul>
	 */
	public static final String	SERVICE_FLOW		= "dal.meter.flow";

	/**
	 * Specifies the current consumption or production property name. The
	 * eventable property can be read with {@link #getCurrent()} getter.
	 */
	public static final String	PROPERTY_CURRENT	= "current";

	/**
	 * Specifies the total consumption or production property name. The
	 * eventable property can be read with {@link #getTotal()} getter.
	 */
	public static final String	PROPERTY_TOTAL		= "total";

	/**
	 * Returns the current metering info. It's a getter method for
	 * {@link #PROPERTY_CURRENT} property.
	 * 
	 * @return The current metering info.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see LevelData
	 */
	public LevelData getCurrent() throws DeviceException;

	/**
	 * Returns the total metering info. It's a getter method for
	 * {@link #PROPERTY_TOTAL} property.
	 * 
	 * @return The total metering info.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see LevelData
	 */
	public LevelData getTotal() throws DeviceException;
}
