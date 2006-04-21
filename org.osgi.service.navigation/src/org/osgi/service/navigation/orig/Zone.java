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
 * Defines a geographical area.
 */
public interface Zone 
{
   
   /**
    * Indicates if the coordinate is contained in the this object.
    * @param coordinate The coordinate that needs to be checked
    * @return true if the coordinate is inside the object, otherwise false
    */
   public boolean contains(Coordinate coordinate);
   
   /**
    * Indicates if the Shape object given as parameter is in this object.
    * @param shape The Shape
    * @param zone
    * @return true if the shape is completely inside the object, otherwise false
    */
   public boolean contains(Zone zone);
}
