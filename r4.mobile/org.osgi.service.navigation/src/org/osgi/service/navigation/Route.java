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

package org.osgi.service.navigation;

/**
 * Interface to the route definition. The route has several states:
 */
public interface Route extends Location {

	/**
	 * Returns the list of Locations that match to the filter.
	 * 
	 * ### properties?? what does the filter do?
	 * 
	 * @param filter A LDAP query used to filter route elements
	 * @return The list of route elements found
	 */
	public Location[] getRouteElements(String filter);

	public Location[] getRouteElements(RectangularZone viewPort);

	Location getLocationAt(int n);
	int size();
}
