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

package org.osgi.test.cases.resourcemonitoring;

import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextException;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.cases.resourcemonitoring.utils.ResourceContextListenerTestImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author $Id$
 */
public class TC3_ResourceContextBundleManagementTestCases extends DefaultTestBundleControl {

	/**
	 * ResourceMonitoringService
	 */
	private ResourceMonitoringService		resourceMonitoringService;

	/**
	 * resource context listener
	 */
	private ResourceContextListenerTestImpl	resourceContextListener;

	protected void setUp() throws Exception {
		super.setUp();

		ServiceReference<ResourceMonitoringService> resourceMonitoringServiceSr = getContext()
				.getServiceReference(ResourceMonitoringService.class);
		if (resourceMonitoringServiceSr != null) {
			resourceMonitoringService = getContext()
					.getService(resourceMonitoringServiceSr);
		}
		resourceContextListener = new ResourceContextListenerTestImpl();
		resourceContextListener.start(getContext());

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
	 * Test case 1 : adding a bundle to a ResourceContext.
	 * 
	 * This test cases tests the adding of a bundle to a ResourceContext.
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addBundle(long)}
	 */
	public void testAddingABundleToAResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name = "context1";
		final long bundleId = getContext().getBundle().getBundleId();

		// create ResourceContext
		ResourceContext resourceContext = resourceMonitoringService.createContext(name, null);

		// add bundle 1 to this context
		resourceContext.addBundle(bundleId);

		// check bundle 1 has been associated to this context
		long[] bundleIds = resourceContext.getBundleIds();
		assertEquals("BundleIds list mismatch.", 1, bundleIds.length);
		assertEquals("BundleId mismatch.", bundleId, bundleIds[0]);

