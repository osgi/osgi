package org.osgi.test.cases.remoteserviceadmin.junit;

import java.util.LinkedList;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.test.support.compatibility.Semaphore;

class TestEventHandler implements EventHandler {
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
		// System.out.println("########################### TestEventHandler::handleEvent "
		// + event.getTopic() + "  " + " ( "
		// + Arrays.asList(event.getPropertyNames()) + "  )");

		synchronized (eventlist) {
			eventlist.add(event);
			sem.signal();
		}

	}

	Event getNextEventForTopic(String... topics) {
		if (topics.length == 0) {
			throw new IllegalArgumentException(
					"at least one topic must be provided");
		}
		try {
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
				sem.waitForSignal(m_timeout);
			}
		} catch (InterruptedException e1) {
			return null;
		}

	}

	int getEventCount() {
		return eventlist.size();
	}
}