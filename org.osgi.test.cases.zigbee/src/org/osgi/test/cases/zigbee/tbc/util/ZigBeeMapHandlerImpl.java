
package org.osgi.test.cases.zigbee.tbc.util;

import java.util.Map;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeMapHandler;

/**
 * Mocked impl.
 */
public class ZigBeeMapHandlerImpl implements ZigBeeMapHandler {

	private Map				successResponse;
	private ZigBeeException	failureResponse;

	/**
	 * Constructor.
	 */
	public ZigBeeMapHandlerImpl() {

	}

	public void onSuccess(Map response) {
		this.isSuccess = true;
		this.successResponse = response;
	}

	public void onFailure(ZigBeeException e) {
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
	 * @return ZigBeeAttributesHandlerImpl.successResponse. Can be null, if the
	 *         handler hasn't receive a response yet, or if the response is
	 *         null.
	 */
	public Map getSuccessResponse() {
		return successResponse;
	}

	/**
	 * FOR TESTCASES ONLY!
	 * 
	 * @return ZigBeeAttributesHandlerImpl.failureResponse. Can be null, if the
	 *         handler hasn't receive a response yet, or if the response is
	 *         null.
	 */
	public ZigBeeException getFailureResponse() {
		return failureResponse;
	}

}
