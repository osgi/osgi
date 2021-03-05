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

package org.osgi.test.cases.coordinator.junit;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.framework.Bundle;
import org.osgi.service.coordinator.CoordinationPermission;
import org.osgi.test.support.PermissionTestCase;

/**
 */
public class CoordinationPermissionTests extends PermissionTestCase {

	/**
	 * 
	 */
	public void testInvalid() {
		invalidCoordinationPermission("(a=b)", "x");
		invalidCoordinationPermission("(a=b)", "   initiate  ,  x   ");
		invalidCoordinationPermission("(a=b)", "");
		invalidCoordinationPermission("(a=b)", "      ");
		invalidCoordinationPermission("(a=b)", null);
		invalidCoordinationPermission("(a=b)", ",");
		invalidCoordinationPermission("(a=b)", ",xxx");
		invalidCoordinationPermission("(a=b)", "xxx,");
		invalidCoordinationPermission("(a=b)", "initiate,");
		invalidCoordinationPermission("(a=b)", "admin,   ");
		invalidCoordinationPermission("(a=b)", "participate,   ");
		invalidCoordinationPermission("(a=b)", "initiateme,");
		invalidCoordinationPermission("(a=b)", "adminme,");
		invalidCoordinationPermission("(a=b)", "participateme,");
		invalidCoordinationPermission("(a=b)", ",initiate");
		invalidCoordinationPermission("(a=b)", ",admin");
		invalidCoordinationPermission("(a=b)", ",participate");
		invalidCoordinationPermission("(a=b)", "   initiateme   ");
		invalidCoordinationPermission("(a=b)", "   adminme     ");
		invalidCoordinationPermission("(a=b)", "   participateme     ");
		invalidCoordinationPermission("(a=b)", "   initiat");
		invalidCoordinationPermission("(a=b)", "   admi");
		invalidCoordinationPermission("(a=b)", "   participat");
		invalidCoordinationPermission("a.b.c", "initiate");
		invalidCoordinationPermission("()", "initiate");
		invalidCoordinationPermission(null, "initiate");

		invalidCoordinationPermission("(coordination.name=a.b.c)", null,
				"initiate");
		invalidCoordinationPermission(null,
				newMockBundle(2, "test.bsn", "test.location", null), "admin");
	}

	/**
	 * 
	 */
	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read");
		Bundle b1 = newMockBundle(2, "test.bsn", "test.location", null);

