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

import java.io.IOException;

/**
 * The {@code ZigBeeDataOutput} interface is designed for converting java data
 * types into a series of bytes. The purpose of this interface is the same as
 * the DataOutput interface provided by java, with the difference that in this
 * interface, the generated bytes ordering is little endian, whereas in the
 * DataOutput is big endian.
 * 
 * @author $Id$
 */
public interface ZigBeeDataOutput {

	/**
	 * Appends a byte to the data output.
	 * 
	 * <p>
	 * To avoid losing information, the passed value must be in the range [-128,
	 * 127] for signed numbers and [0, 255] for unsigned numbers.
	 * 
	 * @param value The value to append
	 */
	public void writeByte(byte value);

	/**
	 * Appends an int value to the data output.
	 * 
	 * <p>
	 * To avoid losing information, according to the {@code size} argument, the
	 * passed {@code long} value if it represents a signed number must fit in
	 * the range [ -2^({@code size} * 8 - 1), -2^({@code size} * 8 - 1) - 1].
	 * 
	 * <p>
	 * For unsigned numbers it should fit in the range [ 0, -2^({@code size} *
	 * 8) - 1].
	 * 
	 * <p>
	 * For instance if {@code size} is 2 the correct range for signed numbers is
	 * [0xffff8000, 0x7fff] (i.e [-32768, +32767]), whereas for unsigned numbers
	 * is [0L, 0xffff].
	 * 
	 * <p>
	 * Although this method allows write even 1 byte of the passed {@code int}
	 * value, it is suggested to use the {@linkplain #writeByte(byte)} because
	 * this latter could be implemented in a more efficient way.
	 * 
	 * @param value The integer value to append
	 * @param size The size in bytes that have to be actually appended. The size
	 *        must be in the range [1,4].
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException If the passed {@code size} is not within
	 *         the allowed range.
	 */
	public void writeInt(int value, int size) throws IOException;

	/**
	 * Appends a long to to the data output.
	 * 
	 * <p>
	 * To avoid losing information, according to the {@code size} argument, the
	 * passed {@code long} value if it represents a signed number must fit in
	 * the range [ -2^({@code size} * 8 - 1), -2^({@code size} * 8 - 1) - 1].
	 * 
	 * <p>
	 * For unsigned numbers it should fit in the range [ 0, -2^({@code size} *
	 * 8) - 1].
	 * 
	 * <p>
	 * For instance if {@code size} is 3 the correct range for signed numbers is
	 * [0xffffffffff800000L, 0x7fffffL] (i.e [-21474836448, +2147483647]),
	 * whereas for unsigned numbers is [0L, 0xffffffL].
	 * 
	 * <p>
	 * Although this method allows write even 1 byte of the passed {@code long}
	 * value, it is suggested to use the {@linkplain #writeByte(byte)} because
	 * this latter could be implemented in a more efficient way.
	 * 
	 * @param value The {@code long} value to append
	 * @param size The size in bytes that have to be actually appended. The size
	 *        must be in the range [1,8].
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException If the passed {@code size} is not within
	 *         the allowed range.
	 */
	public void writeLong(long value, int size) throws IOException;

	/**
	 * Appends on the Data Output Stream a float value
	 * 
	 * @param value The {@code float} value to append.
	 * @param size The size in bytes that have to be actually appended. The size
	 *        must be 2 for semi precision floats or 4 for standard precision
	 *        floats (see the ZigBee Cluster Library specifications).
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException If the passed {@code size} is not within
	 *         the allowed range.
	 */
	public void writeFloat(float value, int size) throws IOException;

	/**
	 * Appends on the Data Output Stream a {@code double} value.
	 * 
	 * @param value The {@code double} value to append.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	public void writeDouble(double value) throws IOException;

	/**
	 * Appends on the Data Output Stream a byte array.
	 * 
	 * The byte array is written on the data output starting from the byte at
	 * index 0.
	 * 
	 * @param bytes A buffer containing the bytes to append to the data output
	 *        stream.
	 * @param length The length in bytes that have to be actually appended.
	 * 
	 * @throws IOException If an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException If the passed buffer is null or shorter
	 *         than {@code length} bytes.
	 */
	public void writeBytes(byte[] bytes, int length) throws IOException;

}