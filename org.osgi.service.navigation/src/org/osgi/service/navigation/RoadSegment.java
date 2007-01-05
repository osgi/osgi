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

import org.osgi.util.measurement.Measurement;

public interface RoadSegment {
	/**
	 * Indicates that the road segment is part of a Motorway.
	 */
	public int	FORMOFWAY_MOTORWAY					= 0;

	/**
	 * Indicates that the road segment is part of a Multiple Carriageway which
	 * is not a motorway.
	 */
	public int	FORMOFWAY_MULTIPLE_CARRIAGEWAY		= 1;

	/**
	 * Indicates that the road segment is part of a Single Carriageway.
	 */
	public int	FORMOFWAY_SINGLE_CARRIAGEWAY		= 2;

	/**
	 * Indicates that the road segment is part of a Roundabout.
	 */
	public int	FORMOFWAY_ROUNDABOUT				= 3;

	/**
	 * Indicates that the road segment is part of a Traffic Square.
	 */
	public int	FORMOFWAY_SQUARE					= 4;

	/**
	 * Indicates that the road segment is part of an Enclosed Traffic Area.
	 */
	public int	FORMOFWAY_ENCLOSED_TRAFFIC_AREA		= 5;

	/**
	 * Indicates that the road segment is part of a Slip Road.
	 */
	public int	FORMOFWAY_SLIP_ROAD					= 6;

	/**
	 * Indicates that the road segment is part of a Service Road.
	 */
	public int	FORMOFWAY_SERVICE_ROAD				= 7;

	/**
	 * Indicates that the road segment is an entrance to or an exit of Car Park.
	 */
	public int	FORMOFWAY_ENTRANCE_EXIT_CAR_PARK	= 8;

	/**
	 * Indicates that the road segment is an entrance to or an exit to Service.
	 */
	public int	FORMOFWAY_ENTRANCE_EXIT_SERVICE		= 9;

	/**
	 * Indicates that the road segment is part of a Pedestrian Zone.
	 */
	public int	FORMOFWAY_PEDESTRIAN_ZONE			= 10;

	/**
	 * Indicates that the road segment is a main road.
	 */
	public int	ROADCLASS_MAIN_ROAD					= 1;

	/**
	 * Indicates that the road segment is a first class road.
	 */
	public int	ROADCLASS_FIRST_CLASS				= 2;

	/**
	 * Indicates that the road segment is a second class road.
	 */
	public int	ROADCLASS_SECOND_CLASS				= 3;

	/**
	 * Indicates that the road segment is a third class road.
	 */
	public int	ROADCLASS_THIRD_CLASS				= 4;

	/**
	 * Indicates that the road segment is a fourth class road.
	 */
	public int	ROADCLASS_FOURTH_CLASS				= 5;

	/**
	 * Indicates that the road segment is a fitfh class road.
	 */
	public int	ROADCLASS_FIFTH_CLASS				= 6;

	/**
	 * Indicates that the road segment is a sixth class road.
	 */
	public int	ROADCLASS_SIXTH_CLASS				= 7;

	/**
	 * Indicates that the road segment is a seventh class road.
	 */
	public int	ROADCLASS_SEVENTH_CLASS				= 8;

	/**
	 * Indicates that the road segment is a eight class road.
	 */
	public int	ROADCLASS_EIGHT_CLASS				= 9;

	/**
	 * Indicates that the road segment is a ninth class road.
	 */
	public int	ROADCLASS_NINTH_CLASS				= 10;

	/**
	 * Returns the form of way of the route element. The list of possible values
	 * are defined in FormOfWay interface.
	 * 
	 * @return org.osgi.service.navigation.FormOfWay
	 */
	public int getFormOfWay();

	/**
	 * Returns the road class of the route element. The list of possible values
	 * are defined in RoadClass interface.
	 * 
	 * @return org.osgi.service.navigation.RoadClass
	 */
	public int getRoadClass();

	 // ### dont we need a length for the segment?
	
	/**
	 * Returns the heading of the route element expressed in radian.
	 * 
	 * @return Measurement
	 */
	public Measurement getHeading();

	
	Location getLocation();

	Map getAttributes();
}
