/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008, 2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.resourcemanagement.junit;

import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceContextEvent;
import org.osgi.service.resourcemanagement.ResourceManager;
import org.osgi.service.resourcemanagement.ResourceMonitorException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 *
 */
public class ResourceContextTestCase extends DefaultTestBundleControl {

	/**
	 * bundleContext
	 */
	private BundleContext context;

	/**
	 * resource manager
	 */
	private ResourceManager resourceManager;

	/**
	 * resource context listener
	 */
	private TestResourceContextListener resourceContextListener;

	protected void tearDown() throws Exception {
		super.tearDown();

		resourceContextListener.stop();

		// delete all existing ResourceContext
		ResourceContext[] existingContexts = resourceManager.listContext();
		for (int i = 0; i < existingContexts.length; i++) {
			ResourceContext currentResourceContext = existingContexts[i];
			currentResourceContext.removeContext(null);
		}
	}

	protected void setUp() throws Exception {
		super.setUp();

		resourceContextListener = new TestResourceContextListener();
		resourceContextListener.start(context);

		// delete all existing ResourceContext
		ResourceContext[] existingContexts = resourceManager.listContext();
		for (int i = 0; i < existingContexts.length; i++) {
			ResourceContext currentResourceContext = existingContexts[i];
			currentResourceContext.removeContext(null);
		}
	}

	public void setBundleContext(BundleContext context) {
		this.context = context;

		ServiceReference resourceManagerSr = context
				.getServiceReference(ResourceManager.class.getName());
		if (resourceManagerSr != null) {
			resourceManager = (ResourceManager) context.getService(resourceManagerSr);
		}
	}

	/**
	 * Test remove resource context without destination. Check a
	 * ResourceContextEvent is sent.
	 */
	public void testRemoveContextWithoutDestination() {
		final String name = "context1";
		boolean exception = false;

		// create the resource context
		ResourceContext resourceContext = resourceManager.createContext(name,
				null);

		// delete this new resourceContext
		resourceContext.removeContext(null);

		// check existing ResourceContext
		assertTrue(resourceManager.listContext().length == 0);
		assertNull(resourceManager.getContext(name));

		// check the resource context name is still accessible
		assertTrue(resourceContext.getName().equals(name));

		// check it is not possible to add bundle
		exception = false;
		try {
			resourceContext.addBundle(2);
		} catch (IllegalStateException e) {
			exception = true;
		}
		assertTrue(exception);

		// check is is not possible to add monitor
		exception = false;
		try {
			resourceContext.addResourceMonitor(new FakeResourceMonitor());
		} catch (IllegalStateException e) {
			exception = true;
		} catch (ResourceMonitorException e) {
			fail("Exception not expected");
		}
		assertTrue(exception);

		// check a ResourceContextEvent has been sent due to deletion
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull(lastEvent);
		assertTrue(lastEvent.getContext().equals(resourceContext));
		assertTrue(lastEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_DELETED);

	}

