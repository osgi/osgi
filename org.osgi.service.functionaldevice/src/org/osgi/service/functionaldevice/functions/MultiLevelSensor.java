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

import org.osgi.service.functionaldevice.DeviceException;
import org.osgi.service.functionaldevice.DeviceFunction;

/**
 * <code>MultiLevelSensor</code> Device Function provides multi-level sensor
 * monitoring. It reports its state when an important event is available. The
 * state is accessible with {@link #getState()} getter. There are no operations.
 * 
 * @see MultiLevelData
 */
public interface MultiLevelSensor extends DeviceFunction {

	/**
	 * Specifies the state property name. The property can be read with
	 * {@link #getState()} getter.
	 * 
	 * @see MultiLevelData
	 */
	public static final String PROPERTY_STATE = "state";

	/**
	 * Returns the <code>MultiLevelSensor</code> current state. It's a getter
	 * method for {@link #PROPERTY_STATE} property.
	 * 
	 * @return The <code>MultiLevelSensor</code> current state.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 * 
	 * @see MultiLevelData
	 */
	public MultiLevelData getState() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

}
