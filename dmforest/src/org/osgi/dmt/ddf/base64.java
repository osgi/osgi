
package org.osgi.dmt.ddf;

/**
 * A helper class to distinguish between bin and base64 types in Dmt Admin. This
 * class just wraps a byte array.
 * 
 */
public class base64 {
	final byte[]	data;

	/**
	 * The constructor, just wrap the byte array to give it a new value
	 * 
	 * @param data the byte array to wrap
	 */
	public base64(byte[] data) {
		this.data = data;
	}

	/**
	 * The wrapped byte array.
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
}
