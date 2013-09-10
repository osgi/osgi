/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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


package org.osgi.test.cases.enocean.packets.radio;

import org.osgi.test.cases.enocean.Utils;
import org.osgi.test.cases.enocean.packets.ByteSerializable;


public class Message implements ByteSerializable {

	public static final int	MESSAGE_4BS	= 0xA5;

	private byte	RORG;
	private byte[]	data;
	private byte[]	senderId;
	// bit7: crc8 if set, or else checksum; bits 0-4: repeater count
	private byte	status;

	/**
	 * Sets senderId and status default values, see EnOcean Radio Protocol spec
	 * for details
	 */
	public Message() {
		setSenderId(0xffffffff);
		// checksum used = CRC8 if STATUS bit 2^7 = 1, repeater status = 0
		setStatus((byte) 0x80);
	}

	public byte[] serialize() {
		byte[] pktBytes = Utils.byteConcat(RORG, getData());
		pktBytes = Utils.byteConcat(pktBytes, getSenderId());
		return Utils.byteConcat(pktBytes, getStatus());
	}

	public byte getRORG() {
		return RORG;
	}

	public void setRORG(int rorg) {
		RORG = (byte) (rorg & 0xff);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = Utils.intTo4Bytes(senderId);
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

}
