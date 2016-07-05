/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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
 * Manage response of a command request to the Base Driver
 * 
 * @author $Id$
 */
public interface ZCLCommandHandler {

	/**
	 * Notifies the result (success or failure) of the call. The entity calling
	 * notifyresponse() (i.e., the base driver in the import situation) must not
	 * parse the ZCL frame payload. Thus, error codes that are conveyed in the
	 * ZCLFrame payload must not be turned into exceptions.
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
	 * @param e the exception if any, see {@link ZCLException} constants mapping
	 *        the names described in "Table 2.17 Enumerated Status Values Used
	 *        in the ZCL" of the ZCL specification.
	 */
	void notifyResponse(ZCLFrame frame, Exception e);

}