		CoordinationPermission p11 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)",
				"    INITIATE,participate   ");
		CoordinationPermission p12 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)",
				"PARTICIPATE  ,   initiate");
		CoordinationPermission p13 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)", "ADMIN   ");
		CoordinationPermission p14 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)", "    Initiate    ");
		CoordinationPermission p15 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)", "parTICIpate   ");
		CoordinationPermission p16 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)", "parTICIpate,admin");
		CoordinationPermission p17 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)", "iniTIAte, ADmin");
		CoordinationPermission p18 = new CoordinationPermission(
				"(coordination.name=com.foo.coord1)",
				"partICipate, initiATe, aDMIn");

		CoordinationPermission p21 = new CoordinationPermission(
				"com.foo.coord1", b1, "    INITIATE,participate   ");
		CoordinationPermission p23 = new CoordinationPermission(
				"com.foo.coord1", b1, "adMIN   ");
		CoordinationPermission p24 = new CoordinationPermission(
				"com.foo.coord1", b1, "    Initiate    ");
		CoordinationPermission p25 = new CoordinationPermission(
				"com.foo.coord1", b1, "parTICIpate   ");
		CoordinationPermission p26 = new CoordinationPermission(
				"com.foo.coord1", b1, "parTICIpate,admin");
		CoordinationPermission p27 = new CoordinationPermission(
				"com.foo.coord1", b1, "iniTIAte, ADmin");
		CoordinationPermission p28 = new CoordinationPermission(
				"com.foo.coord1", b1, "partICipate, initiATe, aDMIn");

		assertEquals("initiate,participate", p11.getActions());
		assertEquals("initiate,participate", p21.getActions());
		assertEquals("initiate,participate", p12.getActions());
		assertEquals("admin", p13.getActions());
		assertEquals("admin", p23.getActions());
		assertEquals("initiate", p14.getActions());
		assertEquals("initiate", p24.getActions());
		assertEquals("participate", p15.getActions());
		assertEquals("participate", p25.getActions());
		assertEquals("admin,participate", p16.getActions());
		assertEquals("admin,participate", p26.getActions());
		assertEquals("admin,initiate", p17.getActions());
		assertEquals("admin,initiate", p27.getActions());
		assertEquals("admin,initiate,participate", p18.getActions());
		assertEquals("admin,initiate,participate", p28.getActions());

		assertImplies(p11, p21);
		assertNotImplies(p11, p23);
		assertImplies(p11, p24);
		assertImplies(p11, p25);
		assertNotImplies(p11, p26);
		assertNotImplies(p11, p27);
		assertNotImplies(p11, p28);

		assertImplies(p12, p21);
		assertNotImplies(p12, p23);
		assertImplies(p12, p24);
		assertImplies(p12, p25);
		assertNotImplies(p12, p26);
		assertNotImplies(p12, p27);
		assertNotImplies(p12, p28);

		assertNotImplies(p13, p21);
		assertImplies(p13, p23);
		assertNotImplies(p13, p24);
		assertNotImplies(p13, p25);
		assertNotImplies(p13, p26);
		assertNotImplies(p13, p27);
		assertNotImplies(p13, p28);

		assertNotImplies(p14, p21);
		assertNotImplies(p14, p23);
		assertImplies(p14, p24);
		assertNotImplies(p14, p25);
		assertNotImplies(p14, p26);
		assertNotImplies(p14, p27);
		assertNotImplies(p14, p28);

		assertNotImplies(p15, p21);
		assertNotImplies(p15, p23);
		assertNotImplies(p15, p24);
		assertImplies(p15, p25);
		assertNotImplies(p15, p26);
		assertNotImplies(p15, p27);
		assertNotImplies(p15, p28);

		assertNotImplies(p16, p21);
		assertImplies(p16, p23);
		assertNotImplies(p16, p24);
		assertImplies(p16, p25);
		assertImplies(p16, p26);
		assertNotImplies(p16, p27);
		assertNotImplies(p16, p28);

		assertNotImplies(p17, p21);
		assertImplies(p17, p23);
		assertImplies(p17, p24);
		assertNotImplies(p17, p25);
		assertNotImplies(p17, p26);
		assertImplies(p17, p27);

		assertImplies(p18, p21);
		assertImplies(p18, p23);
		assertImplies(p18, p24);
		assertImplies(p18, p25);
		assertImplies(p18, p26);
		assertImplies(p18, p27);
		assertImplies(p18, p28);

		assertNotImplies(p11, p11);
		assertNotImplies(p12, p12);
		assertNotImplies(p13, p13);
		assertNotImplies(p14, p14);
		assertNotImplies(p15, p15);
		assertNotImplies(p16, p16);
		assertNotImplies(p17, p17);
		assertNotImplies(p18, p18);

		assertNotImplies(p21, p11);
		assertNotImplies(p23, p13);
		assertNotImplies(p24, p14);
		assertNotImplies(p25, p15);
		assertNotImplies(p26, p16);
		assertNotImplies(p27, p17);
		assertNotImplies(p28, p18);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertNotEquals(p11, p13);
		assertNotEquals(p11, p14);
		assertNotEquals(p11, p15);
		assertNotEquals(p11, p16);
		assertNotEquals(p11, p17);
		assertNotEquals(p11, p18);

		assertEquals(p12, p11);
		assertEquals(p12, p12);
		assertNotEquals(p12, p13);
		assertNotEquals(p12, p14);
		assertNotEquals(p12, p15);
		assertNotEquals(p12, p16);
		assertNotEquals(p12, p17);
		assertNotEquals(p12, p18);

		assertNotEquals(p13, p11);
		assertNotEquals(p13, p12);
		assertEquals(p13, p13);
		assertNotEquals(p13, p14);
		assertNotEquals(p13, p15);
		assertNotEquals(p13, p16);
		assertNotEquals(p13, p17);
		assertNotEquals(p13, p18);

		assertNotEquals(p14, p11);
		assertNotEquals(p14, p12);
		assertNotEquals(p14, p13);
		assertEquals(p14, p14);
		assertNotEquals(p14, p15);
		assertNotEquals(p14, p16);
		assertNotEquals(p14, p17);
		assertNotEquals(p14, p18);

		assertNotEquals(p15, p11);
		assertNotEquals(p15, p12);
		assertNotEquals(p15, p13);
		assertNotEquals(p15, p14);
		assertEquals(p15, p15);
		assertNotEquals(p15, p16);
		assertNotEquals(p15, p17);
		assertNotEquals(p15, p18);

		assertNotEquals(p16, p11);
		assertNotEquals(p16, p12);
		assertNotEquals(p16, p13);
		assertNotEquals(p16, p14);
		assertNotEquals(p16, p15);
		assertEquals(p16, p16);
		assertNotEquals(p16, p17);
		assertNotEquals(p16, p18);

		assertNotEquals(p17, p11);
		assertNotEquals(p17, p12);
		assertNotEquals(p17, p13);
		assertNotEquals(p17, p14);
		assertNotEquals(p17, p15);
		assertNotEquals(p17, p16);
		assertEquals(p17, p17);
		assertNotEquals(p17, p18);

		assertNotEquals(p18, p11);
		assertNotEquals(p18, p12);
		assertNotEquals(p18, p13);
		assertNotEquals(p18, p14);
		assertNotEquals(p18, p15);
		assertNotEquals(p18, p16);
		assertNotEquals(p18, p17);
		assertEquals(p18, p18);

		assertEquals(p21, p21);
		assertNotEquals(p21, p23);
		assertNotEquals(p21, p24);
		assertNotEquals(p21, p25);
		assertNotEquals(p21, p26);
		assertNotEquals(p21, p27);
		assertNotEquals(p21, p28);

		assertNotEquals(p23, p21);
		assertEquals(p23, p23);
		assertNotEquals(p23, p24);
		assertNotEquals(p23, p25);
		assertNotEquals(p23, p26);
		assertNotEquals(p23, p27);
		assertNotEquals(p23, p28);

		assertNotEquals(p24, p21);
		assertNotEquals(p24, p23);
		assertEquals(p24, p24);
		assertNotEquals(p24, p25);
		assertNotEquals(p24, p26);
		assertNotEquals(p24, p27);
		assertNotEquals(p24, p28);

		assertNotEquals(p25, p21);
		assertNotEquals(p25, p23);
		assertNotEquals(p25, p24);
		assertEquals(p25, p25);
		assertNotEquals(p25, p26);
		assertNotEquals(p25, p27);
		assertNotEquals(p25, p28);

		assertNotEquals(p26, p21);
		assertNotEquals(p26, p23);
		assertNotEquals(p26, p24);
		assertNotEquals(p26, p25);
		assertEquals(p26, p26);
		assertNotEquals(p26, p27);
		assertNotEquals(p26, p28);

		assertNotEquals(p27, p21);
		assertNotEquals(p27, p23);
		assertNotEquals(p27, p24);
		assertNotEquals(p27, p25);
		assertNotEquals(p27, p26);
		assertEquals(p27, p27);
		assertNotEquals(p27, p28);

		assertNotEquals(p28, p21);
		assertNotEquals(p28, p23);
		assertNotEquals(p28, p24);
		assertNotEquals(p28, p25);
		assertNotEquals(p28, p26);
		assertNotEquals(p28, p27);
		assertEquals(p28, p28);

		PermissionCollection pc = p13.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p23);

		assertAddPermission(pc, p14);
		assertNotImplies(pc, p21);
		assertNotImplies(pc, p23);
		assertImplies(pc, p24);
		assertNotImplies(pc, p25);
		assertNotImplies(pc, p26);
		assertNotImplies(pc, p27);
		assertNotImplies(pc, p28);

		assertAddPermission(pc, p13);
		assertNotImplies(pc, p21);
		assertImplies(pc, p23);
		assertImplies(pc, p24);
		assertNotImplies(pc, p25);
		assertNotImplies(pc, p26);
		assertImplies(pc, p27);
		assertNotImplies(pc, p28);

		assertSerializable(pc);

		pc = p13.newPermissionCollection();
		assertAddPermission(pc, p11);
		assertImplies(pc, p21);
		assertNotImplies(pc, p23);
		assertImplies(pc, p24);
		assertImplies(pc, p25);
		assertNotImplies(pc, p26);
		assertNotImplies(pc, p27);
		assertNotImplies(pc, p28);

		assertNotAddPermission(pc, op);
		assertSerializable(pc);

		pc = p13.newPermissionCollection();

		assertAddPermission(pc, p13);
		assertNotImplies(pc, p21);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);
		assertNotImplies(pc, p25);
		assertNotImplies(pc, p26);
		assertNotImplies(pc, p27);
		assertNotImplies(pc, p28);

		assertSerializable(pc);

		pc = p11.newPermissionCollection();

		assertAddPermission(pc, p11);
		assertImplies(pc, p21);
		assertNotImplies(pc, p23);
		assertImplies(pc, p24);
		assertImplies(pc, p25);
		assertNotImplies(pc, p26);
		assertNotImplies(pc, p27);
		assertNotImplies(pc, p28);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
		assertSerializable(p15);
		assertSerializable(p16);
		assertSerializable(p17);
		assertSerializable(p18);
		assertNotSerializable(p21);
		assertNotSerializable(p23);
		assertNotSerializable(p24);
		assertNotSerializable(p25);
		assertNotSerializable(p26);
		assertNotSerializable(p27);
		assertNotSerializable(p28);
		assertSerializable(pc);
	}

	/**
	 * 
	 */
	public void testWildcard() {
		Bundle b1 = newMockBundle(2, "test.bsn", "test.location", null);
		CoordinationPermission p21 = new CoordinationPermission(
				"(coordination.name=com.foo.*)", "initiate");
		CoordinationPermission p22 = new CoordinationPermission(
				"com.foo.coord2", b1, "initiate");
		CoordinationPermission p23 = new CoordinationPermission("*", "initiate");
		CoordinationPermission p24 = new CoordinationPermission(
				"com.bar.coord2", b1, "initiate");

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
		CoordinationPermission participate = new CoordinationPermission("*",
				"participate");
		CoordinationPermission admin = new CoordinationPermission("*", "admin");
		CoordinationPermission initiate = new CoordinationPermission("*",
				"initiate");

		assertEquals("participate", participate.getActions());
		assertEquals("admin", admin.getActions());
		assertEquals("initiate", initiate.getActions());

		PermissionCollection adminpc = admin.newPermissionCollection();
		adminpc.add(admin);
		PermissionCollection initiatepc = initiate.newPermissionCollection();
		initiatepc.add(initiate);
		PermissionCollection participatepc = participate
				.newPermissionCollection();
		participatepc.add(participate);

		assertImplies(admin, admin);
		assertImplies(adminpc, admin);
		assertImplies(initiate, initiate);
		assertImplies(initiatepc, initiate);
		assertImplies(participate, participate);
		assertImplies(participatepc, participate);

		assertNotImplies(admin, initiate);
		assertNotImplies(adminpc, initiate);
		assertNotImplies(admin, participate);
		assertNotImplies(adminpc, participate);

		assertNotImplies(initiate, admin);
		assertNotImplies(initiatepc, admin);
		assertNotImplies(initiate, participate);
		assertNotImplies(initiatepc, participate);

		assertNotImplies(participate, initiate);
		assertNotImplies(participatepc, initiate);
		assertNotImplies(participate, admin);
		assertNotImplies(participatepc, admin);
	}

	/**
	 * 
	 */
	public void testFiltersName() {
		Bundle b1 = newMockBundle(2, "test.bsn", "test.location", null);
		CoordinationPermission p31 = new CoordinationPermission(
				"  (coordination.name  =com.foo.coord3)", "initiate");
		CoordinationPermission p32 = new CoordinationPermission(
				"(coordination.name=com.foo.*)", "initiate");
		CoordinationPermission p33 = new CoordinationPermission(
				"(coordination.name=com.*)", "initiate");
		CoordinationPermission p34 = new CoordinationPermission(
				"(coordination.name=*)", "initiate");
		CoordinationPermission p35 = new CoordinationPermission(
				"com.foo.coord3", b1, "initiate");
		CoordinationPermission p38 = new CoordinationPermission("*", "initiate");

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
		CoordinationPermission p41 = new CoordinationPermission("(id=2)",
				"initiate");
		CoordinationPermission p42 = new CoordinationPermission(
				"(location=test.*)", "initiate");
		CoordinationPermission p43 = new CoordinationPermission(
				"(name=test.*)", "initiate");
		CoordinationPermission p44 = new CoordinationPermission(
				"(signer=\\*, o=ACME, c=US)", "initiate");
		CoordinationPermission p45 = new CoordinationPermission(
				"(coordination.name=com.foo.*)", "initiate");

		CoordinationPermission p46 = new CoordinationPermission(
				"com.foo.coord4", newMockBundle(2, "test.bsn",
						"test.location", "cn=Bugs Bunny, o=ACME, c=US"),
				"initiate");
		CoordinationPermission p47 = new CoordinationPermission(
				"com.bar.coord4", newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "initiate");

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
		CoordinationPermission p51 = new CoordinationPermission(
				"  (coordination.name  =com.foo.coord5)", "initiate");
		CoordinationPermission p52 = new CoordinationPermission(
				"(coordination.name=com.foo.*)", "initiate");
		CoordinationPermission p53 = new CoordinationPermission(
				"(coordination.name=com.*)", "initiate");
		CoordinationPermission p54 = new CoordinationPermission(
				"(coordination.name=*)", "initiate");
		CoordinationPermission p59 = new CoordinationPermission("*", "initiate");

		CoordinationPermission p5a = new CoordinationPermission(
				"com.foo.coord5", newMockBundle(2, "test.bsn",
						"test.location", "cn=Bugs Bunny, o=ACME, c=US"),
				"initiate");
		CoordinationPermission p5b = new CoordinationPermission(
				"com.bar.coord5", newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "initiate");

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

	private static void invalidCoordinationPermission(String name,
			String actions) {
		try {
			CoordinationPermission p = new CoordinationPermission(name, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	private static void invalidCoordinationPermission(String name,
			Bundle bundle, String actions) {
		try {
			CoordinationPermission p = new CoordinationPermission(name, bundle,
					actions);
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
