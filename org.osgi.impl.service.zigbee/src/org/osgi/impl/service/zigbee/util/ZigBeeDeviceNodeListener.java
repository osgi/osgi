package org.osgi.impl.service.zigbee.util;

/**
 * 
 */
public interface ZigBeeDeviceNodeListener {

	/**
	 * @param uuid
	 */
	public void addDevice(String uuid);

	/**
	 * @param uuid
	 */
	public void removeDevice(String uuid);

}
