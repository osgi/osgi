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

public class SendUnsubscribe extends GenaSocket implements Runnable {
	private Subscription	subscription;
	private String			subscriptionId;
	private String			unsubscribeMessage;

	public SendUnsubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscriptionId = subscription.getSubscriptionId();
	}

	// This method creates the unsubscribe message and sends the message to the
	// subscription host.
	@Override
	public void run() {
		formUnsubscribeMessage();
		try {
			createSocket(subscription.getHost());
			sendSocket(unsubscribeMessage);
			subscriptionId = subscription.getSubscriptionId();
			EventServiceImpl.subscriberList.remove(subscriptionId);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method creates the unsubscribe message for writing to the socket.
	void formUnsubscribeMessage() {
		unsubscribeMessage = GenaConstants.GENA_UNSUBSCRIBE + " "
				+ subscription.getPublisherPath().trim() + " "
				+ GenaConstants.GENA_SERVER_VERSION + "\r\n"
				+ GenaConstants.GENA_HOST + ": " + subscription.getHost()
				+ "\r\n" + GenaConstants.GENA_SID + ": " + subscriptionId
				+ "\r\n\r\n";
	}
}
