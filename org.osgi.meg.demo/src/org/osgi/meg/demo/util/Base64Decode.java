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

import java.util.Vector;

public class Base64Decode {
	private String	data;

	public Base64Decode(String data) {
		if (data.length() % 4 != 0)
			throw new IllegalArgumentException("length % 4");
		this.data = data;
	}

	private static int getInt(char c) {
		if (c >= 'A' && c <= 'Z')
			return (int) (c - 65);
		else
			if (c >= 'a' && c <= 'z')
				return (int) (c - 71);
			else
				if (c >= '0' && c <= '9')
					return (int) (c + 4);
				else
					if (c == '+')
						return 62;
					else
						if (c == '/')
							return 63;
						else
							if (c == '=')
								return -1;
							else
								throw new IllegalArgumentException(" " + c
										+ " ");
	}

	public byte[] decode() {
		Vector ret = new Vector();
		if (data.length() == 0)
			return new byte[0];
		for (int i = 0; i < data.length(); i += 4) {
			char c1 = 'A';
			char c2 = 'A';
			char c3 = 'A';
			char c4 = 'A';
			c1 = data.charAt(i);
			if (i + 1 < data.length())
				c2 = data.charAt(i + 1);
			if (i + 2 < data.length())
				c3 = data.charAt(i + 2);
			if (i + 3 < data.length())
				c4 = data.charAt(i + 3);
			byte by1 = (byte) getInt(c1);
			byte by2 = (byte) getInt(c2);
			byte by3 = (byte) getInt(c3);
			byte by4 = (byte) getInt(c4);
			ret.add(new Byte((byte) ((by1 << 2) | (by2 >> 4))));
			if (c3 != '=')
				ret.add(new Byte((byte) (((by2 & 0xf) << 4) | (by3 >> 2))));
			if (c4 != '=')
				ret.add(new Byte((byte) (((by3 & 0x3) << 6) | by4)));
		}
		return toByteArray(ret);
	}

	private byte[] toByteArray(Vector v) {
		byte[] ret = new byte[v.size()];
		for (int i = 0; i < v.size(); ++i)
			ret[i] = ((Byte) v.get(i)).byteValue();
		return ret;
	}
}
