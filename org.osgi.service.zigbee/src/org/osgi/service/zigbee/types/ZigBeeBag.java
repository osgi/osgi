
package org.osgi.service.zigbee.types;

import java.util.List;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * This interface represents the 'Bag' Data Type, as described in the ZigBee
 * Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeBag
		implements ZCLDataTypeDescription {

	private final static ZigBeeBag instance = new ZigBeeBag();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeBag getInstance() {
		return instance;
	}

	public String getName() {
		return "Bag";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return List.class;
	}

	public short getId() {
		return ZigBeeDataTypes.BAG;
	}

}
