package org.osgi.service.enocean.channels;

import org.osgi.service.enocean.channels.EnOceanChannelDescription;
import org.osgi.service.enocean.channels.EnOceanChannelEnumValue;

/**
 * 
 *
 * Subinterface of {@link EnOceanChannelDescription} that describes enumerated datafields.
 * 
 * @author $Id$
 */
public interface EnOceanEnumChannelDescription extends EnOceanChannelDescription {
	
	/**
	 * Gets all the possible value for this datafield.
	 * @return
	 */
	public EnOceanChannelEnumValue[] getPossibleValues();

}
