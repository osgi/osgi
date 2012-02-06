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
import org.osgi.framework.resource.Resource;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;


public class InstallSubsystemTests extends SubsystemTest{

	// TestPlan item 2A1
	public void testNoContentSubsystem() {
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_A_EMPTY, false);
		Collection<Resource> constituents = subsystem.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is one constituent because that is the context bundle for the subsystem
		assertEquals("Wrong number of constituents.", 1, constituents.size());
	}

	// TestPlan item 2A2
	public void testIncementingIDs() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY, false);
		long aID = a.getSubsystemId();
		assertTrue("Subsystem id is not > 0: " + aID, aID > 0);
		Subsystem b = doSubsystemInstall(getName() + ":b", root, "b", SUBSYSTEM_B_EMPTY, false);
		long bID = b.getSubsystemId();
		assertTrue("Subsystem ids !(a < b): " + aID + ":" + bID, aID < bID);
		doSubsystemOperation("Uninstall 'a'", a, SubsystemOperation.UNINSTALL, false);
		doSubsystemOperation("Uninstall 'b'", b, SubsystemOperation.UNINSTALL, false);

		long lastID = bID;

		a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY, false);
		aID = a.getSubsystemId();
		assertTrue("Subsystem id is not > lastID: " + aID, aID > lastID);
		b = doSubsystemInstall(getName() + ":b", root, "b", SUBSYSTEM_B_EMPTY, false);
		bID = b.getSubsystemId();
		assertTrue("Subsystem ids !(a < b): " + aID + ":" + bID, aID < bID);
	}

	// TestPlan item 2A3
	public void testSameLocationInstall() {
		Subsystem root = getRootSubsystem();
		Subsystem a1 = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY, false);
		Subsystem a2 = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY, false);
		assertEquals("Use of same location to install returned different subsystems.", a1, a2);
	}

	// TestPlan item 2B1a
	public void testNoContentHeaderScoped() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_SCOPED_NO_CONTENT_HEADER, false);
		Collection<Resource> constituents = a.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is 3 + 1 constituent because there is the context bundle for the scoped subsystem
		assertEquals("Wrong number of constituents.", 4, constituents.size());
		BundleContext aContext = a.getBundleContext();
		Bundle[] aBundles = aContext.getBundles();
		checkBundleConstituents("Verify constituents of subsystem a.", Arrays.asList(aBundles), constituents);
	}

	// TestPlan item 2B1b install into empty scoped
	public void testNoContentHeaderUnscopedIntoEmpty() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY, false);

		// install a feature into the empty subsystem
		Subsystem b = doSubsystemInstall(getName() + ":b", a, "b", SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER, false);

		// verify a does not have new constituents
		Collection<Resource> aConstituents = a.getConstituents();
		assertNotNull("Null constituents for a", aConstituents);
		// there is 2 constituents because of the context bundle for the scoped subsystem and feature 'b'
		assertEquals("Wrong number of constituents.", 2, aConstituents.size());

		// verify b has constituents
		Collection<Resource> bConstituents = b.getConstituents();
		List<Bundle> bBundles = new ArrayList<Bundle>(Arrays.asList(b.getBundleContext().getBundles()));
		// remove the context bundle
		for (Iterator<Bundle> iBundles = bBundles.iterator(); iBundles.hasNext();) {
			if (("org.osgi.service.subsystem.region.context." + a.getSubsystemId()).equals(iBundles.next().getSymbolicName())) {
				iBundles.remove();
			}
		}
		checkBundleConstituents("Constiuents for feature b.", bBundles, bConstituents);
	}

	// TestPlan item 2B1b install into scoped subsystem with all the existing resources
	public void testNoContentHeaderUnscopedIntoFull() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_SCOPED_NO_CONTENT_HEADER, false);

		// install a feature into the empty subsystem
		Subsystem b = doSubsystemInstall(getName() + ":b", a, "b", SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER, false);

		// verify a does not have new constituents
		Collection<Resource> aConstituents = a.getConstituents();
		assertNotNull("Null constituents.", aConstituents);
		// there is 3 + 1 constituent because there is the context bundle for the scoped subsystem
		assertEquals("Wrong number of constituents.", 4, aConstituents.size());
		BundleContext aContext = a.getBundleContext();
		Bundle[] aBundles = aContext.getBundles();
		checkBundleConstituents("Verify constituents of subsystem a.", Arrays.asList(aBundles), aConstituents);

		// verify b has constituents
		Collection<Resource> bConstituents = b.getConstituents();
		List<Bundle> bBundles = new ArrayList<Bundle>(Arrays.asList(aBundles));
		// remove the context bundle
		for (Iterator<Bundle> iBundles = bBundles.iterator(); iBundles.hasNext();) {
			if (("org.osgi.service.subsystem.region.context." + a.getSubsystemId()).equals(iBundles.next().getSymbolicName())) {
				iBundles.remove();
			}
		}
		checkBundleConstituents("Constiuents for feature b.", bBundles, bConstituents);
	}

	// TestPlan item 2B1b embedded features that have shared resources
	public void testNoContentHeaderEmbeddedUnscoped() {
		Subsystem root = getRootSubsystem();
		Subsystem cSubsystem = doSubsystemInstall(getName(), root, "c", SUBSYSTEM_C_SCOPED_NO_CONTENT_HEADER, false);
		Collection<Subsystem> children = cSubsystem.getChildren();
		Subsystem bSubsystem = null;
		Subsystem dSubsystem = null;
		assertEquals("Wrong number of children: c", 2, children.size());
		for (Subsystem subsystem : children) {
			if (SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER.startsWith(subsystem.getSymbolicName())) {
				bSubsystem = subsystem;
			} else if (SUBSYSTEM_D_UNSCOPED_NO_CONTENT_HEADER.startsWith(subsystem.getSymbolicName())) {
				dSubsystem = subsystem;
			}
		}
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER, bSubsystem);
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_D_UNSCOPED_NO_CONTENT_HEADER, dSubsystem);

		Bundle[] allBundles = cSubsystem.getBundleContext().getBundles();
		assertEquals("Wrong number of constituent bundles.", 5, allBundles.length);
		Bundle aBundle = null;
		Bundle bBundle = null;
		Bundle cBundle = null;
		Bundle dBundle = null;
		for (Bundle bundle : allBundles) {
			if (BUNDLE_NO_DEPS_A_V1.startsWith(bundle.getSymbolicName())) {
				aBundle = bundle;
			} else if (BUNDLE_NO_DEPS_B_V1.startsWith(bundle.getSymbolicName())) {
				bBundle = bundle;
			} else if (BUNDLE_NO_DEPS_C_V1.startsWith(bundle.getSymbolicName())) {
				cBundle = bundle;
			} else if (BUNDLE_NO_DEPS_D_V1.startsWith(bundle.getSymbolicName())) {
				dBundle = bundle;
			}
		}
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_A_V1, aBundle);
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_B_V1, bBundle);
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_C_V1, cBundle);
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_D_V1, dBundle);

		Collection<Resource> cConstituents = cSubsystem.getConstituents();
		// 4 expected: context bundle, aBundle, bSubsystem, dSubsystem
		assertEquals("Wrong number of constituents: c", 4, cConstituents.size());
		checkBundleConstituents("Checking bundle constituents: c", Arrays.asList(aBundle, cSubsystem.getBundleContext().getBundle()), cConstituents);
		checkSubsystemConstituents("Checking subsystem constituents: c", children, cConstituents);

		Collection<Resource> bConstituents = bSubsystem.getConstituents();
		// 3 expected: aBundle, bBundle, cBundle
		assertEquals("Wrong number of constituents: b", 3, bConstituents.size());
		checkBundleConstituents("Checking bundle constituents: b", Arrays.asList(aBundle, bBundle, cBundle), bConstituents);

		Collection<Resource> dConstituents = dSubsystem.getConstituents();
		// 2 expected: cBundle, dBundle
		assertEquals("Wrong number of constituents: d", 2, dConstituents.size());
		checkBundleConstituents("Checking bundle constituents: d", Arrays.asList(cBundle, dBundle), bConstituents);
	}

	// TestPlan item 2C1 for composites
	public void testContextComposite() {
		Subsystem root = getRootSubsystem();
		Subsystem eSubsystem = doSubsystemInstall(getName() + ":e", root, "e", SUBSYSTEM_E_COMPOSITE_EMPTY, false);
		checkContextBundle(getName(), eSubsystem);
	}

	// TestPlan item 2C1 for applications
	public void testContextApplication() {
		Subsystem root = getRootSubsystem();
		Subsystem aSubsystem = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_A_EMPTY, false);
		checkContextBundle(getName(), aSubsystem);
	}

	// TestPlan item 2C2 for features
	public void testContextfeature() {
		Subsystem root = getRootSubsystem();
		Subsystem fSubsystem = doSubsystemInstall(getName() + ":f", root, "f", SUBSYSTEM_F_FEATURE_EMPTY, false);
		checkContextBundle(getName(), fSubsystem);
	}

	// TestPlan item 2D1a
	public void testURIDerivedSymbolicName() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Bundle-SymbolicName=foo", SUBSYSTEM_EMPTY, false);
		assertEquals("Wrong symbolic name.", "foo", derivedFoo.getSymbolicName());
	}

	// TestPlan item 2D1b
	public void testURLDerivedSymbolicNamePriority() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Bundle-SymbolicName=foo", SUBSYSTEM_A_EMPTY, false);
		assertEquals("Wrong symbolic name.", "a.empty", derivedFoo.getSymbolicName());
	}

	// TestPlan item 2D1c
	public void testURIDerivedVersion() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Bundle-SymbolicName=foo&Bundle-Version=2.0.0", SUBSYSTEM_EMPTY, false);
		assertEquals("Wrong version.", Version.parseVersion("2.0.0"), derivedFoo.getVersion());
	}

	// TestPlan item 2D1d
	public void testURLDerivedVersionPriority() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Bundle-Version=2.0.0", SUBSYSTEM_A_EMPTY, false);
		assertEquals("Wrong version.", Version.parseVersion("1.0.0"), derivedFoo.getVersion());
	}

	// TestPlan item 2D1e
	public void testURLDerivedEmbeddedURL() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://" + getEmbeddedURL(SUBSYSTEM_EMPTY) + "?Bundle-SymbolicName=foo&Bundle-Version=2.0.0", null, false);
		assertEquals("Wrong symbolic name.", "foo", derivedFoo.getSymbolicName());
		assertEquals("Wrong version.", Version.parseVersion("2.0.0"), derivedFoo.getVersion());
	}

	// TestPlan item 2D2 & 2D3b
	public void testEntryNameDerived() {
		Subsystem root = getRootSubsystem();
		Subsystem gSubsystem = doSubsystemInstall(getName(), root, "g", SUBSYSTEM_G_SCOPED_NO_CONTENT_HEADER, false);
		Collection<Subsystem> children = gSubsystem.getChildren();
		Subsystem fooSubsystem = null;
		Subsystem aSubsystem = null;
		for (Subsystem subsystem : children) {
			if ("foo".equals(subsystem.getSymbolicName())) {
				fooSubsystem = subsystem;
			} else if (SUBSYSTEM_A_EMPTY.startsWith(subsystem.getSymbolicName())) {
				aSubsystem = subsystem;
			}
		}

		// 2D2 tests
		assertNotNull("Could not find subsystem: foo", fooSubsystem);
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_A_EMPTY, aSubsystem);

		assertEquals("Wrong version for subsystem: foo", Version.parseVersion("3.0.0"), fooSubsystem.getVersion());
		assertEquals("Wrong version for subsystem: " + SUBSYSTEM_A_EMPTY, Version.parseVersion("1.0.0"), aSubsystem.getVersion());

		// 2D3b tests
		assertEquals("Wrong symbolic name header.", "foo", fooSubsystem.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME));
		assertEquals("Wrong bundle version header.", "3.0.0",fooSubsystem.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_VERSION));

	}

	// TestPlan item 2D3a
	public void testURIDerivedSymbolicNameHeader() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Bundle-SymbolicName=foo&Bundle-Version=1.0.0", SUBSYSTEM_EMPTY, false);
		assertEquals("Wrong symbolic name header.", "foo", derivedFoo.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME));
		assertEquals("Wrong bundle version header.", "1.0.0", derivedFoo.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_VERSION));
	}

	// TestPlan item 2E1
	public void testInstallFailureInvalidSMV() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "invalid", SUBSYSTEM_INVALID_SMV, true);
	}
}
