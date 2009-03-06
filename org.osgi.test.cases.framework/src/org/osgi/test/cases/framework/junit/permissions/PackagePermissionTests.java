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

import org.osgi.framework.Bundle;
import org.osgi.framework.PackagePermission;
import org.osgi.test.support.PermissionTestCase;

public class PackagePermissionTests extends PermissionTestCase {

	public void testInvalid() {
		invalidPackagePermission("a.b.c", "x");
		invalidPackagePermission("a.b.c", "   import  ,  x   ");
		invalidPackagePermission("a.b.c", "");
		invalidPackagePermission("a.b.c", "      ");
		invalidPackagePermission("a.b.c", null);
		invalidPackagePermission("a.b.c", ",");
		invalidPackagePermission("a.b.c", ",xxx");
		invalidPackagePermission("a.b.c", "xxx,");
		invalidPackagePermission("a.b.c", "import,");
		invalidPackagePermission("a.b.c", "export,   ");
		invalidPackagePermission("a.b.c", "exportonly,   ");
		invalidPackagePermission("a.b.c", "importme,");
		invalidPackagePermission("a.b.c", "exportme,");
		invalidPackagePermission("a.b.c", "exportonlyme,");
		invalidPackagePermission("a.b.c", ",import");
		invalidPackagePermission("a.b.c", ",export");
		invalidPackagePermission("a.b.c", ",exportonly");
		invalidPackagePermission("a.b.c", "   importme   ");
		invalidPackagePermission("a.b.c", "   exportme     ");
		invalidPackagePermission("a.b.c", "   exportonlyme     ");
		invalidPackagePermission("a.b.c", "   impor");
		invalidPackagePermission("a.b.c", "   expor");
		invalidPackagePermission("a.b.c", "   exportonl");
		invalidPackagePermission("()", "import");
		invalidPackagePermission("(package.name=a.b.c)", "exportonly");
		invalidPackagePermission("(package.name=a.b.c)", "exportonly,IMPORT");
		invalidPackagePermission("(package.name=a.b.c)", "export");
		invalidPackagePermission("(package.name=a.b.c)", "export,exportonly");
		invalidPackagePermission("(package.name=a.b.c)", "impoRT,exportonly");
		invalidPackagePermission("(package.name=a.b.c)", "impoRT,export");
		invalidPackagePermission("(package.name=a.b.c)",
				"export,impoRT,exportonly");

		invalidPackagePermission("(package.name=a.b.c)", null, "import");
		invalidPackagePermission("a.b.c", null, "import");
		invalidPackagePermission("a.b.c", newMockBundle(2, "test.bsn",
				"test.location", null), "export");
		invalidPackagePermission("a.b.c", newMockBundle(2, "test.bsn",
				"test.location", null), "exportonly");
		invalidPackagePermission("a.b.c", newMockBundle(2, "test.bsn",
				"test.location", null), "import, export");
		invalidPackagePermission("a.b.c", newMockBundle(2, "test.bsn",
				"test.location", null), "IMporT, exportonly");
		invalidPackagePermission("a.b.c", newMockBundle(2, "test.bsn",
				"test.location", null), "ExporT, exportonly,IMport");
	}

	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read");

		PackagePermission p11 = new PackagePermission("com.foo.service1",
				"    IMPORT,exportonly   ");
		PackagePermission p12 = new PackagePermission("com.foo.service1",
				"EXPORTONLY  ,   import");
		PackagePermission p13 = new PackagePermission("com.foo.service1",
				"expORT   ");

		PackagePermission p14 = new PackagePermission("com.foo.service1",
				"    Import    ");
		PackagePermission p15 = new PackagePermission("com.foo.service1",
				"expORTonly   ");
		PackagePermission p16 = new PackagePermission("com.foo.service1",
				"expORTonly,export");
		PackagePermission p17 = new PackagePermission("com.foo.service1",
				"impORt, EXport");
		PackagePermission p18 = new PackagePermission("com.foo.service1",
				"exPORTonly, impORt, EXport");

