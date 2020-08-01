
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZCLCommandResponse;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.util.promise.Promise;

/**
 * A terminal event
 */
public class ZCLCommandResponseImpl implements ZCLCommandResponse {

	private final Promise<ZCLFrame> response;

	public ZCLCommandResponseImpl(Promise<ZCLFrame> response) {
		this.response = response;
	}

	@Override
	public Promise<ZCLFrame> getResponse() {
		return response;
	}

	@Override
	public boolean isEnd() {
		return false;
	}

}
