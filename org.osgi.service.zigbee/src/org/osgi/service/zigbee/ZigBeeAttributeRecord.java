package org.osgi.service.zigbee;

import org.osgi.service.zigbee.datatype.ZigBeeDataTypeDescription;

public class ZigBeeAttributeRecord {
	
	private short id;
	
	private ZigBeeDataTypeDescription dataType;
	
	private Object value;
	

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public ZigBeeDataTypeDescription getDataType() {
		return dataType;
	}

	public void setDataType(ZigBeeDataTypeDescription dataType) {
		this.dataType = dataType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}
