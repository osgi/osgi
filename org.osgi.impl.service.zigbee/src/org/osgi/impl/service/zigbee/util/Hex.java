
package org.osgi.impl.service.zigbee.util;

import java.math.BigInteger;

/**
 * Utility for printing and parsing hex numbers.
 * 
 * @author $Id$
 */
public class Hex {
	public static String toHexString(BigInteger number, int hexDigits) {
		String s = number.toString(16);
		if (s.length() < hexDigits) {
			int length = s.length();
			for (int i = 0; i < (hexDigits - length); i++) {
				s = "0" + s;
			}
		}
		return s;
	}

	public static String toHexString(int number, int hexDigits) {
		String s = Integer.toHexString(number);
		if (s.length() < hexDigits) {
			int length = s.length();
			for (int i = 0; i < (hexDigits - length); i++) {
				s = "0" + s;
			}
		}
		return s;
	}
}
