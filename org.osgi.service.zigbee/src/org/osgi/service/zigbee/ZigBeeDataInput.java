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

package org.osgi.service.zigbee;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;

/**
 * The {@code ZigBeeDataInput} interface is designed for converting a series of
 * bytes in java data types. The purpose of this interface is the same as the
 * {@link DataInput} interface available in the standard java library, with the
 * difference that in this interface, byte ordering is little endian, whereas in
 * the DataInput interface is big endian.
 * 
 * <p>
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
	 * Read an an integer of the specified {@code size}. The sign bit of the
	 * {@code size}-bytes integer is left-extended. In other words if a
	 * {@code readInt(2)} is issued and the byte read are 0x01, 0x02 and 0xf0,
	 * the method returns 0xfff00201. For this reason if the 4 bytes read from
	 * the stream represent an unsigned value, to get the expected value the and
	 * bitwise operator must be used:
	 * 
	 * <p>
	 * {@code int u = readInt(3) & 0xffffff;}
	 * 
	 * @param size the number of bytes that have to be read. Allowed values for
	 *        this parameter are in the range [1, 4].
	 *
	 * @return the integer read from the data input.
	 * 
	 * @throws EOFException When the end of the input has been reached and there
	 *         are no more data to read.
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException If the passed {@code size} is not in the
	 *         allowed range.
	 */
	public int readInt(int size) throws IOException;

	/**
	 * 
	 * Read a certain amount of bytes and returns a long. The sign bit of the
	 * read {@code size}-bytes long is left-extended. In other words if a
	 * readLong(2) is issued and the byte read are 0x01 and 0xf0, the method
	 * returns 0xfffffffffffff001L. For this reason if the 2 bytes read from the
	 * stream represent an unsigned value, to get the expected value the and
	 * bitwise operator must be used:
	 * 
	 * <p>
	 * {@code long u = readLong(2) & 0xffff;}
	 * 
	 * @param size the number of bytes that have to be read. Allowed values for
	 *        this parameter are in the range [1, 8].
	 * 
	 * @return The {@code long} value read from the data input.
	 * 
	 * @throws EOFException if there are not at least {@code size} bytes left on
	 *         the data input.
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException If the passed {@code size} is not in the
	 *         allowed range.
	 */
	public long readLong(int size) throws IOException;

	/**
	 * @param size expected value for this parameter are 2 or 4 depending if
	 *        reading {@link ZigBeeDataTypes#FLOATING_SEMI} or
	 *        {@link ZigBeeDataTypes#FLOATING_SINGLE}
	 * 
	 * @return The {@code float} number read from the data input.
	 * 
	 * @throws EOFException if there are not at least {@code size} bytes left on
	 *         the data input.
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException If the passed {@code size} is not in the
	 *         allowed range.
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
