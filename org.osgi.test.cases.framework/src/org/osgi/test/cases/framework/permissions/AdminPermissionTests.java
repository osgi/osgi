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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.PropertyPermission;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.test.support.PermissionTestCase;

public class AdminPermissionTests extends PermissionTestCase {

	/*
	 * @param actions <code>class</code>, <code>execute</code>,
	 * <code>extensionLifecycle</code>, <code>lifecycle</code>,
	 * <code>listener</code>, <code>metadata</code>, <code>resolve</code> ,
	 * <code>resource</code>, <code>startlevel</code> or <code>context</code>. A
	 * value of "*" or <code>null</code> indicates all actions.
	 */
	public void testInvalidAdminPermissions() {
		invalidAdminPermission("*", "x");
		invalidAdminPermission("*", "   class  ,  x   ");
		invalidAdminPermission("*", "");
		invalidAdminPermission("*", "      ");
		invalidAdminPermission("*", ",");
		invalidAdminPermission("*", ",xxx");
		invalidAdminPermission("*", "xxx,");
		invalidAdminPermission("*", "execute,");
		invalidAdminPermission("*", "extensionLifecycle,   ");
		invalidAdminPermission("*", "lifecycleme,");
		invalidAdminPermission("*", "listenerme,");
		invalidAdminPermission("*", ",metadata");
		invalidAdminPermission("*", ",resolve");
		invalidAdminPermission("*", "   resourceme   ");
		invalidAdminPermission("*", "   startlevelme     ");
		invalidAdminPermission("*", "   contex");

		invalidAdminPermission("()", "*");
	}
	
	public void testDefaultAdminPermission() {
		AdminPermission p1 = new AdminPermission();
		AdminPermission p2 = new AdminPermission("*", "*");
		AdminPermission p3 = new AdminPermission((String) null, null);
		AdminPermission p4 = new AdminPermission((String) null, p2.getActions());
		Permission op = new PropertyPermission("java.home", "read"); 

		shouldImply(p1, p2);
		shouldImply(p2, p1);
		shouldImply(p1, p3);
		shouldImply(p3, p1);
		shouldImply(p3, p2);
		shouldImply(p2, p3);
		shouldImply(p1, p4);
		shouldImply(p4, p1);
		shouldImply(p4, p3);
		shouldImply(p3, p4);
		shouldImply(p3, p4);
		shouldImply(p4, p3);
		shouldImply(p4, p2);
		shouldImply(p2, p4);
		shouldImply(p1, p1);
		shouldImply(p2, p2);
		shouldImply(p3, p3);
		shouldImply(p4, p4);
		shouldNotImply(p1, op);

		shouldEqual(p1, p2);
		shouldEqual(p2, p1);
		shouldEqual(p1, p3);
		shouldEqual(p3, p1);
		shouldEqual(p2, p3);
		shouldEqual(p3, p2);
		shouldEqual(p3, p4);
		shouldEqual(p4, p3);
		shouldEqual(p2, p4);
		shouldEqual(p4, p2);
		shouldEqual(p1, p4);
		shouldEqual(p4, p1);
		shouldNotEqual(p1, op);

		PermissionCollection pc = p1.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		shouldNotImply(pc, p1);

		shouldAdd(pc, p1);
		shouldAdd(pc, p2);
		shouldAdd(pc, p3);
		shouldAdd(pc, p4);
		shouldNotAdd(pc, op);

		pc.setReadOnly();

		shouldNotAdd(pc, new AdminPermission());

		shouldImply(pc, p1);
		shouldImply(pc, p2);
		shouldImply(pc, p3);
		shouldImply(pc, p4);
		shouldNotImply(pc, op);

		checkEnumeration(pc.elements(), false);

		testSerialization(p1);
		testSerialization(p2);
		testSerialization(p3);
		testSerialization(p4);
	}
	
