
package org.osgi.test.cases.zigbee.tbc.util;

import java.util.Map;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;

/**
 * Mocked impl of ZigBeeHandler.
 */
public class ZigBeeHandlerImpl implements ZigBeeHandler {

	private Map	response;

	/**
	 * Constructor.
	 */
	public ZigBeeHandlerImpl() {

	}

	public void onSuccess(Map response) {
		// System.out.println("ZigBeeHandlerImpl.onSuccess(" + response + ")");
		this.isSuccess = true;
		this.response = response;
	}

	public void onFailure(ZigBeeException e) {
		// System.out.println("ZigBeeHandlerImpl.onFailure(" + response + ")");
		this.isSuccess = false;
		this.response = response;
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
	public Map getResponse() {
		return response;
	}

}
