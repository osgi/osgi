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
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.jndi.service.ExampleService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 * A set of tests for jndi interaction with the service registry
 * 
 * @version $Revision$ $Date: 2009-07-08 14:07:59 -0400 (Wed, 08 Jul
 *          2009) $
 */
public class TestServiceIntegration extends DefaultTestBundleControl {

	public void testServiceLookup() throws Exception {
		// Install the bundle needed for this test
		Bundle serviceBundle = installBundle("service1.jar");
		// Grab the default initialContext so we can access the service registry
		Context ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null", ctx);
			// Lookup the example service
			ExampleService service = (ExampleService) ctx.lookup("osgi:service/org.osgi.test.cases.jndi.service.ExampleService");
			// Verify that we actually got the service
			assertNotNull(service);
			service.testMethod();
			// Cleanup after the test completes
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(serviceBundle);
		}
	}
	
	public void testServiceLookupWithRebinding() throws Exception {
		// Install the bundle needed for this test
		Bundle serviceBundle = installBundle("service1.jar");
		ExampleService service = null;
		// Grab the default initialContext so we can access the service registry
		Context ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null", ctx);
			// Lookup the example service
			service = (ExampleService) ctx.lookup("osgi:service/org.osgi.test.cases.jndi.service.ExampleService");
			// Verify that we actually got the service
			assertNotNull(service);
			service.testMethod();
			// Remove the service from the registry
			uninstallBundle(serviceBundle);
			// Try to access the service.  This should throw an exception
			service.testMethod();
		} catch (ServiceException ex) {
			// Ignore this for now.  It's expected.
			System.out.println("Caught ServiceException in testServiceLookupWithRebinding and ignored it as it was an expected error.");
		}
		
		// Reinstall a bundle containing the service used by the proxy class
		installBundle("service1.jar");
		// See if the rebinding occurs as it should.
		try {
			service.testMethod();
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			
			if (serviceBundle.getState() == Bundle.INSTALLED) {
				uninstallBundle(serviceBundle);
			}
		}
	}
	
	public void testServiceLookupWithoutRebinding() throws Exception {
		// Install the bunlde needed for this test
		Bundle serviceBundle = installBundle("service1.jar");
		// Grab the default initialContext so we can access the service registry
		Context ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null" , ctx);
			// Lookup the example service
			ExampleService service = (ExampleService) ctx.lookup("osgi:service/org.osgi.test.cases.jndi.service.ExampleService");
			// Verify that we actually got the service
			assertNotNull(service);
			service.testMethod();
			// Remove the service from the registry
			uninstallBundle(serviceBundle);
			// Try to access the service.  This should throw an exception.
			service.testMethod();
		} catch (ServiceException ex) {
			if (ex.getType() != ServiceException.UNREGISTERED) {
				fail("testServiceLookupWithoutRebinding failed with wrong ServiceException type.  ServiceException was not of type UNREGISTERED.");
				return;
			}
			pass("org.osgi.framework.ServiceException caught in testServiceLookupWithoutRebinding: SUCCESS");
			return;
		} finally {
			if (ctx != null) {
				ctx.close();
			}

			if (serviceBundle.getState() == Bundle.INSTALLED) {
				uninstallBundle(serviceBundle);
			}
		}
		
		failException("testServiceLookupWithoutRebinding failed", org.osgi.framework.ServiceException.class);
	}
	
	public void testMultipleServiceLookup() throws Exception {
		// Install the bundle needed for this test
		Bundle serviceBundle = installBundle("service2.jar");
		// Grab the default initialContext so we can access the service registry
		Context ctx = new InitialContext();
		Context serviceListContext = null;
		try {
			assertNotNull("The context should not be null", ctx);
			// Lookup the matching services
			serviceListContext = (Context) ctx.lookup("osgi:servicelist/org.osgi.test.cases.jndi.service.ExampleService");
			// Verify we received a context
			assertNotNull("The context should not be null", serviceListContext);
			// Check that the services we were expecting were found
			ServiceReference[] expectedServices = serviceBundle.getRegisteredServices();
			for (int i=0; i < expectedServices.length; i++) {
				ExampleService service = (ExampleService) serviceListContext.lookup(((Long)expectedServices[i].getProperty("service.id")).toString());
				// We should find a corresponding service for each registered service from the bundle
				assertNotNull("Could not find one of the expected services in the returned context", service);
			}
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			if (serviceListContext != null) {
				serviceListContext.close();
			}
			uninstallBundle(serviceBundle);
		}
		
	}
	
	public void testMultipleServiceLookupWithRebinding() throws Exception {
		// Install the bundle needed for this test
		Bundle serviceBundle = installBundle("service1.jar");
		// Grab the default initialContext so we can access the service registry
		Context ctx = new InitialContext();
		Context serviceListContext = null;
		ExampleService service = null;
		try {
			assertNotNull("The context should not be null", ctx);
			// Lookup the matching services
			serviceListContext = (Context) ctx.lookup("osgi:servicelist/org.osgi.test.cases.jndi.service.ExampleService");
			// Verify we received the context
			assertNotNull("The context should not be null", serviceListContext);
			// Get the service.id for the registered service we expect to get from serviceListContext
			ServiceReference[] expectedServices = serviceBundle.getRegisteredServices();
			ServiceReference exampleServiceReference = expectedServices[0];
			// Lookup the service in the returned serviceListContext
			service = (ExampleService) serviceListContext.lookup(((Long)exampleServiceReference.getProperty("service.id")).toString());
			// Remove the service that is being proxied
			uninstallBundle(serviceBundle);
			// Try to call a method for this class
			service.testMethod();	
		} catch (ServiceException ex) {
			if (ex.getType() != ServiceException.UNREGISTERED) {
				fail("testMultipleServiceLookupWithRebinding failed with wrong ServiceException type.  ServiceException was not of type UNREGISTERED.");
				return;
			}
			// As long as we get the correct exception, do nothing else since there is still more testing.
		} 
		
		// Reinstall the service bundle
		installBundle("service1.jar");
		
		try {
			// Try to use the proxied service again.  This service should not have been rebound so we should still get an exception
			service.testMethod();
		} catch (ServiceException ex) {
			if (ex.getType() != ServiceException.UNREGISTERED) {
				fail("testMultipleServiceLookupWithRebinding failed with wrong ServiceException type.  ServiceException was not of type UNREGISTERED.");
				return;
			}
			pass("org.osgi.framework.ServiceException caught in testMultipleServiceLookupWithRebinding: SUCCESS");
			return;
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			if (serviceListContext != null) {
				serviceListContext.close();
			}
			if (serviceBundle.getState() == Bundle.INSTALLED) {
				uninstallBundle(serviceBundle);
			}
		}
		
		failException("testMultipleServiceLookupWithRebinding failed", org.osgi.framework.ServiceException.class);
	}

	public void testBundleContextLookup() throws Exception {
		// Grab the default initialContext so we can access the service registry
		Context ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null", ctx);
			// Lookup the bundle context
			BundleContext bundleCtx = (BundleContext) ctx.lookup("osgi:framework/bundleContext");
			assertNotNull("The bundle context should not be null", bundleCtx);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
	}
	
	public void testServiceNameProperty() throws Exception {
		// Install the bundles need for this test
		Bundle serviceBundle = installBundle("service1.jar");
		// Grab the default InitialContext so we can access the service registry
		Context ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null", ctx);
			// Lookup the example service using the service name
			ExampleService service = (ExampleService) ctx.lookup("osgi:service/ExampleService");
			// Verify that we actually got the service
			assertNotNull(service);
		} finally {
			// Cleanup after the test completes
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(serviceBundle);
		}
	}

}
