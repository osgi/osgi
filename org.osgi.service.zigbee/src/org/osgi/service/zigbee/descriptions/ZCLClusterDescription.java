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

package org.osgi.service.zigbee.descriptions;

/**
 * This interface represents a ZCL Cluster description.
 * 
 * @author $Id$
 */
public interface ZCLClusterDescription {
	/**
	 * @return the cluster identifier.
	 */
	public int getId();

	/**
	 * Returns an array of the generated command descriptions.
	 * 
	 * @return an array of the generated command descriptions.
	 */
	public ZCLCommandDescription[] getGeneratedCommandDescriptions();

	/**
	 * Returns an array of the received command description.
	 * 
	 * @return an array of the received command description.
	 */
	public ZCLCommandDescription[] getReceivedCommandDescriptions();

	/**
	 * Returns an array of the attribute descriptions.
	 * 
	 * @return an array of the attribute descriptions.
	 */
	public ZCLAttributeDescription[] getAttributeDescriptions();

	/**
	 * Returns an array of the command descriptions.
	 * 
	 * @return an array of the command descriptions.
	 */
	public ZCLGlobalClusterDescription getGlobalClusterDescription();
}
