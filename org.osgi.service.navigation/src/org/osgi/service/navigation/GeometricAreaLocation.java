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