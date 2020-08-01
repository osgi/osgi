
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZCLCommandResponse;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * A terminal event
 */
public class EndResponse implements ZCLCommandResponse {
	private final Promise<ZCLFrame> response = Promises.resolved(null);

	@Override
	public Promise<ZCLFrame> getResponse() {
		return response;
	}

	@Override
	public boolean isEnd() {
		return true;
	}

}
