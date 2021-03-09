/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.framework.junit.hooks.weaving;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.hooks.weaving.WeavingException;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.hooks.weaving.WovenClassListener;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

public class WeavingHookTests extends DefaultTestBundleControl implements WeavingHook, WovenClassListener {

	/* Test Constants */

	/** The name of the package containing the test classes for weaving */
	private static final String TESTCLASSES_PACKAGE = "org.osgi.test.cases.framework.weaving.tb1";
	/** The name of the basic test class for weaving */
	private static final String TEST_CLASS_NAME = TESTCLASSES_PACKAGE + ".TestClass";
	/** The name of the basic test class for weaving with a dynamic import */
	private static final String DYNAMIC_IMPORT_TEST_CLASS_NAME = TESTCLASSES_PACKAGE + ".TestDynamicImport";
	/** The symbolic name of the bundle containing the test classes for weaving */
	private static final String TESTCLASSES_SYM_NAME = "org.osgi.test.cases.framework.weaving.testclasses";
	/** The resource name of the bundle containing the test classes for weaving */
	private static final String TESTCLASSES_JAR = "weaving.testClasses.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	private static final String TEST_IMPORT_100_JAR = "weaving.testImport_1.0.0.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	private static final String TEST_IMPORT_110_JAR = "weaving.testImport_1.1.0.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	private static final String TEST_IMPORT_160_JAR = "weaving.testImport_1.6.0.jar";
	/** The resource name of one of the bundles containing SymbolicNameVersion class */
	private static final String TEST_ALT_IMPORT_JAR = "weaving.testAlternativeImport.jar";
	/** The symbolic name of one of the bundles containing SymbolicNameVersion class */
	private static final String TEST_IMPORT_SYM_NAME = "org.osgi.test.cases.framework.weaving.test.import";
	/** The symbolic name of one of the bundles containing SymbolicNameVersion class */
	private static final String TEST_ALT_IMPORT_SYM_NAME = "org.osgi.test.cases.framework.weaving.test.alternative.import";
	/** The package name for the SymbolicNameVersion class */
	private static final String IMPORT_TEST_CLASS_PKG = "org.osgi.test.cases.framework.weaving.tb2";
	/** The class name name for the SymbolicNameVersion class */
	private static final String IMPORT_TEST_CLASS_NAME = IMPORT_TEST_CLASS_PKG + ".SymbolicNameVersion";
	/** An import string used in these tests */
	private static final String ORG_OSGI_FRAMEWORK_VERSION_1_6 = "org.osgi.framework;version=1.6";

	/**
	 * This class is used to weave the test classes in many of the
	 * tests
	 */
	public class ConfigurableWeavingHook implements WeavingHook {

		/**
		 * The expected value of the constant in the UTF8 pool
		 * that needs to be changed.
		 */
		private String expected = "DEFAULT";

		/** The value to change the UTF8 constant to */
		private String changeTo = "WOVEN";

		/** A list of dynamic imports to add */
		private List<String> dynamicImports = new ArrayList<String>();

		/**
		 * Set to true when this hook is called for one of the
		 * test weaving classes
		 */
		private boolean called;

		/** An exception to throw instead of weaving the class */
		private RuntimeException toThrow;

		/**
		 * Set the expected value of the Constant in the class to be woven
		 * @param expected
		 */
		public void setExpected(String expected) {
			this.expected = expected;
		}

		/**
		 * Set the value of the Constant to weave into the class
		 * @param expected
		 */
		public void setChangeTo(String changeTo) {
			this.changeTo = changeTo;
		}

		/**
		 * Add a dynamic import
		 * @param importString
		 */
		public void addImport(String importString) {
			dynamicImports.add(importString);
		}

		/**
		 * Has this weaving hook been called for a test class
		 * @return
		 */
		public boolean isCalled() {
			return called;
		}

		/** Reset {@link #isCalled()} */
		public void clearCalls() {
			called = false;
		}

		/**
		 * Set an exception to throw instead of weaving the class
		 * @param re
		 */
		public void setExceptionToThrow(RuntimeException re) {
			toThrow = re;
		}


		public void weave(WovenClass wovenClass) {

			// We are only interested in classes that are in the test
			if(wovenClass.getClassName().startsWith(TESTCLASSES_PACKAGE)) {

				called = true;
				//If there is an exception, throw it and prevent it being thrown again
				if(toThrow != null) {
					try {
						throw toThrow;
					} finally {
						toThrow = null;
					}
				}
				// Load the class and change the UTF8 constant
				ClassParser parser = new ClassParser(new ByteArrayInputStream(
						wovenClass.getBytes()), null);

				try {
					//Create a new class based on the old one
					ClassGen generator = new ClassGen(parser.parse());

					//Create a new constant
					ConstantPoolGen factory = new ConstantPoolGen();
					Constant c = factory.getConstant(factory.addUtf8(changeTo));

					//Find the old constant
					int location = generator.getConstantPool().lookupUtf8(expected);

					if(location < 0)
						throw new RuntimeException("Unable to locate the expected " + expected +
								" in the constant pool " + generator.getConstantPool());

					//Replace the constant
					generator.getConstantPool().setConstant(location, c);

					//Get the new class as a byte[]
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					generator.getJavaClass().dump(baos);

					//Add any imports and set the new class bytes
					for(int i = 0; i < dynamicImports.size(); i++) {
						wovenClass.getDynamicImports().add(dynamicImports.get(i));
					}
					wovenClass.setBytes(baos.toByteArray());

				} catch (Exception e) {
					//Throw on any IllegalArgumentException as this comes from
					//The dynamic import. Anything else is an error and should be
					//wrapped and thrown.
					if(e instanceof IllegalArgumentException)
						throw (IllegalArgumentException)e;
					else
						throw new RuntimeException(e);
				}
			}
		}

