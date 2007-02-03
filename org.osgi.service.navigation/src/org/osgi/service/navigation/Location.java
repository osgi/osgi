/*******************************************************************************
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2006, 2007). All Rights Reserved.
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
 *******************************************************************************/

package org.osgi.service.navigation;

import java.util.Map;

import org.osgi.util.measurement.Measurement;

public interface Location {
	final static int FEATURE_ROAD_OR_FERRY = 41;
	final static int ADMINISTRATIVE_BOUNDARY = 11;
	// etc. from GDF
	
	Measurement getDistance2Location(Location location);
	Coordinate[] getCoordinates();
	Object getSource();
	Address getAddress();	
	Map getAttributes();
	
	int getFeature();
}
