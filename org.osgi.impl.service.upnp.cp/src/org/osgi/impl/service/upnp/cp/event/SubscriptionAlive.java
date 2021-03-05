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

import java.util.Enumeration;

public class SubscriptionAlive extends Thread {
	private boolean				active	= true;
	@SuppressWarnings("unused")
	private EventServiceImpl	esi;

	// Constructor used for storing refereneces
	public SubscriptionAlive(EventServiceImpl esi) {
		this.esi = esi;
		setDaemon(true);
	}

	// run method of the thread which keeps on polling all the sessions for
	// checking invalid
	// sessions.Sleeps for a particular time, then again checks.
	@Override
	public void run() {
		while (active) {
			long curTime = System.currentTimeMillis();
			Enumeration<String> timeouts = EventServiceImpl.subscriberList
					.keys();
			for (; timeouts.hasMoreElements();) {
				String key = timeouts.nextElement();
				Subscription sc = EventServiceImpl.subscriberList
						.get(key);
				if (!sc.getInfinite()) {
					long val = sc.getExpirytime();
					if (curTime - val >= 0) {
						EventServiceImpl.subscriberList.remove(key);
					}
				}
			}
			try {
				Thread.sleep(3000);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
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
