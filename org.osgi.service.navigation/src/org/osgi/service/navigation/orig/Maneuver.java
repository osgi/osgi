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
 * A maneuver is the action that the driver has to perform at a junction. 
 * Several advices can be rendered at a maneuver.
 */
public interface Maneuver extends Hint 
{
   
   /**
    * Returns the junction type where the maneuver will be performed. 
    * The value must be one of the value defined in JunctionType interface.
    * @return int One of the values contained in the JunctionType interface
    */
   public int getJunctionType();
   
   /**
    * Returns the angle of the turn which is the difference between the angle of the
    * from and to segments.
    * @return int The angle made by the from and to segments
    */
   public int getTurnAngle();
   
   /**
    * Returns the action performed at the maneuver.
    * The value must be one of the value defined by ActionType interface.
    * @return int One of the values defined in ActionType interface
    */
   public int getActionType();
   
   /**
    * Returns the road sign information towards the itinerary.
    * It is usuallly a city / municipality names.
    * @return String The road sign information
    */
   public String getRoadSign();
   
   /**
    * Returns the number of the segment by which the planned route is leaving the 
    * junction. 
    * The numbering starts at the from-segment (number 0). For right-turning 
    * possibilities, 
    * numbering proceeds in counter-clockwise order. For left-turning possibilities, 
    * numbering proceeds in clockwise order.
    * @return int The number of exists at the next roundabout
    */
   public int getNumberOfExit();
   
   /**
    * In case of turn action at a maneuver, this flag indicates if there several 
    * possibility (roads)
    * to turn.
    * This information is important to render advice like "At the next junction take 
    * the xxth on the left/right".
    * @return boolean True if there is a multiple turn at the next junction
    */
   public boolean isMultipleTurn();
   
   /**
    * Returns the junction geometry defined by the JunctionGeometry class.
    * This geometry is used to render one or several detailed advices.
    * @return The junction geometry
    */
   public JunctionGeometry getGeometry();
}
