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

import org.osgi.service.subsystem.Subsystem;

public class ExplicitResourceSubsystemTests extends SubsystemTest{

	public void test6A1_applications() {
		doTest6A1(SUBSYSTEM_6_EMPTY_APPLICATION_A, SUBSYSTEM_6_EMPTY_APPLICATION_B);
	}

	public void test6A1_composites() {
		doTest6A1(SUBSYSTEM_6_EMPTY_COMPOSITE_A, SUBSYSTEM_6_EMPTY_COMPOSITE_B);
	}

	private void doTest6A1(String s1Name, String s2Name) {
		Subsystem root = getRootSubsystem();
		Subsystem s1 = doSubsystemInstall(getName() + ":s1", root, "s1", s1Name, false);
		Subsystem s2 = doSubsystemInstall(getName() + ":s2", root, "s2", s2Name, false);

		doSubsystemOperation(getName() + ":s1", s1, Operation.START, false);
		doSubsystemOperation(getName() + ":s2", s2, Operation.START, false);

		doBundleInstall(getName() + ":s1", s1.getBundleContext(), "X", BUNDLE_NO_DEPS_A_V1, false);
		doBundleInstall(getName() + ":s2", s2.getBundleContext(), "X", BUNDLE_NO_DEPS_A_V1, true);
	}

	public void test6A2_applications() {
		doTest6A2(SUBSYSTEM_6_EMPTY_APPLICATION_A, SUBSYSTEM_6_EMPTY_APPLICATION_B);
	}

	public void test6A2_composites() {
		doTest6A2(SUBSYSTEM_6_EMPTY_COMPOSITE_A, SUBSYSTEM_6_EMPTY_COMPOSITE_B);
	}

	private void doTest6A2(String s1Name, String s2Name) {
		Subsystem root = getRootSubsystem();
		Subsystem s1 = doSubsystemInstall(getName() + ":s1", root, "s1", s1Name, false);
		Subsystem s2 = doSubsystemInstall(getName() + ":s2", root, "s2", s2Name, false);

		doSubsystemOperation(getName() + ":s1", s1, Operation.START, false);
		doSubsystemOperation(getName() + ":s2", s2, Operation.START, false);

		doBundleInstall(getName() + ":s1", s1.getBundleContext(), "X", BUNDLE_NO_DEPS_A_V1, false);
		doBundleInstall(getName() + ":s2", s2.getBundleContext(), "Y", BUNDLE_NO_DEPS_A_V1, false);
	}

	public void test6A3_applications() {
		doTest6A3(SUBSYSTEM_6A3_APPLICATION);
	}

	public void test6A3_composites() {
		doTest6A3(SUBSYSTEM_6A3_COMPOSITE);
	}

	public void test6A3_features() {
		doTest6A3(SUBSYSTEM_6A3_FEATURE);
	}

	private void doTest6A3(String s1Name) {
		Subsystem root = getRootSubsystem();
		Subsystem s1 = doSubsystemInstall(getName() + ":s1", root, "s1", s1Name, false);

		doSubsystemOperation(getName() + ":s1", s1, Operation.START, false);

		doBundleInstall(getName() + ":s1", s1.getBundleContext(), getName() + ":X", BUNDLE_NO_DEPS_A_V1, true);
	}
}
