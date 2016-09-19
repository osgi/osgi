
package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.ZCLAttributeInfo;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

public class ZCLAttributeInfoImpl implements ZCLAttributeInfo {

	private int						manufacturerCode;
	private ZCLDataTypeDescription	dataType;
	private int						id;

	public ZCLAttributeInfoImpl(int id, int manufacturerCode, ZCLDataTypeDescription dataType) {
		this.id = id;
		this.manufacturerCode = manufacturerCode;
		this.dataType = dataType;
	}

	public boolean isManufacturerSpecific() {
		return manufacturerCode >= 0 ? true : false;
	}

	public int getManufacturerCode() {
		return manufacturerCode;
	}

	public int getId() {
		return id;
	}

	public ZCLDataTypeDescription getDataType() {
		return dataType;
	}

}
