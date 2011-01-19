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
*
*/
public class SimpleWeavingTests extends DefaultTestBundleControl {
	
	public void testBasicWeaving() throws Exception {
		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle("testclasses.jar");
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
	
	public void testBasicWeavingDynamicImport() throws Exception {
		// Install the bundles necessary for this test
		Bundle weavingClasses = installBundle("testclasses.jar");
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
}
