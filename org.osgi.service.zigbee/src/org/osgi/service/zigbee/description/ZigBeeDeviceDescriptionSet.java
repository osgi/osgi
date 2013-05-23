package org.osgi.service.zigbee.description;

/**
 * This interface represents a ZigBee Device description Set
 * ZigBeeDeviceDescriptionSet is registered as an OSGi Service
 * Provides method to retrieve Endpoints descriptions
 * 
 * @version 1.0
 */
public interface ZigBeeDeviceDescriptionSet {
	
	
	public final static String VERSION = "zigbee.profile.version";
	
	public final static String PROFILE_ID = "zigbee.profile.id";
	
	public final static String PROFILE_NAME = "zigbee.profile.name";
	
	public final static String MANUFACTURER_CODE = "zigbee.profile.manufacturer.code";
	
	public final static String MANUFACTURER_NAME = "zigbee.profile.manufacturer.name";
	
	public final static String DEVICES = "zigbee.profile.devices";
	
	/**
	 * @param id deviceId Identifier of the device.
	 * @param version The version of the application profile.
	 * @return The associated device description.
	 */
	public ZigBeeDeviceDescription getDeviceSpecification(int deviceId, short version);
}
