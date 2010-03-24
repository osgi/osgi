/*
 * Copyright (c) IBM Corporation (2010). All Rights Reserved.
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


package org.osgi.test.cases.jndi.secure.tests;

import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.directory.BasicAttributes;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIConstants;
import org.osgi.service.jndi.JNDIContextManager;
import org.osgi.service.jndi.JNDIProviderAdmin;
import org.osgi.test.cases.jndi.secure.factories.FactoryBundleActivator;
import org.osgi.test.cases.jndi.secure.provider.CTObjectFactory;
import org.osgi.test.cases.jndi.secure.provider.CTDirObjectFactory;
import org.osgi.test.cases.jndi.secure.provider.CTReference;
import org.osgi.test.cases.jndi.secure.provider.CTTestObject;
import org.osgi.test.cases.jndi.secure.provider.CTContext;
import org.osgi.test.cases.jndi.secure.provider.CTInitialContextFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/** 
 * @version $Revision$ $Date$
 */
public class TestJNDISecurity extends DefaultTestBundleControl {
	
	public void testLookupAccessRestrictions() throws Exception {
		// Grab the context manager
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		Context ctx = null;
		try {
			// Grab a default context for looking up services
			ctx = ctxManager.newInitialContext();
			assertNotNull("The context should not be null", ctx);
			// Try to grab a service that we shouldn't be allowed to grab.
			Object testObject = ctx.lookup("osgi:service/org.osgi.service.permissionadmin.PermissionAdmin");
		} catch (javax.naming.NameNotFoundException ex) {
			pass("javax.naming.NameNotFoundException caught in testLookupAccessRestrictions: SUCCESS");
			return;
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			ungetService(ctxManager);
		}

		failException("testLookupAccessRestrictions failed, ", javax.naming.NameNotFoundException.class);
	}
	
	public void testBundleContextAccessRestrictions() throws Exception {
		// Install the bundle necessary for this test
		Bundle contextBundle = installBundle("inaccessibleBundleContext.jar");
		// Grab the context manager
		Context ctx = null;
		Hashtable env = new Hashtable();
		BundleContext bundleCtx = (BundleContext) getService(Object.class);
		env.put(JNDIConstants.BUNDLE_CONTEXT, bundleCtx);
		try {
			// Grab a default context for looking up the bundle context
			ctx = new InitialContext(env);
			assertNotNull("The context should not be null", ctx);
			//Try to grab the bundle context.  We don't have permissions for it so this should fail.
			Object testObject = ctx.lookup("osgi:framework/bundleContext");
		} catch (javax.naming.NameNotFoundException ex) {
			pass("javax.naming.NameNotFoundException caught in testBundleContextAccessRestrictions: SUCCESS");
			return;
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			ungetService(bundleCtx);
			uninstallBundle(contextBundle);
		}
		
		failException("testBundleContextAccessRestrictions failed, ", javax.naming.NameNotFoundException.class);
	}
	
	public void testSecureLookupWithSpecificInitialContextFactory() throws Exception {
		// Install the bundle necessary for this test
		Bundle factoryBundle = installBundle("factoryBundle.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		int invokeCountBefore = CTContext.getInvokeCount();
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab the context
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			// Verify that we actually received the context
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
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			ungetService(ctxManager);
		}
	}
	
	public void testSecureLookupWithInitialContextFactoryBuilder() throws Exception {
		// Install the bundle necessary for this test
		Bundle builderBundle = installBundle("builderBundle.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		int invokeCountBefore = CTContext.getInvokeCount();
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab the context
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext();
			// Verify that we actually received the context
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
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(builderBundle);
			ungetService(ctxManager);
		}
	}
	
	public void testSecureGetObjectInstanceWithFactoryName() throws Exception {
		// Install the bundle necessary for this test
		Bundle factoryBundle = installBundle("factoryBundle.jar");
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			// Create a reference object we can use for testing.
			CTReference ref = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
			// Resolve the reference
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertNotNull(testObject);
		} finally {
			uninstallBundle(factoryBundle);
			ungetService(ctxAdmin);
		}
	}
	
	public void testSecureGetObjectInstanceWithFactoryNameAndAttributes() throws Exception {
		// Install the bundle necessary for this test
		Bundle factoryBundle = installBundle("factoryBundle.jar");
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			BasicAttributes attrs = new BasicAttributes();
			attrs.put("testAttribute", new Object());
			// Create a reference object we can use for testing
			CTReference ref = new CTReference(CTTestObject.class.getName(), CTDirObjectFactory.class.getName());
			// Resolve the reference
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(ref, null, null, null, attrs);
			assertEquals(testObject.getAttributes(), attrs);
		} finally {
			uninstallBundle(factoryBundle);
			ungetService(ctxAdmin);
		}
	}
	
	public void testSecureGetObjectInstanceWithBuilder() throws Exception {
		// Install the bundle necessary for this test
		Bundle builderBundle = installBundle("builderBundle.jar");
		// Grab the JNDIProviderAdmin service
		JNDIProviderAdmin ctxAdmin = (JNDIProviderAdmin) getService(JNDIProviderAdmin.class);
		try {
			// Create a reference object we can use for testing
			CTReference ref = new CTReference(CTTestObject.class.getName(), CTObjectFactory.class.getName());
			// Resolve the reference
			CTTestObject testObject = (CTTestObject) ctxAdmin.getObjectInstance(ref, null, null, null);
			assertNotNull(testObject);
		} finally {
			uninstallBundle(builderBundle);
			ungetService(ctxAdmin);
		}
	}
}
