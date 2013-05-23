package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a Complex Descriptor as described in the ZigBee Specification 
 * The Complex Descriptor contains extended information for each of the device descriptions contained in the node.
 * The use of the Complex Descriptor is optional.
 * 
 * @version 1.0
 */

public interface ZigBeeComplexDescriptor {
	/**
	 * @return the language code used for character strings.
	 */
	public String getLanguageCode();
	
	/**
	 * @return the encoding used by characters in the character set.
	 */
	public String getCharacterSetIdentifier();
	
	/**
	 * @return the manufacturer name field.
	 */
	public String getManufacturerName();
	
	/**
	 * @return the model name field
	 */
	public String getModelName();
	
	/**
	 * @return the serial number field.
	 */
	public String getSerialNumber();
	
	/**
	 * @return the Device URL field.
	 */
	public String getDeviceURL();
	
	/**
	 * @return the icon field.
	 */
	public byte[] getIcon();
	
	/**
	 * @return the icon field URL.
	 */
	public String getIconURL();
	
}
