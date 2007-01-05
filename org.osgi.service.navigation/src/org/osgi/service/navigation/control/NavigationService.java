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

package org.osgi.service.navigation.control;

import org.osgi.service.navigation.*;

public interface NavigationService  {
	Location[] getLocation(Address address );
	Location getLocation(Coordinate coordinate);	
	Route createRoute(Location from, Location to);
	Location getCurrentLocation();
	Path startGuidance(Route route );
	void stopGuidance();
	Route getCurrentRoute();
	Path getCurrentPath();
}
 
