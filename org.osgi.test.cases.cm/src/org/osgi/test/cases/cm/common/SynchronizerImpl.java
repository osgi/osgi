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

package org.osgi.test.cases.cm.common;

import java.util.Dictionary;

import org.osgi.test.cases.cm.shared.Synchronizer;

/**
 * 
 * Helper class to be used for synchronization when dealing with asynchronous
 * event broadcasts. Unlike the previously used
 * <code>org.osgi.test.cases.util.Semaphore</code>, this class does not block
 * indefinedly when an expected signal doesn't arrive.
 * 
 * @author Jorge Mascena
 * @author Ikuo Yamasaki, NTT Corporation
 */
public class SynchronizerImpl implements Synchronizer {
	private int signalCount;
	private int signalDeletedCount;
	private final String id;
	private final String header;
	private Dictionary<String,Object>	props;
	private static final boolean DEBUG = true;

	/**
	 * Creates a <code>Synchronizer</code> instance with no signals on the
	 * queue.
	 * 
	 * @param id
	 * 
	 */
	public SynchronizerImpl(String id) {
		signalCount = 0;
		signalDeletedCount = 0;
		this.id = id;
		this.header = "SYNC(" + this.id + ")";
	}

	public SynchronizerImpl() {
		this.id = null;
		this.header = "SYNC";
	}

	/**
	 * Puts a signal on the queue and notifies sleeping threads possibly waiting
	 * for a signal to be consumed.
	 * 
	 */
	public synchronized void signal() {
		signalCount++;
		if (DEBUG)
			System.out.println(header
					+ ":signal() signalCount is incremented to " + signalCount);
		notifyAll();
	}

	public synchronized void signalDeleted() {
		signalDeletedCount++;
		if (DEBUG)
			System.out.println(header
					+ ":signalDeleted() signalDeletedCount is incremented to "
					+ signalDeletedCount);
		notifyAll();
	}

	/**
	 * Consumes signals from the queue. If no signal is available, waits for
	 * <code>timemilli</code> milliseconds and then checks again if there's a
	 * signal to be consumed.
	 * 
	 * @param timemilli
	 *            the time (in millisends) to wait for a signal if none is
	 *            available.
	 * @return <code>true</code> if there was a signal to be consumed.
	 *         <code>false</code> otherwise.
	 */
	public synchronized boolean waitForSignal(long timemilli) {
		return waitForSignal(timemilli, 1);
	}

	public synchronized boolean waitForSignal(long timemilli, int compareCount) {
		return this.waitForSignal(timemilli, compareCount, false);
	}

	/**
	 * Consumes some signals from the queue. If the specified compareCount
	 * signals are received or expire <code>timemilli</code> milliseconds,
	 * return the flag the signals are consumed.
	 * 
	 * @param timemilli
	 *            the time (in millisends) to wait for a signal if none is
	 *            available.
	 * @param compareCount
	 *            the amount of signals to consume
	 * @return <code>true</code> if there were signals to be consumed.
	 *         <code>false</code> otherwise.
	 */
	public synchronized boolean waitForSignal(long timemilli, int compareCount,
			boolean deleted) {
		String name = this.getUsedName(deleted);
		int count = this.getUsedCount(deleted);

		if (DEBUG)
			System.out.println(header + ":Begin waitForSignal(" + timemilli
					+ "," + compareCount + "," + deleted + "): " + name + "="
					+ count);

		final long preTime = System.currentTimeMillis();
		while (true) {
			name = this.getUsedName(deleted);
			count = this.getUsedCount(deleted);

			if (!(count < compareCount))
				break;
			long curTime = System.currentTimeMillis();
			if (curTime >= preTime + timemilli)
				break;
			long period = preTime + timemilli - curTime;
			try {
				if (DEBUG)
					System.out.println(header + ":waitForSignal(" + timemilli
							+ "," + compareCount + "): " + name + "=" + count
							+ ": going to wait...");
				wait(period);
				if (DEBUG)
					System.out.println(header + ":waitForSignal(" + timemilli
							+ "," + compareCount + "): " + name + "=" + count
							+ ": timeout or notified !!");
			} catch (InterruptedException e) {
				if (DEBUG)
					System.out.println(header + ":waitForSignal(" + timemilli
							+ "," + compareCount + "): " + name + "=" + count
							+ ": interrupted!!");
			}
		}
		if (DEBUG)
			System.out.println(header + ":end   waitForSignal(" + timemilli
					+ "," + compareCount + "): " + name + "=" + count);

		return this.getUsedCount(deleted) >= compareCount;
	}

	private int getUsedCount(boolean deleted) {
		if (deleted) {
			return this.signalDeletedCount;
		} else {
			return this.signalCount;
		}
	}

	private String getUsedName(boolean deleted) {
		if (deleted) {
			return "signalDeletedCount";
		} else {
			return "signalCount";
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void signal(Dictionary<String, ? > p) {
		this.props = (Dictionary<String,Object>) p;
		if (DEBUG)
			System.out.println(header + ":signal(props=" + p + ")");
		this.signal();
	}

	public synchronized Dictionary<String,Object> getProps() {
		return props;
	}

	public synchronized int getCount() {
		return signalCount;
	}

	public synchronized void resetCount() {
		signalCount = 0;
	}

	public synchronized int getDeletedCount() {
		return signalDeletedCount;
	}

	public void signalDeleted(String pid) {
		if (DEBUG)
			System.out.println(header + ":signal(pid=" + pid + ")");
		this.signalDeleted();
	}

}
