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
