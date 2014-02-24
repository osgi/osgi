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
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.data.BooleanData;

/**
 * <code>BooleanSensor</code> function provides boolean sensor monitoring. It
 * reports its state when an important event is available. The state is
 * accessible with {@link #getData()} getter. There are no operations.
 * <p>
 * As an example, the function is easily mappable to ZigBee Occupancy Sensing
 * cluster and Z-Wave Binary Sensor command class.
 * 
 * The sensor type can be:
 * <ul>
 * <li>{@link Types#TYPE_LIGHT}</li>
 * <li>{@link Types#TYPE_GAS}</li>
 * <li>{@link Types#TYPE_SMOKE}</li>
 * <li>{@link Types#TYPE_DOOR}</li>
 * <li>{@link Types#TYPE_WINDOW}</li>
 * <li>{@link Types#TYPE_POWER}</li>
 * <li>{@link Types#TYPE_RAIN}</li>
 * <li>{@link Types#TYPE_CONTACT}</li>
 * <li>{@link Types#TYPE_FIRE}</li>
 * <li>{@link Types#TYPE_OCCUPANCY}</li>
 * <li>{@link Types#TYPE_WATER}</li>
 * <li>{@link Types#TYPE_MOTION}</li>
 * <li>other type defined in {@link Types}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see BooleanData
 */
public interface BooleanSensor extends Function {

	/**
	 * Specifies the state property name. The property value is accessible with
	 * {@link #getData()} getter.
	 */
	public static final String	PROPERTY_DATA	= "data";

	/**
	 * Returns the <code>BooleanSensor</code>current state. It's a getter method
	 * for {@link #PROPERTY_DATA} property.
	 * 
	 * @return The <code>BooleanSensor</code> current state.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see BooleanData
	 */
	public BooleanData getData() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

}
