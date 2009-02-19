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

import org.osgi.framework.PackagePermission;
import org.osgi.test.support.PermissionTestCase;

public class PackagePermissionTests extends PermissionTestCase {

	public void testInvalid() {
		invalidPackagePermission("a.b.c", "x");
		invalidPackagePermission("a.b.c", "   get  ,  x   ");
		invalidPackagePermission("a.b.c", "");
		invalidPackagePermission("a.b.c", "      ");
		invalidPackagePermission("a.b.c", null);
		invalidPackagePermission("a.b.c", ",");
		invalidPackagePermission("a.b.c", ",xxx");
		invalidPackagePermission("a.b.c", "xxx,");
		invalidPackagePermission("a.b.c", "import,");
		invalidPackagePermission("a.b.c", "export,   ");
		invalidPackagePermission("a.b.c", "importme,");
		invalidPackagePermission("a.b.c", "exportme,");
		invalidPackagePermission("a.b.c", ",import");
		invalidPackagePermission("a.b.c", ",export");
		invalidPackagePermission("a.b.c", "   importme   ");
		invalidPackagePermission("a.b.c", "   exportme     ");
		invalidPackagePermission("a.b.c", "   impor");
		invalidPackagePermission("a.b.c", "   expor"); 
	}

	public void testPermissions() {
		Permission op = new PropertyPermission("java.home", "read"); 

		PackagePermission p11 = new PackagePermission("com.foo.service1",
				"    IMPORT,export   ");
		PackagePermission p12 = new PackagePermission("com.foo.service1",
				"EXPORT  ,   import");
		PackagePermission p13 = new PackagePermission("com.foo.service1",
				"expORT   ");
		PackagePermission p14 = new PackagePermission("com.foo.service1",
				"    Import    "); 

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

		pc = p21.newPermissionCollection();

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

		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
		assertSerializable(p21);
		assertSerializable(p22);
		assertSerializable(p23);
		assertSerializable(p24);
	}
	
	public void testActionImplications() {
		PackagePermission export = new PackagePermission("*", "export");
		PackagePermission importx = new PackagePermission("*", "import");

		assertImplies(export, export);
		assertImplies(export, importx);
		assertNotImplies(importx, export);
		assertImplies(importx, importx);
	}

	private void invalidPackagePermission(String name, String actions) {
		try {
			PackagePermission p = new PackagePermission(name, actions);
			fail(p + " created with invalid actions"); 
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