		assertEquals("exportonly,import", p11.getActions());
		assertEquals("exportonly,import", p12.getActions());
		assertEquals("exportonly,import", p13.getActions());
		assertEquals("import", p14.getActions());
		assertEquals("exportonly", p15.getActions());
		assertEquals("exportonly,import", p16.getActions());
		assertEquals("exportonly,import", p17.getActions());
		assertEquals("exportonly,import", p18.getActions());

		assertImplies(p11, p11);
		assertImplies(p11, p12);
		assertImplies(p11, p13);
		assertImplies(p11, p14);
		assertImplies(p11, p15);
		assertImplies(p11, p16);
		assertImplies(p11, p17);
		assertImplies(p11, p18);

		assertImplies(p14, p14);
		assertImplies(p15, p15);

		assertNotImplies(p14, p11);
		assertNotImplies(p14, p12);
		assertNotImplies(p14, p13);
		assertNotImplies(p14, p15);
		assertNotImplies(p14, p16);
		assertNotImplies(p14, p17);
		assertNotImplies(p14, p18);

		assertNotImplies(p15, p11);
		assertNotImplies(p15, p12);
		assertNotImplies(p15, p13);
		assertNotImplies(p15, p14);
		assertNotImplies(p15, p16);
		assertNotImplies(p15, p17);
		assertNotImplies(p15, p18);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertEquals(p11, p13);
		assertEquals(p11, p16);
		assertEquals(p11, p17);
		assertEquals(p11, p18);

		assertNotEquals(p11, p14);
		assertNotEquals(p11, p15);

		assertNotEquals(p13, p14);
		assertNotEquals(p13, p15);

		assertNotEquals(p14, p11);
		assertNotEquals(p14, p15);

		assertNotEquals(p15, p11);
		assertNotEquals(p15, p14);

		assertNotEquals(p16, p14);

		assertNotEquals(p11, p15);

		assertNotEquals(p14, p11);
		assertNotEquals(p14, p15);

