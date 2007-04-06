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

/**
 * The Address is made of a list of places. Each place is represented by a key
 * (one of the integer values contained in AddressKeys interface) and a value (a
 * String object) which is the place name. The address format can be retrieved
 * by using the getOrdreredKeys() method that returns you an ordered list of
 * keys. A place name can be retrieved by using: getPlace(int key) with the
 * place key as parameter getPlaces(), in this case all values are returned at
 * once The AddressKeys interface contains all possible values for address place
 * keys. For a specific Country (or even within the same country) an ordered
 * list of keys is defined and helps the user to read the address received.
 */
public class Address {
	/**
	 * Address key referencing the country name.
	 */
	public int	COUNTRY				= 1;

	/**
	 * Address key referencing the city name.
	 */
	public int	CITY				= 2;

	/**
	 * Address key referencing the street name.
	 */
	public int	STREET				= 3;

	/**
	 * Address key referencing the name of an intersecting street.
	 */
	public int	INTERSECTING_STREET	= 4;

	/**
	 * Address key referencing the house number.
	 */
	public int	HOUSE_NUMBER		= 5;

	/**
	 * Address key referencing the postal code.
	 */
	public int	POSTAL_CODE			= 6;

	/**
	 * Address key referencing the county.
	 */
	public int	COUNTY				= 7;

	/**
	 * Address key referencing the state.
	 */
	public int	STATE				= 8;

	/**
	 * Address key referencing the building name.
	 */
	public int	BUILDING_NAME		= 9;

	/**
	 * Address key referencing the building floor.
	 */
	public int	BUILDING_FLOOR		= 10;

	/**
	 * Address key referencing the building room.
	 */
	public int	BUILDING_ROOM		= 11;
	/**
	 * Address key referencing a non-administrative area. (e.g. "Foret de
	 * Fontainebleau")
	 */
	public static int AREANAME = 12;

	
	String[]	places;

	int[]		keys;

	/**
	 * Initialize a new Address object with a set of places.
	 * 
	 * @param places Descriptive values of a place.
	 * @param keys One of the predefined keys.
	 */
	public Address(String[] places, int[] keys) {
		this.places = places;
		this.keys = keys;
	}

	/**
	 * Returns the list of place names that define the address.
	 * 
	 * @return List of place names
	 */
	public String[] getPlaces() {
		return places;
	}

	/**
	 * Returns the place name of a specified place level. The key must be one
	 * the values contained in AddressKey interface.
	 * 
	 * @param key The key of the requested place name
	 * @return String
	 */
	public String getPlace(int key) {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == key)
				return places[i];
		}
		return null;
	}

	/**
	 * Returns the ordered list of keys used to define the address. Each key
	 * represents a place level.
	 * 
	 * @return The ordered list of keys
	 */
	public int[] getOrderedKeys() {
		return keys;
	}
}