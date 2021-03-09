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

package org.osgi.test.cases.enocean.messages;

import org.osgi.service.enocean.EnOceanMessage;
import org.osgi.test.cases.enocean.utils.ByteSerializable;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 * @author $Id$
 */
public class Message implements EnOceanMessage, ByteSerializable {

	/** MESSAGE_RPS */
	public static final int MessageType_1 = 0xF6;
	/** MESSAGE_4BS */
	public static final int MessageType_2 = 0xA5;

	private byte RORG;
	protected byte[] data;
	private byte[] senderId;
	private byte status; // bit7: checksum type.
	// 1=crc8,0=checksum
	// bits 0-4: repeater count.

	// Additional fields
	private int slf;
	private int dbm;
	private int subTelNum;
	private int destinationId;
	private int type;
	private int func;

	/**
	 * Sets senderId and status default values, see EnOcean Radio Protocol spec
	 * for details
	 */
	public Message() {
		setSenderId(0xffffffff);
		// checksum used = CRC8 if STATUS bit 2^7 = 1, repeater status = 0
		setStatus((byte) 0x80);
	}

	public byte[] getBytes() {
		byte[] pktBytes = Utils.byteConcat(RORG, getPayloadBytes());
		pktBytes = Utils.byteConcat(pktBytes, senderId);
		return Utils.byteConcat(pktBytes, status);
	}

	/**
	 * @param rorg
	 */
	public void setRORG(int rorg) {
		RORG = (byte) (rorg & 0xff);
	}

	/**
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	public int getSenderId() {
		return Utils.bytes2intLE(senderId, 0, 4);
	}

	/**
	 * @param senderId
	 */
	public void setSenderId(int senderId) {
		this.senderId = Utils.intTo4Bytes(senderId);
	}

	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 */
	public void setStatus(byte status) {
		this.status = status;
	}

	public int getRorg() {
		return RORG;
	}

	public int getFunc() {
		return func;
	}

	/**
	 * @param func
	 */
	public void setFunc(int func) {
		this.func = func;
	}

	public int getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	public int getDestinationId() {
		return destinationId;
	}

	/**
	 * @param destinationId
	 */
	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}

	public byte[] getPayloadBytes() {
		return data;
	}

	public int getSubTelNum() {
		return subTelNum;
	}

	public int getDbm() {
		return dbm;
	}

	public int getSecurityLevelFormat() {
		return slf;
	}

}
