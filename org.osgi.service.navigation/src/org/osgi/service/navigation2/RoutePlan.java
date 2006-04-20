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

package org.osgi.service.navigation2;

/**
 * Defines the complete route plan that the user would like to realise. This
 * plan can be associated to the complete Journey or to a specific via-point. If
 * so then it overrides global navigation plan (if any).
 */
public class RoutePlan {
	int[]		features;
	Location[]	included;
	Location[]	avoid;
	

	public RoutePlan(int[] features, Location included[], Location avoid[]) {
		this.features = features;
		this.included = included;
		this.avoid = avoid;
	}

	/**
	 * Returns the list of LocationPreference objects.
	 * 
	 * @return org.osgi.nursery.util.location.Location[]
	 */
	public Location[] getIncludedLocations() { return included; }

	/**
	 * Returns the list of LocationPreference objects.
	 * 
	 * @return org.osgi.nursery.util.location.Location[]
	 */
	public Location[] getExcludedLocations() { return avoid; }

	/**
	 * Returns the list of FeaturePreference objects.
	 * 
	 * @return org.osgi.nursery.util.route.FeaturePreference[]
	 */
	public int[] getFeaturePreferences() { return features; }
}
