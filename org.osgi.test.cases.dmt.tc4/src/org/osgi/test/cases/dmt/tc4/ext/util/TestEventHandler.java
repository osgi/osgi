package org.osgi.test.cases.dmt.tc4.ext.util;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class TestEventHandler implements EventHandler {

	private final List<Event> eventList;

    public TestEventHandler() {
		this.eventList = new ArrayList<>();
    }

    @Override
	public void handleEvent(Event event) {
        synchronized (eventList) {
            eventList.add(event);
            eventList.notifyAll();
        }
    }

    public int getEventListSize() throws InterruptedException {
        synchronized (eventList) {
            return eventList.size();
        }
    }

    public Event getEvent(int index) throws InterruptedException {
        synchronized (eventList) {
            if (eventList.size() <= index) {
                eventList.wait(1000);
            }
            return eventList.get(index);
        }
    }
}
