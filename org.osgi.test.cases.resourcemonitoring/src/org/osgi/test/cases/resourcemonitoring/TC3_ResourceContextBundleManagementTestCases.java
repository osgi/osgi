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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.service.resourcemonitoring.ResourceMonitoringServiceException;
import org.osgi.test.cases.resourcemonitoring.utils.ResourceContextListenerTestImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * see Conformance Tests description.odt file.
 * 
 * @author $Id$
 */
public class TC3_ResourceContextBundleManagementTestCases extends DefaultTestBundleControl {

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
	 * Test adding a bundle to a resource context.
	 */
	public void testTC41AddingABundleToAResourceContext() {
		final String name = "context1";
		final long bundleId = 1l;

		try {
			// create ResourceContext
			ResourceContext resourceContext = resourceMonitoringService.createContext(name,
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
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

	/**
	 * Test adding a bundle previously associated with a resource context.
	 * 
	 * Expect a RuntimeException.
	 */
	public void testTC42AddingABundlePreviouslyAssociatedWithAResourceContext() {
		final String name = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		try {
			// create ResourceContext
			ResourceContext resourceContext = resourceMonitoringService.createContext(name,
					null);
			ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2,
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
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}

	}

	/**
	 * Test remove a bundle from a ResourceContext. Check a
	 * {@link ResourceContextEvent} is received.
	 */
	public void testTC43RemoveBundleFromAResourceContext() {
		final String name = "context1";
		final long bundleId = 1;

		try {
			// create ResourceContext
			ResourceContext resourceContext = resourceMonitoringService.createContext(name,
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
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

	/**
	 * Test remove a bundle from context with a destination. Expect one
	 * ResourceContextEvent from removing the bundle and one more for adding it
	 * into another ResourceContext.
	 */
	public void testTC44RemoveABundleWithADestinationResourceContext() {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = 1;

		try {
			// create two Resource Context
			ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
					null);
			ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2,
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

			// check a ResourceContextEvent has been received about the newly
			// added
			// bundleId
			ResourceContextEvent addedEvent = resourceContextListener
					.getLastEvent();
			assertNotNull(addedEvent);
			assertTrue(addedEvent.getType() == ResourceContextEvent.BUNDLE_ADDED);
			assertTrue(addedEvent.getBundleId() == bundleId);
			assertTrue(addedEvent.getContext().equals(resourceContext2));
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

	/**
	 * Test remove bundle with null destination. Expect a REMOVED_BUNDLE event.
	 */
	public void testTC45RemoveABundleWithANullDestinationResourceContext() {
		final String name = "context1";
		final long bundleId = 1;

		try {
			// create ResourceContext
			ResourceContext resourceContext = resourceMonitoringService.createContext(name,
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
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

	/**
	 * Test remove bundle with a deleted resource context as destination. Expect
	 * a RuntimeException when invoking
	 * {@link ResourceContext#removeBundle(long, ResourceContext)}. The bundle
	 * is correctly removed.
	 */
	public void testTC46RemovingABundleWithADestinationContextWhichHasBeenPreviouslyDeleted() {
		final String name1 = "context1";
		final String name2 = "context2";
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

			// try to remove bundleId from resourceContext1 and associate it
			// with
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

			// check a REMOVED_BUNDLE ResourceContextEvent event has been
			// received
			ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
			assertNotNull(lastEvent);
			assertTrue(lastEvent.getType() == ResourceContextEvent.BUNDLE_REMOVED);
			assertTrue(lastEvent.getContext().equals(resourceContext1));
			assertTrue(lastEvent.getBundleId() == bundleId);
		} catch (ResourceMonitoringServiceException e) {
			e.printStackTrace();
			fail("A pb occurred when creating the ResourceContext (e.g. its name is already used).");
		}
	}

}
