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

package org.osgi.service.navigation.orig;

public interface NavigationService 
{
   
   /**
    * Returns a Location object relative to the Address given as parameter.
    * 
    * @param address The textual address where the user wants to go.
    * @return The complete Location information if the address has been resolved
    */
   public Location locate(Address address);
   
   /**
    * Returns a Location object relative to the coordinate given as parameter.
    * 
    * @param coordinate The WGS84 coordinate where the user wants to go.
    * @return The nearset Location of the given coordinate.
    */
   public Location locate(Coordinate coordinate);
   
   /**
    * Returns a list of POI (Point Of Interest) in a certain zone.
    * The user can filter the number of POIs by using their categories.
    * 
    * @param local The locale used to retreive POI information
    * @param zone The zone of the search.
    * @param filter The filter to apply to the search.
    * @return List of POIs found.
    */
   public PointOfInterest[] getPOIs(String local, Zone zone, String filter);
   
   /**
    * Computes a route between two Route boundaries (from and to) with the
    * given route plan.
    * 
    * @param from The source. 
    * @param to The destination.
    * @param plan The route plan that contains criteria for route computation.
    * @return The planned route
    */
   public Route compute(RouteBoundary from, RouteBoundary to, RoutePlan plan);
}
