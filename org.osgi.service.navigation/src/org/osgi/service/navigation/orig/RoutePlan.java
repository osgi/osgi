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
 * Defines the complete route plan that the user would like to realise.
 * This plan can be associated to the complete Journey or to a specific via-point. 
 * If so then it overrides global navigation plan (if any).
 */
public class RoutePlan 
{
   
   /**
    * Indicates that the shortest way must be found.
    */
   public static final int SHORTEST = 0;
   
   /**
    * Indicates that the fastest way must be found.
    */
   public static final int FASTEST = 1;
   private int navPref;
   private FeaturePreference featurePrefs[];
   private Location includedLocs[];
   private Location excludedLocs[];
   
   /**
    * @param navPref
    * @param featurePrefs
    * @param includedLocs
    * @param excludedLocs
    */
   public RoutePlan(int navPref, FeaturePreference[] featurePrefs, Location[] includedLocs, Location[] excludedLocs) 
   {
		this.navPref = navPref;
		this.featurePrefs = featurePrefs;
		this.includedLocs = includedLocs;
		this.excludedLocs = excludedLocs;    
   }
   
   /**
    * Returns the navigation preference set for this NavigationPlan object.
    * The value is one of the value defined by this class.
    * @return int
    */
   public int getNavigationPreference() 
   {
   		return navPref;    
   }
   
   /**
    * Returns the list of LocationPreference objects.
    * @return org.osgi.nursery.util.location.Location[]
    */
   public Location[] getIncludedLocations() 
   {
   		return includedLocs;    
   }
   
   /**
    * Returns the list of LocationPreference objects.
    * @return org.osgi.nursery.util.location.Location[]
    */
   public Location[] getExcludedLocations() 
   {
   		return excludedLocs;    
   }
   
   /**
    * Returns the list of FeaturePreference objects.
    * @return org.osgi.nursery.util.route.FeaturePreference[]
    */
   public FeaturePreference[] getFeaturePreferences() 
   {
   		return featurePrefs;    
   }
}
