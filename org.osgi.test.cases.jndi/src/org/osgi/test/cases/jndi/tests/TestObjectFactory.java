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
import javax.naming.NamingException;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.jndi.provider.CTObjectFactory;
import org.osgi.test.cases.jndi.provider.CTReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 * A set of tests for the access and use of ObjectFactory and
 * ObjectFactoryBuilder instances
 * 
 * @version $Revision$ $Date: 2009-07-08 12:50:57 -0400 (Wed, 08 Jul
 *          2009) $
 */
public class TestObjectFactory extends DefaultTestBundleControl {

	public void testReferenceLookup() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("objectFactory1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt to
		// correctly lookup the reference.
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osgi.test.cases.jndi.provider.CTInitialContextFactory");
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference("java.lang.String", CTObjectFactory.class.getName());
			ctx.bind("reference", reference);
			String str = (String) ctx.lookup("reference");
			assertNotNull(str);
		} finally {
			// Cleanup after the test completes
			ctx.close();
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}

	public void testReferenceLookupWithBuilder() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("objectFactoryBuilder1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt to
		// correctly lookup the reference.
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osgi.test.cases.jndi.provider.CTInitialContextFactory");
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference("java.lang.String");
			ctx.bind("reference", reference);
			String str = (String) ctx.lookup("reference");
			assertNotNull(str);
		} finally {
			// Cleanup after the test completes
			ctx.close();
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}

	public void testObjectFactoryRemoval() throws Exception {
		// Install the bundle that has the test provider implementations
		Bundle testBundle = installBundle("objectFactory1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osgi.test.cases.jndi.provider.CTInitialContextFactory");
		// Uninstall the bundle now so the provider implementations are
		// unregistered
		uninstallBundle(testBundle);
		// Try to grab the ObjectFactory. We should get a NullPointerException
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference("java.lang.String", CTObjectFactory.class.getName());
			ctx.bind("reference", reference);
			String str = (String) ctx.lookup("reference");
			assertNotNull(str);
		} catch (NamingException ne) {
			return;
		} finally {
			// If we don't get the exception, then this test fails
			ctx.close();
			uninstallBundle(factoryBundle);
		}
	}

	public void testObjectFactoryBuilderRemoval() throws Exception {
		// Install the bundle that has the test provider implementations
		Bundle testBundle = installBundle("objectFactoryBuilder1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.osgi.test.cases.jndi.provider.CTInitialContextFactory");
		// Uninstall the bundle now so the provider implementations are
		// unregistered
		uninstallBundle(testBundle);
		// Try to grab the ObjectFactory. We should get a NullPointerException
		InitialContext ctx = new InitialContext(env);
		try {
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference("java.lang.String");
			ctx.bind("reference", reference);
			String str = (String) ctx.lookup("reference");
			assertNotNull(str);
		} catch (NamingException ne) {
			return;
		} finally {
			// If we don't get the exception, then this test fails
			ctx.close();
			uninstallBundle(factoryBundle);
		}
		failException("testObjectFactoryBuilderRemoval failed, ", NamingException.class);
	}

	public void testServiceRanking() throws Exception {
		// Install the necessary bundles
		Bundle testBundle = installBundle("objectFactory2.jar");
		// Use the default context to grab one of the factories and make sure
		// it's the right one
		InitialContext ctx = new InitialContext();
		try {
			assertNotNull("The context should not be null", ctx);
			CTObjectFactory of = (CTObjectFactory) ctx.lookup("osgi:services/org.osgi.test.cases.jndi.provider.CTObjectFactory");
			Hashtable ofEnv = of.getEnvironment();
			if (!ofEnv.containsKey("test1")) {
				fail("The right context was not returned");
			}
		} finally {
			ctx.close();
			uninstallBundle(testBundle);
		}
	}

}
