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
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.resource.Resource;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;

public class RootSubsystemTests extends SubsystemTest{
	public  static final String ROOT_SYMBOLIC_NAME = "org.osgi.service.subsystem.root";
	public static final Version ROOT_VERSION = new Version(1,0,0);
	public static final long ROOT_ID = 0;
	public static final String ROOT_LOCATION = "subsystem://?Subsystem-SymbolicName=org.osgi.service.subsystem.root&Subsystem-Version=1.0.0";

	// TestPlan item 1A Subsystem object
	public void testRootSubsystemExists() {
		Subsystem root = getRootSubsystem();
		checkSubsystemProperties(root, "root", ROOT_SYMBOLIC_NAME, ROOT_VERSION, SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, ROOT_ID, ROOT_LOCATION, State.ACTIVE);
		assertNotNull("The root subsystem parents must not be null.", root.getParents());
		assertTrue("The root subsystem parents must be empty.", root.getParents().isEmpty());
		Map<String, String> rootHeaders = root.getSubsystemHeaders(null);
		assertNotNull("The root headers are null.", rootHeaders);
		assertEquals("The root Subsystem-SymbolicName header is wrong.", ROOT_SYMBOLIC_NAME, rootHeaders.get(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME));
		assertEquals("The root Subsystem-Version header is wrong.", ROOT_VERSION.toString(), rootHeaders.get(SubsystemConstants.SUBSYSTEM_VERSION));
		String rootType = rootHeaders.get(SubsystemConstants.SUBSYSTEM_TYPE);
		if (rootType != null) {
			assertEquals("The root Subsystem-Type header is wrong.", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, rootType);
		}
	}

	// TestPlan item 1A Subsystem service properties
	public void testRootSubsystemServiceProperties() {
		ServiceReference<Subsystem> rootReference = rootSubsystem.getServiceReference();
		checkSubsystemProperties(rootReference, "root", ROOT_SYMBOLIC_NAME, ROOT_VERSION, SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, ROOT_ID, State.ACTIVE);
	}

	// TestPlan item 1B
	public void testRootConstituents() {
		Subsystem root = getRootSubsystem();
		Collection<Resource> rootConstituents = root.getConstituents();
		assertNotNull("Root constituents is null.", rootConstituents);
		assertEquals("Wrong number of root constituents.", initialRootConstituents.size(), rootConstituents.size());
		checkBundleConstituents("root", initialRootConstituents, rootConstituents);
	}

	// TestPlan item 1C
	public void testRootContextBundle() {
		checkContextBundle("root", getRootSubsystem());
	}

	// TestPlan item 1D
	public void testInvalidRootOperations() {
		Subsystem root = getRootSubsystem();
		doSubsystemOperation("root", root, Operation.STOP, true);
		doSubsystemOperation("root", root, Operation.UNINSTALL, true);
	}
}
