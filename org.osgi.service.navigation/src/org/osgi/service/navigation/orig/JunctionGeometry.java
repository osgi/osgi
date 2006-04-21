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
 * A JunctionGeometry is the physical description of the junction that 
 * the driver is about to pass by performing the maneuver.
 */
public interface JunctionGeometry 
{
   
   /**
    * Returns the list of segments that are inside the maneuver geometry.
    * @return org.osgi.nursery.util.maneuver.JunctionSegment[]
    */
   public JunctionSegment[] getInnerSegment();
   
   /**
    * Returns the list of segments that are outside the maneuver geometry.
    * @return org.osgi.nursery.util.maneuver.JunctionSegment[]
    */
   public JunctionSegment[] getOuterSegment();
   
   /**
    * Returns the segment from where the traveler is entering the maneuver
    * @return org.osgi.nursery.util.maneuver.JunctionSegment
    */
   public JunctionSegment getFromSegment();
   
   /**
    * Returns the segment where the traveler is leaving the maneuver.
    * @return org.osgi.nursery.util.maneuver.JunctionSegment
    */
   public JunctionSegment getToSegment();
}
