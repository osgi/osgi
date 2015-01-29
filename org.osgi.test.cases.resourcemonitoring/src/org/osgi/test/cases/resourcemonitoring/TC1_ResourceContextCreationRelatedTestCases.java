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
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
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

	/**
	 * This method is called after a test is executed. Remove all existing
	 * ResourceContexts.
	 */
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
	 * Test create a ResourceContext and check the list
	 * 
	 * @throws ResourceMonitoringServiceException if a pb occurred, e.g. if the
	 *         name is already used.
	 */
	public void testTC1CreationOfAResourceContext() throws ResourceMonitoringServiceException {
		final String name = "context1";

		// retrieve the list of existing resource context.
		ResourceContext[] resourceContexts = resourceMonitoringService.listContext();
		assertNotNull("ResourceContext list must not be null.", resourceContexts);
		assertEquals("ResourceContext list must be empty.", 0, resourceContexts.length);

		// create resource context
		ResourceContext resourceContext = resourceMonitoringService.createContext(name,
				null);

		// check the resourceContext object is not null
		assertNotNull("The resourceContext must not be null.", resourceContext);

		// check the name of the resource contextt
		assertTrue("Name mismatch.", resourceContext.getName().equals(name));

		// check the new ResourceContext has no associated bundles
		assertNotNull("BundleIds list must not be null.", resourceContext.getBundleIds());
		assertTrue("BundleIds list must be empty.", resourceContext.getBundleIds().length == 0);

		// check the new ResourceContext has no associated monitors
		assertNotNull("Monitors list must not be null.", resourceContext.getMonitors());
		assertTrue("Monitors list must be empty.", resourceContext.getMonitors().length == 0);

		// check we receive a ResourceContextEvent
		ResourceContextEvent lastEvent = resourceContextListener.getLastEvent();
		assertNotNull("Last received event must not be null.", lastEvent);
		assertEquals("Event resource context mismatch.", resourceContext, lastEvent.getContext());
		assertEquals("Event type mismatch.", ResourceContextEvent.RESOURCE_CONTEXT_CREATED, lastEvent.getType());

		// check the resource context has been created
		resourceContexts = resourceMonitoringService.listContext();
		assertNotNull("ResourceContext list must not be null.", resourceContexts);
		assertTrue("ResourceContext list length must be equal to 1.", resourceContexts.length == 1);
		assertTrue("ResourceContext mismatch.", resourceContexts[0].equals(resourceContext));

		// get the resource context by name
		ResourceContext retrievedResourceContext = resourceMonitoringService
				.getContext(name);
		assertNotNull("ResourceContext must not be null.", retrievedResourceContext);
		assertTrue("ResourceContext mismatch.", retrievedResourceContext.equals(resourceContext));
	}

	/**
	 * Test create a new ResourceContext with an existing resource context name.
	 * 
	 * @throws ResourceMonitoringServiceException if a pb occurred, e.g. if the
	 *         name is already used.
	 */
	public void testTC2CreationOfAResourceContextWithAnExistingResourceContextName() throws ResourceMonitoringServiceException {
		final String name = "context1";
		// create first instance of context1
		ResourceContext resourceContext = resourceMonitoringService.createContext(name,
				null);
		assertNotNull(resourceContext);

		// retrieve event
		ResourceContextEvent event = resourceContextListener.getLastEvent();

		// check there is one ResourceContext
		assertTrue(resourceMonitoringService.listContext().length == 1);

		// try to create a new ResourceContext with the same name
		try {
			resourceMonitoringService.createContext(name, null);
			fail("A ResourceMonitoringServiceException is expected here.");
		} catch (ResourceMonitoringServiceException e) {
			assertException("A ResourceMonitoringServiceException is expected.", ResourceMonitoringServiceException.class, e);
		}

		// check no event has been sent (i.e the last event is still the
		// same)
		assertTrue(resourceContextListener.getLastEvent() == event);

		// check there is still one ResourceContext
		assertTrue(resourceMonitoringService.listContext().length == 1);
	}

	/**
	 * Test create a ResourceContext based on a template.
	 * 
	 * @throws ResourceMonitorException if resourceMonitor is associated to
	 *         another context or resourceMonitor has been deleted.
	 * @throws ResourceMonitoringServiceException if a pb occurred, e.g. if the
	 *         name is already used.
	 */
	public void testTC3CreationOfAResourceContextBasedOnATemplateResourceContext()
			throws ResourceMonitorException, ResourceMonitoringServiceException {
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

		assertNotNull(resourceContext2);
		assertTrue(resourceContext2.getName().equals(name2));
		assertTrue(resourceContext2.getBundleIds().length == 0);
		ResourceMonitor[] rc2Monitors = resourceContext2.getMonitors();
		assertTrue(rc2Monitors.length == 1);
		assertTrue(rc2Monitors[0].getResourceType().equals(
				ResourceMonitoringService.RES_TYPE_CPU));
	}

	/**
	 * @throws ResourceMonitorException if resourceMonitor is associated to
	 *         another context or resourceMonitor has been deleted.
	 * @throws ResourceMonitoringServiceException if a pb occurred, e.g. if the
	 *         name is already used.
	 */
	public void testTC4CreationOfAResourceContextBasedOnATemplateResourceContextPreviouslyDeleted()
			throws ResourceMonitorException, ResourceMonitoringServiceException {
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

		assertNotNull(resourceContext2);
		assertTrue(resourceContext2.getName().equals(name2));
		assertTrue(resourceContext2.getBundleIds().length == 0);
		ResourceMonitor[] rc2Monitors = resourceContext2.getMonitors();
		assertTrue(rc2Monitors.length == 0);
	}

	/**
	 * Test retrieve the context based on a bundle id.
	 * 
	 * @throws ResourceMonitoringServiceException if a pb occurred, e.g. if the
	 *         name is already used.
	 */
	public void testTC5RetrievingAResourceContextBasedOnABundleIdentifier() throws ResourceMonitoringServiceException {
		final String name = "context1";
		final long bundleId = 1;
		// get ResourceContext associated with bundleId and check bundleId
		// is not associated with a context
		ResourceContext resourceContext = resourceMonitoringService.getContext(bundleId);
		assertNull(resourceContext);

		// create a new context
		ResourceContext resourceContext1 = resourceMonitoringService.createContext(name,
				null);

		// associated bundleId with resourceContext1
		resourceContext1.addBundle(bundleId);

		// get ResourceContext by bundle id
		resourceContext = resourceMonitoringService.getContext(bundleId);
		assertNotNull(resourceContext);
		assertTrue(resourceContext.equals(resourceContext1));

		// try to retrieve the ResourceContext with an unexisting bundle
		resourceContext = resourceMonitoringService.getContext(-1);
		assertNull(resourceContext);
	}

	/**
	 * Test supported types
	 * 
	 * @throws InvalidSyntaxException If the specified filter contains an
	 *         invalid filter expression that cannot be parsed.
	 */
	public void testTC6SupportedTypesOfResources() throws InvalidSyntaxException {
		String[] supportedTypes = resourceMonitoringService.getSupportedTypes();
		assertNotNull(supportedTypes);

		// retrieves all ServiceReference of ResourceMonitorFactory services
		Collection factorySrs = getContext().getServiceReferences(
				ResourceMonitorFactory.class, null);
		assertNotNull(factorySrs);

		// check the number of ServiceReference is the same as the number of
		// supported types
		assertTrue("factorySrs.size(): " + factorySrs.size() + " must be equal to supportedTypes.length: " + supportedTypes.length, factorySrs.size() == supportedTypes.length);

		// iterate over the ServiceReference collection and retrieves each
		// ResourceMonitorFactory service.
		List computedSupportedTypes = new ArrayList();
		for (Iterator it = factorySrs.iterator(); it.hasNext();) {
			ResourceMonitorFactory rmf = (ResourceMonitorFactory) getContext().getService(
					(ServiceReference) it.next());
			// add the type in the list
			computedSupportedTypes.add(rmf.getType());
		}

		assertTrue(computedSupportedTypes.size() == supportedTypes.length);

		// check that each supported types is in the list
		for (int i = 0; i < supportedTypes.length; i++) {
			assertTrue(computedSupportedTypes.contains(supportedTypes[i]));
		}
	}

}
