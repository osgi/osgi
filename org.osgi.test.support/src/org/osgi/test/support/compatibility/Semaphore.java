
package org.osgi.test.support.compatibility;


/**
 * A classic Semaphore class.
 */
public class Semaphore {

	int count = 0;

	/**
	 * Creates a new Semaphore with 0 as a start value.
	 */
	public Semaphore() {
		this(0);
	}

	/** 
	 * Creates a new Semaphore with a given start value.
	 * @param startValue sets the value to start with.
	 * @throws IllegalArgumentException if the startValue is negative
	 */
	public Semaphore(int startValue) throws IllegalArgumentException {
		if(startValue < 0) {
			throw new IllegalArgumentException("startValue can't be lower than 0");
		}
		synchronized (this) {
			count = startValue;
		}
	}
	
	/**
	 * Waits until a signal is (or has been) given.
	 */
	public void waitForSignal() throws InterruptedException {
		waitForSignal(0);
	}

	/**
	 * Waits until a signal is (or has been) given.
	 * 
	 * @param timeout The maximum number of milliseconds to wait for a signal or
	 *        0 to wait indefinitely.
	 * @return true if the signal is received; false if the wait times out.
	 */
	public synchronized boolean waitForSignal(long timeout)
			throws InterruptedException {
		while (count == 0) {
			wait(timeout);
			if ((count == 0) && (timeout != 0)) {
				return false;
			}
		}
		count--;
		return true;
	}
	
	/**
	 * Give the signal.
	 */
	public synchronized void signal() {
        count++;
        notify();
    }
}

