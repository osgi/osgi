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

package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a simple descriptor as described in the ZigBee
 * Specification The Simple Descriptor contains information specific to each
 * endpoint present in the node.
 * 
 * @version 1.0
 */

public interface ZigBeeSimpleDescriptor {
	/**
	 * @return the application profile id.
	 */
	public int getApplicationProfileId();

	/**
	 * @return device id as defined per profile.
	 */
	public int getApplicationDeviceId();

	/**
	 * @return the endpoint for which this descriptor is defined.
	 */
	public short getEndpoint();

	/**
	 * @return the version of the application.
	 */
	public short getApplicationDeviceVersion();

	/**
	 * @return the count of input clusters.
	 */
	public short getApplicationInputClusterCount();

	/**
	 * @return the count of output clusters.
	 */
	public short getApplicationOutputClusterCount();

	/**
	 * @return list of input cluster identifiers.
	 */
	public int[] getInputClusterList();

	/**
	 * @return list of output cluster identifiers.
	 */
	public int[] getOutputClusterList();
}
