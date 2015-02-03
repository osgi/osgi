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
import org.osgi.service.resourcemonitoring.ResourceMonitoringServiceException;
import org.osgi.test.cases.resourcemonitoring.utils.FakeResourceMonitor;
import org.osgi.test.cases.resourcemonitoring.utils.ResourceContextListenerTestImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * see Conformance Tests description.odt file.
 * 
 * @author $Id$
 */
public class TC1_ResourceContextCreationRelatedTestCases extends DefaultTestBundleControl {

	/**
	 * bundle context
	 */
	private BundleContext					bundleContext;

	/**
	 * ResourceMonitoringService.
	 */
	private ResourceMonitoringService		resourceMonitoringService;

	/**
	 * resource context listener
	 */
	private ResourceContextListenerTestImpl	resourceContextListener;

	/**
	 * Set bundle context.
	 */
	public void setBundleContext(BundleContext context) {
		bundleContext = context;

		ServiceReference serviceReference = bundleContext
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = (ResourceMonitoringService) bundleContext.getService(serviceReference);
	}

	protected void setUp() throws Exception {
		super.setUp();

		resourceContextListener = new ResourceContextListenerTestImpl();
		resourceContextListener.start(bundleContext);

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
	 * This test case validates the Resource Context creation and the retrieving
	 * of a ResourceContext.
	 * 
	 * @throws ResourceMonitoringServiceException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#getMonitors()}
	 */
	public void testTC1CreationOfAResourceContext() throws ResourceMonitoringServiceException, ResourceContextException {
		final String name = "context1";

		// Lists existing contexts by calling ResourceManager.listContext().
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		// Check that the returned list is not null and is empty.
		assertNotNull("ResourceContext list must not be null.", resourceContexts);
		assertEquals("ResourceContext list must be empty.", 0, resourceContexts.length);

		// A ResourceContextListener service was already registered.

		// Creates a new ResourceContext named "context1".
		ResourceContext resourceContext = resourceMonitoringService.createContext(name,
				null);

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
		// the length of the array is 1.
		assertNotNull("ResourceContext list must not be null.", resourceContexts);
		assertEquals("ResourceContext list length must be equal to 1.", 1, resourceContexts.length);
		assertEquals("ResourceContext mismatch.", resourceContext, resourceContexts[0]);

		// Retrieves the ResourceContext using ResourceContext.getContext(name
		// of context)
		ResourceContext retrievedResourceContext = resourceMonitoringService
				.getContext(name);

		// Checks that the returned ResourceContext object is equal to the
		// created ResourceContext.
		assertNotNull("ResourceContext must not be null.", retrievedResourceContext);
		assertEquals("ResourceContext mismatch.", resourceContext, retrievedResourceContext);
	}

	/**
	 * Test create a new ResourceContext with an existing resource context name.
	 * 
	 * @throws ResourceMonitoringServiceException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 */
	public void testTC2CreationOfAResourceContextWithAnExistingResourceContextName() throws ResourceMonitoringServiceException {
		final String name = "context1";
		// create first instance of context1
		ResourceContext resourceContext = resourceMonitoringService.createContext(name,
				null);
		assertNotNull("ResourceContext must not be null.", resourceContext);

		// retrieve event
		ResourceContextEvent event = resourceContextListener.getLastEvent();

		// check there is one ResourceContext
		assertEquals("ResourceMonitoringService.listContext() mismatch.", 1, resourceMonitoringService.listContext().length);

		// try to create a new ResourceContext with the same name
		try {
			resourceMonitoringService.createContext(name, null);
			fail("A ResourceMonitoringServiceException is expected here.");
		} catch (ResourceMonitoringServiceException e) {
			assertException("A ResourceMonitoringServiceException is expected.", ResourceMonitoringServiceException.class, e);
		}

		// check no event has been sent (i.e the last event is still the
		// same)
		assertEquals("Event mismatch.", event, resourceContextListener.getLastEvent());

		// check there is still one ResourceContext
		assertEquals("ResourceMonitoringService.listContext().length mismatch.", 1, resourceMonitoringService.listContext().length);
	}

	/**
	 * Test create a ResourceContext based on a template.
	 * 
	 * @throws ResourceMonitoringServiceException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#addResourceMonitor(ResourceMonitor)},
	 *         {@link ResourceContext#getMonitors()}
	 */
	public void testTC3CreationOfAResourceContextBasedOnATemplateResourceContext()
			throws ResourceMonitoringServiceException, ResourceContextException {
		final String name1 = "context1";
		final String name2 = "context2";
		// create a Context context1
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
				null);

		// add a FakeMonitor simulating a CPU ResourceMonitor
		resourceContext1.addResourceMonitor(new FakeResourceMonitor(
				ResourceMonitoringService.RES_TYPE_CPU, resourceContext1));

		// create the ResourceContext2 based on ResourceContext1
		ResourceContext resourceContext2 = resourceMonitoringService.createContext(name2,
				resourceContext1);

		assertNotNull("ResourceContext must not be null.", resourceContext2);
		assertEquals("Name mismatch.", name2, resourceContext2.getName());
		assertEquals("BundleIds list mismatch.", 0, resourceContext2.getBundleIds().length);
		ResourceMonitor[] rc2Monitors = resourceContext2.getMonitors();
		assertEquals("ResourceMonitors list mismatch.", 1, rc2Monitors.length);
		assertEquals("ResourceType mismatch.", ResourceMonitoringService.RES_TYPE_CPU, rc2Monitors[0].getResourceType());
	}

