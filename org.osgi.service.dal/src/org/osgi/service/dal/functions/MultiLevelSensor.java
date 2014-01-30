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

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.DeviceFunction;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * <code>MultiLevelSensor</code> Device Function provides multi-level sensor
 * monitoring. It reports its state when an important event is available. The
 * state is accessible with {@link #getData()} getter. There are no operations.
 * <p>
 * As an example, the function is easily mappable to ZigBee Illuminance
 * Measurement, Temperature Measurement, Pressure Measurement, Flow Measurement
 * and Relative Humidity Measurement cluster and Z-Wave Multilevel Sensor
 * command class.
 * 
 * The sensor type can be:
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
 * <li>{@link Types#TYPE_RAIN}</li>
 * <li>other type defined in {@link Types}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see LevelData
 */
public interface MultiLevelSensor extends DeviceFunction {

	/**
	 * Specifies the state property name. The property can be read with
	 * {@link #getData()} getter.
	 * 
	 * @see LevelData
	 */
	public static final String	PROPERTY_DATA	= "data";

	/**
	 * Returns the <code>MultiLevelSensor</code> current state. It's a getter
	 * method for {@link #PROPERTY_DATA} property.
	 * 
	 * @return The <code>MultiLevelSensor</code> current state.
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

}
