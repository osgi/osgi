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
import javax.naming.StringRefAddr;

import org.osgi.framework.Bundle;
import org.osgi.service.jndi.JNDIContextManager;
import org.osgi.service.jndi.JNDIProviderAdmin;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;
import org.osgi.test.cases.jndi.provider.CTObjectFactory;
import org.osgi.test.cases.jndi.provider.CTReference;
import org.osgi.test.cases.jndi.provider.CTTestObject;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * A set of methods to test the functionality of the JNDIProviderAdmin interface
 * 
 * @version $Revision$ $Date: 2009-12-14 16:08:27 -0500 (Mon, 14 Dec
 *          2009) $
 * 
 */

public class TestJNDIProviderAdmin extends DefaultTestBundleControl {

	public void testGetObjectInstanceWithReferenceable() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle objectFactoryBundle = installBundle("objectFactory1.jar");
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			// Create a referenceable object for testing
			CTTestObject ref = new CTTestObject("pass");
			// Resolve the reference
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertEquals(ref.getValue(), testObject.getValue());
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
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			// Do a getObjectInstance call with only the object class as an option
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(CTTestObject.class.getName(), null, null, null);
			assertNotNull(testObject);
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
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			// Create a reference object we can use for testing.
			CTReference ref = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
			// Resolve the reference
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertNotNull(testObject);
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
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Setup context so we can grab the reference from it
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			CTTestObject bindObject = new CTTestObject("pass");
			ctx.bind("testObject", bindObject);
			StringRefAddr addr = new StringRefAddr("URL", "ct://testObject"); 
			// Create a reference object we can use for testing
			CTReference ref = new CTReference(CTTestObject.class.getName(), addr);
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(ref, null, ctx, null);
			assertEquals(bindObject.getValue(), testObject.getValue());
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
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			// Create a reference object we can use for testing
			CTReference ref = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
			// Resolve the reference
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertNotNull(testObject);
		} finally {
			uninstallBundle(objectFactoryBuilderBundle);
			uninstallBundle(contextFactoryBundle);
			ungetService(ctxAdmin);
		}
	}
	
	public void testGetObjectInstanceWithMissingFactory() throws Exception {
		// Install the required bundles
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			// Create a reference object we can use for testing.
			CTReference ref = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
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
