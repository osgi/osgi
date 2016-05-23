
package org.osgi.service.zigbee.types;

import java.io.IOException;
import java.math.BigInteger;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Unsigned Integer 64-bits' Data Type, as
 * described in the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeUnsignedInteger64
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeUnsignedInteger64 instance = new ZigBeeUnsignedInteger64();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeUnsignedInteger64 getInstance() {
		return instance;
	}

	public String getName() {
		return "UnsignedInteger64";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return BigInteger.class;
	}

	public short getId() {
		return ZigBeeDataTypes.UNSIGNED_INTEGER_64;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.UNSIGNED_INTEGER_64, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.UNSIGNED_INTEGER_64);
	}

}
