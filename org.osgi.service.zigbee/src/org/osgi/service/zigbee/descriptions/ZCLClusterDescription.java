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

package org.osgi.service.zigbee.descriptions;

/**
 * This interface represents a ZCL Cluster description
 * 
 * @author $Id$
 */
public interface ZCLClusterDescription {
	/**
	 * @return the cluster identifier
	 */
	public int getId();

	/**
	 * @return an array of cluster's generated command description
	 */
	public ZCLCommandDescription[] getGeneratedCommandDescriptions();

	/**
	 * @return an array of cluster's received command description
	 */
	public ZCLCommandDescription[] getReceivedCommandDescriptions();

	/**
	 * @return an array of cluster's Attributes description
	 */
	public ZCLAttributeDescription[] getAttributeDescriptions();

	/**
	 * @return an array of cluster's Commands description
	 */
	public ZCLGlobalClusterDescription getGlobalClusterDescription();
}
