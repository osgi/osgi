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
package org.osgi.test.cases.dmt.tc4.ext.junit;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtEvent;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.dmt.tc4.ext.util.ArrayAssert;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataMountPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDmtEventListener;
import org.osgi.test.cases.dmt.tc4.ext.util.TestEventHandler;
import org.osgi.test.support.sleep.Sleep;

public class InternalChangedEventTest extends DmtAdminTestCase {

	private static final String SESSION_ID = "session.id";

	private static final int ALL_TYPE = DmtEvent.ADDED | DmtEvent.COPIED
			| DmtEvent.DELETED | DmtEvent.RENAMED | DmtEvent.REPLACED //
			| DmtEvent.SESSION_CLOSED | DmtEvent.SESSION_OPENED;

	private TestDmtEventListener dmtEventListener;

	private ServiceRegistration<EventHandler>	eventHandlerRegistration;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Sleep.sleep(250L);
		getDmtAdmin();
	}

	@Override
	protected void tearDown() throws Exception {
		unregisterPlugins();
		closeDmtSession();

		if (dmtEventListener != null) {
			removeDmtEventListener(dmtEventListener);
		}

		if (eventHandlerRegistration != null) {
			eventHandlerRegistration.unregister();
		}

		super.tearDown();
	}

	public void testPostEventNullTypeWithNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, null, "B1", null);
			fail();
		} catch (IllegalArgumentException npe) {
		}
	}

	public void testPostEventNullTypeWithNodesAndNewNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, null, "B1", "C1", null);
			fail();
		} catch (IllegalArgumentException npe) {
		}
	}

	public void testPostEventWithNullNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED, null, null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_ADDED, new String[0],
				timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_ADDED, new String[0],
				timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostEventWithEmptyNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED, new String[0],
				null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_ADDED, new String[0],
				timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_ADDED, new String[0],
				timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostEventWithNodesContainsNull() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED, new String[] {
					"B1", null }, null);
			fail();
		} catch (IllegalArgumentException npe) {
		} catch (NullPointerException e) {
		}
	}

	public void testPostEventWithNodesContainsAbsoluteURI() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED,
					new String[] { "./B1" }, null);
			fail();
		} catch (IllegalArgumentException npe) {
		}
	}

	public void testPostEventWithNodesContainsInvalidURI() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED,
					new String[] { "/" }, null);
			fail();
		} catch (IllegalArgumentException npe) {
		}
	}

	public void testPostEventWithNullNewNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint
				.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, null, null, null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_RENAMED, new String[0],
				new String[0], timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_RENAMED, new String[0],
				new String[0], timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostEventWithEmptyNewNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, new String[0],
				new String[0], null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_RENAMED, new String[0],
				new String[0], timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_RENAMED, new String[0],
				new String[0], timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostEventWithNewNodesContainsNull() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED,
					new String[] { "B1" }, new String[] { "B2", null }, null);
			fail();
		} catch (IllegalArgumentException npe) {
		} catch (NullPointerException e) {
		}
	}

	public void testPostEventWithNewNodesContainsAbosluteURI() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED,
					new String[] { "B1" }, new String[] { "./B2" }, null);
			fail();
		} catch (IllegalArgumentException npe) {
		}
	}

	public void testPostEventWithNewNodesContainsInvalidURI() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED,
					new String[] { "B1" }, new String[] { "/" }, null);
			fail();
		} catch (IllegalArgumentException npe) {
		}
	}

	public void testPostEventWithNodesAndNewNodesDifferentLength()
			throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED,
					new String[] { "B1" }, new String[] { "B2", "B3" }, null);
			fail();
		} catch (IllegalArgumentException npe) {
		}
	}

	public void testPostEventWithNodesToRemovedMountPoint() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> regigstartionA1 = registerMountDataPlugin(
				pluginA1,
				"./A1");

		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put("key1", "value1");
		unregister(regigstartionA1);
		// try {
		// mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED, new String[] {
		// "./A1" }, null);
		// fail();
		// } catch (IllegalStateException exception) {
		// }
		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();
		assertEquals("initial event list of TestEventHandler must be empty!",
				0, handler.getEventListSize());

		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED,
				new String[] { "./A1" }, null);

		Event event = null;
		try {
			// waits for first event
			event = handler.getEvent(0);
		} catch (Exception e) {
			// can be ignored
		}
		assertNull("No event are expeced when mount point is removed!", event);

		// just check the size, the waiting is on the previous call
		assertEquals("No events are expected when mount point is removed.", 0,
				listener.getEventListSize());

	}

	public void testPostEventWithNodesAndNewNodesToRemovedMountPoint()
			throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> regigstartionA1 = registerMountDataPlugin(
				pluginA1,
				"./A1");

		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put("key1", "value1");

		unregister(regigstartionA1);
		// try {
		// mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
		// "./A1", "./A2" }, new String[] { "./B1", "./B2" }, null);
		// fail();
		// } catch (IllegalStateException exception) {
		// }

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();
		assertEquals("initial event list of TestEventHandler must be empty!",
				0, handler.getEventListSize());

		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"./A1", "./A2" }, new String[] { "./B1", "./B2" }, null);

		Event event = null;
		try {
			// waits for first event
			event = handler.getEvent(0);
		} catch (Exception e) {
			// can be ignored
		}
		assertNull("No event are expeced when mount point is removed!", event);

		// just check the size, the waiting is on the previous call
		assertEquals("No events are expected when mount point is removed.", 0,
				listener.getEventListSize());
	}

	public void testPostAddedEventWithNodesAndNewNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, DmtConstants.EVENT_TOPIC_ADDED, "B1", "C1",
					null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostDeletedEventWithNodesAndNewNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, DmtConstants.EVENT_TOPIC_DELETED, "B1", "C1",
					null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostSessionOpenedEventWithNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint,
					"org/osgi/service/dmt/DmtEvent/SESSION_OPENED", "B1", null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostSessionClosedEventWithNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint,
					"org/osgi/service/dmt/DmtEvent/SESSION_CLOSED", "B1", null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostUndefinedEventWithNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, "org/osgi/service/dmt/DmtEvent/UNDEFINED",
					"B1", null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostReplacedEventWithNodesAndNewNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, DmtConstants.EVENT_TOPIC_REPLACED, "B1",
					"C1", null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostCopiedEventWithNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, DmtConstants.EVENT_TOPIC_COPIED, "B1", null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostRenamedEventWithNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, DmtConstants.EVENT_TOPIC_RENAMED, "B1", null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostSessionOpenedEventWithNodesAndNewNodes()
			throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint,
					"org/osgi/service/dmt/DmtEvent/SESSION_OPENED", "B1", "C1",
					null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostSessionClosedEventWithNodesAndNewNodes()
			throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint,
					"org/osgi/service/dmt/DmtEvent/SESSION_CLOSED", "B1", "C1",
					null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testPostUndefinedEventWithNodesAndNewNodes() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		try {
			postEvent(mountPoint, "org/osgi/service/dmt/DmtEvent/UNDEFINED",
					"B1", "C1", null);
			fail();
		} catch (IllegalArgumentException iae) {
		}
	}

	public void testMultiMountPoints() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, new String[] { "./A0", "./A1",
				"./A2", "./A3", "./A4" });

		long timeStamp0 = System.currentTimeMillis();
		MountPoint mountPoint0 = pluginA1.getMountPointEvent(0).getMountPoint();
		long timeStamp1 = System.currentTimeMillis();
		MountPoint mountPoint1 = pluginA1.getMountPointEvent(1).getMountPoint();
		long timeStamp2 = System.currentTimeMillis();
		MountPoint mountPoint2 = pluginA1.getMountPointEvent(2).getMountPoint();
		long timeStamp3 = System.currentTimeMillis();
		MountPoint mountPoint3 = pluginA1.getMountPointEvent(3).getMountPoint();
		long timeStamp4 = System.currentTimeMillis();
		MountPoint mountPoint4 = pluginA1.getMountPointEvent(4).getMountPoint();

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		mountPoint0.postEvent(DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"B0", "C0" }, null);
		DmtEvent dmtEvent0 = listener.getDmtEvent(0);
		Event event0 = handler.getEvent(0);
		assertEvent(dmtEvent0, DmtEvent.ADDED, new String[] { "./A0/B0",
				"./A0/C0" });
		assertEvent(dmtEvent0, DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"./A0/B0", "./A0/C0" }, timeStamp0);
		assertEvent(event0, DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"./A0/B0", "./A0/C0" }, timeStamp0);

		mountPoint1.postEvent(DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"B1", "C1" }, null);
		DmtEvent dmtEvent1 = listener.getDmtEvent(1);
		Event event1 = handler.getEvent(1);
		assertEvent(dmtEvent1, DmtEvent.DELETED, new String[] { "./A1/B1",
				"./A1/C1" });
		assertEvent(dmtEvent1, DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp1);
		assertEvent(event1, DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp1);

		mountPoint2.postEvent(DmtConstants.EVENT_TOPIC_REPLACED, new String[] {
				"B2", "C2" }, null);
		DmtEvent dmtEvent2 = listener.getDmtEvent(2);
		Event event2 = handler.getEvent(2);
		assertEvent(dmtEvent2, DmtEvent.REPLACED, new String[] { "./A2/B2",
				"./A2/C2" });
		assertEvent(dmtEvent2, DmtConstants.EVENT_TOPIC_REPLACED, new String[] {
				"./A2/B2", "./A2/C2" }, timeStamp2);
		assertEvent(event2, DmtConstants.EVENT_TOPIC_REPLACED, new String[] {
				"./A2/B2", "./A2/C2" }, timeStamp2);

		mountPoint3.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"B3", "C3" }, new String[] { "D3", "E3" }, null);
		DmtEvent dmtEvent3 = listener.getDmtEvent(3);
		Event event3 = handler.getEvent(3);
		assertEvent(dmtEvent3, DmtEvent.RENAMED, new String[] { "./A3/B3",
				"./A3/C3" }, new String[] { "./A3/D3", "./A3/E3" });
		assertEvent(dmtEvent3, DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"./A3/B3", "./A3/C3" }, new String[] { "./A3/D3", "./A3/E3" },
				timeStamp3);
		assertEvent(event3, DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"./A3/B3", "./A3/C3" }, new String[] { "./A3/D3", "./A3/E3" },
				timeStamp3);

		mountPoint4.postEvent(DmtConstants.EVENT_TOPIC_COPIED, new String[] {
				"B4", "C4" }, new String[] { "D4", "E4" }, null);
		DmtEvent dmtEvent4 = listener.getDmtEvent(4);
		Event event4 = handler.getEvent(4);
		assertEvent(dmtEvent4, DmtEvent.COPIED, new String[] { "./A4/B4",
				"./A4/C4" }, new String[] { "./A4/D4", "./A4/E4" });
		assertEvent(dmtEvent4, DmtConstants.EVENT_TOPIC_COPIED, new String[] {
				"./A4/B4", "./A4/C4" }, new String[] { "./A4/D4", "./A4/E4" },
				timeStamp4);
		assertEvent(event4, DmtConstants.EVENT_TOPIC_COPIED, new String[] {
				"./A4/B4", "./A4/C4" }, new String[] { "./A4/D4", "./A4/E4" },
				timeStamp4);

		assertEquals(5, listener.getEventListSize());
		assertEquals(5, handler.getEventListSize());
	}

	public void testPostAddedEvent() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"B1", "C1" }, null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtEvent.ADDED, new String[] { "./A1/B1",
				"./A1/C1" });
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostDeletedEvent() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"B1", "C1" }, null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtEvent.DELETED, new String[] { "./A1/B1",
				"./A1/C1" });
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostRepalcedEvent() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"B1", "C1" }, null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtEvent.DELETED, new String[] { "./A1/B1",
				"./A1/C1" });
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"./A1/B1", "./A1/C1" }, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostRenamedEvent() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"B1", "C1" }, new String[] { "B2", "C2" }, null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtEvent.RENAMED, new String[] { "./A1/B1",
				"./A1/C1" }, new String[] { "./A1/B2", "./A1/C2" });
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"./A1/B1", "./A1/C1" }, new String[] { "./A1/B2", "./A1/C2" },
				timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"./A1/B1", "./A1/C1" }, new String[] { "./A1/B2", "./A1/C2" },
				timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostCopiedEvent() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_COPIED, new String[] {
				"B1", "C1" }, new String[] { "B2", "C2" }, null);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEvent(dmtEvent, DmtEvent.COPIED, new String[] { "./A1/B1",
				"./A1/C1" }, new String[] { "./A1/B2", "./A1/C2" });
		assertEvent(dmtEvent, DmtConstants.EVENT_TOPIC_COPIED, new String[] {
				"./A1/B1", "./A1/C1" }, new String[] { "./A1/B2", "./A1/C2" },
				timeStamp);

		Event event = handler.getEvent(0);
		assertEvent(event, DmtConstants.EVENT_TOPIC_COPIED, new String[] {
				"./A1/B1", "./A1/C1" }, new String[] { "./A1/B2", "./A1/C2" },
				timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostAddedEventWithOptionalProperties() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put("key1", "value1");
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"B1", "C1" }, eventProps);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEquals(DmtEvent.ADDED, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(null, dmtEvent.getNewNodes());

		ArrayAssert
				.assertEquivalenceArrays(new String[] {
						EventConstants.EVENT_TOPIC,
						SESSION_ID,
						DmtConstants.EVENT_PROPERTY_NODES, //
						EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
						EventConstants.BUNDLE_SYMBOLICNAME,
						EventConstants.BUNDLE_VERSION,
						EventConstants.BUNDLE_SIGNER, //
						EventConstants.TIMESTAMP, "key1" },
						dmtEvent.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_ADDED,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) dmtEvent
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", dmtEvent.getProperty("key1"));
		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, timeStamp);

		Event event = handler.getEvent(0);
		ArrayAssert.assertEquivalenceArrays(new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP, "key1" }, event.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_ADDED,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(new String[] { "./A1/B1", "./A1/C1" },
						(String[]) event
								.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) event
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", event.getProperty("key1"));
		assertEventBundle(event);
		assertTimeStamp(event, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostDeletedEventWithOptionalProperties() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put("key1", "value1");
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_DELETED, new String[] {
				"B1", "C1" }, eventProps);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEquals(DmtEvent.DELETED, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(null, dmtEvent.getNewNodes());

		ArrayAssert
				.assertEquivalenceArrays(new String[] {
						EventConstants.EVENT_TOPIC, //
						SESSION_ID,
						DmtConstants.EVENT_PROPERTY_NODES, //
						EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
						EventConstants.BUNDLE_SYMBOLICNAME,
						EventConstants.BUNDLE_VERSION,
						EventConstants.BUNDLE_SIGNER, //
						EventConstants.TIMESTAMP, "key1" },
						dmtEvent.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_DELETED,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) dmtEvent
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", dmtEvent.getProperty("key1"));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, timeStamp);

		Event event = handler.getEvent(0);
		ArrayAssert.assertEquivalenceArrays(new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP, "key1" }, event.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_DELETED,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(new String[] { "./A1/B1", "./A1/C1" },
						(String[]) event
								.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) event
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", event.getProperty("key1"));
		assertEventBundle(event);
		assertTimeStamp(event, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostReplacedEventWithOptionalProperties() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put("key1", "value1");
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_REPLACED, new String[] {
				"B1", "C1" }, eventProps);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEquals(DmtEvent.REPLACED, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(null, dmtEvent.getNewNodes());
		ArrayAssert
				.assertEquivalenceArrays(new String[] {
						EventConstants.EVENT_TOPIC, //
						SESSION_ID,
						DmtConstants.EVENT_PROPERTY_NODES, //
						EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
						EventConstants.BUNDLE_SYMBOLICNAME,
						EventConstants.BUNDLE_VERSION,
						EventConstants.BUNDLE_SIGNER, //
						EventConstants.TIMESTAMP, "key1" },
						dmtEvent.getPropertyNames());

		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) dmtEvent
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", dmtEvent.getProperty("key1"));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, timeStamp);

		Event event = handler.getEvent(0);
		ArrayAssert.assertEquivalenceArrays(new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP, "key1" }, event.getPropertyNames());

		assertEquals(DmtConstants.EVENT_TOPIC_REPLACED,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(new String[] { "./A1/B1", "./A1/C1" },
						(String[]) event
								.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) event
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", event.getProperty("key1"));

		assertEventBundle(event);
		assertTimeStamp(event, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostRenamedEventWithOptionalProperties() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put("key1", "value1");
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"B1", "C1" }, new String[] { "B2", "C2" }, eventProps);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEquals(DmtEvent.RENAMED, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, dmtEvent.getNewNodes());
		ArrayAssert
				.assertEquivalenceArrays(new String[] {
						EventConstants.EVENT_TOPIC, //
						SESSION_ID,
						DmtConstants.EVENT_PROPERTY_NODES,
						DmtConstants.EVENT_PROPERTY_NEW_NODES, //
						EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
						EventConstants.BUNDLE_SYMBOLICNAME,
						EventConstants.BUNDLE_VERSION,
						EventConstants.BUNDLE_SIGNER, //
						EventConstants.TIMESTAMP, "key1" },
						dmtEvent.getPropertyNames());

		assertEquals(DmtConstants.EVENT_TOPIC_RENAMED,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", dmtEvent.getProperty("key1"));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, timeStamp);

		Event event = handler.getEvent(0);
		ArrayAssert.assertEquivalenceArrays(new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES,
				DmtConstants.EVENT_PROPERTY_NEW_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP, "key1" }, event.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_RENAMED,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(new String[] { "./A1/B1", "./A1/C1" },
						(String[]) event
								.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, (String[]) event
						.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", event.getProperty("key1"));

		assertEventBundle(event);
		assertTimeStamp(event, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostCopiedEventWithOptionalProperties() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put("key1", "value1");
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_COPIED, new String[] {
				"B1", "C1" }, new String[] { "B2", "C2" }, eventProps);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEquals(DmtEvent.COPIED, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, dmtEvent.getNewNodes());

		ArrayAssert
				.assertEquivalenceArrays(new String[] {
						EventConstants.EVENT_TOPIC, //
						SESSION_ID,
						DmtConstants.EVENT_PROPERTY_NODES,
						DmtConstants.EVENT_PROPERTY_NEW_NODES, //
						EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
						EventConstants.BUNDLE_SYMBOLICNAME,
						EventConstants.BUNDLE_VERSION,
						EventConstants.BUNDLE_SIGNER, //
						EventConstants.TIMESTAMP, "key1" },
						dmtEvent.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_COPIED,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", dmtEvent.getProperty("key1"));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, timeStamp);

		Event event = handler.getEvent(0);
		ArrayAssert.assertEquivalenceArrays(new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES,
				DmtConstants.EVENT_PROPERTY_NEW_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP, "key1" }, event.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_COPIED,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(new String[] { "./A1/B1", "./A1/C1" },
						(String[]) event
								.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, (String[]) event
						.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", event.getProperty("key1"));

		assertEventBundle(event);
		assertTimeStamp(event, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostEventWithNodesOverlappedProperties() throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put(EventConstants.EVENT_TOPIC, "foo");
		eventProps.put(DmtConstants.EVENT_PROPERTY_NODES, "bar");
		eventProps.put(DmtConstants.EVENT_PROPERTY_NEW_NODES, "baz");
		eventProps.put("key1", "value1");
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_ADDED, new String[] {
				"B1", "C1" }, eventProps);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEquals(DmtEvent.ADDED, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(null, dmtEvent.getNewNodes());

		ArrayAssert
				.assertEquivalenceArrays(new String[] {
						EventConstants.EVENT_TOPIC, //
						SESSION_ID,
						DmtConstants.EVENT_PROPERTY_NODES, //
						EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
						EventConstants.BUNDLE_SYMBOLICNAME,
						EventConstants.BUNDLE_VERSION,
						EventConstants.BUNDLE_SIGNER, //
						EventConstants.TIMESTAMP, "key1" },
						dmtEvent.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_ADDED,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		assertEquals(null,
				dmtEvent.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", dmtEvent.getProperty("key1"));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, timeStamp);

		Event event = handler.getEvent(0);
		ArrayAssert.assertEquivalenceArrays(new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP, "key1" }, event.getPropertyNames());
		assertEquals(DmtConstants.EVENT_TOPIC_ADDED,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(new String[] { "./A1/B1", "./A1/C1" },
						(String[]) event
								.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		assertEquals(null,
				event.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", event.getProperty("key1"));

		assertEventBundle(event);
		assertTimeStamp(event, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	public void testPostEventWithNodesNewNodesAndOverlappedProperties()
			throws Exception {
		TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
		registerMountDataPlugin(pluginA1, "./A1");

		TestDmtEventListener listener = addEventListener();
		TestEventHandler handler = registerEventHandler();

		long timeStamp = System.currentTimeMillis();
		MountPoint mountPoint = pluginA1.getMountPointEvent(0).getMountPoint();
		Dictionary<String,Object> eventProps = new Hashtable<>();
		eventProps.put(EventConstants.EVENT_TOPIC, "foo");
		eventProps.put(DmtConstants.EVENT_PROPERTY_NODES, "bar");
		eventProps.put(DmtConstants.EVENT_PROPERTY_NEW_NODES, "baz");
		eventProps.put("key1", "value1");
		mountPoint.postEvent(DmtConstants.EVENT_TOPIC_RENAMED, new String[] {
				"B1", "C1" }, new String[] { "B2", "C2" }, eventProps);

		DmtEvent dmtEvent = listener.getDmtEvent(0);
		assertEquals(DmtEvent.RENAMED, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, dmtEvent.getNewNodes());
		ArrayAssert
				.assertEquivalenceArrays(new String[] {
						EventConstants.EVENT_TOPIC, //
						SESSION_ID,
						DmtConstants.EVENT_PROPERTY_NODES,
						DmtConstants.EVENT_PROPERTY_NEW_NODES, //
						EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
						EventConstants.BUNDLE_SYMBOLICNAME,
						EventConstants.BUNDLE_VERSION,
						EventConstants.BUNDLE_SIGNER, //
						EventConstants.TIMESTAMP, "key1" },
						dmtEvent.getPropertyNames());

		assertEquals(DmtConstants.EVENT_TOPIC_RENAMED,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B1", "./A1/C1" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, (String[]) dmtEvent
						.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", dmtEvent.getProperty("key1"));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, timeStamp);

		Event event = handler.getEvent(0);
		ArrayAssert.assertEquivalenceArrays(new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES,
				DmtConstants.EVENT_PROPERTY_NEW_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP, "key1" }, event.getPropertyNames());

		assertEquals(DmtConstants.EVENT_TOPIC_RENAMED,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(new String[] { "./A1/B1", "./A1/C1" },
						(String[]) event
								.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(
				new String[] { "./A1/B2", "./A1/C2" }, (String[]) event
						.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));
		assertEquals("value1", event.getProperty("key1"));

		assertEventBundle(event);
		assertTimeStamp(event, timeStamp);

		assertEquals(1, listener.getEventListSize());
		assertEquals(1, handler.getEventListSize());
	}

	private TestDmtEventListener addEventListener() {
		dmtEventListener = new TestDmtEventListener();
		addDmtEventListener(ALL_TYPE, ".", dmtEventListener);
		return dmtEventListener;
	}

	private TestEventHandler registerEventHandler() {
		TestEventHandler handler = new TestEventHandler();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(EventConstants.EVENT_TOPIC,
				"org/osgi/service/dmt/DmtEvent/*");
		eventHandlerRegistration = context.registerService(
				EventHandler.class, handler, serviceProps);
		return handler;
	}

	private static void assertEvent(DmtEvent dmtEvent, int type, String[] nodes) {
		assertEquals(type, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(nodes, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(null, dmtEvent.getNewNodes());
	}

	private static void assertEvent(DmtEvent dmtEvent, String topic,
			String[] nodes, long eventTime) {
		String[] expectedPropertyNames = new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP };
		ArrayAssert.assertEquivalenceArrays(expectedPropertyNames,
				dmtEvent.getPropertyNames());

		assertEquals(topic,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(nodes, (String[]) dmtEvent
				.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) dmtEvent
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, eventTime);
	}

	private static void assertEvent(DmtEvent dmtEvent, int type,
			String[] nodes, String[] newNodes) {
		String[] expectedPropertyNames = new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES,
				DmtConstants.EVENT_PROPERTY_NEW_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP };
		ArrayAssert.assertEquivalenceArrays(expectedPropertyNames,
				dmtEvent.getPropertyNames());

		assertEquals(type, dmtEvent.getType());
		assertInternalEventSessionId(dmtEvent);
		ArrayAssert.assertEquivalenceArrays(nodes, dmtEvent.getNodes());
		ArrayAssert.assertEquivalenceArrays(newNodes, dmtEvent.getNewNodes());
	}

	private static void assertEvent(DmtEvent dmtEvent, String topic,
			String[] nodes, String[] newNodes, long eventTime) {
		String[] expectedPropertyNames = new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES,
				DmtConstants.EVENT_PROPERTY_NEW_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP };
		ArrayAssert.assertEquivalenceArrays(expectedPropertyNames,
				dmtEvent.getPropertyNames());

		assertEquals(topic,
				(String) dmtEvent.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert.assertEquivalenceArrays(nodes, (String[]) dmtEvent
				.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(newNodes, (String[]) dmtEvent
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));

		assertEventBundle(dmtEvent);
		assertTimeStamp(dmtEvent, eventTime);
	}

	private static void assertEventBundle(DmtEvent dmtEvent) {
		Bundle bundle = context.getBundle();
		assertEquals(bundle, dmtEvent.getProperty(EventConstants.BUNDLE));
		assertEquals(Long.valueOf(bundle.getBundleId()),
				dmtEvent.getProperty(EventConstants.BUNDLE_ID));
		assertEquals(bundle.getSymbolicName(),
				dmtEvent.getProperty(EventConstants.BUNDLE_SYMBOLICNAME));
		assertEquals(bundle.getVersion(),
				dmtEvent.getProperty(EventConstants.BUNDLE_VERSION));
	}

	private static void assertTimeStamp(DmtEvent dmtEvent, long eventTime) {
		long timeStamp = ((Long) dmtEvent.getProperty(EventConstants.TIMESTAMP))
				.longValue();
		assertTrue(eventTime <= timeStamp);

		long currentTime = System.currentTimeMillis();
		assertTrue(timeStamp <= currentTime);
	}

	private static void assertEvent(Event event, String topic, String[] nodes,
			long eventTime) {
		String[] expectedPropertyNames = new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP };
		ArrayAssert.assertEquivalenceArrays(expectedPropertyNames,
				event.getPropertyNames());

		assertEquals(topic,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(nodes, (String[]) event
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(null, (String[]) event
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));

		assertEventBundle(event);
		assertTimeStamp(event, eventTime);
	}

	private static void assertEvent(Event event, String topic, String[] nodes,
			String[] newNodes, long eventTime) {
		String[] expectedPropertyNames = new String[] {
				EventConstants.EVENT_TOPIC, //
				SESSION_ID,
				DmtConstants.EVENT_PROPERTY_NODES,
				DmtConstants.EVENT_PROPERTY_NEW_NODES, //
				EventConstants.BUNDLE, EventConstants.BUNDLE_ID,
				EventConstants.BUNDLE_SYMBOLICNAME,
				EventConstants.BUNDLE_VERSION, EventConstants.BUNDLE_SIGNER, //
				EventConstants.TIMESTAMP };
		ArrayAssert.assertEquivalenceArrays(expectedPropertyNames,
				event.getPropertyNames());

		assertEquals(topic,
				(String) event.getProperty(EventConstants.EVENT_TOPIC));
		ArrayAssert
				.assertEquivalenceArrays(nodes, (String[]) event
						.getProperty(DmtConstants.EVENT_PROPERTY_NODES));
		ArrayAssert.assertEquivalenceArrays(newNodes, (String[]) event
				.getProperty(DmtConstants.EVENT_PROPERTY_NEW_NODES));

		assertEventBundle(event);
		assertTimeStamp(event, eventTime);
	}

	private static void assertEventBundle(Event event) {
		Bundle bundle = context.getBundle();
		assertEquals(bundle, event.getProperty(EventConstants.BUNDLE));
		assertEquals(Long.valueOf(bundle.getBundleId()),
				event.getProperty(EventConstants.BUNDLE_ID));
		assertEquals(bundle.getSymbolicName(),
				event.getProperty(EventConstants.BUNDLE_SYMBOLICNAME));
		assertEquals(bundle.getVersion(),
				event.getProperty(EventConstants.BUNDLE_VERSION));
	}

	private static void assertTimeStamp(Event event, long eventTime) {
		long timeStamp = ((Long) event.getProperty(EventConstants.TIMESTAMP))
				.longValue();
		assertTrue(eventTime <= timeStamp);

		long currentTime = System.currentTimeMillis();
		assertTrue(timeStamp <= currentTime);
	}

	private void postEvent(MountPoint mountPoint, String topic, String node,
			Dictionary<String,Object> props) {
		mountPoint.postEvent(topic, new String[] { node }, props);
	}

	private void postEvent(MountPoint mountPoint, String topic, String node,
			String newNode, Dictionary<String,Object> props) {
		mountPoint.postEvent(topic, new String[] { node },
				new String[] { newNode }, props);
	}

	private static void assertInternalEventSessionId(DmtEvent event) {
		assertEquals(-1, event.getSessionId());
	}
}
