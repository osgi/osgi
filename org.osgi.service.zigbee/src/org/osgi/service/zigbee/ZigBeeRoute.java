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
 * This interface represents an entry of the RoutingTableList (see Table 2.128
 * RoutingTableList Record Format in ZIGBEE SPECIFICATION:
 * 1_053474r17ZB_TSC-ZigBee-Specification.pdf).
 * 
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeeRoute {

	/**
	 * Constant value representing an active route.
	 */
	public static final int	ACTIVE				= 0xf0;

	/**
	 * Constant value representing a route that is under discovery.
	 */
	public static final int	DISCOVERY_UNDERWAY	= 0xf1;

	/**
	 * Constant value representing a failed route discovery.
	 */
	public static final int	DISCOVERY_FAILED	= 0xf2;

	/**
	 * Constant value representing an inactive route.
	 */
	public static final int	INACTIVE			= 0xf3;

	/**
	 * Constant value representing a route which is under validation.
	 */
	public static final int	VALIDATION_UNDERWAY	= 0xf4;

	/**
	 * Returns the service PID of the {@link ZigBeeNode} as destination of this
	 * route entry.
	 * 
	 * @return the service PID of the {@link ZigBeeNode} as destination of this
	 *         route entry.
	 */
	public String getDestination();

	/**
	 * Returns the service PID of the {@link ZigBeeNode} to send the data for
	 * reaching the destination.
	 * 
	 * @return the service PID of the {@link ZigBeeNode} to send the data for
	 *         reaching the destination.
	 */
	public String getNextHop();

	/**
	 * Returns the status of this route.
	 * 
	 * @return the status of this route (or routing link) as defined by ZigBee
	 *         Specification: ACTIVE, DISCOVERY_UNDERWAY, DISCOVERY FAILED,
	 *         INACTIVE, VALIDATION_UNDERDAY.
	 */
	public int getStatus();

}
