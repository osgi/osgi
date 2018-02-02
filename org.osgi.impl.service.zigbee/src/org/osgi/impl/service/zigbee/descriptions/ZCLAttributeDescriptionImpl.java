/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZCLAttributeDescriptionImpl implements ZCLAttributeDescription {
	private int						id;
	private int						manufacturerCode;

	private boolean					isReadOnly;
	private Object					defaultValue;
	private String					name;
	private boolean					isMandatory;
	private boolean					isReportable;
	private ZCLDataTypeDescription	datatype;

	public ZCLAttributeDescriptionImpl(int id, boolean isReadOnly, Object defaultvalue, String name,
			boolean isMandatory, boolean isReportable, ZCLDataTypeDescription datatype) {

		this.id = id;
		this.isReadOnly = isReadOnly;
		this.defaultValue = defaultvalue;
		this.name = name;
		this.isMandatory = isMandatory;
		this.isReportable = isReportable;
		this.datatype = datatype;

		this.manufacturerCode = -1;
	}

	public int getId() {
		return id;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getShortDescription() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public boolean isReportable() {
		return isReportable;
	}

	public boolean isPartOfAScene() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", isReadOnly: " + isReadOnly + ", defaultValue: "
				+ defaultValue + ", name: " + name + ", isMandatory: " + isMandatory + ", isReportable: " + isReportable
				+ ", datatype: " + datatype + "]";
	}

	public boolean isManufacturerSpecific() {
		return manufacturerCode < 0 ? false : true;
	}

	public int getManufacturerCode() {
		return manufacturerCode;
	}

	public ZCLDataTypeDescription getDataType() {
		return datatype;
	}
}
