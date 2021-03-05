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

package org.osgi.test.support.sleep;

public class Sleep {
	/**
	 * Sleep for the requested amount of milliseconds.
	 *
	 * @param timeout
	 */
	public static void sleep(long timeout) throws InterruptedException {
		if (timeout <= 0) {
			Thread.sleep(timeout);
			return;
		}
		final long endTime = System.currentTimeMillis() + timeout;
		do {
			Thread.sleep(timeout);
			timeout = endTime - System.currentTimeMillis();
		} while (timeout > 0);
	}
}
