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
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;
import org.osgi.test.cases.resourcemonitoring.utils.FakeResourceMonitorFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @author $Id$
 */
public class TC7_ResourceMonitorFactoryTestCase extends DefaultTestBundleControl {

	/**
	 * bundle context.
	 */
	private BundleContext				bundleContext;

	/**
	 * ResourceMonitoringService.
	 */
	private ResourceMonitoringService	resourceMonitoringService;

	/**
	 * resourceContext
	 */
	private ResourceContext				resourceContext;

	/**
	 * resource context name
	 */
	private final String				resourceContextName	= "context1";

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		ServiceReference serviceReference = this.bundleContext
				.getServiceReference(ResourceMonitoringService.class);
		resourceMonitoringService = (ResourceMonitoringService) this.bundleContext.getService(serviceReference);
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
	 * Test case 1 : Adding another resource monitor factory, and test that
	 * everything worked well.
	 */
	public void testTC1AddingAnotherResourceMonitorFactory() {
		StringBuffer filter = new StringBuffer();
		filter.append("(&(");
		filter.append(Constants.OBJECTCLASS);
		filter.append("=");
		filter.append(ResourceMonitorFactory.class.getName());
		filter.append("))");
		Integer totalNumberOfExistingResourceMonitorFactories = null;
		try {
			Collection factoryReferences = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());

			// Keep the total number of existing resource monitor factory/ies.
			totalNumberOfExistingResourceMonitorFactories = new Integer(factoryReferences.size());
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("Can not get the already existing OSGi service resource monitor factories.");
		}

		// Create, and add another resource monitor factory in the OSGi services
		// registry.
		FakeResourceMonitorFactory fakeResourceMonitorFactory = new FakeResourceMonitorFactory(bundleContext, ResourceMonitoringService.RES_TYPE_DISK_STORAGE);

		// Check that the new total number of existing resource monitor
		// factory/ies is equal to the previous one plus one.
		int newTotalNumberOfExistingResourceMonitorFactories;
		try {
			Collection factoryReferences = bundleContext.getServiceReferences(
					ResourceMonitorFactory.class, filter.toString());

			// Keep the new total number of existing resource monitor
			// factory/ies.
			newTotalNumberOfExistingResourceMonitorFactories = factoryReferences.size();
			assertEquals("TotalNumberOfExistingResourceMonitorFactories mismatch.", totalNumberOfExistingResourceMonitorFactories.intValue() + 1, newTotalNumberOfExistingResourceMonitorFactories);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			fail("Can not get the already existing OSGi service resource monitor factories.");
		}

		// Remove the just added resource monitor factory from the OSGi services
		// registry.
		fakeResourceMonitorFactory.stop();
	}

}
