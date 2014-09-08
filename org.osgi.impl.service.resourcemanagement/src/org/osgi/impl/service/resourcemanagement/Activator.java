/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

package org.osgi.impl.service.resourcemanagement;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleManager;
import org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleManagerImpl;
import org.osgi.service.resourcemanagement.ResourceManager;

/**
 * Activator
 */
public class Activator implements BundleActivator {

	/**
	 * bundle manager.
	 */
	private BundleManager					bundleManager;

	/**
	 * resource manager.
	 */
	private ResourceManagerImpl				resourceManager;

	/**
	 * event notifier.
	 */
	private ResourceContextEventNotifier	eventNotifier;

	/**
	 * service registration for Resource Manager service.
	 */
	private ServiceRegistration				resourceManagerServiceRegistration;

	/**
	 * bundle context.
	 */
	private BundleContext					bundleContext;

	/**
	 * @param context
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Start org.osgi.impl.service.resourcemanagementorg.osgi.impl.service.resourcemanagement.Activator");

		bundleContext = context;

		// threadManager = new ThreadManagerImpl(bundleManager);
		// threadManager.start(context);

		eventNotifier = new ResourceContextEventNotifierImpl();
		eventNotifier.start(context);
		
		startResourceManager();
	}

	/**
	 * @param context
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		stopResourceManager();

		eventNotifier.stop(context);
		eventNotifier = null;
	}

	/**
	 * Start the ResourceManager.
	 */
	private void startResourceManager() {
		bundleManager = new BundleManagerImpl();
		bundleManager.start(bundleContext);

		resourceManager = new ResourceManagerImpl(bundleManager, eventNotifier);
		resourceManager.start(bundleContext);

		resourceManagerServiceRegistration = bundleContext
				.registerService(ResourceManager.class.getName(),
						resourceManager, null);
	}

	/**
	 * Stop the ResourceManager service.
	 */
	private void stopResourceManager() {
		if (resourceManagerServiceRegistration != null) {
			resourceManagerServiceRegistration.unregister();
			resourceManagerServiceRegistration = null;
		}

		if (resourceManager != null) {
			resourceManager.stop(bundleContext);
			resourceManager = null;

			bundleManager.stop();
			bundleManager = null;
		}
	}

}
