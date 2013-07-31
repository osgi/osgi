package org.osgi.service.enocean.channels;

import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.channels.EnOceanChannelDescription;
import org.osgi.service.enocean.channels.EnOceanChannelEnumValue;

/**
 * 
 *
 * Public and registered description interface for a datafield. Encompasses all the possible subtypes
 * for a datafield.
 * 
 * @author $Id$
 */
public interface EnOceanChannelDescription {
	
	/**
	 * A RAW datafield is only made of bytes.
	 */
	public final static String TYPE_RAW = "enocean.datafield.description.raw";
	
	/**
	 * A SCALED datafield maps itself to a double value representing a physical measure.
	 */
	public final static String TYPE_SCALED = "enocean.datafield.description.scaled";
	
	/**
	 * An ENUM datafield maps itself to one between a list of discrete {@link EnOceanChannelEnumValue} "value objects".
	 */
	public final static String TYPE_ENUM = "enocean.datafield.description.enum";
	
	/**
	 * Retrieves the type of the datafield.
	 * @return
	 */
	public String getType();
	
	/**
	 * Tries to serialize the datafield into a series of bytes.
	 * @param obj
	 * @return
	 * @throws EnOceanException
	 */
	public byte[] serialize(Object obj) throws EnOceanException;
	
	/**
	 * Tries to deserialize a series of bytes into a documented value object 
	 * (raw bytes, Double or {@link EnOceanChannelEnumValue}.
	 * Of course this method will be specialized for each {@link EnOceanChannelDescription} subinterface,
	 * depending on the type of this datafield.
	 * @param bytes
	 * @return an EnOceanDatafield object.
	 * @throws EnOceanException
	 */
	public Object deserialize(byte[] bytes) throws EnOceanException;
}
