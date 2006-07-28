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

/**
 * A Circular Zone is defined by a center expressed by a WGS84 
 * coordinate and a radius expressed in meter.
 *
 * ### I am not happy with this implementation class because it
 *     means putting implementation code in the spec, which gives
 *     all kind of nasty problems. So we need a factory
 *     and make these interfaces. Another problem is that
 *     we bind the spec to the types of zones.
 *     
 * @version $Revision$
 */
public class CircularZone extends RectangularZone {
	
	public CircularZone(Coordinate coordinate, double radius ) {
		super(coordinate, radius, radius);
	}
	
	public double getRadius() {
		return getWidth();
	}
	
	public boolean contains(Coordinate coordinate) {
		return false;
	}

	public boolean contains(Zone coordinate) {
		// TODO
		return false;
	}
}