		// check a ResourceContextEvent has been sent
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull("LastEvent must not be null.", lastEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_ADDED, lastEvent.getType());
		assertEquals("ResourceContext mismatch.", resourceContext, lastEvent.getContext());
		assertEquals("BundleId mismatch.", bundleId, lastEvent.getBundleId());
	}

	/**
	 * Test case 2 : adding a bundle previously associated with a ResourceContext.
	 * 
	 * This test case tests the adding of a bundle previously added to another ResourceContext.
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addBundle(long)}
	 */
	public void testAddingABundlePreviouslyAssociatedWithAResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name = "context1";
		final String name2 = "context2";
		final long bundleId = getContext().getBundle().getBundleId();

		// Create ResourceContexts.
		ResourceContext resourceContext = resourceMonitoringService.createContext(name, null);
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2, null);

		// Add bundleId 1 to resourceContext.
		resourceContext.addBundle(bundleId);
		long[] bundleIds = resourceContext.getBundleIds();
		assertEquals("BundleIds list mismatch: the resourceContext bundleIds list is expected to contain 1 bundleId.", 1, bundleIds.length);
		assertEquals("BundleId mismatch: the bundleIds list must only contain the bundleId " + bundleId, bundleId, bundleIds[0]);

		// Try to add bundleId 1 to resourceContext2.
		try {
			resourceContext2.addBundle(bundleId);
			fail("A ResourceContextException is expected.");
		} catch (ResourceContextException e) {
			log("Expected exception: " + e.getMessage());
		}

		// Check that bundleId 1 is not associated with resourceContext2.
		assertEquals("BundleIds list mismatch: the resourceContext2 bundleIds list is expected to be empty.", 0,
				resourceContext2.getBundleIds().length);

		// Check that bundleId 1 is still associated with resourceContext1.
		bundleIds = resourceContext.getBundleIds();
		assertEquals("BundleIds list mismatch: the resourceContext bundleIds list is expected to contain 1 bundleId.", 1, bundleIds.length);
		assertEquals("BundleId mismatch: the bundleIds list must only contain the bundleId " + bundleId, bundleId, bundleIds[0]);
	}

	/**
	 * Test case 3 : removing a bundle from a ResourceContext.
	 * 
	 * This test case validates the removing of a bundle from a ResourceContext (and checks that a {@link ResourceContextEvent} is
	 * received).
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addBundle(long)} {@link ResourceContext#removeBundle(long)}
	 */
	public void testRemoveBundleFromAResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name = "context1";
		final long bundleId = getContext().getBundle().getBundleId();

		// create ResourceContext
		ResourceContext resourceContext = resourceMonitoringService.createContext(name, null);

		// add bundle 1
		resourceContext.addBundle(bundleId);

		// remove bundleId from resourceContext
		resourceContext.removeBundle(bundleId);

		// check list of bundles
		assertNotNull("BundleIds list must not be null.", resourceContext.getBundleIds());
		assertEquals("BundleIds list mismatch.", 0, resourceContext.getBundleIds().length);

		// check a ResourceContextEvent has been received
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull("LastEvent must not be null.", lastEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, lastEvent.getType());
		assertEquals("ResourceContext mismatch.", resourceContext, lastEvent.getContext());
		assertEquals("BundleId mismatch.", bundleId, lastEvent.getBundleId());

		// try to remove it again ==> expect a ResourceContextException
		try {
			resourceContext.removeBundle(bundleId);
			fail("A ResourceContextException is expected.");
		} catch (ResourceContextException e) {
			log("Expected exception: " + e.getMessage());
		}
	}

	/**
	 * Test case 4 : removing a bundle with a destination ResourceContext.
	 * 
	 * This test case tests the removing of a bundle from a context with a destination context (expects one ResourceContextEvent from
	 * removing the bundle and one more for adding it into another ResourceContext).
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addBundle(long)} {@link ResourceContext#removeBundle(long)}
	 */
	public void testRemoveABundleWithADestinationResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = getContext().getBundle().getBundleId();

		// create two Resource Context
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1, null);
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2, null);

		// add bundleId to context1
		resourceContext1.addBundle(bundleId);

		// remove bundleId from context1 with destination = context2
		resourceContext1.removeBundle(bundleId, resourceContext2);

		// check context1 has no associated bundles
		assertEquals("BundleIds list mismatch.", 0, resourceContext1.getBundleIds().length);

		// check context2 has one associated bundle (i.e bundleId)
		long[] context2BundleIds = resourceContext2.getBundleIds();
		assertEquals("BundleIds list mismatch.", 1, context2BundleIds.length);
		assertEquals("BundleId mismatch.", bundleId, context2BundleIds[0]);

		// check a ResourceContextEvent has been received about the removed
		// bundle (should be the last last received event)
		ResourceContextEvent removedEvent = resourceContextListener.getLastLastEvent();
		assertNotNull("RemovedEvent must not be null.", removedEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, removedEvent.getType());
		assertEquals("BundleId mismatch.", bundleId, removedEvent.getBundleId());
		assertEquals("ResourceContext mismatch.", resourceContext1, removedEvent.getContext());

		// check a ResourceContextEvent has been received about the newly added
		// bundleId
		ResourceContextEvent addedEvent = resourceContextListener.getLastEvent();
		assertNotNull("AddedEvent must not be null.", addedEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_ADDED, addedEvent.getType());
		assertEquals("BundleId mismatch.", bundleId, addedEvent.getBundleId());
		assertEquals("ResourceContext mismatch.", resourceContext2, addedEvent.getContext());
	}

	/**
	 * Test case 5 : removing a bundle with a null destination ResourceContext.
	 * 
	 * This test case validates the removing of a bundle from a ResourceContext with a null destination. This test case is similar to
	 * {@link TC3_ResourceContextBundleManagementTestCases#testRemoveBundleFromAResourceContext()} (Expect a REMOVED_BUNDLE event).
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addBundle(long)} {@link ResourceContext#removeBundle(long)}
	 */
	public void testRemoveABundleWithANullDestinationResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name = "context1";
		final long bundleId = getContext().getBundle().getBundleId();

		// create ResourceContext
		ResourceContext resourceContext = resourceMonitoringService.createContext(name, null);

		// add bundleId to resourceContext
		resourceContext.addBundle(bundleId);

		// remove it with destination null
		resourceContext.removeBundle(bundleId, null);

		// check list of bundle
		long[] bundleIds = resourceContext.getBundleIds();
		assertNotNull("BundleIds list must not be null.", bundleIds);
		assertEquals("BundlesIds list mismatch.", 0, bundleIds.length);

		// check that a REMOVED_BUNDLE event has been received
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull("LastEvent must not be null.", lastEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, lastEvent.getType());
		assertEquals("ResourceContext mismatch.", resourceContext, lastEvent.getContext());
		assertEquals("BundleId mismatch.", bundleId, lastEvent.getBundleId());
	}

	/**
	 * Test case 6 : removing a bundle with a destination context which has been previously deleted.
	 * 
	 * This test case checks the removing of a bundle from a ResourceContext with a destination Resource Context which has been removed
	 * (Expect a ResourceContextException when invoking {@link ResourceContext#removeBundle(long, ResourceContext)}).
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addBundle(long)} {@link ResourceContext#removeBundle(long)}
	 */
	public void testRemovingABundleWithADestinationContextWhichHasBeenPreviouslyDeleted() throws IllegalArgumentException,
			ResourceContextException {
		final String name1 = "context1";
		final String name2 = "context2";
		final long bundleId = getContext().getBundle().getBundleId();

		// create ResourceContexts
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1, null);
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2, null);

		// add bundleId to resourceContext1
		resourceContext1.addBundle(bundleId);

		// delete resourceContext2
		resourceContext2.removeContext(null);

		// try to remove bundleId from resourceContext1 and associate it with
		// deleted ResourceContext2 ==> expect a ResourceContextException
		try {
			resourceContext1.removeBundle(bundleId, resourceContext2);
			fail("A ResourceContextException is expected.");
		} catch (ResourceContextException e) {
			log("Expected exception: " + e.getMessage());
		}

		// check bundleId has been removed from resourceContext1
		long[] resourceContext1bundleIds = resourceContext1.getBundleIds();
		assertEquals("BundleIds list mismatch.", 0, resourceContext1bundleIds.length);

		// check a REMOVED_BUNDLE ResourceContextEvent event has been received
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull("LastEvent must not be null.", lastEvent);
		assertEquals("Type mismatch.", ResourceContextEvent.BUNDLE_REMOVED, lastEvent.getType());
		assertEquals("ResourceContext mismatch.", resourceContext1, lastEvent.getContext());
		assertEquals("BundleId mismatch.", bundleId, lastEvent.getBundleId());
	}

}
