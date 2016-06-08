/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.ZigBeeHandler;

/**
 * Mocked impl of ZigBeeHandler.
 * 
 * @author $Id: dfdfaaf7098658786a9cf7174d0508bac974267e $
 */
public class ZigBeeHandlerImpl implements ZigBeeHandler {

	private Object		successResponse;
	private Exception	failureResponse;
	private Boolean		responseReceived	= Boolean.FALSE;

	/**
	 * Constructor.
	 */
	public ZigBeeHandlerImpl() {

	}

	public void onSuccess(Object response) {
		this.isSuccess = Boolean.TRUE;
		responseReceived = Boolean.TRUE;
		this.successResponse = response;
	}

	public void onFailure(Exception e) {
		this.isSuccess = Boolean.FALSE;
		responseReceived = Boolean.TRUE;
		this.failureResponse = e;
	}

	// Code below is for the testcases only.
	/**
	 * FOR TESTCASES ONLY!
	 */
	private Boolean isSuccess;

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
