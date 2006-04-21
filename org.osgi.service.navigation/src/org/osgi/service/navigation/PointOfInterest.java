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

import java.util.Dictionary;

/**
 * A point of interest represents external service provider, like a hospital, 
 * a gas station, a theatre, a hotel, etc. It consists of a name, a brand, 
 * an address, a type, and a location. 
 * Optionally a point of interest contains an advice: a visual representation 
 * (e.g. HTML page) and an audible representation (e.g. mpeg file). 
 * It can contain as well a phone number, an influence zone where the advice can 
 * be rendered.
 * Points of interest are further separated in national and local POI. 
 * National (or country specific) POI means of "national" importance such as 
 * international airports, Disney Land, London Tower, etc.
 */
public interface PointOfInterest extends Location
{
   /**
    * Returns the POI name.
    * @return The name of the POI
    */
   public String getName();
   
   /**
    * Returns the location (WGS84 coordinates) of possible entries to the POI.
    * These location can be used to determine a route.
    * @return The list of entry points to the POI
    */
   public Coordinates[] getEntryPoints();
   
   /**
    * Returns additional properties of the POI.
    * @return The list of specific properties of the POI, null if properties are not 
    * provided
    */
   public Dictionary getProperties();
   
   /**
    * Returns the POI Category. The third party provider has to deliver the categories 
    * it can handled.
    * The categroy is expressed by a String. It can contain sub-categories separated by a slash.
    * @return String The POI categories. Each sub category must 
    */
   public String getCategory();
}
