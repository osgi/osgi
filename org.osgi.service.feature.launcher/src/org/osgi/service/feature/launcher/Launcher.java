package org.osgi.service.feature.launcher;

import org.osgi.service.feature.Feature;

/**
 * A launcher can launch one or more Feature models into a running system.
 */
public interface Launcher {
	/**
	 * Returns the effective Feature, which combines all features provided to
	 * the launcher together.
	 * 
	 * @return The effective Feature.
	 */
	Feature getEffectiveFeature();

	/**
	 * Start the launcher.
	 */
	void start();

	/**
	 * Wait until the system has stopped.
	 * 
	 * @param timeout Maximum number of milliseconds to wait. A value of zero
	 *            will wait indefinitely.
	 * @throws InterruptedException If another thread interrupted the current
	 *             thread before or while the current thread was waiting for the
	 *             system to stop. The <i>interrupted status</i> of the current
	 *             thread is cleared when this exception is thrown.
	 */
	void waitForStop(long timeout) throws InterruptedException;
}
