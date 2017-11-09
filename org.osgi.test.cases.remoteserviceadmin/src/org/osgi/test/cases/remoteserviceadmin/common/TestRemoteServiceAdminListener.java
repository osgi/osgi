package org.osgi.test.cases.remoteserviceadmin.common;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

/**
 * RemoteServiceAdminListener implementation, which collects and returns the
 * received events in order.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * 
 */
public class TestRemoteServiceAdminListener implements
		RemoteServiceAdminListener {
	private LinkedList<RemoteServiceAdminEvent> eventlist = new LinkedList<RemoteServiceAdminEvent>();
	private Semaphore sem = new Semaphore(0);
	private long timeout;

	public TestRemoteServiceAdminListener(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @see org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener#remoteAdminEvent(org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent)
	 */
	public void remoteAdminEvent(final RemoteServiceAdminEvent event) {
		eventlist.add(event);
		sem.release();
	}

	public RemoteServiceAdminEvent getNextEvent() {
		try {
			sem.tryAcquire(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e1) {
			return null;
		}

		try {
			return eventlist.removeFirst();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public int getEventCount() {
		return eventlist.size();
	}
}
