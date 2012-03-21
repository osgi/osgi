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

import java.util.Arrays;
import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.resource.Resource;
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
		doTest6A3(SUBSYSTEM_6A_FEATURE1);
	}

	private void doTest6A3(String s1Name) {
		Subsystem root = getRootSubsystem();
		Subsystem s1 = doSubsystemInstall(getName() + ":s1", root, "s1", s1Name, false);

		doSubsystemOperation(getName() + ":s1", s1, Operation.START, false);

		doBundleInstall(getName() + ":s1", s1.getBundleContext(), getName() + ":X", BUNDLE_NO_DEPS_A_V1, true);
	}

	public void test6A4a_application() {
		doTest6A4a(SUBSYSTEM_6A4_APPLICATION1);
	}

	public void test6A4a_composite() {
		doTest6A4a(SUBSYSTEM_6A4_COMPOSITE1);
	}

	private void doTest6A4a(String s1Name) {
		Subsystem root = getRootSubsystem();
		Subsystem s1 = doSubsystemInstall(getName() + ":s1", root, "s1", s1Name, false);
		doSubsystemOperation(getName() + ":s1", s1, Operation.START, false);

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		BundleContext aContext = a.getBundleContext();
		assertNotNull("Context for bundle a is null.", aContext);
		Bundle b = doBundleInstall("shared bundle b", aContext, "b", BUNDLE_NO_DEPS_B_V1, false);

		Collection<Subsystem> children = s1.getChildren();
		assertEquals("Wrong number of children", 2, children.size());
		for (Subsystem child : children) {
			Collection<Resource> constituents = child.getConstituents();
			assertEquals("Wrong number of constituents: " + child.getSymbolicName(), 2, constituents.size());
			checkBundleConstituents(child.getSymbolicName(), Arrays.asList(a, b), constituents);
		}
	}

	public void test6A4b_application() {
		doTest6A4b(SUBSYSTEM_6A4_APPLICATION2);
	}

	public void test6A4b_composite() {
		doTest6A4b(SUBSYSTEM_6A4_COMPOSITE2);
	}
	private void doTest6A4b(String s1Name) {
		Subsystem root = getRootSubsystem();
		Subsystem s1 = doSubsystemInstall(getName() + ":s1", root, "s1", s1Name, false);
		doSubsystemOperation(getName() + ":s1", s1, Operation.START, false);

		Bundle a = getBundle(s1, BUNDLE_NO_DEPS_A_V1);
		BundleContext aContext = a.getBundleContext();
		assertNotNull("Context for bundle a is null.", aContext);
		Bundle b = doBundleInstall("shared bundle b", aContext, "b", BUNDLE_NO_DEPS_B_V1, false);

		Collection<Subsystem> children = s1.getChildren();
		assertEquals("Wrong number of children", 1, children.size());
		Subsystem child = children.iterator().next();
		Collection<Resource> childConstituents = child.getConstituents();
		assertEquals("Wrong number of constituents: " + child.getSymbolicName(), 2, childConstituents.size());
		checkBundleConstituents(child.getSymbolicName(), Arrays.asList(a, b), childConstituents);

		Collection<Resource> s1Constituents = s1.getConstituents();
		// should be the context bundle + feature + bundles a and b (4)
		assertEquals("Wrong number of constituents: " + s1.getSymbolicName(), 4, s1Constituents.size());
		checkBundleConstituents(s1.getSymbolicName(), Arrays.asList(a, b), s1Constituents);
	}
}
