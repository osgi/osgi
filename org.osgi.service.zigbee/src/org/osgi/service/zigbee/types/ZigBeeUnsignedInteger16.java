
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Unsigned Integer 16-bits' Data Type, as
 * described in the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeUnsignedInteger16
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeUnsignedInteger16 instance = new ZigBeeUnsignedInteger16();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeUnsignedInteger16 getInstance() {
		return instance;
	}

	public String getName() {
		return "UnsignedInteger16";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Integer.class;
	}

	public short getId() {
		return ZigBeeDataTypes.UNSIGNED_INTEGER_16;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.UNSIGNED_INTEGER_16, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.UNSIGNED_INTEGER_16);
	}

}