		/**
		 * Register this hook using the supplied context
		 * @param ctx
		 * @return
		 */
		public ServiceRegistration<WeavingHook> register(BundleContext ctx) {
			return register(ctx, 0);
		}

		/**
		 * Register this hook using the supplied context and ranking
		 * @param ctx
		 * @param rank
		 * @return
		 */
		public ServiceRegistration<WeavingHook> register(BundleContext ctx, int rank) {
			Hashtable<String, Object> table = new Hashtable<String, Object>();
			table.put(Constants.SERVICE_RANKING, Integer.valueOf(rank));
			return ctx.registerService(WeavingHook.class, this, table);
		}
	}

	/**
	 * This class is used as to listen for Framework errors
	 * issued when there is a Weaving failure
	 */
	public class ClassLoadErrorListener implements FrameworkListener {
		/** The expected cause of the error */
		private final Exception cause;
		/** The expected bundle that caused the error */
		private final Bundle source;
		/** Whether the correct error has been seen */
		private boolean validated = false;
		/** The list of events we have seen */
		private List<FrameworkEvent> events = new ArrayList<FrameworkEvent>();

		/**
		 * Create a Listener expecting an error with the supplied cause and source
		 * @param cause The exception that caused the error
		 * @param source The bundle that was the source of the error
		 */
		public ClassLoadErrorListener(RuntimeException cause, Bundle source) {
			this.cause = cause;
			this.source = source;
		}

		/**
		 * Receieve and validate the framework event
		 */
		public synchronized void frameworkEvent(FrameworkEvent event) {
			if(!validated) {
			   validated = (event.getType() == FrameworkEvent.ERROR &&
						event.getThrowable() == cause &&
						event.getBundle() == source);
			}
			events.add(event);
		}

		/**
		 * True if one, and only one, valid event was received
		 * @return
		 */
		public boolean wasValidEventSent() {
			return validated;
		}

		public String toString() {
			return "Called with events " + events;
		}
	}



	/** The test classes */
	private Bundle weavingClasses;

	protected void setUp() throws Exception {
		super.setUp();
		weavingClasses = installBundle(TESTCLASSES_JAR);
		listenerWovenClass = null;
		wovenClassStates.clear();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		uninstallBundle(weavingClasses);
	}

