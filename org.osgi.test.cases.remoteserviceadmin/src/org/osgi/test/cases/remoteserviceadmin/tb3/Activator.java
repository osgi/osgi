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
package org.osgi.test.cases.remoteserviceadmin.tb3;

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class Activator implements BundleActivator {
	BundleContext                  context;
	ServiceTracker tracker;
	BundleTracker  bundleTracker;
	Semaphore sem = new Semaphore(0);
	Semaphore servicesem = new Semaphore(0);

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		teststop();
	}

	/**
	 * Searches for a proxy for A, which was created from an endpoint description
	 * added as file in a bundle.
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {
		Filter filter = context.createFilter("(&(objectClass=" + A.class.getName() +")(newkey=newvalue))");
		tracker = new ServiceTracker(context, filter, null);
		tracker.open();
		
		A service = (A) tracker.waitForService(60000);
		Assert.assertNotNull("no service A found", service);
		
		// call the service
		Assert.assertEquals("A", service.getA());

		tracker.close();

		tracker = new ServiceTracker(context, filter, new ServiceTrackerCustomizer() {
			
			public void removedService(ServiceReference reference, Object service) {
				System.out.println("service " + reference + " was removed");
				
				servicesem.signal();
			}
			
			public void modifiedService(ServiceReference reference, Object service) {
			}
			
			public Object addingService(ServiceReference reference) {
				return reference;
			}
		});
		tracker.open();

		bundleTracker = new BundleTracker(context, Bundle.ACTIVE, new BundleTrackerCustomizer() {
			
			public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
				System.out.println("bundle " + bundle.getSymbolicName() + " was stopped");

				sem.signal();
			}
			
			public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
			}
			
			public Object addingBundle(Bundle bundle, BundleEvent event) {
				if (bundle.getSymbolicName().equals("org.osgi.test.cases.remoteserviceadmin.testbundle")) {
					return bundle;
				}
				return null;
			}
		});
		bundleTracker.open();
	}

	/**
	 * Make sure the service goes away when the endpoint description is removed.
	 * 
	 * @throws Exception
	 */
	private void teststop() throws Exception {
		try {
			Assert.assertTrue("did not receive event that bundle stopped", sem.waitForSignal(60000));

			Assert.assertTrue("did not receive event that service was removed", servicesem.waitForSignal(60000));
		} finally {
			bundleTracker.close();
			tracker.close();
		}
	}
}
