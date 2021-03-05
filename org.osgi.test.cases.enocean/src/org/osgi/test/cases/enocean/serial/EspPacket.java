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

package org.osgi.test.cases.enocean.serial;

import org.osgi.test.cases.enocean.utils.ByteSerializable;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 * @author $Id$
 */
public class EspPacket {

	/** SYNC_BYTE */
	public static final byte	SYNC_BYTE				= 0x55;
	/** TYPE_RADIO */
	public static final int		TYPE_RADIO				= 0x01;
	/** TYPE_RESPONSE */
	public static final int		TYPE_RESPONSE			= 0x02;
	/** TYPE_RADIO_SUB_TEL */
	public static final int		TYPE_RADIO_SUB_TEL		= 0x03;
	/** TYPE_EVENT */
	public static final int		TYPE_EVENT				= 0x04;
	/** TYPE_COMMON_COMMAND */
	public static final int		TYPE_COMMON_COMMAND		= 0x05;
	/** TYPE_SMART_ACK_RADIO */
	public static final int		TYPE_SMART_ACK_RADIO	= 0x06;
	/** TYPE_REMOTE_MAN_COMMAND */
	public static final int		TYPE_REMOTE_MAN_COMMAND	= 0x07;

	private int					dataLength;					// 2 bytes
	private int					optionalLength;				// 1 byte
	private int					packetType;					// 1 byte

	private ByteSerializable	data;
	private ByteSerializable	optional;

	/**
	 * 
	 */
	public EspPacket() {

	}

	/**
	 * @param data
	 */
	public EspPacket(byte[] data) {
		byte[] header = Utils.byteRange(data, 0, 6);
		byte[] payload = Utils.byteRange(data, 6, data.length - 7);
		deserialize(header, payload);
	}

	/**
	 * @return serialized value.
	 */
	public byte[] serialize() {
		byte[] dataBytes = data.getBytes();
		setDataLength(dataBytes.length);
		dataBytes = Utils.byteConcat(dataBytes, optional.getBytes());
		byte[] crc = Utils.byteToBytes(Utils.crc8(dataBytes));
		dataBytes = Utils.byteConcat(dataBytes, crc);
		return Utils.byteConcat(serializeHeader(), dataBytes);
	}

	/**
	 * @return data's length.
	 */
	public int getDataLength() {
		return dataLength;
	}

	/**
	 * @param dataLength
	 */
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	/**
	 * @return optional's length.
	 */
	public int getOptionalLength() {
		return optionalLength;
	}

	/**
	 * @param optionalLength
	 */
	public void setOptionalLength(int optionalLength) {
		this.optionalLength = optionalLength;
	}

	/**
	 * @return packet's type.
	 */
	public int getPacketType() {
		return packetType;
	}

	/**
	 * @param packetType
	 */
	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	/**
	 * @return data.
	 */
	public ByteSerializable getData() {
		return data;
	}

	/**
	 * @param embedded
	 */
	public void setData(ByteSerializable embedded) {
		this.data = embedded;
	}

	/**
	 * @return optional.
	 */
	public ByteSerializable getOptional() {
		return optional;
	}

	/**
	 * @param optional
	 */
	public void setOptional(ByteSerializable optional) {
		this.optional = optional;
	}

	private byte[] serializeHeader() {
		byte[] syncByte = Utils.intTo1Byte(SYNC_BYTE);
		byte[] header = Utils.intTo2Bytes(getDataLength());
		header = Utils.byteConcat(header, Utils.intTo1Byte(getOptionalLength()));
		header = Utils.byteConcat(header, Utils.intTo1Byte(getPacketType()));
		byte[] fullHeader = Utils.byteConcat(syncByte, header);
		return Utils.byteConcat(fullHeader, Utils.crc8(header));
	}

	private void deserialize(byte[] header, byte[] payload) {
		if ((header[0] != SYNC_BYTE) || (header.length != 6)) {
			throw new IllegalArgumentException("wrong header");
		}
		int dataLen = Utils.bytes2intLE(header, 1, 2);
		int optionalLen = header[3];
		setPacketType(header[4]);
		setData(new TrivialByteSerializable(Utils.byteRange(payload, 0, dataLen)));
		setOptional(new TrivialByteSerializable(Utils.byteRange(payload, dataLen, optionalLen)));
	}
}
