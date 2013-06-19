package org.osgi.service.enocean.datafields;

public interface EnOceanScaledDatafieldDescription extends EnOceanDatafieldDescription {
	
	public int getRangeStart();
	
	public int getRangeStop();
	
	public double getScaleStart();
	
	public double getScaleStop();	
	
	public String getUnit();
}
