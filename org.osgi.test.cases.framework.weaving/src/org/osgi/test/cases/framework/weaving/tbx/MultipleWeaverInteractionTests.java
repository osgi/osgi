/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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
package org.osgi.test.cases.framework.weaving.tbx;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
*
*/
public class MultipleWeaverInteractionTests extends DefaultTestBundleControl {
	
	/**
	 * Test that multiple weavers get called in service id order
	 * @throws Exception
	 */
	public void testMultipleWeavers() throws Exception {
		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		
		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();
		
		hook1.setChangeTo("1 Finished");
		hook2.setExpected("1 Finished");
		hook2.setChangeTo("2 Finished");
		hook3.setExpected("2 Finished");
		hook3.setChangeTo("Chain Complete");
		
		ServiceRegistration reg1 = null;
		ServiceRegistration reg2= null;
		ServiceRegistration reg3 = null;
		try {
			reg1 = hook1.register(getContext(), 0);
			reg2 = hook2.register(getContext(), 0);
			reg3 = hook3.register(getContext(), 0);
			Class clazz = weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "Chain Complete", clazz.newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
			uninstallBundle(weavingClasses);
		}
	}
	
	/**
	 * Test that multiple weavers get called in ranking and service id order
	 * @throws Exception
	 */
	public void testMultipleWeaversWithRankings() throws Exception {
		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		
		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();
		
		//Called in proper order
		hook3.setChangeTo("3 Finished");
		hook1.setExpected("3 Finished");
		hook1.setChangeTo("1 Finished");
		hook2.setExpected("1 Finished");
		hook2.setChangeTo("Chain Complete");
		
		
		ServiceRegistration reg1 = null;
		ServiceRegistration reg2= null;
		ServiceRegistration reg3 = null;
		try {
			reg1 = hook1.register(getContext(), 0);
			reg2 = hook2.register(getContext(), 0);
			reg3 = hook3.register(getContext(), 1);
			Class clazz = weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "Chain Complete", clazz.newInstance().toString());
			
			// We expect the order to change if we update our ranking
			Hashtable table = new Hashtable();
			table.put(Constants.SERVICE_RANKING, new Integer(2));
			reg2.setProperties(table);
			
			hook2.setExpected("DEFAULT");
			hook2.setChangeTo("2 Finished");
			hook3.setExpected("2 Finished");
			hook3.setChangeTo("3 Finished");
			hook1.setChangeTo("org.osgi.framework.hooks.weaving.WovenClass");
			
			hook2.addImport("org.osgi.framework.hooks.weaving");
			
			clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.hooks.weaving.WovenClass", 
					clazz.newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
			uninstallBundle(weavingClasses);
		}
	}
	
	/**
	 * Test that an exception stops the weaving chain mid-way
	 * @throws Exception
	 */
	public void testExceptionPreventsSubsequentCalls() throws Exception {

		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		
		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();
		
		RuntimeException cause = new RuntimeException();
		
		//If hook 2 throws an exception then 3 should not be called
		
		hook1.setChangeTo("1 Finished");
		hook2.setExceptionToThrow(cause);
		hook3.setExpected("2 Finished");
		hook3.setChangeTo("Chain Complete");
		
		ServiceRegistration reg1 = null;
		ServiceRegistration reg2= null;
		ServiceRegistration reg3 = null;
		try {
			reg1 = hook1.register(getContext(), 0);
			reg2 = hook2.register(getContext(), 0);
			reg3 = hook3.register(getContext(), 0);
			weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
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
			uninstallBundle(weavingClasses);
		}
	}
	
	/**
	 * Test that an exception causes blacklisting 
	 * @throws Exception
	 */
	public void testExceptionCausesBlackListing() throws Exception {

		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		
		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();
		
		RuntimeException cause = new RuntimeException();
		
		hook1.setChangeTo("1 Finished");
		hook2.setExceptionToThrow(cause);
		hook3.setExpected("1 Finished");
		hook3.setChangeTo("Chain Complete");
		
		ServiceRegistration reg1 = null;
		ServiceRegistration reg2= null;
		ServiceRegistration reg3 = null;
		ClassLoadErrorListener listener = new ClassLoadErrorListener(cause, getContext().getBundle());
		try {
			try {
				reg1 = hook1.register(getContext(), 0);
				reg2 = hook2.register(getContext(), 0);
				reg3 = hook3.register(getContext(), 0);
			
				getContext().addFrameworkListener(listener);
			
				weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
				fail("Class should fail to Load");
			} catch (ClassFormatError cfe) {
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
	
		
			Class clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertTrue("Hook 1 should be called", hook1.isCalled());
			assertFalse("Hook 2 should not be called", hook2.isCalled());
			assertTrue("Hook 3 should be called", hook3.isCalled());
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.wiring.BundleWiring", 
					clazz.newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
			uninstallBundle(weavingClasses);
		}
	}
	
	/**
	 * Test that a WeavingException does not result in blacklisting
	 * @throws Exception
	 */
	public void testWeavingExceptionDoesNotCauseBlackListing() throws Exception {

		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		
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
		
		ServiceRegistration reg1 = null;
		ServiceRegistration reg2= null;
		ServiceRegistration reg3 = null;
		ClassLoadErrorListener listener = new ClassLoadErrorListener(cause, getContext().getBundle());
		try {
			try {
				reg1 = hook1.register(getContext(), 0);
				reg2 = hook2.register(getContext(), 0);
				reg3 = hook3.register(getContext(), 0);
				
				getContext().addFrameworkListener(listener);
				
				weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
				fail("Class should fail to Load");
			} catch (ClassFormatError cfe) {
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
		
			Class clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertTrue("Hook 1 should be called", hook1.isCalled());
			assertTrue("Hook 2 should not be called", hook2.isCalled());
			assertTrue("Hook 3 should be called", hook3.isCalled());
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.wiring.BundleWiring", 
					clazz.newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
			uninstallBundle(weavingClasses);
		}
	}
	
	/**
	 * Test that the registration, not the object, is blacklisted
	 * @throws Exception
	 */
	public void testBlackListingOnlyAppliesToRegistration () throws Exception {

		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		
		ConfigurableWeavingHook hook1 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook2 = new ConfigurableWeavingHook();
		ConfigurableWeavingHook hook3 = new ConfigurableWeavingHook();
		
		RuntimeException cause = new RuntimeException();
		
		hook1.setChangeTo("1 Finished");
		hook2.setExceptionToThrow(cause);
		hook3.setExpected("1 Finished");
		hook3.setChangeTo("Chain Complete");
		
		ServiceRegistration reg1 = null;
		ServiceRegistration reg2= null;
		ServiceRegistration reg3 = null;
		ClassLoadErrorListener listener = new ClassLoadErrorListener(cause, getContext().getBundle());
		try {
			try {
				reg1 = hook1.register(getContext(), 0);
				reg2 = hook2.register(getContext(), 0);
				reg3 = hook3.register(getContext(), 0);
				
				getContext().addFrameworkListener(listener);
				
				weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
				fail("Class should fail to Load");
			} catch (ClassFormatError cfe) {
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
			Class clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertTrue("Hook 1 should be called", hook1.isCalled());
			assertTrue("Hook 2 should not be called", hook2.isCalled());
			assertTrue("Hook 3 should be called", hook3.isCalled());
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.wiring.BundleRevision", 
					clazz.newInstance().toString());
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
			uninstallBundle(weavingClasses);
		}
	}
	
}
