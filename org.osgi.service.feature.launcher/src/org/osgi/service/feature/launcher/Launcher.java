package org.osgi.service.feature.launcher;

import org.osgi.framework.launch.Framework;

/**
 * A launcher can launch a Feature model into a running system.
 */
public interface Launcher {
	/**
	 * Start the launcher. This method is asynchronous and will return as soon
	 * as the launching has been initiated.
	 * 
	 * @return The Framework the Feature is launched into.
	 */
	Framework start();

	/**
	 * Wait until the system has stopped.
	 * 
	 * @param timeout Maximum number of milliseconds to wait. A value of zero
	 *            will wait indefinitely.
	 * @throws InterruptedException If another thread interrupted the current
	 *             thread before or while the current thread was waiting for the
	 *             system to stop. The <i>interrupted status</i> of the current
	 *             thread is cleared when this exception is thrown.
	 * @throws LauncherException When the launch is not successful.
	 */
	void waitForStop(long timeout)
			throws InterruptedException, LauncherException;
}
