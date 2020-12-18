/*
 * Copyright (c) OSGi Alliance (2013, 2020). All Rights Reserved.
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

package org.osgi.test.cases.enocean.messages;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.enocean.EnOceanRPC;
import org.osgi.test.cases.enocean.utils.Logger;
import org.osgi.test.cases.enocean.utils.Utils;

/**
 * SYS_EX message implementation.
 * 
 * @author $Id$
 */
public class MessageSYS_EX extends Message {

	/** MESSAGE_SYS_EX */
	public static final byte MESSAGE_SYS_EX = (byte) 0xC5;

	private List<byte[]>		subTelegrams	= new ArrayList<>();

	int							manuf;

	public int getRorg() {
		return MESSAGE_SYS_EX;
	}

	public int getSubTelNum() {
		return subTelegrams.size();
	}

	/**
	 * @param data
	 */
	public MessageSYS_EX(byte[] data) {
		super();

		Logger.i("MessageSYS_EX", "data: " + Utils.bytesToHex(data));
		byte[] data_0 = Utils.byteRange(data, 2, 4);
		manuf = ((data_0[1] & 0x7f) << 4) + ((data_0[2] & 0xf0) >> 4);
		int func = ((data_0[2] & 0x0ff) << 8) + data_0[3];
		int len = (data_0[0] << 1) + ((data_0[1] & 0x80) >> 7);
		// Logger.i("MessageSYS_EX", "(data_0[1] & 0x7f) << 4): "
		// + ((data_0[1] & 0x7f) << 4) + " / (data_0[2] & 0xf0) >> 4): "
		// + ((data_0[2] & 0xf0) >> 4));
		// Logger.i("MessageSYS_EX", "(data_0[2] & 0x0ff) << 8): "
		// + ((data_0[2] & 0x0ff) << 8) + " / (data_0[3]): " + data_0[3]);
		// Logger.i("MessageSYS_EX", "(data_0[0] << 1): " + (data_0[0] << 1)
		// + " / (data_0[1] & 0x80) >> 7): " + ((data_0[1] & 0x80) >> 7));
		Logger.i("MessageSYS_EX", "manuf: " + manuf + " / func: " + func
				+ " / len: " + len);
		setFunc(func);
		setSenderId(Utils.bytes2intLE(data, 10, 4));
		if (len <= 4) {
			setData(Utils.byteRange(data, 6, len));
		} else {
			byte[] data_1 = Utils.byteRange(data, 6, 4);
			int extraTelegrams = (int) Math.floor((len - 4) / 8) + 1;
			for (int i = 0; i < extraTelegrams; i++) {
				Logger.i("MessageSYS_EX", "data_1: " + Utils.bytesToHex(data_1));
				data_1 = Utils.byteConcat(data_1,
						Utils.byteRange(data, 18 + i * 16, 8));
			}
			Logger.i("MessageSYS_EX", "data_1: " + Utils.bytesToHex(data_1));
			setData(Utils.byteRange(data_1, 0, len));
		}
		Logger.i("MessageSYS_EX", "data0: " + Utils.bytesToHex(getBytes())
				+ " / sender: " + getSenderId());
	}

	/**
	 * @param rpc
	 */
	public MessageSYS_EX(EnOceanRPC rpc, int destinationId) {
		int rnd = (int) (Math.random() * 3);
		byte[] payload = rpc.getPayload();
		if (payload == null) {
			payload = new byte[] {};
		}
		// payload without manuf, function...
		int len = payload.length;
		manuf = rpc.getManufacturerId();
		int func = rpc.getFunctionId();
		int sender = rpc.getSenderId();

		// Generate first telegram
		byte[] data_0 = new byte[] {
				(byte) (len >> 1),
				(byte) (((len & 0x01) << 7) | ((manuf & 0x7f0) >> 4)),
				(byte) (((manuf & 0x0f) << 4) | ((func & 0xf00) >> 8)),
				(byte) (func & 0xff)
		};
		byte[] data_1 = payload; // Utils.byteRange(payload, 0, len);
		data_1 = Utils.padUpTo(data_1, 4);
		data_0 = Utils.byteConcat(data_0, data_1);
		byte[] telegram0 = generateTelegram(MESSAGE_SYS_EX,
				getIdx(rnd, 0), data_0, sender);
		subTelegrams.add(telegram0);

		this.data = telegram0;

		if (len > 4) {
			int extraTelegrams = (int) Math.floor((len - 4) / 8);
			for (int i = 0; i < extraTelegrams; i++) {
				byte[] telegram = generateTelegram(MESSAGE_SYS_EX,
						getIdx(rnd, i + 1),
						Utils.byteRange(payload, 4 + i * 8, 8), sender);
				subTelegrams.add(telegram);
				this.data = Utils.byteConcat(this.data, telegram);
			}
		}
		setDestinationId(destinationId);
	}

	public EnOceanRPC getRPC() {
		return new EnOceanRPC() {

			public void setSenderId(int chipId) {
			}

			public int getSenderId() {
				return MessageSYS_EX.this.getSenderId();
			}

			public byte[] getPayload() {
				return MessageSYS_EX.this.getPayloadBytes();
			}

			public String getName() {
				return null;
			}

			public int getManufacturerId() {
				return MessageSYS_EX.this.manuf;
			}

			public int getFunctionId() {
				return MessageSYS_EX.this.getFunc();
			}
		};
	}

	public boolean isTeachin() {
		return false;
	}

	public boolean hasTeachInInfo() {
		return false;
	}

	public int teachInFunc() {
		return 0;
	}

	public int teachInType() {
		return 0;
	}

	public int teachInManuf() {
		return 0;
	}

	public List<byte[]> getTelegrams() {
		return subTelegrams;
	}

	private byte[] generateTelegram(byte rorg, byte idx, byte[] data_0, int senderId) {
		byte[] pkt = Utils.byteConcat(rorg, idx);
		pkt = Utils.byteConcat(pkt, data_0);
		pkt = Utils.byteConcat(pkt, Utils.intTo4Bytes(senderId));
		// Default status for sending is 0, not repeated or STATUS_OK
		pkt = Utils.byteConcat(pkt, (byte) 0x00);
		// Assume the CRC for SYS_EX is on the data bytes
		pkt = Utils.byteConcat(pkt, Utils.crc8(data_0));
		return pkt;
	}

	private final byte getIdx(int seq, int i) {
		return (byte) ((seq << 6) | i & 0x3f);
	}

}
