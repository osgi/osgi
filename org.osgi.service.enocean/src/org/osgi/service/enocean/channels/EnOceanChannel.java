package org.osgi.service.enocean.channels;


/**
 * Holds the metadata and means to retrieve the actual value of a channel.
 * It is only one of the interfaces that a channel should implement; to be complete,
 * a channel should also implement either the {@link EnOceanDataChannel} interface or
 * the {@link EnOceanEnumChannel} interface.
 *  
 * @version 1.0
 */
public interface EnOceanChannel {
		
	/**
	 * @return The offset, in bits, where this channel is found in the telegram.
	 */
	public int getOffset();
	
	/**
	 * @return The size, in bits, of this channel.
	 */
	public int getSize();
	
	/**
	 * @return The description of this channel, if any.
	 */
	public EnOceanChannelDescription getDescription();


	/**
	 * Gets or sets the raw value of this channel.
	 * @return
	 */
	byte[] getRawValue();
	
	/**
	 * Sets the raw value of a channel.
	 * @param rawValue
	 */
	void setRawValue(byte[] rawValue);

}
