package org.osgi.service.remoteserviceadmin;

/**
 * A Remote Service Admin Listener is notified asynchronously of any export or
 * import registrations and unregistrations.
 * 
 * @ThreadSafe
 */

public interface RemoteServiceAdminListener {
	/**
	 * Receive a Remote Service Admin event.
	 * @param event
	 */
	void remoteAdminEvent( RemoteServiceAdminEvent event);
}