	/**
	 * @throws ResourceMonitoringServiceException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#addResourceMonitor(ResourceMonitor)},
	 *         {@link ResourceContext#removeContext(ResourceContext)},
	 *         {@link ResourceContext#getMonitors()}
	 */
	public void testTC4CreationOfAResourceContextBasedOnATemplateResourceContextPreviouslyDeleted()
			throws ResourceMonitoringServiceException, ResourceContextException {
		final String name1 = "context1";
		final String name2 = "context2";
		// create a Context context1
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name1,
				null);

		// add a FakeMonitor simulating a CPU ResourceMonitor
		resourceContext1.addResourceMonitor(new FakeResourceMonitor(
				ResourceMonitoringService.RES_TYPE_CPU, resourceContext1));

		// remove context1
		resourceContext1.removeContext(null);

		// create the ResourceContext2 based on ResourceContext1
		ResourceContext resourceContext2 = null;
		resourceContext2 = resourceMonitoringService.createContext(name2,
				resourceContext1);

		assertNotNull("ResourceContext must not be null.", resourceContext2);
		assertEquals("Name mismatch.", name2, resourceContext2.getName());
		assertEquals("BundleIds list mismatch.", 0, resourceContext2.getBundleIds().length);
		ResourceMonitor[] rc2Monitors = resourceContext2.getMonitors();
		assertEquals("Monitors list mismatch.", 0, rc2Monitors.length);
	}

	/**
	 * Test retrieve the context based on a bundle id.
	 * 
	 * @throws ResourceMonitoringServiceException, see
	 *         {@link ResourceMonitoringService#createContext(String, ResourceContext)}
	 * @throws ResourceContextException, see
	 *         {@link ResourceContext#addBundle(long)}
	 */
	public void testTC5RetrievingAResourceContextBasedOnABundleIdentifier() throws ResourceMonitoringServiceException, ResourceContextException {
		final String name = "context1";
		final long bundleId = 1;
		// get ResourceContext associated with bundleId and check bundleId
		// is not associated with a context
		ResourceContext resourceContext = resourceMonitoringService.getContext(bundleId);
		assertNull("ResourceContext must be null.", resourceContext);

		// create a new context
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name,
				null);

		// associated bundleId with resourceContext1
		resourceContext1.addBundle(bundleId);

		// get ResourceContext by bundle id
		resourceContext = resourceMonitoringService.getContext(bundleId);
		assertNotNull("ResourceContext must not be null.", resourceContext);
		assertEquals("ResourceContext mismatch.", resourceContext1, resourceContext);

		// try to retrieve the ResourceContext with an unexisting bundle
		resourceContext = resourceMonitoringService.getContext(-1);
		assertNull("ResourceContext must be null.", resourceContext);
	}

	/**
	 * Test supported types
	 * 
	 * @throws InvalidSyntaxException, see
	 *         {@link BundleContext#getServiceReferences(Class, String)}
	 */
	public void testTC6SupportedTypesOfResources() throws InvalidSyntaxException {
		String[] supportedTypes = resourceMonitoringService.getSupportedTypes();
		assertNotNull("SupportedTypes must not be null.", supportedTypes);

		// retrieves all ServiceReference of ResourceMonitorFactory services
		Collection factorySrs = getContext().getServiceReferences(
				ResourceMonitorFactory.class, null);

		// check the number of ServiceReference is the same as the number of
		// supported types
		assertEquals("factorySrs.size(): " + factorySrs.size() + " must be equal to supportedTypes.length: " + supportedTypes.length, supportedTypes.length, factorySrs.size());

		// iterate over the ServiceReference collection and retrieves each
		// ResourceMonitorFactory service.
		List computedSupportedTypes = new ArrayList();
		for (Iterator it = factorySrs.iterator(); it.hasNext();) {
			ResourceMonitorFactory rmf = (ResourceMonitorFactory) getContext().getService(
					(ServiceReference) it.next());
			// add the type in the list
			computedSupportedTypes.add(rmf.getType());
		}

		assertEquals("SupportedTypes list mismatch.", supportedTypes.length, computedSupportedTypes.size());

		// check that each supported types is in the list
		for (int i = 0; i < supportedTypes.length; i++) {
			assertTrue("The given supportedType must appear in the SupportedTypes list.", computedSupportedTypes.contains(supportedTypes[i]));
		}
	}

}
