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

package org.osgi.test.cases.framework.junit.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.framework.AdaptPermission;
import org.osgi.framework.Bundle;
import org.osgi.test.support.PermissionTestCase;

/**
 */
public class AdaptPermissionTests extends PermissionTestCase {

	/**
	 * 
	 */
	public void testInvalid() {
		invalidAdaptPermission("(a=b)", "x");
		invalidAdaptPermission("(a=b)", "   adapt  ,  x   ");
		invalidAdaptPermission("(a=b)", "");
		invalidAdaptPermission("(a=b)", "      ");
		invalidAdaptPermission("(a=b)", null);
		invalidAdaptPermission("(a=b)", ",");
		invalidAdaptPermission("(a=b)", ",xxx");
		invalidAdaptPermission("(a=b)", "xxx,");
		invalidAdaptPermission("(a=b)", "adapt,");
		invalidAdaptPermission("(a=b)", "adaptme,");
		invalidAdaptPermission("(a=b)", ",adapt");
		invalidAdaptPermission("(a=b)", "   adaptme   ");
		invalidAdaptPermission("(a=b)", "   adap");
		invalidAdaptPermission("a.b.c", "adapt");
		invalidAdaptPermission("()", "adapt");
		invalidAdaptPermission(null, "adapt");

		invalidAdaptPermission("(adaptClass=a.b.c)", null, "adapt");
		invalidAdaptPermission(null,
				newMockBundle(2, "test.bsn", "test.location", null), "adapt");
	}

	/**
	 * 
	 */
	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read");
		Bundle b1 = newMockBundle(2, "test.bsn", "test.location", null);

		AdaptPermission p11 = new AdaptPermission(
				"(adaptClass=com.foo.coord1)", "    adapt   ");
		AdaptPermission p12 = new AdaptPermission(
				"(adaptClass=com.foo.coord1)", "ADAPT   ");

		AdaptPermission p21 = new AdaptPermission("com.foo.coord1", b1,
				"    adapt   ");
		AdaptPermission p22 = new AdaptPermission("com.foo.coord1", b1,
				"adAPT   ");

		assertEquals("adapt", p11.getActions());
		assertEquals("adapt", p21.getActions());
		assertEquals("adapt", p12.getActions());
		assertEquals("adapt", p22.getActions());

		assertImplies(p11, p21);
		assertImplies(p11, p22);

		assertImplies(p12, p21);
		assertImplies(p12, p22);



		assertNotImplies(p11, p11);
		assertNotImplies(p11, p12);
		assertNotImplies(p12, p11);
		assertNotImplies(p12, p12);

		assertNotImplies(p21, p11);
		assertNotImplies(p21, p12);
		assertNotImplies(p21, p21);
		assertNotImplies(p21, p22);

