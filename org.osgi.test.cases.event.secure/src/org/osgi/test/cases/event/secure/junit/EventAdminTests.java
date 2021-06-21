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
package org.osgi.test.cases.event.secure.junit;

import static org.osgi.test.support.OSGiTestCaseProperties.getScaling;
import static org.osgi.test.support.OSGiTestCaseProperties.getTimeout;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServicePermission;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.event.secure.service.ReceiverService;
import org.osgi.test.cases.event.secure.service.SenderService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Secure EventAdmin tests.
 *
 * @author $Id$
 */
public class EventAdminTests extends DefaultTestBundleControl {

	EventAdmin											eventAdmin;
	PermissionAdmin										permissionAdmin;
	ServiceTracker<SenderService, SenderService>		senderServiceTracker;
	ServiceTracker<ReceiverService, ReceiverService>	receiverServiceTracker;
	private Bundle										tb1;
	private Bundle										tb2;
	private long										timeout	= getTimeout() * getScaling();

	/**
	 * Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone.
	 *
	 * @throws Exception
	 */
	protected void setUp() throws Exception {
		tb1 = installBundle("tb1.jar");
		tb2 = installBundle("tb2.jar");
		eventAdmin = getService(EventAdmin.class);
		permissionAdmin = getService(PermissionAdmin.class);
		senderServiceTracker = new ServiceTracker<SenderService, SenderService>(getContext(), SenderService.class,
				null);
		senderServiceTracker.open();
		receiverServiceTracker = new ServiceTracker<ReceiverService, ReceiverService>(getContext(),
				ReceiverService.class, null);
		receiverServiceTracker.open();
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * tearDown is never reached.
	 *
	 * @throws Exception
	 */
	protected void tearDown() throws Exception {
		senderServiceTracker.close();
		receiverServiceTracker.close();
		tb1.stop();
		tb2.stop();
		uninstallBundle(tb1);
		uninstallBundle(tb2);
		permissionAdmin.setPermissions(tb1.getLocation(), null);
		permissionAdmin.setPermissions(tb2.getLocation(), null);
		ungetAllServices();
	}

	public void testSendSuccess() throws Exception {
		setPermissions(tb1,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), SenderService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventAdmin.class.getName(),
								ServicePermission.GET),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/test/cases/event/*",
						TopicPermission.PUBLISH)});

		setPermissions(tb2,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), ReceiverService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventHandler.class.getName(),
								ServicePermission.REGISTER),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/test/cases/event/*",
						TopicPermission.SUBSCRIBE)});

		tb1.start();
		tb2.start();

		SenderService senderService = senderServiceTracker.waitForService(timeout);
		assertNotNull("missing SenderService", senderService);

		ReceiverService receiverService = receiverServiceTracker.waitForService(timeout);
		assertNotNull("missing ReceiverService", receiverService);

		String topic = "org/osgi/test/cases/event/ACTION1";
		receiverService.setTopics(topic);
		Event event1 = new Event(topic, emptyMap());
		senderService.sendEvent(event1);
		List<Event> received = receiverService.getLastReceivedEvents();
		assertEquals("wrong number of events", 1, received.size());
		Event e = received.get(0);
		assertEquals("event has wrong topic", topic, e.getTopic());
	}

	public void testPostSuccess() throws Exception {
		setPermissions(tb1,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), SenderService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventAdmin.class.getName(),
								ServicePermission.GET),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/test/cases/event/*",
						TopicPermission.PUBLISH)});

		setPermissions(tb2,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), ReceiverService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventHandler.class.getName(),
								ServicePermission.REGISTER),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/test/cases/event/*",
						TopicPermission.SUBSCRIBE)});

		tb1.start();
		tb2.start();

		SenderService senderService = senderServiceTracker.waitForService(timeout);
		assertNotNull("missing SenderService", senderService);

		ReceiverService receiverService = receiverServiceTracker.waitForService(timeout);
		assertNotNull("missing ReceiverService", receiverService);

		String topic = "org/osgi/test/cases/event/ACTION1";
		receiverService.setTopics(topic);
		Event event1 = new Event(topic, emptyMap());
		senderService.postEvent(event1);
		Sleep.sleep(2000L * getScaling());
		List<Event> received = receiverService.getLastReceivedEvents();
		assertEquals("wrong number of events", 1, received.size());
		Event e = received.get(0);
		assertEquals("event has wrong topic", topic, e.getTopic());
	}

	public void testSendNoPublishPermission() throws Exception {
		setPermissions(tb1,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), SenderService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventAdmin.class.getName(),
								ServicePermission.GET),
				new PermissionInfo(TopicPermission.class.getName(), "xorg/osgi/test/cases/event/*",
						TopicPermission.PUBLISH)});

		setPermissions(tb2,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), ReceiverService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventHandler.class.getName(),
								ServicePermission.REGISTER),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/test/cases/event/*",
						TopicPermission.SUBSCRIBE)});

		tb1.start();
		tb2.start();

		SenderService senderService = senderServiceTracker.waitForService(timeout);
		assertNotNull("missing SenderService", senderService);

		ReceiverService receiverService = receiverServiceTracker.waitForService(timeout);
		assertNotNull("missing ReceiverService", receiverService);

		String topic = "org/osgi/test/cases/event/ACTION1";
		receiverService.setTopics(topic);
		Event event1 = new Event(topic, emptyMap());
		try {
			senderService.sendEvent(event1);
			failException("sent event without permission", SecurityException.class);
		}
		catch (SecurityException e) {
			// expected
		}
		List<Event> received = receiverService.getLastReceivedEvents();
		assertEquals("wrong number of events", 0, received.size());
	}

	public void testPostNoPublishPermission() throws Exception {
		setPermissions(tb1,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), SenderService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventAdmin.class.getName(),
								ServicePermission.GET),
				new PermissionInfo(TopicPermission.class.getName(), "xorg/osgi/test/cases/event/*",
						TopicPermission.PUBLISH)});

		setPermissions(tb2,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), ReceiverService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventHandler.class.getName(),
								ServicePermission.REGISTER),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/test/cases/event/*",
						TopicPermission.SUBSCRIBE)});

		tb1.start();
		tb2.start();

		SenderService senderService = senderServiceTracker.waitForService(timeout);
		assertNotNull("missing SenderService", senderService);

		ReceiverService receiverService = receiverServiceTracker.waitForService(timeout);
		assertNotNull("missing ReceiverService", receiverService);

		String topic = "org/osgi/test/cases/event/ACTION1";
		receiverService.setTopics(topic);
		Event event1 = new Event(topic, emptyMap());
		try {
			senderService.postEvent(event1);
			failException("posted event without permission", SecurityException.class);
		}
		catch (SecurityException e) {
			// expected
		}
		Sleep.sleep(2000L * getScaling());
		List<Event> received = receiverService.getLastReceivedEvents();
		assertEquals("wrong number of events", 0, received.size());
	}

	public void testReceiveNoSubscribePermission() throws Exception {
		setPermissions(tb1,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), SenderService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventAdmin.class.getName(),
								ServicePermission.GET),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/test/cases/event/*",
						TopicPermission.PUBLISH)});

		setPermissions(tb2,
				new PermissionInfo[] {
						new PermissionInfo(ServicePermission.class.getName(), ReceiverService.class.getName(),
								ServicePermission.REGISTER),
						new PermissionInfo(ServicePermission.class.getName(), EventHandler.class.getName(),
								ServicePermission.REGISTER),
				new PermissionInfo(TopicPermission.class.getName(), "xorg/osgi/test/cases/event/*",
						TopicPermission.SUBSCRIBE)});

		tb1.start();
		tb2.start();

		SenderService senderService = senderServiceTracker.waitForService(timeout);
		assertNotNull("missing SenderService", senderService);

		ReceiverService receiverService = receiverServiceTracker.waitForService(timeout);
		assertNotNull("missing ReceiverService", receiverService);

		String topic = "org/osgi/test/cases/event/ACTION1";
		receiverService.setTopics(topic);
		Event event1 = new Event(topic, emptyMap());
		senderService.sendEvent(event1);
		List<Event> received = receiverService.getLastReceivedEvents();
		assertEquals("wrong number of events", 0, received.size());
	}

	private void setPermissions(Bundle bundle, PermissionInfo[] newPerm) {
		permissionAdmin.setPermissions(bundle.getLocation(), newPerm);

		PermissionInfo[] perms = permissionAdmin.getPermissions(bundle.getLocation());
		pass("Permissions of [" + bundle.getLocation() + "]");
		for (int i = 0; i < perms.length; i++) {
			pass("permission [" + i + "]: " + perms[i]);
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> emptyMap() {
		return Collections.EMPTY_MAP;
	}
}
