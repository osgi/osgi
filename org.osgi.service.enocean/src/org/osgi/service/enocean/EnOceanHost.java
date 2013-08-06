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
	
	public static int FLASH_TYPE_FLASH = 0;
	public static int FLASH_TYPE_RAM_0 = 1;
	public static int FLASH_TYPE_DATA = 2;
	public static int FLASH_TYPE_IDATA = 3;
	public static int FLASH_TYPE_XDATA = 4;
	
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
	 * @param level one value in REPEATER_LEVEL_XXX
	 */
	public void setRepeaterLevel(int level) throws EnOceanException;
	
	/**
	 * Gets the current repeater level of the host (cf. ESP3 command 0x0A: C0_RD_REPEATER)
	 * @return REPEATER_LEVEL_XXX depending on the repeater level.
	 */
	public int getRepeaterLevel() throws EnOceanException;
	
	/**
	 * Retrieves the SENDER_ID associated with the given servicePID, if existing on this chip.
	 * @param servicePID
	 * @return
	 */
	public int getSenderId(String servicePID);
}
