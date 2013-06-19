package org.osgi.service.enocean;

import java.util.Map;

/**
 * This interface represents an EnOcean Device node, a physical device that can communicate
 * using the EnOcean protocol.
 *  
 * @version 1.0
 */
public interface EnOceanDevice {
	
	/**
	 * Property name for the mandatory CHIP_ID of the device
	 */
	public final static String CHIP_ID = "enocean.device.chip_id";
	
	/**
	 * Property name for the BASE_ID of the device
	 * The base ID can be left to 0 if not set.
	 */
	public final static String BASE_ID = "enocean.device.base_id";
	
	/**
	 * Property name for the radiotelegram main type of the profile
	 * associated with this device.
	 */
	public final static String RORG = "enocean.profile.rorg";
	
	/**
	 * Property name for the radiotelegram functional type of the profile
	 * associated with this device.
	 */
	public final static String FUNC = "enocean.profile.func";
	
	/**
	 * Property name for the radiotelegram subtype of the profile
	 * associated with this device.
	 */
	public final static String TYPE = "enocean.profile.type";
	
	/**
	 * Property name for friendly name of this device.
	 */
	public final static String NAME = "enocean.device.name";
	
	/**
	 * Property name for friendly profile name of this device.
	 */
	public final static String PROFILE_NAME = "enocean.device.profile_name";
	
	/**
	 * Property name for the mandatory SLF of the device.
	 */
	public final static String SECURITY_LEVEL_FORMAT = "enocean.device.security_level_format";

	/**
	 * @param the EnOceanTelegram, as raw bytes, to be issued.
	 * @throws EnOceanException
	 */
	public void send(byte[] telegram) throws EnOceanException;
	
	/**
	 * @param the EnOceanTelegram to be issued.
	 * @throws EnOceanException
	 */
	public void send(EnOceanTelegram telegram) throws EnOceanException;
	
	/**
	 * @return Switches the device into learning mode or not.
	 */
	public void setLearningMode(boolean learnMode);
	
	/**
	 * @return The current rolling code in use with this device's communications.
	 */
	public int getRollingCode();
	public void setRollingCode(int rollingCode);
	
	/**
	 * @return The current encryption key used in this device's communications.
	 */
	public byte[] getEncryptionKey();
	public void setEncryptionKey(byte[] key);
	
	/**
	 * @return The latest telegram issued by this device.
	 */
	public EnOceanTelegram getLastTelegram();
	
	/**
	 * @return The list of currently learned devices.
	 */
	public int[] getLearnedDevices();
	
	/**
	 * @return A { MANUFACTURER_ID: FUNCTION_ID } Map of the available RPCs.
	 */
	public Map getRPC();
	
}
