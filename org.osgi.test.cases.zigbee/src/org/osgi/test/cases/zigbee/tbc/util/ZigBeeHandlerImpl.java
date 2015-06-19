
package org.osgi.test.cases.zigbee.tbc.util;

import org.osgi.service.zigbee.ZigBeeHandler;

/**
 * Mocked impl of ZigBeeHandler.
 */
public class ZigBeeHandlerImpl implements ZigBeeHandler {

	private Object		successResponse;
	private Exception	failureResponse;

	/**
	 * Constructor.
	 */
	public ZigBeeHandlerImpl() {

	}

	public void onSuccess(Object response) {
		this.isSuccess = true;
		this.successResponse = response;
	}

	public void onFailure(Exception e) {
		this.isSuccess = false;
		this.failureResponse = e;
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
	 * @return Can be null, if the handler hasn't receive a response yet, or if
	 *         the response is null.
	 */
	public Object getSuccessResponse() {
		return successResponse;
	}

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return Can be null, if the handler hasn't receive a response yet, or if
	 *         the response is null.
	 */
	public Exception getFailureResponse() {
		return failureResponse;
	}

}
