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

package org.osgi.impl.service.dal.simulator;

import java.util.Dictionary;

import org.osgi.service.dal.Device;

/**
 * The device simulator is registered in the OSGi service registry.
 */
public interface DeviceSimulator {

	/**
	 * Registers a new device with the specified properties. If the function
	 * properties are not {@code null}, they are registered to the device in the
	 * correct order. The function interface is specified as a string value to
	 * {@link Constants#OBJECTCLASS} key.
	 * 
	 * @param deviceProps The device properties. They cannot be {@code null}.
	 * @param functionProps The function properties. They can be {@code null}.
	 * 
	 * @return The registered device.
	 * 
	 * @throws NullPointerException If the device properties are {@code null}.
	 * @throws IllegalArgumentException If the function type is not supported.
	 * @throws IllegalStateException If the device simulator is unregistered.
	 */
	public Device registerDevice(Dictionary<String,Object> deviceProps,
			Dictionary<String,Object>[] functionProps);

	/**
	 * Forces the function with the given class name to publish an event.
	 * 
	 * @param functionClassName The function class name.
	 * @param propertyName The function property name.
	 * 
	 * @throws IllegalArgumentException If the property is not supported by the
	 *         function or the function class name points to a missing function.
	 */
	public void publishEvent(String functionClassName, String propertyName);
}
