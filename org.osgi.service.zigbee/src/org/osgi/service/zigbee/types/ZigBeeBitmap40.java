
package org.osgi.service.zigbee.types;

import java.io.IOException;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;

/**
 * This interface represents the 'Bitmap 40-bits' Data Type, as described in the
 * ZigBee Specification
 * 
 * @author $Id$
 * 
 */
public class ZigBeeBitmap40
		implements ZCLSimpleTypeDescription {

	private final static ZigBeeBitmap40 instance = new ZigBeeBitmap40();

	/**
	 * Get a Singleton instance of this class
	 * 
	 * @return the Singleton instance
	 */
	public static ZigBeeBitmap40 getInstance() {
		return instance;
	}

	public String getName() {
		return "Bitmap40";
	}

	public boolean isAnalog() {
		return false;
	}

	public Class getJavaDataType() {
		return Long.class;
	}

	public short getId() {
		return ZigBeeDataTypes.BITMAP_40;
	}

	public void serialize(ZigBeeDataOutput os, Object value) {
		ZigBeeDataTypes.serializeDataType(os, ZigBeeDataTypes.BITMAP_40, value);
	}

	public Object deserialize(ZigBeeDataInput is) throws IOException {
		return ZigBeeDataTypes.deserializeDataType(is, ZigBeeDataTypes.BITMAP_40);
	}

}
