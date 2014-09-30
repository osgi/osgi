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

package org.osgi.test.cases.resourcemonitoring.junit;

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
public class ResourceMonitorTestCase extends DefaultTestBundleControl {

	/**
	 * bundle context.
	 */
	private BundleContext			bundleContext;

	/**
	 * ResourceMonitoringService.
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * cpu factory.
	 */
	private ResourceMonitorFactory	cpuFactory;

	/**
	 * resourceContext
	 */
	private ResourceContext			resourceContext;

	/**
	 * resource context name
	 */
	private final String			resourceContextName	= "context1";

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
		resourceContext = resourceMonitoringService.createContext(resourceContextName,
				null);
	}

	protected void tearDown() throws Exception {
		resourceContext.removeContext(null);
	}

	/**
	 * Check if CPU Resource Monitor Factory is available.
	 */
	public void testCpuFactoryAvailable() {
		assertNotNull(cpuFactory);
	}

	/**
	 * Create Resource Monitor.
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testCreateResourceMonitor() throws ResourceMonitorException {

		// check existing resource monitors
		ResourceMonitor[] monitors = resourceContext.getMonitors();
		assertNotNull(monitors);
		assertTrue(monitors.length == 0);

		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);
		assertNotNull(resourceMonitor);

		// check ResourceContext
		monitors = resourceContext.getMonitors();
		assertTrue(monitors.length == 1);
		assertTrue(monitors[0].equals(resourceMonitor));

		// get monitor from ResourceContext
		ResourceMonitor retrievedMonitor = resourceContext
				.getMonitor(ResourceMonitoringService.RES_TYPE_CPU);
		assertNotNull(retrievedMonitor);
		assertTrue(retrievedMonitor.equals(resourceMonitor));

		// check resource context from monitor
		ResourceContext retrievedRC = resourceMonitor.getContext();
		assertNotNull(retrievedRC);
		assertTrue(retrievedRC.equals(resourceContext));

		// check the newly ResourceMonitor
		assertFalse(resourceMonitor.isEnabled());
		assertFalse(resourceMonitor.isDeleted());
		assertTrue(resourceMonitor.getResourceType().equals(
				cpuFactory.getType()));

		// create again a CPU Resource Monitor for this context
		boolean exception = false;
		try {
			cpuFactory.createResourceMonitor(resourceContext);
		} catch (ResourceMonitorException e) {
			exception = true;
		}
		assertTrue(exception);

		// check the ResourceContext has still one ResourceContext
		monitors = resourceContext.getMonitors();
		assertTrue(monitors.length == 1);
		assertTrue(monitors[0].equals(resourceMonitor));

	}

	/**
	 * Test deleting a ResourceMonitor. Check the monitor is removed from the
	 * context.
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testDeleteResourceMonitor()
			throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// delete ResourceMonitor
		resourceMonitor.delete();

		// check resource context monitors
		ResourceMonitor[] monitors = resourceContext.getMonitors();
		assertNotNull(monitors);
		assertTrue(monitors.length == 0);

		// check ResourceMonitor state
		assertTrue(resourceMonitor.isDeleted());
	}

	/**
	 * Test enabling/disabling a Resource Monitor
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testEnablingAndDisablingAResourceMonitor() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// check the monitor is disabled by default
		assertFalse(resourceMonitor.isEnabled());

		// enable it
		resourceMonitor.enable();

		// check the monitor is enabled
		assertTrue(resourceMonitor.isEnabled());

		// disable it
		resourceMonitor.disable();

		// check the monitor is disabled
		assertFalse(resourceMonitor.isEnabled());

		// delete the monitor
		resourceMonitor.delete();

		// try to enable a deleted monitor ==> expect an IllegalStateException
		boolean exception = false;
		try {
			resourceMonitor.enable();
		} catch (IllegalStateException e) {
			exception = true;
		}
		assertTrue(exception);

		// try to disable it ==> expect an IllegalStateException
		exception = false;
		try {
			resourceMonitor.disable();
		} catch (IllegalStateException e) {
			exception = true;
		}
		assertTrue(exception);

	}

	/**
	 * Test retrieving resource usage
	 * 
	 * @throws ResourceMonitorException
	 */
	public void testRetrieveResourceUsage() throws ResourceMonitorException {
		// create ResourceMonitor
		ResourceMonitor resourceMonitor = cpuFactory
				.createResourceMonitor(resourceContext);

		// retrieves the resource usage
		assertNotNull(resourceMonitor.getUsage());

		// enable it
		resourceMonitor.enable();

		// retrieves the resource usage
		assertNotNull(resourceMonitor.getUsage());

		// disable it
		resourceMonitor.disable();

		// retrieves the resource usage
		assertNotNull(resourceMonitor.getUsage());

		// delete the monitor
		resourceMonitor.delete();

		// try to enable a deleted monitor ==> expect an IllegalStateException
		boolean exception = false;
		try {
			resourceMonitor.getUsage();
		} catch (IllegalStateException e) {
			exception = true;
		}
		assertTrue(exception);

	}

}
