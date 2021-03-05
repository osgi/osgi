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
 * Serial events are sent using the white board model, in which a bundle
 * interested in receiving the Serial events registers an object implementing
 * the SerialEventListener interface. A COM port name can be set to limit the
 * events for which a bundle is notified.
 * 
 * @ThreadSafe
 * @author $Id$
 */
public interface SerialEventListener {

	/**
	 * Key for a service property that is used to limit received events.
	 */
	String	SERIAL_COMPORT	= SerialDevice.SERIAL_COMPORT;

	/**
	 * Callback method that is invoked for received an event.
	 * 
	 * @param event The SerialEvent object.
	 */
	void notifyEvent(SerialEvent event);
}
