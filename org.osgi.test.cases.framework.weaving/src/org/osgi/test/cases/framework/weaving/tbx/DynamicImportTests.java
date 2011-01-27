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

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests that Dynamic imports are added properly
 * 
 * @author IBM
 */
public class DynamicImportTests extends DefaultTestBundleControl {
	
	/** The test classes */
	private Bundle weavingClasses;
	//Various sources for the SymbolicNameVersion class
	private Bundle im100;
	private Bundle im110;
	private Bundle im160;
	private Bundle altIM;

	/**
	 * Test that adding attributes gets the correct resolution
	 * @param attributes Attributes to add to the import package (include a leading ';')
	 * @param result The expected toString from the woven class
	 * @throws Exception
	 */
	private void doTest(String attributes, String result) throws Exception {
		
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.addImport(TestConstants.IMPORT_TEST_CLASS_PKG + attributes);
		hook.setChangeTo(TestConstants.IMPORT_TEST_CLASS_NAME);
		ServiceRegistration reg = null;
		try {
			reg = hook.register(getContext(), 0);
			Class clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", result, clazz.newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
		}
	}
	
	/**
	 * A basic test with a version range
	 * @throws Exception
	 */
	public void testDynamicImport() throws Exception {
		doTest(";version=\"[1,2)\"", TestConstants.TEST_ALT_IMPORT_SYM_NAME + "_1.0.0");
	}
	
	/**
	 * A test with a version range that prevents the "best" package
	 * @throws Exception
	 */
	public void testVersionConstrainedDynamicImport() throws Exception {
		doTest(";version=\"[1,1.5)\"" , TestConstants.TEST_IMPORT_SYM_NAME + "_1.1.0");
	}
	
	/**
	 * A test with a bundle-symbolic-name attribute
	 * @throws Exception
	 */
	public void testBSNConstrainedDynamicImport() throws Exception {
		doTest(";" + Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE + "=" 
				+ TestConstants.TEST_IMPORT_SYM_NAME, TestConstants.TEST_IMPORT_SYM_NAME + "_1.1.0");
	}
	
	/**
	 * A test with a bundle-symbolic-name attribute and a version constraint
	 * @throws Exception
	 */
	public void testBSNAndVersionConstrainedDynamicImport() throws Exception {
		doTest(";version=\"[1.0,1.1)\";" + Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE + "="
				+ TestConstants.TEST_IMPORT_SYM_NAME, TestConstants.TEST_IMPORT_SYM_NAME + "_1.0.0");
	}
	
	/**
	 * A test with an attribute constraint
	 * @throws Exception
	 */
	public void testAttributeConstrainedDynamicImport() throws Exception {
		doTest(";foo=bar", TestConstants.TEST_IMPORT_SYM_NAME + "_1.0.0");
	}
	
	/**
	 * A test with a mandatory attribute constraint
	 * @throws Exception
	 */
	public void testMandatoryAttributeConstrainedDynamicImport() throws Exception {
		doTest(";prop=val", TestConstants.TEST_IMPORT_SYM_NAME + "_1.6.0");
	}
	
	/**
	 * A test with multiple imports that would wire differently
	 * @throws Exception
	 */
	public void testMultipleConflictingDynamicImports() throws Exception {
		
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.addImport(TestConstants.IMPORT_TEST_CLASS_PKG + ";version=\"(1.0,1.5)\"");
		hook.addImport(TestConstants.IMPORT_TEST_CLASS_PKG + ";foo=bar");
		hook.setChangeTo(TestConstants.IMPORT_TEST_CLASS_NAME);
		ServiceRegistration reg = null;
		try {
			reg = hook.register(getContext(), 0);
			Class clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", TestConstants.TEST_IMPORT_SYM_NAME + "_1.1.0", clazz.newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
		}
	}
	
	/**
	 * A test with a bad input that causes a failure
	 * @throws Exception
	 */
	public void testBadDynamicImportString() throws Exception {
		
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.addImport(TestConstants.IMPORT_TEST_CLASS_PKG + ";version=(1.0,1.5)\"");
		hook.setChangeTo(TestConstants.IMPORT_TEST_CLASS_NAME);
		ServiceRegistration reg = null;
		try {
			reg = hook.register(getContext(), 0);
			weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			fail("Should not get here!");
		} catch (ClassFormatError cfe) {
			if(!!!(cfe.getCause() instanceof IllegalArgumentException))
				fail("The class load should generate an IllegalArgumentException due " +
						"to the bad dynamic import string " + cfe.getMessage());
		} finally {
			if (reg != null)
				reg.unregister();
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		uninstallBundle(weavingClasses);
		uninstallBundle(im100);
		uninstallBundle(im110);
		uninstallBundle(im160);
		uninstallBundle(altIM);
	}

	protected void setUp() throws Exception {
		super.setUp();
		weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		im100 = installBundle(TestConstants.TEST_IMPORT_100_JAR);
		im110 = installBundle(TestConstants.TEST_IMPORT_110_JAR);
		im160 = installBundle(TestConstants.TEST_IMPORT_160_JAR);
		altIM = installBundle(TestConstants.TEST_ALT_IMPORT_JAR);
	}
}
