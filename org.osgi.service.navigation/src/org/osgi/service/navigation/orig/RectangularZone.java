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
 * A Box is a rectangular area defined by two coordinates: the top left 
 * coordinate and the bottom right coordinate.
 */
public class RectangularZone implements Zone 
{
   private Coordinate topLeft;
   private Coordinate bottomRight;
   
   /**
    * Create a new Box object.
    * @param topLeft The top left coordinate of the box.
    * @param bottomRight The bottom right coordinate of the box.
    */
   public RectangularZone(Coordinate topLeft, Coordinate bottomRight) 
   {
	   this.topLeft = topLeft;
	   this.bottomRight = bottomRight;
   }
   
   /**
    * Returns the top left coordinate of the box.
    * @return org.osgi.nursery.util.geo.Coordinate
    */
   public Coordinate getTopLeft() 
   {
	   return topLeft;
   }
   
   /**
    * Returns the bottom right coordinate of the box.
    * @return org.osgi.nursery.util.geo.Coordinate
    */
   public Coordinate getBottomRight() 
   {
	   return bottomRight;
   }
   
   /**
    * Indicates if the coordinate is in the box.
    * @param coordinate The coordinate that need to be checked
    * @return boolean
    */
   public boolean contains(Coordinate coordinate) 
   {
    return true;
   }
   
   /**
    * Indicates if the Shape is in the box. 
    * @param area The area that need tobe checked.
    * @param zone
    * @return boolean
    */
   public boolean contains(Zone zone) 
   {
    return true;
   }
}
