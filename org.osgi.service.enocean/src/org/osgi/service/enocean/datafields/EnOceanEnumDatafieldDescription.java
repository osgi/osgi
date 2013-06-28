package org.osgi.service.enocean.datafields;

/**
 * 
 *
 * Subinterface of {@link EnOceanDatafieldDescription} that describes enumerated datafields.
 * 
 * @author $Id$
 */
public interface EnOceanEnumDatafieldDescription extends EnOceanDatafieldDescription {
	
	/**
	 * Gets all the possible value for this datafield.
	 * @return
	 */
	public EnOceanDatafieldEnum[] getPossibleValues();

}
