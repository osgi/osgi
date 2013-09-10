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


package org.osgi.test.cases.enocean.packets.serial;

import org.osgi.test.cases.enocean.Utils;
import org.osgi.test.cases.enocean.packets.ByteSerializable;

public abstract class SerialPacket {

	private Header	header;
	private ByteSerializable	data;
	private ByteSerializable	optional;

	public SerialPacket() {
		header = new Header();
	}

	public byte[] serialize() {
		byte[] dataBytes = data.serialize();
		header.setDataLength(dataBytes.length);
		dataBytes = Utils.byteConcat(dataBytes, optional.serialize());
		byte[] crc = Utils.byteToBytes(Utils.crc8(dataBytes));
		dataBytes = Utils.byteConcat(dataBytes, crc);
		return Utils.byteConcat(header.serialize(), dataBytes);
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public ByteSerializable getData() {
		return data;
	}

	public void setData(ByteSerializable embedded) {
		this.data = embedded;
	}

	public ByteSerializable getOptional() {
		return optional;
	}

	public void setOptional(ByteSerializable optional) {
		this.optional = optional;
	}
}
