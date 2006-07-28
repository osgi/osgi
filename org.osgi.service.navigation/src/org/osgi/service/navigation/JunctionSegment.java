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
 * A JunctionSegment is a part of the JunctionGeometry.
 */
public interface JunctionSegment {

	/**
	 * Returns the angle of this segment with the North in degrees.
	 * 
	 * @return Measurement
	 */
	 double getAngle();

	/**
	 * Returns the coordinate where this segment is attached to the maneuver.
	 * 
	 * @return org.osgi.nursery.util.geo.Coordinate
	 */
	 Coordinate getCoordinate();

	/**
	 * Returns true if this JunctionSegment object is part of the inner segments
	 * otherwise false.
	 * 
	 * @return boolean
	 */
	 boolean isInnerSegment();

	/**
	 * Returns true if this JunctionSegment object is in the itinerary otherwise
	 * false.
	 * 
	 * @return boolean
	 */
	 boolean isOnRoute();

	JunctionSegment[] getJunctionSegments();
}
