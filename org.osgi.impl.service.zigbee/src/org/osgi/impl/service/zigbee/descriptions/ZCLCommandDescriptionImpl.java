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

import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.descriptions.ZCLParameterDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
 * 
 */
public class ZCLCommandDescriptionImpl implements ZCLCommandDescription {

	private int							id;
	private String						name;
	private boolean						isMandatory;
	private ZCLParameterDescription[]	parameterDescription	= null;
	private boolean						isServerSide;
	private int							manufacturerCode;
	private boolean						isCusterSpecificCommand;
	private int							responseCommandId;

	public ZCLCommandDescriptionImpl(int id, String name, boolean mandatory) {
		this.id = id;
		this.name = name;
		this.isMandatory = mandatory;
	}

	public ZCLCommandDescriptionImpl(int id, String name, boolean mandatory, boolean isServerSide, int manufacturerCode, boolean isClusterSpecific, int responseId,
			ZCLParameterDescription[] parameterDescription) {
		this(id, name, mandatory);
		this.parameterDescription = parameterDescription;
		this.isServerSide = isServerSide;
		this.manufacturerCode = manufacturerCode;
		this.isCusterSpecificCommand = isClusterSpecific;
		this.responseCommandId = responseId;
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
		return parameterDescription;
	}

	public boolean isClusterSpecificCommand() {
		return isCusterSpecificCommand;
	}

	public int getManufacturerCode() {
		return manufacturerCode;
	}

	public boolean isClientServerDirection() {
		return isServerSide;
	}

	public boolean isManufacturerSpecific() {
		return manufacturerCode >= 0;
	}

	/**
	 * This method returns the response command id or -1 if the command response
	 * is the defaultResponse.
	 * 
	 * @return
	 */
	public int getResponseCommandId() {
		return responseCommandId;
	}
}
