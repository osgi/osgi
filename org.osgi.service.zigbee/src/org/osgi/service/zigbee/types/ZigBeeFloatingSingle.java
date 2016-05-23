
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Floating Single' Data Type, as described in
 * the ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeFloatingSingle
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeFloatingSingle instance = new ZigBeeFloatingSingle();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeFloatingSingle getInstance() {
		return instance;
	}

	public String getName() {
		return "FloatingSingle";
	}

	public boolean isAnalog() {
		return true;
	}

	public Class getJavaDataType() {
		return Float.class;
	}

	public short getId() {
		return ZigBeeDataTypes.FLOATING_SINGLE;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.FLOATING_SINGLE, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.FLOATING_SINGLE);
	}

}
