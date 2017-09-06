/*
 * Copyright (c) OSGi Alliance (2009, 2017). All Rights Reserved.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

import org.osgi.framework.Bundle;
import org.osgi.framework.CapabilityPermission;
import org.osgi.test.support.PermissionTestCase;

public class CapabilityPermissionTests extends PermissionTestCase {

	public void testInvalid() {
		invalidCapabilityPermission("a.b.c", "x");
		invalidCapabilityPermission("a.b.c", "   require  ,  x   ");
		invalidCapabilityPermission("a.b.c", "");
		invalidCapabilityPermission("a.b.c", "      ");
		invalidCapabilityPermission("a.b.c", null);
		invalidCapabilityPermission("a.b.c", ",");
		invalidCapabilityPermission("a.b.c", ",xxx");
		invalidCapabilityPermission("a.b.c", "xxx,");
		invalidCapabilityPermission("a.b.c", "require,");
		invalidCapabilityPermission("a.b.c", "provide,   ");
		invalidCapabilityPermission("a.b.c", "requireme,");
		invalidCapabilityPermission("a.b.c", "provideme,");
		invalidCapabilityPermission("a.b.c", ",require");
		invalidCapabilityPermission("a.b.c", ",provide");
		invalidCapabilityPermission("a.b.c", "   requireme   ");
		invalidCapabilityPermission("a.b.c", "   provideme     ");
		invalidCapabilityPermission("a.b.c", "   requir");
		invalidCapabilityPermission("a.b.c", "   provid");
		invalidCapabilityPermission("()", "require");
		invalidCapabilityPermission("(capability.namespace=a.b.c)",
				"provide,REQUIRE");
		invalidCapabilityPermission("(capability.namespace=a.b.c)", "provide");
		invalidCapabilityPermission("(capability.namespace=a.b.c)",
				"requiRE,provide");

		invalidCapabilityPermission("(capability.namespace=a.b.c)", null, null,
				"require");
		invalidCapabilityPermission("a.b.c", null, null, "require");
		invalidCapabilityPermission("(capability.namespace=a.b.c)",
				Collections.<String, Object> emptyMap(), null, "require");
		invalidCapabilityPermission("a.b.c",
				Collections.<String, Object> emptyMap(), null,
				"require");
		invalidCapabilityPermission("a.b.c",
				Collections.<String, Object> emptyMap(),
				newMockBundle(2, "test.bsn", "test.location", null), "provide");
		invalidCapabilityPermission("a.b.c",
				Collections.<String, Object> emptyMap(),
				newMockBundle(2, "test.bsn", "test.location", null),
				"require, provide");
	}

	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read");

		CapabilityPermission p11 = new CapabilityPermission(
				"com.foo.capability1",
				"    REQUIRE,provide   ");
		CapabilityPermission p12 = new CapabilityPermission(
				"com.foo.capability1",
				"PROVIDE  ,   require");
		CapabilityPermission p13 = new CapabilityPermission(
				"com.foo.capability1",
				"provIDE   ");
		CapabilityPermission p14 = new CapabilityPermission(
				"com.foo.capability1",
				"    Require    ");

		assertEquals("require,provide", p11.getActions());
		assertEquals("require,provide", p12.getActions());
		assertEquals("provide", p13.getActions());
		assertEquals("require", p14.getActions());

		assertImplies(p11, p11);
		assertImplies(p11, p12);
		assertImplies(p11, p13);
		assertImplies(p11, p14);

		assertImplies(p14, p14);

		assertNotImplies(p14, p11);
		assertNotImplies(p14, p12);
		assertNotImplies(p14, p13);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertEquals(p12, p11);

		assertNotEquals(p11, p13);
		assertNotEquals(p11, p14);

		assertNotEquals(p13, p14);

		assertNotEquals(p14, p11);

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
		assertSerializable(pc);

		pc = p13.newPermissionCollection();
		assertAddPermission(pc, p11);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

		assertNotAddPermission(pc, op);
		assertSerializable(pc);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
		assertSerializable(pc);
	}

	public void testNames() {
		CapabilityPermission p21 = new CapabilityPermission(
				"com.foo.capability2",
				"require");
		CapabilityPermission p22 = new CapabilityPermission("com.foo.*",
				"require");
		CapabilityPermission p23 = new CapabilityPermission("com.*", "require");
		CapabilityPermission p24 = new CapabilityPermission("*", "require");

		assertImplies(p21, p21);
		assertImplies(p22, p21);
		assertImplies(p23, p21);
		assertImplies(p24, p21);

		assertImplies(p22, p22);
		assertImplies(p23, p22);
		assertImplies(p24, p22);

		assertImplies(p23, p23);
		assertImplies(p24, p23);

		assertImplies(p24, p24);

		assertNotImplies(p21, p22);
		assertNotImplies(p21, p23);
		assertNotImplies(p21, p24);

		assertNotImplies(p22, p23);
		assertNotImplies(p22, p24);

		assertNotImplies(p23, p24);

		PermissionCollection pc = p21.newPermissionCollection();

		assertAddPermission(pc, p21);
		assertImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertSerializable(pc);

		pc = p22.newPermissionCollection();

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertSerializable(pc);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertSerializable(pc);

		pc = p24.newPermissionCollection();

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		assertSerializable(p21);
		assertSerializable(p22);
		assertSerializable(p23);
		assertSerializable(p24);
		assertSerializable(pc);
	}

	public void testActionImplications() {
		CapabilityPermission provide = new CapabilityPermission("*", "provide");
		CapabilityPermission require = new CapabilityPermission("*", "require");

		assertEquals("provide", provide.getActions());
		assertEquals("require", require.getActions());

		assertImplies(require, require);
		assertImplies(provide, provide);

		assertNotImplies(require, provide);

		assertNotImplies(provide, require);

		CapabilityPermission comfooexport = new CapabilityPermission(
				"com.foo.*", "provide");
		CapabilityPermission both = new CapabilityPermission("com.foo.bar",
				"require,provide");
		PermissionCollection pc = require.newPermissionCollection();
		assertAddPermission(pc, require);
		assertAddPermission(pc, comfooexport);
		assertImplies(pc, both);
	}

	public void testFiltersName() {
		CapabilityPermission p31 = new CapabilityPermission(
				"  (capability.namespace  =com.foo.capability2)", "require");
		CapabilityPermission p32 = new CapabilityPermission(
				"(capability.namespace=com.foo.*)", "require");
		CapabilityPermission p33 = new CapabilityPermission(
				"(capability.namespace=com.*)", "require");
		CapabilityPermission p34 = new CapabilityPermission(
				"(capability.namespace=*)",
				"require");
		CapabilityPermission p35 = new CapabilityPermission(
				"com.foo.capability2",
				"require");
		CapabilityPermission p36 = new CapabilityPermission("com.foo.*",
				"require");
		CapabilityPermission p37 = new CapabilityPermission("com.*", "require");
		CapabilityPermission p38 = new CapabilityPermission("*", "require");

		assertImplies(p31, p35);
		assertImplies(p32, p35);
		assertImplies(p33, p35);
		assertImplies(p34, p35);
		assertImplies(p36, p35);
		assertImplies(p37, p35);
		assertImplies(p38, p35);

		assertNotImplies(p31, p31);
		assertNotImplies(p31, p32);
		assertNotImplies(p31, p33);
		assertNotImplies(p31, p34);

		assertNotImplies(p31, p36);
		assertImplies(p32, p36);
		assertImplies(p33, p36);
		assertImplies(p34, p36);
		assertNotImplies(p35, p36);
		assertImplies(p36, p36);
		assertImplies(p37, p36);
		assertImplies(p38, p36);

		assertNotImplies(p31, p37);
		assertNotImplies(p32, p37);
		assertImplies(p33, p37);
		assertImplies(p34, p37);
		assertNotImplies(p35, p37);
		assertNotImplies(p36, p37);
		assertImplies(p37, p37);
		assertImplies(p38, p37);

		assertNotImplies(p31, p38);
		assertNotImplies(p32, p38);
		assertNotImplies(p33, p38);
		assertImplies(p34, p38);
		assertNotImplies(p35, p38);
		assertNotImplies(p36, p38);
		assertNotImplies(p37, p38);
		assertImplies(p38, p38);

		PermissionCollection pc = p31.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p31);

		assertNotImplies(pc, p31);
		assertNotImplies(pc, p32);
		assertNotImplies(pc, p33);
		assertNotImplies(pc, p34);

		assertImplies(pc, p35);
		assertNotImplies(pc, p36);
		assertNotImplies(pc, p37);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertNotImplies(pc, p37);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertImplies(pc, p38);

		checkEnumeration(pc.elements(), false);
		assertSerializable(pc);

		pc = p32.newPermissionCollection();
		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertNotImplies(pc, p37);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p33.newPermissionCollection();
		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p34.newPermissionCollection();
		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertImplies(pc, p36);
		assertImplies(pc, p37);
		assertImplies(pc, p38);

		assertSerializable(p31);
		assertSerializable(p32);
		assertSerializable(p33);
		assertSerializable(p34);
		assertSerializable(p35);
		assertSerializable(p36);
		assertSerializable(pc);
	}

	public void testFiltersAttributesBundle() {
		CapabilityPermission p41 = new CapabilityPermission("(id=2)", "require");
		CapabilityPermission p42 = new CapabilityPermission(
				"(location=test.*)", "require");
		CapabilityPermission p43 = new CapabilityPermission("(name=test.*)",
				"require");
		CapabilityPermission p44 = new CapabilityPermission(
				"(signer=\\*, o=ACME, c=US)", "require");
		CapabilityPermission p45 = new CapabilityPermission(
				"(capability.namespace=com.foo.*)", "require");
		CapabilityPermission p46 = new CapabilityPermission("(attr1=foo*)",
				"require");

		Map<String,Object> attributes = new HashMap<>();
		attributes.put("attr1", "foo1");
		CapabilityPermission p47 = new CapabilityPermission(
				"com.foo.capability2", attributes,
				newMockBundle(2, "test.bsn", "test.location",
						"cn=Bugs Bunny, o=ACME, c=US"), "require");

		attributes.put("attr1", "bar1");
		CapabilityPermission p48 = new CapabilityPermission(
				"com.bar.capability2", attributes,
				newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "require");

		assertImplies(p41, p47);
		assertImplies(p42, p47);
		assertImplies(p43, p47);
		assertImplies(p44, p47);
		assertImplies(p45, p47);
		assertImplies(p46, p47);

		assertNotImplies(p41, p48);
		assertNotImplies(p42, p48);
		assertNotImplies(p43, p48);
		assertNotImplies(p44, p48);
		assertNotImplies(p45, p48);
		assertNotImplies(p46, p48);

		assertNotImplies(p41, p41);
		assertNotImplies(p41, p42);
		assertNotImplies(p41, p43);
		assertNotImplies(p41, p44);
		assertNotImplies(p41, p45);
		assertNotImplies(p41, p46);

		PermissionCollection pc = p41.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p44);
		assertImplies(pc, p47);
		assertNotImplies(pc, p48);

		assertNotImplies(pc, p41);
		assertNotImplies(pc, p42);
		assertNotImplies(pc, p43);
		assertNotImplies(pc, p44);
		assertNotImplies(pc, p45);
		assertNotImplies(pc, p46);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p46);
		assertImplies(pc, p47);
		assertNotImplies(pc, p48);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p45);
		assertImplies(pc, p47);
		assertNotImplies(pc, p48);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p43);
		assertImplies(pc, p47);
		assertNotImplies(pc, p48);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p42);
		assertImplies(pc, p47);
		assertNotImplies(pc, p48);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p41);
		assertImplies(pc, p47);
		assertNotImplies(pc, p48);
		checkEnumeration(pc.elements(), false);

		// throw IllegalArgumentException If specified permission to be
		// added was constructed with bundles.
		assertNotAddPermission(pc, p47);
		assertNotAddPermission(pc, p48);

		assertSerializable(p41);
		assertSerializable(p42);
		assertSerializable(p43);
		assertSerializable(p44);
		assertSerializable(p45);
		assertSerializable(p46);
		assertNotSerializable(p47);
		assertNotSerializable(p48);
		assertSerializable(pc);
	}

	public void testPermissionCollection() {
		CapabilityPermission p51 = new CapabilityPermission(
				"  (capability.namespace  =com.foo.capability2)", "require");
		CapabilityPermission p52 = new CapabilityPermission(
				"(capability.namespace=com.foo.*)", "require");
		CapabilityPermission p53 = new CapabilityPermission(
				"(capability.namespace=com.*)", "require");
		CapabilityPermission p54 = new CapabilityPermission(
				"(capability.namespace=*)",
				"require");
		CapabilityPermission p55 = new CapabilityPermission(
				"com.foo.capability2",
				"require");

		CapabilityPermission p56 = new CapabilityPermission(
				"com.bar.capability2",
				"require");
		CapabilityPermission p57 = new CapabilityPermission("com.bar.*",
				"require");
		CapabilityPermission p58 = new CapabilityPermission("com.*", "require");
		CapabilityPermission p59 = new CapabilityPermission("*", "require");

		CapabilityPermission p5a = new CapabilityPermission(
				"com.foo.capability2", Collections.<String, Object> emptyMap(),
				newMockBundle(2, "test.bsn", "test.location",
						"cn=Bugs Bunny, o=ACME, c=US"), "require");
		CapabilityPermission p5b = new CapabilityPermission(
				"com.bar.capability2", Collections.<String, Object> emptyMap(),
				newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "require");

		PermissionCollection pc;
		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertImplies(p51, p5a);
		assertImplies(pc, p5a);
		assertImplies(p51, p55);
		assertImplies(pc, p55);
		assertNotImplies(p51, p5b);
		assertNotImplies(pc, p5b);
		assertNotImplies(p51, p56);
		assertNotImplies(pc, p56);
		assertSerializable(pc);

		pc = p56.newPermissionCollection();
		assertAddPermission(pc, p56);
		assertNotImplies(p56, p5a);
		assertNotImplies(pc, p5a);
		assertNotImplies(p56, p55);
		assertNotImplies(pc, p55);
		assertImplies(p56, p5b);
		assertImplies(pc, p5b);
		assertImplies(p56, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertAddPermission(pc, p56);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p52.newPermissionCollection();
		assertAddPermission(pc, p52);
		assertAddPermission(pc, p57);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p52.newPermissionCollection();
		assertAddPermission(pc, p52);
		assertImplies(p52, p5a);
		assertImplies(pc, p5a);
		assertImplies(p52, p55);
		assertImplies(pc, p55);
		assertNotImplies(p52, p5b);
		assertNotImplies(pc, p5b);
		assertNotImplies(p52, p56);
		assertNotImplies(pc, p56);
		assertSerializable(pc);

		pc = p57.newPermissionCollection();
		assertAddPermission(pc, p57);
		assertNotImplies(p57, p5a);
		assertNotImplies(pc, p5a);
		assertNotImplies(p57, p55);
		assertNotImplies(pc, p55);
		assertImplies(p57, p5b);
		assertImplies(pc, p5b);
		assertImplies(p57, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p53.newPermissionCollection();
		assertAddPermission(pc, p53);
		assertAddPermission(pc, p58);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p53.newPermissionCollection();
		assertAddPermission(pc, p53);
		assertImplies(p53, p5a);
		assertImplies(pc, p5a);
		assertImplies(p53, p55);
		assertImplies(pc, p55);
		assertImplies(p53, p5b);
		assertImplies(pc, p5b);
		assertImplies(p53, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p58.newPermissionCollection();
		assertAddPermission(pc, p58);
		assertImplies(p58, p5a);
		assertImplies(pc, p5a);
		assertImplies(p58, p55);
		assertImplies(pc, p55);
		assertImplies(p58, p5b);
		assertImplies(pc, p5b);
		assertImplies(p58, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertAddPermission(pc, p59);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertImplies(p54, p5a);
		assertImplies(pc, p5a);
		assertImplies(p54, p55);
		assertImplies(pc, p55);
		assertImplies(p54, p5b);
		assertImplies(pc, p5b);
		assertImplies(p54, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p59.newPermissionCollection();
		assertAddPermission(pc, p59);
		assertImplies(p59, p5a);
		assertImplies(pc, p5a);
		assertImplies(p59, p55);
		assertImplies(pc, p55);
		assertImplies(p59, p5b);
		assertImplies(pc, p5b);
		assertImplies(p59, p56);
		assertImplies(pc, p56);
		assertSerializable(pc);
	}

	public void testAttributes() {
		CapabilityPermission p61 = new CapabilityPermission(
				"(&(@@at=atat)(@name=expected)(name=test.bsn)(@capability.namespace=expected)(capability.namespace=com.foo.capability2))",
				"require");
		Bundle bundle = newMockBundle(2, "test.bsn", "test.location",
				"cn=Bugs Bunny, o=ACME, c=US");
		Map<String,Object> attributes = new HashMap<>();
		attributes.put("name", "expected");
		attributes.put("capability.namespace", "expected");
		attributes.put("@at", "atat");
		CapabilityPermission p62 = new CapabilityPermission(
				"com.foo.capability2", attributes, bundle, "require");
		assertImplies(p61, p62);
	}

	private static void invalidCapabilityPermission(String name, String actions) {
		try {
			CapabilityPermission p = new CapabilityPermission(name, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private static void invalidCapabilityPermission(String name,
			Map<String,Object> attributes, Bundle bundle, String actions) {
		try {
			CapabilityPermission p = new CapabilityPermission(name, attributes,
					bundle, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
