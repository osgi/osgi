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

import javax.naming.Context;
import javax.naming.InitialContext;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.jndi.service.ExampleService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 *
 * A set of tests for jndi interaction with the service registry
 * 
 * @version $Revision$ $Date$
 */
public class TestServiceIntegration extends DefaultTestBundleControl {
	
	public void testServiceLookup() throws Exception {
		// Install the bundle needed for this test
		Bundle testBundle = installBundle("service1.jar");
		// Grab the default initialContext so we can access the service registry
		Context ctx = new InitialContext();
		assertNotNull("The context should not be null", ctx);
		// Lookup the example service
		ExampleService service = (ExampleService) ctx.lookup("osgi:services/org.osgi.test.cases.jndi.service.ExampleService");
		// Verify that we actually got the service
		assertNotNull(service);
		// Cleanup after the test completes
		ctx.close();
		uninstallBundle(testBundle);
	}
	
	
	public void testServiceNameProperty() throws Exception {
		// Install the bundles need for this test
		Bundle testBundle = installBundle("service1.jar");
		// Grab the default InitialContext so we can access the service registry
		Context ctx = new InitialContext();
		assertNotNull("The context should not be null", ctx);
		// Lookup the example service using the service name
		ExampleService service = (ExampleService) ctx.lookup("osgi:services/ExampleService");
		// Verify that we actually got the service
		assertNotNull(service);
		// Cleanup after the test completes
		ctx.close();
		uninstallBundle(testBundle);
	}
	
	
}
