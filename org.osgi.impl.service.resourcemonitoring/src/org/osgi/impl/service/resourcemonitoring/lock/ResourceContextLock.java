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

package org.osgi.impl.service.resourcemonitoring.lock;

/**
 *
 */
public class ResourceContextLock {

	/**
	 * number of user holding the lock
	 */
	private int	count	= 0;

	/**
	 * 
	 */
	public ResourceContextLock() {

	}

	/**
	 * Acquire bundle lock
	 */
	public void acquire() {
		synchronized (this) {
			while (count == 1) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			count++;
		}
	}

	/**
	 * Release bundle lock
	 */
	public void release() {
		synchronized (this) {
			count--;
			this.notifyAll();
		}
	}

	/**
	 * Check if the bundle lock is acquired.
	 * 
	 * @return true if acquired.
	 */
	public boolean isLocked() {
		boolean isLocked = false;
		synchronized (this) {
			isLocked = (count == 1);
		}
		return isLocked;
	}
}
