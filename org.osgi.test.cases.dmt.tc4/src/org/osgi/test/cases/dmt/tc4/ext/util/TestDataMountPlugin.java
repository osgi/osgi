/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.dmt.tc4.ext.util;

import static junit.framework.TestCase.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
public class TestDataMountPlugin extends TestDataPlugin implements MountPlugin {

	private final List<MountPointEvent> eventList;

    public TestDataMountPlugin() {
		this.eventList = new ArrayList<>();
    }

    @Override
	public void mountPointAdded(MountPoint mountPoint) {
        synchronized (eventList) {
            eventList.add(new MountPointEvent(MountPointEvent.MOUNT_POINTS_ADDED, mountPoint));
            eventList.notifyAll();
        }
    }

    @Override
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
            return eventList.get(index);
        }
    }
}
