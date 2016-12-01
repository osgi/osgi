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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.resource.Resource;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.test.cases.subsystem.resource.TestResource;


public class InstallSubsystemTests extends SubsystemTest{

	// TestPlan item 2A1
	public void test2A1_NoContentSubsystem() {
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EMPTY_A, false);
		Collection<Resource> constituents = subsystem.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is one constituent because that is the context bundle for the subsystem
		assertEquals("Wrong number of constituents.", 1, constituents.size());
	}

	// TestPlan item 2A2
	public void test2A2_IncementingIDs() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_EMPTY_A, false);
		long aID = a.getSubsystemId();
		assertTrue("Subsystem id is not > 0: " + aID, aID > 0);
		Subsystem b = doSubsystemInstall(getName() + ":b", root, "b", SUBSYSTEM_EMPTY_B, false);
		long bID = b.getSubsystemId();
		assertTrue("Subsystem ids !(a < b): " + aID + ":" + bID, aID < bID);
		doSubsystemOperation("Uninstall 'a'", a, Operation.UNINSTALL, false);
		doSubsystemOperation("Uninstall 'b'", b, Operation.UNINSTALL, false);

		long lastID = bID;

		a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_EMPTY_A, false);
		aID = a.getSubsystemId();
		assertTrue("Subsystem id is not > lastID: " + aID, aID > lastID);
		b = doSubsystemInstall(getName() + ":b", root, "b", SUBSYSTEM_EMPTY_B, false);
		bID = b.getSubsystemId();
		assertTrue("Subsystem ids !(a < b): " + aID + ":" + bID, aID < bID);
	}

	// TestPlan item 2A3
	public void test2A3_SameLocationInstall() {
		Subsystem root = getRootSubsystem();
		Subsystem a1 = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_EMPTY_A, false);
		Subsystem a2 = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_EMPTY_A, false);
		assertEquals("Use of same location to install returned different subsystems.", a1, a2);
	}

	// TestPlan item 2B1a
	public void test2B1a_NoContentHeaderScoped() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_A, false);
		Collection<Resource> constituents = a.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is 3 + 1 constituent because there is the context bundle for the scoped subsystem
		assertEquals("Wrong number of constituents.", 4, constituents.size());
		BundleContext aContext = a.getBundleContext();
		Bundle[] aBundles = aContext.getBundles();
		checkBundleConstituents("Verify constituents of subsystem a.", Arrays.asList(aBundles), constituents);
	}

	// TestPlan item 2B1b install into empty scoped
	public void test2B1b_NoContentHeaderUnscopedIntoEmpty() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_EMPTY_A, false);

		// install a feature into the empty subsystem
		Subsystem b = doSubsystemInstall(getName() + ":b", a, "b", SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B, false);

		// verify a does not have new constituents
		Collection<Resource> aConstituents = a.getConstituents();
		assertNotNull("Null constituents for a", aConstituents);
		// there is 2 constituents because of the context bundle for the scoped subsystem and feature 'b'
		assertEquals("Wrong number of constituents.", 2, aConstituents.size());

		// verify b has constituents
		List<Bundle> bBundles = new ArrayList<Bundle>(Arrays.asList(b.getBundleContext().getBundles()));
		// remove the context bundle
		for (Iterator<Bundle> iBundles = bBundles.iterator(); iBundles.hasNext();) {
			if (("org.osgi.service.subsystem.region.context." + a.getSubsystemId()).equals(iBundles.next().getSymbolicName())) {
				iBundles.remove();
			}
		}
		Collection<Resource> bConstituents = b.getConstituents();
		assertEquals("Wrong number of constituents.", 3, bConstituents.size());
		checkBundleConstituents("Constiuents for feature b.", bBundles, bConstituents);
	}

	// TestPlan item 2B1b install into scoped subsystem with all the existing resources
	public void test2B1b_NoContentHeaderUnscopedIntoFull() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_A, false);

		// install a feature into the empty subsystem
		Subsystem b = doSubsystemInstall(getName() + ":b", a, "b", SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B, false);

		// verify a does not have new constituents
		Collection<Resource> aConstituents = a.getConstituents();
		assertNotNull("Null constituents.", aConstituents);
		// there is 4 + 1 constituent because there is the context bundle for the scoped subsystem + bundles (a,b,c) + feature b
		assertEquals("Wrong number of constituents.", 5, aConstituents.size());
		BundleContext aContext = a.getBundleContext();
		assertNotNull("Context is null.", aContext);
		Bundle[] aBundles = aContext.getBundles();
		checkBundleConstituents("Verify constituents of subsystem a.", Arrays.asList(aBundles), aConstituents);

		// verify b has constituents
		List<Bundle> bBundles = new ArrayList<Bundle>(Arrays.asList(aBundles));
		// remove the context bundle
		for (Iterator<Bundle> iBundles = bBundles.iterator(); iBundles.hasNext();) {
			if (("org.osgi.service.subsystem.region.context." + a.getSubsystemId()).equals(iBundles.next().getSymbolicName())) {
				iBundles.remove();
			}
		}
		Collection<Resource> bConstituents = b.getConstituents();
		assertEquals("Wrong number of constituents.", 3, bConstituents.size());
		checkBundleConstituents("Constiuents for feature b.", bBundles, bConstituents);
	}

	// TestPlan item 2B1b embedded features that have shared resources
	public void test2B1b_NoContentHeaderEmbeddedUnscoped() {
		Subsystem root = getRootSubsystem();
		Subsystem cSubsystem = doSubsystemInstall(getName(), root, "c", SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_C, false);
		Collection<Subsystem> children = cSubsystem.getChildren();
		Subsystem bSubsystem = null;
		Subsystem dSubsystem = null;
		assertEquals("Wrong number of children: c", 2, children.size());
		for (Subsystem subsystem : children) {
			if (getSymbolicName(SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B).equals(subsystem.getSymbolicName())) {
				bSubsystem = subsystem;
			} else if (getSymbolicName(SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_D).equals(subsystem.getSymbolicName())) {
				dSubsystem = subsystem;
			}
		}
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B, bSubsystem);
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_D, dSubsystem);

		Bundle[] allBundles = cSubsystem.getBundleContext().getBundles();
		assertEquals("Wrong number of constituent bundles.", 5, allBundles.length);
		Bundle aBundle = null;
		Bundle bBundle = null;
		Bundle cBundle = null;
		Bundle dBundle = null;
		for (Bundle bundle : allBundles) {
			if (getSymbolicName(BUNDLE_NO_DEPS_A_V1).equals(bundle.getSymbolicName())) {
				aBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_B_V1).equals(bundle.getSymbolicName())) {
				bBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_C_V1).equals(bundle.getSymbolicName())) {
				cBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_D_V1).equals(bundle.getSymbolicName())) {
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
		checkBundleConstituents("Checking bundle constituents: d", Arrays.asList(cBundle, dBundle), dConstituents);
	}

	// TestPlan item 2B2a
	public void test2B2a_ContentHeaderScopedNoRepository() {
		Subsystem root = getRootSubsystem();
		Subsystem h = doSubsystemInstall(getName() + ":h", root, "h", SUBSYSTEM_CONTENT_HEADER_SCOPED_H, false);
		Collection<Resource> constituents = h.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is 3 + 1 constituent because there is the context bundle for the scoped subsystem
		assertEquals("Wrong number of constituents.", 4, constituents.size());
		BundleContext hContext = h.getBundleContext();
		Bundle[] hBundles = hContext.getBundles();
		assertEquals("Wrong number of bundles.", 4, hBundles.length);
		checkBundleConstituents("Verify constituents of subsystem h.", Arrays.asList(hBundles), constituents);
	}

	// TestPlan item 2B2b
	public void test2B2b_ContentHeaderLocalRepository() {
		registerRepository(REPOSITORY_NODEPS_V2);
		Subsystem root = getRootSubsystem();
		Subsystem h = doSubsystemInstall(getName() + ":h", root, "h", SUBSYSTEM_CONTENT_HEADER_SCOPED_H, false);
		Collection<Resource> constituents = h.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is 3 + 1 constituent because there is the context bundle for the scoped subsystem
		assertEquals("Wrong number of constituents.", 4, constituents.size());
		BundleContext hContext = h.getBundleContext();
		Bundle[] hBundles = hContext.getBundles();
		assertEquals("Wrong number of bundles.", 4, hBundles.length);
		checkBundleConstituents("Verify constituents of subsystem h.", Arrays.asList(hBundles), constituents);

		Version v1 = new Version(1, 0, 0);
		for (Bundle bundle : hBundles) {
			if (!bundle.equals(hContext.getBundle())) {
				assertEquals("Wrong version for bundle: " + bundle.getSymbolicName(), v1, bundle.getVersion());
			}
		}
	}

	// TestPlan item 2B2c
	public void test2B2c_ContentHeaderScopedWithRepository() {
		registerRepository(REPOSITORY_NODEPS);
		Subsystem root = getRootSubsystem();
		Subsystem i = doSubsystemInstall(getName() + ":i", root, "i", SUBSYSTEM_CONTENT_HEADER_SCOPED_I, false);
		Collection<Resource> constituents = i.getConstituents();
		assertNotNull("Null constituents.", constituents);
		// there is 3 + 1 constituent because there is the context bundle for the scoped subsystem
		assertEquals("Wrong number of constituents.", 4, constituents.size());
		BundleContext iContext = i.getBundleContext();
		Bundle[] iBundles = iContext.getBundles();
		assertEquals("Wrong number of bundles.", 4, iBundles.length);
		checkBundleConstituents("Verify constituents of subsystem i.", Arrays.asList(iBundles), constituents);
	}

	// TestPlan item 2B2d install into empty scoped
	public void test2B2d_ContentHeaderUnscopedIntoEmpty() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_EMPTY_A, false);

		// install a feature into the empty subsystem
		Subsystem j = doSubsystemInstall(getName(), a, "j", SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, false);

		// verify a does not have new constituents
		Collection<Resource> aConstituents = a.getConstituents();
		assertNotNull("Null constituents for a", aConstituents);
		// there is 2 constituents because of the context bundle for the scoped subsystem and feature 'j'
		assertEquals("Wrong number of constituents.", 2, aConstituents.size());

		// verify j has constituents
		List<Bundle> jBundles = new ArrayList<Bundle>(Arrays.asList(j.getBundleContext().getBundles()));
		// remove the context bundle
		for (Iterator<Bundle> itrBundles = jBundles.iterator(); itrBundles.hasNext();) {
			if (("org.osgi.service.subsystem.region.context." + a.getSubsystemId()).equals(itrBundles.next().getSymbolicName())) {
				itrBundles.remove();
			}
		}
		Collection<Resource> jConstituents = j.getConstituents();
		assertEquals("Wrong number of constituents.", 3, jConstituents.size());
		checkBundleConstituents("Constiuents for feature j.", jBundles, jConstituents);
	}

	// TestPlan item 2B2d install into scoped subsystem with all the existing resources
	public void test2B2d_ContentHeaderUnscopedIntoFull() {
		Subsystem root = getRootSubsystem();
		Subsystem h = doSubsystemInstall(getName() + ":h", root, "h", SUBSYSTEM_CONTENT_HEADER_SCOPED_H, false);

		// install a feature into the empty subsystem
		Subsystem j = doSubsystemInstall(getName() + ":j", h, "j", SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, false);

		// verify h does not have new constituents
		Collection<Resource> hConstituents = h.getConstituents();
		assertNotNull("Null constituents.", hConstituents);
		// there is 4 + 1 constituent because there is the context bundle for the scoped subsystem + bundles (a,b,c) + feature h
		assertEquals("Wrong number of constituents.", 5, hConstituents.size());
		BundleContext hContext = h.getBundleContext();
		assertNotNull("Context is null.", hContext);
		Bundle[] hBundles = hContext.getBundles();
		checkBundleConstituents("Verify constituents of subsystem h.", Arrays.asList(hBundles), hConstituents);

		// verify j has constituents
		List<Bundle> jBundles = new ArrayList<Bundle>(Arrays.asList(hBundles));
		// remove the context bundle
		for (Iterator<Bundle> itrBundles = jBundles.iterator(); itrBundles.hasNext();) {
			if (("org.osgi.service.subsystem.region.context." + h.getSubsystemId()).equals(itrBundles.next().getSymbolicName())) {
				itrBundles.remove();
			}
		}
		Collection<Resource> jConstituents = j.getConstituents();
		assertEquals("Wrong number of constituents.", 3, jConstituents.size());
		checkBundleConstituents("Constiuents for feature j.", jBundles, jConstituents);
	}

	// TestPlan item 2B2d embedded features that have shared resources
	public void test2B2d_ContentHeaderEmbeddedUnscoped() {
		Subsystem root = getRootSubsystem();
		Subsystem lSubsystem = doSubsystemInstall(getName(), root, "l", SUBSYSTEM_CONTENT_HEADER_SCOPED_L, false);
		Collection<Subsystem> children = lSubsystem.getChildren();
		Subsystem jSubsystem = null;
		Subsystem kSubsystem = null;
		assertEquals("Wrong number of children: l", 2, children.size());
		for (Subsystem subsystem : children) {
			if (getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J).equals(subsystem.getSymbolicName())) {
				jSubsystem = subsystem;
			} else if (getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K).equals(subsystem.getSymbolicName())) {
				kSubsystem = subsystem;
			}
		}
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, jSubsystem);
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K, kSubsystem);

		Bundle[] allBundles = lSubsystem.getBundleContext().getBundles();
		assertEquals("Wrong number of constituent bundles.", 5, allBundles.length);
		Bundle aBundle = null;
		Bundle bBundle = null;
		Bundle cBundle = null;
		Bundle dBundle = null;
		for (Bundle bundle : allBundles) {
			if (getSymbolicName(BUNDLE_NO_DEPS_A_V1).equals(bundle.getSymbolicName())) {
				aBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_B_V1).equals(bundle.getSymbolicName())) {
				bBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_C_V1).equals(bundle.getSymbolicName())) {
				cBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_D_V1).equals(bundle.getSymbolicName())) {
				dBundle = bundle;
			}
		}
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_A_V1, aBundle);
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_B_V1, bBundle);
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_C_V1, cBundle);
		assertNotNull("Could not find bundle: " + BUNDLE_NO_DEPS_D_V1, dBundle);

		Collection<Resource> lConstituents = lSubsystem.getConstituents();
		// 4 expected: context bundle, aBundle, bSubsystem, dSubsystem
		assertEquals("Wrong number of constituents: l", 4, lConstituents.size());
		checkBundleConstituents("Checking bundle constituents: l", Arrays.asList(aBundle, lSubsystem.getBundleContext().getBundle()), lConstituents);
		checkSubsystemConstituents("Checking subsystem constituents: l", children, lConstituents);

		Collection<Resource> jConstituents = jSubsystem.getConstituents();
		// 3 expected: aBundle, bBundle, cBundle
		assertEquals("Wrong number of constituents: j", 3, jConstituents.size());
		checkBundleConstituents("Checking bundle constituents: j", Arrays.asList(aBundle, bBundle, cBundle), jConstituents);

		Collection<Resource> kConstituents = kSubsystem.getConstituents();
		// 2 expected: cBundle, dBundle
		assertEquals("Wrong number of constituents: k", 2, kConstituents.size());
		checkBundleConstituents("Checking bundle constituents: k", Arrays.asList(cBundle, dBundle), kConstituents);
	}

	// TestPlan items 2B2e
	public void test2B2e_ContentHeaderOptional() {
		Subsystem root = getRootSubsystem();
		Subsystem m = doSubsystemInstall(getName() + ":m", root, "m", SUBSYSTEM_CONTENT_HEADER_SCOPED_M, false);
		Collection<Resource> mConstituents = m.getConstituents();
		assertNotNull("Null constituents.", mConstituents);
		// there is 2 + 1 constituent because there is the context bundle for the scoped subsystem + bundle a + subsystem J
		assertEquals("Wrong number of constituents.", 3, mConstituents.size());
		BundleContext mContext = m.getBundleContext();
		assertNotNull("Null subsystem context.", mContext);
		Bundle[] mBundles = mContext.getBundles();
		// There is 3 + 1 constituents; there is the context bundle for the scoped + bundles a, b, c
		Bundle aBundle = null;
		Bundle bBundle = null;
		Bundle cBundle = null;
		for (Bundle bundle : mBundles) {
			if (getSymbolicName(BUNDLE_NO_DEPS_A_V1).equals(bundle.getSymbolicName())) {
				aBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_B_V1).equals(bundle.getSymbolicName())) {
				bBundle = bundle;
			} else if (getSymbolicName(BUNDLE_NO_DEPS_C_V1).equals(bundle.getSymbolicName())) {
				cBundle = bundle;
			}
		}
		assertEquals("Wrong number of bundles.", 4, mBundles.length);
		checkBundleConstituents("Verify constituents of subsystem m.", Arrays.asList(aBundle), mConstituents);
		Collection<Subsystem> children = m.getChildren();
		assertEquals("Wrong number of children", 1, children.size());
		assertEquals("Could not find correct child subsystem.", getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J), children.iterator().next().getSymbolicName());
		checkSubsystemConstituents("Verify constituents of subsystem m.", children, mConstituents);

		Collection<Resource> jConstituents = children.iterator().next().getConstituents();
		assertEquals("Wrong number of constituents.", 3, jConstituents.size());
		checkBundleConstituents("Verify constituents of subsystem j.", Arrays.asList(aBundle, bBundle, cBundle), jConstituents);
	}

	// TestPlan items 2B2f
	public void test2B2f_ContentHeaderMandatory() {
		Subsystem root = getRootSubsystem();
		Subsystem i = doSubsystemInstall(getName() + ":i", root, "i", SUBSYSTEM_CONTENT_HEADER_SCOPED_I, true);
		assertNull("Should not have installed subsystem 'i'", i);
	}

	public void test2B2g_succeedInsall() {
		registerRepository(REPOSITORY_2B2g);
		@SuppressWarnings("unused")
		Subsystem s1 = doSubsystemInstall(getName(), getRootSubsystem(), getName(), SUBSYSTEM_2B2g_S2_APPLICATION, false);
	}

	public void test2B2g_failInstall() {
		registerRepository(REPOSITORY_2B2g);
		doSubsystemInstall(getName(), getRootSubsystem(), getName(), SUBSYSTEM_2B2g_S3_APPLICATION, true);
	}

	// TestPlan item 2C1 for composites
	public void test2C1_ContextComposite() {
		Subsystem root = getRootSubsystem();
		Subsystem eSubsystem = doSubsystemInstall(getName() + ":e", root, "e", SUBSYSTEM_EMPTY_COMPOSITE_E, false);
		checkContextBundle(getName(), eSubsystem);
	}

	// TestPlan item 2C1 for applications
	public void test2C1_ContextApplication() {
		Subsystem root = getRootSubsystem();
		Subsystem aSubsystem = doSubsystemInstall(getName() + ":a", root, "a", SUBSYSTEM_EMPTY_A, false);
		checkContextBundle(getName(), aSubsystem);
	}

	// TestPlan item 2C2 for features
	public void test2C2_ContextFeature() {
		Subsystem root = getRootSubsystem();
		Subsystem fSubsystem = doSubsystemInstall(getName() + ":f", root, "f", SUBSYSTEM_EMPTY_FEATURE_F, false);
		checkContextBundle(getName(), fSubsystem);
	}

	// TestPlan item 2D1a
	public void test2D1a_URIDerivedSymbolicName() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Subsystem-SymbolicName=foo", SUBSYSTEM_EMPTY, false);
		assertEquals("Wrong symbolic name.", "foo", derivedFoo.getSymbolicName());
	}

	// TestPlan item 2D1b
	public void test2D1b_URLDerivedSymbolicNamePriority() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Subsystem-SymbolicName=foo", SUBSYSTEM_EMPTY_A, false);
		assertEquals("Wrong symbolic name.", getSymbolicName(SUBSYSTEM_EMPTY_A), derivedFoo.getSymbolicName());
	}

	// TestPlan item 2D1c
	public void test2D1c_URIDerivedVersion() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Subsystem-SymbolicName=foo&Subsystem-Version=2.0.0", SUBSYSTEM_EMPTY, false);
		assertEquals("Wrong version.", Version.parseVersion("2.0.0"), derivedFoo.getVersion());
	}

	// TestPlan item 2D1d
	public void test2D1d_URLDerivedVersionPriority() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Subsystem-Version=2.0.0", SUBSYSTEM_EMPTY_A, false);
		assertEquals("Wrong version.", Version.parseVersion("1.0.0"), derivedFoo.getVersion());
	}

	// TestPlan item 2D1e
	public void test2D1e_URLDerivedEmbeddedURL() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://" + getEmbeddedURL(SUBSYSTEM_EMPTY) + "?Subsystem-SymbolicName=foo&Subsystem-Version=2.0.0", null, false);
		assertEquals("Wrong symbolic name.", "foo", derivedFoo.getSymbolicName());
		assertEquals("Wrong version.", Version.parseVersion("2.0.0"), derivedFoo.getVersion());
	}

	// TestPlan item 2D2 & 2D3b
	public void test2D2_2D3b_EntryNameDerived() {
		Subsystem root = getRootSubsystem();
		Subsystem gSubsystem = doSubsystemInstall(getName(), root, "g", SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_G, false);
		Collection<Subsystem> children = gSubsystem.getChildren();
		Subsystem fooSubsystem = null;
		Subsystem aSubsystem = null;
		for (Subsystem subsystem : children) {
			if ("foo".equals(subsystem.getSymbolicName())) {
				fooSubsystem = subsystem;
			} else if (getSymbolicName(SUBSYSTEM_EMPTY_A).equals(subsystem.getSymbolicName())) {
				aSubsystem = subsystem;
			}
		}

		// 2D2 tests
		assertNotNull("Could not find subsystem: foo", fooSubsystem);
		assertNotNull("Could not find subsystem: " + SUBSYSTEM_EMPTY_A, aSubsystem);

		assertEquals("Wrong version for subsystem: foo", Version.parseVersion("3.0.0"), fooSubsystem.getVersion());
		assertEquals("Wrong version for subsystem: " + SUBSYSTEM_EMPTY_A, Version.parseVersion("1.0.0"), aSubsystem.getVersion());

		// 2D3b tests
		assertEquals("Wrong symbolic name header.", "foo", fooSubsystem.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME));
		assertEquals("Wrong bundle version header.", "3.0.0", fooSubsystem.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_VERSION));

	}

	// TestPlan item 2D3a
	public void test2D3a_URIDerivedSymbolicNameHeader() {
		Subsystem root = getRootSubsystem();
		Subsystem derivedFoo = doSubsystemInstall(getName(), root, "subsystem://?Subsystem-SymbolicName=foo&Subsystem-Version=1.0.0", SUBSYSTEM_EMPTY, false);
		assertEquals("Wrong symbolic name header.", "foo", derivedFoo.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME));
		assertEquals("Wrong bundle version header.", "1.0.0", derivedFoo.getSubsystemHeaders(null).get(SubsystemConstants.SUBSYSTEM_VERSION));
	}

	public void test2D4a() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "subsystem://?Subsystem-SymbolicName=foo$invalid&Subsystem-Version=1.0.0", SUBSYSTEM_EMPTY, true);
	}

	public void test2D4b() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "subsystem://?Subsystem-SymbolicName=foo&Subsystem-Version=1.invalidVersion.0", SUBSYSTEM_EMPTY, true);
	}

	// TestPlan item 2E1
	public void test2E1_InstallFailureInvalidSMV() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "invalid", SUBSYSTEM_INVALID_SMV, true);
	}

	// TestPlan item 2E2
	public void test2E2_InstallFailureFeatureCycles() {
		registerRepository(REPOSITORY_CYCLE);
		Subsystem root = getRootSubsystem();
		Subsystem b = doSubsystemInstall(getName(), root, "b", SUBSYSTEM_EMPTY_B, false);
		doSubsystemInstall(getName(), b, "cycle.a", SUBSYSTEM_CYCLE_UNSCOPED_A, true);
	}

	// TestPlan item 2E3
	public void test2E3_InstallFailureScopedRecursive() {
		registerRepository(REPOSITORY_CYCLE);
		Subsystem root = getRootSubsystem();
		Subsystem b = doSubsystemInstall(getName(), root, "b", SUBSYSTEM_EMPTY_B, false);
		doSubsystemInstall(getName(), b, "cycle.c", SUBSYSTEM_CYCLE_SCOPED_C, true);
	}

	// TestPlan item 2E4
	public void test2E4_InstallFailureDifferentType() {
		Subsystem root = getRootSubsystem();
		Subsystem b = doSubsystemInstall(getName(), root, "b", SUBSYSTEM_EMPTY_B, false);
		doSubsystemInstall(getName(), b, "a1", SUBSYSTEM_EMPTY_A, false);
		doSubsystemInstall(getName(), b, "a2", SUBSYSTEM_EMPTY_COMPOSITE_A, true);
	}

	// TestPlan item 2E5
	public void test2E5_InstsallFailureContentResource() {
		TestResource.failContent = true;
		try {
			registerRepository(REPOSITORY_NODEPS);
			Subsystem root = getRootSubsystem();
			doSubsystemInstall(getName() + ":i", root, "i", SUBSYSTEM_CONTENT_HEADER_SCOPED_I, true);
		} finally {
			TestResource.failContent = false;
		}
	}

	// TestPlan item 2E6
	public void test2E6_InstallFailureDuplicateLocation() {
		Subsystem root = getRootSubsystem();
		Subsystem a = doSubsystemInstall(getName(), root, "a", SUBSYSTEM_EMPTY_A, false);
		Subsystem b = doSubsystemInstall(getName(), root, "b", SUBSYSTEM_EMPTY_B, false);

		doSubsystemInstall(getName(), a, "j", SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, false);
		doSubsystemInstall(getName(), b, "j", SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, true);
	}

	// TestPlan item 2E7
	public void test2E7_InstallFailureInvalidCompositeContent() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "invalid", SUBSYSTEM_INVALID_COMPOSITE_N, true);
	}

	// TestPlan item 2E8
	public void test2E8_InstallFailureInvalidContentType() {
		registerRepository(REPOSITORY_INVALID_TYPE);
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "invalid.content", SUBSYSTEM_INVALID_COMPOSITE_CONTENT_TYPE, true);
	}

	// TestPlan item 2E9
	public void test2E9_InstallFailureInvalidPreferType() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "invalid.prefer.type", SUBSYSTEM_INVALID_APPLICATION_PREFER_TYPE, true);
	}

	// TestPlan item 2E10
	public void test2E10_InstallFailureInvalidPreferType() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, "invalid.feature.prefer", SUBSYSTEM_INVALID_FEATURE_PREFER, true);
	}

	public void test2E11() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_2E11_APPLICATION, true);
	}

	public void test2E12() {
		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_2E12_APPLICATION, true);
	}

	public void test2F1() throws IOException {
		File nonESAExtension = getContext().getDataFile(getName() + ".zip");
		copyFile(getSubsystemArchive(SUBSYSTEM_EMPTY_A), nonESAExtension);

		Subsystem root = getRootSubsystem();
		doSubsystemInstall(getName(), root, nonESAExtension.toURI().toString(), null, false);
	}

	public void test2F2() throws IOException {
		Subsystem root = getRootSubsystem();
		Subsystem s = doSubsystemInstall(getName(), root, "", SUBSYSTEM_EMPTY_A, false);
		assertEquals("Wrong location", "", s.getLocation());
	}

	public void test2F3() {
		Subsystem root = getRootSubsystem();
		Subsystem s1 = doSubsystemInstall(getName(), root, getName() + ".a", SUBSYSTEM_2F3a_APPLICATION, false);
		assertEquals("Wrong version", Version.parseVersion("1"), s1.getVersion());

		Subsystem s2 = doSubsystemInstall(getName(), root, getName() + ".b", SUBSYSTEM_2F3b_APPLICATION, false);
		assertEquals("Wrong version", Version.parseVersion("1.0.0.qualifier"), s2.getVersion());
	}

	public void test2F4() {
		Subsystem root = getRootSubsystem();
		Subsystem s = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_2F4_APPLICATION, false);
		assertEquals("Wrong symbolic name.", getSymbolicName(SUBSYSTEM_2F4_APPLICATION), s.getSymbolicName());
		assertEquals("Wrong header value." , "test", s.getSubsystemHeaders(null).get("Unknown-Header"));
	}

	public void test2F5() throws InvalidSyntaxException {
		Subsystem root = getRootSubsystem();
		Subsystem s = doSubsystemInstall(getName(), root, getName(), SUBSYSTEM_EMPTY_A, false);
		checkSubsystemProperties(s, getName(), getSymbolicName(SUBSYSTEM_EMPTY_A), new Version("1.0.0"), SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION , s.getSubsystemId(), getName(), State.INSTALLED);
		Collection<ServiceReference<Subsystem>> refs = getContext().getServiceReferences(Subsystem.class, "(" + SubsystemConstants.SUBSYSTEM_ID_PROPERTY + "=" + s.getSubsystemId() + ")");
		assertEquals("Wrong number of sybsystem services.", 1, refs.size());
		checkSubsystemProperties(refs.iterator().next(), getName(), getSymbolicName(SUBSYSTEM_EMPTY_A), new Version("1.0.0"), SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION , s.getSubsystemId(), State.INSTALLED);
	}

	public static void copyFile(File src, File dest) throws IOException {
	    if(!dest.exists()) {
	        dest.createNewFile();
	    }

		try (FileInputStream fis = new FileInputStream(src);
				FileOutputStream fos = new FileOutputStream(dest)) {
			FileChannel source = fis.getChannel();
			FileChannel destination = fos.getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	}

}
