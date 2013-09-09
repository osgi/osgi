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
 * <code>BinaryControl</code> Device Function provides a binary control support.
 * The function state is accessible with {@link #getState()} getter and
 * {@link #setState(BinaryData)} setter. The state can be reversed with
 * {@link #reverse()} method, can be set to <code>true</code> value with
 * {@link #setTrue()} method and can be set to <code>false</code> value with
 * {@link #setFalse()} method.
 * 
 * @see BinaryData
 */
public interface BinaryControl extends DeviceFunction {

	/**
	 * Specifies the reverse operation name. The operation can be executed with
	 * {@link #reverse()} method.
	 */
	public static final String OPERATION_REVERSE = "reverse";

	/**
	 * Specifies the operation name, which sets the control state to
	 * <code>true</code> value. The operation can be executed with
	 * {@link #setTrue()} method.
	 */
	public static final String OPERATION_SET_TRUE = "setTrue";

	/**
	 * Specifies the operation name, which sets the control state to
	 * <code>false</code> value. The operation can be executed with
	 * {@link #setFalse()} method.
	 */
	public static final String OPERATION_SET_FALSE = "setFalse";

	/**
	 * Specifies the state property name. The property value is accessible with
	 * {@link #getState()} method.
	 * 
	 * @see BinaryData
	 */
	public static final String PROPERTY_STATE = "state";

	/**
	 * Returns the current state of <code>BinaryControl</code>. It's a getter
	 * method for {@link #PROPERTY_STATE} property.
	 * 
	 * @return The current state of the Binary Sensor.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 * 
	 * @see BinaryData
	 */
	public BinaryData getState() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets the state of <code>BinaryControl</code>. It's a setter method for
	 * {@link #PROPERTY_STATE} property.
	 * 
	 * @param data
	 *            The new <code>BinaryControl</code> state.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 * 
	 * @see BinaryData
	 */
	public void setState(BinaryData data) throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Reverses the <code>BinaryControl</code> state. If the current state
	 * represents <code>true</code> value, it'll be reversed to
	 * <code>false</code>. If the current state represents <code>false</code>
	 * value, it'll be reversed to <code>true</code>. The operation name is
	 * {@link #OPERATION_REVERSE}.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 */
	public void reverse() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets the <code>BinaryControl</code> state to <code>true</code> value. The
	 * operation name is {@link #OPERATION_SET_TRUE}.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 */
	public void setTrue() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets the <code>BinaryControl</code> state to <code>false</code> value.
	 * The operation name is {@link #OPERATION_SET_FALSE}.
	 * 
	 * @throws UnsupportedOperationException
	 *             If the operation is not supported.
	 * @throws IllegalStateException
	 *             If this device function service object has already been
	 *             unregistered.
	 * @throws DeviceException
	 *             If an operation error is available.
	 */
	public void setFalse() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

}
