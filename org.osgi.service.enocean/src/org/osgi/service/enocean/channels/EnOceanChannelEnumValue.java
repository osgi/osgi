package org.osgi.service.enocean.channels;

/**
 * 
 *
 * This transitional interface is used to define all the possible values taken by an enumerated datafield.
 * 
 * @author $Id$
 */
public interface EnOceanChannelEnumValue {
	
	/**
	 * The start value of the enumeration.
	 * @return
	 */
	public int getStart();
	
	/**
	 * The stop value of the enumeration.
	 * @return
	 */
	public int getStop();
	
	/**
	 * The non-mandatory friendly name of this datafield enumeration value ('SWITCH_ON' for instance).
	 * @return
	 */
	public String getName();
	
	/**
	 * A non-mandatory description of what this enumerated value is about.
	 * @return
	 */
	public String getDescription();

}
