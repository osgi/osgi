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

/**
 * Interface to a location entity.
 * A location can contain: a coordinate, an address and a zone.
 */
public interface Location extends RouteBoundary 
{
   
   /**
    * Indicates that the address has been entered first and it is the relevant
    * part of the location.
    */
   public static final int ADDRESS_FIRST = 1;
   
   /**
    * Indicates that the coordinate has been entered first and it is the relevant
    * part of the location
    */
   public static final int COORDINATE_FIRST = 2;
    
   /**
    * Indicates that the zone has been entered first and it is the relevant
    * part of the location
    */
   public static final int ZONE_FIRST = 3;
    
   /**
    * Returns the address of this location or null if the address is not found.
    * @return The address at this location, null if the address is not found
    */
   public Address getAddress();
   
   /**
    * Returns the WGS84 coordinate of this location or null if the coordinate
    * is not known.
    * @return The WGS84 coordinate of this location, null if the coordinate
    * is not known.
    */
   public Coordinate getCoordinate(); 
   
   /**
    * Indicates the most relevant part of the address: ADDRESS_FIRST or 
    * COORDINATE_FIRST.
    * This information is important to resolve the address against the map database.
    * @return int The address type.
    */
   public int getType(); 
   
   /**
    * Returns the shape of this location or null if the shape is not known.
    * @return The shape of this location, null if it is not known.
    */
   public Zone getZone();   
}
