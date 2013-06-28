package org.osgi.service.enocean.datafields;

/**
 * 
 *
 * Subinterface of {@link EnOceanDatafieldDescription} that describes physical measuring datafields.
 * 
 * @author $Id$
 */
public interface EnOceanScaledDatafieldDescription extends EnOceanDatafieldDescription {
	
	/**
	 * The start of the raw input range for this datafield.
	 * @return
	 */
	public int getRangeStart();
	
	/**
	 * The end of the raw input range for this datafield.
	 * @return
	 */
	public int getRangeStop();
	
	/**
	 * The scale start at which this datafield will be mapped to (-20,0°C for instance) 
	 * @return
	 */
	public double getScaleStart();
	
	/**
	 * The scale stop at which this datafield will be mapped to (+30,0°C for instance) 
	 * @return
	 */
	public double getScaleStop();	
	
	/**
	 * The non-mandatory physical unit description of this datafield.
	 * @return
	 */
	public String getUnit();
}
