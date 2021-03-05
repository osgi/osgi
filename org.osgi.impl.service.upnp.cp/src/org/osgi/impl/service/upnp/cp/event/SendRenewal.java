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

public class SendRenewal extends GenaSocket implements Runnable {
	private String			timeout;
	private Subscription	subscription;
	private String			renewalMessage;

	public SendRenewal(Subscription subscription, String timeout) {
		this.subscription = subscription;
		this.timeout = timeout;
	}

	// This method creates the renewal message and sends it to the given host.
	@Override
	public void run() {
		formRenewalMessage();
		try {
			createSocket(subscription.getHost());
			sendSocket(renewalMessage);
			parseRequest();
			updateSubscription();
			subscription.setWaiting(false);
		}
		catch (Exception e) {
			subscription.setWaiting(false);
			System.out.println(e.getMessage());
		}
	}

	// This method creates the renewal message in the renewalMessage string
	// variable.
	void formRenewalMessage() {
		renewalMessage = GenaConstants.GENA_SUBSCRIBE + " "
				+ subscription.getPublisherPath().trim() + " "
				+ GenaConstants.GENA_SERVER_VERSION + "\r\n"
				+ GenaConstants.GENA_HOST + ": " + subscription.getHost()
				+ "\r\n" + GenaConstants.GENA_SID + ": " + sid + "\r\n"
				+ GenaConstants.GENA_TIMEOUT + ": " + timeout + "\r\n\r\n";
	}

	// This method is used to update the subscription object's expirty time and
	// timeout values.
	void updateSubscription() throws Exception {
		checkSubscriptionId();
		checkTimeoutDuration();
		if (timeDuration == 0) {
			subscription.setInfinite(true);
		}
		else {
			subscription.setExpirytime(timeDuration);
		}
		subscription.setTimeout(receivedTimeout);
	}
}
