/*
 * $Id$
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

import org.osgi.util.position.Position;

// ### This should be moved to org.osgi.util.position (or coordinate)

/**
 * Defines a WGS84 coordinate with latitude, longitude and altitude.
 */
public class Coordinate {
	double	latitude;
	double	longitude;
	
	// ### This was a float but that is silly. In java, you must cast
	// floats most of the time. You need a very good reason to save
	// 4 bytes for this misery
	// TODO: if we have to create many of these it will become interesting, chack if double is OK (RvdB)
	double	altitude;

	

	// ### The following constructor should only be there if 
	// we move this class to org.osgi.util.position
	/**
	 * TODO: Is there a need to do this?
	 */
	public Coordinate(Position position) {
		latitude = position.getLatitude().getValue();
		longitude = position.getLongitude().getValue();
		altitude = position.getAltitude().getValue();
	}

	/**
	 * Create a new <code>Coordinate</code> object.
	 * 
	 * @param latitude a double value specifying the latitude in radians
	 * @param longitude a double value specifying the longitude in radians
	 * @param altitude a float value specifying the altitude in meters
	 */
	public Coordinate(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/**
	 * Returns the latitude of this coordinate in radians.
	 * 
	 * @return double
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Returns the longitude of this coordinate in radians.
	 * 
	 * @return double
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the altitude of this coordinate in meters.
	 * 
	 * @return float
	 */
	public double getAltitude() {
		return altitude;
	}

	// ### Dont we have any calculations on Coordinates??
}