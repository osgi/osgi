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
import org.osgi.service.enocean.EnOceanMessage;

public class EspPacket {

	public static final byte	SYNC_BYTE				= 0x55;
	public static final int		TYPE_RADIO				= 0x01;
	public static final int		TYPE_RESPONSE			= 0x02;
	public static final int		TYPE_RADIO_SUB_TEL		= 0x03;
	public static final int		TYPE_EVENT				= 0x04;
	public static final int		TYPE_COMMON_COMMAND		= 0x05;
	public static final int		TYPE_SMART_ACK_RADIO	= 0x06;
	public static final int		TYPE_REMOTE_MAN_COMMAND	= 0x07;

	private int					packetType;					// 1 byte
	private byte[]				data;
	private byte[]				optional;

	public EspPacket(byte[] header, byte[] payload) {
		deserialize(header, payload);
	}

	public EspPacket(byte[] data) {
		byte[] header = Utils.byteRange(data, 0, 5);
		byte[] payload = Utils.byteRange(data, 6, data.length - 6);
		deserialize(header, payload);
	}

	public EspPacket(EnOceanMessage msg) {
		setPacketType(TYPE_RADIO);
		byte[] data = msg.getBytes();
		setData(data);
		byte[] optional = Utils.byteConcat((byte) msg.getSubTelNum(), Utils.intTo4Bytes(msg.getDestinationId()));
		optional = Utils.byteConcat(optional, (byte) msg.getDbm());
		optional = Utils.byteConcat(optional, (byte) msg.getSecurityLevelFormat());
		setOptional(optional);
	}

	public byte[] serialize() {
		byte[] header = Utils.byteConcat(SYNC_BYTE, Utils.intTo2Bytes(data.length));
		header = Utils.byteConcat(header, (byte) 0x07); // 7 bytes optional data
		header = Utils.byteConcat(header, (byte) TYPE_RADIO);
		byte crcHeader = Utils.crc8(header);
		// Append optional data to data
		byte[] fullData = getFullData();
		// Calc CRC of data
		byte crcData = Utils.crc8(fullData);
		byte[] packet = Utils.byteConcat(header, crcHeader);
		packet = Utils.byteConcat(packet, fullData);
		packet = Utils.byteConcat(packet, crcData);
		return packet;
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

	private void deserialize(byte[] header, byte[] payload) {
		if ((header[0] != SYNC_BYTE) || (header.length != 6)) {
			throw new IllegalArgumentException("wrong header");
		}
		int dataLen = Utils.bytes2intLE(header, 1, 2);
		int optionalLen = header[3];
		setPacketType(header[4]);
		setData(Utils.byteRange(payload, 0, dataLen));
		setOptional(Utils.byteRange(payload, dataLen, optionalLen));
	}
}
