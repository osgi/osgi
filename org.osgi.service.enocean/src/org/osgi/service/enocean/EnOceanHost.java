package org.osgi.service.enocean;


/**
 * This interface represents an EnOcean Host, an ESP3-capable device that offers EnOcean
 * networking features.
 *  
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanHost {
	
	/**
	 * Filter based on the CHIP_ID of the devices.
	 */
	public static int FILTER_TYPE_CHIP_ID = 0;
	
	/**
	 * Filter based on the Radio Telegram Type of the received messages.
	 */
	public static int FILTER_TYPE_RORG = 1;
	
	/**
	 * Filter based on the actual power level of the messages; can be a pass-high or pass-low.
	 */
	public static int FILTER_TYPE_DBM = 2;
	
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
	 * A sub-interface that is used to access the chip's filter values.
	 * 
	 * @author $Id$
	 */
	public interface EnOceanHostFilter {
		/**
		 * The type of the filter (chip, rorg, dbm)
		 * @return
		 */
		public int getType();
		/**
		 * The value of the filter (a chip id, a particular rorg, a power level)
		 * @return
		 */
		public int getValue();
		
		/**
		 * The status of this filter; is it enabled or disabled ?
		 * @return
		 */
		public boolean isEnabled();
	}
	
	/**
	 * 
	 * A sub-interface to access to all the chip's secutiry properties at once.
	 * 
	 * 
	 * @author $Id$
	 */
	public interface EnOceanHostSecurity {
		/**
		 * Gets the security level mask as specified in the EnOcean Security specification.
		 * @return
		 */
		public int securityLevel();
		/**
		 * Retrieves the current security key (4 bytes), if any. 
		 * @return
		 */
		public byte[] getSecurityKey();
		/**
		 * Retrievs the current rolling code, if any.
		 * @return
		 */
		public int getSecurityRLC();
	}
	
	/**
	 *
	 * Accesses all the version info (chip or API) at once.
	 * 
	 * @author $Id$
	 */
	public interface EnOceanVersionInfo {

		/**
		 * Raw version info, encoded on 4 bytes.
		 * @return  [main,beta,alpha,build]
		 */
		public byte[] raw();
		
		/**
		 * @return Main version info
		 */
		public int main();
		
		/**
		 * @return Beta version info
		 */
		public int beta();
		
		/**
		 * @return Alpha version info
		 */
		public int alpha();
		
		/**
		 * @return Build version info
		 */
		public int build();

	}
	
	/**
	 * Set the energy saving period (cf. ESP3 command 0x01: C0_WR_SLEEP)
	 * @param period in 10ms units, up to 0xFFFFFF
	 */
	public void sleep(int period) throws EnOceanException;
	
	/**
	 * Reset the EnOcean Host (cf. ESP3 command 0x02: C0_WR_RESET)
	 */
	public void reset() throws EnOceanException;
	
	/**
	 * Returns the chip's application version info (cf. ESP3 command 0x03: C0_RD_VERSION)
	 * @return an {@link EnOceanVersionInfo} object containing the application version info.
	 */
	public EnOceanVersionInfo appVersion() throws EnOceanException;
	
	/**
	 * Returns the chip's API version info (cf. ESP3 command 0x03: C0_RD_VERSION)
	 * @return an {@link EnOceanVersionInfo} object containing the API version info.
	 */
	public EnOceanVersionInfo apiVersion() throws EnOceanException;

	/**
	 * Returns the chip's app description (cf. ESP3 command 0x03: C0_RD_VERSION)
	 * @return
	 */
	public String appDescription() throws EnOceanException;
	
	/**
	 * Perform Host's built-in self test (cf. ESP3 command 0x06: C0_WR_BIST)
	 * @return
	 */
	public boolean buitInSelfCheck() throws EnOceanException;

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
	 * Add a filter, specifying its type, value and direction (whitelist or blacklist, 
	 * cf. ESP3 command 0x0B: C0_WR_FILTER_ADD)
	 * @param filterType One of the FILTER_TYPE_XXX values.
	 * @param filterValue The CHIP_ID, RORG or DBM to block or enable
	 * @param block according to the filter if true, or whitelist it.
	 */
	public void addFilter(int filterType, int filterValue, boolean block) throws EnOceanException;
	
	/**
	 * Remove a filter based on its type and value (cf. ESP3 command 0x0C: C0_WR_FILTER_DEL)
	 * @param filterType
	 * @param filterValue
	 */
	public void deleteFilter(int filterType, int filterValue) throws EnOceanException;
	
	/**
	 * Removes all the filters (cf. ESP3 command 0x0D: C0_WR_FILTER_DEL_ALL)
	 */
	public void removeFilters() throws EnOceanException;
	
	/**
	 * 
	 * @param enable or not the filters (cf. ESP3 command 0x0E: C0_WR_FILTER_ENABLE)
	 * @param useAndComposition use AND when enabling or disabling; or use an OR instead.
	 */
	public void enableFilters(boolean enable, boolean useAndComposition) throws EnOceanException;
	
	/**
	 * Retrieves the list of currently set filters (cf. ESP3 command 0x0F: C0_RD_FILTER)
	 * @return a list of {@link EnOceanHostFilter} objects.
	 */
	public EnOceanHostFilter[] getFilters() throws EnOceanException;
	
	/**
	 * Writes X bytes form a flash bank at a specified address (cf. ESP3 command 0x12: C0_WR_MEM)
	 * @param memType one of the FLASH_TYPE_XXX values.
	 * @param address the actual address inside of the flash bank.
	 * @param data the data payload to write
	 */
	public void writeFlash(int memType, int address, byte[] data) throws EnOceanException;

	/**
	 * Reads X bytes from a flash bank at the specified address (cf. ESP3 command 0x13: C0_RD_MEM)
	 * @param memType one of the FLASH_TYPE_XXX values
	 * @param address the actual address inside 
	 * @param length the desired length to be read inside of the flash
	 * @return the bytes from the flash in case the operation succeeded
	 * @throws EnOceanException
	 */
	public byte[] readFlash(int memType, int address, int length) throws EnOceanException;
}
