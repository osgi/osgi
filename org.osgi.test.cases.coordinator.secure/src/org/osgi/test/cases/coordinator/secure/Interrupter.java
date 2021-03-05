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
package org.osgi.test.cases.coordinator.secure;

/**
 * Used to prevent the CT from becoming blocked due to unexpected behavior from
 * an implementation.
 */
public class Interrupter extends Thread {
	private final Thread thread;
	private final long timeout;
	
	/**
	 * Create a new Interrupter.
	 * @param t The thread to interrupt should the timer expire.
	 * @param timeout How many milliseconds to wait before interrupting the target thread.
	 */
	public Interrupter(Thread t, long timeout) {
		thread = t;
		this.timeout = timeout;
		setDaemon(true);
	}
	
	public synchronized void run() {
		try {
			wait(timeout);
			thread.interrupt();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
