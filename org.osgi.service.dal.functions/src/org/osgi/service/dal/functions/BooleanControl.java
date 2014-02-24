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
 * <code>BooleanControl</code> function provides a boolean control support. The
 * function state is accessible with {@link #getData()} getter and
 * {@link #setData(boolean)} setter. The state can be reversed with
 * {@link #reverse()} method, can be set to <code>true</code> value with
 * {@link #setTrue()} method and can be set to <code>false</code> value with
 * {@link #setFalse()} method.
 * <p>
 * As an example, the function is easily mappable to ZigBee OnOff cluster and
 * Z-Wave Binary Switch command class.
 * 
 * The control type can be:
 * <ul>
 * <li>{@link Types#TYPE_LIGHT}</li>
 * <li>{@link Types#TYPE_DOOR}</li>
 * <li>{@link Types#TYPE_WINDOW}</li>
 * <li>{@link Types#TYPE_POWER}</li>
 * <li>other type defined in {@link Types}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see BooleanData
 */
public interface BooleanControl extends Function {

	/**
	 * Specifies the reverse operation name. The operation can be executed with
	 * {@link #reverse()} method.
	 */
	public static final String	OPERATION_REVERSE	= "reverse";

	/**
	 * Specifies the operation name, which sets the control state to
	 * <code>true</code> value. The operation can be executed with
	 * {@link #setTrue()} method.
	 */
	public static final String	OPERATION_SET_TRUE	= "setTrue";

	/**
	 * Specifies the operation name, which sets the control state to
	 * <code>false</code> value. The operation can be executed with
	 * {@link #setFalse()} method.
	 */
	public static final String	OPERATION_SET_FALSE	= "setFalse";

	/**
	 * Specifies the state property name. The property value is accessible with
	 * {@link #getData()} method.
	 * 
	 * @see BooleanData
	 */
	public static final String	PROPERTY_DATA		= "data";

	/**
	 * Returns the current state of <code>BooleanControl</code>. It's a getter
	 * method for {@link #PROPERTY_DATA} property.
	 * 
	 * @return The current state of <code>BooleanControl</code>.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see BooleanData
	 * @see BooleanControl#PROPERTY_DATA
	 */
	public BooleanData getData() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets the <code>BooleanControl</code> state to the specified value. It's
	 * setter method for {@link #PROPERTY_DATA} property.
	 * 
	 * @param data The new function value.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * @throws IllegalArgumentException If there is an invalid argument.
	 * 
	 * @see BooleanControl#PROPERTY_DATA
	 */
	public void setData(boolean data) throws UnsupportedOperationException,
			IllegalStateException, DeviceException, IllegalArgumentException;

	/**
	 * Reverses the <code>BooleanControl</code> state. If the current state
	 * represents <code>true</code> value, it'll be reversed to
	 * <code>false</code>. If the current state represents <code>false</code>
	 * value, it'll be reversed to <code>true</code>. The operation name is
	 * {@link #OPERATION_REVERSE}.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void reverse() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets the <code>BooleanControl</code> state to <code>true</code> value.
	 * The operation name is {@link #OPERATION_SET_TRUE}.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void setTrue() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets the <code>BooleanControl</code> state to <code>false</code> value.
	 * The operation name is {@link #OPERATION_SET_FALSE}.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void setFalse() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

}
