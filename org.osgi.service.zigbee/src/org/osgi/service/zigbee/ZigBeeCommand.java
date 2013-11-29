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
 * This interface represents a ZigBee Command
 * 
 * @version 1.0
 */
public interface ZigBeeCommand {

	/**
	 * @return The command identifier
	 */
	public int getId();

	/**
	 * Invokes the action. The handler will provide the invocation response in
	 * an asynchronously way.
	 * 
	 * The source endpoint is not specified in this method call. To send the
	 * appropriate message on the network, the base driver must generate a
	 * source endpoint. The latter must not correspond to any exported endpoint.
	 * 
	 * @param bytes An array of bytes containing a command frame sequence.
	 * @param handler The handler that manages the command response.
	 * @throws ZigBeeException
	 */
	public void invoke(byte[] bytes, ZigBeeCommandHandler handler) throws ZigBeeException;

	/**
	 * This method is to be used by applications when the targeted device has to
	 * distinguish between source endpoints of the message. For instance, alarms
	 * cluster (see 3.11 Alarms Cluster in [ZCL]) generated events are
	 * differently interpreted if they come from the oven or from the intrusion
	 * alert system.
	 * 
	 * @param bytes An array of bytes containing a command frame sequence.
	 * @param handler The handler that manages the command response.
	 * @param exportedServicePID : the source endpoint of the command request.
	 *        In targeted situations, the source endpoint is the valid service
	 *        PID of an exported endpoint.
	 * @throws ZigBeeException
	 */
	public void invoke(byte[] bytes, ZigBeeCommandHandler handler, String exportedServicePID) throws ZigBeeException;

}
