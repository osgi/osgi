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
import org.osgi.service.dal.functions.data.BooleanData;

/**
 * {@code BooleanSensor} function provides boolean sensor monitoring. It reports
 * its state when an important event is available. The eventable state is
 * accessible with {@link #getData()} getter. There are no operations.
 * <p>
 * The sensor type can be:
 * <ul>
 * <li>{@link Types#LIGHT}</li>
 * <li>{@link Types#GAS}</li>
 * <li>{@link Types#SMOKE}</li>
 * <li>{@link Types#DOOR}</li>
 * <li>{@link Types#WINDOW}</li>
 * <li>{@link Types#POWER}</li>
 * <li>{@link Types#RAIN}</li>
 * <li>{@link Types#CONTACT}</li>
 * <li>{@link Types#FIRE}</li>
 * <li>{@link Types#OCCUPANCY}</li>
 * <li>{@link Types#WATER}</li>
 * <li>{@link Types#MOTION}</li>
 * <li>other type defined in {@link Types}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see BooleanData
 */
public interface BooleanSensor extends Function {

	/**
	 * Specifies the state property name. The eventable property value is
	 * accessible with {@link #getData()} getter.
	 */
	public static final String	PROPERTY_DATA	= "data";

	/**
	 * Returns the {@code BooleanSensor} current state. It's a getter method for
	 * {@link #PROPERTY_DATA} property.
	 * 
	 * @return The {@code BooleanSensor} current state.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see BooleanData
	 */
	public BooleanData getData() throws DeviceException;
}
