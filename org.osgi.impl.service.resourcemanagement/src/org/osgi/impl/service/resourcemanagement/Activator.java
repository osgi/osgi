/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
import org.osgi.impl.service.resourcemanagement.persistency.Persistence;
import org.osgi.impl.service.resourcemanagement.persistency.PersistenceImpl;
import org.osgi.impl.service.resourcemanagement.threadmanager.ThreadManager;
import org.osgi.impl.service.resourcemanagement.threadmanager.ThreadManagerImpl;
import org.osgi.service.resourcemanagement.ResourceManager;

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 */
public class Activator implements BundleActivator {

	/**
	 * bundle manager.
	 */
	private BundleManager bundleManager;

	/**
	 * thread manager.
	 */
	private ThreadManager threadManager;

	/**
	 * resource manager.
	 */
	private ResourceManagerImpl resourceManager;

	/**
	 * event notifier.
	 */
	private ResourceContextEventNotifier eventNotifier;

	/**
	 * persistence.
	 */
	private Persistence persistence;

	/**
	 * service registration for Resource Manager service.
	 */
	private ServiceRegistration<ResourceManager> resourceManagerServiceRegistration;

	/**
	 * @param context
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		persistence = new PersistenceImpl();

		bundleManager = new BundleManagerImpl();
		bundleManager.start(context);

		threadManager = new ThreadManagerImpl(bundleManager);
		threadManager.start(context);

		eventNotifier = new ResourceContextEventNotifierImpl();
		eventNotifier.start(context);

		resourceManager = new ResourceManagerImpl(bundleManager, eventNotifier,
				threadManager);
		// load persisted resource context configuration
		resourceManager.restoreContext(persistence.load(context));
		resourceManager.start(context);



		resourceManagerServiceRegistration = context.registerService(
				ResourceManager.class, resourceManager, null);

	}

	/**
	 * @param context
	 * @throws java.lang.Exception
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {

		resourceManagerServiceRegistration.unregister();
		resourceManagerServiceRegistration = null;
		persistence.persist(context, resourceManager.listContext());
		resourceManager.stop(context);
		resourceManager = null;

		eventNotifier.stop(context);
		eventNotifier = null;

		threadManager.stop(context);
		threadManager = null;

		bundleManager.stop();
		bundleManager = null;

	}


}
