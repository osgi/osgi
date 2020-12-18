/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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

import java.security.InvalidParameterException;
import org.osgi.service.zigbee.ZigBeeDataOutput;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZigBeeDataOutputImpl implements ZigBeeDataOutput {

	private ZigBeeSerializer frame;

	/**
	 * @param zclFrameImpl
	 */
	public ZigBeeDataOutputImpl(ZigBeeSerializer zclFrameImpl) {
		this.frame = zclFrameImpl;
	}

	@Override
	public void writeByte(byte value) {
		frame.writeByte(value);
	}

	@Override
	public void writeInt(int value, int size) {
		if (size <= 4) {
			frame.writeInt(value, size);
		} else
			throw new InvalidParameterException();
	}

	@Override
	public void writeLong(long value, int size) {
		frame.writeLong(value, size);
	}

	@Override
	public void writeFloat(float value, int size) {
		frame.writeFloat(value, size);
	}

	@Override
	public void writeDouble(double value) {
		frame.writeDouble(value);
	}

	@Override
	public void writeBytes(byte[] bytes, int length) {
		frame.writeBytes(bytes, length);
	}
}
