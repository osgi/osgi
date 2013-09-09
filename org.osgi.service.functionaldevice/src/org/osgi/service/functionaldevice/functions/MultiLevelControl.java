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
 * <code>MultiLevelControl</code> Device Function provides multi-level control
 * support. The function level is accessible with {@link #getLevel()} getter and
 * {@link #setLevel(MultiLevelData)} setter.
 * 
 * @see MultiLevelData
 */
public interface MultiLevelControl extends DeviceFunction {

	/**
	 * Specifies the level property name. The property can be read with
	 * {@link #getLevel()} getter and can be set with
	 * {@link #setLevel(MultiLevelData)} setter.
	 */
	public static final String PROPERTY_LEVEL = "level";

	/**
	 * Returns <code>MultiLevelControl</code> level. It's a getter method for
	 * {@link #PROPERTY_LEVEL} property.
	 * 
	 * @return <code>MultiLevelControl</code> level.
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
	public MultiLevelData getLevel() throws UnsupportedOperationException,
			IllegalStateException, DeviceException;

	/**
	 * Sets <code>MultiLevelControl</code> level. It's a setter method for
	 * {@link #PROPERTY_LEVEL} property.
	 * 
	 * @param level
	 *            The new <code>MultiLevelControl</code> level.
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
	public void setLevel(MultiLevelData level)
			throws UnsupportedOperationException, IllegalStateException,
			DeviceException;

}
