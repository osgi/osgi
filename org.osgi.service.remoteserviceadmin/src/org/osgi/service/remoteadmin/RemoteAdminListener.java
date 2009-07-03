package org.osgi.service.remoteadmin;

/**
 * A Remote Service Admin Listener is notified asynchronously of any export or
 * import registrations and unregistrations.
 * 
 * @ThreadSafe
 */

public interface RemoteAdminListener {
	/**
	 * @param event
	 */
	void remoteAdminEvent( RemoteAdminEvent event);
}
