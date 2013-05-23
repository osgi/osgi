package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a User Descriptor as described in the ZigBee Specification 
 * The User Descriptor contains information that allows the user to identify the device using  user-friendly character string.
 * The use of the User Descriptor is optional.
 * 
 * @version 1.0
 */

public interface ZigBeeUserDescriptor {
	/**
	 * @return a user-friendly that identify the device, such as 'Bedroom TV' or 'Stairs light' 
	 */
	public String getUserDescription();
}
