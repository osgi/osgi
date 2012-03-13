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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.namespace.BundleNamespace;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Resource;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;


public class DependencySubsystemTests extends SubsystemTest{

	// TestPlan item 4A application
	public void test4A_application() {
		doTest4A(SUBSYSTEM_4A_APPLICATION);
	}

	// TestPlan item 4A composites
	public void test4A_composite() {
		doTest4A(SUBSYSTEM_4A_COMPOSITE);
	}

	// TestPlan item 4A features
	public void test4A_feature() {
		doTest4A(SUBSYSTEM_4A_FEATURE);
	}

	private void doTest4A(String subsystemName) {
		registerRepository(REPOSITORY_1);
		Subsystem root = getRootSubsystem();
		Collection<Resource> origRootConstituents = root.getConstituents();

		Subsystem subsystem = doSubsystemInstall(getName(), root, getName(), subsystemName, false);

		Collection<Resource> constituents = subsystem.getConstituents();
		assertNotNull("Null constituents.", constituents);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(subsystem.getType())) {
			// there should be 5 (A, B, C, D, E) bundles
			assertEquals("Wrong number of constituents.", 5, constituents.size());
		} else {
			// there should be the context + 6 (A, B, C, D, E) bundles
			assertEquals("Wrong number of constituents.", 6, constituents.size());
		}
		doSubsystemOperation("start application", subsystem, Operation.START, false);
		Bundle a = getBundle(subsystem, BUNDLE_SHARE_A);
		Bundle b = getBundle(subsystem, BUNDLE_SHARE_B);
		Bundle c = getBundle(subsystem, BUNDLE_SHARE_C);
		Bundle d = getBundle(subsystem, BUNDLE_SHARE_D);
		Bundle e = getBundle(subsystem, BUNDLE_SHARE_E);

		for (Bundle bundle : new Bundle[] {a, b, c, d, e} ) {
			assertEquals("Wrong state for the bundle: " + bundle.getSymbolicName(), Bundle.ACTIVE, bundle.getState());
		}
		checkWiring(a, a, b, c, d, e);

