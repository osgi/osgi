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

import org.osgi.service.zigbee.ZCLHeader;

/**
 * This is a mock implementation of the ZCLHeader interface. This interface
 * represent the ZCL Header field in a ZCL Frame. This implementation is far
 * from a real one, and it is provided to allow to test the test cases provided
 * along with the ZigBee Device Service.
 * 
 * @author $Id$
 */
public class ZCLHeaderImpl implements ZCLHeader {

	private short	commandId;

	private int		manufacturerCode			= -1;

	private boolean	isClusterSpecificCommand	= false;

	private boolean	isManufacturerSpecific		= false;

	private boolean	isClientServerDirection		= false;

	private boolean	disableDefaultResponse		= false;

	private byte	tsn							= 40;

	/**
	 * 
	 * This constructor must be used to create the header for a non-manufacturer
	 * specific command.
	 * 
	 * @param commandId The ZCL Frame Command Id
	 * 
	 * @param isClusterSpecificCommand pass {@code true} if the command is
	 *        cluster specific.
	 * 
	 * @param isClientServerDirection pass {@code true} if the direction of the
	 *        command is Client to Server.
	 * 
	 * @param disableDefaultResponse pass {@code true} to disable the default
	 *        response
	 * 
	 * @param sequenceNumber The Sequence number of the generated frame.
	 *
	 */

	public ZCLHeaderImpl(short commandId, boolean isClusterSpecificCommand, boolean isClientServerDirection,
			boolean disableDefaultResponse, byte sequenceNumber) {
		this.isClusterSpecificCommand = isClusterSpecificCommand;
		this.isClientServerDirection = isClientServerDirection;
		this.disableDefaultResponse = disableDefaultResponse;
		this.commandId = commandId;
		this.tsn = sequenceNumber;
	}

	/**
	 * 
	 * This constructor must be used to create the header for a manufacturer
	 * specific command.
	 * 
	 * @param commandId The ZCL Frame Command Id
	 * 
	 * @param isClusterSpecificCommand {@code true} if the command is cluster
	 *        specific.
	 * 
	 * @param isClientServerDirection {@code true} if the direction of the
	 *        command is Client side to Server side
	 * 
	 * @param disableDefaultResponse {@code true} to disable the default
	 *        response
	 * 
	 * @param sequenceNumber The ZCLFrame Header transaction sequence number
	 * 
	 * @param manufacturerCode The manufacturer code of the command.
	 */
	public ZCLHeaderImpl(short commandId, boolean isClusterSpecificCommand, boolean isClientServerDirection,
			boolean disableDefaultResponse, byte sequenceNumber, int manufacturerCode) {

		this(commandId, isClusterSpecificCommand, isClientServerDirection, disableDefaultResponse, sequenceNumber);

		this.isManufacturerSpecific = true;
		this.manufacturerCode = manufacturerCode;
	}

	public short getCommandId() {
		return this.commandId;
	}

	public int getManufacturerCode() {
		return this.manufacturerCode;
	}

	public boolean isClusterSpecificCommand() {
		return isClusterSpecificCommand;
	}

	public boolean isManufacturerSpecific() {
		return isManufacturerSpecific;
	}

	public boolean isClientServerDirection() {
		return isClientServerDirection;
	}

	public boolean isDefaultResponseDisabled() {
		return disableDefaultResponse;
	}

	public byte getSequenceNumber() {
		return this.tsn;
	}

	/**
	 * This method cannot be implemented and neither tested, since for doing
	 * that we have to reveal the way the bits are mapped into this field.
	 */

	public short getFrameControlField() {
		return 0;
	}
}
