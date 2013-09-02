
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZigBeeAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

public class ZigBeeAttributeDescriptionImpl implements ZigBeeAttributeDescription {

	private int							id;

	private short						accessType;

	private Object						defaultValue;

	private String						name;

	private boolean						isMandatory;

	private boolean						isReportable;

	private ZigBeeDataTypeDescription	datatype;

	public ZigBeeAttributeDescriptionImpl(int id, short accessType, Object defaultvalue, String name,
			boolean isMandatory, boolean isReportable, ZigBeeDataTypeDescription datatype) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.accessType = accessType;
		this.defaultValue = defaultvalue;
		this.name = name;
		this.isMandatory = isMandatory;
		this.isReportable = isReportable;
		this.datatype = datatype;
	}

	public int getId() {
		return id;
	}

	public short getAccessType() {
		return accessType;
	}

	public ZigBeeDataTypeDescription getDataTypeDescription() {
		return datatype;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getShortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public boolean isReportable() {
		// TODO Auto-generated method stub
		return isReportable;
	}

}
