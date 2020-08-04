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

import java.util.Collection;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextException;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author $Id$
 */
public class TC4_ResourceMonitorTestCase extends DefaultTestBundleControl {

	/**
	 * ResourceMonitoringService.
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * cpu factory.
	 */
	private ResourceMonitorFactory< ? >	cpuFactory;

	/**
	 * resourceContext
	 */
	private ResourceContext				resourceContext;

	/**
	 * resource context name
	 */
	private final String				resourceContextName	= "context1";

	protected void setUp() throws Exception {
		super.setUp();
		ServiceReference<ResourceMonitoringService> serviceReference = getContext()
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = getContext()
				.getService(serviceReference);

		StringBuffer filter = new StringBuffer();
		filter.append("(&(");
		filter.append(Constants.OBJECTCLASS);
		filter.append("=");
		filter.append(ResourceMonitorFactory.class.getName());
		filter.append(")(");
		filter.append(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY);
		filter.append("=");
		filter.append(ResourceMonitoringService.RES_TYPE_CPU);
		filter.append("))");
		try {
			@SuppressWarnings({
					"rawtypes", "unchecked"
			})
			Collection<ServiceReference<ResourceMonitorFactory< ? >>> factoryReferences = (Collection) getContext()
					.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());
			if (factoryReferences != null) {
				if (factoryReferences.size() > 0) {
					ServiceReference<ResourceMonitorFactory< ? >> factoryReference = factoryReferences
							.iterator()
							.next();
					cpuFactory = getContext()
							.getService(factoryReference);

				}
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("Can not get the already existing OSGi service resource monitor factories.");
		}

		resourceContext = resourceMonitoringService.createContext(resourceContextName, null);

		assertNotNull("CpuFactory must not be null.", cpuFactory);
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		resourceContext.removeContext(null);
	}

	/**
	 * Test case 1 : creation of a ResourceMonitor.
	 * 
	 * This test case tests the creation of a ResourceMonitor through a ResourceMonitorFactory.
	 * 
	 * @throws ResourceMonitorException
	 *             , see {@link ResourceMonitorFactory#createResourceMonitor(ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceContext#getMonitors()}
	 */
	public void testCreationOfResourceMonitor() throws ResourceMonitorException, ResourceContextException {

		// check existing resource monitors
		ResourceMonitor< ? >[] monitors = resourceContext.getMonitors();
		assertNotNull("Monitors list must not be null.", monitors);
		assertEquals("Monitors list mismatch.", 0, monitors.length);

		// create ResourceMonitor
		ResourceMonitor< ? > resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);
		assertNotNull("ResourceMonitor must not be null.", resourceMonitor);

		// check ResourceContext
		monitors = resourceContext.getMonitors();
		assertEquals("ResourceMonitors list mismatch.", 1, monitors.length);
		assertEquals("ResourceMonitor mismatch.", resourceMonitor, monitors[0]);

		// get monitor from ResourceContext
		ResourceMonitor< ? > retrievedMonitor = resourceContext
				.getMonitor(ResourceMonitoringService.RES_TYPE_CPU);
		assertNotNull("ResourceMonitor must not be null.", retrievedMonitor);
		assertEquals("ResourceMonitor mismatch.", resourceMonitor, retrievedMonitor);

		// check resource context from monitor
		ResourceContext retrievedRC = resourceMonitor.getContext();
		assertNotNull("ResourceContext must not be null.", retrievedRC);
		assertEquals("ResourceContext mismatch.", resourceContext, retrievedRC);

		// check the newly ResourceMonitor
		assertFalse("ResourceMonitor must not be enabled.", resourceMonitor.isEnabled());
		assertFalse("ResourceMonitor must not be deleted.", resourceMonitor.isDeleted());
		assertEquals("Type mismatch.", cpuFactory.getType(), resourceMonitor.getResourceType());

		// create again a CPU Resource Monitor for this context
		try {
			cpuFactory.createResourceMonitor(resourceContext);
			fail("A ResourceMonitorException is expected here.");
		} catch (ResourceMonitorException e) {
			log("Expected exception: " + e.getMessage());
		}

