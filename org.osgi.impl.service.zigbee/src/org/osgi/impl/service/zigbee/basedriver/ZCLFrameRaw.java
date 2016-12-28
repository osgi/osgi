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

package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;

/**
 * ZCLFrame implementation class that may be initialized with a specific raw
 * frame (the ZCLFramImpl class accepts only the ZCL frame payload and not a
 * full frame). This implementation of the ZCLFrame interface ensures that the
 * getBytes() returns the raw frame used in the constructor, but also that it is
 * possible to unmashal it using the ZCLFrame ZCLFrame.getDataOutput() and its
 * returned ZigBeeDataOutput stream.
 * 
 * @author $id$
 *
 */
public class ZCLFrameRaw extends ZCLFrameImpl implements ZCLFrame {

	private byte[] frame;

	public ZCLFrameRaw(ZCLHeader header, byte[] rawFrame) {
		super(header);

		if (rawFrame == null || header == null) {
			throw new NullPointerException("ZCLFrameRaw constructor requires not null arguments");
		}
		this.frame = rawFrame;

		/* calculate the size in bytes of the ZCL header */
		int size = header.isManufacturerSpecific() ? (maxHeaderSize) : minHeaderSize;
		data = new byte[rawFrame.length - size];

		System.arraycopy(rawFrame, size, data, 0, rawFrame.length - size);
		/* data contains only the payload */
	}

	/**
	 * Over
	 */

	public byte[] getBytes() {
		return (byte[]) frame.clone();
	}

	private final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public String toString() {
		String s = "[ ";
		for (int i = 0; i < frame.length; i++) {
			int value = (frame[i] >>> 4) & 0x0f;
			s = s + hexDigits[value];

			value = frame[i] & 0x0f;
			s = s + hexDigits[value] + " ";

		}

		return s + "]";
	}

}
