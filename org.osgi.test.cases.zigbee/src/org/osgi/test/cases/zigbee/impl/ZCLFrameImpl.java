/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.test.cases.zigbee.impl;

import java.io.EOFException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;

/**
 * This is a mock implementation of the ZCLFrame interface. This implementation
 * is far from a real one, and it is provided to allow to test the test cases of
 * the ZigBee Device Service. It cannot be complete for ZigBee spec licensing
 * problems.
 * 
 * @author $Id: dac1f801159e835fbc02367d721172a588649289 $
 */
public class ZCLFrameImpl implements ZCLFrame {

	// The minimum header size of the ZCL command frame. The value is given by
	// the configuration file.
	public static int minHeaderSize;

	/**
	 * The buffer used to store the ZCLFrame payload.
	 */
	protected byte[] data = null;

	protected boolean isEmpty = true;

	int index = 0;

	protected ZCLHeader zclHeader = null;

	/**
	 * Basic constructor. It creates a default ZCL Frame with the passed
	 * commandId, with a maxPayloadSize of 30 bytes.
	 * 
	 * @param commandId The command identifier of the ZCL Frame.
	 */
	public ZCLFrameImpl(int commandId) {
		this.zclHeader = new ZCLHeaderImpl(commandId, false, true, false, (byte) 0);
		this.data = new byte[30];
		this.isEmpty = true;
	}

	/**
	 * Basic constructor. It creates a ZCLFrame with the passed ZCLHeader and
	 * with a maxPayloadZise of 30 bytes.
	 * 
	 * @param header a ZCLHeader instance
	 */
	public ZCLFrameImpl(ZCLHeader header) {
		this.zclHeader = header;
		this.data = new byte[30];
		this.isEmpty = true;
	}

	/**
	 * Creates a ZCLFrame and initalize it with the passd ZCLHeader and ZCL
	 * payload.
	 * 
	 * @param header The ZCLHeader
	 * 
	 * @param payload The ZCLFrame payload.
	 */

	public ZCLFrameImpl(ZCLHeader header, byte[] payload) {
		this.zclHeader = header;
		this.data = payload;
		this.isEmpty = false;
	}

	/**
	 * Creates a ZCLFrame and initialize it with the passed payload. Create a
	 * default ZCLHeader.
	 * 
	 * Using this constructor is discuraged since it is not possible to set the
	 * ZCLFrame sequence number
	 * 
	 * @param commandId The commandId to put in the header.
	 * 
	 * @param payload The ZCLFrame payload.
	 * 
	 * @deprecated
	 */

	public ZCLFrameImpl(int commandId, byte[] payload) {
		this.zclHeader = new ZCLHeaderImpl(commandId, false, true, false, (byte) 0);
		this.data = payload;
		this.isEmpty = false;
	}

	/**
	 * Creates
	 * 
	 * @param header An instance of ZCLHeader interface
	 * 
	 * @param maxPayloadSize The maximum size of the internal buffer used to
	 *        store the ZCL Frame payload
	 */
	public ZCLFrameImpl(ZCLHeader header, int maxPayloadSize) {
		this.zclHeader = header;
		this.data = new byte[maxPayloadSize];
	}

	public ZCLHeader getHeader() {
		return zclHeader;
	}

	/**
	 * Returns the ZCL Frame + ZCL Payload in a byte array. In this mock
	 * implementation of the ZCLFrame the ZCL Header is not copied in the
	 * returned byte array (@see ZCLFrameImpl}
	 */

	public byte[] getBytes() {
		int size = zclHeader.isManufacturerSpecific() ? (minHeaderSize + 2) : minHeaderSize;

		byte[] d = new byte[size + index];
		System.arraycopy(data, 0, d, size, index);
		return d;
	}

	public ZigBeeDataInput getDataInput() {
		return new ZigBeeDataInputImpl(this);
	}

	/**
	 * Returns an instance of ZigBeeDataOutput stream.
	 * 
	 * @return zigBeeDataOutput
	 */
	public ZigBeeDataOutput getDataOutput() {
		return new ZigBeeDataOutputImpl(this);
	}

	public byte readByte(int pos) throws EOFException {
		try {
			return data[pos];
		} catch (IndexOutOfBoundsException e) {
			throw new EOFException();
		}
	}

	public int readInt(int pos, int size) throws EOFException {
		if (size <= 4 && size != 0) {
			return (int) readLong(pos, size);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public long readLong(int pos, int size) throws EOFException {
		if (size <= 8 && size != 0) {
			try {
				long l = 0;
				pos += size;
				for (int i = 0; i < size; i++) {
					short s = (short) (data[--pos] & 0xFF);
					l = (l <<= 8) | s;
				}
				pos += size;
				return l;
			} catch (IndexOutOfBoundsException e) {
				throw new EOFException();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public float readFloat(int pos, int size) throws EOFException {
		if (size == 4) {
			int raw = readInt(pos, 4);
			return Float.intBitsToFloat(raw);
		} else if (size == 2) {
			// FIXME: implement 2 bytes float
			throw new RuntimeException("Not yet implemented: Float semi");
		} else {
			throw new IllegalArgumentException("invalid size");
		}
	}

	public double readDouble(int pos) throws EOFException {
		long raw = readLong(pos, 8);
		return Double.longBitsToDouble(raw);
	}

	public byte[] readBytes(int pos, int len) throws EOFException {
		if (pos + len > this.data.length) {
			throw new EOFException();
		}
		byte[] bytes = new byte[len];
		System.arraycopy(data, pos, bytes, 0, len);
		pos += len;
		return bytes;
	}

	public void writeByte(byte value) {
		data[index++] = value;
	}

	public void writeInt(int value, int size) {
		if (size == 0 || size > 4) {
			throw new IllegalArgumentException();
		}
		this.writeLong(value, size);
	}

	public void writeLong(long value, int size) {
		if (size == 0 || size > 8) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < size; i++) {
			data[index++] = (byte) (value & 0xFF);
			value >>= 8;
		}
	}

	public void writeFloat(float value, int size) {
		if (size == 4) {
			int raw = Float.floatToRawIntBits(value);
			this.writeInt(raw, 4);
		} else if (size == 2) {
			// FIXME: implement 2 bytes float
			throw new RuntimeException("Not yet implemented: Float semi");
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void writeDouble(double value) {
		long raw = Double.doubleToRawLongBits(value);
		this.writeLong(raw, 8);
	}

	public void writeBytes(byte[] bytes, int length) {
		if ((bytes == null) || (length == 0)) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < length; i++) {
			this.writeByte(bytes[i]);
		}
	}
}
