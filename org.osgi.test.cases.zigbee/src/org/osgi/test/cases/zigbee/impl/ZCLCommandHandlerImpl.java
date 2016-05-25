package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.ZCLCommandHandler;
import org.osgi.service.zigbee.ZCLFrame;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZCLCommandHandlerImpl implements ZCLCommandHandler {

	private ZCLFrame response;
	private Exception e;
	private Boolean responseReceived = Boolean.FALSE;

	/**
	 * Constructor.
	 */
	public ZCLCommandHandlerImpl() {

	}

	public void notifyResponse(ZCLFrame frame, Exception exception) {
		responseReceived = Boolean.TRUE;
		this.response = frame;
		this.e = exception;
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

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return Can be null, if the handler hasn't receive an exception yet.
	 */
	public Exception getException() {
		return this.e;
	}

	public boolean waitForResponse(long timeout) {
		long timeStart = System.currentTimeMillis();
		while (!responseReceived.booleanValue()) {

			if (System.currentTimeMillis() - timeStart > timeout) {
				return false;
			}
		}
		return true;
	}

}
