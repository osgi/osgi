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

package org.osgi.test.cases.resourcemonitoring.utils;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextListener;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitorException;
import org.osgi.service.resourcemonitoring.ResourceMonitorFactory;

/**
 * A fake resource monitor factory.
 * 
 * @author $Id$
 */
public class FakeResourceMonitorFactory
		implements ResourceMonitorFactory<Object>, ResourceContextListener {

	/**
	 * type of {@link ResourceMonitor} this factory is able to create
	 */
	private final String		factoryType;

	/**
	 * bundle context
	 */
	private final BundleContext	bundleContext;

	/**
	 * Register the factory as a ResourceContextListener to be informed when a ResourceContext is deleted.
	 * ServiceRegistration<ResourceContextListener>
	 */
	private ServiceRegistration< ? >	serviceRegistration;

	/**
	 * Create, and register this FakeResourceMonitorFactory as a service in the OSGi services registry.
	 * 
	 * @param bundleContext
	 * @param factoryType
	 */
	public FakeResourceMonitorFactory(BundleContext bundleContext, String factoryType) {
		this.bundleContext = bundleContext;
		this.factoryType = factoryType;

		// register this factory as a ResourceContextListener.
		// Dictionary<String, Object> properties.
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put(ResourceMonitorFactory.RESOURCE_TYPE_PROPERTY, factoryType);
		serviceRegistration = this.bundleContext.registerService(new String[] { ResourceContextListener.class.getName(),
				ResourceMonitorFactory.class.getName() }, this, properties);
	}

	/**
	 * Unregister the factory from the OSGi services registry.
	 */
	public void stop() {
		serviceRegistration.unregister();
	}

	public String getType() {
		return this.factoryType;
	}

	public void notify(ResourceContextEvent event) {
		// TODO Auto-generated method stub

	}

	public ResourceMonitor<Object> createResourceMonitor(
			ResourceContext resourceContext) throws ResourceMonitorException {
		// TODO Auto-generated method stub
		return null;
	}

}
