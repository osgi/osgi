/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.util;

public class Base64Encode {
	private byte[]	data;
	private int		bitIndex;
	private int		bitIndexExtra;
	private int		byteIndex;

	public Base64Encode(byte[] data) {
		this.data = data;
	}

	private void init() {
		bitIndex = 0;
		byteIndex = 0;
		if (data.length * 8 % 6 > 0)
			bitIndexExtra = 6 - (data.length * 8 % 6);
		else
			bitIndexExtra = 0;
	}

	private int nextBit() {
		if (byteIndex >= data.length) {
			return bitIndexExtra-- > 0 ? 0 : -1;
		}
		int ret = ((data[byteIndex] << bitIndex) & 128) == 128 ? 1 : 0;
		bitIndex = (bitIndex + 1) % 8;
		byteIndex = (bitIndex == 0 ? byteIndex + 1 : byteIndex);
		return ret;
	}

	private static char getChar(int code) {
		if (code >= 0 && code <= 25)
			return (char) (code + 65);
		else
			if (code >= 26 && code <= 51)
				return (char) (code + 71);
			else
				if (code >= 52 && code <= 61)
					return (char) (code - 4);
				else
					if (code == 62)
						return '+';
					else
						if (code == 63)
							return '/';
						else
							throw new IllegalArgumentException();
	}

	public String encode() throws Exception {
		init();
		StringBuffer ret = new StringBuffer();
		int bit = 0;
		int code = 0;
		int i = 0;
		while ((bit = nextBit()) > -1) {
			code = (code << 1) + bit;
			i = (i + 1) % 6;
			if (i == 0) {
				ret.append(getChar(code));
				if (ret.length() % 78 == 76)
					ret.append("\r\n");
				code = 0;
			}
		}
		if (data.length % 3 == 1)
			ret.append("==");
		else
			if (data.length % 3 == 2)
				ret.append("=");;
		return ret.toString();
	}
}
