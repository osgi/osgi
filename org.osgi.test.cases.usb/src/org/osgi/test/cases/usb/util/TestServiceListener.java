package org.osgi.test.cases.usb.util;

import java.util.LinkedList;
import java.util.List;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class TestServiceListener implements ServiceListener {

    private List list = new LinkedList();
    private int type;

    public TestServiceListener(int type) {
        this.type = type;
    }

    public void serviceChanged(ServiceEvent event) {
        if (event.getType() == type) {
            ServiceReference ref = event.getServiceReference();
            list.add(ref);

            System.out.println("Callback: " + ref);
        }
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public ServiceReference get(int index) {
        return (ServiceReference)list.get(index);
    }
}
