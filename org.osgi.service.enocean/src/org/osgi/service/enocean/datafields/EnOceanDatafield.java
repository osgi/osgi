package org.osgi.service.enocean.datafields;

/**
 * Holds the metadata and means to retrieve the actual value of a Datafield.
 * It is only one of the interfaces that a Datafield should implement; to be complete,
 * a datafield should also implement either the EnOceanScaledDatafield interface or
 * the EnOceanEnumDatafield interface.
 *  
 * @version 1.0
 */
public interface EnOceanDatafield {
	
	/**
	 * @return The shortcut name of this datafield, as described in the EEP specification.
	 */
	public String getShortcut();
	
	/**
	 * @return The offset, in bits, where this datafield is found in the telegram.
	 */
	public int getOffset();
	
	/**
	 * @return The size, in bits, of this datafield.
	 */
	public int getSize();
	
	/**
	 * @return The human-friendly name of this datafield, if any.
	 */
	public String getName();
	
	/**
	 * @return The description of this datafield, if any.
	 */
	public EnOceanDatafieldDescription getDescription();
	
	/**
	 * @return The value of this datafield.
	 */
	public Object getValue();
	
	/**
	 * Sets the complex value of a datafield.
	 * @param obj
	 */
	public void setValue(Object obj);

	/**
	 * Gets or sets the raw value of this datafield.
	 * @return
	 */
	byte[] getRawValue();
	
	/**
	 * Sets the raw value of a datafield.
	 * @param rawValue
	 */
	void setRawValue(byte[] rawValue);

}
