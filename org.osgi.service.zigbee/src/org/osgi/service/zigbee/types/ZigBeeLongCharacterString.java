
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Long Character String' Data Type, as described
 * in the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeLongCharacterString
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeLongCharacterString instance = new ZigBeeLongCharacterString();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeLongCharacterString getInstance() {
		return instance;
	}

	public String getName() {
		return "LongCharacterString";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return String.class;
	}

	public short getId() {
		return ZigBeeDataTypes.LONG_CHARACTER_STRING;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.LONG_CHARACTER_STRING, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.LONG_CHARACTER_STRING);
	}

}
