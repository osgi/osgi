
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Enumeration 8-bits' Data Type, as described in
 * the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeEnumeration8
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeEnumeration8 instance = new ZigBeeEnumeration8();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeEnumeration8 getInstance() {
		return instance;
	}

	public String getName() {
		return "Enumeration8";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return Short.class;
	}

	public short getId() {
		return ZigBeeDataTypes.ENUMERATION_8;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.ENUMERATION_8, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.ENUMERATION_8);
	}

}
