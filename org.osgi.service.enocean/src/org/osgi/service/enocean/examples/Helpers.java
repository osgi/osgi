package org.osgi.service.enocean.examples;

import java.util.Collection;

public final class Helpers {
	
	public static final byte[] concat(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
	public static final byte[] combine(byte[][] bytes) {
		byte[] previous = bytes[0];
		for(int i=1;i<bytes.length;i++) {
			previous = concat(previous, bytes[i]); 
		}
		return previous;
	}

	/**
	 * Converts an int to a big-endian byte array.
	 * @param value
	 * @return
	 */
	public static final byte[] intToByteArray_BE(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	/**
	 * Converts a short int to a 1-byte array.
	 * @param value
	 * @return
	 */
	public static final byte[] shortToByteArray(int value) {
		return new byte[] { (byte)(value & 0xff) };
	}

}
