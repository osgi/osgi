/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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


package org.osgi.test.cases.framework.junit.frameworkutil;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkUtil;
import org.osgi.test.support.OSGiTestCase;

public class BundleReferenceTests extends OSGiTestCase {

	public void testBundleReference() {
		Bundle me = getContext().getBundle();
		Class self = getClass();
		ClassLoader cl = self.getClassLoader();
		assertTrue("classloader is not an instanceof BundleReference",
				cl instanceof BundleReference);
		BundleReference ref = (BundleReference) cl;
		assertEquals("wrong bundle returned", me, ref.getBundle());
	}

	public void testGetBundle() {
		Bundle me = getContext().getBundle();
		Class self = getClass();
		assertEquals("wrong bundle returned", me, FrameworkUtil.getBundle(self));
	}

	public void testBootClass() {
		assertNull("expected null", FrameworkUtil.getBundle(Object.class));
	}

	public void testNull() {
		try {
			FrameworkUtil.getBundle(null);
			fail("expected runtime exception");
		}
		catch (RuntimeException e) {
			// expected
		}
	}
}
