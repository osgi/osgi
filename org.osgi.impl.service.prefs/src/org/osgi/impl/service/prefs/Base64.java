/*
 * @(#)Base64.java	1.3 01/07/18
 * $Id$
 *
 
 * 
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 * 
 */
package org.osgi.impl.service.prefs;

/**
 * Static methods for translating Base64 encoded strings to byte arrays and
 * vice-versa.
 * 
 * @version $Id$
 */
class Base64 {
	/**
	 * Translates the specified byte array into a Base64 string as per
	 * Preferences.put(byte[]).
	 */
	static String byteArrayToBase64(byte[] a) {
		int aLen = a.length;
		int numFullGroups = aLen / 3;
		int numBytesInPartialGroup = aLen - 3 * numFullGroups;
		int resultLen = 4 * ((aLen + 2) / 3);
		StringBuffer result = new StringBuffer(resultLen);
		// Translate all full groups from byte array elements to Base64
		int inCursor = 0;
		for (int i = 0; i < numFullGroups; i++) {
			int byte0 = a[inCursor++] & 0xff;
			int byte1 = a[inCursor++] & 0xff;
			int byte2 = a[inCursor++] & 0xff;
			result.append(intToBase64[byte0 >> 2]);
			result.append(intToBase64[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
			result.append(intToBase64[(byte1 << 2) & 0x3f | (byte2 >> 6)]);
			result.append(intToBase64[byte2 & 0x3f]);
		}
		// Translate partial group if present
		if (numBytesInPartialGroup != 0) {
			int byte0 = a[inCursor++] & 0xff;
			result.append(intToBase64[byte0 >> 2]);
			if (numBytesInPartialGroup == 1) {
				result.append(intToBase64[(byte0 << 4) & 0x3f]);
				result.append("==");
			}
			else {
				// assert numBytesInPartialGroup == 2;
				int byte1 = a[inCursor++] & 0xff;
				result.append(intToBase64[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
				result.append(intToBase64[(byte1 << 2) & 0x3f]);
				result.append('=');
			}
		}
		// assert inCursor == a.length;
		// assert result.length() == resultLen;
		return result.toString();
	}

	/**
	 * This array is a lookup table that translates 6-bit positive integer index
	 * values into their "Base64 Alphabet" equivalents as specified in Table 1
	 * of RFC 2045.
	 */
	private static final char	intToBase64[]	= {'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '+', '/'	};

	/**
	 * Translates the specified Base64 string (as per Preferences.get(byte[]))
	 * into a byte array.
	 * 
	 * @throw IllegalArgumentException if <tt>s</tt> is not a valid Base64
	 *        string.
	 */
	static byte[] base64ToByteArray(String s) {
		int sLen = s.length();
		int numGroups = sLen / 4;
		if (4 * numGroups != sLen)
			throw new IllegalArgumentException(
					"String length must be a multiple of four.");
		int missingBytesInLastGroup = 0;
		int numFullGroups = numGroups;
		if (sLen != 0) {
			if (s.charAt(sLen - 1) == '=') {
				missingBytesInLastGroup++;
				numFullGroups--;
			}
			if (s.charAt(sLen - 2) == '=')
				missingBytesInLastGroup++;
		}
		byte[] result = new byte[3 * numGroups - missingBytesInLastGroup];
		// Translate all full groups from base64 to byte array elements
		int inCursor = 0, outCursor = 0;
		for (int i = 0; i < numFullGroups; i++) {
			int ch0 = base64toInt(s.charAt(inCursor++));
			int ch1 = base64toInt(s.charAt(inCursor++));
			int ch2 = base64toInt(s.charAt(inCursor++));
			int ch3 = base64toInt(s.charAt(inCursor++));
			result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));
			result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
			result[outCursor++] = (byte) ((ch2 << 6) | ch3);
		}
		// Translate partial group, if present
		if (missingBytesInLastGroup != 0) {
			int ch0 = base64toInt(s.charAt(inCursor++));
			int ch1 = base64toInt(s.charAt(inCursor++));
			result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));
			if (missingBytesInLastGroup == 1) {
				int ch2 = base64toInt(s.charAt(inCursor++));
				result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
			}
		}
		// assert inCursor == s.length()-missingBytesInLastGroup;
		// assert outCursor == result.length;
		return result;
	}

	/**
	 * Translates the specified character, which is assumed to be in the "Base
	 * 64 Alphabet" into its equivalent 6-bit positive integer.
	 * 
	 * @throw IllegalArgumentException is <tt>c/<cc> is not in the 
	 *        Base64 Alphabet.
	 */
	private static int base64toInt(char c) {
		if (c >= 'A' && c <= 'Z')
			return c - 'A';
		if (c >= 'a' && c <= 'z')
			return c - 'a' + 26;
		if (c >= '0' && c <= '9')
			return c - '0' + 52;
		if (c == '+')
			return 62;
		if (c == '/')
			return 63;
		throw new IllegalArgumentException("Illegal character " + c);
	}
}
