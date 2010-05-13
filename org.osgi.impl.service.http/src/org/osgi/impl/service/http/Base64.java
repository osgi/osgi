/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.http;

//  ******************** Base64 ********************
/**
 * * A base64 encode/decoder * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$
 */
public final class Base64 {
	private static final char	encodeTable[]	= {'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '+', '/'	};
	private static final byte	decodeTable[]	= {-1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
			-1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1,
			-1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
			40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

	// No instances
	private Base64() {
	}

	/**
	 * Encode a string of binary data using Base64.
	 */
	public static String encode(String in) {
		StringBuffer sb = new StringBuffer();
		int ilen = in.length();
		int bits = 0;
		int nbits = 0;
		for (int i = 0; i < ilen; i++) {
			char c = in.charAt(i);
			if (c >= 256)
				throw new IllegalArgumentException("Illegal character");
			bits = (bits << 8) | c;
			nbits += 8;
			while (nbits >= 6) {
				nbits -= 6;
				sb.append(encodeTable[0x3f & (bits >> nbits)]);
			}
		}
		switch (nbits) {
			case 2 :
				sb.append(encodeTable[0x3f & (bits << 4)]);
				sb.append('=');
				sb.append('=');
				break;
			case 4 :
				sb.append(encodeTable[0x3f & (bits << 2)]);
				sb.append('=');
				break;
		}
		return sb.toString();
	}

	/**
	 * Decode a string of Base64 data.
	 */
	public static String decode(String in) {
		StringBuffer sb = new StringBuffer();
		int ilen = in.length();
		int i = 0;
		int bits = 0;
		int nbits = 0;
		// Check for complete groups and proper termination
		if (ilen % 4 != 0)
			throw new IllegalArgumentException("Not a multiple of 4 characters");
		for (; ilen > 0 && in.charAt(ilen - 1) == '='; ilen--);
		if (in.length() - ilen > 2)
			throw new IllegalArgumentException("Too many trailing =");
		// Decode
		for (; i < ilen; i++) {
			char c = in.charAt(i);
			byte b = c < 128 ? decodeTable[c] : -1;
			if (b < 0)
				throw new IllegalArgumentException("Illegal character");
			bits = (bits << 6) | b;
			nbits += 6;
			if (nbits >= 8) {
				nbits -= 8;
				sb.append((char) (0xff & (bits >> nbits)));
			}
		}
		return sb.toString();
	}
} // Base64
