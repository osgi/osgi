
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Floating Double' Data Type, as described in
 * the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeFloatingDouble
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeFloatingDouble instance = new ZigBeeFloatingDouble();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeFloatingDouble getInstance() {
		return instance;
	}

	public String getName() {
		return "FloatingDouble";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Double.class;
	}

	public short getId() {
		return ZigBeeDataTypes.FLOATING_DOUBLE;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.FLOATING_DOUBLE, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.FLOATING_DOUBLE);
	}

}