	/**
	 * Perform a basic weave, and show the loaded class is changed
	 * @throws Exception
	 */
	public void testBasicWeaving() throws Exception {
		// Install the bundles necessary for this test
		ServiceRegistration<WeavingHook> reg = null;
		try {
			reg = new ConfigurableWeavingHook().register(getContext(), 0);
			Class<?> clazz = weavingClasses.loadClass(TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "WOVEN",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
		}
	}

	/**
	 * Perform a basic weave that adds an import, and show the loaded class fails if
	 * the hook does not add the import
	 * @throws Exception
	 */
	public void testBasicWeavingNoDynamicImport() throws Exception {
		// Install the bundles necessary for this test
		ServiceRegistration<WeavingHook> reg = null;
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.setChangeTo("org.osgi.framework.Bundle");
		try {
			reg = hook.register(getContext(), 0);
			Class<?>clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			clazz.getConstructor().newInstance().toString();
			fail("Should fail to load the Bundle class");
		} catch (RuntimeException cnfe) {
			assertTrue("Wrong exception: " + cnfe.getCause().getClass(),
				(cnfe.getCause() instanceof ClassNotFoundException));
		} finally {
			if (reg != null)
				reg.unregister();
		}
	}

	/**
	 * Perform a basic weave that adds an import, and show the loaded class works if
	 * the hook adds the import
	 * @throws Exception
	 */
	public void testBasicWeavingDynamicImport() throws Exception {
		// Install the bundles necessary for this test
		ServiceRegistration<WeavingHook> reg = null;
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.addImport("org.osgi.framework");
		hook.setChangeTo("org.osgi.framework.Bundle");
		try {
			reg = hook.register(getContext(), 0);
			Class<?>clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.Bundle",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
		}
	}

	/**
	 * Test that multiple weavers get called in service id order
	 * @throws Exception
	 */
	public void testMultipleWeavers() throws Exception {

		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();

		hook1.setChangeTo("1 Finished");
		hook2.setExpected("1 Finished");
		hook2.setChangeTo("2 Finished");
		hook3.setExpected("2 Finished");
		hook3.setChangeTo("Chain Complete");

		ServiceRegistration<WeavingHook> reg1 = null;
		ServiceRegistration<WeavingHook> reg2= null;
		ServiceRegistration<WeavingHook> reg3 = null;
		try {
			reg1 = hook1.register(getContext(), 0);
			reg2 = hook2.register(getContext(), 0);
			reg3 = hook3.register(getContext(), 0);
			Class<?>clazz = weavingClasses.loadClass(TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "Chain Complete",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	/**
	 * Test that multiple weavers get called in ranking and service id order
	 * @throws Exception
	 */
	public void testMultipleWeaversWithRankings() throws Exception {

		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();

		//Called in proper order
		hook3.setChangeTo("3 Finished");
		hook1.setExpected("3 Finished");
		hook1.setChangeTo("1 Finished");
		hook2.setExpected("1 Finished");
		hook2.setChangeTo("Chain Complete");


		ServiceRegistration<WeavingHook> reg1 = null;
		ServiceRegistration<WeavingHook> reg2= null;
		ServiceRegistration<WeavingHook> reg3 = null;
		try {
			reg1 = hook1.register(getContext(), 0);
			reg2 = hook2.register(getContext(), 0);
			reg3 = hook3.register(getContext(), 1);
			Class<?>clazz = weavingClasses.loadClass(TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "Chain Complete",
					clazz.getConstructor().newInstance().toString());

			// We expect the order to change if we update our ranking
			Hashtable<String, Object> table = new Hashtable<String, Object>();
			table.put(Constants.SERVICE_RANKING, Integer.valueOf(2));
			reg2.setProperties(table);

			hook2.setExpected("DEFAULT");
			hook2.setChangeTo("2 Finished");
			hook3.setExpected("2 Finished");
			hook3.setChangeTo("3 Finished");
			hook1.setChangeTo("org.osgi.framework.hooks.weaving.WovenClass");

			hook2.addImport("org.osgi.framework.hooks.weaving");

			clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.hooks.weaving.WovenClass",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	/**
	 * Test that an exception stops the weaving chain mid-way
	 * @throws Exception
	 */
	public void testExceptionPreventsSubsequentCalls() throws Exception {

		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();

		RuntimeException cause = new RuntimeException();

		//If hook 2 throws an exception then 3 should not be called

		hook1.setChangeTo("1 Finished");
		hook2.setExceptionToThrow(cause);
		hook3.setExpected("2 Finished");
		hook3.setChangeTo("Chain Complete");

		ServiceRegistration<WeavingHook> reg1 = null;
		ServiceRegistration<WeavingHook> reg2= null;
		ServiceRegistration<WeavingHook> reg3 = null;
		try {
			reg1 = hook1.register(getContext(), 0);
			reg2 = hook2.register(getContext(), 0);
			reg3 = hook3.register(getContext(), 0);
			weavingClasses.loadClass(TEST_CLASS_NAME);
			fail("Class should fail to Load");
		} catch (ClassFormatError cfe) {
			assertSame("Should be caused by our Exception", cause, cfe.getCause());
			assertTrue("Hook 1 should be called", hook1.isCalled());
			assertTrue("Hook 2 should be called", hook2.isCalled());
			assertFalse("Hook 3 should not be called", hook3.isCalled());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	/**
	 * Test that an exception causes deny listing
	 * 
	 * @throws Exception
	 */
	public void testExceptionCausesDenyListing() throws Exception {

		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();

		RuntimeException cause = new RuntimeException();

		hook1.setChangeTo("1 Finished");
		hook2.setExceptionToThrow(cause);
		hook3.setExpected("1 Finished");
		hook3.setChangeTo("Chain Complete");

		ServiceRegistration<WeavingHook> reg1 = null;
		ServiceRegistration<WeavingHook> reg2= null;
		ServiceRegistration<WeavingHook> reg3 = null;
		ClassLoadErrorListener listener = new ClassLoadErrorListener(cause, getContext().getBundle());
		try {
			try {
				reg1 = hook1.register(getContext(), 0);
				reg2 = hook2.register(getContext(), 0);
				reg3 = hook3.register(getContext(), 0);

				getContext().addFrameworkListener(listener);

				weavingClasses.loadClass(TEST_CLASS_NAME);
				fail("Class should fail to Load");
			} catch (ClassFormatError cfe) {

				waitForListener(listener);

				getContext().removeFrameworkListener(listener);
				assertTrue("Wrong event was sent " + listener, listener.wasValidEventSent());

				assertSame("Should be caused by our Exception", cause, cfe.getCause());
				assertTrue("Hook 1 should be called", hook1.isCalled());
				assertTrue("Hook 2 should be called", hook2.isCalled());
				assertFalse("Hook 3 should not be called", hook3.isCalled());
			}

			hook1.clearCalls();
			hook2.clearCalls();
			hook3.clearCalls();

			hook1.addImport("org.osgi.framework.wiring;version=\"[1.0.0,2.0.0)\"");
			hook3.setChangeTo("org.osgi.framework.wiring.BundleWiring");


			Class<?>clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertTrue("Hook 1 should be called", hook1.isCalled());
			assertFalse("Hook 2 should not be called", hook2.isCalled());
			assertTrue("Hook 3 should be called", hook3.isCalled());
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.wiring.BundleWiring",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	private void waitForListener(ClassLoadErrorListener listener) {
		long t = System.currentTimeMillis();

		while(System.currentTimeMillis() - t < 10000) {
			if(listener.wasValidEventSent())
				break;
			else {
				try {
					Sleep.sleep(250);
				} catch (InterruptedException e) {
					break;
				}
			}
		}

	}

	/**
	 * Test that a WeavingException does not result in deny listing
	 * 
	 * @throws Exception
	 */
	public void testWeavingExceptionDoesNotCauseDenyListing() throws Exception {

		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();

		RuntimeException cause = new WeavingException("Test Exception");

		hook1.setChangeTo("1 Finished");
		hook2.setExceptionToThrow(cause);
		hook2.setExpected("1 Finished");
		hook2.setChangeTo("2 Finished");
		hook3.setExpected("2 Finished");
		hook3.setChangeTo("Chain Complete");

		ServiceRegistration<WeavingHook> reg1 = null;
		ServiceRegistration<WeavingHook> reg2= null;
		ServiceRegistration<WeavingHook> reg3 = null;
		ClassLoadErrorListener listener = new ClassLoadErrorListener(cause, getContext().getBundle());
		try {
			try {
				reg1 = hook1.register(getContext(), 0);
				reg2 = hook2.register(getContext(), 0);
				reg3 = hook3.register(getContext(), 0);

				getContext().addFrameworkListener(listener);

				weavingClasses.loadClass(TEST_CLASS_NAME);
				fail("Class should fail to Load");
			} catch (ClassFormatError cfe) {

				waitForListener(listener);

				getContext().removeFrameworkListener(listener);
				assertTrue("Wrong event was sent " + listener, listener.wasValidEventSent());

				assertSame("Should be caused by our Exception", cause, cfe.getCause());
				assertTrue("Hook 1 should be called", hook1.isCalled());
				assertTrue("Hook 2 should be called", hook2.isCalled());
				assertFalse("Hook 3 should not be called", hook3.isCalled());
			}

			hook1.clearCalls();
			hook2.clearCalls();
			hook3.clearCalls();

			hook2.addImport("org.osgi.framework.wiring;version=\"[1.0.0,2.0.0)\"");
			hook3.setChangeTo("org.osgi.framework.wiring.BundleWiring");

			Class<?>clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertTrue("Hook 1 should be called", hook1.isCalled());
			assertTrue("Hook 2 should not be called", hook2.isCalled());
			assertTrue("Hook 3 should be called", hook3.isCalled());
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.wiring.BundleWiring",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	/**
	 * Test that the registration, not the object, is deny listed
	 * 
	 * @throws Exception
	 */
	public void testDenyListingOnlyAppliesToRegistration() throws Exception {

		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();

		RuntimeException cause = new RuntimeException();

		hook1.setChangeTo("1 Finished");
		hook2.setExceptionToThrow(cause);
		hook3.setExpected("1 Finished");
		hook3.setChangeTo("Chain Complete");

		ServiceRegistration<WeavingHook> reg1 = null;
		ServiceRegistration<WeavingHook> reg2= null;
		ServiceRegistration<WeavingHook> reg3 = null;
		ClassLoadErrorListener listener = new ClassLoadErrorListener(cause, getContext().getBundle());
		try {
			try {
				reg1 = hook1.register(getContext(), 0);
				reg2 = hook2.register(getContext(), 0);
				reg3 = hook3.register(getContext(), 0);

				getContext().addFrameworkListener(listener);

				weavingClasses.loadClass(TEST_CLASS_NAME);
				fail("Class should fail to Load");
			} catch (ClassFormatError cfe) {

				waitForListener(listener);

				getContext().removeFrameworkListener(listener);
			    assertTrue("Wrong event was sent " + listener, listener.wasValidEventSent());

				assertSame("Should be caused by our Exception", cause, cfe.getCause());
				assertTrue("Hook 1 should be called", hook1.isCalled());
				assertTrue("Hook 2 should be called", hook2.isCalled());
				assertFalse("Hook 3 should not be called", hook3.isCalled());
			}

			hook1.clearCalls();
			hook2.clearCalls();
			hook3.clearCalls();

			hook1.addImport("org.osgi.framework.wiring;version=\"[1.0.0,2.0.0)\"");
			hook3.setChangeTo("3 Finished");
			hook2.setExpected("3 Finished");
			hook2.setChangeTo("org.osgi.framework.wiring.BundleRevision");

			reg2.unregister();
			reg2 = hook2.register(getContext());
			Class<?>clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertTrue("Hook 1 should be called", hook1.isCalled());
			assertTrue("Hook 2 should not be called", hook2.isCalled());
			assertTrue("Hook 3 should be called", hook3.isCalled());
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.wiring.BundleRevision",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	/**
	 * Test that adding attributes gets the correct resolution
	 * @param attributes Attributes to add to the import package (include a leading ';')
	 * @param result The expected toString from the woven class
	 * @throws Exception
	 */
	private void doTest(String attributes, String result) throws Exception {

		setupImportChoices();
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.addImport(IMPORT_TEST_CLASS_PKG + attributes);
		hook.setChangeTo(IMPORT_TEST_CLASS_NAME);
		ServiceRegistration<WeavingHook> reg = null;
		try {
			reg = hook.register(getContext(), 0);
			Class<?>clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", result,
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
			tearDownImportChoices();
		}
	}

	/**
	 * A basic test with a version range
	 * @throws Exception
	 */
	public void testDynamicImport() throws Exception {
		doTest(";version=\"[1,2)\"", TEST_ALT_IMPORT_SYM_NAME + "_1.0.0");
	}

	/**
	 * A test with a version range that prevents the "best" package
	 * @throws Exception
	 */
	public void testVersionConstrainedDynamicImport() throws Exception {
		doTest(";version=\"[1,1.5)\"" , TEST_IMPORT_SYM_NAME + "_1.1.0");
	}

	/**
	 * A test with a bundle-symbolic-name attribute
	 * @throws Exception
	 */
	public void testBSNConstrainedDynamicImport() throws Exception {
		doTest(";" + Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE + "="
				+ TEST_IMPORT_SYM_NAME, TEST_IMPORT_SYM_NAME + "_1.1.0");
	}

	/**
	 * A test with a bundle-symbolic-name attribute and a version constraint
	 * @throws Exception
	 */
	public void testBSNAndVersionConstrainedDynamicImport() throws Exception {
		doTest(";version=\"[1.0,1.1)\";" + Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE + "="
				+ TEST_IMPORT_SYM_NAME, TEST_IMPORT_SYM_NAME + "_1.0.0");
	}

	/**
	 * A test with an attribute constraint
	 * @throws Exception
	 */
	public void testAttributeConstrainedDynamicImport() throws Exception {
		doTest(";foo=bar", TEST_IMPORT_SYM_NAME + "_1.0.0");
	}

	/**
	 * A test with a mandatory attribute constraint
	 * @throws Exception
	 */
	public void testMandatoryAttributeConstrainedDynamicImport() throws Exception {
		doTest(";prop=val", TEST_IMPORT_SYM_NAME + "_1.6.0");
	}

	/**
	 * A test with multiple imports that would wire differently
	 * @throws Exception
	 */
	public void testMultipleConflictingDynamicImports() throws Exception {

		setupImportChoices();
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.addImport(IMPORT_TEST_CLASS_PKG + ";version=\"(1.0,1.5)\"");
		hook.addImport(IMPORT_TEST_CLASS_PKG + ";foo=bar");
		hook.setChangeTo(IMPORT_TEST_CLASS_NAME);
		ServiceRegistration<WeavingHook> reg = null;
		try {
			reg = hook.register(getContext(), 0);
			Class<?>clazz = weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful",
					TEST_IMPORT_SYM_NAME + "_1.1.0",
					clazz.getConstructor().newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
			tearDownImportChoices();
		}
	}

	/**
	 * A test with a bad input that causes a failure
	 * @throws Exception
	 */
	public void testBadDynamicImportString() throws Exception {
		setupImportChoices();
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		// Note the missing quote for the version attribute
		hook.addImport(IMPORT_TEST_CLASS_PKG + ";version=(1.0,1.5)\"");
		hook.setChangeTo(IMPORT_TEST_CLASS_NAME);
		ServiceRegistration<WeavingHook> reg = null;
		try {
			reg = hook.register(getContext(), 0);
			weavingClasses.loadClass(DYNAMIC_IMPORT_TEST_CLASS_NAME);
			fail("Should not get here!");
		} catch (ClassFormatError cfe) {
			if(!(cfe.getCause() instanceof IllegalArgumentException))
				fail("The class load should generate an IllegalArgumentException due " +
						"to the bad dynamic import string " + cfe.getMessage());
		} finally {
			if (reg != null)
				reg.unregister();
			tearDownImportChoices();
		}
	}

	//Various sources for dynamic imports
	private Bundle im100;
	private Bundle im110;
	private Bundle im160;
	private Bundle altIM;

	private void tearDownImportChoices() throws Exception {
		uninstallBundle(im100);
		uninstallBundle(im110);
		uninstallBundle(im160);
		uninstallBundle(altIM);
	}

	private void setupImportChoices() throws Exception {
		im100 = installBundle(TEST_IMPORT_100_JAR);
		im110 = installBundle(TEST_IMPORT_110_JAR);
		im160 = installBundle(TEST_IMPORT_160_JAR);
		altIM = installBundle(TEST_ALT_IMPORT_JAR);
	}


	/** The {@link WovenClass} from the test weave */
	private WovenClass wc = null;
	/** The first hook registration */
	private ServiceRegistration<WeavingHook> reg1;
	/** The second hook registration */
	private ServiceRegistration<WeavingHook> reg2;
	/** The real class bytes */
	private byte[] realBytes;
	/** Some fake class bytes */
	private byte[] fakeBytes = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};

	/**
	 * Test the basic contract of WovenClass, incluing immutability after a weave has finished
	 * @throws Exception
	 */
	public void testWovenClass() throws Exception {

		registerThisHook();

		try {
			Class<?>clazz = weavingClasses.loadClass(TEST_CLASS_NAME);

			assertWiring();

			assertTrue("Should be complete now", wc.isWeavingComplete());
			assertNotSame("Should get copies of the byte array now", realBytes, wc.getBytes());
			assertEquals("Wrong class", TEST_CLASS_NAME, wc.getClassName());
			assertSame("Should be set now", clazz, wc.getDefinedClass());
			assertSame("Should be set now", clazz.getProtectionDomain(), wc.getProtectionDomain());

			assertImmutableList();

			try {
				wc.setBytes(fakeBytes);
				fail("Should not be possible");
			} catch (IllegalStateException ise) {
				//No action needed
			}
		} finally {
			unregisterThisHook();
		}
	}

	/**
	 * Test the basic contract of WovenClass, including immutability after a weave has failed with bad
	 * bytes
	 * @throws Exception
	 */
	public void testBadWeaveClass() throws Exception {

		registerThisHook();

		try {
			reg2.unregister();

			try {
				weavingClasses.loadClass(TEST_CLASS_NAME);
				fail("Should have dud bytes here!");
			} catch (ClassFormatError cfe) {
				assertWiring();

				assertTrue("Should be complete now", wc.isWeavingComplete());
				assertNotSame("Should get copies of the byte array now", fakeBytes, wc.getBytes());
				assertTrue("Content should still be equal though", Arrays.equals(fakeBytes, wc.getBytes()));
				assertEquals("Wrong class", TEST_CLASS_NAME, wc.getClassName());
				assertNull("Should not be set", wc.getDefinedClass());

				assertImmutableList();

				try {
					wc.setBytes(fakeBytes);
					fail("Should not be possible");
				} catch (IllegalStateException ise) {
					//No action needed
				}
			}

			reg2 =  getContext().registerService(WeavingHook.class, this, null);
		} finally {
			unregisterThisHook();
		}
	}

	/**
	 * Test the basic contract of WovenClass, including immutability after a weave has failed with
	 * an exception
	 * @throws Exception
	 */
	public void testHookException() throws Exception {

		registerThisHook();

		try {
			ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
			hook.setExceptionToThrow(new RuntimeException());
			ServiceRegistration<WeavingHook>reg3 = hook.register(getContext());
			try {
				weavingClasses.loadClass(TEST_CLASS_NAME);
				fail("Should blow up");
			} catch (ClassFormatError cfe) {
				assertWiring();

				assertTrue("Should be complete now", wc.isWeavingComplete());
				assertNotSame("Should get copies of the byte array now", realBytes, wc.getBytes());
				assertTrue("Content should still be equal though", Arrays.equals(realBytes, wc.getBytes()));
				assertEquals("Wrong class", TEST_CLASS_NAME, wc.getClassName());
				assertNull("Should not be set", wc.getDefinedClass());

				assertImmutableList();

				try {
					wc.setBytes(fakeBytes);
					fail("Should not be possible");
				} catch (IllegalStateException ise) {
					//No action needed
				}
			}

			reg3.unregister();
		} finally {
			unregisterThisHook();
		}
	}

	/**
	 * Try messing up the Dynamic import list in any way possible
	 */
	private void assertImmutableList() {
		assertImmutableList(wc);
	}

	private void registerThisHook() throws Exception {
		reg1 = getContext().registerService(WeavingHook.class, this, null);
		reg2 = getContext().registerService(WeavingHook.class, this, null);
	}

	private void unregisterThisHook() throws Exception {
		wc = null;
		reg1.unregister();
		reg2.unregister();
	}

	/**
	 * Test the various {@link WovenClass} methods inside a WeavingHook
	 */
	public void weave(WovenClass wovenClass) {
		assertEquals("Wrong state", WovenClass.TRANSFORMING, wovenClass.getState());

		if(wc == null) {
			//First time through
			wc = wovenClass;

			assertWiring();

			realBytes = wovenClass.getBytes();
			assertSame("Should be the same byte array!", realBytes, wovenClass.getBytes());

			assertEquals("Wrong class", TEST_CLASS_NAME, wc.getClassName());

			assertNull("Should not be defined yet", wc.getDefinedClass());

			List<String> dynamicImports = wc.getDynamicImports();
			assertSame("Should be the same List!", dynamicImports, wovenClass.getDynamicImports());
			assertTrue("No imports yet", dynamicImports.isEmpty());

			dynamicImports.add("org.osgi.framework;version=1.5");

			assertFalse("Weaving should not be complete", wc.isWeavingComplete());

			wc.setBytes(fakeBytes);
			assertSame("Should be the same byte array!", fakeBytes, wovenClass.getBytes());
		} else {
			//Second time through
			assertSame("Should be the same byte array!", fakeBytes, wovenClass.getBytes());
			wc.setBytes(realBytes);
			List<String> dynamicImports = wc.getDynamicImports();
			assertEquals("Should be one import", 1, dynamicImports.size());
			dynamicImports.clear();
			assertTrue("Should be no imports now", dynamicImports.isEmpty());
			dynamicImports.add(ORG_OSGI_FRAMEWORK_VERSION_1_6);
		}

	}

	/**
	 * Check that the BundleWiring is usable
	 */
	private void assertWiring() {
		BundleWiring bw = wc.getBundleWiring();

		assertTrue("Should be the current bundle", bw.isCurrent());
		assertEquals("Wrong bundle", TESTCLASSES_SYM_NAME,
				bw.getBundle().getSymbolicName());
		assertEquals("Wrong bundle", Version.parseVersion("1.0.0"),
				bw.getBundle().getVersion());
		assertNotNull("No Classloader", bw.getClassLoader());
	}
	
	private WovenClass listenerWovenClass;
	private final List<Integer> wovenClassStates = new ArrayList<Integer>(2);
	
	public void modified(WovenClass wovenClass) {
		if (listenerWovenClass == null)
			listenerWovenClass = wovenClass;
		else
			assertSame("Wrong woven class", wovenClass, listenerWovenClass);
		wovenClassStates.add(wovenClass.getState());
		switch(wovenClass.getState()) {
			case WovenClass.TRANSFORMING:
				fail("Woven class listeners must not be notified of the TRANSFORMING state");
			case WovenClass.TRANSFORMED:
				assertTransformed(wovenClass);
				break;
			case WovenClass.TRANSFORMING_FAILED:
				assertTransformingFailed(wovenClass);
				break;
			case WovenClass.DEFINED:
				assertDefined(wovenClass);
				break;
			case WovenClass.DEFINE_FAILED:
				assertDefineFailed(wovenClass);
				break;
			default:
				fail("Invalid state for woven class");
		}
	}
	
	private void assertTransformed(WovenClass wovenClass) {
		assertState(wovenClass, WovenClass.TRANSFORMED);
		assertImmutableBytes(wovenClass);
		assertImmutableList(wovenClass);
		assertClassNotDefined(wovenClass);
		assertBundleWiringNotUpdated(wovenClass);
		assertWeavingIncomplete(wovenClass);
	}
	
	private void assertTransformingFailed(WovenClass wovenClass) {
		assertState(wovenClass, WovenClass.TRANSFORMING_FAILED);
		assertImmutableBytes(wovenClass);
		assertImmutableList(wovenClass);
		assertClassNotDefined(wovenClass);
		assertBundleWiringNotUpdated(wovenClass);
		assertWeavingComplete(wovenClass);
	}
	
	private void assertDefined(WovenClass wovenClass) {
		assertState(wovenClass, WovenClass.DEFINED);
		assertImmutableBytes(wovenClass);
		assertImmutableList(wovenClass);
		assertClassDefined(wovenClass);
		assertBundleWiringUpdated(wovenClass);
		assertWeavingComplete(wovenClass);
	}
	
	private void assertDefineFailed(WovenClass wovenClass) {
		assertState(wovenClass, WovenClass.DEFINE_FAILED);
		assertImmutableBytes(wovenClass);
		assertImmutableList(wovenClass);
		assertClassNotDefined(wovenClass);
		assertBundleWiringNotUpdated(wovenClass);
		assertWeavingComplete(wovenClass);
	}
	
	private void assertClassDefined(WovenClass wovenClass) {
		assertNotNull("Class was not defined", wovenClass.getDefinedClass());
	}
	
	private void assertClassNotDefined(WovenClass wovenClass) {
		assertNull("Class was defined", wovenClass.getDefinedClass());
	}
	
	private void assertWeavingComplete(WovenClass wovenClass) {
		assertTrue("Weaving was not complete", wovenClass.isWeavingComplete());
	}
	
	private void assertWeavingIncomplete(WovenClass wovenClass) {
		assertFalse("Weaving was complete", wovenClass.isWeavingComplete());
	}
	
	private static void assertState(WovenClass wovenClass, int expected) {
		assertEquals("Wrong state", expected, wovenClass.getState());
	}

	private static void assertImmutableBytes(WovenClass wovenClass) {
		try {
			wovenClass.setBytes(new byte[0]);
			fail("Bytes were mutable");
		} 
		catch (IllegalStateException e) {
			// Okay
		}
	}
	
	private ServiceRegistration<WovenClassListener> listenerReg;
	
	private void registerThisListener() throws Exception {
		listenerReg = getContext().registerService(WovenClassListener.class, this, null);
	}

	private void unregisterThisListener() {
		listenerReg.unregister();
	}
	
	private void registerAll() throws Exception {
		registerThisHook();
		registerThisListener();
	}
	
	private void unregisterAll() throws Exception {
		unregisterThisListener();
		unregisterThisHook();
	}
	
	/**
	 * Test the basic contract of WovenClassListener.
	 * @throws Exception
	 */
	public void testWovenClassListener() throws Exception {
		registerAll();
		try {
			Class<?> clazz = weavingClasses.loadClass(TEST_CLASS_NAME);
			assertDefinedClass(listenerWovenClass, clazz);
			assertStates(WovenClass.TRANSFORMED, WovenClass.DEFINED);
		} 
		finally {
			unregisterAll();
		}
	}
	
	private void assertStates(Integer...states) {
		assertEquals("Listener did not receive notification of all states", states.length, wovenClassStates.size());
		int i = 0;
		for (Integer state : states)
			assertEquals("Wrong state", state, wovenClassStates.get(i++));
	}
	
	/**
	 * Test the class load does not fail if a listener throws an exception.
	 * @throws Exception
	 */
	public void testWovenClassListenerExceptionDoesNotCauseClassLoadToFail() throws Exception {
		final AtomicInteger integer = new AtomicInteger(0);
		ServiceRegistration<WovenClassListener> reg = getContext().registerService(
				WovenClassListener.class, 
				new WovenClassListener() {
					public void modified(WovenClass wovenClass) {
						integer.set(1);
						throw new RuntimeException();
					}
				}, 
				null);
		try {
			registerAll();
			try {
				Class<?> clazz = weavingClasses.loadClass(TEST_CLASS_NAME);
				assertEquals("Listener not called", 1, integer.get());
				assertDefinedClass(listenerWovenClass, clazz);
				assertStates(WovenClass.TRANSFORMED, WovenClass.DEFINED);
			}
			finally {
				unregisterAll();
			}
		}
		finally {
			reg.unregister();
		}
	}
	
	/**
	 * Test that listeners are still notified when weaving hook throws exception.
	 * @throws Exception
	 */
	public void testWovenClassListenerCalledWhenWeavingHookException() throws Exception {
		final AtomicInteger integer = new AtomicInteger(0);
		Dictionary<String, Object> props = new Hashtable<String, Object>(1);
		props.put(Constants.SERVICE_RANKING, Integer.valueOf(Integer.MIN_VALUE));
		ServiceRegistration<WeavingHook> reg = getContext().registerService(
				WeavingHook.class, 
				new WeavingHook() {
					public void weave(WovenClass wovenClass) {
						integer.set(1);
						throw new RuntimeException();
					}
				}, 
				props);
		try {
			registerAll();
			try {
				weavingClasses.loadClass(TEST_CLASS_NAME);
				fail("Class should have failed to load");
			}
			catch (ClassFormatError e) {
				assertEquals("Hook not called", 1, integer.get());
				assertStates(WovenClass.TRANSFORMING_FAILED);
			}
			finally {
				unregisterAll();
			}
		}
		finally {
			reg.unregister();
		}
	}
	
	/**
	 * Test that listeners are still notified when class definition fails.
	 * @throws Exception
	 */
	public void testWovenClassListenerCalledWhenClassDefinitionFails() throws Exception {
		final AtomicInteger integer = new AtomicInteger(0);
		ServiceRegistration<WeavingHook> reg = getContext().registerService(
				WeavingHook.class, 
				new WeavingHook() {
					public void weave(WovenClass wovenClass) {
						integer.set(1);
						wovenClass.setBytes(new byte[0]);
					}
				}, 
				null);
		try {
			registerThisListener();
			try {
				weavingClasses.loadClass(TEST_CLASS_NAME);
				fail("Class should have failed to load");
			}
			catch (ClassFormatError e) {
				assertEquals("Hook not called", 1, integer.get());
				assertStates(WovenClass.TRANSFORMED, WovenClass.DEFINE_FAILED);
			}
			finally {
				unregisterThisListener();
			}
		}
		finally {
			reg.unregister();
		}
	}
	
	/**
	 * Test that listeners are not notified when no weaving hooks are registered.
	 * @throws Exception
	 */
	public void testWovenClassListenerNotNotifiedWhenNoWeavingHooks() throws Exception {
		registerThisListener();
		try {
			weavingClasses.loadClass(TEST_CLASS_NAME);
			assertNull("Listener notified with no weaving hooks registered", listenerWovenClass);
			assertStates();
		}
		finally {
			unregisterThisListener();
		}
	}
	
	private static void assertBundleWiringUpdated(WovenClass wovenClass) {
		assertFalse("Bundle wiring was not updated", wovenClass.getBundleWiring().getRequirements(PackageNamespace.PACKAGE_NAMESPACE).isEmpty());
	}
	
	private static void assertBundleWiringNotUpdated(WovenClass wovenClass) {
		assertTrue("Bundle wiring was updated", wovenClass.getBundleWiring().getRequirements(PackageNamespace.PACKAGE_NAMESPACE).isEmpty());
	}
	
	private static void assertDefinedClass(WovenClass wovenClass, Class<?> clazz) {
		assertEquals("Wrong defined class", clazz, wovenClass.getDefinedClass());
	}
	
	private static void assertImmutableList(WovenClass wc) {
		List<String> dynamicImports = wc.getDynamicImports();
		try {
			dynamicImports.clear();
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.remove(0);
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.remove(ORG_OSGI_FRAMEWORK_VERSION_1_6);
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.removeAll(Arrays.asList(new String[] {ORG_OSGI_FRAMEWORK_VERSION_1_6}));
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.add(ORG_OSGI_FRAMEWORK_VERSION_1_6);
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.add(0, "foo");
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.addAll(Arrays.asList(new String[] {"bar"}));
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.addAll(0, Arrays.asList(new String[] {"bar"}));
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.set(0, "foo");
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			dynamicImports.retainAll(Arrays.asList(new String[] {"bar"}));
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			Iterator<String> it = dynamicImports.iterator();
			it.next();
			it.remove();
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}

		try {
			Iterator<String> it = dynamicImports.listIterator();
			it.next();
			it.remove();
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}
	}
}
