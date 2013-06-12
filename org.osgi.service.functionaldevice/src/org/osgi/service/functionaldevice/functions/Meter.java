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
 * <code>Meter</code> Device Function can measure metering information. The
 * function provides two properties and one operation:
 * <ul>
 * <li>{@link #PROPERTY_CURRENT}</li> - property accessible with
 * {@link #getCurrent()} getter;
 * <li>{@link #PROPERTY_TOTAL}</li> - property accessible with
 * {@link #getTotal()} getter;
 * <li>{@link #OPERATION_RESET_TOTAL}</li> - operation can be executed with
 * {@link #resetTotal()}.
 * </ul>
 * The Device Function name is
 * <code>org.osgi.service.functionaldevice.functions.Meter</code>.
 */
public interface Meter extends DeviceFunction {

	/**
	 * Specifies the reset total operation name. The operation can be executed
	 * with {@link #resetTotal()} method.
	 */
	public static final String	OPERATION_RESET_TOTAL	= "resetTotal";

	/**
	 * Specifies the current info property name. The property can be read with
	 * {@link #getCurrent()} method.
	 */
	public static final String	PROPERTY_CURRENT		= "current";

	/**
	 * Specifies the total info property name. The property can be read with
	 * {@link #getTotal()} method.
	 */
	public static final String	PROPERTY_TOTAL			= "total";

	/**
	 * Returns the current metering info. It's a getter method for
	 * {@link #PROPERTY_CURRENT} property.
	 * 
	 * @return The current metering info.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public double getCurrent() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Returns the total metering info. It's a getter method for
	 * {@link #PROPERTY_TOTAL} property.
	 * 
	 * @return The total metering info.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public double getTotal() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Resets the total metering info.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void resetTotal() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

}
