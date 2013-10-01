
package org.osgi.test.cases.zigbee.tbc.util;

import java.util.Map;
import org.osgi.service.zigbee.ZigBeeCommandHandler;

/**
 * Mocked impl of ZigBeeHandler.
 */
public class ZigBeeCommandHandlerImpl implements ZigBeeCommandHandler {

	private byte[]	responseSuccess;
	private Map		responseFailure;

	/**
	 * Constructor.
	 */
	public ZigBeeCommandHandlerImpl() {

	}

	public void onSuccess(byte[] response) {
		// System.out.println("ZigBeeHandlerImpl.onSuccess(" + response + ")");
		this.isSuccess = true;
		this.responseSuccess = response;
	}

	public void onFailure(Map response) {
		// System.out.println("ZigBeeHandlerImpl.onFailure(" + response + ")");
		this.isSuccess = false;
		this.responseFailure = response;
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
	 * @return ZigBeeHandlerImpl.reponse. Can be null, if the handler hasn't
	 *         receive a response yet, or if the response is null.
	 */
	public byte[] getResponseSuccess() {
		return responseSuccess;
	}

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return ZigBeeHandlerImpl.reponse. Can be null, if the handler hasn't
	 *         receive a response yet, or if the response is null.
	 */
	public Map getResponseFailure() {
		return responseFailure;
	}

}
