
package org.osgi.service.zigbee.types;

import java.util.List;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * This interface represents the 'Array' Data Type, as described in the ZigBee
 * Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeArray
		implements ZCLDataTypeDescription {

	private final static ZigBeeArray instance = new ZigBeeArray();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeArray getInstance() {
		return instance;
	}

	public String getName() {
		return "Array";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return List.class;
	}

	public short getId() {
		return ZigBeeDataTypes.ARRAY;
	}

}
