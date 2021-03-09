/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.tr069todmt.encode;

/**
 * Base64 is an utility class for encoding/decoding binary data to ASCII Base 64
 * encoding and vice-versa.
 */
public class Base64 {

	private final static byte	MASK	= 0x3f;
	private final static byte	PADDING	= (byte) '=';

	/**
	 * Auxiliary array containing int values of chars.
	 */
	private static final byte	CODES[]	= new byte[] {
										65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85,
										86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
										110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51,
										52, 53, 54, 55, 56, 57, 43, 47
										};

	/**
	 * Encodes binary data to Base 64 ASCII string. Result is with 76 symbols
	 * per line.
	 * 
	 * @param data byte array data for encoding.
	 * @return encoded data.
	 */
	public static byte[] encode(byte[] data) {
		return encode(data, 0, data.length);
	}

	/**
	 * Encodes binary data to Base 64 ASCII string. The function can break the
	 * result into lines with specified numbers of characters.
	 * 
	 * @param data byte array data for encoding.
	 * @param off1 starting offset in the array
	 * @param length the number of bytes to encode
	 * @return encoded data.
	 */
	public static byte[] encode(byte[] data, int off1, int length) {
		int atomLen = length == 0 ? 0 : (length - 1) / 3 + 1;
		byte[] b_res = new byte[atomLen * 4 + 2];
		for (int i = 0; i < off1 + length / 3; i++) {
			encodeAtom(data, off1 + i * 3, b_res, i * 3);
		}
		// if there are left some bytes
		int left = length % 3;
		int off = length - left;
		if (left != 0) {
			b_res[b_res.length - 6] = CODES[(data[off] >>> 2) & MASK];
			if (left == 1) {
				b_res[b_res.length - 5] = CODES[(data[off] << 4) & 0x30];
				b_res[b_res.length - 4] = PADDING;
			} else {
				b_res[b_res.length - 5] = CODES[((data[off] << 4) & 0x30)
						+ ((data[off + 1] >>> 4) & 0x0f)];
				b_res[b_res.length - 4] = CODES[((data[off + 1]) & 0x0f) << 2];
			}
			b_res[b_res.length - 3] = PADDING;
		}

		byte[] resWithoutLineBreaks = new byte[b_res.length - 2];
		System.arraycopy(b_res, 0, resWithoutLineBreaks, 0, resWithoutLineBreaks.length);
		return resWithoutLineBreaks;
	}

	private static final void encodeAtom(byte[] b_in, int pos1, byte[] b_out, int pos2) {
		int temp = b_in[pos1] & 0xff;
		temp <<= 8;
		temp |= b_in[pos1 + 1] & 0xff;
		temp <<= 8;
		temp |= b_in[pos1 + 2] & 0xff;

		int local = (pos2 / 3) << 2;
		b_out[local++] = CODES[(temp >> 18) & MASK];
		b_out[local++] = CODES[(temp >> 12) & MASK];
		b_out[local++] = CODES[(temp >> 6) & MASK];
		b_out[local] = CODES[temp & MASK];
	}

	/**
	 * Decodes Base64 encoded ASCII data to binary form.
	 * 
	 * @param data the base64-encoded data.
	 * @return decoded data.
	 * @throws Exception when data is not in correct Base64 format.
	 * @see #decode(byte[], int, int)
	 */
	public static byte[] decode(byte[] data) throws Exception {
		return decode(data, 0, data.length);
	}

	/**
	 * Decodes Base64 encoded ASCII data to binary form.
	 * 
	 * @param data the base64-encoded data.
	 * @param off the index in the array from which to start decoding
	 * @param length the number of bytes to decode
	 * @return decoded data.
	 * @throws Exception when data is not in correct Base64 format.
	 */
	public static byte[] decode(byte[] data, int off, int length) throws Exception {
		byte[] b_res = null;
		data = readEncoded(data, off, length);
		if ((data.length & 0x03) != 0) {
			throw new Exception("Decode exception: input array size must be multiple by 4");
		}

		int len = data.length;
		int atomLen = len >>> 2;
		int padding = 0;

		if (data[len - 1] == PADDING)
			padding++; // 1 or 2 chars
		if (data[len - 2] == PADDING)
			padding++; // 1 char, otherwise - 2
		if (data[len - 3] == PADDING) {
			throw new Exception("Decode exception: invalid input data");
		}
		b_res = new byte[atomLen * 3 - padding];
		for (int i = 0, j = 0; i < data.length - 4;) {
			decodeAtom(b_res, data, i, j);
			i += 4;
			j += 3;
		}

		/* if there are more symbols */
		if (padding != 0) {
			int tmp = 0;
			tmp |= getIndex(data[len - 4]);
			if (padding == 2) {
				tmp <<= 2;
				tmp |= (getIndex(data[len - 3]) >>> 4);
			} else /* 1 more byte do decode */{
				tmp <<= 6;
				tmp |= getIndex(data[len - 3]);
				tmp <<= 4;
				tmp |= (getIndex(data[len - 2]) >>> 2);
				b_res[b_res.length - 2] = (byte) ((tmp >> 8) & 0xff);
			}
			b_res[b_res.length - 1] = (byte) (tmp & 0xff);
		} else {
			decodeAtom(b_res, data, data.length - 4, b_res.length - 3);
		}
		return b_res;
	}

	private static final void decodeAtom(byte[] b_out, byte[] b_in, int off1, int off2) throws Exception {
		if ((off1 < 0) || (b_in.length <= off1))
			throw new Exception("decodeAtom exception: invalid offset");

		int tmp = 0;
		for (int i = 0; i < 4; i++) {
			tmp <<= 6;
			tmp |= (0xff & getIndex(b_in[i + off1]));
		}

		b_out[off2++] = (byte) ((tmp >> 16) & 0xff);
		b_out[off2++] = (byte) ((tmp >> 8) & 0xff);
		b_out[off2] = (byte) (tmp & 0xff);
	}

	private static final int getIndex(byte b) throws Exception {
		// 'A' - 0
		// 'a' - 26
		// '0' - 52
		// '+' - 62
		// '/' - 63
		if ((b > 64) && (b < 91))
			return b - 65;
		if ((b > 96) && (b < 123))
			return b - 71;
		if ((b > 47) && (b < 58))
			return b + 4;
		if (b == 43)
			return 62;
		if (b == 47)
			return 63;
		throw new Exception("decodeAtom exception: unknown symbol");
	}

	private static final byte[] readEncoded(byte[] in, int start, int length)
			throws Exception {
		byte[] out = new byte[length];
		int off = 0;
		for (int i = start; i < start + length; i++) {
			if ((in[i] >= '0' && in[i] <= '9') ||
					(in[i] >= 'A' && in[i] <= 'Z') ||
					(in[i] >= 'a' && in[i] <= 'z') ||
					(in[i] == '/') ||
					(in[i] == '+') ||
					(in[i] == '=')) {
				out[off++] = in[i];
			}
		}
		byte[] b_res = new byte[off];
		System.arraycopy(out, 0, b_res, 0, off);
		return b_res;
	}
}
