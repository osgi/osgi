
package org.osgi.impl.service.resourcemanagement.lock;

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
