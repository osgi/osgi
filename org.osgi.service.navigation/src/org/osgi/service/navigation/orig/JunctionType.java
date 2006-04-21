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
 * Contains all possible junction type.
 */
public interface JunctionType 
{
   
   /**
    * Indicates that there is no junction at the maneuver.
    */
   public static final int NONE = 0;
   
   /**
    * Indicates that there is a square at the maneuver.
    */
   public static final int SQUARE = 1;
   
   /**
    * Indicates that there is a enclosed traffic area at the maneuver.
    * An enclosed traffic area is a confined area within which 
    * unstructured traffic movements are allowed.
    */
   public static final int ENCLOSED_TRAFFIC_AREA = 2;
   
   /**
    * Indicates that there is normal intersection at the maneuver.
    */
   public static final int INTERSECTION = 3;
   
   /**
    * Indicates that there is a roundabout at the maneuver.
    */
   public static final int ROUNDABOUT = 4;
   
   /**
    * Indicates that the driver will access a motorway at the next maneuver.
    */
   public static final int MOTORWAY_ENTRY = 5;
   
   /**
    * Indicates that the driver will xit from a motorway at the next maneuver.
    */
   public static final int MOTORWAY_EXIT = 6;
   
   /**
    * Indicates that the driver will access a main road at the next maneuver.
    */
   public static final int MAINROAD_ENTRY = 7;
   
   /**
    * Indicates that the driver will exit from a main road at the next maneuver.
    */
   public static final int MAINROAD_EXIT = 8;
   
   /**
    * Indicates that the driver will access a restricted area at the next maneuver.
    */
   public static final int RESTRICTED_AREA_ENTRY = 9;
   
   /**
    * Indicates that the driver will exit a restricted area at the next maneuver.
    */
   public static final int RESTRICTED_AREA_EXIT = 10;
   
   /**
    * Indicates that the driver will access a facility at the next maneuver.
    */
   public static final int FACILITY_ENTRY = 11;
   
   /**
    * Indicates that the driver will exit from a facility at the next maneuver.
    */
   public static final int FACILITY_EXIT = 12;
}
