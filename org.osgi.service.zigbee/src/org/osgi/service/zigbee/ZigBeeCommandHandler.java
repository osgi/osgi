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
	 * The ZigBee Base Driver will release the handler object when he receives a
	 * null frame in a notifyResponse call or thanks to the an implementation
	 * specific timeout.
	 * 
	 * The ZigBee Base Driver MUST discard the Default Response if the caller
	 * set the DisableDefaultReponse flag and the status of DefaultResponse
	 * command is SUCCESS.
	 * 
	 * Multiple response management: Several responses MAY be sent to an
	 * endpoint. A handler could be called several times on a command handler.
	 * 
	 * @param frame the ZCLFrame
	 */
	void notifyResponse(ZCLFrame frame);

}
