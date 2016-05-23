
package org.osgi.service.zigbee.types;

import java.io.IOException;
import java.util.Date;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'UTC Time' Data Type, as described in the
 * ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeUTCTime
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeUTCTime instance = new ZigBeeUTCTime();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeUTCTime getInstance() {
		return instance;
	}

	public String getName() {
		return "UTCTime";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Date.class;
	}

	public short getId() {
		return ZigBeeDataTypes.UTC_TIME;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.UTC_TIME, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.UTC_TIME);
	}

}
