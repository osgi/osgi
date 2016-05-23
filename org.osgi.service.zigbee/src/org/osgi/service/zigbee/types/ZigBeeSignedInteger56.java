
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Signed Integer 56-bits' Data Type, as
 * described in the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeSignedInteger56
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeSignedInteger56 instance = new ZigBeeSignedInteger56();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeSignedInteger56 getInstance() {
		return instance;
	}

	public String getName() {
		return "SignedInteger56";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Long.class;
	}

	public short getId() {
		return ZigBeeDataTypes.SIGNED_INTEGER_56;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.SIGNED_INTEGER_56, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.SIGNED_INTEGER_56);
	}

}
