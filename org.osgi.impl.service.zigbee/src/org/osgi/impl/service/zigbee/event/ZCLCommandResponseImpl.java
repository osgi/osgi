
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZCLCommandResponse;
import org.osgi.service.zigbee.ZCLFrame;

/**
 * A terminal event
 */
public class ZCLCommandResponseImpl implements ZCLCommandResponse {

	private final Exception	failure;

	private final ZCLFrame	response;

	public ZCLCommandResponseImpl(Exception e) {
		failure = e;
		response = null;
	}

	public ZCLCommandResponseImpl(ZCLFrame z) {
		failure = null;
		response = z;
	}

	public Exception getFailure() {
		return null;
	}

	public ZCLFrame getResponse() {
		return null;
	}

	public boolean isEnd() {
		return false;
	}

}
