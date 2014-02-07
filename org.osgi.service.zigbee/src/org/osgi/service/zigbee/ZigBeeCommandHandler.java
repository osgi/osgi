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
 * ZigBeeCommandHandler manages response of a command request to the Base Driver
 * 
 * @version 1.0
 */

public interface ZigBeeCommandHandler {

	/**
	 * Notifies the result (success or failure) of the call.
	 * 
	 * The base driver will release the handler object when he receives a null
	 * frame in a notifyResponse call or thanks to the an implementation
	 * specific timeout.
	 * 
	 * @param frame the ZCLFrame
	 */
	void notifyResponse(ZCLFrame frame);

}
