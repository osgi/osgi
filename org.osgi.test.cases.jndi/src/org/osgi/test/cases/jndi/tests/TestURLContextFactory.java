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
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * 
 * A set of tests for URL operations in jndi
 * 
 * @version $Revision$ $Date$
 */
public class TestURLContextFactory extends DefaultTestBundleControl {
	
	public void testURLContextFactoryRegistration() throws Exception {
		// Install the bundles needed for this test
		Bundle testBundle = installBundle("urlContext1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Setup the standard context that we'll be using the URL to access so that we have something to lookup
		String testString = "test string";
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		InitialContext ctx = new InitialContext(env);
		assertNotNull("The context should not be null", ctx);
		ctx.bind("testString", testString);
		// Attempt to look up the test string
		Context urlCtx = new InitialContext();
		String result = (String)urlCtx.lookup("ct://testString");
		assertEquals(testString, result);
		// Cleanup after the test
		uninstallBundle(factoryBundle);
		uninstallBundle(testBundle);
	}
	
	public void testURLContextFactoryRemoval() throws Exception {
		// Install the bundles needed for this test
		Bundle testBundle = installBundle("urlContext1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Setup the standard context that we'll be using the URL to access so that we have something to lookup
		String testString = "test string";
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		Context ctx = new InitialContext(env);
		assertNotNull("The context should not be null", ctx);
		ctx.bind("testString", testString);
		// Remove the url context jar
		uninstallBundle(testBundle);
		InitialContext urlCtx = new InitialContext();
		try {
			String result = (String)urlCtx.lookup("ct://testString");
			assertEquals(testString, result);
		} catch (NamingException ex) {
			// We're expecting this, since they're should be no context able to handle this url
			uninstallBundle(factoryBundle);
			return;
		}
		
	    failException("testURLContextFactoryRemoval failed, ", javax.naming.NamingException.class);
	    
	}
}
