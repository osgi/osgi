
package org.osgi.service.zigbee.types;

import java.io.IOException;
import java.math.BigInteger;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'General Data 64-bits' Data Type, as described
 * in the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeGeneralData64
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeGeneralData64 instance = new ZigBeeGeneralData64();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeGeneralData64 getInstance() {
		return instance;
	}

	public String getName() {
		return "GeneralData64";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return BigInteger.class;
	}

	public short getId() {
		return ZigBeeDataTypes.GENERAL_DATA_64;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.GENERAL_DATA_64, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.GENERAL_DATA_64);
	}

}
