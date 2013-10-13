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


package org.osgi.impl.service.enocean.basedriver.esp;

import org.osgi.impl.service.enocean.utils.Utils;

public class EspPacket {

	public static final byte	SYNC_BYTE				= 0x55;
	public static final int		TYPE_RADIO				= 0x01;
	public static final int		TYPE_RESPONSE			= 0x02;
	public static final int		TYPE_RADIO_SUB_TEL		= 0x03;
	public static final int		TYPE_EVENT				= 0x04;
	public static final int		TYPE_COMMON_COMMAND		= 0x05;
	public static final int		TYPE_SMART_ACK_RADIO	= 0x06;
	public static final int		TYPE_REMOTE_MAN_COMMAND	= 0x07;

	private int					dataLength;					// 2 bytes
	private int					optionalLength;				// 1 byte
	private int					packetType;					// 1 byte

	private byte[]				data;
	private byte[]				optional;

	public EspPacket(byte[] header, byte[] payload) {
		if ((header[0] != SYNC_BYTE) || (header.length != 6)) {
			throw new IllegalArgumentException("wrong header");
		}
		setDataLength(Utils.bytes2intLE(header, 1, 2));
		setOptionalLength(header[3]);
		setPacketType(header[4]);
		setData(Utils.byteRange(payload, 0, getDataLength()));
		setOptional(Utils.byteRange(payload, getDataLength(), getOptionalLength()));
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getOptionalLength() {
		return optionalLength;
	}

	public void setOptionalLength(int optionalLength) {
		this.optionalLength = optionalLength;
	}

	public int getPacketType() {
		return packetType;
	}

	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getOptional() {
		return optional;
	}

	public void setOptional(byte[] optional) {
		this.optional = optional;
	}

	public byte[] getFullData() {
		return Utils.byteConcat(data, optional);
	}
}
