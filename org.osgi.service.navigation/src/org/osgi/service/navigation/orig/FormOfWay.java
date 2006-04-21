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
 * Defines form of way values according to GDF standard.
 */
public interface FormOfWay 
{
   
   /**
    * Indicates that the road segment is part of a Motorway.
    */
   public int MOTORWAY = 0;
   
   /**
    * Indicates that the road segment is part of a Multiple Carriageway 
    * which is not a motorway.
    */
   public int MULTIPLE_CARRIAGEWAY = 1;
   
   /**
    * Indicates that the road segment is part of a Single Carriageway.
    */
   public int SINGLE_CARRIAGEWAY = 2;
   
   /**
    * Indicates that the road segment is part of a Roundabout.
    */
   public int ROUNDABOUT = 3;
   
   /**
    * Indicates that the road segment is part of a Traffic Square.
    */
   public int SQUARE = 4;
   
   /**
    * Indicates that the road segment is part of an Enclosed Traffic Area.
    */
   public int ENCLOSED_TRAFFIC_AREA = 5;
   
   /**
    * Indicates that the road segment is part of a Slip Road.
    */
   public int SLIP_ROAD = 6;
   
   /**
    * Indicates that the road segment is part of a Service Road.
    */
   public int SERVICE_ROAD = 7;
   
   /**
    * Indicates that the road segment is an entrance to or an exit of Car Park.
    */
   public int ENTRANCE_EXIT_CAR_PARK = 8;
   
   /**
    * Indicates that the road segment is an entrance to or an exit to Service.
    */
   public int ENTRANCE_EXIT_SERVICE = 9;
   
   /**
    * Indicates that the road segment is part of a Pedestrian Zone.
    */
   public int PEDESTRIAN_ZONE = 10;
}
