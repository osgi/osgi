
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
		count = startValue;
	}
	
	/**
	 * Waits until a signal is (or has been) given.
	 */
	public void waitForSignal() throws InterruptedException {
		waitForSignal(0);
	}
	
	/**
	 * Waits until a signal is (or has been) given.
	 */
	public synchronized void waitForSignal(long timeout) throws InterruptedException {
		while (count == 0) {
			wait(timeout);
		}
		count--;
	}
	
	/**
	 * Give the signal.
	 */
	public synchronized void signal() {
        count++;
        notify();
    }
}

