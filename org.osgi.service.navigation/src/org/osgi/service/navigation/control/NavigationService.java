/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2006, 2007). All Rights Reserved.
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

package org.osgi.service.navigation.control;

import org.osgi.service.navigation.*;

public interface NavigationService  {
	/**
	 * Given an address specification, returns one or more matching Locations
	 */ 
	Location[] getLocation(Address address );
	/**
	 * returns the Location for a given Coordinate
	 *
	 * Note: a coordinate does not always specify a location uniquely, 
     * e.g. coordinate of the middle of a crossing could lead to 4 corners matching.
     * The implementer of this service will decide then what the best match is
     */
	Location[] getLocation(Coordinate coordinate);
	
	/**
	 * Based on a opolygin will return a Location 
	 * @param area defined by a Polygon
	 * @return a Location
	 */
	GeometricAreaLocation getLocation(PolygonArea area);
	
	/**
	 * create a RoutePlan based on the from Location and the given destination
	 * 
	 * @param from start of the route. Typically the CurrentLocation .
	 * @param to destination
	 * @return Routeplan
	 *
	 * TODO I think we need a CurrentLocation interface? This ocation would behave different: it will constantly updated
	 * so if it will be given as start it will still be the actual current location when startGuidance in invoked. 
	 */ 
	RoutePlan createRoutePlan(Location from, Location to);
	
	/**
	* get the current Location of the vehicle
	* TODO should return CurrentLocation. 
	*/
	Route createRoute(Location from, Location to);
	Location getCurrentLocation();
	
	/**
	* select routeplan as the the route to follow. This method will start guiding the driver to the 
	* routeplan's destination.
	*/
	void startGuidance(RoutePlan routePlan );
	/** 
	 * stops guidance 
	 */
	void stopGuidance();
	
	/**
	 * returns te currently active route
	 * @return Route part of the routePlan currently active
	 */
	Route getCurrentRoute();
	Path getCurrentPath();
}
 
