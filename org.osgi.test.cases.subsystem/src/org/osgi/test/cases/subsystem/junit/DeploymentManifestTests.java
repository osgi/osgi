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

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
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

		Version v1 = new Version(1, 0, 0);
		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		Bundle b = getBundle(s1, BUNDLE_NO_DEPS_B_V1);

		assertEquals("Wrong version for bundle: " + a, v1, a.getVersion());
		assertEquals("Wrong version for bundle: " + b, v1, b.getVersion());
	}

	public void test5B1() {
		doTest5B(SUBSYSTEM_5B_APPLICATION_S1);
	}

	public void test5B2() {
		doTest5B(SUBSYSTEM_5B_COMPOSITE_S1);
	}

	public void test5B3() {
		doTest5B(SUBSYSTEM_5B_FEATURE_S1);
	}

	private void doTest5B(String subsystemName) {
		registerRepository(REPOSITORY_1);
		Subsystem root = getRootSubsystem();

		assertNoBundle(root, BUNDLE_SHARE_A);
		assertNoBundle(root, BUNDLE_SHARE_B);
		assertNoBundle(root, BUNDLE_SHARE_F);
		assertNoBundle(root, BUNDLE_SHARE_G);

		doSubsystemInstall("install s1", root, "s1", subsystemName, false);

		assertNoBundle(root, BUNDLE_SHARE_A);
		assertNoBundle(root, BUNDLE_SHARE_B);

		assertNotNull("F is not installed in root.", getBundle(root, BUNDLE_SHARE_F));
		assertNotNull("G is not installed in root.", getBundle(root, BUNDLE_SHARE_G));
	}
}
