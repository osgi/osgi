
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZCLCommandResponse;
import org.osgi.service.zigbee.ZCLFrame;

/**
 * A terminal event
 */
public class EndResponse implements ZCLCommandResponse {

	public Exception getFailure() {
		return null;
	}

	public ZCLFrame getResponse() {
		return null;
	}

	public boolean isEnd() {
		return true;
	}

}
