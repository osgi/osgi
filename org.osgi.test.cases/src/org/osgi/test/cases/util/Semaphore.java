
package org.osgi.test.cases.util;

/**
 * A classic Semaphore class.
 */
public class Semaphore {

	int count = 0;

	/** 
	 * Creates a new Semaphore with 0 as a starti value.
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
	public synchronized void waitForSignal() {
		while(count == 0) {
			try {
				super.wait();
			}
			catch(InterruptedException e) {
			    throw new RuntimeException("Thread " 
			        + Thread.currentThread().getName() + " was interrupted");
			}
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

