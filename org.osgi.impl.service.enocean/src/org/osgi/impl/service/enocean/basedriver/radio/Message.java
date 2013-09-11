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


package org.osgi.impl.service.enocean.basedriver.radio;

import org.osgi.impl.service.enocean.utils.Utils;


public class Message {

	public static final int	MESSAGE_4BS	= 0xA5;

	private byte	RORG;
	private byte[]	data;
	private byte[]	senderId;
	// bit7: crc8 if set, or else checksum; bits 0-4: repeater count
	private byte	status;

	public Message(byte[] data) {
		setRORG(data[0]);
		setData(Utils.byteRange(data, 1, data.length - 7));
		setSenderId(Utils.byteRange(data, data.length - 6, 4));
		setStatus(data[data.length - 2]);
	}

	/**
	 * Copy constructor
	 * 
	 * @param msg
	 */
	protected Message(Message msg) {
		setRORG(msg.getRORG());
		setData(msg.getData());
		setSenderId(msg.getSenderId());
		setStatus(msg.getStatus());
	}

	public String toString() {
		byte[] out = Utils.byteConcat(RORG, data);
		out = Utils.byteConcat(out, senderId);
		out = Utils.byteConcat(out, status);
		return Utils.bytesToHexString(out);
	}

	public int getRORG() {
		return (RORG & 0xff);
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

	public void setSenderId(byte[] senderId) {
		this.senderId = senderId;
	}

	public int getStatus() {
		return (status & 0xff);
	}

	public void setStatus(int status) {
		this.status = (byte) (status & 0xff);
	}

}
