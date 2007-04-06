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

/**
 * 
 * Helper class to be used for synchronization when dealing with asynchronous
 * event broadcasts. Unlike the previously used
 * <code>org.osgi.test.cases.util.Semaphore</code>, this class does not block
 * indefinedly when an expected signal doesn't arrive.
 * 
 * @author Jorge Mascena
 */
public class Synchronizer {
	private int	signalCount;

	/**
	 * Creates a <code>Synchronizer</code> instance with no signals on the
	 * queue.
	 * 
	 */
	public Synchronizer() {
		signalCount = 0;
	}

	/**
	 * Puts a signal on the queue and notifies sleeping threads possibly waiting
	 * for a signal to be consumed.
	 * 
	 */
	public synchronized void signal() {
		signalCount++;
		notifyAll();
	}

	/**
	 * Consumes a signal from the queue. If no signal is available, waits for
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
	 * Consumes some signals from the queue. If no signal is available, waits for
	 * <code>timemilli</code> milliseconds and then checks again if there's
	 * signals to be consumed.
	 * 
	 * @param timemilli the time (in millisends) to wait for a signal if none is
	 *        available.
	 * @param compareCount the amount of signals to consume
	 * @return <code>true</code> if there was a signal to be consumed.
	 *         <code>false</code> otherwise.
	 */	
	public synchronized boolean waitForSignal(long timemilli, int compareCount) {
		if (signalCount < compareCount) {
			try {
				wait(timemilli);
			}
			catch (InterruptedException e) {
			}
		}
		
		return signalCount >= compareCount;
	}
}
