
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Long Octet String' Data Type, as described in
 * the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeLongOctetString
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeLongOctetString instance = new ZigBeeLongOctetString();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeLongOctetString getInstance() {
		return instance;
	}

	public String getName() {
		return "LongOctetString";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return byte[].class;
	}

	public short getId() {
		return ZigBeeDataTypes.LONG_OCTET_STRING;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.LONG_OCTET_STRING, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.LONG_OCTET_STRING);
	}

}
