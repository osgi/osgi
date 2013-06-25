package org.osgi.service.enocean;


/**
 * This interface represents an EnOcean Host, an ESP3-capable device that offers EnOcean
 * networking calpability.
 *  
 * @version 1.0
 */
public interface EnOceanHost {
	
	public static int FILTER_TYPE_CHIP_ID = 0;
	public static int FILTER_TYPE_RORG = 1;
	public static int FILTER_TYPE_DBM = 2;
	
	
	public static int REPEATER_LEVEL_OFF = 0;
	public static int REPEATER_LEVEL_ONE = 1;
	public static int REPEATER_LEVEL_TWO = 2;
	
	public static int FLASH_TYPE_FLASH = 0;
	public static int FLASH_TYPE_RAM_0 = 1;
	public static int FLASH_TYPE_DATA = 2;
	public static int FLASH_TYPE_IDATA = 3;
	public static int FLASH_TYPE_XDATA = 4;
	
	public interface EnOceanHostFilter {
		public int getType();
		public int getValue();
		public boolean isEnabled();
	}
	
	public interface EnOceanHostSecurity {
		public int securityLevel();
		public byte[] getSecurityKey();
		public int getSecurityRLC();
	}
	
	/**
	 * Reset the EnOcean Host.
	 */
	public void reset() throws EnOceanException;
	
	/**
	 * Set the energy saving period
	 * @param period in 10ms units, up to 0xFFFFFF
	 */
	public void energySaving(int period) throws EnOceanException;
	
	/**
	 * Returns the chip's application version info.
	 * @return
	 */
	public EnOceanVersionInfo appVersion() throws EnOceanException;
	
	/**
	 * Returns the chip's API version info.
	 * @return
	 */
	public EnOceanVersionInfo apiVersion() throws EnOceanException;

	/**
	 * Returns the chip's app description.
	 * @return
	 */
	public String appDescription() throws EnOceanException;
	
	/**
	 * Perform Host's built-in self test.
	 * @return
	 */
	public boolean buitInSelfCheck() throws EnOceanException;

	/**
	 * 
	 * @return the BASE_ID of the device as defined in EnOcean specification.
	 */
	public int getBaseID() throws EnOceanException;
	
	/**
	 * Sets the base ID of the device, may be used up to 10 times.
	 * @param baseID to be set.
	 */
	public void setBaseID(int baseID) throws EnOceanException;
	
	/**
	 * @param level one value in REPEATER_LEVEL_XXX
	 */
	public void setRepeaterLevel(int level) throws EnOceanException;
	
	/**
	 * @return REPEATER_LEVEL_XXX depending on the repeater level.
	 */
	public int getRepeaterLevel() throws EnOceanException;
	
	/** 
	 * @param filterType One of the FILTER_TYPE_XXX values.
	 * @param filterValue The CHIP_ID, RORG or DBM to block or enable
	 * @param block according to the filter if true, or whitelist it.
	 */
	public void addFilter(int filterType, int filterValue, boolean block) throws EnOceanException;
	
	/**
	 * 
	 * @param filterType
	 * @param filterValue
	 */
	public void deleteFilter(int filterType, int filterValue) throws EnOceanException;
	
	/**
	 * Removes all the filters.
	 */
	public void removeFilters() throws EnOceanException;
	
	/**
	 * 
	 * @param enable or not the filters
	 * @param useAndComposition use AND when enabling or disabling; or use an OR instead.
	 */
	public void enableFilters(boolean enable, boolean useAndComposition) throws EnOceanException;
	
	/**
	 * Retrieves the list of currently set filters.
	 * @return
	 */
	public EnOceanHostFilter[] getFilters() throws EnOceanException;
	
	/**
	 * 
	 * @param memType one of the FLASH_TYPE_XXX values.
	 * @param address the actual address inside of the flash bank.
	 * @param data the data payload to write
	 */
	public void writeFlash(int memType, int address, byte[] data) throws EnOceanException;

	/**
	 * 
	 * @param memType one of the FLASH_TYPE_XXX values
	 * @param address the actual address inside 
	 * @param length the desired length to be read inside of the flash
	 * @return the bytes from the flash in case the operation succeeded
	 * @throws EnOceanException
	 */
	public byte[] readFlash(int memType, int address, int length) throws EnOceanException;

	/**
	 * @return EnOcean security information interface object
	 */
	public EnOceanHostSecurity getSecurityInfo() throws EnOceanException;
	
	/**
	 * @param securityLevel the SECURITY_LEVEL_FORMAT as described in the EnOcean specs
	 * @param encryptionKey
	 * @param rollingCode
	 */
	public void setSecurityInfo(int securityLevel, byte[] encryptionKey, int rollingCode) throws EnOceanException;
}
