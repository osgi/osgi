/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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

	private short						id;
	private String						name;
	private boolean						isMandatory;
	private ZCLParameterDescription[]	parameterDescription	= null;
	private boolean						isServerSide;
	private int							manufacturerCode;
	private boolean						isCusterSpecificCommand;
	private int							responseCommandId;
	private byte[]						frame;

	public ZCLCommandDescriptionImpl(short id, String name, boolean mandatory) {
		this.id = id;
		this.name = name;
		this.isMandatory = mandatory;
	}

	public ZCLCommandDescriptionImpl(short id, String name, boolean mandatory, boolean isServerSide, int manufacturerCode, boolean isClusterSpecific, int responseId,
			ZCLParameterDescription[] parameterDescription) {
		this(id, name, mandatory);
		this.parameterDescription = parameterDescription;
		this.isServerSide = isServerSide;
		this.manufacturerCode = manufacturerCode;
		this.isCusterSpecificCommand = isClusterSpecific;
		this.responseCommandId = responseId;
	}

	@Override
	public short getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getShortDescription() {
		return "command description";
	}

	@Override
	public boolean isMandatory() {
		return isMandatory;
	}

	@Override
	public ZCLParameterDescription[] getParameterDescriptions() {
		return parameterDescription;
	}

	@Override
	public boolean isClusterSpecificCommand() {
		return isCusterSpecificCommand;
	}

	@Override
	public int getManufacturerCode() {
		return manufacturerCode;
	}

	@Override
	public boolean isClientServerDirection() {
		return isServerSide;
	}

	@Override
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

	/**
	 * Return the raw frame.
	 * 
	 * @return Returns the raw ZCLFrame or null if the command descriptor do not
	 *         contain any raw frame.
	 */
	public byte[] getFrame() {
		return frame;
	}

	/**
	 * Allows to assign a raw frame to the ZCL Command. The passed byte array
	 * must be a full ZCL frame and the ZCL header part of this frame MUST be
	 * compatible with the other ZCLCommandDescriptor fields.
	 * 
	 * @param frame
	 */

	public void setFrame(byte[] frame) {
		this.frame = frame;
	}
}
