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
package org.osgi.test.cases.remoteserviceadmin.tb5;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class Activator implements BundleActivator {
	BundleContext                  context;
	ServiceTracker tracker;
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
	 * Searches for a proxy for A, which was created from an endpoint description
	 * added as file in a bundle.
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {
		tracker = new ServiceTracker(context, A.class.getName(), null);
		tracker.open();
		
		A service = (A) tracker.waitForService(timeout);
		Assert.assertNotNull("no service A found", service);
		
		// call the service
		Assert.assertEquals("A", service.getA());

		tracker.close();
	}

	/**
	 * Make sure the service goes away when the endpoint description is removed.
	 * 
	 * @throws Exception
	 */
	private void teststop() throws Exception {
	}
}
