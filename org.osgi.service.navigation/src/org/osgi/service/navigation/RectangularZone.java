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

/*
 * A Box is a rectangular area defined by two coordinates: the top left
 * coordinate and the bottom right coordinate.
 * 
 * ### I am not happy with this implementation class because it means putting
 * implementation code in the spec, which gives all kind of nasty problems. So
 * we need a factory and make these interfaces. Another problem is that we bind
 * the spec to the types of zones.
 */
public class RectangularZone implements Zone {
	Coordinate	center;
	double		width;
	double		height;


	/**
	 * 
	 * @param topleft
	 * @param bottomRight
	 */
	public RectangularZone(Coordinate topLeft, Coordinate bottomRight) {
		// ### How to calculate this, this is non trivial I think because
		// distances are very different from the pole than on the equator
	}
	
	public RectangularZone(Coordinate center, double width, double height) {
		this.center = center;
		this.width = width;
		this.height = height;
	}

	public Coordinate getCenter() {
		return center;
	}

	public boolean contains(Coordinate coordinate) {
		return false;
	}

	public boolean contains(Zone zone) {
		return false;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
