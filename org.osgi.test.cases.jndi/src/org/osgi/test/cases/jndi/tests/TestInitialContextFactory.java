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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.jndi.provider.CTContext;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 * A set of tests for the access and use of InitialContextFactory and
 * InitialContextFactoryBuilder instances
 * 
 * @version $Revision$ $Date: 2009-07-07 15:38:11 -0400 (Tue, 07 Jul
 *          2009) $
 */
public class TestInitialContextFactory extends DefaultTestBundleControl {

	public void testSpecificInitialContextFactory() throws Exception {
		// Install the bundles needed for this test
		Bundle testBundle = installBundle("initialContextFactory1.jar");
		int invokeCountBefore = CTContext.getInvokeCount();
		// Setup the environment for grabbing the specific initialContextFactory
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab the initialContext
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
			int invokeCountAfter = CTContext.getInvokeCount();
			if (!(invokeCountAfter > invokeCountBefore)) {
				ctx.close();
				fail("The correct Context object was not found");
			}
			Object testObject = (Object) ctx.lookup("testObject");
			assertNotNull(testObject);
		} finally {
			// Cleanup after the test completes
			ctx.close();
			uninstallBundle(testBundle);
		}
	}

	public void testUnspecifiedInitialContextFactory() throws Exception {
		// Install the bundles needed for this test
		Bundle testBundle = installBundle("initialContextFactory1.jar");
		// We don't setup the environment because we want to see if the
		// appropriate context factory is returned even if it isn't specified
		int invokeCountBefore = CTContext.getInvokeCount();
		InitialContext ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
			int invokeCountAfter = CTContext.getInvokeCount();
			if (!(invokeCountAfter > invokeCountBefore)) {
				ctx.close();
				fail("The correct Context object was not found");
			}
			Object testObject = ctx.lookup("testObject");
			assertNotNull(testObject);
		} finally {
			// Cleanup after the test completes
			ctx.close();
			uninstallBundle(testBundle);
		}
	}

	public void testInitialContextFactoryBuilder() throws Exception {
		// Install the bundles needed for this test
		Bundle testBundle = installBundle("initialContextFactoryBuilder1.jar");
		// Try to get an initialContext object using the builder
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		int invokeCountBefore = CTContext.getInvokeCount();
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			// Verify that we actually received the InitialContext
			ctx.bind("testObject", new Object());
			int invokeCountAfter = CTContext.getInvokeCount();
			if (!(invokeCountAfter > invokeCountBefore)) {
				ctx.close();
				fail("The correct Context object was not found");
			}
			Object testObject = ctx.lookup("testObject");
			assertNotNull(testObject);
		} finally {
			// Cleanup after the test completes
			ctx.close();
			uninstallBundle(testBundle);
		}
	}

	public void testInitialContextFactoryRemoval() throws Exception {
		// Install the bundle that has the test provider implementations
		Bundle testBundle = installBundle("initialContextFactory1.jar");
		// Uninstall the bundle now so the provider implementations are
		// unregistered
		uninstallBundle(testBundle);
		// Try to get a context using the just removed initialContextFactory
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
		} catch (javax.naming.NoInitialContextException ex) {
			return;
		} finally {
			// If we don't get the exception, then this test fails
			ctx.close();
		}
		failException("testInitialContextFactoryRemoval failed, ", javax.naming.NoInitialContextException.class);
	}

	public void testInitialContextFactoryBuilderRemoval() throws Exception {
		// Install the bundle that has the test provider implementations
		Bundle testBundle = installBundle("initialContextFactoryBuilder1.jar");
		// Uninstall the bundle now so the provider implementations are
		// unregistered
		uninstallBundle(testBundle);
		// Try to grab the initialContextFactory. We should get a
		// NullPointerException
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
		} catch (NullPointerException npe) {
			return;
		} finally {
			// If we don't get the exception, then this test fails
			ctx.close();
		}
		failException("testInitialContextFactoryBuilderRemoval failed, ", java.lang.NullPointerException.class);
	}

	public void testNoInitialContextFound() throws Exception {
		// Setup the environment for grabbing the specific initialContextFactory
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Try to grab a context from the specified initialContextFactory. This
		// should throw an exception.
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			ctx.lookup("testObject");
		} catch (javax.naming.NoInitialContextException ex) {
			return;
		} finally {
			// If we don't get the exception, then this test fails
			ctx.close();
		}
		failException("testNoInitialContextFound failed, ", javax.naming.NoInitialContextException.class);
	}

	public void testServiceRanking() throws Exception {
		// Install the necessary bundles
		Bundle testBundle = installBundle("initialContextFactory2.jar");
		// Use the default context to grab one of the factories and make sure
		// it's the right one
		InitialContext ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null", ctx);
			CTInitialContextFactory ctf = (CTInitialContextFactory) ctx.lookup("osgi:services/org.osgi.test.cases.jndi.provider.CTInitialContextFactory");
			// Let's grab a context instance and check the environment
			Hashtable ctxEnv = ctf.getInitialContext(null).getEnvironment();
			if (!ctxEnv.containsKey("test1")) {
				fail("The right context was not returned");
			}
		} finally {
			ctx.close();
			uninstallBundle(testBundle);
		}
	}

}
