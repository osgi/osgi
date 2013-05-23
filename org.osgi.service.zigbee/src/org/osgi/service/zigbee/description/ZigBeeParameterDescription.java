package org.osgi.service.zigbee.description;

import org.osgi.service.zigbee.datatype.ZigBeeDataTypeDescription;


/**
 * This interface represents a ZigBee parameter description
 * 
 * @version 1.0
 */
public interface ZigBeeParameterDescription {
	/**
	 * @return the parameter data type
	 */
	public ZigBeeDataTypeDescription getDataTypeDescription();
	
	/**
	 * checks whether the value object is conform to the parameter
	 * data type description
	 * 
	 * @param value The value to check
	 * @return true if value is conform otherwise returns false
	 */
	public boolean checkValue(Object value);
}
