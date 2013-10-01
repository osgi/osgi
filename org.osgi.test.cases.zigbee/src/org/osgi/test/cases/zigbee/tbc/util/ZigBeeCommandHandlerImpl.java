
package org.osgi.test.cases.zigbee.tbc.util;

import org.osgi.service.zigbee.ZigBeeCommandHandler;
import org.osgi.service.zigbee.ZigBeeException;

/**
 * Mocked impl of ZigBeeCommandHandler.
 */
public class ZigBeeCommandHandlerImpl implements ZigBeeCommandHandler {

	private byte[]			responseSuccess;
	private ZigBeeException	responseFailure;

	/**
	 * Constructor.
	 */
	public ZigBeeCommandHandlerImpl() {

	}

	public void onSuccess(byte[] response) {
		// System.out.println("ZigBeeCommandHandlerImpl.onSuccess(" + response +
		// ")");
		this.isSuccess = true;
		this.responseSuccess = response;
	}

	public void onFailure(ZigBeeException e) {
		// System.out.println("ZigBeeCommandHandlerImpl.onFailure(" + response +
		// ")");
		this.isSuccess = false;
		this.responseFailure = e;
	}

	// Code below is for the testcases only.
	/**
	 * FOR TESTCASES ONLY!
	 */
	private Boolean	isSuccess;

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return null if the handler hasn't receive a response yet, or true if
	 *         onSuccess method has been called, or false if the onFailure
	 *         method has been called.
	 */
	public Boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return ZigBeeCommandHandlerImpl.reponse. Can be null, if the handler
	 *         hasn't receive a response yet, or if the response is null.
	 */
	public byte[] getResponseSuccess() {
		return responseSuccess;
	}

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return ZigBeeCommandHandlerImpl.responseFailure. Can be null, if the
	 *         handler hasn't receive a response yet, or if the response is
	 *         null.
	 */
	public ZigBeeException getResponseFailure() {
		return responseFailure;
	}

}
