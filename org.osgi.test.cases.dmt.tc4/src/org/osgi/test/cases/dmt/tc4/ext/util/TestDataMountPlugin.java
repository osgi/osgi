package org.osgi.test.cases.dmt.tc4.ext.util;

import static junit.framework.TestCase.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
public class TestDataMountPlugin extends TestDataPlugin implements MountPlugin {

    private final List eventList;

    public TestDataMountPlugin() {
        this.eventList = new ArrayList();
    }

    public void mountPointAdded(MountPoint mountPoint) {
        synchronized (eventList) {
            eventList.add(new MountPointEvent(MountPointEvent.MOUNT_POINTS_ADDED, mountPoint));
            eventList.notifyAll();
        }
    }

    public void mountPointRemoved(MountPoint mountPoint) {
        synchronized (eventList) {
            eventList.add(new MountPointEvent(MountPointEvent.MOUNT_POINTS_REMOVED, mountPoint));
            eventList.notifyAll();
        }
    }

    public int getMountPointEventSize() throws InterruptedException {
        synchronized (eventList) {
            return eventList.size();
        }
    }

    public MountPointEvent getMountPointEvent(int index) throws InterruptedException {
        synchronized (eventList) {
            if (eventList.size() <= index) {
                eventList.wait(1000);
            }
			assertTrue("No expected mount point", index < eventList.size());
            return (MountPointEvent) eventList.get(index);
        }
    }
}
