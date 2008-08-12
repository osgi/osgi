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
//### This should be moved to org.osgi.util.position (or coordinate)

/**
 * Defines a WGS84 coordinate with latitude, longitude and altitude.
 */
public class Coordinates {
	private double	latitude;
	private double	longitude;
	private float	altitude;

	/**
	 * Create a new <code>Coordinate</code> object. 
	 * @param latitude a double value specifying the latitude in radians
	 * @param longitude a double value specifying the longitude in radians
	 * @param altitude a float value specifying the altitude in meters
	 */
	public Coordinates(double latitude, double longitude, float altitude) {
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

	/**
	 * Calculate the x, y, and z distance between this 
	 * coordinate and the other coordinate. The distance returned
	 * must be in meters.
	 * 
	 * ### complicated calculation
	 * 
	 * @param coordinate
	 * @return the x, y, and z distance in meters
	 */
	public double[] distance(Coordinates coordinate) {
		// TODO Auto-generated method stub
		return new double[3];
	}

	/**
	 * Return a new coordinate that is translated from this
	 * coordinate in meters.
	 * 
	 * @param x longitude direction translation?
	 * @param y latitude direction translation?
	 * @param z altidude direction translatation
	 * @return a translated coordinate
	 */
	public Coordinates translate(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}
}