/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.upnp.cd.event;

import java.io.DataOutputStream;
import java.util.Hashtable;

// This class processes the renewal request from the subscriber. Check the respective headers
// and if its a valid request, does the renewal and returns the subscriber the renewal response
// method.
public class RenewalCheck extends ErrorCheck {
	private DataOutputStream	dos;
	private Hashtable<String,String>	headers;

	// constructor for intializing variables
	public RenewalCheck(DataOutputStream dos,
			Hashtable<String,String> headers) {
		this.dos = dos;
		this.headers = headers;
	}

	// This method does all the renewal processing. Checks for valid headers and
	// if valid
	// renews the subscription.
	public void renew() {
		if (!checkHeaders(headers)) {
			writeData(result);
			return;
		}
		if (!check_SID(headers)) {
			writeData(result);
			return;
		}
		String timeout = headers.get("timeout");
		String sid = headers.get("sid");
		Subscription subscription = EventRegistry
				.getSubscriber(sid);
		setTime(timeout, subscription);
		String message = formSubscription_Okay_Message(headers, subscription);
		writeData(message);
	}

	// This method writes the output back to the subscriber.
	void writeData(String message) {
		try {
			dos.writeBytes(message);
			dos.flush();
			dos.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
