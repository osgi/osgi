package org.osgi.service.enocean.datafields;


public interface EnOceanEnumDatafieldDescription extends EnOceanDatafieldDescription {
	
	public EnOceanDatafieldEnum[] getPossibleValues();

}
