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

import org.osgi.util.position.Position;

/**
 * Defines a WGS84 coordinate with latitude, longitude and altitude.
 */
public class Coordinate {
	private double	latitude;
	private double	longitude;
	private float	altitude;

	/**
	 * Create a new <code>Coordinate</code> object from a <code>Position</code> object.
	 * @param position
	 */
	public Coordinate(Position pos) {
		this.latitude = pos.getLatitude().getValue();
		this.longitude = pos.getLongitude().getValue();
		this.altitude = (float) pos.getAltitude().getValue();
	}

	/**
	 * Create a new <code>Coordinate</code> object from another 
	 * <code>Coordinate</code> object. 
	 * @param coord a <tt>Coordinate</tt> object where values are copied from.
	 */
	public Coordinate(Coordinate coord) {
		this.latitude = coord.latitude;
		this.longitude = coord.longitude;
		this.altitude = coord.altitude;
	}

	/**
	 * Create a new <code>Coordinate</code> object. 
	 * @param latitude a double value specifying the latitude in radians
	 * @param longitude a double value specifying the longitude in radians
	 * @param altitude a float value specifying the altitude in meters
	 */
	public Coordinate(double latitude, double longitude, float altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/**
	 * Returns the latitude of this coordinate in radians.
	 * @return double
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Returns the longitude of this coordinate in radians.
	 * @return double
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the altitude of this coordinate in meters.
	 * @return float
	 */
	public float getAltitude() {
		return altitude;
	}
}