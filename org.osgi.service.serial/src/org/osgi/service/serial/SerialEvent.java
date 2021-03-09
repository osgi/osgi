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

package org.osgi.service.serial;

/**
 * A serial device event. SerialEvent objects are delivered to
 * SerialEventListeners when an event occurs.
 * 
 * <p>
 * A type of code is used to identify the event. Additional event types may be
 * defined in the future.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public interface SerialEvent {

	/**
	 * Event type indicating data available.
	 */
	int	DATA_AVAILABLE	= 1;

	/**
	 * Returns the type of this event.
	 * 
	 * @return The type of this event.
	 */
	int getType();

	/**
	 * Returns the port name of this event.
	 * 
	 * <p>
	 * This value must be equal to the value of
	 * {@link SerialDevice#SERIAL_COMPORT} service property of the
	 * {@link SerialDevice}.
	 * 
	 * @return The port name of this event.
	 */
	String getComPort();
}
