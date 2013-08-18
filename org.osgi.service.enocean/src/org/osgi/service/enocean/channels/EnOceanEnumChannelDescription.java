package org.osgi.service.enocean.channels;


/**
 * 
 *
 * Subinterface of {@link EnOceanChannelDescription} that describes enumerated channels.
 * 
 * @author $Id$
 */
public interface EnOceanEnumChannelDescription extends EnOceanChannelDescription {
	
	/**
	 * Gets all the possible value for this channel.
	 * @return
	 */
	public EnOceanChannelEnumValue[] getPossibleValues();

}
