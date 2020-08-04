
package org.osgi.impl.service.tr069todmt.encode;

import java.util.Arrays;

/**
 * 
 */
public final class HexBinary {

	private final byte[]	value;

	/**
	 * @param value
	 */
	public HexBinary(byte[] value) {
		this.value = value;
	}

	/**
	 * @param hexString
	 */
	public HexBinary(String hexString) {
		value = fromHexString(hexString);
	}

	/**
	 * @return
	 */
	public byte[] binaryValue() {
		return value;
	}

	/**
	 * @return
	 */
	public String getEncoded() {
		return toHexString(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HexBinary) {
			return Arrays.equals(((HexBinary) obj).value, value);
		} else if (obj instanceof byte[]) {
			return Arrays.equals((byte[]) obj, value);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 11; // any arbitrary constant is fine here
	}

	private static byte[] fromHexString(String str) {
		if ((str.length() % 2) != 0) {
			throw new IllegalArgumentException("Illegal hex string: the length MUST be multiple of 2!");
		}

		byte[] value = new byte[str.length() / 2];
		for (int i = 0, j = 0; i < value.length; i++) {
			int next = 16 * fromHexChar(str.charAt(j++));
			next += fromHexChar(str.charAt(j++));
			value[i] = (byte) next;
		}
		return value;
	}

	private static String toHexString(byte[] ba) {
		char[] result = new char[ba.length * 2];
		for (int i = 0, j = 0; i < ba.length;) {
			result[j++] = toHexChar((byte) ((ba[i] >> 4) & 0x0F));
			result[j++] = toHexChar((byte) (ba[i++] & 0x0F));
		}
		return new String(result).toUpperCase();
	}

	private static final char	CHAR_A_MINUS_10	= 'a' - 10;

	private static char toHexChar(byte b) { // accepts byte [0:15]
		if (b < 10) {
			return (char) ('0' + b);
		} else {
			return (char) (CHAR_A_MINUS_10 + b);
		}
	}

	private static final char	CHAR_UPPER_A_MINUS_10	= 'A' - 10;

	private static int fromHexChar(char c) {
		if ('0' <= c && c <= '9') {
			return (c - '0');
		} else if ('a' <= c && c <= 'f') {
			return (c - CHAR_A_MINUS_10);
		} else if ('A' <= c && c <= 'F') {
			return (c - CHAR_UPPER_A_MINUS_10);
		} else {
			throw new IllegalArgumentException("Illegal hex string: non hex digit found - " + c + "!");
		}
	}
}
