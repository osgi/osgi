
package org.osgi.test.cases.zigbee.tbc.util;

import org.osgi.service.zigbee.ZCLCommandHandler;
import org.osgi.service.zigbee.ZCLFrame;

/**
 * Mocked impl.
 */
public class ZCLCommandHandlerImpl implements ZCLCommandHandler {

	private ZCLFrame	response;

	/**
	 * Constructor.
	 */
	public ZCLCommandHandlerImpl() {

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.zigbee.ZCLCommandHandler#notifyResponse(org.osgi.service.zigbee.ZCLFrame)
	 */
	public void notifyResponse(ZCLFrame frame) {
		this.response = frame;
	}

	// Code below is for the testcases only.
	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return Can be null, if the handler hasn't receive a response yet.
	 */
	public ZCLFrame getResponse() {
		return this.response;
	}

}
