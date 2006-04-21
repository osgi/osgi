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
 * Defines a feature that the user would like to include or exclude.
 * A feature can be set only once. If a user exclude Highway then we 
 * must not include them afterwards.
 */
public class FeaturePreference 
{
   private boolean excluded;
   
   /**
    * FeaturePreference object to set HIGHWAY preference.
    */
   public static final FeaturePreference HIGHWAY = new FeaturePreference (false);
   
   /**
    * FeaturePreference object to set TOLLWAY preference.
    */
   public static final FeaturePreference TOLLWAY = new FeaturePreference (false);
   
   /**
    * FeaturePreference object to set FERRY preference.
    */
   public static final FeaturePreference FERRY = new FeaturePreference (false);
   
   /**
    * Exclude this FeaturePreference object.
    * @param excluded True if the preference is excluded, false otherwise
    */
   private FeaturePreference(boolean excluded) 
   {
		this.excluded = excluded;    
   }
   
   /**
    * Exclude this FeaturePreference object.
    * @param excluded True if the preference is excluded, false otherwise
    */
   public void setExcluded(boolean excluded) 
   {
   		this.excluded = excluded;    
   }
   
   /**
    * Returns true if this FeaturePreference object is excluded otherwise false.
    * @return boolean True if the preference is excluded, false otherwise
    */
   public boolean isExcluded() 
   {
   		return excluded;    
   }
}
