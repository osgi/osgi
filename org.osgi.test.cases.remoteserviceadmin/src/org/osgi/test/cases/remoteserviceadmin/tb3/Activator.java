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
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class Activator implements BundleActivator, ServiceListener {
	BundleContext                  context;
	ServiceTracker tracker;
	BundleTracker  bundleTracker;
	Semaphore sem = new Semaphore(0);
	Semaphore servicesem = new Semaphore(0);
	long timeout;
	int  factor;

	public Activator() {
		timeout = Long.getLong("rsa.ct.timeout", 300000L);
		factor = Integer.getInteger("rsa.ct.timeout.factor", 3);
	}
	
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
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	public void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.UNREGISTERING) {
			System.out.println("service " + event.getServiceReference() + " is unregistered");
			
			servicesem.signal();
		}
	}

	/**
	 * Searches for a proxy for A, which was created from an endpoint description
	 * added as file in a bundle.
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {
		Filter filter = context.createFilter("(&(objectClass=" + A.class.getName() +")(newkey=newvalue))");
		
		context.addServiceListener(this, filter.toString());
		
		tracker = new ServiceTracker(context, filter, null);
		tracker.open();
		
		A service = (A) tracker.waitForService(timeout);
		Assert.assertNotNull("no service A found", service);
		
		// call the service
		Assert.assertEquals("A", service.getA());

		tracker.close();

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
			sem.waitForSignal(timeout * factor);

			servicesem.waitForSignal(timeout * factor);
		} finally {
			bundleTracker.close();
		}
	}
}
