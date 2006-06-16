/*******************************************************************************
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
 *******************************************************************************/

package org.osgi.service.navigation;

public class CircularZone implements Location {
	Coordinates		coordinate;
	double			radius;
	
	public CircularZone(Coordinates coordinate, double radius ) {
		this.coordinate  = coordinate;
		this.radius = radius;
	}
	
	public Coordinates getCoordinate() {
		return coordinate;
	}

	public boolean contains(Coordinates coordinate) {
		double [] distance = coordinate.distance(this.coordinate);
		return Math.sqrt(distance[0]*distance[0] + distance[1]*distance[2])<= radius;		
	}

}