		// check the ResourceContext has still one ResourceMonitor
		monitors = resourceContext.getMonitors();
		assertEquals("ResourceMonitors list mismatch.", 1, monitors.length);
		assertEquals("ResourceMonitor mismatch.", resourceMonitor, monitors[0]);
	}

	/**
	 * Test case 2 : deletion of a ResourceMonitor from a ResourceContext.
	 * 
	 * This test case validates the deletion of ResourceMonitor from a ResourceContext (check that the monitor is removed from the context).
	 * 
	 * @throws ResourceMonitorException
	 *             , see {@link ResourceMonitorFactory#createResourceMonitor(ResourceContext)}
	 * @throws ResourceContextException
	 *             , see {@link ResourceMonitor#delete()} {@link ResourceContext#getMonitors()}
	 */
	public void testDeletionOfAResourceMonitorFromAResourceContext() throws ResourceMonitorException, ResourceContextException {
		// create ResourceMonitor
		ResourceMonitor< ? > resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// delete ResourceMonitor
		resourceMonitor.delete();

		// check resource context monitors
		ResourceMonitor< ? >[] monitors = resourceContext.getMonitors();
		assertNotNull("ResourceMonitors list must not be null.", monitors);
		assertEquals("ResourceMonitors list mismatch.", 0, monitors.length);

		// check ResourceMonitor state
		assertTrue("ResourceMonitor must have been deleted.", resourceMonitor.isDeleted());
	}

	/**
	 * Test case 3 : enabling and disabling a ResourceMonitor.
	 * 
	 * This test case checks the enabling/disabling of a ResourceMonitor.
	 * 
	 * @throws ResourceMonitorException
	 *             , see {@link ResourceMonitorFactory#createResourceMonitor(ResourceContext)} {@link ResourceMonitor#enable()}
	 *             {@link ResourceMonitor#disable()} {@link ResourceMonitor#delete()}
	 */
	public void testEnablingAndDisablingAResourceMonitor() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor< ? > resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// check the monitor is disabled by default
		assertFalse("ResourceMonitor must not be enabled.", resourceMonitor.isEnabled());

		// enable it
		resourceMonitor.enable();

		// check the monitor is enabled
		assertTrue("ResourceMonitor must be enabled.", resourceMonitor.isEnabled());

		// disable it
		resourceMonitor.disable();

		// check the monitor is disabled
		assertFalse("ResourceMonitor must not be enabled.", resourceMonitor.isEnabled());

		// delete the monitor
		resourceMonitor.delete();

		// try to enable a deleted monitor ==> expect a
		// ResourceMonitorException
		try {
			resourceMonitor.enable();
			fail("A ResourceMonitorException is expected.");
		} catch (ResourceMonitorException e) {
			log("Expected exception: " + e.getMessage());
		}

		// try to disable it ==> expect a ResourceMonitorException
		try {
			resourceMonitor.disable();
			fail("A ResourceMonitorException is expected.");
		} catch (ResourceMonitorException e) {
			log("Expected exception: " + e.getMessage());
		}
	}

	/**
	 * Test case 4 : retrieving a resource usage.
	 * 
	 * This use case checks the retrieving of the resource usage a ResourceContext is consuming.
	 * 
	 * @throws ResourceMonitorException
	 *             , see {@link ResourceMonitorFactory#createResourceMonitor(ResourceContext)} {@link ResourceMonitor#getUsage()}
	 *             {@link ResourceMonitor#enable()} {@link ResourceMonitor#disable()} {@link ResourceMonitor#delete()}
	 */
	public void testRetrievingResourceUsage() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor< ? > resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// retrieves the resource usage
		assertNotNull("Usage must not be null.", resourceMonitor.getUsage());

		// enable it
		resourceMonitor.enable();

		// retrieves the resource usage
		assertNotNull("Usage must not be null.", resourceMonitor.getUsage());

		// disable it
		resourceMonitor.disable();

		// retrieves the resource usage
		assertNotNull("Usage must not be null.", resourceMonitor.getUsage());

		// delete the monitor
		resourceMonitor.delete();

		// try to enable a deleted monitor ==> expect a ResourceMonitorException
		try {
			resourceMonitor.getUsage();
			fail("A ResourceMonitorException is expected.");
		} catch (ResourceMonitorException e) {
			log("Expected exception: " + e.getMessage());
		}
	}

}
