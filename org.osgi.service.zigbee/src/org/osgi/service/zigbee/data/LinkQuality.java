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

package org.osgi.service.zigbee.data;

import org.osgi.service.zigbee.ZigBeeNode;

/**
 * This interface represents an entry of the RoutingTableList (see Table 2.126
 * NeighborTableList Record Format in ZIGBEE SPECIFICATION:
 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf)
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public interface LinkQuality {

	/**
	 * @return the Service.PID refering to the {@link ZigBeeNode} representing
	 *         neighbour
	 */
	public String getNeighbour();

	/**
	 * See the LQI field of the (NeighborTableList Record Format).
	 * 
	 * @return the Link Quality Indicator estimated by {@link ZigBeeNode}
	 *         returning this for communicating with {@link ZigBeeNode}
	 *         identified by the {@link #getNeighbour()}
	 */
	public int getLQI();

	/**
	 * See the Depth field of the (NeighborTableList Record Format).
	 * 
	 * @return the tree-depth of device
	 */
	public int getDepth();

	/**
	 * See the Relationship field of the (NeighborTableList Record Format).
	 * 
	 * @return the relationship between {@link ZigBeeNode} returning this and
	 *         the {@link ZigBeeNode} identified by the {@link #getNeighbour()}
	 */
	public int getRelationship();

}
