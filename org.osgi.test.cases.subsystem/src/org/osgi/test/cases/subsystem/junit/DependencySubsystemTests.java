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
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Resource;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.test.cases.subsystem.resource.TestResource;


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
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = doSubsystemInstall(getName(), root, getName(), subsystemName, false);
		Collection<Resource> constituents = subsystem.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there should be the context + 6 (A, B, C, D, E) bundles
		assertEquals("Wrong number of constituents.", 6, constituents.size());

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
	}

	private void checkWiring(Bundle packageExporter, Bundle bundleProvider, Bundle capabilityProvider, Bundle packageImporter, Bundle bundleRequirer, Bundle CapabilityRequirer) {
		BundleWiring pExporterWiring = packageExporter.adapt(BundleRevision.class).getWiring();
		BundleWiring bProviderWiring = bundleProvider.adapt(BundleRevision.class).getWiring();
		BundleWiring cProviderWiring = capabilityProvider.adapt(BundleRevision.class).getWiring();
		BundleWiring pImporterWiring = packageImporter.adapt(BundleRevision.class).getWiring();
		BundleWiring bRequirerWiring = bundleRequirer.adapt(BundleRevision.class).getWiring();
		BundleWiring cRequirerWiring = CapabilityRequirer.adapt(BundleRevision.class).getWiring();

		List<BundleWire> pWires = pImporterWiring.getRequiredWires(null);
		assertEquals("Wring number of packages", 1, pWires.size());
		List<BundleWire> bWires = bRequirerWiring.getRequiredWires(null);
		assertEquals("Wring number of bundles", 1, bWires.size());
		List<BundleWire> cWires = cRequirerWiring.getRequiredWires(null);
		assertEquals("Wring number of capabilities", 1, cWires.size());

		assertEquals("Wrong package provider.", pExporterWiring, pWires.get(0).getProviderWiring());
		assertEquals("Wrong bundle provider.", bProviderWiring, bWires.get(0).getProviderWiring());
		assertEquals("Wrong capability provider.", cProviderWiring, cWires.get(0).getProviderWiring());
	}
}
