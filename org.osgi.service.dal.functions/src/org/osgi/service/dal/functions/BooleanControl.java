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
 * {@code BooleanControl} function provides a boolean control support. The
 * eventable function state is accessible with {@link #getData()} getter and
 * {@link #setData(boolean)} setter. The state can be reversed with
 * {@link #inverse()} method, can be set to {@code true} value with
 * {@link #setTrue()} method and can be set to {@code false} value with
 * {@link #setFalse()} method.
 * <p>
 * The control type can be:
 * <ul>
 * <li>{@link Types#LIGHT}</li>
 * <li>{@link Types#DOOR}</li>
 * <li>{@link Types#WINDOW}</li>
 * <li>{@link Types#POWER}</li>
 * <li>other type defined in {@link Types}</li>
 * <li>custom - vendor specific type</li>
 * </ul>
 * 
 * @see BooleanData
 */
public interface BooleanControl extends Function {

	/**
	 * Specifies the inverse operation name. The operation can be executed with
	 * {@link #inverse()} method.
	 */
	public static final String	OPERATION_INVERSE	= "inverse";

	/**
	 * Specifies the operation name, which sets the control state to
	 * {@code true} value. The operation can be executed with {@link #setTrue()}
	 * method.
	 */
	public static final String	OPERATION_SET_TRUE	= "setTrue";

	/**
	 * Specifies the operation name, which sets the control state to
	 * {@code false} value. The operation can be executed with
	 * {@link #setFalse()} method.
	 */
	public static final String	OPERATION_SET_FALSE	= "setFalse";

	/**
	 * Specifies the state property name. The eventable property value is
	 * accessible with {@link #getData()} method.
	 * 
	 * @see BooleanData
	 */
	public static final String	PROPERTY_DATA		= "data";

	/**
	 * Returns the current state of {@code BooleanControl}. It's a getter method
	 * for {@link #PROPERTY_DATA} property.
	 * 
	 * @return The current state of {@code BooleanControl}.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see BooleanData
	 * @see BooleanControl#PROPERTY_DATA
	 */
	public BooleanData getData() throws DeviceException;

	/**
	 * Sets the {@code BooleanControl} state to the specified value. It's setter
	 * method for {@link #PROPERTY_DATA} property.
	 * 
	 * @param data The new function value.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * @throws IllegalArgumentException If there is an invalid argument.
	 * 
	 * @see BooleanControl#PROPERTY_DATA
	 */
	public void setData(boolean data) throws DeviceException;

	/**
	 * Reverses the {@code BooleanControl} state. If the current state
	 * represents {@code true} value, it'll be changed to {@code false}. If the
	 * current state represents {@code false} value, it'll be changed to
	 * {@code true}. The operation name is {@link #OPERATION_INVERSE}.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void inverse() throws DeviceException;

	/**
	 * Sets the {@code BooleanControl} state to {@code true} value. The
	 * operation name is {@link #OPERATION_SET_TRUE}.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void setTrue() throws DeviceException;

	/**
	 * Sets the {@code BooleanControl} state to {@code false} value. The
	 * operation name is {@link #OPERATION_SET_FALSE}.
	 * 
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void setFalse() throws DeviceException;
}
