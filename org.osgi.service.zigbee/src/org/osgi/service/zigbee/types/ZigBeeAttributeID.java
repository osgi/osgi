
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Attribute ID' Data Type, as described in the
 * ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeAttributeID
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeAttributeID instance = new ZigBeeAttributeID();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeAttributeID getInstance() {
		return instance;
	}

	public String getName() {
		return "AttributeID";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return Integer.class;
	}

	public short getId() {
		return ZigBeeDataTypes.ATTRIBUTE_ID;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.ATTRIBUTE_ID, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.ATTRIBUTE_ID);
	}

}
