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

import org.osgi.util.measurement.Measurement;


/**
 * A Circular Zone is defined by a center expressed by a WGS84 coordinate
 * and a radius expressed in meter.
 */
public class CircularZone implements Zone 
{
   private Measurement radius;
   private Coordinate center;
   
   /**
    * Create a new CircularZone object.
    * @param center The center of the zone.
    * @param radius The radius of the zone expressed in meter.
    */
   public CircularZone(Coordinate center, Measurement radius) 
   {
	   this.radius = radius;
	   this.center = center;
   }
   
   /**
    * Returns the radius of the zone expressed in meter.
    * @return The radius of the Circular Zone expressed in meter.
    */
   public Measurement getRadius() 
   {
    return radius;
   }
   
   /**
    * Returns the center of the zone expressed by a Coordinate object.
    * @return The center of the zone.
    */
   public Coordinate getCenter() 
   {
    return center;
   }
   
   /**
    * Indicates if the coordinate is contained in the this object.
    * @param coordinate The coordinate that needs to be checked
    * @return true if the coordinate is inside the zone, false otherwise.
    */
   public boolean contains(Coordinate coordinate) 
   {
    return true;
   }
   
   /**
    * Indicates if the area is contained in this object. 
    * @param zone The zone that needs to be checked
    * @return boolean true if the given zone is inside the zone, false otherwise.
    */
   public boolean contains(Zone zone) 
   {
    return true;
   }
}
