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

package org.osgi.services.functionaldevice.functions;

/**
 * Binary Switch device function has a property state and can move the state
 * from on to off and vice versa. The state is accessible with
 * {@link #getState()} getter. The switch state can be reversed with
 * {@link #toggle()} method. {@link #STATE_ON} state can be reached with the
 * inherited method {@link #turnOn()}. {@link #STATE_OFF} state can be reached
 * with the inherited method {@link #turnOff()}.
 */
public interface BinarySwitch extends OnOff {

	/**
	 * Specifies the toggle operation name. The operation can be executed with
	 * {@link #toggle()} method.
	 */
	public static final String	OPERATION_TOGGLE	= "toggle";

	/**
	 * Specifies the state property name. The property can be read with
	 * {@link #getState()} method.
	 */
	public static final String	PROPERTY_STATE		= "state";

	/**
	 * Specifies the on state property value.
	 */
	public static final boolean	STATE_ON			= true;

	/**
	 * Specifies the off state property value.
	 */
	public static final boolean	STATE_OFF			= false;

	/**
	 * Reverses the current state of the binary switch. If the current state is
	 * {@link #STATE_ON}, it'll be reversed to {@link #STATE_OFF}. If the
	 * current state is {@link #STATE_OFF}, it'll be reversed to
	 * {@link #STATE_ON}.
	 */
	public void toggle();

	/**
	 * Returns the state of the binary switch. It's a getter method for
	 * {@link #PROPERTY_STATE} property.
	 * 
	 * @return The state of the binary switch.
	 */
	public boolean getState();

}
