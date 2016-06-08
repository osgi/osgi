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

package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.descriptions.ZCLParameterDescription;

/**
 * Mocked impl.
 * 
 * @author $Id: 4dc47934ffed925dc345e171dd8980872f7e46b4 $
 * 
 */
public class ZCLCommandDescriptionImpl implements ZCLCommandDescription {

	private int							id;
	private String						name;
	private boolean						isMandatory;
	private ZCLParameterDescription[]	parametersDesc;

	/**
	 * @param id
	 * @param name
	 * @param mandatory
	 */
	public ZCLCommandDescriptionImpl(int id, String name, boolean mandatory) {
		this.id = id;
		this.name = name;
		this.isMandatory = mandatory;
		parametersDesc = null;
	}

	/**
	 * @param id
	 * @param name
	 * @param mandatory
	 * @param parametersDesc
	 */
	public ZCLCommandDescriptionImpl(int id, String name, boolean mandatory, ZCLParameterDescription[] parametersDesc) {
		this.id = id;
		this.name = name;
		this.isMandatory = mandatory;
		this.parametersDesc = parametersDesc;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getShortDescription() {
		return "command description";
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public ZCLParameterDescription[] getParameterDescriptions() {
		return parametersDesc;
	}

	public ZCLFrame serialize(ZCLHeader header, Object[] javaValues) {

		return null;
	}

	public Object[] deserialize(ZCLFrame frame) {

		return null;
	}

	public boolean isClusterSpecificCommand() {

		return false;
	}

	public int getManufacturerCode() {

		return -1;
	}

	public boolean isClientServerDirection() {

		return false;
	}

	public boolean isManufacturerSpecific() {

		return false;
	}

}