	/**
	 * 
	 */
	public void testRemoveContextWithDestination() {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		// create ResourceContexts
		ResourceContext resourceContext1 = resourceManager.createContext(name1,
				null);
		ResourceContext resourceContext2 = resourceManager.createContext(name2,
				null);
		log("resourceContext1:" + resourceContext1);
		log("resourceContext2:" + resourceContext2);

		// checks the array of existing ResourceContext
		assertTrue(resourceManager.listContext().length == 2);

		// associate bundleId with resourceContext1
		resourceContext1.addBundle(bundleId);

		// associate a monitor

		// delete resourceContext1 and specify resourceContext2 as destination
		resourceContext1.removeContext(resourceContext2);
		// deleting resourceContext1 MUST generate 6 events (in this order):
		// - 1 for creating resourceContext1
		// - 1 for creating resourceContext2
		// - 1 for adding bundleId to resourceContext1
		// - 1 for removing bundleId from resourceContext1
		// - 1 for adding bundleId to resourceContext2
		// - 1 for removing resourceContext1

		// checks only context2 is only the one existing ResourceContext
		ResourceContext[] existingContexts = resourceManager.listContext();
		assertTrue(existingContexts.length == 1);
		assertTrue(existingContexts[0].getName().equals(name2));

		// check resourceContext1 (no associated bundle and no associated
		// monitors)
		long[] rc1BundleIds = resourceContext1.getBundleIds();
		assertTrue(rc1BundleIds.length == 0);
		// ResourceMonitor[] rc1ResourceMonitors =
		// resourceContext1.getMonitors();
		// assertTrue(rc1ResourceMonitors.length == 0);

		// check bundleId belongs to resourceContext2
		long[] rc2BundleIds = resourceContext2.getBundleIds();
		assertTrue(rc2BundleIds.length == 1);
		assertTrue(rc2BundleIds[0] == bundleId);

		List receivedEvents = resourceContextListener.getReceivedEvents();

		log("nb of receivedEvent : " + receivedEvents.size());
		assertTrue(receivedEvents.size() == 6);

		// first received event is a BUNDLE_REMOVED ResourceContextEvent has
		// been received
		ResourceContextEvent firstEvent = (ResourceContextEvent) receivedEvents
				.get(3);
		assertNotNull(firstEvent);
		assertTrue(firstEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(firstEvent.getContext().equals(resourceContext1));
		assertTrue(firstEvent.getBundleId() == bundleId);

		// check a BUNDLE_ADDED ResourceContextEvent has been received (second
		// received event)
		ResourceContextEvent secondEvent = (ResourceContextEvent) receivedEvents
				.get(4);
		assertNotNull(secondEvent);
		assertTrue(secondEvent.getType() == ResourceContextEvent.BUNDLE_ADDED);
		assertTrue(secondEvent.getContext().equals(resourceContext2));
		assertTrue(secondEvent.getBundleId() == bundleId);

		// check the third received event is a RESOURCE_CONTEXT_DELETED event
		ResourceContextEvent thirdEvent = (ResourceContextEvent) receivedEvents
				.get(5);
		assertNotNull(thirdEvent);
		assertTrue(thirdEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_DELETED);
		assertTrue(thirdEvent.getContext().equals(resourceContext1));
	}

	/**
	 * Test remove a context and specify a deleted context as destination. The
	 * to be deleted context is deleted.
	 */
	public void testRemoveContextWithDeletedDestination() {
		final String name1 = "name1";
		final String name2 = "name2";
		final long bundleId = 1;

		// create ResourceContexts
		ResourceContext resourceContext1 = resourceManager.createContext(name1,
				null);
		ResourceContext resourceContext2 = resourceManager.createContext(name2,
				null);

		// add bundleId to resourceContext1
		resourceContext1.addBundle(bundleId);

		// delete resourceContext2
		resourceContext2.removeContext(null);

		// try to delete resourceContext1 with resourceContext2 as destination
		// resourceContext2 has been previously deleted ==> ex
		resourceContext1.removeContext(resourceContext2);

		// try to add bundleId to resourceContext1 => expect a RuntimeException
		boolean exception = false;
		try {
			resourceContext1.addBundle(bundleId);
		} catch (RuntimeException e) {
			exception = true;
		}
		assertTrue(exception);

		// check this no existing ResourceContext
		assertTrue(resourceManager.listContext().length == 0);

		// check events
		List events = resourceContextListener.getReceivedEvents();
		// 6 events MUST be emitted (in this order) :
		// - creation of context1
		// - creation of context2
		// - adding of bundle 1 to context1
		// - deletion of context2
		// - removing of bundle1 from context1
		// - deletion of context1
		assertTrue(events.size() == 6);

		// event RESOURCE_CONTEXT_CREATED for context1
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_CREATED);
		assertTrue(event.getContext().getName().equals(name1));

		// event RESOURCE_CONTEXT_CREATED for context2
		event = (ResourceContextEvent) events.get(1);
		assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_CREATED);
		assertTrue(event.getContext().getName().equals(name2));

