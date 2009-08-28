/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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
 */
public class SynchronizerImpl implements Synchronizer {
	private int				signalCount;
	private final String	id;
	private final String	header;
	private Dictionary		props;
	private static final boolean	DEBUG	= true;

	/**
	 * Creates a <code>Synchronizer</code> instance with no signals on the
	 * queue.
	 * 
	 * @param id
	 * 
	 */
	public SynchronizerImpl(String id) {
		signalCount = 0;
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

	/**
	 * Consumes signals from the queue. If no signal is available, waits for
	 * <code>timemilli</code> milliseconds and then checks again if there's a
	 * signal to be consumed.
	 * 
	 * @param timemilli the time (in millisends) to wait for a signal if none is
	 *        available.
	 * @return <code>true</code> if there was a signal to be consumed.
	 *         <code>false</code> otherwise.
	 */
	public synchronized boolean waitForSignal(long timemilli) {
		return waitForSignal(timemilli, 1);
	}

	/**
	 * Consumes some signals from the queue. If the specified compareCount
	 * signals are received or expire <code>timemilli</code> milliseconds,
	 * return the flag the signals are consumed.
	 * 
	 * @param timemilli the time (in millisends) to wait for a signal if none is
	 *        available.
	 * @param compareCount the amount of signals to consume
	 * @return <code>true</code> if there were signals to be consumed.
	 *         <code>false</code> otherwise.
	 */
	public synchronized boolean waitForSignal(long timemilli, int compareCount) {
		if (DEBUG)
			System.out.println(header + ":Begin waitForSignal(" + timemilli
					+ "," + compareCount + "): signalCount=" + signalCount);
		final long preTime = System.currentTimeMillis();
		while (signalCount < compareCount) {
			long curTime = System.currentTimeMillis();
			if (curTime >= preTime + timemilli)
				break;
			long period = preTime + timemilli - curTime;
			try {
				if (DEBUG)
					System.out.println(header + ":waitForSignal(" + timemilli
							+ "," + compareCount + "): signalCount="
							+ signalCount + ": going to wait...");
				wait(period);
				if (DEBUG)
					System.out.println(header + ":waitForSignal(" + timemilli
							+ "," + compareCount + "):signalCount="
							+ signalCount + ": timeout or notified !!");
			}
			catch (InterruptedException e) {
				if (DEBUG)
					System.out.println(header + ":waitForSignal(" + timemilli
							+ "," + compareCount + "):signalCount="
							+ signalCount + ": interrupted!!");
			}
		}
		if (DEBUG)
			System.out.println(header + ":end   waitForSignal(" + timemilli
					+ "," + compareCount + "): signalCount=" + signalCount);

		return signalCount >= compareCount;
	}

	public synchronized void signal(Dictionary p) {
		this.props = p;
		if (DEBUG)
			System.out.println(header + ":signal(" + p + ")");
		this.signal();
	}

	public synchronized Dictionary getProps() {
		return props;
	}

	public synchronized int getCount() {
		return signalCount;
	}

	public synchronized void resetCount() {
		signalCount = 0;
	}
}
