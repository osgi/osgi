package org.osgi.service.enocean.channels;

import org.osgi.service.enocean.EnOceanException;

/**
 * 
 *
 * Public and registered description interface for a channel. Encompasses all the possible subtypes
 * for a channel.
 * 
 * @author $Id$
 */
public interface EnOceanChannelDescription {
	
	/**
	 * The unique ID of this EnOceanChannelDescription object.
	 */
	public final static String DESCRIPTION_ID = "enocean.channel.description.id";
	
	/**
	 * A RAW channel is only made of bytes.
	 */
	public final static String TYPE_RAW = "enocean.channel.description.raw";
	
	/**
	 * A DATA channel maps itself to a {@link Double} value representing a physical measure.
	 */
	public final static String TYPE_DATA = "enocean.channel.description.data";
	
	/**
	 * A FLAG channel maps itself to a {@link Boolean} value.
	 */
	public final static String TYPE_FLAG = "enocean.channel.description.flag";
	
	/**
	 * An ENUM channel maps itself to one between a list of discrete {@link EnOceanChannelEnumValue} "value objects".
	 */
	public final static String TYPE_ENUM = "enocean.channel.description.enum";
	
	/**
	 * Retrieves the type of the channel.
	 * 
	 * @return one of the above-described types.
	 */
	public String getType();
	
	/**
	 * Tries to serialize the channel into a series of bytes.
	 * @param obj the value of the channel.
	 * @return the right-aligned value, in raw bytes, of the channel.
	 * @throws EnOceanException
	 */
	public byte[] serialize(Object obj) throws EnOceanException;
	
	/**
	 * Tries to deserialize a series of bytes into a documented value object 
	 * (raw bytes, Double or {@link EnOceanChannelEnumValue}.
	 * Of course this method will be specialized for each {@link EnOceanChannelDescription} subinterface,
	 * depending on the type of this channel.
	 * @param bytes the right-aligned raw bytes. 
	 * @return a value object.
	 * @throws EnOceanException
	 */
	public Object deserialize(byte[] bytes) throws EnOceanException;
}
