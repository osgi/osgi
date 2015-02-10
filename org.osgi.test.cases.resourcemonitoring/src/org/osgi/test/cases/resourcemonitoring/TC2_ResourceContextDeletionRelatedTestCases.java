/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.test.cases.resourcemonitoring;

import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextException;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.cases.resourcemonitoring.utils.FakeResourceMonitor;
import org.osgi.test.cases.resourcemonitoring.utils.ResourceContextListenerTestImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author $Id$
 */
public class TC2_ResourceContextDeletionRelatedTestCases extends DefaultTestBundleControl {

	/**
	 * bundleContext
	 */
	private BundleContext					context;

	/**
	 * ResourceMonitoringService
	 */
	private ResourceMonitoringService		resourceMonitoringService;

	/**
	 * resource context listener
	 */
	private ResourceContextListenerTestImpl	resourceContextListener;

	public void setBundleContext(BundleContext context) {
		this.context = context;

		ServiceReference resourceMonitoringServiceSr = context
				.getServiceReference(ResourceMonitoringService.class.getName());
		if (resourceMonitoringServiceSr != null) {
			resourceMonitoringService = (ResourceMonitoringService) context.getService(resourceMonitoringServiceSr);
		}
	}

	protected void setUp() throws Exception {
		super.setUp();

		resourceContextListener = new ResourceContextListenerTestImpl();
		resourceContextListener.start(context);

		// delete all existing ResourceContext
		ResourceContext[] existingContexts = resourceMonitoringService.listContext();
		for (int i = 0; i < existingContexts.length; i++) {
			ResourceContext currentResourceContext = existingContexts[i];
			currentResourceContext.removeContext(null);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		resourceContextListener.stop();

		// delete all existing ResourceContext
		ResourceContext[] existingContexts = resourceMonitoringService.listContext();
		for (int i = 0; i < existingContexts.length; i++) {
			ResourceContext currentResourceContext = existingContexts[i];
			currentResourceContext.removeContext(null);
		}
	}

	/**
	 * Test case 1 : deletion of a ResourceContext.
	 * 
	 * This test case validates the deletion of a ResourceContext with no
	 * ResourceContext as destination.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#removeContext(ResourceContext)}
	 */
	public void testDeletionOfAResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name = "context1";

		// create the resource context
		ResourceContext resourceContext = resourceMonitoringService.createContext(name,
				null);

		// delete this new resourceContext
		resourceContext.removeContext(null);

		// check existing ResourceContext
		assertEquals("ResourceContext list must be empty.", 0, resourceMonitoringService.listContext().length);
		assertNull("ResourceMonitoringService must contain a ResourceContext named: " + name, resourceMonitoringService.getContext(name));

		// check the resource context name is still accessible
		assertEquals("Name mismatch.", name, resourceContext.getName());

		// check it is not possible to add bundle
		try {
			resourceContext.addBundle(2);
			failSame("A ResourceContextException is expected.");
		} catch (ResourceContextException e) {
			log("Expected exception: ");
			e.printStackTrace();
		}

		// check it is not possible to add monitor
		try {
			resourceContext.addResourceMonitor(new FakeResourceMonitor());
			fail("A ResourceContextException is expected here.");
		} catch (ResourceContextException e) {
			log("Expected exception: ");
			e.printStackTrace();
		}

		// check a ResourceContextEvent has been sent due to deletion
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull("LastEvent must not be null.", lastEvent);
		assertEquals("ResourceContext mismatch.", resourceContext, lastEvent.getContext());
		assertEquals("Last event type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, lastEvent.getType());
	}

	/**
	 * Test case 2 : deletion of a ResourceContext with a destination
	 * ResourceContext.
	 * 
	 * This test case validates the deletion of a ResourceContext with a
	 * destination.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#addBundle(long)}
	 *         {@link ResourceContext#removeContext(ResourceContext)}
	 */
	public void testDeletionOfAResourceContextWithADestinationResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		// create ResourceContexts
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
				null);
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2,
				null);
		log("resourceContext1:" + resourceContext1);
		log("resourceContext2:" + resourceContext2);

		// checks the array of existing ResourceContext
		assertEquals("ResourceContext list mismatch.", 2, resourceMonitoringService.listContext().length);

		// associate bundleId with resourceContext1
		resourceContext1.addBundle(bundleId);

		// associate a monitor

		// delete resourceContext1 and specify resourceContext2 as
		// destination
		resourceContext1.removeContext(resourceContext2);
		// deleting resourceContext1 MUST generate 6 events (in this order):
		// - 1 for creating resourceContext1
		// - 1 for creating resourceContext2
		// - 1 for adding bundleId to resourceContext1
		// - 1 for removing bundleId from resourceContext1
		// - 1 for adding bundleId to resourceContext2
		// - 1 for removing resourceContext1

		// checks only context2 is only the one existing ResourceContext
		ResourceContext[] existingContexts = resourceMonitoringService.listContext();
		assertEquals("ResourceContext list mismatch.", 1, existingContexts.length);
		assertEquals("Name mismatch.", name2, existingContexts[0].getName());

		// check resourceContext1 (no associated bundle and no associated
		// monitors)
		long[] rc1BundleIds = resourceContext1.getBundleIds();
		assertEquals("BundleIds list mismatch.", 0, rc1BundleIds.length);
		// ResourceMonitor[] rc1ResourceMonitors =
		// resourceContext1.getMonitors();
		// assertEquals("ResourceMonitors list mismatch.", 0,
		// rc1ResourceMonitors.length);

		// check bundleId belongs to resourceContext2
		long[] rc2BundleIds = resourceContext2.getBundleIds();
		assertEquals("BundleIds list mismatch.", 1, rc2BundleIds.length);
		assertEquals("BundleId mismatch.", bundleId, rc2BundleIds[0]);

		List receivedEvents = resourceContextListener.getReceivedEvents();

		log("nb of receivedEvent : " + receivedEvents.size());
		assertEquals("Number of events mismatch.", 6, receivedEvents.size());

		// first received event is a BUNDLE_REMOVED ResourceContextEvent has
		// been received
		ResourceContextEvent firstEvent = (ResourceContextEvent) receivedEvents
				.get(3);
		assertNotNull("FirstEvent must not be null.", firstEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, firstEvent.getType());
		assertEquals("ResourceContext mismatch.", resourceContext1, firstEvent.getContext());
		assertEquals("BundleId mismatch.", bundleId, firstEvent.getBundleId());

		// check a BUNDLE_ADDED ResourceContextEvent has been received
		// (second
		// received event)
		ResourceContextEvent secondEvent = (ResourceContextEvent) receivedEvents
				.get(4);
		assertNotNull("SecondEvent must not be null.", secondEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_ADDED, secondEvent.getType());
		assertEquals("ResourceContext mismatch.", resourceContext2, secondEvent.getContext());
		assertEquals("BundleId mismatch.", bundleId, secondEvent.getBundleId());

		// check the third received event is a RESOURCE_CONTEXT_REMOVED
		// event
		ResourceContextEvent thirdEvent = (ResourceContextEvent) receivedEvents
				.get(5);
		assertNotNull("ThirdEvent must not be null.", thirdEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, thirdEvent.getType());
		assertEquals("ResourceContext mismatch.", resourceContext1, thirdEvent.getContext());
	}

	/**
	 * Test case 3 : deletion of a ResourceContext with a previously deleted
	 * ResourceContext as destination.
	 * 
	 * This test case validates the deletion of a ResourceContext with a
	 * ResourceContext destination which has been previously deleted.
	 * 
	 * @throws IllegalArgumentException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#addBundle(long)},
	 *         {@link ResourceContext#removeContext(ResourceContext)}
	 */
	public void testDeletionOfAResourceContextWithAPreviouslyDeletedResourceContextAsDestination() throws IllegalArgumentException, ResourceContextException {
		final String name1 = "name1";
		final String name2 = "name2";
		final long bundleId = 1;

		// create ResourceContexts
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
				null);
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2,
				null);

		// add bundleId to resourceContext1
		resourceContext1.addBundle(bundleId);

		// delete resourceContext2
		resourceContext2.removeContext(null);

		// try to delete resourceContext1 with resourceContext2 as
		// destination resourceContext2 has been previously deleted ==> ex
		resourceContext1.removeContext(resourceContext2);

		// try to add bundleId to resourceContext1 => expect a
		// ResourceContextException
		try {
			resourceContext1.addBundle(bundleId);
			fail("A ResourceContextException is expected.");
		} catch (ResourceContextException e) {
			log("Expected exception: ");
			e.printStackTrace();
		}

		// check this no existing ResourceContext
		assertEquals("ResourceContexts list mismatch.", 0, resourceMonitoringService.listContext().length);

		// check events
		List events = resourceContextListener.getReceivedEvents();
		// 6 events MUST be emitted (in this order) :
		// - creation of context1
		// - creation of context2
		// - adding of bundle 1 to context1
		// - deletion of context2
		// - removing of bundle1 from context1
		// - deletion of context1
		assertEquals("Number of events mismatch.", 6, events.size());

		// event RESOURCE_CONTEXT_CREATED for context1
		ResourceContextEvent event = (ResourceContextEvent) events.get(0);
		assertEquals("Event type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, event.getType());
		assertEquals("Name mismatch.", name1, event.getContext().getName());

		// event RESOURCE_CONTEXT_CREATED for context2
		event = (ResourceContextEvent) events.get(1);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, event.getType());
		assertEquals("Name mismatch.", name2, event.getContext().getName());

		// event BUNDLE_ADDED for bundle1 and context1
		event = (ResourceContextEvent) events.get(2);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_ADDED, event.getType());
		assertEquals("Name mismatch.", name1, event.getContext().getName());
		assertEquals("BundleId mismatch.", bundleId, event.getBundleId());

		// event RESOURCE_CONTEXT_REMOVED for context2
		event = (ResourceContextEvent) events.get(3);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, event.getType());
		assertEquals("Name mismatch.", name2, event.getContext().getName());

		// event BUNDLE_REMOVED from context1
		event = (ResourceContextEvent) events.get(4);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, event.getType());
		assertEquals("Name mismatch.", name1, event.getContext().getName());
		assertEquals("BundleId mismatch.", bundleId, event.getBundleId());

		// event RESOURCE_CONTEXT_REMOVED for context2
		event = (ResourceContextEvent) events.get(5);
		assertEquals("Type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_REMOVED, event.getType());
		assertEquals("Name mismatch.", name1, event.getContext().getName());
	}
}
