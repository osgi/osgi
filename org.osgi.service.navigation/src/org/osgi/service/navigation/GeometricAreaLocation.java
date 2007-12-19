/*******************************************************************************
 * /*
 *  * $Date$
 *  * 
 *  * Copyright (c) OSGi Alliance (2007). All Rights Reserved.
 *  * 
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  */
 *******************************************************************************/
package org.osgi.service.navigation;



/**
 * A Geometric Area represent an area represented by a PolygonArea  
 * @author rob van den berg
 * @version 1.0
 * @created 28-nov-2006 15:28:30
 */
public interface GeometricAreaLocation extends Location {

	/**
	 * 
	 * @returns true if the location is fully enclosed by the area. 
	 * @param loc 
	 */
	public boolean contains(Location loc);

	/**
	 * @returns: Polygon thet forms the edge of the area
	 */
	public PolygonArea getPolygon();

}