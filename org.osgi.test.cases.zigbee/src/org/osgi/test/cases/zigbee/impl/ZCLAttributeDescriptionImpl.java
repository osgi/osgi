package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZCLAttributeDescriptionImpl implements ZCLAttributeDescription {

	private int id;
	private boolean isReadOnly;
	private Object defaultValue;
	private String name;
	private boolean isMandatory;
	private boolean isReportable;
	private ZCLDataTypeDescription datatype;

	/**
	 * @param id
	 * @param isReadOnly
	 * @param defaultvalue
	 * @param name
	 * @param isMandatory
	 * @param isReportable
	 * @param datatype
	 */
	public ZCLAttributeDescriptionImpl(int id, boolean isReadOnly,
			Object defaultvalue, String name, boolean isMandatory,
			boolean isReportable, ZCLDataTypeDescription datatype) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.isReadOnly = isReadOnly;
		this.defaultValue = defaultvalue;
		this.name = name;
		this.isMandatory = isMandatory;
		this.isReportable = isReportable;
		this.datatype = datatype;
	}

	public int getId() {
		return id;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public ZCLDataTypeDescription getDataTypeDescription() {
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
		return isReportable;
	}

	public boolean checkValue(Object value) {
		return false;
	}

	public boolean isPartOfAScene() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", isReadOnly: "
				+ isReadOnly + ", defaultValue: " + defaultValue + ", name: "
				+ name + ", isMandatory: " + isMandatory + ", isReportable: "
				+ isReportable + ", datatype: " + datatype + "]";
	}

	public boolean isManufacturerSpecific() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getManufacturerCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ZCLDataTypeDescription getDataType() {
		// TODO Auto-generated method stub
		return datatype;
	}

}
