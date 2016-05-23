
package org.osgi.service.zigbee.types;

import java.io.IOException;
import java.util.Date;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Date' Data Type, as described in the ZigBee
 * Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeDate
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeDate instance = new ZigBeeDate();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeDate getInstance() {
		return instance;
	}

	public String getName() {
		return "Date";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Date.class;
	}

	public short getId() {
		return ZigBeeDataTypes.DATE;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.DATE, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.DATE);
	}

}
