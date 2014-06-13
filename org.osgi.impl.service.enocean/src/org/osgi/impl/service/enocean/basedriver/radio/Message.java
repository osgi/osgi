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

import java.util.ArrayList;
import java.util.List;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanMessage;

public abstract class Message implements EnOceanMessage {

	public static final byte	MESSAGE_4BS		= (byte) 0xA5;
	public static final byte	MESSAGE_RPS		= (byte) 0xF6;
	public static final byte	MESSAGE_1BS		= (byte) 0xD5;
	public static final byte	MESSAGE_VLD		= (byte) 0xD2;
	public static final byte	MESSAGE_SYS_EX	= (byte) 0xC5;

	private byte				RORG;
	private byte[]				data;
	private byte[]				senderId;
	private byte				status;
	private byte				subTelNum;
	private byte[]				destinationId;
	private byte				dbm;
	private byte				securityLevel;
	private byte				func;
	private byte				type;

	private byte[]				messageBytes;

	public Message() {
	}

	public Message(byte[] data) {
		this.messageBytes = data;
		setRORG(data[0]);
		setPayloadBytes(Utils.byteRange(data, 1, data.length - 6 - 7));
		setSenderId(Utils.byteRange(data, data.length - 5 - 7, 4));
		setStatus(data[data.length - 1 - 7]);
		setSubTelNum(data[data.length - 7]);
		setDestinationId(Utils.byteRange(data, data.length - 6, 4));
		setDbm(data[data.length - 2]);
		setSecurityLevel(data[data.length - 1]);
	}

	private void setPayloadBytes(byte[] byteRange) {
		this.data = byteRange;
	}

	public String toString() {
		byte[] out = Utils.byteConcat(RORG, data);
		out = Utils.byteConcat(out, senderId);
		out = Utils.byteConcat(out, status);
		return Utils.bytesToHexString(out);
	}

	/**
	 * The message's RadioTelegram Type
	 */
	public int getRorg() {
		return (RORG & 0xff);
	}

	public void setRORG(int rorg) {
		RORG = (byte) (rorg & 0xff);
	}

	public byte[] getBytes() {
		return messageBytes;
	}

	public int getSenderId() {
		return Utils.bytes2intLE(senderId, 0, 4);
	}

	/**
	 * Sender ID of the message
	 */
	public void setSenderId(byte[] senderId) {
		this.senderId = senderId;
	}

	/**
	 * EnOceanMessage status byte. bit 7 : if set, use crc8 else use checksum
	 * bits 5-6 : reserved bits 0-4 : repeater count
	 */
	public int getStatus() {
		return (status & 0xff);
	}

	public void setStatus(int status) {
		this.status = (byte) (status & 0xff);
	}

	public int getSubTelNum() {
		return subTelNum;
	}

	public void setSubTelNum(byte subTelNum) {
		this.subTelNum = subTelNum;
	}

	public int getDestinationId() {
		return Utils.bytes2intLE(destinationId, 0, 4);
	}

	public void setDestinationId(byte[] destinationId) {
		this.destinationId = destinationId;
	}

	public int getDbm() {
		return dbm;
	}

	public void setDbm(byte dbm) {
		this.dbm = dbm;
	}

	public int getSecurityLevelFormat() {
		return securityLevel;
	}

	public void setSecurityLevel(byte securityLevel) {
		this.securityLevel = securityLevel;
	}

	public void setFunc(int func) {
		this.func = (byte) func;
	}

	public int getFunc() {
		return func;
	}

	public void setType(int type) {
		this.type = (byte) type;
	}

	public int getType() {
		return type;
	}

	public byte[] getPayloadBytes() {
		return data;
	}

	public List getTelegrams() {
		List list = new ArrayList();
		list.add(getBytes());
		return list;
	}

	public abstract boolean isTeachin();

	public abstract boolean hasTeachInInfo();

	public abstract int teachInFunc();

	public abstract int teachInType();

	public abstract int teachInManuf();
}
