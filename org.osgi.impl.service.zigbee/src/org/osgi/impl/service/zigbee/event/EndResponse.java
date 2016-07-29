
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZCLCommandResponse;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * A terminal event
 */
public class EndResponse implements ZCLCommandResponse {
	private final Promise response = Promises.resolved(null);

	public Promise getResponse() {
		return response;
	}

	public boolean isEnd() {
		return true;
	}

}
