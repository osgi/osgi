package org.osgi.service.zigbee.description;

/**
 * This interface represents a ZigBee device description
 * 
 * @version 1.0
 */
public interface ZigBeeDeviceDescription {
	
	/**
	 * @return The device identifier.
	 */
	public int getId();
	
	/**
	 * @return The device name.
	 */
	public String getName();
	
	/**
	 * @return The device version.
	 */
	public short getVersion();
	
	/**
	 * @return The profile identifier.
	 */
	public int getProfileId();
	
	/**
	 * @return An array of server cluster description.
	 */
	public ZigBeeClusterDescription[] getServerClustersDescriptions();
	
	/**
	 * @return an array of client cluster description.
	 */
	public ZigBeeClusterDescription[] getClientClustersDescriptions();
}
