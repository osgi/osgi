/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.test.cases.composite.junit;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;



public class CompositePropertyTests extends AbstractCompositeTestCase {

	static final String TEST1 = "test1";
	static final String TEST2 = "test2";
	static final String VALUE1 = "value1";
	static final String VALUE2 = "value2";

	public void setUp() {
		super.setUp();
	}

	public void testProperties01() {
		// Test scoping of BundleContext properties for the composite system bundle
		Map configuration = new HashMap();
		configuration.put(TEST1, VALUE1);
		configuration.put(TEST2, VALUE2);
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, configuration);
		String value1 = composite.getSystemBundleContext().getProperty(TEST1);
		assertEquals("Wrong value1", VALUE1, value1);
		String value2 = composite.getSystemBundleContext().getProperty(TEST2);
		assertEquals("Wrong value2", VALUE2, value2);
	}

	public void testProperties02() {
		// Test scoping of BundleContext properties for a constituent bundle
		Map configuration = new HashMap();
		configuration.put(TEST1, VALUE1);
		configuration.put(TEST2, VALUE2);
		CompositeBundle composite = createCompositeBundle(compAdmin, getName(), null, configuration);
		Bundle tb1 = installConstituent(composite, "tb1", "tb1.jar");
		startCompositeBundle(composite);
		try {
			tb1.start();
		} catch (BundleException e) {
			fail("Failed to start tb1.", e);
		}
		String value1 = tb1.getBundleContext().getProperty(TEST1);
		assertEquals("Wrong value1", VALUE1, value1);
		String value2 = tb1.getBundleContext().getProperty(TEST2);
		assertEquals("Wrong value2", VALUE2, value2);
	}
}
