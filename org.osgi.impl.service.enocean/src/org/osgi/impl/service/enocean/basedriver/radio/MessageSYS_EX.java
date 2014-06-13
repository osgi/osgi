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
import org.osgi.service.enocean.EnOceanRPC;

/**
 * SYS_EX message implementation.
 * 
 * @author Victor Perron <victor.perron@orange.fr>
 */
public class MessageSYS_EX extends Message {

	private int		subTelNum;
	private List	subTelegrams	= new ArrayList();

	public int getRorg() {
		return MESSAGE_SYS_EX;
	}

	public int getSubTelNum() {
		return subTelNum;
	}

	/**
	 * @param data
	 */
	public MessageSYS_EX(byte[] data) {
		super(data);
	}

	/**
	 * @param rpc
	 */
	public MessageSYS_EX(EnOceanRPC rpc) {
		int seq = getRandomSeq();
		byte[] payload = rpc.getPayload();
		if (payload == null) {
			payload = new byte[] {};
		}
		// payload without manuf, function...
		int len = payload.length;
		int manuf = rpc.getManufacturerId();
		int func = rpc.getFunctionId();

		// Generate first telegram
		byte[] data_0 = new byte[] {
				(byte) (len >> 1),
				(byte) (((len & 0x01) << 7) | ((manuf & 0x7f0) >> 4)),
				(byte) (((manuf & 0x0f) << 4) | ((func & 0xf00) >> 8)),
				(byte) (func & 0xff)
		};
		byte[] data_1 = Utils.byteRange(payload, 0, len);
		data_1 = Utils.padUpTo(data_1, 4);
		data_0 = Utils.byteConcat(data_0, data_1);
		byte[] telegram0 = generateTelegram(
				Message.MESSAGE_SYS_EX,
				getIdx(seq, 0),
				data_0,
				rpc.getSenderId()
				);
		subTelegrams.add(telegram0);

		if (len > 4) {
			int extraTelegrams = (int) Math.floor((len - 4) / 8);
			for (int i = 0; i < extraTelegrams; i++) {
				byte[] telegram = generateTelegram(
						Message.MESSAGE_SYS_EX,
						getIdx(seq, i + 1),
						Utils.byteRange(payload, 4 + i * 8, 8),
						rpc.getSenderId()
						);
				subTelegrams.add(telegram);
			}
			subTelNum = extraTelegrams + 1;
		} else {
			subTelNum = 1;
		}
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

	public List getTelegrams() {
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

	private int getRandomSeq() {
		return (int) (Math.random() * 3);
	}

	private byte getIdx(int seq, int i) {
		return (byte) ((seq << 6) | i & 0x3f);
	}
}
