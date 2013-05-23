package org.osgi.service.zigbee;

import org.osgi.service.zigbee.handler.ZigBeeHandler;

/**
 * This interface represents the ZigBee network coordinator
 * 
 * @version 1.0
 */
public interface ZigBeeCoordinator {
	/**
	 * Starts the ZigBee network
	 */
	public void startNetwork() throws ZigBeeException;
	
	/**
	 * Updates the list of devices in the network, by adding 
	 * the new devices that joined the network and removing 
	 * the devices that left the network since the last refresh.
	 */
	public void refreshNetwork() throws ZigBeeException;
	
	/**
	 * Set the network channel
	 */
	public void setChannel(ZigBeeHandler handler, int channel) throws ZigBeeException;
	
	/**
	 * @return The current Network key
	 */
	public String getNetworkKey() throws ZigBeeException;
}
