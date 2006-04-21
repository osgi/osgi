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
 * The Address is made of a list of places. Each place is represented by a key
 * (one of the integer values contained in AddressKeys interface) and a value (a
 * String object) which is the place name. The address format can be retrieved
 * by using the getOrdreredKeys() method that returns you an ordered list of
 * keys. A place name can be retrieved by using: � getPlace(int key) with the
 * place key as parameter � getPlaces(), in this case all values are returned at
 * once The AddressKeys interface contains all possible values for address place
 * keys. For a specific Country (or even within the same country) an ordered
 * list of keys is defined and helps the user to read the address received.
 */
public interface AddressLocation extends Location {
	Address getAddress();
}