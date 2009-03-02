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

package org.osgi.test.cases.framework.junit.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.framework.BundlePermission;
import org.osgi.test.support.PermissionTestCase;

public class BundlePermissionTests extends PermissionTestCase {

	public void testInvalid() {
		invalidBundlePermission("a.b.c", "x");
		invalidBundlePermission("a.b.c", "   host  ,  x   ");
		invalidBundlePermission("a.b.c", "");
		invalidBundlePermission("a.b.c", "      ");
		invalidBundlePermission("a.b.c", null);
		invalidBundlePermission("a.b.c", ",");
		invalidBundlePermission("a.b.c", ",xxx");
		invalidBundlePermission("a.b.c", "xxx,");
		invalidBundlePermission("a.b.c", "provide,");
		invalidBundlePermission("a.b.c", "require,   ");
		invalidBundlePermission("a.b.c", "hostme,");
		invalidBundlePermission("a.b.c", "fragmentme,");
		invalidBundlePermission("a.b.c", ",host");
		invalidBundlePermission("a.b.c", ",fragment");
		invalidBundlePermission("a.b.c", "   provideme   ");
		invalidBundlePermission("a.b.c", "   requireme     ");
		invalidBundlePermission("a.b.c", "   hos");
		invalidBundlePermission("a.b.c", "   fragmen"); 
	}

	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read"); 

		BundlePermission p11 = new BundlePermission("com.foo.service1",
				"    REQUIRE,provide   ");
		BundlePermission p12 = new BundlePermission("com.foo.service1",
				"PROVIDE  ,   require");
		BundlePermission p13 = new BundlePermission("com.foo.service1",
				"proVIDE   ");
		BundlePermission p14 = new BundlePermission("com.foo.service1",
				"    Require    "); 
		BundlePermission p15 = new BundlePermission("com.foo.service1",
				"    fraGMent    ");
		BundlePermission p16 = new BundlePermission("com.foo.service1",
				"    HosT    "); 

		assertImplies(p11, p11);
		assertImplies(p11, p12);
		assertImplies(p11, p13);
		assertImplies(p11, p14);
		assertNotImplies(p11, p15);
		assertNotImplies(p11, p16);

		assertImplies(p12, p11);
		assertImplies(p12, p12);
		assertImplies(p12, p13);
		assertImplies(p12, p14);
		assertNotImplies(p12, p15);
		assertNotImplies(p12, p16);

		assertImplies(p13, p11);
		assertImplies(p13, p12);
		assertImplies(p13, p13);
		assertImplies(p13, p14);
		assertNotImplies(p13, p15);
		assertNotImplies(p13, p16);

		assertImplies(p14, p14);
		assertNotImplies(p14, p11);
		assertNotImplies(p14, p12);
		assertNotImplies(p14, p13);
		
		assertImplies(p15, p15);
		assertNotImplies(p15, p11);
		assertNotImplies(p15, p12);
		assertNotImplies(p15, p13);

		assertImplies(p16, p16);
		assertNotImplies(p16, p11);
		assertNotImplies(p16, p12);
		assertNotImplies(p16, p13);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertEquals(p11, p13);
		assertEquals(p12, p11);
		assertEquals(p12, p12);
		assertEquals(p12, p13);
		assertEquals(p13, p11);
		assertEquals(p13, p12);
		assertEquals(p13, p13);
		assertEquals(p14, p14);
		assertEquals(p15, p15);
		assertEquals(p16, p16);

		assertNotEquals(p11, p14);
		assertNotEquals(p12, p14);
		assertNotEquals(p13, p14);
		assertNotEquals(p13, p15);
		assertNotEquals(p13, p16);
		assertNotEquals(p14, p11);
		assertNotEquals(p14, p12);
		assertNotEquals(p14, p13);
		assertNotEquals(p14, p15);
		assertNotEquals(p14, p16);

