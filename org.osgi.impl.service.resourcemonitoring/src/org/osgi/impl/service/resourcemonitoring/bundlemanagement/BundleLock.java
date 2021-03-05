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

package org.osgi.impl.service.resourcemonitoring.bundlemanagement;

/**
 *
 */
public class BundleLock {

	private boolean	isLocked;

	/**
	 * Acquire bundle lock. This method blocks until the bundle lock is
	 * acquired.
	 */
	public void acquireLock() {
		boolean acquired = false;

		synchronized (this) {
			while (!acquired) {
				if (!isLocked) {
					isLocked = true;
					acquired = true;
					return;
				}

				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Release the lock.
	 */
	public void releaseLock() {
		synchronized (this) {
			isLocked = false;
			notifyAll();
		}
	}

}
