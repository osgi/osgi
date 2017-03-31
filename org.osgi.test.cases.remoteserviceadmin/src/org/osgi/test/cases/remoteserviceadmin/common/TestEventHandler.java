package org.osgi.test.cases.remoteserviceadmin.common;

import static junit.framework.TestCase.assertEquals;

import java.util.LinkedList;

import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * Simple EventHandler implementation that records all events received and
 * allows to request the events
 * */
public class TestEventHandler implements EventHandler {
	private final LinkedList<Event> eventlist = new LinkedList<Event>();
	private final Semaphore sem = new Semaphore(0);

	private final long m_timeout;

	public TestEventHandler(long timeout) {
		m_timeout = timeout;
	}

	/**
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	public void handleEvent(Event event) {
		// System.out
		// .println("########################### TestEventHandler::handleEvent "
		// + event.getTopic()
		// + "  "
		// + " ( "
		// + Arrays.asList(event.getPropertyNames()) + "  )");

		synchronized (eventlist) {
			eventlist.add(event);
			sem.signal();
		}

	}

	public Event getNextEventForTopic(String... topics) {
		if (topics.length == 0) {
			throw new IllegalArgumentException(
					"at least one topic must be provided");
		}
		try {
			int waited = 0;
			while (true) {
				synchronized (eventlist) {
					for (Event e : eventlist) {
						for (String topic : topics) {
							if (topic.equals(e.getTopic())) {
								eventlist.remove(e);
								return e;
							}
						}
					}
				}
				sem.waitForSignal(m_timeout / 10);
				++waited;
				if (waited >= 10)
					return null;
			}
		} catch (InterruptedException e1) {
			return null;
		}

	}

	public static RemoteServiceAdminEvent verifyBasicRsaEventProperties(
			ServiceReference rsaRef, Event event) {
		assertEquals("122.7: bundle has to refer to RSA bundle",
				rsaRef.getBundle(), event.getProperty("bundle"));
		assertEquals(
				"122.7: bundle.symbolicname has to refer to RSA bundle SymbolicName",
				rsaRef.getBundle().getSymbolicName(),
				event.getProperty("bundle.symbolicname"));
		assertEquals("122.7: bundle.id has to refer to RSA bundle id",
				rsaRef.getBundle().getBundleId(),
				event.getProperty("bundle.id"));
		assertEquals(
				"122.7: bundle.version has to refer to RSA bundle version",
				rsaRef.getBundle().getVersion(),
				event.getProperty("bundle.version"));
		RemoteServiceAdminEvent rsaevent = (RemoteServiceAdminEvent) event
				.getProperty("event");
		return rsaevent;
	}

	public int getEventCount() {
		synchronized (eventlist) {
			return eventlist.size();
		}
	}
}