		assertNotImplies(p22, p11);
		assertNotImplies(p22, p12);
		assertNotImplies(p22, p21);
		assertNotImplies(p22, p22);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);

		assertEquals(p12, p11);
		assertEquals(p12, p12);

		assertEquals(p21, p21);
		assertEquals(p21, p22);

		assertEquals(p22, p21);
		assertEquals(p22, p22);

		PermissionCollection pc = p11.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p22);

		assertAddPermission(pc, p11);
		assertImplies(pc, p21);
		assertImplies(pc, p22);

		assertSerializable(pc);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p11);
		assertSerializable(p12);
		assertNotSerializable(p21);
		assertNotSerializable(p22);
		assertSerializable(pc);
	}

	/**
	 * 
	 */
	public void testWildcard() {
		Bundle b1 = newMockBundle(2, "test.bsn", "test.location", null);
		AdaptPermission p21 = new AdaptPermission("(adaptClass=com.foo.*)",
				"adapt");
		AdaptPermission p22 = new AdaptPermission("com.foo.coord2", b1, "adapt");
		AdaptPermission p23 = new AdaptPermission("*", "adapt");
		AdaptPermission p24 = new AdaptPermission("com.bar.coord2", b1, "adapt");

		assertImplies(p21, p22);
		assertNotImplies(p21, p23);
		assertNotImplies(p21, p24);
		assertImplies(p23, p22);
		assertImplies(p23, p24);
		assertImplies(p23, p23);

		PermissionCollection pc = p21.newPermissionCollection();

		assertAddPermission(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p23);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		assertSerializable(pc);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertSerializable(pc);

		assertSerializable(p21);
		assertNotSerializable(p22);
		assertSerializable(p23);
		assertNotSerializable(p24);
	}

	/**
	 * 
	 */
	public void testActionImplications() {
		AdaptPermission adapt = new AdaptPermission("*", "adapt");

		assertEquals("adapt", adapt.getActions());
		adapt = new AdaptPermission(adapt.getName(), adapt.getActions());
		assertEquals("adapt", adapt.getActions());

		PermissionCollection adaptpc = adapt.newPermissionCollection();
		adaptpc.add(adapt);

		assertImplies(adapt, adapt);
		assertImplies(adaptpc, adapt);
	}

	/**
	 * 
	 */
	public void testFiltersName() {
		Bundle b1 = newMockBundle(2, "test.bsn", "test.location", null);
		AdaptPermission p31 = new AdaptPermission(
				"  (adaptClass  =com.foo.coord3)", "adapt");
		AdaptPermission p32 = new AdaptPermission("(adaptClass=com.foo.*)",
				"adapt");
		AdaptPermission p33 = new AdaptPermission("(adaptClass=com.*)", "adapt");
		AdaptPermission p34 = new AdaptPermission("(adaptClass=*)", "adapt");
		AdaptPermission p35 = new AdaptPermission("com.foo.coord3", b1, "adapt");
		AdaptPermission p38 = new AdaptPermission("*", "adapt");

		assertImplies(p31, p35);
		assertImplies(p32, p35);
		assertImplies(p33, p35);
		assertImplies(p34, p35);
		assertImplies(p38, p35);

		assertNotImplies(p31, p31);
		assertNotImplies(p31, p32);
		assertNotImplies(p31, p33);
		assertNotImplies(p31, p34);

		assertNotImplies(p31, p38);
		assertNotImplies(p32, p38);
		assertNotImplies(p33, p38);
		assertImplies(p34, p38);
		assertNotImplies(p35, p38);
		assertImplies(p38, p38);

		PermissionCollection pc = p31.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p31);

		assertNotImplies(pc, p31);
		assertNotImplies(pc, p32);
		assertNotImplies(pc, p33);
		assertNotImplies(pc, p34);

		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);

		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertImplies(pc, p38);

		checkEnumeration(pc.elements(), false);
		assertSerializable(pc);

		pc = p32.newPermissionCollection();
		assertAddPermission(pc, p32);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p33.newPermissionCollection();
		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		assertNotImplies(pc, p38);
		assertSerializable(pc);

		pc = p34.newPermissionCollection();
		assertAddPermission(pc, p34);
		assertImplies(pc, p35);
		assertImplies(pc, p38);

		assertSerializable(p31);
		assertSerializable(p32);
		assertSerializable(p33);
		assertSerializable(p34);
		assertNotSerializable(p35);
		assertSerializable(pc);
	}

	/**
	 * 
	 */
	public void testFiltersBundle() {
		AdaptPermission p41 = new AdaptPermission("(id=2)", "adapt");
		AdaptPermission p42 = new AdaptPermission("(location=test.*)", "adapt");
		AdaptPermission p43 = new AdaptPermission("(name=test.*)", "adapt");
		AdaptPermission p44 = new AdaptPermission("(signer=\\*, o=ACME, c=US)",
				"adapt");
		AdaptPermission p45 = new AdaptPermission("(adaptClass=com.foo.*)",
				"adapt");

		AdaptPermission p46 = new AdaptPermission("com.foo.coord4",
				newMockBundle(2, "test.bsn", "test.location",
						"cn=Bugs Bunny, o=ACME, c=US"), "adapt");
		AdaptPermission p47 = new AdaptPermission("com.bar.coord4",
				newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "adapt");

		assertImplies(p41, p46);
		assertImplies(p42, p46);
		assertImplies(p43, p46);
		assertImplies(p44, p46);
		assertImplies(p45, p46);

		assertNotImplies(p41, p47);
		assertNotImplies(p42, p47);
		assertNotImplies(p43, p47);
		assertNotImplies(p44, p47);
		assertNotImplies(p45, p47);

		assertNotImplies(p41, p41);
		assertNotImplies(p41, p42);
		assertNotImplies(p41, p43);
		assertNotImplies(p41, p44);
		assertNotImplies(p41, p45);

		PermissionCollection pc = p41.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p44);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);

		assertNotImplies(pc, p41);
		assertNotImplies(pc, p42);
		assertNotImplies(pc, p43);
		assertNotImplies(pc, p44);
		assertNotImplies(pc, p45);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p43);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p42);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);
		assertSerializable(pc);

		pc = p41.newPermissionCollection();
		assertAddPermission(pc, p41);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);
		checkEnumeration(pc.elements(), false);

		// throw IllegalArgumentException If specified permission to be
		// added was constructed with bundles.
		assertNotAddPermission(pc, p46);
		assertNotAddPermission(pc, p47);

		assertSerializable(p41);
		assertSerializable(p42);
		assertSerializable(p43);
		assertSerializable(p44);
		assertSerializable(p45);
		assertNotSerializable(p46);
		assertNotSerializable(p47);
		assertSerializable(pc);
	}

	/**
	 * 
	 */
	public void testPermissionCollection() {
		AdaptPermission p51 = new AdaptPermission(
				"  (adaptClass  =com.foo.coord5)", "adapt");
		AdaptPermission p52 = new AdaptPermission("(adaptClass=com.foo.*)",
				"adapt");
		AdaptPermission p53 = new AdaptPermission("(adaptClass=com.*)", "adapt");
		AdaptPermission p54 = new AdaptPermission("(adaptClass=*)", "adapt");
		AdaptPermission p59 = new AdaptPermission("*", "adapt");

		AdaptPermission p5a = new AdaptPermission("com.foo.coord5",
				newMockBundle(2, "test.bsn", "test.location",
						"cn=Bugs Bunny, o=ACME, c=US"), "adapt");
		AdaptPermission p5b = new AdaptPermission("com.bar.coord5",
				newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "adapt");

		PermissionCollection pc;
		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertImplies(p51, p5a);
		assertImplies(pc, p5a);
		assertNotImplies(p51, p5b);
		assertNotImplies(pc, p5b);
		assertSerializable(pc);

		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertImplies(pc, p5a);
		assertNotImplies(pc, p5b);
		assertSerializable(pc);

		pc = p52.newPermissionCollection();
		assertAddPermission(pc, p52);
		assertImplies(p52, p5a);
		assertImplies(pc, p5a);
		assertNotImplies(p52, p5b);
		assertNotImplies(pc, p5b);
		assertSerializable(pc);

		pc = p53.newPermissionCollection();
		assertAddPermission(pc, p53);
		assertImplies(p53, p5a);
		assertImplies(pc, p5a);
		assertImplies(p53, p5b);
		assertImplies(pc, p5b);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertAddPermission(pc, p59);
		assertImplies(pc, p5a);
		assertImplies(pc, p5b);
		assertSerializable(pc);

		pc = p54.newPermissionCollection();
		assertAddPermission(pc, p54);
		assertImplies(p54, p5a);
		assertImplies(pc, p5a);
		assertImplies(p54, p5b);
		assertImplies(pc, p5b);
		assertSerializable(pc);

		pc = p59.newPermissionCollection();
		assertAddPermission(pc, p59);
		assertImplies(p59, p5a);
		assertImplies(pc, p5a);
		assertImplies(p59, p5b);
		assertImplies(pc, p5b);
		assertSerializable(pc);
	}

	private static void invalidAdaptPermission(String name, String actions) {
		try {
			AdaptPermission p = new AdaptPermission(name, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	private static void invalidAdaptPermission(String name, Bundle bundle,
			String actions) {
		try {
			AdaptPermission p = new AdaptPermission(name, bundle, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		catch (NullPointerException e) {
			// expected
		}
	}
}
