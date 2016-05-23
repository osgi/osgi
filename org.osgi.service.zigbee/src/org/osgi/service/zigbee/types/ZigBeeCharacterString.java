
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Character String' Data Type, as described in
 * the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeCharacterString
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeCharacterString instance = new ZigBeeCharacterString();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeCharacterString getInstance() {
		return instance;
	}

	public String getName() {
		return "CharacterString";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return String.class;
	}

	public short getId() {
		return ZigBeeDataTypes.CHARACTER_STRING;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.CHARACTER_STRING, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.CHARACTER_STRING);
	}

}
