
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZCLCommandResponse;
import org.osgi.util.promise.Promise;

/**
 * A terminal event
 */
public class ZCLCommandResponseImpl implements ZCLCommandResponse {

	private final Promise response;

	public ZCLCommandResponseImpl(Promise response) {
		this.response = response;
	}

	public Promise getResponse() {
		return response;
	}

	public boolean isEnd() {
		return false;
	}

}
