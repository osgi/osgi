
package org.osgi.service.zigbee.types;

import java.util.List;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * This interface represents the 'Structure' Data Type, as described in the
 * ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeStructure
		implements ZCLDataTypeDescription {

	private final static ZigBeeStructure instance = new ZigBeeStructure();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeStructure getInstance() {
		return instance;
	}

	public String getName() {
		return "Structure";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return List.class;
	}

	public short getId() {
		return ZigBeeDataTypes.STRUCTURE;
	}

}
