/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.service.resourcemonitoring.ResourceMonitoringServiceException;
import org.osgi.test.cases.resourcemonitoring.utils.FakeResourceMonitor;
import org.osgi.test.cases.resourcemonitoring.utils.ResourceContextListenerTestImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * see Conformance Tests description.odt file.
 * 
 * @author $Id$
 */
public class TC2_ResourceContextDeletionRelatedTestCases extends DefaultTestBundleControl {

	/**
	 * bundleContext
	 */
	private BundleContext				context;

	/**
	 * ResourceMonitoringService
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * resource context listener
	 */
	private ResourceContextListenerTestImpl	resourceContextListener;

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

	public void setBundleContext(BundleContext context) {
		this.context = context;

		ServiceReference resourceMonitoringServiceSr = context
				.getServiceReference(ResourceMonitoringService.class.getName());
		if (resourceMonitoringServiceSr != null) {
			resourceMonitoringService = (ResourceMonitoringService) context.getService(resourceMonitoringServiceSr);
		}
	}

	/**
	 * Test remove resource context without destination. Check a
	 * ResourceContextEvent is sent.
	 */
	public void testTC1DeletionOfAResourceContext() {
		final String name = "context1";
		boolean exception = false;

		try {
			// create the resource context
			ResourceContext resourceContext = resourceMonitoringService.createContext(name,
					null);

			// delete this new resourceContext
			resourceContext.removeContext(null);

			// check existing ResourceContext
			assertTrue(resourceMonitoringService.listContext().length == 0);
			assertNull(resourceMonitoringService.getContext(name));

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
			assertTrue(lastEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

	/**
	 * 
	 */
	public void testTC2DeletionOfAResourceContextWithADestinationResourceContext() {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		try {
			// create ResourceContexts
			ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
					null);
			ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2,
					null);
			log("resourceContext1:" + resourceContext1);
			log("resourceContext2:" + resourceContext2);

			// checks the array of existing ResourceContext
			assertTrue(resourceMonitoringService.listContext().length == 2);

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

			// check a BUNDLE_ADDED ResourceContextEvent has been received
			// (second
			// received event)
			ResourceContextEvent secondEvent = (ResourceContextEvent) receivedEvents
					.get(4);
			assertNotNull(secondEvent);
			assertTrue(secondEvent.getType() == ResourceContextEvent.BUNDLE_ADDED);
			assertTrue(secondEvent.getContext().equals(resourceContext2));
			assertTrue(secondEvent.getBundleId() == bundleId);

			// check the third received event is a RESOURCE_CONTEXT_REMOVED
			// event
			ResourceContextEvent thirdEvent = (ResourceContextEvent) receivedEvents
					.get(5);
			assertNotNull(thirdEvent);
			assertTrue(thirdEvent.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
			assertTrue(thirdEvent.getContext().equals(resourceContext1));
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

	/**
	 * Test remove a context and specify a deleted context as destination. The
	 * to be deleted context is deleted.
	 */
	public void testTC3DeletionOfAResourceContextWithAPreviouslyDeletedResourceContextAsDestination() {
		final String name1 = "name1";
		final String name2 = "name2";
		final long bundleId = 1;

		try {
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
			// destination
			// resourceContext2 has been previously deleted ==> ex
			resourceContext1.removeContext(resourceContext2);

			// try to add bundleId to resourceContext1 => expect a
			// RuntimeException
			boolean exception = false;
			try {
				resourceContext1.addBundle(bundleId);
			} catch (RuntimeException e) {
				exception = true;
			}
			assertTrue(exception);

			// check this no existing ResourceContext
			assertTrue(resourceMonitoringService.listContext().length == 0);

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

			// event RESOURCE_CONTEXT_REMOVED for context2
			event = (ResourceContextEvent) events.get(3);
			assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
			assertTrue(event.getContext().getName().equals(name2));

			// event BUNDLE_REMOVED from context1
			event = (ResourceContextEvent) events.get(4);
			assertTrue(event.getType() == ResourceContextEvent.BUNDLE_REMOVED);
			assertTrue(event.getContext().getName().equals(name1));
			assertTrue(event.getBundleId() == bundleId);

			// event RESOURCE_CONTEXT_REMOVED for context2
			event = (ResourceContextEvent) events.get(5);
			assertTrue(event.getType() == ResourceContextEvent.RESOURCE_CONTEXT_REMOVED);
			assertTrue(event.getContext().getName().equals(name1));
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

}
