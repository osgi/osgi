package org.osgi.service.zigbee;

import org.osgi.service.zigbee.handler.ZigBeeHandler;

/**
 * This interface represents the machine that hosts the code to run a ZigBee device or client
 * ZigBeeHost must be registered as a service
 *  
 * @version 1.0
 */
public interface ZigBeeHost extends ZigBeeDeviceNode {
	/**
	 * Indicates if a ZigBee device can join the network.
	 * @param duration The time during which associations are permitted.
	 * @return true, false, or null(if the ZigBeeHost is an end device)
	 */
	public void permitJoin(short duration) throws ZigBeeException;
	
	/**
	 * @return The current network channel
	 */
	public int getChannel() throws ZigBeeException;
	
	/**
	 * @return The current network channel mask
	 */
	public int getChannelMask() throws ZigBeeException;
	
	/**
	 * Set the network channel mask
	 */
	public void setChannelMask(ZigBeeHandler handler, int mask) throws ZigBeeException;
	
	/**
	 * @return 0 if security is disabled, an int code if enabled
	 */
	public int getSecurityLevel();
	
	/**
	 * @return A ZigBeeCoordinator object if the chip is the network coordinator, otherwise, returns null.
	 */
	public ZigBeeCoordinator getCoordinator();
}
