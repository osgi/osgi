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

import java.util.Enumeration;

// This class is a utility thread which wakes up based on a time period and checks for all invalid
// subscriptions based on subscription timeout period. Removes all invalid subscriptions from the
// Event registry.
public class SubscriptionAlive extends Thread {
	boolean	active	= true;

	// Constructor which sets this thread to a daemon thread.
	public SubscriptionAlive() {
		setDaemon(true);
	}

	// run method of the thread which keeps on polling event registry for all
	// the subscriptions
	// whose timeout period expires. Removes the subscription object if timeout
	// expires and
	// notifies event registry to check wehther the servicelistener related with
	// this subscription can
	// be unsubscribed from the framework or not.
	@Override
	public void run() {
		while (active) {
			long curTime = System.currentTimeMillis();
			Enumeration<String> subscriberIds = EventRegistry
					.getSubscriberIds();
			for (; subscriberIds.hasMoreElements();) {
				String subscriberId = subscriberIds.nextElement();
				Subscription sc = EventRegistry
						.getSubscriber(subscriberId);
				if (!sc.getInfinite()) {
					long val = sc.getExpirytime();
					if (curTime - val >= 0) {
						String serviceId = sc.getServiceId();
						EventRegistry.removeSubscriber(subscriberId);
						EventRegistry.checkForServiceId(serviceId);
					}
				}
			}
			try {
				Thread.sleep(3000);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Method which will be called from httpserver when the server shuts down.
	// Since this threads
	// continously runs, there should be a way to come out of the loop.Sets the
	// variable to false.
	public void surrender(boolean b) {
		active = b;
	}
}
