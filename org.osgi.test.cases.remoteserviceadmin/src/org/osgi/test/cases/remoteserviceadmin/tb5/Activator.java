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
package org.osgi.test.cases.remoteserviceadmin.tb5;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class Activator implements BundleActivator {
	BundleContext                  context;
	ServiceTracker<A,A>	tracker;
	long timeout;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		timeout = OSGiTestCaseProperties.getLongProperty("rsa.tck.timeout",
				300000L);
		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
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
		tracker = new ServiceTracker<>(context, A.class, null);
		tracker.open();

		A service = Tracker.waitForService(tracker, timeout);
		assertNotNull("no service A found", service);

		// call the service

		// Marc: TB4 registers the implementation of A from tb2
		// t2 reimplements getA to return "this is A" not "A"

		assertEquals("this is A", service.getA());

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
