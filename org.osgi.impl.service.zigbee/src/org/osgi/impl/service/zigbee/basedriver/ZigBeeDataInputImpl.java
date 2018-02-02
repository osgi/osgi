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

import java.io.EOFException;
import org.osgi.service.zigbee.ZigBeeDataInput;

/**
 * A mock implementation of {@link ZigBeeDataInput} interface.
 * 
 * @author $Id$
 * 
 */
public class ZigBeeDataInputImpl implements ZigBeeDataInput {

	private ZigBeeSerializer	frame;

	private int					i	= 0;

	/**
	 * @param frame
	 */
	public ZigBeeDataInputImpl(ZigBeeSerializer frame) {
		this.frame = frame;
	}

	public byte readByte() throws EOFException {
		return frame.readByte(i++);
	}

	public int readInt(int size) throws EOFException {
		int v = frame.readInt(i, size);
		i += size;
		return v;
	}

	public long readLong(int size) throws EOFException {
		long v = frame.readLong(i, size);
		i += size;
		return v;
	}

	public float readFloat(int size) throws EOFException {
		float v = frame.readFloat(i, size);
		i += size;
		return v;
	}

	public byte[] readBytes(int len) throws EOFException {
		byte[] v = frame.readBytes(i, len);
		i += len;
		return v;
	}

	public double readDouble() throws EOFException {
		double v = frame.readDouble(i);
		i += 8;
		return v;
	}
}
