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
		invalidPackagePermission("(package.name=a.b.c)", "export"); 
		invalidPackagePermission("(package.name=a.b.c)", null, "import"); 
		invalidPackagePermission("a.b.c", null, "import"); 
		invalidPackagePermission("a.b.c", newMockBundle(2, "test.bsn",
				"test.location", null), "export"); 
		invalidPackagePermission("a.b.c", newMockBundle(2, "test.bsn",
				"test.location", null), "exportonly"); 
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
		
		assertEquals("exportonly,import", p11.getActions());
		assertEquals("exportonly,import", p12.getActions());
		assertEquals("exportonly,import", p13.getActions());
		assertEquals("import", p14.getActions());

		assertImplies(p11, p11);
		assertImplies(p11, p12);
		assertImplies(p11, p13);
		assertImplies(p11, p14);

		assertImplies(p12, p11);
		assertImplies(p12, p12);
		assertImplies(p12, p13);
		assertImplies(p12, p14);

		assertImplies(p13, p11);
		assertImplies(p13, p12);
		assertImplies(p13, p13);
		assertImplies(p13, p14);

		assertImplies(p14, p14);

		assertNotImplies(p14, p11);
		assertNotImplies(p14, p12);
		assertNotImplies(p14, p13);

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

		assertNotEquals(p11, p14);
		assertNotEquals(p12, p14);
		assertNotEquals(p13, p14);
		assertNotEquals(p14, p11);
		assertNotEquals(p14, p12);
		assertNotEquals(p14, p13);

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

		pc = p13.newPermissionCollection();

		assertAddPermission(pc, p13);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

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

		pc = p22.newPermissionCollection();

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

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

	public void testFiltersPackage() {
		PackagePermission p31 = new PackagePermission(
				"  (package.name  =com.foo.service2)",
				"import");
		PackagePermission p32 = new PackagePermission(
				"(package.name=com.foo.*)", "import");
		PackagePermission p33 = new PackagePermission("(package.name=com.*)",
				"import");
		PackagePermission p34 = new PackagePermission("(package.name=*)",
				"import");
		PackagePermission p35 = new PackagePermission("com.foo.service2",
				"import");
		PackagePermission p36 = new PackagePermission("com.foo.*", "import");

		assertImplies(p31, p35);
		assertImplies(p32, p35);
		assertImplies(p33, p35);
		assertImplies(p34, p35);

		assertInvalidImplies(p31, p32);
		assertInvalidImplies(p31, p33);
		assertInvalidImplies(p31, p34);

		assertInvalidImplies(p32, p33);
		assertInvalidImplies(p32, p34);

		assertInvalidImplies(p33, p34);

		PermissionCollection pc = p31.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p34);
		assertImplies(pc, p35);

		assertInvalidImplies(pc, p32);
		assertInvalidImplies(pc, p33);
		assertInvalidImplies(pc, p34);

		assertAddPermission(pc, p33);
		assertImplies(pc, p35);
		
		assertAddPermission(pc, p32);
		assertImplies(pc, p35);

		assertAddPermission(pc, p31);
		assertImplies(pc, p35);
		checkEnumeration(pc.elements(), false);

		pc = p32.newPermissionCollection();

		assertAddPermission(pc, p33);
		assertImplies(pc, p35);

		assertAddPermission(pc, p36);
		assertImplies(pc, p35);
		
		assertSerializable(p31);
		assertSerializable(p32);
		assertSerializable(p33);
		assertSerializable(p34);
		assertSerializable(p35);
		assertSerializable(p36);
	}
	
	public void testFiltersBundles() {
		PackagePermission p41 = new PackagePermission(
				"(exporter.id=2)",
				"import");
		PackagePermission p42 = new PackagePermission(
				"(exporter.location=test.*)", "import");
		PackagePermission p43 = new PackagePermission("(exporter.name=test.*)",
				"import");
		PackagePermission p44 = new PackagePermission(
				"(exporter.signer=\\*, o=ACME, c=US)",
				"import");
		PackagePermission p45 = new PackagePermission(
				"(package.name=com.foo.*)", "import");
		
		PackagePermission p46 = new PackagePermission("com.foo.service2",
				newMockBundle(2, "test.bsn", "test.location",
						"cn=Bugs Bunny, o=ACME, c=US"),
				"import");
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
		
		assertInvalidImplies(p41, p42);
		assertInvalidImplies(p41, p43);
		assertInvalidImplies(p41, p44);
		assertInvalidImplies(p41, p45);

		PermissionCollection pc = p41.newPermissionCollection();
		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p44);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);

		assertInvalidImplies(pc, p42);
		assertInvalidImplies(pc, p43);
		assertInvalidImplies(pc, p44);
		assertInvalidImplies(pc, p45);

		assertAddPermission(pc, p43);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);

		assertAddPermission(pc, p42);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);

		assertAddPermission(pc, p41);
		assertImplies(pc, p46);
		assertNotImplies(pc, p47);
		checkEnumeration(pc.elements(), false);

		assertNotAddPermission(pc, p46);
		assertNotAddPermission(pc, p47);

		assertSerializable(p41);
		assertSerializable(p42);
		assertSerializable(p43);
		assertSerializable(p44);
		assertSerializable(p45);
		assertNotSerializable(p46);
		assertNotSerializable(p47);
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
