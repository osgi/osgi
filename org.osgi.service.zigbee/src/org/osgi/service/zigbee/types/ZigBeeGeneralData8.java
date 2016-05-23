
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'General Data 8-bits' Data Type, as described
 * in the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeGeneralData8
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeGeneralData8 instance = new ZigBeeGeneralData8();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeGeneralData8 getInstance() {
		return instance;
	}

	public String getName() {
		return "GeneralData8";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return Short.class;
	}

	public short getId() {
		return ZigBeeDataTypes.GENERAL_DATA_8;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.GENERAL_DATA_8, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.GENERAL_DATA_8);
	}

}
