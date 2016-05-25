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

package org.osgi.service.zigbee;

import java.io.EOFException;
import java.io.IOException;

/**
 * The {@code ZigBeeDataInput} interface is designed for converting a series of
 * bytes in java data types. The purpose of this interface is the same as the
 * DataInput interface that is in the java library, with the difference that in
 * this interface, byte ordering is little endian, whereas in the DataInput
 * interface is big endian.
 * 
 * Each method provided by this interface read one or more bytes from the
 * underlying stream, combine them, and return a java data type. The pointer to
 * the stream is then moved immediately after the last byte read. If this
 * pointer past the available buffer bounds, a subsequent call to one of these
 * methods will throw a {@link EOFException}.
 * 
 * @author $Id$
 */
public interface ZigBeeDataInput {

	/**
	 * Read a byte from the DataInput Stream.
	 * 
	 * @return the byte read from the data input.
	 * 
	 * @throws EOFException When the end of the input has been reached and there
	 *         are no more data to read.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	public byte readByte() throws IOException;

	/**
	 * Read an an integer of the specified {@code size}.
	 * 
	 * @param size the number of bytes that have to be read. Allowed values for
	 *        this parameter are in the range (1, 4]. If b1 is the first read
	 *        byte and b4 is the last (supposing that size is 4) then:
	 *        <p>
	 *        {@code int = (b1 & 0xff) | ((b2 & 0xff) << 8) | ((b3 & 0xff) << 16) |
	 *        ((b4 & 0xff) << 24) }
	 * 
	 * @return the integer read.
	 * 
	 * @throws EOFException When the end of the input has been reached and there
	 *         are no more data to read.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	public int readInt(int size) throws IOException;

	/**
	 * 
	 * Read a certain amount of bytes and returns a long.
	 * 
	 * @param size the number of bytes that have to be read. Allowed values for
	 *        this parameter are in the range (5, 8].
	 * 
	 * @return the long resulting from the bytes read.
	 * 
	 * @throws EOFException if there are not at least {@code size} bytes left on
	 *         the data input.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	public long readLong(int size) throws IOException;

	/**
	 * @param size expected value for this parameter are 2 or 4 depending if
	 *        reading {@link ZigBeeDataTypes#FLOATING_SEMI} or
	 *        {@link ZigBeeDataTypes#FLOATING_SINGLE}
	 * 
	 * @return a decoded float
	 * 
	 * @throws EOFException if there are not at least {@code size} bytes left on
	 *         the data input.
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 */
	public float readFloat(int size) throws IOException;

	/**
	 * @return a decoded double.
	 * 
	 * @throws EOFException if there are not at least {@code size} 8 bytes left
	 *         on the data input.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	public double readDouble() throws IOException;

	/**
	 * Read the specified amount of bytes from the underlying stream and return
	 * a copy of them. If the number of available bytes is less than the
	 * requested len, it throws an EOFException
	 * 
	 * @param len the number of bytes to read.
	 * 
	 * @return return a copy of the byte contained in the stream
	 * 
	 * @throws EOFException if there are not at least {@code len} bytes left on
	 *         the data input.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	public byte[] readBytes(int len) throws IOException;

}
