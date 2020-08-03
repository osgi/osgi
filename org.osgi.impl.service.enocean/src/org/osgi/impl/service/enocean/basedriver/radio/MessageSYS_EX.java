/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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

import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanRPC;

/**
 * SYS_EX message implementation.
 * 
 * @author $Id$
 */
public class MessageSYS_EX extends Message {
    
    static private final String TAG = "MessageSYS_EX";

	private List<byte[]>		subTelegrams	= new ArrayList<>();

	int							manuf;

    @Override
	public int getRorg() {
	return MESSAGE_SYS_EX;
    }

    @Override
	public int getSubTelNum() {
	return subTelegrams.size();
    }

    /**
     * @param data
     */
    public MessageSYS_EX(byte[] data) {
	super();

	Logger.i(TAG, "data: " + Utils.bytesToHexString(data));
	byte[] data_0 = Utils.byteRange(data, 2, 4);
	manuf = ((data_0[1] & 0x7f) << 4) + ((data_0[2] & 0xf0) >> 4);
	func = (byte) (((data_0[2] & 0x0ff) << 8) + data_0[3]);
	int len = (data_0[0] << 1) + ((data_0[1] & 0x80) >> 7);
	setSenderId(Utils.byteRange(data, 10, 4));
	if (len <= 4) {
	    setPayloadBytes(Utils.byteRange(data, 6, len));
	} else {
	    byte[] data_1 = Utils.byteRange(data, 6, 4);
	    int extraTelegrams = (int) Math.floor((len - 4) / 8) + 1;
	    for (int i = 0; i < extraTelegrams; i++) {
		data_1 = Utils.byteConcat(data_1,
			Utils.byteRange(data, 18 + i * 8, 8));
	    }
	    setPayloadBytes(Utils.byteRange(data_1, 0, len));
	}
	Logger.i(TAG, "manuf: " + manuf + " / func: " + func
		+ " / len: " + len + " / sender: " + getSenderId()
		+ " / payload: " + Utils.bytesToHexString(getPayloadBytes()));
    }

    /**
     * @param rpc
     * @param destinationId
     */
    public MessageSYS_EX(EnOceanRPC rpc, int destinationId) {
	this(rpc);
	setDestinationId(destinationId);
    }

    /**
     * @param rpc
     */
    public MessageSYS_EX(EnOceanRPC rpc) {
	int rnd = (int) (Math.random() * 3);
	byte[] payload = rpc.getPayload();
	if (payload == null) {
	    payload = new byte[] {};
	}
	// payload without manuf, function...
	int len = payload.length;
	manuf = rpc.getManufacturerId();
	int functionId = rpc.getFunctionId();
	setFunc(functionId);
	senderId = Utils.intTo4Bytes(rpc.getSenderId());

	// Generate first telegram
	byte[] data_0 = new byte[] {
		(byte) (len >> 1),
		(byte) (((len & 0x01) << 7) | ((manuf & 0x7f0) >> 4)),
		(byte) (((manuf & 0x0f) << 4) | ((functionId & 0xf00) >> 8)),
		(byte) (functionId & 0xff)
	};
	Logger.i(TAG, "data_0: " + Utils.bytesToHexString(data_0)
		+ " / payload: " + Utils.bytesToHexString(payload));
	byte[] data_1 = payload;
	data_1 = Utils.padUpTo(data_1, 4);
	data_0 = Utils.byteConcat(data_0, data_1);
	byte[] telegram0 = generateTelegram(Message.MESSAGE_SYS_EX,
		getIdx(rnd, 0), data_0, senderId);
	subTelegrams.add(telegram0);
	this.messageBytes = telegram0;

	if (len > 4) {
	    int extraTelegrams = (int) Math.floor((len - 4) / 8) + 1;
	    for (int i = 0; i < extraTelegrams; i++) {
		byte[] telegram = generateTelegram(Message.MESSAGE_SYS_EX,
			getIdx(rnd, i + 1),
			Utils.byteRangePadded(payload, 4 + i * 16, 8), senderId);
		subTelegrams.add(telegram);
		this.messageBytes = Utils.byteConcat(this.messageBytes,
			telegram);
	    }
	}
	Logger.i(TAG, "manuf: " + manuf + " / func: " + func
		+ " / len: " + len + " / sender: " + getSenderId()
		+ " / payload: " + Utils.bytesToHexString(getBytes()));
    }

    public EnOceanRPC getRPC() {
	return new EnOceanRPC() {

	    @Override
		public void setSenderId(int chipId) {
			// ignore
	    }

	    @Override
		public int getSenderId() {
		return MessageSYS_EX.this.getSenderId();
	    }

	    @Override
		public byte[] getPayload() {
		return MessageSYS_EX.this.getBytes();
	    }

	    @Override
		public String getName() {
		return null;
	    }

	    @Override
		public int getManufacturerId() {
		return MessageSYS_EX.this.manuf;
	    }

	    @Override
		public int getFunctionId() {
		return MessageSYS_EX.this.getFunc();
	    }
	};
    }

    @Override
	public boolean isTeachin() {
	return false;
    }

    @Override
	public boolean hasTeachInInfo() {
	return false;
    }

    @Override
	public int teachInFunc() {
	return 0;
    }

    @Override
	public int teachInType() {
	return 0;
    }

    @Override
	public int teachInManuf() {
	return 0;
    }

    @Override
	public List<byte[]> getTelegrams() {
	return subTelegrams;
    }

    private byte[] generateTelegram(byte rorg, byte idx, byte[] data_0, byte[] sender) {
	byte[] pkt = Utils.byteConcat(rorg, idx);
	pkt = Utils.byteConcat(pkt, data_0);
	pkt = Utils.byteConcat(pkt, sender);
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
