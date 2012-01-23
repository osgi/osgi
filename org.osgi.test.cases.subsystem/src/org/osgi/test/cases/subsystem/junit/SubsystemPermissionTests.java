/*
 * Copyright (c) OSGi Alliance (2009, 2011). All Rights Reserved.
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

package org.osgi.test.cases.subsystem.junit;

import java.security.PermissionCollection;

import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemPermission;
import org.osgi.test.support.MockFactory;
import org.osgi.test.support.PermissionTestCase;

public class SubsystemPermissionTests extends PermissionTestCase {

	/*
	 * @param actions <code>execute</code>, <code>lifecycle</code>,
	 * <code>metadata</code> or <code>context</code>.
	 * A value of "*" or <code>null</code> indicates all actions.
	 */
	public void testInvalid() {
		invalidSubsystemPermission("*", "*");
		invalidSubsystemPermission("*", "x");
		invalidSubsystemPermission("*", "   execute  ,  x   ");
		invalidSubsystemPermission("*", "");
		invalidSubsystemPermission("*", "      ");
		invalidSubsystemPermission("*", ",");
		invalidSubsystemPermission("*", ",xxx");
		invalidSubsystemPermission("*", "xxx,");
		invalidSubsystemPermission("*", "execute,");
		invalidSubsystemPermission("*", "lifecycleme,");
		invalidSubsystemPermission("*", ",metadata");
		invalidSubsystemPermission("*", "   executeme   ");
		invalidSubsystemPermission("*", "   lifecycleme     ");
		invalidSubsystemPermission("*", "   metada");
		invalidSubsystemPermission("*", "   execut");
		invalidSubsystemPermission("*", "   contex");

		invalidSubsystemPermission("com.acme", "execute");
		invalidSubsystemPermission("()", "execute");
		invalidSubsystemPermission((Subsystem) null, "execute");
	}

	public void testFilter() {
		SubsystemPermission p1 = new SubsystemPermission("(id=2)", "execute");
		SubsystemPermission p2 = new SubsystemPermission(" (id =2)", "execute");
		SubsystemPermission p3 = new SubsystemPermission(newMockSubsystem(2, "test.bsn",
				"test.location"), "execute");
		SubsystemPermission p4 = new SubsystemPermission("(name=test.*)", "execute");
		SubsystemPermission p5 = new SubsystemPermission("(location=test.*)",
				"execute,lifecycle,metadata,context");
		assertImplies(p1, p3);
		assertImplies(p2, p3);
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

		assertAddPermission(pc, new SubsystemPermission("(id=2)", "execute"));
		assertAddPermission(pc, new SubsystemPermission("(id=2)", "lifecycle"));
		assertAddPermission(pc, new SubsystemPermission("(id=2)", "metadata"));
		assertAddPermission(pc, new SubsystemPermission("(id=2)", "context"));

		Subsystem testBundle1 = newMockSubsystem(2, "test.bsn", "test.location");
		Subsystem testBundle2 = newMockSubsystem(1, "test.bsn", "test.location");
		assertImplies(pc, new SubsystemPermission(testBundle1, "execute"));
		assertImplies(pc, new SubsystemPermission(testBundle1, "lifecycle"));
		assertImplies(pc, new SubsystemPermission(testBundle1, "metadata"));
		assertImplies(pc, new SubsystemPermission(testBundle1, "context"));
		assertNotImplies(pc, new SubsystemPermission(testBundle2, "execute"));
		assertNotImplies(pc, new SubsystemPermission(testBundle2, "lifecycle"));
		assertNotImplies(pc, new SubsystemPermission(testBundle2, "metadata"));
		assertNotImplies(pc, new SubsystemPermission(testBundle2, "context"));
		assertNotImplies(pc, new SubsystemPermission("*", "execute"));
		assertNotImplies(pc, new SubsystemPermission(" *", "lifecycle"));
		assertNotImplies(pc, new SubsystemPermission("* ", "metadata"));
		assertNotImplies(pc, new SubsystemPermission(" * ", "context"));

		assertAddPermission(pc, new SubsystemPermission("*",
				"execute,lifecycle,metadata,context"));
		assertImplies(pc, new SubsystemPermission(testBundle1, "execute"));
		assertImplies(pc, new SubsystemPermission(testBundle1, "lifecycle"));
		assertImplies(pc, new SubsystemPermission(testBundle1, "metadata"));
		assertImplies(pc, new SubsystemPermission(testBundle1, "context"));
		assertImplies(pc, new SubsystemPermission(testBundle2, "execute"));
		assertImplies(pc, new SubsystemPermission(testBundle2, "lifecycle"));
		assertImplies(pc, new SubsystemPermission(testBundle2, "metadata"));
		assertImplies(pc, new SubsystemPermission(testBundle2, "context"));
		assertImplies(pc, new SubsystemPermission("*", "execute"));
		assertImplies(pc, new SubsystemPermission(" *", "lifecycle"));
		assertImplies(pc, new SubsystemPermission("* ", "metadata"));
		assertImplies(pc, new SubsystemPermission(" * ", "context"));

		assertNotImplies(pc, p1);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p1);
		assertSerializable(p2);
		assertNotSerializable(p3);
		assertSerializable(p4);
		assertSerializable(p5);
		assertSerializable(pc);
	}

	public void testActionImplications() {
		SubsystemPermission execute = new SubsystemPermission("*", "EXECUTE");
		SubsystemPermission lifecycle = new SubsystemPermission("*", "LIFECYCLE");
		SubsystemPermission metadata = new SubsystemPermission("*", "METADATA");
		SubsystemPermission context = new SubsystemPermission("*", "CONTEXT");

		assertEquals("execute", execute.getActions());
		assertEquals("lifecycle", lifecycle.getActions());
		assertEquals("metadata", metadata.getActions());
		assertEquals("context", context.getActions());

		execute = new SubsystemPermission(execute.getName(), execute.getActions());
		lifecycle = new SubsystemPermission(lifecycle.getName(),
				lifecycle.getActions());
		metadata = new SubsystemPermission(metadata.getName(),
				metadata.getActions());
		context = new SubsystemPermission(context.getName(), context.getActions());


		assertImplies(execute, execute);
		assertNotImplies(execute, lifecycle);
		assertNotImplies(execute, metadata);
		assertNotImplies(execute, context);


		assertNotImplies(lifecycle, execute);
		assertImplies(lifecycle, lifecycle);
		assertNotImplies(lifecycle, metadata);
		assertNotImplies(lifecycle, context);

		assertNotImplies(metadata, execute);
		assertNotImplies(metadata, lifecycle);
		assertImplies(metadata, metadata);
		assertNotImplies(metadata, context);

		assertNotImplies(context, execute);
		assertNotImplies(context, lifecycle);
		assertNotImplies(context, metadata);
		assertImplies(context, context);

		PermissionCollection pc = context.newPermissionCollection();
		SubsystemPermission ap = new SubsystemPermission("(id=2)", "metadata");
		SubsystemPermission all = new SubsystemPermission(newMockSubsystem(2, "test.bsn",
				"test.location"), "metadata,context");

		assertAddPermission(pc, context);
		assertAddPermission(pc, ap);
		assertImplies(pc, all);
	}

	private static void invalidSubsystemPermission(String name, String actions) {
		try {
			SubsystemPermission p = new SubsystemPermission(name, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	private static void invalidSubsystemPermission(Subsystem subsystem, String actions) {

		try {
			SubsystemPermission p = new SubsystemPermission(subsystem, actions);
			fail(p + " created with invalid arguments");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public static Subsystem newMockSubsystem(long id, String name, String location) {

		return MockFactory.newMock(Subsystem.class, new MockSubsystem(id,
				name, location));
	}

	public static class MockSubsystem {
		private final long		id;
		private final String	name;
		private final String	location;

		MockSubsystem(long id, String name, String location) {
			this.id = id;
			this.name = name;
			this.location = location;
		}

		public long getSubsystemId() {
			return id;
		}

		public String getLocation() {
			return location;
		}

		public String getSymbolicName() {
			return name;
		}
	}
}
