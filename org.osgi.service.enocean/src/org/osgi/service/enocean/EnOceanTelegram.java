package org.osgi.service.enocean;

import org.osgi.service.enocean.datafields.EnOceanDatafield;

/**
 * Holds the necessary methods to interact with an EnOcean Telegram.
 *  
 * @version 1.0
 */
public interface EnOceanTelegram {
	
	/**
	 * Property name for the radiotelegram main type of the profile
	 * associated with this device.
	 */
	public final static String RORG = "enocean.profile.rorg";
	
	/**
	 * Property name for the sender ID of this telegram (mandatory)
	 */
	public final static String SENDER_ID = "enocean.profile.sender_id";
	
	/**
	 * Property name for the optional destination ID of this telegram.
	 */
	public final static String DESTINATION_ID = "enocean.profile.destination_id";
	
	/**
	 * @return the current EnOcean status of this telegram.
	 */
	public int getStatus();
	
	/**
	 * 
	 * @param status the status to be set.
	 */
	public void setStatus(int status);
	
	/**
	 * @return The serialized byte list corresponding to the binary telegram.
	 * @throws EnOceanException
	 */
	public byte[] serialize() throws EnOceanException;
	
	/**
	 * @return The deserialized object from a sequence of bytes. The actual return type depends
	 * 			on the implementation.
	 * @throws EnOceanException
	 */
	public Object deserialize(byte[] bytes) throws EnOceanException;

	/**
	 * @return The list of associated datafields.
	 */
	public EnOceanDatafield[] getDatafields();
	
	/**
	 * @return The number of subtelegrams in the case of multifram messages.
	 */
	public int getSubtelegramCount();}
