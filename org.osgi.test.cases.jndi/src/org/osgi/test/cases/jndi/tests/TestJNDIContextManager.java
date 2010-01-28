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
import javax.naming.OperationNotSupportedException;

import org.osgi.framework.Bundle;
import org.osgi.service.jndi.JNDIContextManager;
import org.osgi.test.cases.jndi.provider.CTContext;
import org.osgi.test.cases.jndi.provider.CTInitialContextFactory;
import org.osgi.test.cases.jndi.service.ExampleService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/** 
 * 
 * A set of methods to test the functionality of the JNDIContextManager interface
 * 
 * @version $Revision$ $Date$
 * 
 */

public class TestJNDIContextManager extends DefaultTestBundleControl {
	 
	public void testLookupOfJREProvidedContexts() throws Exception {
		// No provider bundle needed.  The JRE is the provider
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Create environment to grab built in LDAP provider
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
		// Grab the context
		Context ctx = null;		
		try {
			ctx = ctxManager.newInitialContext(env);
			// Verify that we actually received the context
			assertNotNull("The context should not be null", ctx);
			Hashtable ctxEnv = ctx.getEnvironment();
			assertEquals((String)env.get(Context.INITIAL_CONTEXT_FACTORY), (String)ctxEnv.get(Context.INITIAL_CONTEXT_FACTORY));
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			ungetService(ctxManager);
		}
	}
	public void testLookupWithSpecificInitialContextFactory() throws Exception {
		// install provider bundle
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		int invokeCountBefore = CTContext.getInvokeCount();
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab the context
		Context ctx = ctxManager.newInitialContext(env);
		try {
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
	
	public void testDefaultLookupWithInitialContextFactory() throws Exception {
		// install provider bundle
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		int invokeCountBefore = CTContext.getInvokeCount();
		// Grab the context
		Context ctx = ctxManager.newInitialContext();
		try {
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
	
	public void testLookupWithInitialContextFactoryBuilder() throws Exception {
		// install provider bundle
		Bundle factoryBuilderBundle = installBundle("initialContextFactoryBuilder1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		int invokeCountBefore = CTContext.getInvokeCount();
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab the context
		Context ctx = ctxManager.newInitialContext(env);
		try {
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
			uninstallBundle(factoryBuilderBundle);
			ungetService(ctxManager);
		}
	}
	
	public void testDefaultLookupWithInitialContextFactoryBuilder() throws Exception {
		// install provider bundle
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		Bundle factoryBuilderBundle = installBundle("initialContextFactoryBuilder1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		int invokeCountBefore = CTContext.getInvokeCount();
		// Grab the context
		Context ctx = ctxManager.newInitialContext();
		try {
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
			uninstallBundle(factoryBuilderBundle);
			uninstallBundle(factoryBundle);
			ungetService(ctxManager);
		}
	}
	
	public void testLookupWithJndiPropertiesFile() throws Exception {
		// install the required bundles
		Bundle factoryBundle = installBundle("initialContextFactoryWithProperties.jar");
		// Grab the jNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService (JNDIContextManager.class);
		int invokeCountBefore = CTContext.getInvokeCount();
		// Grab the contxext
		Context ctx = ctxManager.newInitialContext();
		try {
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
	
	public void testLookupWithNoMatchingContext() throws Exception {
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Setup the environment for grabbing the specific initialContextFactory
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Try to grab a context from the specified initialContextFactory. This
		// should throw an exception.
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			assertNotNull("The context should not be null", ctx);
			ctx.lookup("testObject");
		} catch (javax.naming.NoInitialContextException ex) {
			pass("javax.naming.NoInitialContextException caught in testLookupWithNoMatchingContext: SUCCESS");
			return;
		} finally {
			// If we don't get the exception, then this test fails
			if (ctx != null) {
				ctx.close();
			}
			ungetService(ctxManager);
		}
		failException("testLookupWithNoMatchingContext failed, ", javax.naming.NoInitialContextException.class);
	}
	
	public void testDefaultLookupWithNoMatchingContext() throws Exception {
		// Install a bundle that registers a service we know we can grab
		Bundle serviceBundle = installBundle("service1.jar");
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Try to grab a context.  There's none installed so we should get back one capable
		// of only performing URL lookups.
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext();
			assertNotNull("The context should not be null", ctx);
			ExampleService service = (ExampleService) ctx.lookup("osgi:service/org.osgi.test.cases.jndi.service.ExampleService");
			assertNotNull(service);
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
			uninstallBundle(serviceBundle);
			ungetService(ctxManager);
		}
	}
	
	public void testProviderUnregistration() throws Exception {
		// Install a bundle for grabbing a context
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Setup the environment
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab the context
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
			// Remove the bundle containing the provider.  The backing for the context should be removed as well.
			uninstallBundle(factoryBundle);
			Object obj = ctx.lookup("testObject");
		} catch (javax.naming.NoInitialContextException ex) {
			// This is what we're expecting to receive.
			pass("javax.naming.NoInitialContextException caught in testProviderUnregistration: SUCCESS");
			return;
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			if (factoryBundle.getState() == Bundle.INSTALLED) {
				uninstallBundle(factoryBundle);
			}
			ungetService(ctxManager);
		}
		
		failException("testProviderUnregistration failed", javax.naming.NoInitialContextException.class);
	}
	
	public void testProviderUnregistrationWithBuilder() throws Exception {
		// Install a bundle for grabbing a context
		Bundle factoryBundle = installBundle("initialContextFactoryBuilder1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Setup the environment
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab the context
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
			// Remove the bundle containing the provider.  The backing for the context should be removed as well.
			uninstallBundle(factoryBundle);
			Object obj = ctx.lookup("testObject");
		} catch (javax.naming.NoInitialContextException ex) {
			// This is what we're expecting to receive.
			pass("javax.naming.NoInitialContextException caught in testProviderRegistrationWithBuilder: SUCCESS");
			return;	
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			if (factoryBundle.getState() == Bundle.INSTALLED) {
				uninstallBundle(factoryBundle);
			}
			ungetService(ctxManager);
		}
		
		failException("testProviderUnregistrationWithBuilder failed", javax.naming.NoInitialContextException.class);
	}
	
	public void testContextRebinding() throws Exception {
		// Install a bundle for grabbing a context
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Setup the environment
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab a context
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			// Verify that we actually received the context
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
			// Remove the bundle containing the provider.  The backing for the context should be removed as well.
			uninstallBundle(factoryBundle);
			Object obj = ctx.lookup("testObject");
		} catch (javax.naming.NoInitialContextException ex) {
			// do nothing here
		}
		
		// Install a bundle that can be used to rebind the context
		Bundle builderBundle = installBundle("initialContextFactoryBuilder1.jar");
		// See if the rebinding occurs as it should
		try {
			Object obj = ctx.lookup("testObject");
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(builderBundle);
			ungetService(ctxManager);
		}
	}
	
	public void testUngetContextManager() throws Exception {
		// Install a bundle for grabbing a context
		Bundle factoryBundle = installBundle("initialContextFactory1.jar");
		// Grab the JNDIContextManager service
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		// Setup the environment
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CTInitialContextFactory.class.getName());
		// Grab a context
		Context ctx = null;
		try {
			ctx = ctxManager.newInitialContext(env);
			// Verify that we actually received the context
			assertNotNull("The context should not be null", ctx);
			ctx.bind("testObject", new Object());
			ungetService(ctxManager);
			Object obj = ctx.lookup("testObject");
		} catch (OperationNotSupportedException ex) {
			pass("javax.naming.OperationNotSupportedException caught in testUngetContextManager: SUCCESS");
			return;
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			uninstallBundle(factoryBundle);
		}
		
		failException("testUngetContextManager failed", javax.naming.OperationNotSupportedException.class);
	}
}
