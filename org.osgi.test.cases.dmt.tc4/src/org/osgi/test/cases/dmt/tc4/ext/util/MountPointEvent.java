package org.osgi.test.cases.dmt.tc4.ext.util;

import org.osgi.service.dmt.spi.MountPoint;

public class MountPointEvent {

    public static final int MOUNT_POINTS_ADDED = 0x00;

    public static final int MOUNT_POINTS_REMOVED = 0x01;

    private final int type;

    private final MountPoint mountPoint;

    public MountPointEvent(int type, MountPoint mountPoint) {
        this.type = type;
        this.mountPoint = mountPoint;
    }

    public int getType() {
        return type;
    }

    public MountPoint getMountPoint() {
        return mountPoint;
    }
}
