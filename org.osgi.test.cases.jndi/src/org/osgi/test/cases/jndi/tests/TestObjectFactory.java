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
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.jndi.provider.CTDirObjectFactory;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;
import org.osgi.test.cases.jndi.provider.CTInitialDirContextFactory;
import org.osgi.test.cases.jndi.provider.CTObjectFactory;
import org.osgi.test.cases.jndi.provider.CTReference;
import org.osgi.test.cases.jndi.provider.CTTestObject;
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

	
	public void testReferenceableLookup() throws Exception {
		// Install the bundles required for this test
		Bundle contextFactoryBundle = installBundle("initialContextFactory1.jar");
		Bundle objectFactoryBundle = installBundle("objectFactory1.jar");
		// Create an appropriate context, create a referenceable object, then try to resolve the referenceable object
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		Context ctx = null;
		try { 
			ctx = new InitialContext(env);
			assertNotNull("The context should not be null", ctx);
			CTTestObject ref = new CTTestObject("pass");
			ctx.bind("referenceable", ref);
			CTTestObject obj = (CTTestObject) ctx.lookup("referenceable");
			assertEquals(ref.getValue(), obj.getValue());
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(contextFactoryBundle);
			uninstallBundle(objectFactoryBundle);
		}
	}
	
	public void testReferenceableLookupWithAttributes() throws Exception {
		// Install the bundles required for this test
		Bundle contextFactoryBundle = installBundle("initialDirContextFactory1.jar");
		Bundle objectFactoryBundle = installBundle("dirObjectFactory1.jar");
		// Create an appropriate context, create a referenceable object, then try to resolve the referenceable object
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialDirContextFactory.class.getName());
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			assertNotNull("The context should not be null", ctx);
			CTTestObject ref = new CTTestObject("pass", CTDirObjectFactory.class.getName());
			BasicAttributes attrs = new BasicAttributes("testAttributes", new Object());
			ctx.bind("referenceable", ref, attrs);
			CTTestObject obj = (CTTestObject) ctx.lookup("referenceable");
			Attributes testAttributes = ctx.getAttributes("referenceable");
			assertEquals(obj.getValue(), ref.getValue());
			assertEquals(testAttributes, attrs);
		} finally {
			if (ctx != null) {
				ctx.close();
			} 
			uninstallBundle(objectFactoryBundle);
			uninstallBundle(contextFactoryBundle);
		}
	}
	
	public void testReferenceLookup() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("objectFactory1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt to
		// correctly lookup the reference.
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		Context ctx = null;
		try {
			ctx = new InitialContext(env);
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
			ctx.bind("reference", reference);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			assertNotNull(obj);
		} finally {
			// Cleanup after the test completes
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}
	
	public void testReferenceLookupWithAttributes() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("dirObjectFactory1.jar");
		Bundle factoryBundle = installBundle("initialDirContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt to
		// correctly lookup the reference
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialDirContextFactory.class.getName());
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName(), CTDirObjectFactory.class.getName());
			BasicAttributes attrs = new BasicAttributes("testAttributes", new Object());
			ctx.bind("reference", reference, attrs);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			Attributes testAttributes = ctx.getAttributes("reference");
			assertNotNull(obj);
			assertEquals(testAttributes, attrs);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}
	
	public void testReferenceLookupWithNoFactory() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("objectFactory1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt
		// to correctly lookup the reference.
		Context ctx = null;
		try {
			ctx = new InitialContext();
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName());
			ctx.bind("reference", reference);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			assertNotNull(obj);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}
	
	public void testReferenceLookupWithNoFactoryAndAttributes() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("dirObjectFactory1.jar");
		Bundle factoryBundle = installBundle("initialDirContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt
		// to correctly lookup the reference
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext();
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName());
			BasicAttributes attrs = new BasicAttributes("testAttributes", new Object());
			ctx.bind("reference", reference, attrs);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			Attributes testAttributes = ctx.getAttributes("reference");
			assertNotNull(obj);
			assertEquals(testAttributes, attrs);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
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
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		Context ctx = null;
		try {
			ctx = new InitialContext(env);
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName());
			ctx.bind("reference", reference);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			assertNotNull(obj);
		} finally {
			// Cleanup after the test completes
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}
	
	public void testReferenceLookupWithBuilderAndAttributes() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("dirObjectFactoryBuilder1.jar");
		Bundle factoryBundle = installBundle("initialDirContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt to
		// correctly lookup the reference.
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialDirContextFactory.class.getName());
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName());
			BasicAttributes attrs = new BasicAttributes("testAttributes", new Object());
			ctx.bind("reference", reference, attrs);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			Attributes testAttributes = ctx.getAttributes("reference");
			assertNotNull(obj);
			assertEquals(testAttributes, attrs);
		} finally {
			// Cleanup after the test completes
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}
	
	public void testReferenceLookupWithBuilderAndNoFactory() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("objectFactoryBuilder1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt to
		// correctly lookup the reference.
		Context ctx = null;
		try {
			ctx = new InitialContext();
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName());
			ctx.bind("reference", reference);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			assertNotNull(obj);
		} finally {
			// Cleanup after the test completes
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}
	
	public void testReferenceLookupWithBuilderAndNoFactoryAndAttributes() throws Exception {
		// Install the bundles required for this test
		Bundle testBundle = installBundle("dirObjectFactoryBuilder1.jar");
		Bundle factoryBundle = installBundle("initialDirContextFactory1.jar");
		// Create an appropriate context, store a reference, then attempt to
		// correctly lookup the reference.
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext();
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName());
			BasicAttributes attrs = new BasicAttributes("testAttributes", new Object());
			ctx.bind("reference", reference, attrs);
			CTTestObject obj = (CTTestObject) ctx.lookup("reference");
			Attributes testAttributes = ctx.getAttributes("reference");
			assertNotNull(obj);
			assertEquals(testAttributes, attrs);
		} finally {
			// Cleanup after the test completes
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(testBundle);
		}
	}
	
	public void testObjectFactoryRemoval() throws Exception {
		// Install the bundle that has the test provider implementations
		Bundle testBundle = installBundle("objectFactory1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Uninstall the bundle now so the provider implementations are
		// unregistered
		uninstallBundle(testBundle);
		// Try to grab the ObjectFactory. We should get a NullPointerException
		Context ctx = null;
		try {
			ctx = new InitialContext(env);
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
			ctx.bind("reference", reference);
			// If no object factory is available for use, we should just return the reference.
			CTReference ref = (CTReference)ctx.lookup("reference");
			assertNotNull(ref);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
		}
	}

	public void testObjectFactoryBuilderRemoval() throws Exception {
		// Install the bundle that has the test provider implementations
		Bundle testBundle = installBundle("objectFactoryBuilder1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Uninstall the bundle now so the provider implementations are
		// unregistered
		uninstallBundle(testBundle);
		// Try to grab the ObjectFactory. We should get a NullPointerException
		Context ctx = null;
		try {
			ctx = new InitialContext(env);
			assertNotNull("The context should not be null", ctx);
			CTReference reference = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
			ctx.bind("reference", reference);
			// If no object factory is available for use, we should just return the reference.
			CTReference ref = (CTReference)ctx.lookup("reference");
			assertNotNull(ref);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
		}
	}

	public void testServiceRanking() throws Exception {
		// Install the necessary bundles
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		Bundle testBundle = installBundle("objectFactory2.jar");
		// Use the default context to grab one of the factories and make sure
		// it's the right one
		Context ctx = null;
		try {
			ctx = new InitialContext();
			assertNotNull("The context should not be null", ctx);
			CTObjectFactory of = (CTObjectFactory) ctx.lookup("osgi:service/org.osgi.test.cases.jndi.provider.CTObjectFactory");
			Hashtable ofEnv = of.getEnvironment();
			if (!ofEnv.containsKey("test1")) {
				fail("The right context was not returned");
			}
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(testBundle);
			uninstallBundle(factoryBundle);
		}
	}

}
