
package org.osgi.service.zigbee.types;

import java.util.Set;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * This interface represents the 'Set' Data Type, as described in the ZigBee
 * Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeSet
		implements ZCLDataTypeDescription {

	private final static ZigBeeSet instance = new ZigBeeSet();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeSet getInstance() {
		return instance;
	}

	public String getName() {
		return "Set";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return Set.class;
	}

	public short getId() {
		return ZigBeeDataTypes.SET;
	}

}
