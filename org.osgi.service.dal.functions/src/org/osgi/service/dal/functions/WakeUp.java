/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

import java.math.BigDecimal;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;

/**
 * {@code WakeUp} function provides device awake monitoring and management. It's
 * especially applicable to battery-operated devices. Such device can notify the
 * system that it's awake and can receive commands with an event to property
 * {@link #PROPERTY_AWAKE}.
 * <p>
 * The device can periodically wake up for commands. The interval can be managed
 * with {@link #PROPERTY_WAKE_UP_INTERVAL} property.
 * <p>
 * The application can minimize the power consumption with {@link #sleep()}
 * operation. As a result of the call, the device will sleep and will not
 * receive commands to the next awake.
 * 
 * @see LevelData
 * @see BooleanData
 */
public interface WakeUp extends Function {

	/**
	 * Specifies the awake property name. The property access type can be
	 * {@link PropertyMetadata#ACCESS_EVENTABLE}. If the device is awake, it
	 * will trigger a property event.
	 * <p>
	 * The property value type is {@code BooleanData}. The boolean data is
	 * always {@code true}. It marks that the device is awake.
	 */
	public static final String	PROPERTY_AWAKE				= "awake";

	/**
	 * Specifies the wake up interval. The device can periodically wake up and
	 * receive commands. That interval is managed by this property. The current
	 * property value is available with {@link #getWakeUpInterval()} and can be
	 * modified with {@link #setWakeUpInterval(BigDecimal)} and
	 * {@link #setWakeUpInterval(BigDecimal, String)}.
	 */
	public static final String	PROPERTY_WAKE_UP_INTERVAL	= "wakeUpInterval";

	/**
	 * Specifies the sleep operation name. The operation can be executed with
	 * {@link #sleep()} method.
	 */
	public static final String	OPERATION_SLEEP				= "sleep";

	/**
	 * Returns the current wake up interval. It's a getter method for
	 * {@link #PROPERTY_WAKE_UP_INTERVAL} property. The device can periodically
	 * wake up and receive command based on this interval.
	 * <p>
	 * The interval can be measured in different units like hours, minutes,
	 * seconds etc. The unit is specified in {@code LevelData} instance.
	 * 
	 * @return The current wake up interval.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * 
	 * @see LevelData
	 */
	public LevelData getWakeUpInterval() throws DeviceException;

	/**
	 * Sets wake up interval according to the default unit. It's a setter method
	 * for {@link #PROPERTY_WAKE_UP_INTERVAL} property. The device can
	 * periodically wake up and receive command based on this interval.
	 * 
	 * @param interval The new wake up interval.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * @throws IllegalArgumentException If there is an invalid argument.
	 */
	public void setWakeUpInterval(BigDecimal interval) throws DeviceException;

	/**
	 * Sets wake up interval according to the specified unit. It's a setter
	 * method for {@link #PROPERTY_WAKE_UP_INTERVAL} property. The device can
	 * periodically wake up and receive command based on this interval.
	 * 
	 * @param interval The new wake up interval.
	 * @param unit The interval unit.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 * @throws IllegalArgumentException If there is an invalid argument.
	 */
	public void setWakeUpInterval(BigDecimal interval, String unit) throws DeviceException;

	/**
	 * The device is forced to sleep to minimize the power consumption.
	 * 
	 * @throws UnsupportedOperationException If the operation is not supported.
	 * @throws IllegalStateException If this function service object has already
	 *         been unregistered.
	 * @throws DeviceException If an operation error is available.
	 */
	public void sleep() throws DeviceException;
}
