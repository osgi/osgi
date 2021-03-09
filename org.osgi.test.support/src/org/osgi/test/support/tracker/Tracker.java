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

package org.osgi.test.support.tracker;

import org.osgi.util.tracker.ServiceTracker;

public class Tracker {
	/**
	 * Wait for a tracked service for the requested amount of milliseconds.
	 *
	 * @param millis
	 */
	public static <S, T> T waitForService(final ServiceTracker<S, T> tracker,
			long timeout) throws InterruptedException {
		if (timeout <= 0) {
			return tracker.waitForService(timeout);
		}
		final long endTime = System.currentTimeMillis() + timeout;
		do {
			T service = tracker.waitForService(timeout);
			if (service != null) {
				return service;
			}
			timeout = endTime - System.currentTimeMillis();
		} while (timeout > 0);
		return null;
	}
}