		PermissionCollection pc = p13.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p11);

		assertAddPermission(pc, p14);
		assertImplies(pc, p14);
		assertNotImplies(pc, p11);
		assertNotImplies(pc, p12);
		assertNotImplies(pc, p13);

		assertAddPermission(pc, p13);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

		assertNotAddPermission(pc, op);
		assertSerializable(pc);

		pc = p13.newPermissionCollection();

		assertAddPermission(pc, p13);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertSerializable(pc);

		pc = p11.newPermissionCollection();

		assertAddPermission(pc, p11);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
		assertSerializable(p15);
		assertSerializable(p16);
		assertSerializable(pc);
	}

	public void testNames() {
		BundlePermission p21 = new BundlePermission("com.foo.service2",
				"host");
		BundlePermission p22 = new BundlePermission("com.foo.*", "host");
		BundlePermission p23 = new BundlePermission("com.*", "host");
		BundlePermission p24 = new BundlePermission("org.foo.service2", "host");
		BundlePermission p25 = new BundlePermission("*", "host"); 

		assertImplies(p21, p21);
		assertImplies(p22, p21);
		assertImplies(p23, p21);
		assertNotImplies(p24, p21);
		assertImplies(p25, p21);

		assertImplies(p22, p22);
		assertImplies(p23, p22);
		assertNotImplies(p24, p22);
		assertImplies(p25, p22);

		assertImplies(p23, p23);
		assertNotImplies(p24, p23);
		assertImplies(p25, p23);

		assertNotImplies(p24, p25);
		assertImplies(p25, p25);

		assertImplies(p25, p24);

		assertNotImplies(p21, p22);
		assertNotImplies(p21, p23);
		assertNotImplies(p21, p24);
		assertNotImplies(p21, p25);

		assertNotImplies(p22, p23);
		assertNotImplies(p22, p24);
		assertNotImplies(p22, p25);

		assertNotImplies(p23, p24);
		assertNotImplies(p23, p25);

		PermissionCollection pc = p21.newPermissionCollection();

		assertAddPermission(pc, p21);
		assertImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertNotImplies(pc, p25);

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertNotImplies(pc, p25);

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertNotImplies(pc, p25);

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertNotImplies(pc, p25);

		assertAddPermission(pc, p25);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p25);
		assertImplies(pc, p24);
		assertSerializable(pc);

		pc = p22.newPermissionCollection();

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertNotImplies(pc, p25);
		assertSerializable(pc);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertNotImplies(pc, p25);
		assertSerializable(pc);

		pc = p24.newPermissionCollection();

		assertAddPermission(pc, p24);
		assertNotImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertImplies(pc, p24);
		assertNotImplies(pc, p25);
		assertSerializable(pc);

		pc = p25.newPermissionCollection();
		
		assertAddPermission(pc, p25);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertImplies(pc, p25);

		assertSerializable(p21);
		assertSerializable(p22);
		assertSerializable(p23);
		assertSerializable(p24);
		assertSerializable(p25);
		assertSerializable(pc);
	}
	
	public void testActionImplications() {
		BundlePermission provide = new BundlePermission("*", "provide");
		BundlePermission require = new BundlePermission("*", "require");
		BundlePermission host = new BundlePermission("*", "host");
		BundlePermission fragment = new BundlePermission("*", "fragment");

		assertImplies(provide, provide);
		assertImplies(provide, require);
		assertNotImplies(provide, host);
		assertNotImplies(provide, fragment);
		
		assertNotImplies(require, provide);
		assertImplies(require, require);
		assertNotImplies(require, host);
		assertNotImplies(require, fragment);

		assertNotImplies(host, provide);
		assertNotImplies(host, require);
		assertImplies(host, host);
		assertNotImplies(host, fragment);

		assertNotImplies(fragment, provide);
		assertNotImplies(fragment, require);
		assertNotImplies(fragment, host);
		assertImplies(fragment, fragment);
	}
	
	private static void invalidBundlePermission(String name, String actions) {
		try {
			BundlePermission p = new BundlePermission(name, actions);
			fail(p + " created with invalid actions"); 
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
