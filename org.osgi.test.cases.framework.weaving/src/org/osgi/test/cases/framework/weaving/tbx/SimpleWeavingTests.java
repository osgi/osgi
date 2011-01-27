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
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests that Basic weaving works properly
 * 
 * @author IBM
 */
public class SimpleWeavingTests extends DefaultTestBundleControl {
	
	/**
	 * Perform a basic weave, and show the loaded class is changed
	 * @throws Exception
	 */
	public void testBasicWeaving() throws Exception {
		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		ServiceRegistration reg = null;
		try {
			reg = new ConfigurableWeavingHook().register(getContext(), 0);
			Class clazz = weavingClasses.loadClass(TestConstants.TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "WOVEN", clazz.newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
			uninstallBundle(weavingClasses);
		}
	}
	
	/**
	 * Perform a basic weave that adds an import, and show the loaded class fails if
	 * the hook does not add the import
	 * @throws Exception
	 */
	public void testBasicWeavingNoDynamicImport() throws Exception {
		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		ServiceRegistration reg = null;
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.setChangeTo("org.osgi.framework.Bundle");
		try {
			reg = hook.register(getContext(), 0);
			Class clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			clazz.newInstance().toString();
			fail("Should fail to load the Bundle class");
		} catch (RuntimeException cnfe) {
			assertEquals("Wrong exception", 
					"java.lang.ClassNotFoundException: org.osgi.framework.Bundle", 
					cnfe.getCause().toString());
		} finally {
			if (reg != null)
				reg.unregister();
			uninstallBundle(weavingClasses);
		}
	}
	
	/**
	 * Perform a basic weave that adds an import, and show the loaded class works if
	 * the hook adds the import
	 * @throws Exception
	 */
	public void testBasicWeavingDynamicImport() throws Exception {
		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle(TestConstants.TESTCLASSES_JAR);
		ServiceRegistration reg = null;
		ConfigurableWeavingHook hook = new ConfigurableWeavingHook();
		hook.addImport("org.osgi.framework");
		hook.setChangeTo("org.osgi.framework.Bundle");
		try {
			reg = hook.register(getContext(), 0);
			Class clazz = weavingClasses.loadClass(TestConstants.DYNAMIC_IMPORT_TEST_CLASS_NAME);
			assertEquals("Weaving was unsuccessful", "interface org.osgi.framework.Bundle", 
					clazz.newInstance().toString());
		} finally {
			if (reg != null)
				reg.unregister();
			uninstallBundle(weavingClasses);
		}
	}
}
