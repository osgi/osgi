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
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

import org.osgi.framework.Bundle;
import org.osgi.service.jndi.JNDIContextAdmin;
import org.osgi.service.jndi.JNDIContextManager;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;
import org.osgi.test.cases.jndi.provider.CTObjectFactory;
import org.osgi.test.cases.jndi.provider.CTReference;
import org.osgi.test.cases.jndi.provider.CTReferenceable;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/** 
 * 
 * A set of methods to test the functionality of the JNDIContextAdmin interface
 * 
 * @version $Revision$ $Date$
 * 
 */

public class TestJNDIContextAdmin extends DefaultTestBundleControl {

	public void testGetObjectInstanceWithReferenceable() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle objectFactoryBundle = installBundle("objectFactory1.jar");
		// Grab the JNDIContextAdmin service
		JNDIContextAdmin ctxAdmin = (JNDIContextAdmin) getService(JNDIContextAdmin.class);
		try {
			// Create a referenceable object for testing
			CTReferenceable ref = new CTReferenceable();
			// Resolve the reference
			String testString = (String) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertNotNull(testString);
		} finally {
			uninstallBundle(objectFactoryBundle);
			uninstallBundle(contextFactoryBundle);
			ungetService(ctxAdmin);
		}
	}
	
	public void testGetObjectInstanceWithNoReference() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle objectFactoryBuilderBundle = installBundle("objectFactoryBuilder1.jar");
		// Grab the JNDIContextAdmin service
		JNDIContextAdmin ctxAdmin = (JNDIContextAdmin) getService(JNDIContextAdmin.class);
		try {
			// Do a getObjectInstance call with only the object class as an option
			String testString = (String) ctxAdmin.getObjectInstance(String.class.getName(), null, null, null);
			assertNotNull(testString);
		} finally {
			uninstallBundle(objectFactoryBuilderBundle);
			uninstallBundle(contextFactoryBundle);
			ungetService(ctxAdmin);
		}
	}
	
	public void testGetObjectInstanceWithFactoryName() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle objectFactoryBundle = installBundle("objectFactory1.jar");
		// Grab the JNDIContextAdmin service
		JNDIContextAdmin ctxAdmin = (JNDIContextAdmin) getService(JNDIContextAdmin.class);
		try {
			// Create a reference object we can use for testing.
			CTReference ref = new CTReference(String.class.getName(), CTObjectFactory.class.getName());
			// Resolve the reference
			String testString = (String) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertNotNull(testString);
		} finally {
			uninstallBundle(objectFactoryBundle);
			uninstallBundle(contextFactoryBundle);
			ungetService(ctxAdmin);
		}
	}
	
	public void testGetObjectInstanceWithNoFactoryName() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle urlContextBundle = installBundle("urlContext1.jar");
		Bundle objectFactoryBundle = installBundle("objectFactory1.jar");
		// Grab the JNDIContextAdmin service
		JNDIContextAdmin ctxAdmin = (JNDIContextAdmin) getService(JNDIContextAdmin.class);
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Setup context so we can grab the reference from it
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			String bindString = "pass";
			ctx.bind("testString", bindString);
			StringRefAddr addr = new StringRefAddr("URL", "ct://testString"); 
			// Create a reference object we can use for testing
			CTReference ref = new CTReference(String.class.getName(), addr);
			String testString = (String) ctxAdmin.getObjectInstance(ref, null, ctx, null);
			assertEquals(bindString, testString);
		} finally {
			uninstallBundle(contextFactoryBundle);
			uninstallBundle(urlContextBundle);
			uninstallBundle(objectFactoryBundle);
			ungetService(ctxAdmin);
			ungetService(ctxManager);
		}
	}
	
	public void testGetObjectInstanceWithBuilder() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle objectFactoryBuilderBundle = installBundle("objectFactoryBuilder1.jar");
		// Grab the JNDIContextAdmin service
		JNDIContextAdmin ctxAdmin = (JNDIContextAdmin) getService(JNDIContextAdmin.class);
		try {
			// Create a reference object we can use for testing
			CTReference ref = new CTReference(String.class.getName(), CTObjectFactory.class.getName());
			// Resolve the reference
			String testString = (String) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertNotNull(testString);
		} finally {
			uninstallBundle(objectFactoryBuilderBundle);
			uninstallBundle(contextFactoryBundle);
			ungetService(ctxAdmin);
		}
	}
	
	public void testGetObjectInstanceWithMissingFactory() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIContextAdmin service
		JNDIContextAdmin ctxAdmin = (JNDIContextAdmin) getService(JNDIContextAdmin.class);
		try {
			// Create a reference object we can use for testing.
			CTReference ref = new CTReference(String.class.getName(), CTObjectFactory.class.getName());
			// Resolve the reference, we should get back the reference we provided since the necessary
			// objectFactory isn't available
			CTReference returnedRef = (CTReference) ctxAdmin.getObjectInstance(ref, null, null, null);
			if (!(returnedRef.equals(ref))) {
				fail("The provided reference was not correctly returened");
			}
		} finally {
			uninstallBundle(contextFactoryBundle);
			ungetService(ctxAdmin);
		}
	}
}
