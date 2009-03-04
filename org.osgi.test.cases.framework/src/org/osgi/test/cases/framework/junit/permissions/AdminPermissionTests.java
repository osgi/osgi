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

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.test.support.PermissionTestCase;

public class AdminPermissionTests extends PermissionTestCase {

	/*
	 * @param actions <code>class</code>, <code>execute</code>,
	 * <code>extensionLifecycle</code>, <code>lifecycle</code>,
	 * <code>listener</code>, <code>metadata</code>, <code>resolve</code> ,
	 * <code>resource</code>, <code>startlevel</code> or <code>context</code>. A
	 * value of "*" or <code>null</code> indicates all actions.
	 */
	public void testInvalid() {
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
		invalidAdminPermission((Bundle) null, "*");
	}

	public void testDefault() {
		AdminPermission p1 = new AdminPermission();
		AdminPermission p2 = new AdminPermission("*", "*");
		AdminPermission p3 = new AdminPermission((String) null, null);
		AdminPermission p4 = new AdminPermission((String) null, p2.getActions());
		Permission op = new PropertyPermission("java.home", "read");

		assertImplies(p1, p2);
		assertImplies(p2, p1);
		assertImplies(p1, p3);
		assertImplies(p3, p1);
		assertImplies(p3, p2);
		assertImplies(p2, p3);
		assertImplies(p1, p4);
		assertImplies(p4, p1);
		assertImplies(p4, p3);
		assertImplies(p3, p4);
		assertImplies(p3, p4);
		assertImplies(p4, p3);
		assertImplies(p4, p2);
		assertImplies(p2, p4);
		assertImplies(p1, p1);
		assertImplies(p2, p2);
		assertImplies(p3, p3);
		assertImplies(p4, p4);
		assertNotImplies(p1, op);

		assertEquals(p1, p2);
		assertEquals(p2, p1);
		assertEquals(p1, p3);
		assertEquals(p3, p1);
		assertEquals(p2, p3);
		assertEquals(p3, p2);
		assertEquals(p3, p4);
		assertEquals(p4, p3);
		assertEquals(p2, p4);
		assertEquals(p4, p2);
		assertEquals(p1, p4);
		assertEquals(p4, p1);
		assertNotEquals(p1, op);

		PermissionCollection pc = p1.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p1);

		assertAddPermission(pc, p1);
		assertAddPermission(pc, p2);
		assertAddPermission(pc, p3);
		assertAddPermission(pc, p4);
		assertNotAddPermission(pc, op);

		pc.setReadOnly();

		assertNotAddPermission(pc, new AdminPermission());

