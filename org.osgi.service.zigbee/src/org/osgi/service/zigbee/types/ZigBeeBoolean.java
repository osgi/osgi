
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Boolean' Data Type, as described in the ZigBee
 * Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeBoolean
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeBoolean instance = new ZigBeeBoolean();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeBoolean getInstance() {
		return instance;
	}

	public String getName() {
		return "Boolean";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Boolean.class;
	}

	public short getId() {
		return ZigBeeDataTypes.BOOLEAN;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.BOOLEAN, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.BOOLEAN);
	}

}
