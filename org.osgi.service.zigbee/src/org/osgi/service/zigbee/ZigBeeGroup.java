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

import org.osgi.framework.Constants;

/**
 * This interface represents a ZigBee Group
 * 
 * @version 1.0
 */
public interface ZigBeeGroup {

	/**
	 * Key of the {@link String} containing the Group Address of the device <br>
	 * It is <b>mandatory</b> property for this service
	 */
	public static final String	ID	= "zigbee.group.id";

	/**
	 * @return An array of servers(inputs) clusters, returns an empty array if
	 *         does not provides any servers clusters.
	 */
	public ZigBeeCluster[] getServerClusters();

	/**
	 * @param serverClusterId The server(input) cluster identifier
	 * @return the server(input) cluster identified by id
	 */
	public ZigBeeCluster getServerCluster(int serverClusterId);

	/**
	 * @return The 16bit group address.
	 */
	public short getGroupAddress();

	/**
	 * This method is used for adding an Endpoint to group, it may be invoked on
	 * exported endpoint or even on import endpoint. In the former case, the
	 * ZigBee Base Driver should rely on the <i>APSME-ADD-GROUP</i> API defined
	 * by the ZigBee Specification, in the former case it will use the proper
	 * commands of the <i>Groups</i> cluster of the ZigBee Specification Library
	 * 
	 * @param pid {@link String} representing the PID (see
	 *        {@link Constants#SERVICE_PID} ) of the {@link ZigBeeEndpoint} that
	 *        we want add to this Group
	 * @param handler the {@link ZigBeeCommandHandler} that will notified of the
	 *        result of "joining"
	 * @throws ZigBeeException
	 */
	public void joinGroup(String pid, ZigBeeCommandHandler handler) throws ZigBeeException;

	/**
	 * This method is used for adding an Endpoint to group, it may be invoked on
	 * exported endpoint or even on import endpoint. In the former case, the
	 * ZigBee Base Driver should rely on the <i>APSME-REMOVE-GROUP </i> API
	 * defined by the ZigBee Specification, in the former case it will use the
	 * proper commands of the <i>Groups</i> cluster of the ZigBee Specification
	 * Library
	 * 
	 * @param pid {@link String} representing the PID (see
	 *        {@link Constants#SERVICE_PID} ) of the {@link ZigBeeEndpoint} that
	 *        we want leave to this Group
	 * @param handler the {@link ZigBeeCommandHandler} that will notified of the
	 *        result of "leaving"
	 * @throws ZigBeeException
	 */
	public void leaveGroup(String pid, ZigBeeCommandHandler handler) throws ZigBeeException;

}
