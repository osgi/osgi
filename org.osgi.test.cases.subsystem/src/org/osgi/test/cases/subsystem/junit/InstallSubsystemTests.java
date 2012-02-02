/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

import java.util.Collection;

import org.osgi.framework.resource.Resource;
import org.osgi.service.subsystem.Subsystem;


public class InstallSubsystemTests extends SubsystemTest{

	// TestPlan item 2A1
	public void testNoContentSubsystem() {
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_A_EMPTY_V1, false);
		Collection<Resource> constituents = subsystem.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is one constituent because that is the context bundle for the subsystem
		assertEquals("Wrong number of constituents.", 1, constituents.size());
	}

	// TestPlan item 2A2
	public void testIncementingIDs() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY_V1, false);
		long aID = a.getSubsystemId();
		assertTrue("Subsystem id is not > 0: " + aID, aID > 0);
		Subsystem b = doSubsystemInstall(getName() + ":b", root, "b", SUBSYSTEM_B_EMPTY_V1, false);
		long bID = b.getSubsystemId();
		assertTrue("Subsystem ids !(a < b): " + aID + ":" + bID, aID < bID);
		doSubsystemOperation("Uninstall 'a'", a, SubsystemOperation.UNINSTALL, false);
		doSubsystemOperation("Uninstall 'b'", b, SubsystemOperation.UNINSTALL, false);

		long lastID = bID;

		a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY_V1, false);
		aID = a.getSubsystemId();
		assertTrue("Subsystem id is not > lastID: " + aID, aID > lastID);
		b = doSubsystemInstall(getName() + ":b", root, "b", SUBSYSTEM_B_EMPTY_V1, false);
		bID = b.getSubsystemId();
		assertTrue("Subsystem ids !(a < b): " + aID + ":" + bID, aID < bID);
	}

	// TestPlan item 2A3
	public void testSameLocationInstall() {
		Subsystem root = getRootSubsystem();
		Subsystem a1 = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY_V1, false);
		Subsystem a2 = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY_V1, false);
		assertEquals("Use of same location to install returned different subsystems.", a1, a2);
	}
}
