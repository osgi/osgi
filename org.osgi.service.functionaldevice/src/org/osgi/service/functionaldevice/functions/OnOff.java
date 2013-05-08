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
 * On/Off device function represents turn on and off functionality. The function
 * doesn't provide an access to properties, there are only operations.
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
	 * Turn on device function operation. The operation name is
	 * {@link #OPERATION_TURN_ON}.
	 */
	public void turnOn();

	/**
	 * Turn off device function operation. The operation name is
	 * {@link #OPERATION_TURN_OFF}.
	 */
	public void turnOff();

}
