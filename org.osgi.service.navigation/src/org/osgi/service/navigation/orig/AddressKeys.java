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
 * AddressKeys interface defines all possible values used as
 * key for a place level. All these must allow the definition of any type
 * of postal address. 
 * The address format is defined by a list of ordered keys made of these predefined
 * values.
 */
public interface AddressKeys 
{
   
   /**
    * Address key referencing the country name.
    */
   public int COUNTRY = 1;
   
   /**
    * Address key referencing the city name.
    */
   public int CITY = 2;
   
   /**
    * Address key referencing the street name.
    */
   public int STREET = 3;
   
   /**
    * Address key referencing the name of an intersecting street.
    */
   public int INTERSECTING_STREET = 4;
   
   /**
    * Address key referencing the house number.
    */
   public int HOUSE_NUMBER = 5;
   
   /**
    * Address key referencing the postal code.
    */
   public int POSTAL_CODE = 6;
   
   /**
    * Address key referencing the county.
    */
   public int COUNTY = 7;
   
   /**
    * Address key referencing the state.
    */
   public int STATE = 8;
   
   /**
    * Address key referencing the building name.
    */
   public int BUILDING_NAME = 9;
   
   /**
    * Address key referencing the building floor.
    */
   public int BUILDING_FLOOR = 10;
   
   /**
    * Address key referencing the building room.
    */
   public int BUILDING_ROOM = 11;
}
