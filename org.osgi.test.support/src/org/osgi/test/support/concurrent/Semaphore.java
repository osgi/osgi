/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.support.concurrent;

/**
 * A classic Semaphore class.
 */
public class Semaphore {

	private int	count;

	/**
	 * Creates a new Semaphore with 0 as a start value.
	 */
	public Semaphore() {
		this(0);
	}

	/**
	 * Creates a new Semaphore with a given start value.
	 * 
	 * @param startValue sets the value to start with.
	 * @throws IllegalArgumentException if the startValue is negative
	 */
	public Semaphore(int startValue) throws IllegalArgumentException {
		synchronized (this) {
			if (startValue < 0) {
				throw new IllegalArgumentException(
						"startValue can't be lower than 0");
			}
			count = startValue;
		}
	}

	public synchronized void release() {
		release(1);
	}

	public synchronized void release(int n) {
		count += n;
		notifyAll();
	}

	public synchronized void acquire(int c)
			throws java.lang.InterruptedException {
		while (this.count < c) {
			wait();
		}
		this.count -= c;
	}

	public void acquireUninterruptibly() {
		acquireUninterruptibly(1);
	}

	public void acquireUninterruptibly(int c) {
		while (true)
			try {
				acquire(c);
				return;
			}
			catch (InterruptedException ie) {
				// Ignore
			}
	}

	public synchronized int availablePermits() {
		return count;
	}

	public synchronized int drainPermits() {
		int old = count;
		count = 0;
		return old;
	}

	public boolean isFair() {
		return false;
	}

	public boolean tryAcquire() {
		return tryAcquire(1);
	}

	public synchronized boolean tryAcquire(int c) {
		if ( this.count <= c) {
			this.count -= c;
			return true;
		}
		return false;
	}

}
