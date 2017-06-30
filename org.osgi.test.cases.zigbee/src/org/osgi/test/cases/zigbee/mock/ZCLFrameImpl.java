/*
 * Copyright (c) OSGi Alliance (2016, 2017). All Rights Reserved.
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

package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;

/**
 * This is a mock implementation of the ZCLFrame interface. This implementation
 * is far from a real one, and it is provided to allow to test the test cases of
 * the ZigBee Device Service. It cannot be complete for ZigBee spec licensing
 * problems.
 * 
 * @author $Id$
 */
public class ZCLFrameImpl extends ZigBeeSerializer implements ZCLFrame {

	/**
	 * The minimum header size of the ZCL command frame. The value is given by
	 * the configuration file.
	 */
	public static int	minHeaderSize;

	/**
	 * The maximum header size (when the frame is manufacturer specific) of the
	 * ZCL command frame. The value is given by the configuration file.
	 */
	public static int	maxHeaderSize;

	/**
	 * Stores the ZCL Header
	 */
	protected ZCLHeader	zclHeader	= null;

	/**
	 * Basic constructor. It creates a default ZCL Frame with the passed
	 * commandId, with a maxPayloadSize of 30 bytes.
	 * 
	 * @param commandId The command identifier of the ZCL Frame.
	 */
	public ZCLFrameImpl(short commandId) {
		super();
		this.zclHeader = new ZCLHeaderImpl(commandId, false, true, false, (byte) 0);
	}

	/**
	 * Basic constructor. It creates a ZCLFrame with the passed ZCLHeader and
	 * with a maxPayloadZise of 30 bytes.
	 * 
	 * @param header a ZCLHeader instance
	 */
	public ZCLFrameImpl(ZCLHeader header) {
		super();
		this.zclHeader = header;
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
		super(payload);
		this.zclHeader = header;
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
		super(maxPayloadSize);
		this.zclHeader = header;
	}

	public ZCLHeader getHeader() {
		return zclHeader;
	}

	/**
	 * Returns the ZCL Frame + ZCL payload in a byte array. In this mock
	 * implementation of the ZCLFrame the ZCL Header is not copied in the
	 * returned byte array (@see ZCLFrameImpl} and left empty.
	 */

	public byte[] getBytes() {
		int headerSize = this.getHeaderSize();
		byte[] d = new byte[headerSize + index];
		System.arraycopy(data, 0, d, headerSize, index);
		return d;
	}

	/**
	 * Please note that in this fake implementation we copy only the ZCL frame
	 * payload and not the ZCL frame header.
	 */

	public int getBytes(byte[] buffer) {
		int headerSize = this.getHeaderSize();
		System.arraycopy(data, 0, buffer, headerSize, index);
		return index + headerSize;
	}

	public int getSize() {
		return index + getHeaderSize();
	}

	private int getHeaderSize() {
		return zclHeader.isManufacturerSpecific() ? (maxHeaderSize) : minHeaderSize;
	}
}
