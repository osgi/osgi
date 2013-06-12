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
 * <code>OnOff</code> Device Function represents turn on and off functionality.
 * The function doesn't provide an access to properties, there are only
 * operations. The Device Function name is
 * <code>org.osgi.service.functionaldevice.functions.OnOff</code>.
 */
public interface OnOff extends DeviceFunction {

	/**
	 * Specifies the turn on operation name. The operation can be executed with
	 * {@link #turnOn()} method.
	 */
	public static final String	OPERATION_TURN_ON	= "turnOn";

	/**
	 * Specifies the turn off operation name. The operation can be executed with
	 * {@link #turnOff()} method.
	 */
	public static final String	OPERATION_TURN_OFF	= "turnOff";

	/**
	 * Turn on Device Function operation. The operation name is
	 * {@link #OPERATION_TURN_ON}.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void turnOn() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Turn off Device Function operation. The operation name is
	 * {@link #OPERATION_TURN_OFF}.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void turnOff() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

}
