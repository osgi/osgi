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

package org.osgi.service.zigbee;

/**
 * This interface represents a listener to events from ZigBee Device nodes
 * 
 * @version 1.0
 */
public interface ZigBeeEventListener {

	/**
	 * key for a service property having a value that is an object of type
	 * org.osgi.framework.Filter and that is used to limit received events.
	 */
	public static final String	ZIGBEE_FILTER			= "zigbee.filter";

	/**
	 * Key of {@link String} containing the listener targeted network PAN ID
	 */
	public static final String	PAN_ID_TARGET			= "zigbee.listener.target.pan.id";

	/**
	 * Key of {@link String} containing the listener targeted network extended
	 * PAN ID
	 */
	public static final String	EXTENDED_PAN_ID_TARGET	= "zigbee.listener.target.extended.pan.id";

	/**
	 * Callback method that is invoked for received events. This method must be
	 * called asynchronously.
	 * 
	 * @param event a set of events
	 */
	public void notifyEvent(ZigBeeEvent event);
}
