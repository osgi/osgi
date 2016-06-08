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

import org.osgi.service.zigbee.ZCLCommandHandler;
import org.osgi.service.zigbee.ZCLFrame;

/**
 * Mocked impl.
 * 
 * @author $Id: 7b665f09fd2e023a885fc679d0cb421102f41536 $
 */
public class ZCLCommandHandlerImpl implements ZCLCommandHandler {

	private ZCLFrame	response;
	private Exception	e;
	private Boolean		responseReceived	= Boolean.FALSE;

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
