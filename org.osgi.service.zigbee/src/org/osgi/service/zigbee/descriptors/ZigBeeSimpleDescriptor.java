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

package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a simple descriptor as described in the ZigBee
 * Specification.
 * <p>
 * The Simple Descriptor contains information specific to each endpoint present
 * in the node.
 * 
 * @author $Id$
 */
public interface ZigBeeSimpleDescriptor {

	/**
	 * Returns the application profile id.
	 * 
	 * @return the application profile id.
	 */
	public int getApplicationProfileId();

	/**
	 * Returns the application device id as defined per profile.
	 * 
	 * @return the application device id as defined per profile.
	 */
	public int getApplicationDeviceId();

	/**
	 * Returns the endpoint for which this descriptor is defined.
	 * 
	 * @return the endpoint for which this descriptor is defined.
	 */
	public short getEndpoint();

	/**
	 * Returns the version of the endpoint application.
	 * 
	 * @return the version of the endpoint application.
	 */
	public byte getApplicationDeviceVersion();

	/**
	 * Returns an array of input (server) cluster identifiers.
	 * 
	 * @return an array of input (server) cluster identifiers, returns an empty
	 *         array if does not provides any input (server) clusters.
	 */
	public int[] getInputClusters();

	/**
	 * Returns an array of output (client) cluster identifiers.
	 * 
	 * @return an array of output (client) cluster identifiers, returns an empty
	 *         array if does not provides any output (client) clusters.
	 */
	public int[] getOutputClusters();

	/**
	 * Checks if this endpoint implements the given cluster id as an input
	 * cluster.
	 * 
	 * @param clusterId the cluster identifier.
	 * @return true if and only if this endpoint implements the given cluster id
	 *         as an input cluster.
	 */
	public boolean providesInputCluster(int clusterId);

	/**
	 * Checks if this endpoint implements the given cluster id as an output
	 * cluster.
	 * 
	 * @param clusterId the cluster identifier.
	 * @return true if and only if this endpoint implements the given cluster id
	 *         as an output cluster.
	 */
	public boolean providesOutputCluster(int clusterId);

}
