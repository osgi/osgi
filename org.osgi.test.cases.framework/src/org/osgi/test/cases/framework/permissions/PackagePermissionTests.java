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

package org.osgi.test.cases.framework.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.framework.PackagePermission;
import org.osgi.test.support.PermissionTestCase;

public class PackagePermissionTests extends PermissionTestCase {

	public void testInvalidPackagePermissions() {
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

	public void testPackagePermission() {
		Permission op = new PropertyPermission("java.home", "read"); 

		PackagePermission p11 = new PackagePermission("com.foo.service1",
				"    IMPORT,export   ");
		PackagePermission p12 = new PackagePermission("com.foo.service1",
				"EXPORT  ,   import");
		PackagePermission p13 = new PackagePermission("com.foo.service1",
				"expORT   ");
		PackagePermission p14 = new PackagePermission("com.foo.service1",
				"    Import    "); 

		shouldImply(p11, p11);
		shouldImply(p11, p12);
		shouldImply(p11, p13);
		shouldImply(p11, p14);

		shouldImply(p12, p11);
		shouldImply(p12, p12);
		shouldImply(p12, p13);
		shouldImply(p12, p14);

		shouldImply(p13, p11);
		shouldImply(p13, p12);
		shouldImply(p13, p13);
		shouldImply(p13, p14);

		shouldImply(p14, p14);

		shouldNotImply(p14, p11);
		shouldNotImply(p14, p12);
		shouldNotImply(p14, p13);

		shouldNotImply(p11, op);

		shouldEqual(p11, p11);
		shouldEqual(p11, p12);
		shouldEqual(p11, p13);
		shouldEqual(p12, p11);
		shouldEqual(p12, p12);
		shouldEqual(p12, p13);
		shouldEqual(p13, p11);
		shouldEqual(p13, p12);
		shouldEqual(p13, p13);

		shouldNotEqual(p11, p14);
		shouldNotEqual(p12, p14);
		shouldNotEqual(p13, p14);
		shouldNotEqual(p14, p11);
		shouldNotEqual(p14, p12);
		shouldNotEqual(p14, p13);

		PermissionCollection pc = p13.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		shouldNotImply(pc, p11);

		shouldAdd(pc, p14);
		shouldImply(pc, p14);
		shouldNotImply(pc, p11);
		shouldNotImply(pc, p12);
		shouldNotImply(pc, p13);

		shouldAdd(pc, p13);
		shouldImply(pc, p11);
		shouldImply(pc, p12);
		shouldImply(pc, p13);
		shouldImply(pc, p14);

		shouldNotAdd(pc, op);

		pc = p13.newPermissionCollection();

		shouldAdd(pc, p13);
		shouldImply(pc, p11);
		shouldImply(pc, p12);
		shouldImply(pc, p13);
		shouldImply(pc, p14);

		pc = p11.newPermissionCollection();

		shouldAdd(pc, p11);
		shouldImply(pc, p11);
		shouldImply(pc, p12);
		shouldImply(pc, p13);
		shouldImply(pc, p14);

		pc.setReadOnly();

		shouldNotAdd(pc, p12);

		checkEnumeration(pc.elements(), false);

		PackagePermission p21 = new PackagePermission("com.foo.service2",
				"import");
		PackagePermission p22 = new PackagePermission("com.foo.*", "import");
		PackagePermission p23 = new PackagePermission("com.*", "import");
		PackagePermission p24 = new PackagePermission("*", "import"); 

		shouldImply(p21, p21);
		shouldImply(p22, p21);
		shouldImply(p23, p21);
		shouldImply(p24, p21);

		shouldImply(p22, p22);
		shouldImply(p23, p22);
		shouldImply(p24, p22);

		shouldImply(p23, p23);
		shouldImply(p24, p23);

		shouldImply(p24, p24);

		shouldNotImply(p21, p22);
		shouldNotImply(p21, p23);
		shouldNotImply(p21, p24);

		shouldNotImply(p22, p23);
		shouldNotImply(p22, p24);

		shouldNotImply(p23, p24);

		pc = p21.newPermissionCollection();

		shouldAdd(pc, p21);
		shouldImply(pc, p21);
		shouldNotImply(pc, p22);
		shouldNotImply(pc, p23);
		shouldNotImply(pc, p24);

		shouldAdd(pc, p22);
		shouldImply(pc, p21);
		shouldImply(pc, p22);
		shouldNotImply(pc, p23);
		shouldNotImply(pc, p24);

		shouldAdd(pc, p23);
		shouldImply(pc, p21);
		shouldImply(pc, p22);
		shouldImply(pc, p23);
		shouldNotImply(pc, p24);

		shouldAdd(pc, p24);
		shouldImply(pc, p21);
		shouldImply(pc, p22);
		shouldImply(pc, p23);
		shouldImply(pc, p24);

		pc = p22.newPermissionCollection();

		shouldAdd(pc, p22);
		shouldImply(pc, p21);
		shouldImply(pc, p22);
		shouldNotImply(pc, p23);
		shouldNotImply(pc, p24);

		pc = p23.newPermissionCollection();

		shouldAdd(pc, p23);
		shouldImply(pc, p21);
		shouldImply(pc, p22);
		shouldImply(pc, p23);
		shouldNotImply(pc, p24);

		pc = p24.newPermissionCollection();

		shouldAdd(pc, p24);
		shouldImply(pc, p21);
		shouldImply(pc, p22);
		shouldImply(pc, p23);
		shouldImply(pc, p24);

		testSerialization(p11);
		testSerialization(p12);
		testSerialization(p13);
		testSerialization(p14);
		testSerialization(p21);
		testSerialization(p22);
		testSerialization(p23);
		testSerialization(p24);
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
