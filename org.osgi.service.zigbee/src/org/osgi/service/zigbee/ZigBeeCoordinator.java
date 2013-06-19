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

import org.osgi.service.zigbee.handler.ZigBeeHandler;

/**
 * This interface represents the ZigBee network coordinator
 * 
 * @version 1.0
 */
public interface ZigBeeCoordinator {
	/**
	 * Set the network channel 802.15.4 and ZigBee break the 2.4Ghz band into 16
	 * channels, numbered from 11 to 26.
	 * 
	 * @param handler The handler that manages the command response.
	 * @param channel The network channel. Sets to 0, the channel is chose by
	 *        the chip itself.
	 */
	public void setChannel(ZigBeeHandler handler, byte channel) throws ZigBeeException;

	/**
	 * @return The current Network key
	 */
	public String getNetworkKey() throws ZigBeeException;
}
