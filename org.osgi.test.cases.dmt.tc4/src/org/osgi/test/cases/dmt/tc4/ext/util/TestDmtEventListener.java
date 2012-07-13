package org.osgi.test.cases.dmt.tc4.ext.util;

import org.osgi.service.dmt.DmtEvent;
import org.osgi.service.dmt.DmtEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestDmtEventListener implements DmtEventListener {

    private final List eventList;

    public TestDmtEventListener() {
        this.eventList = new ArrayList();
    }

    public void changeOccurred(DmtEvent event) {
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

    public DmtEvent getDmtEvent(int index) throws InterruptedException {
        synchronized (eventList) {
            while (eventList.size() <= index) {
                eventList.wait();
            }
            return (DmtEvent) eventList.get(index);
        }
    }
}
