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

/**
 * Multi Level Switch device function has a level, can increase or decrease the
 * level with a given step and can set the level to a given value. The level is
 * accessible with {@link #getLevel()} getter and can be set with
 * {@link #setLevel(int)} setter. The step is accessible with {@link #getStep()}
 * getter. {@link #stepUp()} and {@link #stepDown()} can increase and decrease
 * the level with a given step.
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
	 */
	public int getLevel();

	/**
	 * Sets the level of the Multi Level Switch. It's a setter method for
	 * {@link #PROPERTY_LEVEL} property.
	 * 
	 * @param level The new level of the Multi Level Switch.
	 */
	public void setLevel(int level);

	/**
	 * Returns the step of the Multi Level Switch. It's a getter method for
	 * {@link #PROPERTY_STEP} property. The step is used by {@link #stepUp()}
	 * and {@link #stepDown()} method to increase and decrease the current
	 * level.
	 * 
	 * @return The step of the Multi Level Switch.
	 */
	public int getStep();

	/**
	 * Moves the current level of the switch one step down.
	 */
	public void stepDown();

	/**
	 * Moves the current level of the switch one step up.
	 */
	public void stepUp();

}
