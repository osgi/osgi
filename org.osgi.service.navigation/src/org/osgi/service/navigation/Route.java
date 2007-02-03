/*
 * $Header$
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

package org.osgi.service.navigation;

/**
 * Interface to the route definition. The route has several states:
 */
public interface Route  {
	/**
	 * Indicates that the shortest way must be found.
	 */
	public static final int	SHORTEST		= 0;

	/**
	 * Indicates that the fastest way must be found.
	 */
	public static final int	FASTEST			= 1;

	/**
	 * FeaturePreference object to set AVOID_HIGHWAY preference.
	 */
	public static final int	AVOID_HIGHWAY	= 2;

	/**
	 * FeaturePreference object to set AVOID_TOLLWAY preference.
	 */
	public static final int	AVOID_TOLLWAY	= 4;

	/**
	 * FeaturePreference object to set AVOID_FERRY preference.
	 */
	public static final int	AVOID_FERRY		= 8;

	
	/**
	 * Calculate alternatives
	 */
	public static final int CALCULATE_ALTERNATIVES = 10;
	
	
	
	

	/**
	 * ### Can you do this after planning?
	 * ### Is there a circumference around the location to avoid?
	 * ### Can this fail, if so, when is the exception thrown?
	 * 
	 * @param location
	 */
	void addAvoid(Location location);
	void removeAvoid(Location location);

	/**
	 * ### Does this imply there is a segment with the given location? Or is
	 *     this the broad via "Lyon". If so, should this not have a Zone as parm?
	 * ### If this location is unreachable, when will an exception be thrown?
	 * 
	 * @param location
	 */
	void addVia(Location location);
	void removeVia(Location location);
	
	Location[] getAvoids();
	Location[] getVias();
		
	void setStrategy(int strategy);
	
	Location getDestination();
	Location getOrigin();
	
	//### Do we need an error state if the planning failed? or do
	// we throw a planning exception.
	// Exception getErrorState();
	
	// ### do we need a getRouteSegment(Location) method so that
	// we can easily find a segment on the route that we need to
	// do something special with?
	
	
	Path[] getPaths();
	Path getPreferredPath();
	void setPreferredPath(Path path);
}
