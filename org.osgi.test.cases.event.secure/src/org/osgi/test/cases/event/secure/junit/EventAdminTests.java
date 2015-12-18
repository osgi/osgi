/*
 * Copyright (c) OSGi Alliance (2004, 2015). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.event.secure.junit;

import static org.osgi.test.support.OSGiTestCaseProperties.*;

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
