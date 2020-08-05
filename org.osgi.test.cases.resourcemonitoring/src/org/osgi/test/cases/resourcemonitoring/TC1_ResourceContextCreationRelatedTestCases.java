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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextException;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.cases.resourcemonitoring.utils.FakeResourceMonitor;
import org.osgi.test.cases.resourcemonitoring.utils.ResourceContextListenerTestImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author $Id$
 */
public class TC1_ResourceContextCreationRelatedTestCases extends DefaultTestBundleControl {

	/**
	 * ResourceMonitoringService.
	 */
	private ResourceMonitoringService		resourceMonitoringService;

	/**
	 * resource context listener
	 */
	private ResourceContextListenerTestImpl	resourceContextListener;

	protected void setUp() throws Exception {
		super.setUp();

		ServiceReference<ResourceMonitoringService> serviceReference = getContext()
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = getContext()
				.getService(serviceReference);

		resourceContextListener = new ResourceContextListenerTestImpl();
		resourceContextListener.start(getContext());

		// delete all existing resource contexts.
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		for (int i = 0; i < resourceContexts.length; i++) {
			resourceContexts[i].removeContext(null);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		resourceContextListener.stop();

		// delete all existing resource contexts.
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		for (int i = 0; i < resourceContexts.length; i++) {
			resourceContexts[i].removeContext(null);
		}
	}

	/**
	 * Test case 1 : Creation of a ResourceContext
	 * 
	 * This test case validates the Resource Context creation and the retrieving of a ResourceContext.
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#getMonitors()}
	 */
	public void testCreationOfAResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name = "context1";

		// Lists existing contexts by calling ResourceManager.listContext().
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		// Check that the returned list is not null and is empty.
		assertNotNull("ResourceContext list must not be null.", resourceContexts);
		int initialResourceContextsLength = resourceContexts.length;

		// A ResourceContextListener service was already registered.

		// Creates a new ResourceContext named "context1".
		ResourceContext resourceContext = resourceMonitoringService.createContext(name, null);

		// Check that the returned ResourceContext object is not null.
		assertNotNull("The resourceContext must not be null.", resourceContext);

		// Checks that the name of the newly created ResourceContext is
		// "context1".
		assertEquals("Name mismatch.", name, resourceContext.getName());

		// Checks that the newly created ResourceContext has no associated
		// ResourceMonitor objects.
		assertNotNull("Monitors list must not be null.", resourceContext.getMonitors());
		assertEquals("Monitors list must be empty.", 0, resourceContext.getMonitors().length);

		// Checks that the newly created ResourceContext has no associated
		// bundles.
		assertNotNull("BundleIds list must not be null.", resourceContext.getBundleIds());
		assertEquals("BundleIds list must be empty.", 0, resourceContext.getBundleIds().length);

		// Checks that the ResourceContextListener service receives a
		// RESOURCE_CONTEXT_CREATED ResourceContextEvent.
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull("Last received event must not be null.", lastEvent);
		assertEquals("Event resource context mismatch.", resourceContext, lastEvent.getContext());
		assertEquals("Event type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, lastEvent.getType());

		// Retrieves all existing Resource Contexts.
		resourceContexts = resourceMonitoringService.listContext();

		// Check that the created ResourceContext is in the returned array and
		// the length of the array is equal to initialResourceContextsLength +
		// 1.
		assertNotNull("ResourceContext list must not be null.", resourceContexts);
		assertEquals("ResourceContext list length must be equal to " + initialResourceContextsLength + 1,
				initialResourceContextsLength + 1, resourceContexts.length);
		assertEquals("ResourceContext mismatch.", resourceContext, resourceContexts[0]);

		// Retrieves the ResourceContext using ResourceContext.getContext(name
		// of context)
		ResourceContext retrievedResourceContext = resourceMonitoringService.getContext(name);

		// Checks that the returned ResourceContext object is equal to the
		// created ResourceContext.
		assertNotNull("ResourceContext must not be null.", retrievedResourceContext);
		assertEquals("ResourceContext mismatch.", resourceContext, retrievedResourceContext);
	}

	/**
	 * Test case 2 : Creation of a ResourceContext with an existing ResourceContext name
	 * 
	 * This test case tests that it is no possible to create two ResourceContext with the same name.
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 */
	public void testCreationOfAResourceContextWithAnExistingResourceContextName() throws IllegalArgumentException {
		final String name = "context1";
		// A ResourceContextListener service was already registered.

		// Keep resourceMonitoringService.listContext().length.
		int initialResourceContextsLength = resourceMonitoringService.listContext().length;

		// Creates a new ResourceContext named "context1".
		ResourceContext resourceContext = resourceMonitoringService.createContext(name, null);
		// Checks the returned ResourceContext object is not null.
		assertNotNull("ResourceContext must not be null.", resourceContext);

		// Retrieves the RESOURCE_CONTEXT_CREATED event.
		ResourceContextEvent event = resourceContextListener.getLastEvent();

		// Retrieves all existing Resource Contexts, and Check the created
		// ResourceContext is in the returned array and the length of the array
		// is initialResourceContextsLength + 1.
		assertEquals("ResourceMonitoringService.listContext() mismatch.", initialResourceContextsLength + 1,
				resourceMonitoringService.listContext().length);

		// Try to creates again a ResourceContext named "context1", i.e. with
		// the same name.
		try {
			resourceMonitoringService.createContext(name, null);
			fail("A IllegalArgumentException is expected here.");
		} catch (IllegalArgumentException e) {
			// Checks that a IllegalArgumentException is thrown.
			assertException("A IllegalArgumentException is expected.", IllegalArgumentException.class, e);
		}

		// Checks no ResourceContextEvent has been sent (i.e the "last event"
		// must still be the same).
		assertEquals("Event mismatch.", event, resourceContextListener.getLastEvent());

		// Checks that there is still the same number of ResourceContext in the
		// list.
		assertEquals("ResourceMonitoringService.listContext().length mismatch.", initialResourceContextsLength + 1,
				resourceMonitoringService.listContext().length);
	}

	/**
	 * Test case 3 : Creation of a ResourceContext based on a template ResourceContext
	 * 
	 * This test case validates the Resource Context creation with a template Resource Context.
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addResourceMonitor(ResourceMonitor)}, {@link ResourceContext#getMonitors()}
	 */
	public void testCreationOfAResourceContextBasedOnATemplateResourceContext() throws IllegalArgumentException, ResourceContextException {
		final String name1 = "context1";
		final String name2 = "context2";

		// Creates a template ResourceContext named "context1".
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1, null);

		// Checks the created ResourceContext is not null.
		assertNotNull("ResourceContext must not be null.", resourceContext1);

		// Add a FakeResourceMonitor simulating a CPU ResourceMonitor to
		// context1.
		resourceContext1.addResourceMonitor(new FakeResourceMonitor(ResourceMonitoringService.RES_TYPE_CPU, resourceContext1));

		// Creates a new ResourceContext named "context2" with "context1" as
		// template.
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2, resourceContext1);