		// event BUNDLE_ADDED for bundle1 and context1
		event = (ResourceContextEvent) events.get(2);
		assertTrue(event.getType() == ResourceContextEvent.BUNDLE_ADDED);
		assertTrue(event.getContext().getName().equals(name1));
		assertTrue(event.getBundleId() == bundleId);

		// event RESOURCE_CONTEXT_DELETED for context2
		event = (ResourceContextEvent) events.get(3);
		assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_DELETED);
		assertTrue(event.getContext().getName().equals(name2));

		// event BUNDLE_REMOVED from context1
		event = (ResourceContextEvent) events.get(4);
		assertTrue(event.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(event.getContext().getName().equals(name1));
		assertTrue(event.getBundleId() == bundleId);

		// event RESOURCE_CONTEXT_REMOVED for context2
		event = (ResourceContextEvent) events.get(5);
		assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_DELETED);
		assertTrue(event.getContext().getName().equals(name1));

	}

	/**
	 * Test addBundle
	 */
	public void testAddBundle() {
		final String name = "context1";
		final long bundleId = 1l;

		// create ResourceContext
		ResourceContext resourceContext = resourceManager.createContext(name,
				null);

		// add bundle 1 to this context
		resourceContext.addBundle(bundleId);

		// check bundle 1 has been associated to this context
		long[] bundleIds = resourceContext.getBundleIds();
		assertTrue(bundleIds.length == 1);
		assertTrue(bundleIds[0] == bundleId);

		// check a ResourceContextEvent has been sent
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull(lastEvent);
		assertTrue(lastEvent.getType() == ResourceContextEvent.BUNDLE_ADDED);
		assertTrue(lastEvent.getContext().equals(resourceContext));
		assertTrue(lastEvent.getBundleId() == bundleId);
	}

	/**
	 * Test add a bundle twice. Expect a RuntimeException.
	 */
	public void testAddBundleTwice() {
		final String name = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		// create ResourceContext
		ResourceContext resourceContext = resourceManager.createContext(name,
				null);
		ResourceContext resourceContext2 = resourceManager.createContext(name2,
				null);

		// add bundle 1 to this context
		resourceContext.addBundle(bundleId);
		long[] bundleIds = resourceContext.getBundleIds();
		assertTrue(bundleIds.length == 1);
		assertTrue(bundleIds[0] == bundleId);

		boolean exception = false;
		try {
			resourceContext2.addBundle(bundleId);
		} catch (RuntimeException e) {
			exception = true;
		}
		assertTrue(exception);

		// check that bundle1 is not associated with context2
		assertTrue(resourceContext2.getBundleIds().length == 0);

		// check that bundle1 is still associated with context1
		bundleIds = resourceContext.getBundleIds();
		assertTrue(bundleIds.length == 1);
		assertTrue(bundleIds[0] == bundleId);

	}

	/**
	 * Test remove a bundle from a ResourceContext. Check a
	 * {@link ResourceContextEvent} is received.
	 */
	public void testRemoveBundle() {
		final String name = "context1";
		final long bundleId = 1;

		// create ResourceContext
		ResourceContext resourceContext = resourceManager.createContext(name,
				null);

		// add bundle 1
		resourceContext.addBundle(bundleId);

		// remove bundleId from resourceContext
		resourceContext.removeBundle(bundleId);

		// check list of bundles
		assertNotNull(resourceContext.getBundleIds());
		assertTrue(resourceContext.getBundleIds().length == 0);

		// check a ResourceContextEvent has been received
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull(lastEvent);
		assertTrue(lastEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(lastEvent.getContext().equals(resourceContext));
		assertTrue(lastEvent.getBundleId() == bundleId);

		// try to remove it again ==> expect a RuntimeException
		boolean exception = false;
		try {
			resourceContext.removeBundle(bundleId);
		} catch (RuntimeException e) {
			exception = true;
		}
		assertTrue(exception);

	}



	/**
	 * Test remove a bundle from context with a destination. Expect one
	 * ResourceContextEvent from removing the bundle and one more for adding it
	 * into another ResourceContext.
	 */
	public void testRemoveBundleWithDestination() {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		// create two Resource Context
		ResourceContext resourceContext1 = resourceManager.createContext(name1,
				null);
		ResourceContext resourceContext2 = resourceManager.createContext(name2,
				null);

		// add bundleId to context1
		resourceContext1.addBundle(bundleId);

		// remove bundleId from context1 with destination = context2
		resourceContext1.removeBundle(bundleId, resourceContext2);

		// check context1 has no associated bundles
		assertTrue(resourceContext1.getBundleIds().length == 0);

		// check context2 has one associated bundle (i.e bundleId)
		long[] context2BundleIds = resourceContext2.getBundleIds();
		assertTrue(context2BundleIds.length == 1);
		assertTrue(context2BundleIds[0] == bundleId);

		// check a ResourceContextEvent has been received about the removed
		// bundle (should be the last last received event)
		ResourceContextEvent removedEvent = resourceContextListener
				.getLastLastEvent();
		assertNotNull(removedEvent);
		assertTrue(removedEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(removedEvent.getBundleId() == bundleId);
		assertTrue(removedEvent.getContext().equals(resourceContext1));

		// check a ResourceContextEvent has been received about the newly added
		// bundleId
		ResourceContextEvent addedEvent = resourceContextListener
				.getLastEvent();
		assertNotNull(addedEvent);
		assertTrue(addedEvent.getType() == ResourceContextEvent.BUNDLE_ADDED);
		assertTrue(addedEvent.getBundleId() == bundleId);
		assertTrue(addedEvent.getContext().equals(resourceContext2));

	}

	/**
	 * Test remove bundle with null destination. Expect a REMOVED_BUNDLE event.
	 */
	public void testRemoveBundleWithNullDestination() {
		final String name = "context1";
		final long bundleId = 1;

		// create ResourceContext
		ResourceContext resourceContext = resourceManager.createContext(name,
				null);

		// add bundleId to resourceContext
		resourceContext.addBundle(bundleId);

		// remove it with destination null
		resourceContext.removeBundle(bundleId, null);

		// check list of bundle
		long[] bundleIds = resourceContext.getBundleIds();
		assertNotNull(bundleIds);
		assertTrue(bundleIds.length == 0);

		// check that a REMOVED_BUNDLE event has been received
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull(lastEvent);
		assertTrue(lastEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(lastEvent.getContext().equals(resourceContext));
		assertTrue(lastEvent.getBundleId() == bundleId);

	}

	/**
	 * Test remove bundle with a deleted resource context as destination. Expect
	 * a RuntimeException when invoking
	 * {@link ResourceContext#removeBundle(long, ResourceContext)}. The bundle
	 * is correctly removed.
	 */
	public void testRemoveBundleWithDeletedResourceContextDestination() {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		// create ResourceContexts
		ResourceContext resourceContext1 = resourceManager.createContext(name1,
				null);
		ResourceContext resourceContext2 = resourceManager.createContext(name2,
				null);

		// add bundleId to resourceContext1
		resourceContext1.addBundle(bundleId);

		// delete resourceContext2
		resourceContext2.removeContext(null);

		// try to remove bundleId from resourceContext1 and associate it with
		// deleted ResourceContext2 ==> expect a RuntimeException
		boolean exception = false;
		try {
			resourceContext1.removeBundle(bundleId, resourceContext2);
		} catch (RuntimeException e) {
			exception = true;
		}
		assertTrue(exception);

		// check bundleId has been removed from resourceContext1
		long[] resourceContext1bundleIds = resourceContext1.getBundleIds();
		assertTrue(resourceContext1bundleIds.length == 0);

		// check a REMOVED_BUNDLE ResourceContextEvent event has been received
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull(lastEvent);
		assertTrue(lastEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
		assertTrue(lastEvent.getContext().equals(resourceContext1));
		assertTrue(lastEvent.getBundleId() == bundleId);
	}

}
