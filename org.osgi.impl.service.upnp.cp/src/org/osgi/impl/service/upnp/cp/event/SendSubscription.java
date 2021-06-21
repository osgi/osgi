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
package org.osgi.impl.service.upnp.cp.event;

import org.osgi.impl.service.upnp.cp.util.UPnPListener;

public class SendSubscription extends GenaSocket implements Runnable {
	private String			publisherpath;
	private String			hostString;
	private String			timeout;
	private UPnPListener	listener;
	private String			subscriptionMessage;

	public SendSubscription(String publisherpath, String hostString,
			String timeout, UPnPListener listener) {
		this.publisherpath = publisherpath;
		this.hostString = hostString;
		this.timeout = timeout;
		this.listener = listener;
	}

	// This method forms the subscription message and sends it to the given host
	// name.
	@Override
	public void run() {
		try {
			formSubscriptionMessage();
			createSocket(hostString);
			sendSocket(subscriptionMessage);
			if (parseRequest())
				addNewSubscriber();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method creates the subscription message based on the given details.
	void formSubscriptionMessage() {
		subscriptionMessage = GenaConstants.GENA_SUBSCRIBE + " "
				+ publisherpath.trim() + " "
				+ GenaConstants.GENA_SERVER_VERSION + "\r\n"
				+ GenaConstants.GENA_HOST + ": " + hostString + "\r\n"
				+ GenaConstants.GENA_CALLBACK + ": " + "<"
				+ GenaConstants.GENA_CALLBACK_URL + ">\r\n"
				+ GenaConstants.GENA_NT + ": upnp:event\r\n"
				+ GenaConstants.GENA_TIMEOUT + ": " + timeout + "\r\n\r\n";
	}

	// This method adds the new subscriber.
	void addNewSubscriber() throws Exception {
		checkSubscriptionId();
		checkTimeoutDuration();
		Subscription sc = new Subscription(sid, publisherpath, timeDuration,
				receivedTimeout, listener, hostString);
		if (timeDuration == 0) {
			sc.setInfinite(true);
		}
		sc.setActive(true);
		EventServiceImpl.subscriberList.put(sid.trim(), sc);
	}
}
