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
 * Defines all possible actions that a driver has to perform at a Maneuver.
 */
public interface ActionType 
{
   
   /**
    * Indicates that there is no action at this maneuver. 
    */
   public static final int NO_ACTION = 0;
	   
   /**
    * Indicates that the action is a turn. 
    * The direction of turn is indicated by the turn angle.
    * It is up to the client to decide the right advice.
    */
   public static final int TURN = 1;
   
   /**
    * Indicates that the driver has to take an upward direction.
    */
   public static final int UP = 2;
   
   /**
    * Indicates that the driver has to take a downward direction.
    */
   public static final int DOWN = 3;
   
   /**
    * Indicates that the driver has to continue on the same direction.
    */
   public static final int CONTINUE = 4;
   
   /**
    * Indicates that the driver will arrive exaclty at the destination at the next 
    * maneuver.
    */
   public static final int ARRIVAL = 10;
   
   /**
    * Indicates that the driver will arrive near the destination the next maneuver.
    */
   public static final int ARRIVAL_NEIGHBOURHOOD = 11;
   
   /**
    * Indicates that the driver will arrive near the destination at the next maneuver 
    * but the destination is off-road.
    */
   public static final int ARRIVAL_OFF_ROAD = 12;
}
