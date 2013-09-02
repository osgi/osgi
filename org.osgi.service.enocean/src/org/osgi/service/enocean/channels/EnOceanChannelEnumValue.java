package org.osgi.service.enocean.channels;

/**
 * 
 *
 * This transitional interface is used to define all the possible values taken by an enumerated channel.
 * 
 * @author $Id$
 */
public interface EnOceanChannelEnumValue {
	
	/**
	 * The start value of the enumeration.
	 * 
	 * @return the start value.
	 */
	public int getStart();
	
	/**
	 * The stop value of the enumeration.
	 * 
	 * @return the stop value.
	 */
	public int getStop();
	
	/**
	 * A non-mandatory description of what this enumerated value is about.
	 * @return the english description of this channel.
	 */
	public String getDescription();

}
