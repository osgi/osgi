/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2006). All Rights Reserved.
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

package org.osgi.service.navigation;

/**
 * A maneuver is the action that the driver has to perform at a junction.
 * Several advices can be rendered at a maneuver.
 */
public interface Maneuver extends RouteSegment {
	/**
	 * Indicates that there is no junction at the maneuver.
	 */
	public static final int	JUNCTION_NONE					= 0;

	/**
	 * Indicates that there is a square at the maneuver.
	 */
	public static final int	JUNCTION_SQUARE					= 1;

	/**
	 * Indicates that there is a enclosed traffic area at the maneuver. An
	 * enclosed traffic area is a confined area within which unstructured
	 * traffic movements are allowed.
	 */
	public static final int	JUNCTION_ENCLOSED_TRAFFIC_AREA	= 2;

	/**
	 * Indicates that there is normal intersection at the maneuver.
	 */
	public static final int	JUNCTION_INTERSECTION			= 3;

	/**
	 * Indicates that there is a roundabout at the maneuver.
	 */
	public static final int	JUNCTION_ROUNDABOUT				= 4;

	/**
	 * Indicates that the driver will access a motorway at the next maneuver.
	 */
	public static final int	JUNCTION_MOTORWAY_ENTRY			= 5;

	/**
	 * Indicates that the driver will xit from a motorway at the next maneuver.
	 */
	public static final int	JUNCTION_MOTORWAY_EXIT			= 6;

	/**
	 * Indicates that the driver will access a main road at the next maneuver.
	 */
	public static final int	JUNCTION_MAINROAD_ENTRY			= 7;

	/**
	 * Indicates that the driver will exit from a main road at the next
	 * maneuver.
	 */
	public static final int	JUNCTION_MAINROAD_EXIT			= 8;

	/**
	 * Indicates that the driver will access a restricted area at the next
	 * maneuver.
	 */
	public static final int	JUNCTION_RESTRICTED_AREA_ENTRY	= 9;

	/**
	 * Indicates that the driver will exit a restricted area at the next
	 * maneuver.
	 */
	public static final int	JUNCTION_RESTRICTED_AREA_EXIT	= 10;

	/**
	 * Indicates that the driver will access a facility at the next maneuver.
	 */
	public static final int	JUNCTION_FACILITY_ENTRY			= 11;

	/**
	 * Indicates that the driver will exit from a facility at the next maneuver.
	 */
	public static final int	JUNCTION_FACILITY_EXIT			= 12;

	/**
	 * Returns the junction type where the maneuver will be performed. The value
	 * must be one of the value defined in JunctionType interface.
	 * 
	 * @return int One of the values contained in the JunctionType interface
	 */
	public int getJunctionType();

	/**
	 * Returns the angle of the turn which is the difference between the angle
	 * of the from and to segments.
	 * 
	 * ### Should this not be in the junction segment?
	 * @return int The angle made by the from and to segments
	 */
	public int getTurnAngle();

	/**
	 * Returns the action performed at the maneuver. The value must be one of
	 * the value defined by ActionType interface.
	 * 
	 * @return int One of the values defined in ActionType interface
	 */
	public int getActionType();

	/**
	 * Returns the road sign information towards the itinerary. It is usuallly a
	 * city / municipality names.
	 * 
	 * @return String The road sign information
	 */
	public String getRoadSign();

	/**
	 * Returns the number of the segment by which the planned route is leaving
	 * the junction. The numbering starts at the from-segment (number 0). For
	 * right-turning possibilities, numbering proceeds in counter-clockwise
	 * order. For left-turning possibilities, numbering proceeds in clockwise
	 * order.
	 * 
	 * @return int The number of exists at the next roundabout
	 */
	public int getNumberOfExit();

	/**
	 * In case of turn action at a maneuver, this flag indicates if there
	 * several possibility (roads) to turn. This information is important to
	 * render advice like "At the next junction take the xxth on the
	 * left/right".
	 * 
	 * @return boolean True if there is a multiple turn at the next junction
	 */
	public boolean isMultipleTurn();

	/**
	 * Get the first Junction Segment for this maneuver, this is the
	 * from segment. From this segment, you can find the next segments.
	 */
	public JunctionSegment getJunctionSegment();

}
