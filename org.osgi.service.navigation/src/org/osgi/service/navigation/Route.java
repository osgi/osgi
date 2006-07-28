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
 * Interface to the route definition. The route has several states:
 */
public interface Route extends Location {
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

	RouteSegment next();
	RouteSegment [] getSegments(int index, String filter);
	RouteSegment getSegment(int n);
	
	void avoid(Location location);
	Location[] getAvoids();
	Location[] getVias();
	
	double getRemainingDistance();
	double getRemainingTravelTime();
	
	void plan();
	
	void setStrategy(int strategy);
	void via(Location location);
	
	Location getDestination();
	
}
