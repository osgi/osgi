/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.useradmin.tbc;

import org.osgi.service.useradmin.*;
import org.osgi.test.cases.util.Semaphore;

public class RoleListener implements UserAdminListener {
	UserAdminControl	owner;
	Semaphore			semaphore;
	/* The event that end the listening */
	int					lastEvent;
	boolean				logging	= true;

	public RoleListener(UserAdminControl owner, Semaphore semaphore,
			int lastEvent) {
		this.semaphore = semaphore;
		this.owner = owner;
		this.lastEvent = lastEvent;
	}

	public synchronized void roleChanged(UserAdminEvent event) {
		if (logging) {
			// Hold on a bit to see if this was delivered asynchronously
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {
			}
			owner.log("received event", owner
					.eventTypeToString(event.getType())
					+ " for " + event.getRole().getName());
			if (event.getType() == lastEvent) {
				semaphore.signal();
				logging = false;
			}
		}
	}
}
