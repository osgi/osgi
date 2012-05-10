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
import java.util.List;

import org.osgi.framework.Version;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;
import org.osgi.service.subsystem.Subsystem;


public class DeploymentManifestTests extends SubsystemTest{

	public void test5A1() {
		doTest5A(SUBSYSTEM_5A_APPLICATION_S1);
	}

	public void test5A2() {
		doTest5A(SUBSYSTEM_5A_COMPOSITE_S1);
	}

	public void test5A3() {
		doTest5A(SUBSYSTEM_5A_FEATURE_S1);
	}

	private void doTest5A(String subsystemName) {
		registerRepository(REPOSITORY_NODEPS_V2);
		registerRepository(REPOSITORY_NODEPS);
		Subsystem root = getRootSubsystem();

		Subsystem s1 = doSubsystemInstall("install s1", root, "s1", subsystemName, false);
		Collection<Resource> constituents = s1.getConstituents();
		Version v1 = new Version(1, 0, 0);
		for (Resource resource : constituents) {
			List<Capability> identities = resource.getCapabilities(IdentityNamespace.IDENTITY_NAMESPACE);
			assertEquals("No identity.", 1, identities.size());
			Version actual = (Version) identities.get(0).getAttributes().get(IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE);
			assertEquals("Wrong version for resource: " + resource, v1, actual);
		}
	}
}
