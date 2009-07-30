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

import java.security.AccessControlException;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.MockFactory;
import org.osgi.test.support.PermissionTestCase;

public class PermissionRecursionTests extends PermissionTestCase {

	public void testAdminPermission() {
		AdminPermission p1 = new AdminPermission("(location=test.*)", "*");
		PermissionCollection pc = new Permissions();

		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p1);

		Bundle testBundle1 = newMockBundle(1, "test.bsn", "test.location", pc);
		Bundle testBundle2 = newMockBundle(2, "test.bsn", "test2.location", pc);
		assertImplies(pc, new AdminPermission(testBundle1, "resolve"));
		assertImplies(pc, new AdminPermission(testBundle1, "class"));
		assertImplies(pc, new AdminPermission(testBundle1, "resource"));
		assertNotImplies(pc, new AdminPermission(testBundle2, "resolve"));
		assertNotImplies(pc, new AdminPermission(testBundle2, "class"));
		assertNotImplies(pc, new AdminPermission(testBundle2, "resource"));
		assertNotImplies(pc, new AdminPermission("*", "resource"));
	}

	public void testServicePermission() {
		ServicePermission p1 = new ServicePermission("(location=test.*)", "get");
		PermissionCollection pc = new Permissions();

		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p1);
		assertAddPermission(pc, new AdminPermission("(location=*.location)",
				"*"));

		Bundle b1 = newMockBundle(1, "test.bsn", "test.location", pc);
		Bundle b2 = newMockBundle(2, "test.bsn", "test2.location", pc);
		Map m1 = new HashMap();
		m1.put("service.id", new Long(2));
		m1.put("objectClass", new String[] {"com.foo.Service2"});
		ServiceReference r1 = newMockServiceReference(b1, m1);
		Map m2 = new HashMap();
		m2.put("service.id", new Long(3));
		m2.put("objectClass", new String[] {"com.bar.Service2"});
		ServiceReference r2 = newMockServiceReference(b2, m2);

		assertImplies(pc, new ServicePermission(r1, "get"));
		assertNotImplies(pc, new ServicePermission(r2, "get"));
		assertNotImplies(pc, new ServicePermission("*", "get"));
	}

	public void testPackagePermission() {
		PackagePermission p1 = new PackagePermission("(location=test.*)",
				"import");
		PermissionCollection pc = new Permissions();

		checkEnumeration(pc.elements(), true);

		assertAddPermission(pc, p1);
		assertAddPermission(pc, new AdminPermission("(location=*.location)",
				"*"));

		Bundle b1 = newMockBundle(1, "test.bsn", "test.location", pc);
		Bundle b2 = newMockBundle(2, "test.bsn", "test2.location", pc);

		assertImplies(pc, new PackagePermission("com.foo", b1, "import"));
		assertNotImplies(pc, new PackagePermission("com.bar", b2, "import"));
		assertNotImplies(pc, new PackagePermission("*", "import"));
	}

	public static Bundle newMockBundle(long id, String name, String location,
			PermissionCollection pc) {
		MockBundle mock = new MockBundle(id, name, location, pc);
		Bundle bundle = (Bundle) MockFactory.newMock(Bundle.class, mock);
		// set the Bundle object into the mocked bundle
		mock.setBundle(bundle);
		return bundle;
	}

	private static class MockBundle {
		private final long					id;
		private final String				name;
		private final String				location;
		private final PermissionCollection	pc;
		private volatile Bundle				bundle;
		private int							recursion;

		MockBundle(long id, String name, String location,
				PermissionCollection pc) {
			this.id = id;
			this.name = name;
			this.location = location;
			this.pc = pc;
			recursion = 0;
		}

		void setBundle(Bundle bundle) {
			this.bundle = bundle;
		}

		public long getBundleId() {
			return id;
		}

		public String getLocation() {
			synchronized (this) {
				if (recursion > 5) {
					fail("permission recursion");
				}
				recursion++;
			}
			try {
				if (!pc.implies(new AdminPermission(bundle, "metadata"))) {
					throw new AccessControlException("permission not implied");
				}
				return location;
			}
			finally {
				synchronized (this) {
					recursion--;
				}
			}
		}

		public String getSymbolicName() {
			return name;
		}

		public Map getSignerCertificates(int type) {
			return Collections.EMPTY_MAP;
		}
	}
}
