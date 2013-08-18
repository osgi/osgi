package org.osgi.service.enocean;


/**
 * This interface represents an EnOcean Host, a device that offers EnOcean
 * networking features.
 *  
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanHost {
	
	
	/**
	 * The unique ID for this Host: this matches the CHIP_ID of the 
	 * EnOcean Gateway Chip it embodies.
	 */
	public static Object	HOST_ID	= "enocean.host.id";
	
	/**
	 * repeater level to disable repeating; this is the default.
	 */
	public static int REPEATER_LEVEL_OFF = 0;
	/**
	 * repeater level to repeat every telegram at most once.
	 */
	public static int REPEATER_LEVEL_ONE = 1;
	/**
	 * repeater level to repeat every telegram at most twice.
	 */
	public static int REPEATER_LEVEL_TWO = 2;

	
	/**
	 * Reset the EnOcean Host (cf. ESP3 command 0x02: C0_WR_RESET)
	 */
	public void reset() throws EnOceanException;
	
	/**
	 * Returns the chip's application version info (cf. ESP3 command 0x03: C0_RD_VERSION)
	 * @return a String object containing the application version info.
	 */
	public String appVersion() throws EnOceanException;
	
	/**
	 * Returns the chip's API version info (cf. ESP3 command 0x03: C0_RD_VERSION)
	 * @return a String object containing the API version info.
	 */
	public String apiVersion() throws EnOceanException;

	/**
	 * Gets the BASE_ID of the chip, if set (cf. ESP3 command 0x08: C0_RD_IDBASE)
	 * @return the BASE_ID of the device as defined in EnOcean specification 
	 */
	public int getBaseID() throws EnOceanException;
	
	/**
	 * Sets the base ID of the device, may be used up to 10 times (cf. ESP3 command 0x07: C0_WR_IDBASE)
	 * @param baseID to be set.
	 */
	public void setBaseID(int baseID) throws EnOceanException;
	
	/**
	 * Sets the repeater level on the host (cf. ESP3 command 0x09: C0_WR_REPEATER)
	 * @param level one of the Repeater Level constants as defined above.
	 */
	public void setRepeaterLevel(int level) throws EnOceanException;
	
	/**
	 * Gets the current repeater level of the host (cf. ESP3 command 0x0A: C0_RD_REPEATER)
	 * @return one of the Repeater Level constants as defined above.
	 */
	public int getRepeaterLevel() throws EnOceanException;
	
	/**
	 * Retrieves the SENDER_ID associated with the given servicePID, if existing on this chip. 
	 * @param servicePID
	 * @return the associated CHIP_ID of the exported device.
	 */
	public int getSenderId(String servicePID) throws IllegalArgumentException;

}
