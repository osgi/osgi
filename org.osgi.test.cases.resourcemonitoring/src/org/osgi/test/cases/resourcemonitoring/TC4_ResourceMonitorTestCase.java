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

import java.util.Collection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * see Conformance Tests description.odt file.
 * 
 * @author $Id$
 */
public class TC4_ResourceMonitorTestCase extends DefaultTestBundleControl {

	/**
	 * bundle context.
	 */
	private BundleContext				bundleContext;

	/**
	 * ResourceMonitoringService.
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * cpu factory.
	 */
	private ResourceMonitorFactory		cpuFactory;

	/**
	 * resourceContext
	 */
	private ResourceContext				resourceContext;

	/**
	 * resource context name
	 */
	private final String				resourceContextName	= "context1";

	public void setBundleContext(BundleContext context) {
		bundleContext = context;

		ServiceReference serviceReference = bundleContext
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = (ResourceMonitoringService) bundleContext.getService(serviceReference);

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
			Collection factoryReferences = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());
			if (factoryReferences != null) {
				if (factoryReferences.size() > 0) {
					ServiceReference factoryReference = (ServiceReference) factoryReferences
							.iterator().next();
					cpuFactory = (ResourceMonitorFactory) bundleContext.getService(factoryReference);

				}
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();

		resourceContext = resourceMonitoringService.createContext(resourceContextName,
				null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		resourceContext.removeContext(null);
	}

	/**
	 * Check if a CPU Resource Monitor Factory is available.
	 */
	public void testTC1CheckIfACPUResourceMonitorFactoryIsAvailable() {
		assertNotNull("CpuFactory must not be null.", cpuFactory);
	}

	/**
	 * Create Resource Monitor.
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testTC2CreationOfAResourceMonitor() throws ResourceMonitorException {

		// check existing resource monitors
		ResourceMonitor[] monitors = resourceContext.getMonitors();
		assertNotNull("Monitors list must not be null.", monitors);
		assertEquals("Monitors list mismatch.", 0, monitors.length);

		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);
		assertNotNull("ResourceMonitor must not be null.", resourceMonitor);

		// check ResourceContext
		monitors = resourceContext.getMonitors();
		assertEquals("ResourceMonitors list mismatch.", 1, monitors.length);
		assertEquals("ResourceMonitor mismatch.", resourceMonitor, monitors[0]);

		// get monitor from ResourceContext
		ResourceMonitor retrievedMonitor = resourceContext
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
			fail("A ResourceMonitoringServiceException is expected here.");
		} catch (ResourceMonitorException e) {
			log("Expected exception: ");
			e.printStackTrace();
		}

		// check the ResourceContext has still one ResourceContext
		monitors = resourceContext.getMonitors();
		assertEquals("ResourceMonitors list mismatch.", 1, monitors.length);
		assertEquals("ResourceMonitor mismatch.", resourceMonitor, monitors[0]);
	}

	/**
	 * Test deleting a ResourceMonitor. Check the monitor is removed from the
	 * context.
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testTC3DeletionOfAResourceMonitorFromAResourceContext()
			throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// delete ResourceMonitor
		resourceMonitor.delete();

		// check resource context monitors
		ResourceMonitor[] monitors = resourceContext.getMonitors();
		assertNotNull("ResourceMonitors list must not be null.", monitors);
		assertEquals("ResourceMonitors list mismatch.", 0, monitors.length);

		// check ResourceMonitor state
		assertTrue("ResourceMonitor must have been deleted.", resourceMonitor.isDeleted());
	}

	/**
	 * Test enabling/disabling a Resource Monitor
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testTC4EnablingAndDisablingAResourceMonitor() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
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

		// try to enable a deleted monitor ==> expect an IllegalStateException
		try {
			resourceMonitor.enable();
			fail("An IllegalStateException is expected.");
		} catch (IllegalStateException e) {
			log("Expected exception: ");
			e.printStackTrace();
		}

		// try to disable it ==> expect an IllegalStateException
		try {
			resourceMonitor.disable();
			fail("An IllegalStateException is expected.");
		} catch (IllegalStateException e) {
			log("Expected exception: ");
			e.printStackTrace();
		}
	}

	/**
	 * Test retrieving resource usage
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testTC5RetrievingAResourceUsage() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
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

		// try to enable a deleted monitor ==> expect an IllegalStateException
		try {
			resourceMonitor.getUsage();
			fail("An IllegalStateException is expected.");
		} catch (IllegalStateException e) {
			log("Expected exception: ");
			e.printStackTrace();
		}
	}

}