		assertImplies(pc, p1);
		assertImplies(pc, p2);
		assertImplies(pc, p3);
		assertImplies(pc, p4);
		assertNotImplies(pc, op);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p1);
		assertSerializable(p2);
		assertSerializable(p3);
		assertSerializable(p4);
		assertSerializable(pc);
	}

	public void testFilter() {
		AdminPermission p1 = new AdminPermission("(id=2)", "class");
		AdminPermission p2 = new AdminPermission(" (id =2)", "class");
		AdminPermission p3 = new AdminPermission(newMockBundle(2, "test.bsn",
				"test.location", null), "resolve");
		AdminPermission p4 = new AdminPermission("(name=test.*)", "resource");
		AdminPermission p5 = new AdminPermission("(location=test.*)", "*");
		assertImplies(p1, p3);
		assertImplies(p1, p3);
		assertImplies(p4, p3);
		assertImplies(p5, p3);
		assertNotImplies(p1, p2);
		assertNotImplies(p2, p1);
		assertNotImplies(p3, p2);
		assertNotImplies(p3, p1);

		assertEquals(p1, p2);
		assertEquals(p2, p1);
		assertNotEquals(p1, p3);
		assertNotEquals(p2, p3);

		PermissionCollection pc = p1.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p3);
		assertNotImplies(pc, p1);

		assertAddPermission(pc, new AdminPermission("(id=2)", "class"));
		assertAddPermission(pc, new AdminPermission("(id=2)", "resource"));

		Bundle testBundle1 = newMockBundle(2, "test.bsn", "test.location", null);
		Bundle testBundle2 = newMockBundle(1, "test.bsn", "test.location", null);
		assertImplies(pc, new AdminPermission(testBundle1, "resolve"));
		assertImplies(pc, new AdminPermission(testBundle1, "class"));
		assertImplies(pc, new AdminPermission(testBundle1, "resource"));
		assertNotImplies(pc, new AdminPermission(testBundle2, "resolve"));
		assertNotImplies(pc, new AdminPermission(testBundle2, "class"));
		assertNotImplies(pc, new AdminPermission(testBundle2, "resource"));
		assertNotImplies(pc, new AdminPermission("*", "resource"));

		assertAddPermission(pc, new AdminPermission());
		assertImplies(pc, new AdminPermission(testBundle1, "resolve"));
		assertImplies(pc, new AdminPermission(testBundle1, "class"));
		assertImplies(pc, new AdminPermission(testBundle1, "resource"));
		assertImplies(pc, new AdminPermission(testBundle2, "resolve"));
		assertImplies(pc, new AdminPermission(testBundle2, "class"));
		assertImplies(pc, new AdminPermission(testBundle2, "resource"));
		assertImplies(pc, new AdminPermission("*", "resource"));

		assertNotImplies(pc, p1);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p1);
		assertSerializable(p2);
		assertNotSerializable(p3);
		assertSerializable(p4);
		assertSerializable(p5);
		assertSerializable(pc);
	}

	public void testSigners() {
		AdminPermission ap = new AdminPermission("(signer=\\*, o=ACME, c=US)",
				"*");

		assertImplies(ap, new AdminPermission(newMockBundle(1, "test.bsn",
				"test.location", "cn=Bugs Bunny, o=ACME, c=US"), "*"));
		assertImplies(ap, new AdminPermission(newMockBundle(2, "test.bsn",
				"test.location", "ou = Carrots, cn=Daffy Duck, o=ACME, c=US"),
		"*"));
		assertImplies(ap, new AdminPermission(newMockBundle(3, "test.bsn",
				"test.location", "dc=www,dc=acme,dc=com,o=ACME,c=US"), "*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(4, "test.bsn",
				"test.location",
		"street = 9C\\, Avenue St. Drézéry, o=ACME, c=FR"), "*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(5, "test.bsn",
				"test.location", "dc=www, dc=acme, dc=com, c=US"), "*"));

		ap = new AdminPermission("(signer=cn=\\*,o=ACME,c=\\*)", "*");
		
		assertImplies(ap, new AdminPermission(newMockBundle(6, "test.bsn",
				"test.location", "cn = Daffy Duck , o = ACME , c = US"), "*"));
		assertImplies(ap, new AdminPermission(newMockBundle(7, "test.bsn",
				"test.location", "cn=Road Runner, o=ACME, c=NL"), "*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(8, "test.bsn",
				"test.location", "o=ACME, c=NL"), "*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(9, "test.bsn",
				"test.location", "dc=acme.com, cn=Bugs Bunny, o=ACME, c=US"),
				"*"));

		// test bad signer filter that does not escape '*'
		ap = new AdminPermission("(signer=*, o=ACME, c=US)", "*");
		assertNotImplies(ap, new AdminPermission(newMockBundle(1, "test.bsn",
				"test.location", "cn=Bugs Bunny, o=ACME, c=US"), "*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(2, "test.bsn",
				"test.location", "ou = Carrots, cn=Daffy Duck, o=ACME, c=US"),
		"*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(3, "test.bsn",
				"test.location", "dc=www,dc=acme,dc=com,o=ACME,c=US"), "*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(4, "test.bsn",
				"test.location",
		"street = 9C\\, Avenue St. Drézéry, o=ACME, c=FR"), "*"));
		assertNotImplies(ap, new AdminPermission(newMockBundle(5, "test.bsn",
				"test.location", "dc=www, dc=acme, dc=com, c=US"), "*"));
	}
	
	public void testActionImplications() {
		AdminPermission classx = new AdminPermission("*", "class");
		AdminPermission execute = new AdminPermission("*", "execute");
		AdminPermission extensionLifecycle = new AdminPermission("*",
				"extensionLifecycle");
		AdminPermission lifecycle = new AdminPermission("*", "lifecycle");
		AdminPermission listener = new AdminPermission("*", "listener");
		AdminPermission metadata = new AdminPermission("*", "metadata");
		AdminPermission resolve = new AdminPermission("*", "resolve");
		AdminPermission resource = new AdminPermission("*", "resource");
		AdminPermission startlevel = new AdminPermission("*", "startlevel");
		AdminPermission context = new AdminPermission("*", "context");

		assertEquals("class,resolve", classx.getActions());
		assertEquals("execute,resolve", execute.getActions());
		assertEquals("extensionLifecycle", extensionLifecycle.getActions());
		assertEquals("lifecycle", lifecycle.getActions());
		assertEquals("listener", listener.getActions());
		assertEquals("metadata", metadata.getActions());
		assertEquals("resolve", resolve.getActions());
		assertEquals("resolve,resource", resource.getActions());
		assertEquals("startlevel", startlevel.getActions());
		assertEquals("context", context.getActions());

		assertImplies(classx, classx);
		assertNotImplies(classx, execute);
		assertNotImplies(classx, extensionLifecycle);
		assertNotImplies(classx, lifecycle);
		assertNotImplies(classx, listener);
		assertNotImplies(classx, metadata);
		assertImplies(classx, resolve);
		assertNotImplies(classx, resource);
		assertNotImplies(classx, startlevel);
		assertNotImplies(classx, context);

		assertNotImplies(execute, classx);
		assertImplies(execute, execute);
		assertNotImplies(execute, extensionLifecycle);
		assertNotImplies(execute, lifecycle);
		assertNotImplies(execute, listener);
		assertNotImplies(execute, metadata);
		assertImplies(execute, resolve);
		assertNotImplies(execute, resource);
		assertNotImplies(execute, startlevel);
		assertNotImplies(execute, context);
		
		assertNotImplies(extensionLifecycle, classx);
		assertNotImplies(extensionLifecycle, execute);
		assertImplies(extensionLifecycle, extensionLifecycle);
		assertNotImplies(extensionLifecycle, lifecycle);
		assertNotImplies(extensionLifecycle, listener);
		assertNotImplies(extensionLifecycle, metadata);
		assertNotImplies(extensionLifecycle, resolve);
		assertNotImplies(extensionLifecycle, resource);
		assertNotImplies(extensionLifecycle, startlevel);
		assertNotImplies(extensionLifecycle, context);
		
		assertNotImplies(lifecycle, classx);
		assertNotImplies(lifecycle, execute);
		assertNotImplies(lifecycle, extensionLifecycle);
		assertImplies(lifecycle, lifecycle);
		assertNotImplies(lifecycle, listener);
		assertNotImplies(lifecycle, metadata);
		assertNotImplies(lifecycle, resolve);
		assertNotImplies(lifecycle, resource);
		assertNotImplies(lifecycle, startlevel);
		assertNotImplies(lifecycle, context);
		
		assertNotImplies(listener, classx);
		assertNotImplies(listener, execute);
		assertNotImplies(listener, extensionLifecycle);
		assertNotImplies(listener, lifecycle);
		assertImplies(listener, listener);
		assertNotImplies(listener, metadata);
		assertNotImplies(listener, resolve);
		assertNotImplies(listener, resource);
		assertNotImplies(listener, startlevel);
		assertNotImplies(listener, context);
		
		assertNotImplies(metadata, classx);
		assertNotImplies(metadata, execute);
		assertNotImplies(metadata, extensionLifecycle);
		assertNotImplies(metadata, lifecycle);
		assertNotImplies(metadata, listener);
		assertImplies(metadata, metadata);
		assertNotImplies(metadata, resolve);
		assertNotImplies(metadata, resource);
		assertNotImplies(metadata, startlevel);
		assertNotImplies(metadata, context);
		
		assertNotImplies(resolve, classx);
		assertNotImplies(resolve, execute);
		assertNotImplies(resolve, extensionLifecycle);
		assertNotImplies(resolve, lifecycle);
		assertNotImplies(resolve, listener);
		assertNotImplies(resolve, metadata);
		assertImplies(resolve, resolve);
		assertNotImplies(resolve, resource);
		assertNotImplies(resolve, startlevel);
		assertNotImplies(resolve, context);
		
		assertNotImplies(resource, classx);
		assertNotImplies(resource, execute);
		assertNotImplies(resource, extensionLifecycle);
		assertNotImplies(resource, lifecycle);
		assertNotImplies(resource, listener);
		assertNotImplies(resource, metadata);
		assertImplies(resource, resolve);
		assertImplies(resource, resource);
		assertNotImplies(resource, startlevel);
		assertNotImplies(resource, context);
		
		assertNotImplies(startlevel, classx);
		assertNotImplies(startlevel, execute);
		assertNotImplies(startlevel, extensionLifecycle);
		assertNotImplies(startlevel, lifecycle);
		assertNotImplies(startlevel, listener);
		assertNotImplies(startlevel, metadata);
		assertNotImplies(startlevel, resolve);
		assertNotImplies(startlevel, resource);
		assertImplies(startlevel, startlevel);
		assertNotImplies(startlevel, context);
		
		assertNotImplies(context, classx);
		assertNotImplies(context, execute);
		assertNotImplies(context, extensionLifecycle);
		assertNotImplies(context, lifecycle);
		assertNotImplies(context, listener);
		assertNotImplies(context, metadata);
		assertNotImplies(context, resolve);
		assertNotImplies(context, resource);
		assertNotImplies(context, startlevel);
		assertImplies(context, context);
	}

	private static void invalidAdminPermission(String name, String actions) {
		try {
			AdminPermission p = new AdminPermission(name, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
	
	private static void invalidAdminPermission(Bundle bundle, String actions) {
		try {
			AdminPermission p = new AdminPermission(bundle, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}
