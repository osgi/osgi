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

package org.osgi.service.functionaldevice.functions;

import org.osgi.service.functionaldevice.DeviceFunction;
import org.osgi.service.functionaldevice.FunctionalDeviceException;

/**
 * <code>MultiLevelSensor</code> Device Function provides multi-level sensor
 * monitoring. It reports its state when an important event is available. The
 * state is accessible with {@link #getState()} getter. There are no operations.
 * The Device Function name is
 * <code>org.osgi.service.functionaldevice.functions.MultiLevelSensor</code>.
 */
public interface MultiLevelSensor extends DeviceFunction {

	/**
	 * Specifies the state property name. The property can be read with
	 * {@link #getState()} method.
	 */
	public static final String	PROPERTY_STATE	= "state";

	/**
	 * Returns the state of the Multi Level Sensor. It's a getter method for
	 * {@link #PROPERTY_STATE} property.
	 * 
	 * @return The state of the Multi Level Sensor.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public double getState() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

}
