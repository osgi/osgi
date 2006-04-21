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

public interface RouteElement extends Location 
{
   /**
    * Returns the heading of the route element expressed in radian.
    * 
    * @return Measurement
    */
   public Measurement getHeading(); 
   
   /**
    * Returns the form of way of the route element. The list of possible
    * values are defined in FormOfWay interface.
    * 
    * @return org.osgi.service.navigation.FormOfWay
    */
   public FormOfWay getFormOfWay(); 
   
   /**
    * Returns the road class of the route element. The list of possible
    * values are defined in RoadClass interface.
    * 
    * @return org.osgi.service.navigation.RoadClass
    */
   public RoadClass getRoadClass();
   
   /**
    * Return the list of hints associated with this location.
    * 
    * @return org.osgi.service.navigation.Hint
    */
   public Hint[] getHints();
}