		Collection<Resource> newRootconstituents = new ArrayList<Resource>(root.getConstituents());
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(subsystem.getType())) {
			newRootconstituents.removeAll(constituents);
		}
		newRootconstituents.removeAll(origRootConstituents);
		assertEquals("Found unexpected root constituents", 1, newRootconstituents.size());
		checkSubsystemConstituents("The subsystem is not a constituent of its parent.", Arrays.asList(subsystem), newRootconstituents);

	}

	// TestPlan item 4B application
	public void test4B_application() {
		doTest4B(SUBSYSTEM_4B_APPLICATION);
	}

	// TestPlan item 4B composites
	public void test4B_composite() {
		doTest4B(SUBSYSTEM_4B_COMPOSITE);
	}

	// TestPlan item 4B features
	public void test4B_feature() {
		doTest4B(SUBSYSTEM_4B_FEATURE);
	}

	private void doTest4B(String subsystemName) {
		registerRepository(REPOSITORY_2);
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = doSubsystemInstall(getName(), root, getName(), subsystemName, false);

		Collection<Resource> constituents = subsystem.getConstituents();
		assertNotNull("Null constituents.", constituents);
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(subsystem.getType())) {
			// there should be 3 (C, D, E) bundles
			assertEquals("Wrong number of constituents.", 3, constituents.size());
		} else {
			// there should be the context + 3 (C, D, E) bundles
			assertEquals("Wrong number of constituents.", 4, constituents.size());
		}

		doSubsystemOperation("start application", subsystem, Operation.START, false);
		Bundle a = getBundle(root, BUNDLE_SHARE_A);
		Bundle b = getBundle(root, BUNDLE_SHARE_B);
		Bundle c = getBundle(subsystem, BUNDLE_SHARE_C);
		Bundle d = getBundle(subsystem, BUNDLE_SHARE_D);
		Bundle e = getBundle(subsystem, BUNDLE_SHARE_E);

		for (Bundle bundle : new Bundle[] {a, b, c, d, e} ) {
			assertEquals("Wrong state for the bundle: " + bundle.getSymbolicName(), Bundle.ACTIVE, bundle.getState());
		}
		checkWiring(a, a, b, c, d, e);
	}

	// TestPlan item 4C application + application
	public void test4C_application1() {
		doTest4C(SUBSYSTEM_4C_APPLICATION, SUBSYSTEM_4B_APPLICATION);
	}

	// TestPlan item 4C application + composite
	public void test4C_application2() {
		doTest4C(SUBSYSTEM_4C_APPLICATION, SUBSYSTEM_4B_COMPOSITE);
	}
	// TestPlan item 4C application + feature
	public void test4C_application3() {
		doTest4C(SUBSYSTEM_4C_APPLICATION, SUBSYSTEM_4B_FEATURE);
	}

	// TestPlan item 4C composite + application
	public void test4C_composite1() {
		doTest4C(SUBSYSTEM_4C_COMPOSITE, SUBSYSTEM_4B_APPLICATION);
	}

	// TestPlan item 4C composite + composite
	public void test4C_composite2() {
		doTest4C(SUBSYSTEM_4C_COMPOSITE, SUBSYSTEM_4B_COMPOSITE);
	}
	// TestPlan item 4C composite + feature
	public void test4C_composite3() {
		doTest4C(SUBSYSTEM_4C_APPLICATION, SUBSYSTEM_4B_FEATURE);
	}

	private void doTest4C(String subsystemName1, String subsystemName2) {
		registerRepository(REPOSITORY_1);
		Subsystem root = getRootSubsystem();
		Collection<Resource> origRootConstituents = root.getConstituents();

		Subsystem subsystem1 = doSubsystemInstall(getName(), root, subsystemName1, subsystemName1, false);

		Collection<Resource> constituents1 = subsystem1.getConstituents();
		assertNotNull("Null constituents.", constituents1);
		// there should be the context + 2 (A, B) bundles
		assertEquals("Wrong number of constituents.", 3, constituents1.size());

		Subsystem subsystem2 = doSubsystemInstall(getName(), subsystem1, subsystemName2, subsystemName2, false);
		Collection<Resource> constituents2 = subsystem2.getConstituents();
		if (SubsystemConstants.SUBSYSTEM_TYPE_FEATURE.equals(subsystem2.getType())) {
			// there should be 3 (C, D, E) bundles
			assertEquals("Wrong number of constituents.", 3, constituents2.size());
		} else {
			// there should be the context + 3 (C, D, E) bundles
			assertEquals("Wrong number of constituents.", 4, constituents2.size());
		}

		doSubsystemOperation("start application", subsystem1, Operation.START, false);
		Bundle a = getBundle(subsystem1, BUNDLE_SHARE_A);
		Bundle b = getBundle(subsystem1, BUNDLE_SHARE_B);
		Bundle c = getBundle(subsystem2, BUNDLE_SHARE_C);
		Bundle d = getBundle(subsystem2, BUNDLE_SHARE_D);
		Bundle e = getBundle(subsystem2, BUNDLE_SHARE_E);

		for (Bundle bundle : new Bundle[] {a, b, c, d, e} ) {
			assertEquals("Wrong state for the bundle: " + bundle.getSymbolicName(), Bundle.ACTIVE, bundle.getState());
		}
		checkWiring(a, a, b, c, d, e);

		Collection<Resource> newRootconstituents = new ArrayList<Resource>(root.getConstituents());
		newRootconstituents.removeAll(origRootConstituents);
		assertEquals("Found unexpected root constituents", 1, newRootconstituents.size());
		checkSubsystemConstituents("The subsystem is not a constituent of its parent.", Arrays.asList(subsystem1), newRootconstituents);
	}

	private void checkWiring(Bundle packageExporter, Bundle bundleProvider, Bundle capabilityProvider, Bundle packageImporter, Bundle bundleRequirer, Bundle CapabilityRequirer) {
		BundleWiring pExporterWiring = packageExporter.adapt(BundleRevision.class).getWiring();
		BundleWiring bProviderWiring = bundleProvider.adapt(BundleRevision.class).getWiring();
		BundleWiring cProviderWiring = capabilityProvider.adapt(BundleRevision.class).getWiring();
		BundleWiring pImporterWiring = packageImporter.adapt(BundleRevision.class).getWiring();
		BundleWiring bRequirerWiring = bundleRequirer.adapt(BundleRevision.class).getWiring();
		BundleWiring cRequirerWiring = CapabilityRequirer.adapt(BundleRevision.class).getWiring();

		List<BundleWire> pWires = pImporterWiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wring number of packages", 1, pWires.size());
		List<BundleWire> bWires = bRequirerWiring.getRequiredWires(BundleNamespace.BUNDLE_NAMESPACE);
		assertEquals("Wring number of bundles", 1, bWires.size());
		List<BundleWire> cWires = cRequirerWiring.getRequiredWires(null);
		assertEquals("Wring number of capabilities", 1, cWires.size());

		assertEquals("Wrong package provider.", pExporterWiring, pWires.get(0).getProviderWiring());
		assertEquals("Wrong bundle provider.", bProviderWiring, bWires.get(0).getProviderWiring());
		assertEquals("Wrong capability provider.", cProviderWiring, cWires.get(0).getProviderWiring());
	}
}
