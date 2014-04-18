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
 * This interface represents an entry of the RoutingTableList (see Table 2.128
 * RoutingTableList Record Format in ZIGBEE SPECIFICATION:
 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf)
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */
public interface Route {

	/**
	 * Constant value representing an active route
	 */
	public static final int	ACTIVE				= 0x00;

	/**
	 * Constant value representing a route that is under discovery
	 */
	public static final int	DISCOVERY_UNDERWAY	= 0x01;

	/**
	 * Constant value representing a failed route discovery
	 */
	public static final int	DISCOVERY_FAILED	= 0x02;

	/**
	 * Constant value representing an inactive route
	 */
	public static final int	INACTIVE			= 0x03;

	/**
	 * Constant value representing a route which is under validation
	 */
	public static final int	VALIDATION_UNDERWAY	= 0x04;

	/**
	 * @return the Service.PID of the {@link ZigBeeNode} as destination of this
	 *         route entry
	 */
	public String getDestination();

	/**
	 * @return the Service.PID of the {@link ZigBeeNode} to send the data for
	 *         reaching the destination
	 */
	public String getNextHop();

	/**
	 * @return the status of the RoutingLink as defined by ZigBee Specification:
	 *         ACTIVE, DISCOVERY_UNDERWAY, DISCOVERY FAILED, INACTIVE,
	 *         VALIDATION_UNDERDAY
	 */
	public int getStatus();

}