	public void testFilterAdminPermission() {
		AdminPermission p1 = new AdminPermission("(id=2)", "class");
		AdminPermission p2 = new AdminPermission(" (id =2)", "class");
		AdminPermission p3 = new AdminPermission(new BundleStub(2, "test.bsn",
				"test.location"), "resolve");
		AdminPermission p4 = new AdminPermission("(name=test.*)", "resource");
		AdminPermission p5 = new AdminPermission("(location=test.*)", "*");
		shouldImply(p1, p3);
		shouldImply(p1, p3);
		shouldImply(p4, p3);
		shouldImply(p5, p3);
		invalidImply(p1, p2);
		invalidImply(p2, p1);
		unsupportedImply(p3, p2);
		unsupportedImply(p3, p1);

		shouldEqual(p1, p2);
		shouldEqual(p2, p1);
		shouldNotEqual(p1, p3);
		shouldNotEqual(p2, p3);

		PermissionCollection pc = p1.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		shouldNotImply(pc, p3);
		invalidImply(pc, p1);

		shouldAdd(pc, new AdminPermission("(id=2)", "class"));
		shouldAdd(pc, new AdminPermission("(id=2)", "resource"));

		Bundle testBundle1 = new BundleStub(2, "test.bsn", "test.location");
		Bundle testBundle2 = new BundleStub(1, "test.bsn", "test.location");
		shouldImply(pc, new AdminPermission(testBundle1, "resolve"));
		shouldImply(pc, new AdminPermission(testBundle1, "class"));
		shouldImply(pc, new AdminPermission(testBundle1, "resource"));
		shouldNotImply(pc, new AdminPermission(testBundle2, "resolve"));
		shouldNotImply(pc, new AdminPermission(testBundle2, "class"));
		shouldNotImply(pc, new AdminPermission(testBundle2, "resource"));
		shouldNotImply(pc, new AdminPermission("*", "resource"));

		shouldAdd(pc, new AdminPermission());
		shouldImply(pc, new AdminPermission(testBundle1, "resolve"));
		shouldImply(pc, new AdminPermission(testBundle1, "class"));
		shouldImply(pc, new AdminPermission(testBundle1, "resource"));
		shouldImply(pc, new AdminPermission(testBundle2, "resolve"));
		shouldImply(pc, new AdminPermission(testBundle2, "class"));
		shouldImply(pc, new AdminPermission(testBundle2, "resource"));
		shouldImply(pc, new AdminPermission("*", "resource"));

		invalidImply(pc, p1);

		checkEnumeration(pc.elements(), false);

		testSerialization(p1);
		testSerialization(p2);
		testSerialization(p4);
		testSerialization(p5);
	}
	
	private void invalidAdminPermission(String name, String actions) {
		try {
			AdminPermission p = new AdminPermission(name, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private void invalidImply(Permission p1, Permission p2) {
		try {
			p1.implies(p2);
			fail("implies did not throw exception");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private void unsupportedImply(Permission p1, Permission p2) {
		try {
			p1.implies(p2);
			fail("implies did not throw exception");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}
	}
	
	private void invalidImply(PermissionCollection pc, Permission p2) {
		try {
			pc.implies(p2);
			fail("implies did not throw exception");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
	
	private static class BundleStub implements Bundle {
		private final long		id;
		private final String	name;
		private final String	location;

		BundleStub(long id, String name, String location) {
			this.id = id;
			this.name = name;
			this.location = location;
		}

		public Enumeration findEntries(String arg0, String arg1, boolean arg2) {
			throw new UnsupportedOperationException();
		}

		public BundleContext getBundleContext() {
			throw new UnsupportedOperationException();
		}

		public long getBundleId() {
			return id;
		}

		public URL getEntry(String arg0) {
			throw new UnsupportedOperationException();
		}

		public Enumeration getEntryPaths(String arg0) {
			throw new UnsupportedOperationException();
		}

		public Dictionary getHeaders() {
			throw new UnsupportedOperationException();
		}

		public Dictionary getHeaders(String arg0) {
			throw new UnsupportedOperationException();
		}

		public long getLastModified() {
			throw new UnsupportedOperationException();
		}

		public String getLocation() {
			return location;
		}

		public ServiceReference[] getRegisteredServices() {
			throw new UnsupportedOperationException();
		}

		public URL getResource(String arg0) {
			throw new UnsupportedOperationException();
		}

		public Enumeration getResources(String arg0) throws IOException {
			throw new UnsupportedOperationException();
		}

		public ServiceReference[] getServicesInUse() {
			throw new UnsupportedOperationException();
		}

		public Map getSignerCertificates(int arg0) {
			return Collections.EMPTY_MAP;
		}

		public int getState() {
			throw new UnsupportedOperationException();
		}

		public String getSymbolicName() {
			return name;
		}

		public Version getVersion() {
			throw new UnsupportedOperationException();
		}

		public boolean hasPermission(Object arg0) {
			throw new UnsupportedOperationException();
		}

		public Class loadClass(String arg0) throws ClassNotFoundException {
			throw new UnsupportedOperationException();
		}

		public void start() throws BundleException {
			throw new UnsupportedOperationException();
		}

		public void start(int arg0) throws BundleException {
			throw new UnsupportedOperationException();
		}

		public void stop() throws BundleException {
			throw new UnsupportedOperationException();
		}

		public void stop(int arg0) throws BundleException {
			throw new UnsupportedOperationException();
		}

		public void uninstall() throws BundleException {
			throw new UnsupportedOperationException();
		}

		public void update() throws BundleException {
			throw new UnsupportedOperationException();
		}

		public void update(InputStream arg0) throws BundleException {
			throw new UnsupportedOperationException();
		}
	}
}
