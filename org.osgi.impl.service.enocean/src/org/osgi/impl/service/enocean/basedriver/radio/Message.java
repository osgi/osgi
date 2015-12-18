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

import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanMessage;

/**
 * EnOcean message implementation.
 */
public abstract class Message implements EnOceanMessage {

    /** MESSAGE_4BS */
    public static final byte	MESSAGE_4BS	= (byte) 0xA5;
    /** MESSAGE_RPS */
    public static final byte	MESSAGE_RPS	= (byte) 0xF6;
    /** MESSAGE_1BS */
    public static final byte	MESSAGE_1BS	= (byte) 0xD5;
    /** MESSAGE_VLD */
    public static final byte	MESSAGE_VLD	= (byte) 0xD2;
    /** MESSAGE_SYS_EX */
    public static final byte	MESSAGE_SYS_EX	= (byte) 0xC5;

    protected byte RORG;
    protected byte[] data;
    protected byte[] senderId;
    protected byte status;
    protected byte subTelNum;
    protected int destinationId;
    protected byte dbm;
    protected byte securityLevel;
    protected byte func;
    protected byte type;

    protected byte[] messageBytes;

    /**
     * 
     */
    public Message() {
    }

    /**
     * @param data
     */
    public Message(byte[] data) {
	this.messageBytes = data;
	setRORG(data[0]);
	setPayloadBytes(Utils.byteRange(data, 1, data.length - 6 - 7));
	setSenderId(Utils.byteRange(data, data.length - 5 - 7, 4));
	setStatus(data[data.length - 1 - 7]);
	setSubTelNum(data[data.length - 7]);
	// setDestinationId(Utils.byteRange(data, data.length - 6, 4));
	setDestinationId(Utils.bytes2intLE(data, data.length - 6, 4));
	setDbm(data[data.length - 2]);
	setSecurityLevel(data[data.length - 1]);
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

    /**
     * @param rorg
     */
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
     * 
     * @param senderId
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

    /**
     * @param status
     */
    public void setStatus(int status) {
	this.status = (byte) (status & 0xff);
    }

    public int getSubTelNum() {
	return subTelNum;
    }

    /**
     * @param subTelNum
     */
    public void setSubTelNum(byte subTelNum) {
	this.subTelNum = subTelNum;
    }

    public int getDestinationId() {
	// return Utils.bytes2intLE(destinationId, 0, 4);
	return destinationId;
    }

    /**
     * @param destinationId
     */
    public void setDestinationId(int destinationId) {
	this.destinationId = destinationId;
    }

    public int getDbm() {
	return dbm;
    }

    /**
     * @param dbm
     */
    public void setDbm(byte dbm) {
	this.dbm = dbm;
    }

    public int getSecurityLevelFormat() {
	return securityLevel;
    }

    /**
     * @param securityLevel
     */
    public void setSecurityLevel(byte securityLevel) {
	this.securityLevel = securityLevel;
    }

    /**
     * @param func
     */
    public void setFunc(int func) {
	this.func = (byte) func;
    }

    public int getFunc() {
	return func;
    }

    /**
     * @param type
     */
    public void setType(int type) {
	this.type = (byte) type;
    }

    public int getType() {
	return type;
    }

    public byte[] getPayloadBytes() {
	return data;
    }

    protected void setPayloadBytes(byte[] data) {
	this.data = data;
    }

    protected byte getPayloadByte(int idx) {
	return data[idx];
    }

    /**
     * @return telegram(s)
     */
    public List getTelegrams() {
	List list = new ArrayList();
	list.add(getBytes());
	return list;
    }

    /**
     * @return isTeachin.
     */
    public abstract boolean isTeachin();

    /**
     * @return hasTeachInInfo.
     */
    public abstract boolean hasTeachInInfo();

    /**
     * @return teachInFunc.
     */
    public abstract int teachInFunc();

    /**
     * @return teachInType.
     */
    public abstract int teachInType();

    /**
     * @return teachInManuf.
     */
    public abstract int teachInManuf();
}
