/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.jndi.tests;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 * A set of tests for Java EE and jndi integration
 * 
 * @version $Revision$ $Date: 2009-06-13 08:09:54 -0400 (Sat, 13 Jun
 *          2009) $
 */
public class TestJavaEE extends DefaultTestBundleControl {

	public void testApplicationName() throws Exception {
		// Install the bundle that contains the app
		Bundle testBundle = installBundle("service1.jar");
		try {
			// Get a ServiceReference object so we can access the service
			// properties
			ServiceReference ref = testBundle.getBundleContext().getServiceReference(org.osgi.test.cases.jndi.service.ExampleService.class.getName());
			// Grab the property we're looking for
			ref.getProperty("javaee.application.name");
			// Verify we actually received a value
			assertNotNull(ref);
		} finally {
			// Cleanup after the test completes
			uninstallBundle(testBundle);
		}
	}

	public void testApplicationVersion() throws Exception {
		// Install the bundle that contains the app
		Bundle testBundle = installBundle("service1.jar");
		try {
			// Get a ServiceReference object so we can access the service
			// properties
			ServiceReference ref = testBundle.getBundleContext().getServiceReference(org.osgi.test.cases.jndi.service.ExampleService.class.getName());
			// Grab the property we're looking for
			ref.getProperty("javaee.application.version");
			// Verify we actually received a value
			assertNotNull(ref);
		} finally {
			// Cleanup after the test completes
			uninstallBundle(testBundle);
		}
	}

}