		// Checks the newly created ResourceContext is not null.
		assertNotNull("ResourceContext must not be null.", resourceContext2);
		// Checks the name of the newly created ResourceContext is « context2 ».
		assertEquals("Name mismatch.", name2, resourceContext2.getName());
		// Checks that context2 has no associated bundles.
		assertEquals("BundleIds list mismatch.", 0, resourceContext2.getBundleIds().length);

		// Checks that context2 has the same ResourceMonitors as context1.
		ResourceMonitor< ? >[] rc1Monitors = resourceContext1.getMonitors();
		ResourceMonitor< ? >[] rc2Monitors = resourceContext2.getMonitors();
		assertEquals("ResourceMonitors list mismatch.", rc1Monitors.length, rc2Monitors.length);
		assertEquals("ResourceType mismatch.", ResourceMonitoringService.RES_TYPE_CPU, rc2Monitors[0].getResourceType());
	}

	/**
	 * Test case 4 : Creation of a ResourceContext based on a template ResourceContext previously deleted.
	 * 
	 * This test case tests the ResourceContext creation with a template ResourceContext which has been previously deleted.
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addResourceMonitor(ResourceMonitor)}, {@link ResourceContext#removeContext(ResourceContext)}
	 *             , {@link ResourceContext#getMonitors()}
	 */
	public void testCreationOfAResourceContextBasedOnATemplateResourceContextPreviouslyDeleted() throws IllegalArgumentException,
			ResourceContextException {
		final String name1 = "context1";
		final String name2 = "context2";
		// Creates a template ResourceContext named "context1".
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1, null);

		// Checks the created ResourceContext is not null.
		assertNotNull("ResourceContext must not be null.", resourceContext1);

		// Add a FakeResourceMonitor simulating a CPU ResourceMonitor to
		// context1.
		resourceContext1.addResourceMonitor(new FakeResourceMonitor(ResourceMonitoringService.RES_TYPE_CPU, resourceContext1));

		// Delete the ResourceContext context1.
		resourceContext1.removeContext(null);

		// Creates a new ResourceContext named "context2" with "context1" as
		// template.
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2, resourceContext1);

		// Checks the newly created ResourceContext is not null.
		assertNotNull("ResourceContext must not be null.", resourceContext2);
		// Checks the name of the newly created ResourceContext is "context2".
		assertEquals("Name mismatch.", name2, resourceContext2.getName());
		// Checks that context2 has no associated bundles.
		assertEquals("BundleIds list mismatch.", 0, resourceContext2.getBundleIds().length);
		// Checks that context2 has no ResourceMonitor.
		ResourceMonitor< ? >[] rc2Monitors = resourceContext2.getMonitors();
		assertEquals("ResourceMonitors list mismatch.", 0, rc2Monitors.length);
	}

	/**
	 * Test case 5 : Retrieving a ResourceContext based on a bundle identifier.
	 * 
	 * This test case validates the retrieving a ResourceContext based on a bundle.
	 * 
	 * @throws IllegalArgumentException
	 *             , see {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#addBundle(long)}
	 */
	public void testRetrievingAResourceContextBasedOnABundleIdentifier() throws IllegalArgumentException, ResourceContextException {
		final String name = "context1";
		final long bundleId = getContext().getBundle().getBundleId();
		// Retrieve the ResourceContext associated with bundle id 1.
		ResourceContext resourceContext = resourceMonitoringService.getContext(bundleId);
		// Check that the returned ResourceContext object is null.
		assertNull("ResourceContext must be null.", resourceContext);

		// Create the ResourceContext named "context1".
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name, null);
		// Associates bundleId 1 with context1
		resourceContext1.addBundle(bundleId);

		// Retrieves the ResourceContext associated with bundleId 1.
		resourceContext = resourceMonitoringService.getContext(bundleId);
		// Checks the returned ResourceContext object is not null.
		assertNotNull("ResourceContext must not be null.", resourceContext);
		// Checks the returned ResourceContext object is the same as context1.
		assertEquals("ResourceContext mismatch.", resourceContext1, resourceContext);

		// Try to retrieve the ResourceContext associated with a bundle that
		// doesn't exist (i.e. a bundle with bundleId -1).
		resourceContext = resourceMonitoringService.getContext(-1);
		// Checks the returned ResourceContext object is null.
		assertNull("ResourceContext must be null.", resourceContext);
	}

	/**
	 * Test case 6 : Supported types of resources.
	 * 
	 * This test case checks the retrieving of available Resource Monitor Factory.
	 * 
	 * @throws InvalidSyntaxException
	 *             , see {@link BundleContext#getServiceReferences(Class, String)}
	 */
	public void testSupportedTypesOfResources() throws InvalidSyntaxException {

		// Retrieve the array of supported types by calling
		// ResourceManager.getSupportedTypes().
		String[] supportedTypes = resourceMonitoringService.getSupportedTypes();
		// Checks that the array is not null.
		assertNotNull("SupportedTypes must not be null.", supportedTypes);

		// Retrieve all ServiceReference of ResourceMonitorFactory services
		// through the BundleContext
		@SuppressWarnings({
				"unchecked", "rawtypes"
		})
		Collection<ServiceReference<ResourceMonitorFactory< ? >>> factorySrs = (Collection) getContext()
				.getServiceReferences(ResourceMonitorFactory.class, null);

		// Check that the number of ServiceReference is not null.
		assertNotNull("The collection of ServiceReference of ResourceMonitorFactory services must not be null.", factorySrs);
		// Check that the number of ServiceReference is equal to the number of
		// type returned by getSupportedTypes.
		assertEquals("the number of ServiceReference must be equal to the number of type returned by getSupportedTypes.",
				supportedTypes.length, factorySrs.size());

		// Iterate over the ServiceReference collection and retrieve each
		// ResourceMonitorFactory service.
		List<String> computedSupportedTypes = new ArrayList<>();
		for (Iterator<ServiceReference<ResourceMonitorFactory< ? >>> it = factorySrs
				.iterator(); it.hasNext();) {
			ResourceMonitorFactory< ? > rmf = getContext()
					.getService(it.next());
			// add the type in the list
			computedSupportedTypes.add(rmf.getType());
		}

		// Checks that all supported types are available by comparing each found
		// ServiceReference.
		assertEquals("SupportedTypes list mismatch.", supportedTypes.length, computedSupportedTypes.size());
		// Check that each supported types is in the list
		for (int i = 0; i < supportedTypes.length; i++) {
			assertTrue("The given supportedType must appear in the SupportedTypes list.",
					computedSupportedTypes.contains(supportedTypes[i]));
		}
	}

}
