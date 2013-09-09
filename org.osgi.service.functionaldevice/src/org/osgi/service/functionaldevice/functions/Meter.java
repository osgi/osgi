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
 * <code>Meter</code> Device Function can measure metering information. The
 * function provides three properties and one operation:
 * <ul>
 * <li>{@link #PROPERTY_CURRENT}</li> - property accessible with
 * {@link #getCurrent()} getter;
 * <li>{@link #PROPERTY_TOTAL}</li> - property accessible with
 * {@link #getTotal()} getter;
 * <li>{@link #PROPERTY_FLOW}</li> - property accessible with
 * {@link #getTotal()} getter;
 * <li>{@link #OPERATION_RESET_TOTAL}</li> - operation can be executed with
 * {@link #resetTotal()}.
 * </ul>
 * 
 * @see MultiLevelData
 */
public interface Meter extends DeviceFunction {

	/**
	 * Represents the metering consumption flow. It can be used as
	 * {@link #PROPERTY_FLOW} property value.
	 */
	public static final String FLOW_IN = "in";

	/**
	 * Represents the metering generation flow. It can be used as
	 * {@link #PROPERTY_FLOW} property value.
	 */
	public static final String FLOW_OUT = "out";

	/**
	 * Specifies the metering flow property name. The property can be read with
	 * {@link #getFlow()} getter.
	 */
	public static final String PROPERTY_FLOW = "flow";

	/**
	 * Specifies the current consumption property name. The property can be read
	 * with {@link #getCurrent()} getter.
	 */
	public static final String PROPERTY_CURRENT = "current";

	/**
	 * Specifies the total consumption property name. It has been measured since
	 * the last call of {@link #resetTotal()} or device initial run. The
	 * property can be read with {@link #getTotal()} getter.
	 */
	public static final String PROPERTY_TOTAL = "total";

	/**
	 * Specifies the reset total operation name. The operation can be executed
	 * with {@link #resetTotal()} method.
	 */
	public static final String OPERATION_RESET_TOTAL = "resetTotal";

	/**
	 * Returns the metering flow. It's a getter method for
	 * {@link #PROPERTY_FLOW} property.
	 * 
	 * @return The metering flow.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 */
	public String getFlow() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Returns the current metering info. It's a getter method for
	 * {@link #PROPERTY_CURRENT} property.
	 * 
	 * @return The current metering info.
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
	public MultiLevelData getCurrent() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Returns the total metering info. It's a getter method for
	 * {@link #PROPERTY_TOTAL} property.
	 * 
	 * @return The total metering info.
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
	public MultiLevelData getTotal() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Resets the total metering info.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 */
	public void resetTotal() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

}