		PermissionCollection pc = p13.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p11);

		assertAddPermission(pc, p14);
		assertImplies(pc, p14);
		assertNotImplies(pc, p11);
		assertNotImplies(pc, p12);
		assertNotImplies(pc, p13);
		assertNotImplies(pc, p15);
		assertNotImplies(pc, p16);
		assertNotImplies(pc, p17);
		assertNotImplies(pc, p18);

		assertAddPermission(pc, p13);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertImplies(pc, p15);
		assertImplies(pc, p16);
		assertImplies(pc, p17);
		assertImplies(pc, p18);
		assertSerializable(pc);

		pc = p13.newPermissionCollection();
		assertAddPermission(pc, p11);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertImplies(pc, p15);
		assertImplies(pc, p16);
		assertImplies(pc, p17);
		assertImplies(pc, p18);

		assertNotAddPermission(pc, op);
		assertSerializable(pc);

		pc = p13.newPermissionCollection();

		assertAddPermission(pc, p13);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertImplies(pc, p15);
		assertImplies(pc, p16);
		assertImplies(pc, p17);
		assertImplies(pc, p18);
		assertSerializable(pc);

		pc = p11.newPermissionCollection();

		assertAddPermission(pc, p11);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertImplies(pc, p15);
		assertImplies(pc, p16);
		assertImplies(pc, p17);
		assertImplies(pc, p18);

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
		assertSerializable(pc);
	}

	public void testNames() {
		PackagePermission p21 = new PackagePermission("com.foo.service2",
				"import");
		PackagePermission p22 = new PackagePermission("com.foo.*", "import");
		PackagePermission p23 = new PackagePermission("com.*", "import");
		PackagePermission p24 = new PackagePermission("*", "import");

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
		PackagePermission exportonly = new PackagePermission("*", "exportonly");
		PackagePermission export = new PackagePermission("*", "export");
		PackagePermission importx = new PackagePermission("*", "import");

		assertEquals("exportonly", exportonly.getActions());
		assertEquals("exportonly,import", export.getActions());
		assertEquals("import", importx.getActions());

		assertImplies(export, export);
		assertImplies(importx, importx);
		assertImplies(exportonly, exportonly);

		assertImplies(export, importx);
		assertImplies(export, exportonly);

		assertNotImplies(importx, export);
		assertNotImplies(importx, exportonly);

		assertNotImplies(exportonly, importx);
		assertNotImplies(exportonly, export);
	}

	public void testFiltersName() {
		PackagePermission p31 = new PackagePermission(
				"  (package.name  =com.foo.service2)", "import");
		PackagePermission p32 = new PackagePermission(
				"(package.name=com.foo.*)", "import");
		PackagePermission p33 = new PackagePermission("(package.name=com.*)",
				"import");
		PackagePermission p34 = new PackagePermission("(package.name=*)",
				"import");
		PackagePermission p35 = new PackagePermission("com.foo.service2",
				"import");
		PackagePermission p36 = new PackagePermission("com.foo.*", "import");
		PackagePermission p37 = new PackagePermission("com.*", "import");
		PackagePermission p38 = new PackagePermission("*", "import");

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

	public void testFiltersBundle() {
		PackagePermission p41 = new PackagePermission("(id=2)",
				"import");
		PackagePermission p42 = new PackagePermission(
				"(location=test.*)",
				"import");
		PackagePermission p43 = new PackagePermission("(name=test.*)",
				"import");
		PackagePermission p44 = new PackagePermission(
				"(signer=\\*, o=ACME, c=US)", "import");
		PackagePermission p45 = new PackagePermission(
				"(package.name=com.foo.*)", "import");

		PackagePermission p46 = new PackagePermission("com.foo.service2",
				newMockBundle(2, "test.bsn", "test.location",
						"cn=Bugs Bunny, o=ACME, c=US"), "import");
		PackagePermission p47 = new PackagePermission("com.bar.service2",
				newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "import");

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
	
	public void testPermissionCollection() {
		PackagePermission p51 = new PackagePermission(
				"  (package.name  =com.foo.service2)", "import");
		PackagePermission p52 = new PackagePermission(
				"(package.name=com.foo.*)", "import");
		PackagePermission p53 = new PackagePermission("(package.name=com.*)",
				"import");
		PackagePermission p54 = new PackagePermission("(package.name=*)",
				"import");
		PackagePermission p55 = new PackagePermission("com.foo.service2",
				"import");
		
		PackagePermission p56 = new PackagePermission("com.bar.service2",
		"import");
		PackagePermission p57 = new PackagePermission("com.bar.*", "import");
		PackagePermission p58 = new PackagePermission("com.*", "import");
		PackagePermission p59 = new PackagePermission("*", "import");

		PackagePermission p5a = new PackagePermission("com.foo.service2",
				newMockBundle(2, "test.bsn", "test.location",
						"cn=Bugs Bunny, o=ACME, c=US"), "import");
		PackagePermission p5b = new PackagePermission("com.bar.service2",
				newMockBundle(3, "not.bsn", "not.location",
						"cn=Bugs Bunny, o=NOT, c=US"), "import");

		PermissionCollection pc;
		pc = p51.newPermissionCollection();
		assertAddPermission(pc, p51);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertNotImplies(pc, p5b);
		assertNotImplies(pc, p56);
		assertSerializable(pc);
		
		pc = p56.newPermissionCollection();
		assertAddPermission(pc, p56);
		assertNotImplies(pc, p5a);
		assertNotImplies(pc, p55);
		assertImplies(pc, p5b);
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
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertNotImplies(pc, p5b);
		assertNotImplies(pc, p56);
		assertSerializable(pc);
		
		pc = p57.newPermissionCollection();
		assertAddPermission(pc, p57);
		assertNotImplies(pc, p5a);
		assertNotImplies(pc, p55);
		assertImplies(pc, p5b);
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
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p58.newPermissionCollection();
		assertAddPermission(pc, p58);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
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
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);

		pc = p59.newPermissionCollection();
		assertAddPermission(pc, p59);
		assertImplies(pc, p5a);
		assertImplies(pc, p55);
		assertImplies(pc, p5b);
		assertImplies(pc, p56);
		assertSerializable(pc);
	}

	private static void invalidPackagePermission(String name, String actions) {
		try {
			PackagePermission p = new PackagePermission(name, actions);
			fail(p + " created with invalid actions");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private static void invalidPackagePermission(String name, Bundle bundle,
			String actions) {
		try {
			PackagePermission p = new PackagePermission(name, bundle, actions);
			fail(p + " created with invalid actions");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
