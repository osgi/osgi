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
 * <code>MultiLevelSwitch</code> Device Function provides multi-level switch
 * control. It has a level, can increase or decrease the level with a given step
 * and can set the level to a specific value. The level is accessible with
 * {@link #getLevel()} getter and can be set with {@link #setLevel(int)} setter.
 * The step is accessible with {@link #getStep()} getter. {@link #stepUp()} and
 * {@link #stepDown()} can increase and decrease the level with a given step.
 * The Device Function name is
 * <code>org.osgi.service.functionaldevice.functions.MultiLevelSwitch</code>.
 */
public interface MultiLevelSwitch extends DeviceFunction {

	/**
	 * Specifies the step down operation name. The operation can be executed
	 * with {@link #stepDown()} method.
	 */
	public static final String	OPERATION_STEP_DOWN	= "stepDown";

	/**
	 * Specifies the step up operation name. The operation can be executed with
	 * {@link #stepUp()} method.
	 */
	public static final String	OPERATION_STEP_UP	= "stepUp";

	/**
	 * Specifies the level property name. The property can be read with
	 * {@link #getLevel()} getter and can be set with {@link #setLevel(int)}
	 * setter.
	 */
	public static final String	PROPERTY_LEVEL		= "level";

	/**
	 * Specifies the step property name. The property can be read with
	 * {@link #getStep()} getter.
	 */
	public static final String	PROPERTY_STEP		= "step";

	/**
	 * Returns the Multi Level Switch level. It's a getter method for
	 * {@link #PROPERTY_LEVEL} property.
	 * 
	 * @return The level of the Multi Level Switch.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public int getLevel() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Sets the level of the Multi Level Switch. It's a setter method for
	 * {@link #PROPERTY_LEVEL} property.
	 * 
	 * @param level The new level of the Multi Level Switch.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void setLevel(int level) throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Returns the step of the Multi Level Switch. It's a getter method for
	 * {@link #PROPERTY_STEP} property. The step is used by {@link #stepUp()}
	 * and {@link #stepDown()} method to increase and decrease the current
	 * level.
	 * 
	 * @return The step of the Multi Level Switch.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public int getStep() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Moves the current level of the switch one step down.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void stepDown() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

	/**
	 * Moves the current level of the switch one step up.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this device service object has already
	 *         been unregistered.
	 * @throws FunctionalDeviceException If an operation error is available.
	 */
	public void stepUp() throws UnsupportedOperationException, IllegalStateException, FunctionalDeviceException;

}
