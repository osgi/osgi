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
 * Callback for managing response to {@link ZDPFrame} sent by
 * {@link ZigBeeNode#invoke(int, int, ZDPFrame, ZDPHandler)}
 * 
 * @author $Id$
 */
public interface ZDPHandler {

	/**
	 * Notifies the result (success or failure) of the call. This method is
	 * invoked by the entity that registered the {@link ZigBeeNode}, and it is
	 * expected that only the ZigBee Base Driver register it. <br>
	 * 
	 * The {@link ZDPHandler} MUST be invoked with {@code  null} value for the
	 * Exception parameter in case of success.<br>
	 * On the contrary, the {@link ZDPFrame} MUST be contain the message
	 * received from the {@link ZigBeeNode} even in case of failure so that the
	 * implementor can analyze the content of the message to better understand
	 * the failure.
	 * 
	 * @param clusterId the clusterId of the response
	 * @param frame the {@link ZDPFrame} containing the response, in case of
	 *        failure the value MAY be {@code  null}, if it is not the
	 *        {@link ZDPFrame}
	 * @param e is any exception related to ZigBee communication failure, in
	 *        case of success the value is {@code  null}
	 */
	void zdoResponse(int clusterId, ZDPFrame frame, Exception e);

}
