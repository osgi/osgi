
package org.osgi.impl.service.zigbee.basedriver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;

/**
 * Mocked impl.
 */
public class ZCLFrameImpl implements ZCLFrame {

	private ZCLHeader	header	= null;
	private byte[]		payload	= null;

	/**
	 * @param header
	 * @param payload
	 */
	public ZCLFrameImpl(ZCLHeader header, byte[] payload) {
		this.header = header;
		this.payload = payload;
	}

	public ZCLHeader getHeader() {
		return this.header;
	}

	public byte[] getPayload() {
		byte[] result = new byte[payload.length];
		System.arraycopy(payload, 0, result, 0, payload.length);
		return result;
	}

	public ByteArrayInputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	public ByteArrayOutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}
}
