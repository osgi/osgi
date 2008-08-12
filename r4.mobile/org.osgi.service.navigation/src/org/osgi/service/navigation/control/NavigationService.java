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

package org.osgi.service.navigation.control;

import org.osgi.service.navigation.*;
import org.osgi.util.position.Position;

public interface NavigationService  {
   
   /**
    * Returns a Location object relative to the Address given as parameter.
    * ### I think this needs to become iterative
    * @param address The textual address where the user wants to go.
    * @return The complete Location information if the address has been resolved
    */
   public AddressLocation[] locate(Address address);
   
   /**
    * Returns a Location object relative to the coordinate given as 
    * parameter. This is necessary because a
    * 
    * @param coordinate The WGS84 coordinate where the user wants to go.
    * @return The nearset Location of the given coordinate.
    */
   public Location locate(Coordinates where);
   
   
   /**
    * Returns a list of POI (Point Of Interest) in a certain zone.
    * The user can filter the number of POIs by using their categories.
    * 
    * @param local The locale used to retreive POI information
    * @param zone The zone of the search.
    * @param filter The filter to apply to the search.
    * @return List of POIs found.
    */
   public PointOfInterest[] getPOIs(String locale, Coordinates coordinate, String filter);

   Position getCurrentLocation();
   
   NavigationSession navigate(RoutePlan route);
   
}
 
