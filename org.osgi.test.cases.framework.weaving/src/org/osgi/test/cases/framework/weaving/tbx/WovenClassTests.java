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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests the {@link WovenClass} behaviour, including immutability 
 * 
 * @author IBM
 */
public class WovenClassTests extends DefaultTestBundleControl implements WeavingHook{
	
	/** An import string used in these tests */
	private static final String ORG_OSGI_FRAMEWORK_VERSION_1_6 = "org.osgi.framework;version=1.6";
	/** The {@link WovenClass} from the test weave */
	private WovenClass wc = null;
	/** The first hook registration */
	private ServiceRegistration reg1;
	/** The second hook registration */
	private ServiceRegistration reg2;
	/** The bundle containing the test classes */
	private Bundle weavingClasses;
	/** The real class bytes */
	private byte[] realBytes;
	/** Some fake class bytes */
	private byte[] fakeBytes = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
	
	/**
	 * Test the basic contract of WovenClass, incluing immutability after a weave has finished
	 * @throws Exception
	 */
	public void testWovenClass() throws Exception {
		
		Class clazz = weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
		
		assertWiring();
		
		assertTrue("Should be complete now", wc.isWeavingComplete());
		assertNotSame("Should get copies of the byte array now", realBytes, wc.getBytes());
		assertEquals("Wrong class",TestConstants.TEST_CLASS_NAME, wc.getClassName());
		assertSame("Should be set now", clazz, wc.getDefinedClass());
		assertSame("Should be set now", clazz.getProtectionDomain(), wc.getProtectionDomain());
		
		assertImmutableList();
		
		try {
			wc.setBytes(fakeBytes);
			fail("Should not be possible");
		} catch (IllegalStateException ise) {
			//No action needed
		}
	}
	
	/**
	 * Test the basic contract of WovenClass, including immutability after a weave has failed with bad
	 * bytes
	 * @throws Exception
	 */
	public void testBadWeaveClass() throws Exception {
		
		reg2.unregister();
		
		try {
			weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
			fail("Should have dud bytes here!");
		} catch (ClassFormatError cfe) {
			assertWiring();
		
			assertTrue("Should be complete now", wc.isWeavingComplete());
			assertNotSame("Should get copies of the byte array now", fakeBytes, wc.getBytes());
			assertTrue("Content should still be equal though", Arrays.equals(fakeBytes, wc.getBytes()));
			assertEquals("Wrong class",TestConstants.TEST_CLASS_NAME, wc.getClassName());
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
	}
	
	/**
	 * Test the basic contract of WovenClass, including immutability after a weave has failed with
	 * an exception
	 * @throws Exception
	 */
	public void testHookException() throws Exception {
		
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.setExceptionToThrow(new RuntimeException());
		ServiceRegistration reg3 = hook.register(getContext());
		try {
			weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
			fail("Should blow up");
		} catch (ClassFormatError cfe) {
			assertWiring();
		
			assertTrue("Should be complete now", wc.isWeavingComplete());
			assertNotSame("Should get copies of the byte array now", realBytes, wc.getBytes());
			assertTrue("Content should still be equal though", Arrays.equals(realBytes, wc.getBytes()));
			assertEquals("Wrong class",TestConstants.TEST_CLASS_NAME, wc.getClassName());
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
	}

	/**
	 * Try messing up the Dynamic import list in any way possible
	 */
	private void assertImmutableList() {
		List dynamicImports = wc.getDynamicImports();
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
			Iterator it = dynamicImports.iterator();
			it.next();
			it.remove();
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}
		
		try {
			Iterator it = dynamicImports.listIterator();
			it.next();
			it.remove();
			fail("Should be immutable");
		} catch (Exception e) {
			//No action needed
		}
	}
	
	protected void setUp() throws Exception {
		reg1 = getContext().registerService(WeavingHook.class, this, null);
		reg2 = getContext().registerService(WeavingHook.class, this, null);
		weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
	}
	
	protected void tearDown() throws Exception {
		wc = null;
		uninstallBundle(weavingClasses);
		reg1.unregister();
		reg2.unregister();
	}
	
	/**
	 * Test the various {@link WovenClass} methods inside a WeavingHook
	 */
	public void weave(WovenClass wovenClass) {
		
		if(wc == null) {
			//First time through
			wc = wovenClass;
			
			assertWiring();
			
			realBytes = wovenClass.getBytes();
			assertSame("Should be the same byte array!", realBytes, wovenClass.getBytes());
			
			assertEquals("Wrong class",TestConstants.TEST_CLASS_NAME, wc.getClassName());

			assertNull("Should not be defined yet", wc.getDefinedClass());
			
			List dynamicImports = wc.getDynamicImports();
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
			List dynamicImports = wc.getDynamicImports();
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
		assertEquals("Wrong bundle", TestConstants.TESTCLASSES_SYM_NAME, 
				bw.getBundle().getSymbolicName());
		assertEquals("Wrong bundle", Version.parseVersion("1.0.0"), 
				bw.getBundle().getVersion());
		assertNotNull("No Classloader", bw.getClassLoader());
	}
}
