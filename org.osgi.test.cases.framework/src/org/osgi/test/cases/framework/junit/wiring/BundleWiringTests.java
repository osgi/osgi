/*
 * Copyright (c) IBM Corporation (2010, 2011). All Rights Reserved.
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

package org.osgi.test.cases.framework.junit.wiring;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.namespace.BundleNamespace;
import org.osgi.framework.namespace.ExecutionEnvironmentNamespace;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;

public class BundleWiringTests extends WiringTest {
	public void testGetRevision() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");
		Bundle tb14 = install("resolver.tb14.jar");
		List<Bundle> testBundles = Arrays.asList(tb1, tb2, tb3, tb4, tb5, tb14);

		assertTrue(frameworkWiring.resolveBundles(testBundles));

		List<BundleRevision> testRevisions = getRevisions(testBundles);

		BundleRevision tb1Revision = testRevisions.get(0);
		BundleRevision tb2Revision = testRevisions.get(1);
		BundleRevision tb3Revision = testRevisions.get(2);
		BundleRevision tb4Revision = testRevisions.get(3);
		BundleRevision tb5Revision = testRevisions.get(4);
		BundleRevision tb14Revision = testRevisions.get(5);
		
		List<BundleCapability> tb1AllCapabilities = tb1Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb1BundleCapabilities = tb1Revision
				.getDeclaredCapabilities(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleCapability> tb1HostCapabilities = tb1Revision
				.getDeclaredCapabilities(HostNamespace.HOST_NAMESPACE);
		List<BundleCapability> tb1PackageCapabilities = tb1Revision
				.getDeclaredCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		List<BundleCapability> tb1TestCapabilities = tb1Revision.getDeclaredCapabilities("test");
		List<BundleCapability> tb1TestMultipleCapabilities = tb1Revision.getDeclaredCapabilities("test.multiple");
		List<BundleCapability> tb1TestFragmentCapabilities = tb1Revision.getDeclaredCapabilities("test.fragment");
		checkCapabilities(tb1BundleCapabilities, tb1AllCapabilities,
				BundleNamespace.BUNDLE_NAMESPACE, 1, tb1Revision);
		checkCapabilities(tb1HostCapabilities, tb1AllCapabilities,
				HostNamespace.HOST_NAMESPACE, 1, tb1Revision);
		checkCapabilities(tb1PackageCapabilities, tb1AllCapabilities,
				PackageNamespace.PACKAGE_NAMESPACE, 1, tb1Revision);
		checkCapabilities(tb1TestCapabilities, tb1AllCapabilities, "test", 1, tb1Revision);
		checkCapabilities(tb1TestMultipleCapabilities, tb1AllCapabilities, "test.multiple", 2, tb1Revision);
		checkCapabilities(tb1TestFragmentCapabilities, tb1AllCapabilities, "test.fragment", 0, tb1Revision);
		
		List<BundleRequirement> tb1AllRequirements = tb1Revision.getDeclaredRequirements(null);
		List<BundleRequirement> tb1BundleRequirements = tb1Revision
				.getDeclaredRequirements(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleRequirement> tb1HostRequirements = tb1Revision
				.getDeclaredRequirements(HostNamespace.HOST_NAMESPACE);
		List<BundleRequirement> tb1PackageRequirements = tb1Revision
				.getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		checkRequirements(tb1BundleRequirements, tb1AllRequirements,
				BundleNamespace.BUNDLE_NAMESPACE, 0, tb1Revision);
		checkRequirements(tb1HostRequirements, tb1AllRequirements,
				HostNamespace.HOST_NAMESPACE, 0, tb1Revision);
		checkRequirements(tb1PackageRequirements, tb1AllRequirements,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb1Revision);
		
		List<BundleCapability> tb2AllCapabilities = tb2Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb2BundleCapabilities = tb2Revision
				.getDeclaredCapabilities(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleCapability> tb2HostCapabilities = tb2Revision
				.getDeclaredCapabilities(HostNamespace.HOST_NAMESPACE);
		List<BundleCapability> tb2PackageCapabilities = tb2Revision
				.getDeclaredCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		checkCapabilities(tb2BundleCapabilities, tb2AllCapabilities,
				BundleNamespace.BUNDLE_NAMESPACE, 1, tb2Revision);
		checkCapabilities(tb2HostCapabilities, tb2AllCapabilities,
				HostNamespace.HOST_NAMESPACE, 0, tb2Revision);
		checkCapabilities(tb2PackageCapabilities, tb2AllCapabilities,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb2Revision);
		
		List<BundleRequirement> tb2AllRequirements = tb2Revision.getDeclaredRequirements(null);
		List<BundleRequirement> tb2BundleRequirements = tb2Revision
				.getDeclaredRequirements(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleRequirement> tb2HostRequirements = tb2Revision
				.getDeclaredRequirements(HostNamespace.HOST_NAMESPACE);
		List<BundleRequirement> tb2PackageRequirements = tb2Revision
				.getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		checkRequirements(tb2BundleRequirements, tb2AllRequirements,
				BundleNamespace.BUNDLE_NAMESPACE, 0, tb2Revision);
		checkRequirements(tb2HostRequirements, tb2AllRequirements,
				HostNamespace.HOST_NAMESPACE, 0, tb2Revision);
		checkRequirements(tb2PackageRequirements, tb2AllRequirements,
				PackageNamespace.PACKAGE_NAMESPACE, 2, tb2Revision);
		
		List<BundleCapability> tb3AllCapabilities = tb3Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb3BundleCapabilities = tb3Revision
				.getDeclaredCapabilities(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleCapability> tb3HostCapabilities = tb3Revision
				.getDeclaredCapabilities(HostNamespace.HOST_NAMESPACE);
		List<BundleCapability> tb3PackageCapabilities = tb3Revision
				.getDeclaredCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		checkCapabilities(tb3BundleCapabilities, tb3AllCapabilities,
				BundleNamespace.BUNDLE_NAMESPACE, 1, tb3Revision);
		checkCapabilities(tb3HostCapabilities, tb3AllCapabilities,
				HostNamespace.HOST_NAMESPACE, 1, tb3Revision);
		checkCapabilities(tb3PackageCapabilities, tb3AllCapabilities,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb3Revision);
		
		List<BundleRequirement> tb3AllRequirements = tb3Revision.getDeclaredRequirements(null);
		List<BundleRequirement> tb3BundleRequirements = tb3Revision
				.getDeclaredRequirements(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleRequirement> tb3HostRequirements = tb3Revision
				.getDeclaredRequirements(HostNamespace.HOST_NAMESPACE);
		List<BundleRequirement> tb3PackageRequirements = tb3Revision
				.getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		checkRequirements(tb3BundleRequirements, tb3AllRequirements,
				BundleNamespace.BUNDLE_NAMESPACE, 1, tb3Revision);
		checkRequirements(tb3HostRequirements, tb3AllRequirements,
				HostNamespace.HOST_NAMESPACE, 0, tb3Revision);
		checkRequirements(tb3PackageRequirements, tb3AllRequirements,
				PackageNamespace.PACKAGE_NAMESPACE, 1, tb3Revision);

		List<BundleCapability> tb4AllCapabilities = tb4Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb4BundleCapabilities = tb4Revision
				.getDeclaredCapabilities(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleCapability> tb4HostCapabilities = tb4Revision
				.getDeclaredCapabilities(HostNamespace.HOST_NAMESPACE);
		List<BundleCapability> tb4PackageCapabilities = tb4Revision
				.getDeclaredCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		List<BundleCapability> tb4TestFragmentCapabilities = tb4Revision.getDeclaredCapabilities("test.fragment");
		checkCapabilities(tb4BundleCapabilities, tb4AllCapabilities,
				BundleNamespace.BUNDLE_NAMESPACE, 0, tb4Revision);
		checkCapabilities(tb4HostCapabilities, tb4AllCapabilities,
				HostNamespace.HOST_NAMESPACE, 0, tb4Revision);
		checkCapabilities(tb4PackageCapabilities, tb4AllCapabilities,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb4Revision);
		checkCapabilities(tb4TestFragmentCapabilities, tb4AllCapabilities, "test.fragment", 1, tb4Revision);
		
		List<BundleRequirement> tb4AllRequirements = tb4Revision.getDeclaredRequirements(null);
		List<BundleRequirement> tb4BundleRequirements = tb4Revision
				.getDeclaredRequirements(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleRequirement> tb4HostRequirements = tb4Revision
				.getDeclaredRequirements(HostNamespace.HOST_NAMESPACE);
		List<BundleRequirement> tb4PackageRequirements = tb4Revision
				.getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		checkRequirements(tb4BundleRequirements, tb4AllRequirements,
				BundleNamespace.BUNDLE_NAMESPACE, 0, tb4Revision);
		checkRequirements(tb4HostRequirements, tb4AllRequirements,
				HostNamespace.HOST_NAMESPACE, 1, tb4Revision);
		checkRequirements(tb4PackageRequirements, tb4AllRequirements,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb4Revision);
		
		List<BundleCapability> tb5AllCapabilities = tb5Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb5BundleCapabilities = tb5Revision
				.getDeclaredCapabilities(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleCapability> tb5HostCapabilities = tb5Revision
				.getDeclaredCapabilities(HostNamespace.HOST_NAMESPACE);
		List<BundleCapability> tb5PackageCapabilities = tb5Revision
				.getDeclaredCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		checkCapabilities(tb5BundleCapabilities, tb5AllCapabilities,
				BundleNamespace.BUNDLE_NAMESPACE, 1, tb5Revision);
		checkCapabilities(tb5HostCapabilities, tb5AllCapabilities,
				HostNamespace.HOST_NAMESPACE, 1, tb5Revision);
		checkCapabilities(tb5PackageCapabilities, tb5AllCapabilities,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb5Revision);
		
		List<BundleRequirement> tb5AllRequirements = tb5Revision.getDeclaredRequirements(null);
		List<BundleRequirement> tb5BundleRequirements = tb5Revision
				.getDeclaredRequirements(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleRequirement> tb5HostRequirements = tb5Revision
				.getDeclaredRequirements(HostNamespace.HOST_NAMESPACE);
		List<BundleRequirement> tb5PackageRequirements = tb5Revision
				.getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		List<BundleRequirement> tb5TestRequirements = tb5Revision.getDeclaredRequirements("test");
		List<BundleRequirement> tb5TestNoAttrsRequirements = tb5Revision.getDeclaredRequirements("test.no.attrs");
		checkRequirements(tb5BundleRequirements, tb5AllRequirements,
				BundleNamespace.BUNDLE_NAMESPACE, 0, tb5Revision);
		checkRequirements(tb5HostRequirements, tb5AllRequirements,
				HostNamespace.HOST_NAMESPACE, 0, tb5Revision);
		checkRequirements(tb5PackageRequirements, tb5AllRequirements,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb5Revision);
		checkRequirements(tb5TestRequirements, tb5AllRequirements, "test", 10, tb5Revision);
		checkRequirements(tb5TestNoAttrsRequirements, tb5AllRequirements, "test.no.attrs", 1, tb5Revision);
		
		List<BundleCapability> tb14AllCapabilities = tb14Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb14BundleCapabilities = tb14Revision
				.getDeclaredCapabilities(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleCapability> tb14HostCapabilities = tb14Revision
				.getDeclaredCapabilities(HostNamespace.HOST_NAMESPACE);
		List<BundleCapability> tb14PackageCapabilities = tb14Revision
				.getDeclaredCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		checkCapabilities(tb14BundleCapabilities, tb14AllCapabilities,
				BundleNamespace.BUNDLE_NAMESPACE, 1, tb14Revision);
		checkCapabilities(tb14HostCapabilities, tb14AllCapabilities,
				HostNamespace.HOST_NAMESPACE, 1, tb14Revision);
		checkCapabilities(tb14PackageCapabilities, tb14AllCapabilities,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb14Revision);
		
		List<BundleRequirement> tb14AllRequirements = tb14Revision.getDeclaredRequirements(null);
		List<BundleRequirement> tb14BundleRequirements = tb14Revision
				.getDeclaredRequirements(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleRequirement> tb14HostRequirements = tb14Revision
				.getDeclaredRequirements(HostNamespace.HOST_NAMESPACE);
		List<BundleRequirement> tb14PackageRequirements = tb14Revision
				.getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE);
		List<BundleRequirement> tb14TestFragmentRequirements = tb14Revision.getDeclaredRequirements("test.fragment");
		checkRequirements(tb14BundleRequirements, tb14AllRequirements,
				BundleNamespace.BUNDLE_NAMESPACE, 0, tb14Revision);
		checkRequirements(tb14HostRequirements, tb14AllRequirements,
				HostNamespace.HOST_NAMESPACE, 0, tb14Revision);
		checkRequirements(tb14PackageRequirements, tb14AllRequirements,
				PackageNamespace.PACKAGE_NAMESPACE, 0, tb14Revision);
		checkRequirements(tb14TestFragmentRequirements, tb14AllRequirements, "test.fragment", 1, tb14Revision);
	}

	void checkCapabilities(List<BundleCapability> toCheck, List<BundleCapability> all, String namespace, int expectedNum, BundleRevision provider) {
		assertEquals("Wrong number of capabilities for " + namespace + " from " + provider, expectedNum, toCheck.size());
		for (BundleCapability capability : toCheck) {
			assertTrue("Capability is not in all capabilities", all.contains(capability));
			assertEquals("Wrong namespace", namespace, capability.getNamespace());
			assertEquals("Wrong provider", provider, capability.getRevision());
			assertResourceEqualsRevision(capability.getResource(), capability.getRevision());
		}
	}
	
	void checkRequirements(List<BundleRequirement> toCheck, List<BundleRequirement> all, String namespace, int expectedNum, BundleRevision requirer) {
		assertEquals("Wrong number of requirements for " + namespace + " from " + requirer, expectedNum, toCheck.size());
		for (BundleRequirement requirement : toCheck) {
			assertTrue("Capability is not in all capabilities", all.contains(requirement));
			assertEquals("Wrong namespace", namespace, requirement.getNamespace());
			assertEquals("Wrong requirer", requirer, requirement.getRevision());
			assertResourceEqualsRevision(requirement.getResource(), requirement.getRevision());
		}
	}
	
	void checkWires(List<BundleWire> toCheck, List<BundleWire> all, String namespace, int expectedNum) {
		assertEquals("Wrong number of wires for " + namespace, expectedNum, toCheck.size());
		for (BundleWire wire : toCheck) {
			assertTrue("Capability is not in all capabilities for revision", all.contains(wire));
		}
	}

	private List<BundleRevision> getRevisions(List<Bundle> testBundles) {
		ArrayList<BundleRevision> result = new ArrayList<BundleRevision>(testBundles.size());
		for (Bundle bundle : testBundles) {
			BundleRevision revision = bundle.adapt(BundleRevision.class);
			result.add(revision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), revision.getSymbolicName());
			assertEquals("Wrong bundle", bundle, revision.getBundle());
			assertEquals("Wrong version", bundle.getVersion(), revision.getVersion());
			assertEquals("Wrong type", bundle.getHeaders("").get(Constants.FRAGMENT_HOST) == null ? 0 : BundleRevision.TYPE_FRAGMENT, revision.getTypes());
			
			Collection<BundleWire> hostWirings = revision.getWiring()
					.getRequiredWires(HostNamespace.HOST_NAMESPACE);
			assertNotNull("Host wirings must never be null.", hostWirings);
			assertRequiredResourceWiresEqualsRequiredWires(revision.getWiring(), HostNamespace.HOST_NAMESPACE);
			if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
				// we assume the fragment resolved to one host
				assertEquals("Wrong number of host wirings found", 1, hostWirings.size());
			} else {
				// regular bundles must have empty host wirings
				assertEquals("Must have empty wirings for regular bundles", 0, hostWirings.size());
			}
			
			assertCapabilitiesEqualsDeclaredCapabilities(revision);
			assertRequirementsEqualsDeclaredRequirements(revision);
		}
		return result;
	}
	
	private void assertCapabilitiesEqualsDeclaredCapabilities(BundleRevision revision) {
		assertEquals("Capabilities do not equal declared capabilities", revision.getDeclaredCapabilities(null), revision.getCapabilities(null));
	}
	
	private void assertProvidedResourceWiresEqualsProvidedWires(BundleWiring wiring, String namespace) {
		List<BundleWire> providedWires = wiring.getProvidedWires(namespace);
		List<Wire> providedResourceWires = wiring.getProvidedResourceWires(namespace);
		assertEquals("Provided resource wires not same as provided wires for namespace " + namespace, providedWires, providedResourceWires);
	}
	
	private void assertProvidedResourceWiresEqualsProvidedWires(BundleWiring wiring, Collection<String> genericNamespaces) {
		assertProvidedResourceWiresEqualsProvidedWires(wiring, (String)null);
		assertProvidedResourceWiresEqualsProvidedWires(wiring, BundleNamespace.BUNDLE_NAMESPACE);
		assertProvidedResourceWiresEqualsProvidedWires(wiring, HostNamespace.HOST_NAMESPACE);
		assertProvidedResourceWiresEqualsProvidedWires(wiring, PackageNamespace.PACKAGE_NAMESPACE);
		for (String namespace : genericNamespaces)
			assertProvidedResourceWiresEqualsProvidedWires(wiring, namespace);
	}
	
	private void assertProvidedResourceWiresEqualsProvidedWires(BundleWiring[] wirings, Collection<String> genericNamespaces) {
		for (BundleWiring wiring : wirings)
			assertProvidedResourceWiresEqualsProvidedWires(wiring, genericNamespaces);
	}
	
	private void assertRequiredResourceWiresEqualsRequiredWires(BundleWiring wiring, String namespace) {
		List<BundleWire> requiredWires = wiring.getRequiredWires(namespace);
		List<Wire> requiredResourceWires = wiring.getRequiredResourceWires(namespace);
		assertEquals("Required resource wires not same as required wires for namespace " + namespace, requiredWires, requiredResourceWires);
	}
	
	private void assertRequiredResourceWiresEqualsRequiredWires(BundleWiring wiring, Collection<String> genericNamespaces) {
		assertRequiredResourceWiresEqualsRequiredWires(wiring, (String)null);
		assertRequiredResourceWiresEqualsRequiredWires(wiring, BundleNamespace.BUNDLE_NAMESPACE);
		assertRequiredResourceWiresEqualsRequiredWires(wiring, HostNamespace.HOST_NAMESPACE);
		assertRequiredResourceWiresEqualsRequiredWires(wiring, PackageNamespace.PACKAGE_NAMESPACE);
		for (String namespace : genericNamespaces)
			assertRequiredResourceWiresEqualsRequiredWires(wiring, namespace);
	}
	
	private void assertRequiredResourceWiresEqualsRequiredWires(BundleWiring[] wirings, Collection<String> genericNamespaces) {
		for (BundleWiring wiring : wirings)
			assertRequiredResourceWiresEqualsRequiredWires(wiring, genericNamespaces);
	}
	
	private void assertRequirementsEqualsDeclaredRequirements(BundleRevision revision) {
		assertEquals("Requirements do not equal declared requirements", revision.getDeclaredRequirements(null), revision.getRequirements(null));
	}
	
	private void assertResourceEqualsRevision(BundleWiring wiring) {
		assertResourceEqualsRevision(wiring.getRevision(), wiring.getResource());
	}
	
	private void assertResourceEqualsRevision(Resource resource, BundleRevision revision) {
		assertEquals("Resource does not equal revision", revision, resource);
	}
	
	private void assertResourceCapabilitiesEqualsCapabilites(BundleWiring wiring) {
		assertEquals("Resource capabilities do not equal capabilities", wiring.getCapabilities(null), wiring.getResourceCapabilities(null));
	}
	
	private void assertResourceRequirementsEqualsRequirements(BundleWiring wiring) {
		assertEquals("Resource requirements do not equal requirements", wiring.getRequirements(null), wiring.getResourceRequirements(null));
	}

	public void testGetWiring() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");
		Bundle tb14 = install("resolver.tb14.jar");
		List<Bundle> testBundles = Arrays.asList(tb1, tb2, tb3, tb4, tb5, tb14);

		assertTrue(frameworkWiring.resolveBundles(testBundles));

		BundleWiring tb1Wiring = tb1.adapt(BundleWiring.class);
		BundleWiring tb2Wiring = tb2.adapt(BundleWiring.class);
		BundleWiring tb3Wiring = tb3.adapt(BundleWiring.class);
		BundleWiring tb4Wiring = tb4.adapt(BundleWiring.class);
		BundleWiring tb5Wiring = tb5.adapt(BundleWiring.class);
		BundleWiring tb14Wiring = tb14.adapt(BundleWiring.class);
		BundleWiring[] wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring, tb14Wiring};
		
		checkBundleWiring(testBundles.toArray(new Bundle[testBundles.size()]), wirings);

		List<String> genericNamespaces = Arrays.asList("test", "test.multiple", "test.no.attrs", "test.fragment");
		assertProvidedResourceWiresEqualsProvidedWires(tb1Wiring, genericNamespaces);
		
		List<BundleWire> allTb1Capabilities = tb1Wiring.getProvidedWires(null);
		List<BundleWire> osgiBundleTb1Capabilities = tb1Wiring
				.getProvidedWires(BundleNamespace.BUNDLE_NAMESPACE);
		List<BundleWire> osgiHostTb1Capabilities = tb1Wiring
				.getProvidedWires(HostNamespace.HOST_NAMESPACE);
		List<BundleWire> osgiPackageTb1Capabilities = tb1Wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		List<BundleWire> genTestTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(0));
		List<BundleWire> genTestMultipleTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(1));
		List<BundleWire> genTestNoAttrsTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(2));
		List<BundleWire> genTestFragmentTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(3));

		// check for osgi.wiring.host capability from wiring with
		// fragment-attachment:="never"
		List<BundleCapability> osgiHostTb2Capabilities = tb2Wiring
				.getCapabilities(HostNamespace.HOST_NAMESPACE);
		assertEquals("Expecting no osgi.wiring.host capability", 0, osgiHostTb2Capabilities.size());

		checkBundleWires(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb14Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiHostTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities, genTestFragmentTb1Capabilities, genTestNoAttrsTb1Capabilities);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings);

		tb1Wiring = tb1.adapt(BundleWiring.class);
		tb2Wiring = tb2.adapt(BundleWiring.class);
		tb3Wiring = tb3.adapt(BundleWiring.class);
		tb4Wiring = tb4.adapt(BundleWiring.class);
		tb5Wiring = tb5.adapt(BundleWiring.class);
		tb14Wiring = tb14.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring, tb14Wiring};

		checkBundleWiring(testBundles.toArray(new Bundle[testBundles.size()]), wirings);

		// get wired capabilities before update
		osgiBundleTb1Capabilities = tb1Wiring
				.getProvidedWires(BundleNamespace.BUNDLE_NAMESPACE);
		osgiHostTb1Capabilities = tb1Wiring
				.getProvidedWires(HostNamespace.HOST_NAMESPACE);
		osgiPackageTb1Capabilities = tb1Wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		genTestTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(0));
		genTestMultipleTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(1));
		genTestNoAttrsTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(2));
		genTestFragmentTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(3));

		// test the update case
		URL content = getContext().getBundle().getEntry("resolver.tb1.v110.jar");
		assertNotNull("Cannot find content for update", content);
		try {
			tb1.update(content.openStream());
		} catch (BundleException e) {
			fail("Unexpected update failure",e);
		} catch (IOException e) {
			fail("Unexpected update failure",e);
		}

		assertTrue("Could not resolve updated bundle", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb1})));

		// check that the updated wiring has no requirers
		BundleWiring updatedWiring = tb1.adapt(BundleWiring.class);
		checkBundleWiring(new Bundle[] {tb1}, new BundleWiring[] {updatedWiring});
		List<BundleWire> updatedWires = updatedWiring.getProvidedWires(null);
		assertNotNull("Requirers is null", updatedWires);
		// May have zero wires or one fragment wire
		if (updatedWires.size() == 1) {
			assertEquals("Wrong wire capability namespace",
					HostNamespace.HOST_NAMESPACE, updatedWires.get(0)
							.getCapability().getNamespace());
			assertEquals("Wrong requirer", tb4Wiring.getRevision(),
					updatedWires.get(0).getRequirement().getRevision());
		} else {
			assertEquals("Wrong number of requirers", 0, updatedWires.size());
		}

		assertTrue("Wiring is not in use for: " + tb1, tb1Wiring.isInUse());
		assertFalse("Wiring is current for: " + tb1, tb1Wiring.isCurrent());

		// Check that old wiring for tb1 is still correct
		assertProvidedResourceWiresEqualsProvidedWires(tb1Wiring, genericNamespaces);
		allTb1Capabilities = tb1Wiring.getProvidedWires(null);
		checkBundleWires(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb14Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiHostTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities, genTestFragmentTb1Capabilities, genTestNoAttrsTb1Capabilities);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings);
		checkNotInUseWirings(new BundleWiring[] {updatedWiring});

		tb1Wiring = tb1.adapt(BundleWiring.class);
		tb2Wiring = tb2.adapt(BundleWiring.class);
		tb3Wiring = tb3.adapt(BundleWiring.class);
		tb4Wiring = tb4.adapt(BundleWiring.class);
		tb5Wiring = tb5.adapt(BundleWiring.class);
		tb14Wiring = tb14.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring, tb14Wiring};

		checkBundleWiring(testBundles.toArray(new Bundle[testBundles.size()]), wirings);

		// test uninstall case
		try {
			tb1.uninstall();
		} catch (BundleException e) {
			fail("Unexpecte error on uninstall", e);
		}

		assertNull("Bundle wiring is not null for bundle: " + tb1, tb1.adapt(BundleWiring.class));
		// note that we do not reget tb1Wiring because it must be null on uninstall from the check above
		tb2Wiring = tb2.adapt(BundleWiring.class);
		tb3Wiring = tb3.adapt(BundleWiring.class);
		tb4Wiring = tb4.adapt(BundleWiring.class);
		tb5Wiring = tb5.adapt(BundleWiring.class);
		tb14Wiring = tb14.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring, tb14Wiring};

		assertTrue("Wiring is not in use for: " + tb1, tb1Wiring.isInUse());
		assertFalse("Wring is current for: " + tb1, tb1Wiring.isCurrent());

		allTb1Capabilities = tb1Wiring.getProvidedWires(null);
		osgiBundleTb1Capabilities = tb1Wiring
				.getProvidedWires(BundleNamespace.BUNDLE_NAMESPACE);
		osgiHostTb1Capabilities = tb1Wiring
				.getProvidedWires(HostNamespace.HOST_NAMESPACE);
		osgiPackageTb1Capabilities = tb1Wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		genTestTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(0));
		genTestMultipleTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(1));
		genTestNoAttrsTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(2));
		genTestFragmentTb1Capabilities = tb1Wiring.getProvidedWires(genericNamespaces.get(3));
		
		checkBundleWires(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb14Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiHostTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities, genTestFragmentTb1Capabilities, genTestNoAttrsTb1Capabilities);

		// test the refresh case
		refreshBundles(null);
		assertFalse(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings);

		// Wirings must be null now since the bundles are not resolved
		assertNull("Bundle wiring is not null for bundle: " + tb1, tb1.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb2, tb2.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb3, tb3.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb4, tb4.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb5, tb5.adapt(BundleWiring.class));
	}

	private void checkBundleWiring(Bundle[] bundles, BundleWiring[] wirings) {
		assertEquals("Lists are not the same size", bundles.length, wirings.length);
		assertProvidedResourceWiresEqualsProvidedWires(wirings,
				Collections.emptyList());
		assertRequiredResourceWiresEqualsRequiredWires(wirings,
				Collections.emptyList());
		for (int i = 0; i < wirings.length; i++) {
			BundleWiring wiring = wirings[i];
			Bundle bundle = bundles[i];
			BundleRevision revision = bundle.adapt(BundleRevision.class);
			assertNotNull("BundleRevision is null for: " + bundle, revision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), revision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), revision.getVersion());

			assertNotNull("BundleWiring is null for bundle: " + bundle, wiring);
			assertEquals("Wrong bundle for wiring: " + bundle, bundle, wiring.getBundle());

			BundleRevision wiringRevision = wiring.getRevision();
			assertNotNull("Wiring revision is null for bundle: " + bundle, wiringRevision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), wiringRevision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), wiringRevision.getVersion());

			assertTrue("Wiring is not current for: " + bundle, wiring.isCurrent());
			assertTrue("Wiring is not in use for: " + bundle, wiring.isInUse());
			
			assertResourceEqualsRevision(wiring);
			assertResourceCapabilitiesEqualsCapabilites(wiring);
			assertResourceRequirementsEqualsRequirements(wiring);
		}
	}

	private void checkNotInUseWirings(BundleWiring[] wirings) {
		for (int i = 0; i < wirings.length; i++) {
			BundleWiring wiring = wirings[i];
			if (wiring == null)
				continue; // fragment case
			assertFalse("Wiring is current for: " + wiring.getBundle(), wiring.isCurrent());
			assertFalse("Wiring is in use for: " + wiring.getBundle(), wiring.isInUse());
			assertNull("Wiring capabilities must be null: " + wiring.getBundle(), wiring.getCapabilities(null));
			assertNull("Wiring requirements must be null: " + wiring.getBundle(), wiring.getRequirements(null));
			assertNull("Wiring class loader must be null: " + wiring.getBundle(), wiring.getClassLoader());
			assertNull("Wiring findEntries must be null: " + wiring.getBundle(), wiring.findEntries("/", "*", 0));
			assertNull("Wiring listResources must be null: " + wiring.getBundle(), wiring.listResources("/", "*", 0));
			assertNull("Wiring providedWires must be null: " + wiring.getBundle(), wiring.getProvidedWires(null));
			assertNull("Wiring requiredWires must be null: " + wiring.getBundle(), wiring.getRequiredWires(null));
		}
	}
	
	private void checkCapabilitiesTb1v110(BundleWiring tb1Wiring, Bundle tb4) {
		assertEquals("Wrong number of capabilities", 9, tb1Wiring.getCapabilities(null).size());
		checkCapabilities(
				tb1Wiring.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE),
				tb1Wiring.getCapabilities(null), 
				BundleNamespace.BUNDLE_NAMESPACE,
				1, 
				tb1Wiring.getRevision());
		checkCapabilities(
				tb1Wiring.getCapabilities(HostNamespace.HOST_NAMESPACE),
				tb1Wiring.getCapabilities(null), 
 HostNamespace.HOST_NAMESPACE,
				1, 
				tb1Wiring.getRevision());
		checkCapabilities(
				tb1Wiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE),
				tb1Wiring.getCapabilities(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				1, 
				tb1Wiring.getRevision());
		checkCapabilities(
				tb1Wiring.getCapabilities("test"), 
				tb1Wiring.getCapabilities(null), 
				"test", 
				1, 
				tb1Wiring.getRevision());
		checkCapabilities(
				tb1Wiring.getCapabilities("test.multiple"), 
				tb1Wiring.getCapabilities(null), 
				"test.multiple", 
				2, 
				tb1Wiring.getRevision());
		checkCapabilities(
				tb1Wiring.getCapabilities("test.fragment"), 
				tb1Wiring.getCapabilities(null), 
				"test.fragment", 
				1, 
				tb4.adapt(BundleRevision.class));
		checkCapabilities(
				tb1Wiring.getCapabilities("test.no.attrs"), 
				tb1Wiring.getCapabilities(null), 
				"test.no.attrs", 
				1, 
				tb1Wiring.getRevision());
	}
	
	private void checkCapabilitiesTb2(BundleWiring tb2Wiring) {
		assertEquals("Wrong number of capabilities", 2, tb2Wiring.getCapabilities(null).size());
		checkCapabilities(
				tb2Wiring.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE),
				tb2Wiring.getCapabilities(null), 
				BundleNamespace.BUNDLE_NAMESPACE,
				1, 
				tb2Wiring.getRevision());
		checkCapabilities(
				tb2Wiring.getCapabilities(HostNamespace.HOST_NAMESPACE),
				tb2Wiring.getCapabilities(null), 
 HostNamespace.HOST_NAMESPACE,
				0, 
				tb2Wiring.getRevision());
		checkCapabilities(
				tb2Wiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE),
				tb2Wiring.getCapabilities(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				0, 
				tb2Wiring.getRevision());
	}
	
	private void checkCapabilitiesTb3(BundleWiring tb3Wiring) {
		assertEquals("Wrong number of capabilities", 3, tb3Wiring.getCapabilities(null).size());
		checkCapabilities(
				tb3Wiring.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE),
				tb3Wiring.getCapabilities(null), 
				BundleNamespace.BUNDLE_NAMESPACE,
				1, 
				tb3Wiring.getRevision());
		checkCapabilities(
				tb3Wiring.getCapabilities(HostNamespace.HOST_NAMESPACE),
				tb3Wiring.getCapabilities(null), 
 HostNamespace.HOST_NAMESPACE,
				1, 
				tb3Wiring.getRevision());
		checkCapabilities(
				tb3Wiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Wiring.getCapabilities(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				0, 
				tb3Wiring.getRevision());
	}
	
	private void checkCapabilitiesTb4(BundleWiring tb4Wiring) {
		assertEquals("Wrong number of capabilities", 1, tb4Wiring.getCapabilities(null).size());
	}
	
	private void checkCapabilitiesTb5(BundleWiring tb5Wiring) {
		assertEquals("Wrong number of capabilities", 3, tb5Wiring.getCapabilities(null).size());
		checkCapabilities(
				tb5Wiring.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE),
				tb5Wiring.getCapabilities(null), 
				BundleNamespace.BUNDLE_NAMESPACE,
				1, 
				tb5Wiring.getRevision());
		checkCapabilities(
				tb5Wiring.getCapabilities(HostNamespace.HOST_NAMESPACE),
				tb5Wiring.getCapabilities(null), 
 HostNamespace.HOST_NAMESPACE,
				1, 
				tb5Wiring.getRevision());
		checkCapabilities(
				tb5Wiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE),
				tb5Wiring.getCapabilities(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				0, 
				tb5Wiring.getRevision());
	}
	
	private void checkCapabilitiesTb14(BundleWiring tb14Wiring) {
		assertEquals("Wrong number of capabilities", 3, tb14Wiring.getCapabilities(null).size());
		checkCapabilities(
				tb14Wiring.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE),
				tb14Wiring.getCapabilities(null), 
				BundleNamespace.BUNDLE_NAMESPACE,
				1, 
				tb14Wiring.getRevision());
		checkCapabilities(
				tb14Wiring.getCapabilities(HostNamespace.HOST_NAMESPACE),
				tb14Wiring.getCapabilities(null), 
 HostNamespace.HOST_NAMESPACE,
				1, 
				tb14Wiring.getRevision());
		checkCapabilities(
				tb14Wiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE),
				tb14Wiring.getCapabilities(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				0, 
				tb14Wiring.getRevision());
	}
	
	private void checkRequirementsTb1v110(BundleWiring tb1Wiring) {
		assertEquals("Wrong number of requirements", 0, tb1Wiring.getRequirements(null).size());
	}
	
	private void checkRequirementsTb2(BundleWiring tb2Wiring) {
		assertEquals("Wrong number of requirements", 2, tb2Wiring.getRequirements(null).size());
		checkRequirements(
				tb2Wiring.getRequirements(PackageNamespace.PACKAGE_NAMESPACE),
				tb2Wiring.getRequirements(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				2, 
				tb2Wiring.getRevision());
	}
	
	private void checkRequirementsTb3(BundleWiring tb3Wiring) {
		assertEquals("Wrong number of requirements", 2, tb3Wiring.getRequirements(null).size());
		checkRequirements(
				tb3Wiring.getRequirements(BundleNamespace.BUNDLE_NAMESPACE),
				tb3Wiring.getRequirements(null), 
				BundleNamespace.BUNDLE_NAMESPACE,
				1, 
				tb3Wiring.getRevision());
		checkRequirements(
				tb3Wiring.getRequirements(HostNamespace.HOST_NAMESPACE),
				tb3Wiring.getRequirements(null), 
 HostNamespace.HOST_NAMESPACE,
				0, 
				tb3Wiring.getRevision());
		checkRequirements(
				tb3Wiring.getRequirements(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Wiring.getRequirements(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				1, 
				tb3Wiring.getRevision());
	}
	
	private void checkRequirementsTb4(BundleWiring tb4Wiring) {
		assertEquals("Wrong number of requirements", 1, tb4Wiring.getRequirements(null).size());
		checkRequirements(
				tb4Wiring.getRequirements(BundleNamespace.BUNDLE_NAMESPACE),
				tb4Wiring.getRequirements(null), 
				BundleNamespace.BUNDLE_NAMESPACE,
				0, 
				tb4Wiring.getRevision());
		checkRequirements(
				tb4Wiring.getRequirements(HostNamespace.HOST_NAMESPACE),
				tb4Wiring.getRequirements(null), 
 HostNamespace.HOST_NAMESPACE,
				1, 
				tb4Wiring.getRevision());
		checkRequirements(
				tb4Wiring.getRequirements(PackageNamespace.PACKAGE_NAMESPACE),
				tb4Wiring.getRequirements(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				0, 
				tb4Wiring.getRevision());
	}
	
	private void checkRequirementsTb5(BundleWiring tb5Wiring) {
		assertEquals("Wrong number of requirements", 11, tb5Wiring.getRequirements(null).size());
		checkRequirements(
				tb5Wiring.getRequirements("test"), 
				tb5Wiring.getRequirements(null), 
				"test", 
				10, 
				tb5Wiring.getRevision());
		checkRequirements(
				tb5Wiring.getRequirements("test.no.attrs"), 
				tb5Wiring.getRequirements(null), 
				"test.no.attrs", 
				1, 
				tb5Wiring.getRevision());
	}
	
	private void checkRequirementsTb14(BundleWiring tb14Wiring) {
		assertEquals("Wrong number of requirements", 1, tb14Wiring.getRequirements(null).size());
		checkRequirements(
				tb14Wiring.getRequirements("test.fragment"), 
				tb14Wiring.getRequirements(null), 
				"test.fragment", 
				1, 
				tb14Wiring.getRevision());
	}

	private void checkBundleWires(
			BundleWiring tb1Wiring, BundleWiring tb2Wiring, BundleWiring tb3Wiring, BundleWiring tb5Wiring, BundleWiring tb14Wiring, 
			Bundle tb4, 
			List<BundleWire> allTb1ProvidedWires, 
			List<BundleWire> osgiBundleTb1ProvidedWires, 
			List<BundleWire> osgiHostTb1ProvidedWires, 
			List<BundleWire> osgiPackageTb1ProvidedWires, 
			List<BundleWire> genTestTb1ProvidedWires, 
			List<BundleWire> genTestMultipleTb1ProvidedWires, 
			List<BundleWire> genTestFragmentTb1ProvidedWires, 
			List<BundleWire> genTestNoAttrsTb1ProvidedWires) {
		assertEquals("Wrong number of wires", 15, allTb1ProvidedWires.size());
		checkWires(osgiBundleTb1ProvidedWires, allTb1ProvidedWires,
				BundleNamespace.BUNDLE_NAMESPACE, 1);
		checkWires(osgiHostTb1ProvidedWires, allTb1ProvidedWires,
				HostNamespace.HOST_NAMESPACE, 1);
		checkWires(osgiPackageTb1ProvidedWires, allTb1ProvidedWires,
				PackageNamespace.PACKAGE_NAMESPACE, 1);
		checkWires(genTestTb1ProvidedWires, allTb1ProvidedWires, "test", 10);
		checkWires(genTestMultipleTb1ProvidedWires, allTb1ProvidedWires, "test.multiple", 0);
		checkWires(genTestFragmentTb1ProvidedWires, allTb1ProvidedWires, "test.fragment", 1);
		checkWires(genTestNoAttrsTb1ProvidedWires, allTb1ProvidedWires, "test.no.attrs", 1);
		
		checkCapabilitiesTb1v110(tb1Wiring, tb4);
		checkCapabilitiesTb2(tb2Wiring);
		checkCapabilitiesTb3(tb3Wiring);
		checkCapabilitiesTb4(tb4.adapt(BundleWiring.class));
		checkCapabilitiesTb5(tb5Wiring);
		checkCapabilitiesTb14(tb14Wiring);
		checkRequirementsTb1v110(tb1Wiring);
		checkRequirementsTb2(tb2Wiring);
		checkRequirementsTb3(tb3Wiring);
		checkRequirementsTb4(tb4.adapt(BundleWiring.class));
		checkRequirementsTb5(tb5Wiring);
		checkRequirementsTb14(tb14Wiring);
		
		checkBundleWire(
				osgiBundleTb1ProvidedWires.get(0), 
				tb1Wiring, 
				tb3Wiring, 
				tb1Wiring.getCapabilities(BundleNamespace.BUNDLE_NAMESPACE)
						.get(0),
				tb3Wiring.getRequirements(BundleNamespace.BUNDLE_NAMESPACE)
						.get(0));
		checkBundleWire(
				osgiHostTb1ProvidedWires.get(0), 
				tb1Wiring, 
				tb4.adapt(BundleWiring.class), 
				tb1Wiring.getCapabilities(HostNamespace.HOST_NAMESPACE).get(0),
				tb4.adapt(BundleWiring.class)
						.getRequirements(HostNamespace.HOST_NAMESPACE).get(0));
		checkBundleWire(
				osgiPackageTb1ProvidedWires.get(0), 
				tb1Wiring, 
				tb2Wiring, 
				tb1Wiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE)
						.get(0),
				tb2Wiring.getRequirements(PackageNamespace.PACKAGE_NAMESPACE)
						.get(1));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(0),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(0));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(1),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(1));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(2),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(2));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(3),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(3));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(4),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(4));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(5),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(5));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(6),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(6));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(7),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(7));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(8),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(8));
		checkBundleWire(
				tb5Wiring.getRequiredWires("test").get(9),
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test").get(0),
				tb5Wiring.getRequirements("test").get(9));
		checkBundleWire(
				genTestNoAttrsTb1ProvidedWires.get(0), 
				tb1Wiring, 
				tb5Wiring, 
				tb1Wiring.getCapabilities("test.no.attrs").get(0),
				tb5Wiring.getRequirements("test.no.attrs").get(0));
		checkBundleWire(
				genTestFragmentTb1ProvidedWires.get(0), 
				tb1Wiring, 
				tb14Wiring, 
				tb1Wiring.getCapabilities("test.fragment").get(0),
				tb14Wiring.getRequirements("test.fragment").get(0));
		
		List<BundleWire> fragments = tb1Wiring
				.getProvidedWires(HostNamespace.HOST_NAMESPACE);
		assertEquals("Wrong number of fragments", 1, fragments.size());
		assertEquals("Wrong fragment", tb4, fragments.get(0).getRequirerWiring().getBundle());
	}

	private void checkBundleWire(
			BundleWire wire, 
			BundleWiring provider, 
			BundleWiring requirer, 
			BundleCapability capability,
			BundleRequirement requirement) {
		assertEquals("Wrong provider", provider.getResource(), wire.getProvider());
		assertEquals("Wrong requirer", requirer.getResource(), wire.getRequirer());
		assertEquals("Wrong provider wiring", provider, wire.getProviderWiring());
		assertEquals("Wrong requirer wiring", requirer, wire.getRequirerWiring());
		assertEquals("Wrong capability", capability, wire.getCapability());
		assertEquals("Wrong requirement", requirement, wire.getRequirement());
		assertTrue("Requirement does not match capability", wire.getRequirement().matches(wire.getCapability()));
		String filterDirective = wire.getRequirement().getDirectives().get(Constants.FILTER_DIRECTIVE);
		if (wire.getRequirement().getNamespace().startsWith("osgi.wiring.")) {
			assertTrue("An osgi.wiring.* requirement has non-empty attribute map.", wire.getRequirement().getAttributes().isEmpty());
			assertNotNull("Null filter directive is not allowed for osgi.wiring.* name spaces.", filterDirective);
			try {
				Filter filter = FrameworkUtil.createFilter(filterDirective);
				assertTrue("Filter directive does not match capability attributes: " + filterDirective, filter.matches(wire.getCapability().getAttributes()));
			} catch (InvalidSyntaxException e) {
				fail("Failed to create filter: " + filterDirective, e);
			}
		}
	}

	public void testGetRevisions() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");
		List<Bundle> testBundles = Arrays.asList(new Bundle[]{tb1, tb2, tb3, tb4, tb5});

		assertTrue(frameworkWiring.resolveBundles(testBundles));

		BundleRevisions tb1Revisions = tb1.adapt(BundleRevisions.class);
		BundleRevisions tb2Revisions = tb2.adapt(BundleRevisions.class);
		BundleRevisions tb3Revisions = tb3.adapt(BundleRevisions.class);
		BundleRevisions tb4Revisions = tb4.adapt(BundleRevisions.class);
		BundleRevisions tb5Revisions = tb5.adapt(BundleRevisions.class);
		BundleRevisions[] revisions = new BundleRevisions[] {tb1Revisions,
				tb2Revisions, tb3Revisions, tb4Revisions, tb5Revisions};

		checkWirings(testBundles.toArray(new Bundle[testBundles.size()]), revisions, 1, true);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		// do not reget the BundleRevisions must survive refresh operations
		checkWirings(testBundles.toArray(new Bundle[testBundles.size()]), revisions, 1, true);

		// test the update case
		Bundle tb8 = install("resolver.tb8.jar");
		BundleRevision tb1Revision1 = tb1.adapt(BundleRevision.class);
		URL content = getContext().getBundle().getEntry("resolver.tb1.v120.jar");
		assertNotNull("Cannot find content for update", content);
		try {
			tb1.update(content.openStream());
		} catch (BundleException e) {
			fail("Unexpected update failure",e);
		} catch (IOException e) {
			fail("Unexpected update failure",e);
		}
		BundleRevision tb1Revision2 = tb1.adapt(BundleRevision.class);
		assertTrue("Could not resolve updated bundle", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb1, tb8})));
		checkWirings(new Bundle[] {tb1}, new BundleRevisions[] {tb1Revisions},
				2,
				true);
		checkRevisions(tb1Revisions, new BundleRevision[] {tb1Revision2,
				tb1Revision1});

		Bundle tb9 = install("resolver.tb9.jar");
		content = getContext().getBundle().getEntry("resolver.tb1.v130.jar");
		assertNotNull("Cannot find content for update", content);
		try {
			tb1.update(content.openStream());
		} catch (BundleException e) {
			fail("Unexpected update failure",e);
		} catch (IOException e) {
			fail("Unexpected update failure",e);
		}
		BundleRevision tb1Revision3 = tb1.adapt(BundleRevision.class);
		assertTrue("Could not resolve updated bundle", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb1, tb9})));
		checkWirings(new Bundle[] {tb1}, new BundleRevisions[] {tb1Revisions},
				3,
				true);
		checkRevisions(tb1Revisions, new BundleRevision[] {tb1Revision3,
				tb1Revision2, tb1Revision1});
		
		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue("Could not resolve test bundles", frameworkWiring.resolveBundles(testBundles));

		// do not reget the BundleRevisions must survive refresh operations
		checkWirings(testBundles.toArray(new Bundle[testBundles.size()]), revisions, 1, true);

		// test uninstall case
		try {
			tb1.uninstall();
		} catch (BundleException e) {
			fail("Unexpected error on uninstall", e);
		}

		// regetting tb1 revisions to test that we can still get it after uninstall
		// this revision will only have 1 revision and it is not current
		tb1Revisions = tb1.adapt(BundleRevisions.class);
		checkWirings(new Bundle[] {tb1}, new BundleRevisions[] {tb1Revisions},
				1,
				false);
		// all other wirings are current and will only have one wiring each
		BundleRevisions[] otherRevisions = new BundleRevisions[] {tb2Revisions,
				tb3Revisions, tb4Revisions, tb5Revisions};
		Bundle[] otherBundes = new Bundle[] {tb2, tb3, tb4, tb5};
		checkWirings(otherBundes, otherRevisions, 1, true);
	}

	private void checkRevisions(BundleRevisions revisions,
			BundleRevision[] bundleRevisions) {
		List<BundleRevision> revisionList = revisions.getRevisions();
		assertEquals("Wrong number of revisions", bundleRevisions.length, revisionList.size());
		int i = 0;
		for (Iterator<BundleRevision> iRevisions = revisionList.iterator(); iRevisions.hasNext(); i++)
			assertEquals("Wrong revision found", bundleRevisions[i], iRevisions.next());
	}

	private void checkWirings(Bundle[] bundles,
			BundleRevisions[] bundlesRevisions,
			int expectedNumRevisions, boolean hasCurrent) {
		assertEquals("Lists are not the same size", bundles.length,
				bundlesRevisions.length);
		for (int i = 0; i < bundlesRevisions.length; i++) {
			Bundle bundle = bundles[i];
			BundleRevision current = bundle.adapt(BundleRevision.class);
			if (hasCurrent) {
				assertNotNull("BundleRevision is null for: " + bundle, current);
				assertEquals("Wrong BSN", bundle.getSymbolicName(), current.getSymbolicName());
				assertEquals("Wrong version", bundle.getVersion(), current.getVersion());
			} else {
				assertNull("BundleRevision must be null for: " + bundle, current);
			}
			BundleRevisions bundleRevisions = bundlesRevisions[i];
			assertNotNull("BundleRevisions is null for bundle: " + bundle,
					bundleRevisions);
			assertEquals("Wrong bundle for revisions", bundle,
					bundleRevisions.getBundle());
			List<BundleRevision> revisions = bundleRevisions.getRevisions();
			if (hasCurrent)
				assertEquals("Wrong current revision for bundle", current, revisions.get(0));
			assertEquals("Wrong number of in use revisions",
					expectedNumRevisions, revisions.size());

			int index = 0;
			for (Iterator<BundleRevision> iter = revisions.iterator(); iter.hasNext(); index++) {
				BundleRevision revision = iter.next();
				BundleWiring wiring = revision.getWiring();
				assertNotNull("bundle wiring is null", wiring);
				Collection<BundleWire> hostWires = wiring
						.getProvidedWires(HostNamespace.HOST_NAMESPACE);
				Collection<BundleWire> fragmentWires = wiring
						.getRequiredWires(HostNamespace.HOST_NAMESPACE);
				assertNotNull("Host wires is null", hostWires);
				assertNotNull("Fragment wires is null", fragmentWires);
				if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
					assertEquals("Wrong number of host wires", 0, hostWires.size());
					assertEquals("Wrong number of fragment wires", 1, fragmentWires.size());
					BundleWire fragmentWire = fragmentWires.iterator().next();
					assertTrue(
							"Fragment wire not found",
							fragmentWire
									.getProviderWiring()
									.getProvidedWires(
											HostNamespace.HOST_NAMESPACE)
									.contains(fragmentWire));
					continue;
				}
				assertEquals("Wrong number of fragment wires", 0, fragmentWires.size());
				if (index == 0 && hasCurrent)
					assertTrue("Wiring is not current for: " + bundle, wiring.isCurrent());
				else
					assertFalse("Wiring is current for: " + bundle, wiring.isCurrent());
				assertTrue("Wiring is not in use for: " + bundle, wiring.isInUse());
			}
		}
	}

	// Note that this test assumes the tests are run on a JavaSE 1.5 vm or higher
	public void testOSGiEE() {
		// First bundle tests that the framework sets reasonable defaults for JavaSE 1.5
		Bundle tb10v100 = install("resolver.tb10.v100.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb10v100})));
		// Second bundle requires an osgi.ee that should not be able to resolve
		Bundle tb10v110 = install("resolver.tb10.v110.jar");
		assertFalse(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb10v110})));
		// Third bundle requires an osgi.ee that is specified by the system.capabilities.extra property
		Bundle tb10v120 = install("resolver.tb10.v120.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb10v120})));
		// forth bundle requires JavaSE [1.3, 1.4)
		Bundle tb10v130 = install("resolver.tb10.v130.jar");
		assertFalse(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb10v130})));
		// fifth bundle requires JavaSE [1.3, 2.0)
		Bundle tb10v140 = install("resolver.tb10.v140.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb10v140})));


		// Test that the ees come from the system bundle
		BundleWiring tb10v100Wiring = tb10v100.adapt(BundleWiring.class);
		assertNotNull("Wiring is null for: " + tb10v100, tb10v100Wiring);
		List<BundleWire> v100RequiredEEs = tb10v100Wiring.getRequiredWires("osgi.ee");
		assertEquals("Wrong number of required osgi.ees", 7, v100RequiredEEs.size());
		Bundle systemBundle = getContext().getBundle(0);
		assertNotNull("SystemBundle is null", systemBundle);
		for (Iterator<BundleWire> ees = v100RequiredEEs.iterator(); ees.hasNext();) {
			assertEquals("Wrong provider for osgi.ee", systemBundle, ees.next().getProviderWiring().getBundle());
		}

		BundleWiring tb10v120Wiring = tb10v120.adapt(BundleWiring.class);
		assertNotNull("Wiring is null for: " + tb10v120, tb10v120Wiring);
		List<BundleWire> v120RequiredEEs = tb10v120Wiring.getRequiredWires("osgi.ee");
		assertEquals("Wrong number of required osgi.ees", 1, v120RequiredEEs.size());
		assertNotNull("SystemBundle is null", systemBundle);
		for (Iterator<BundleWire> ees = v120RequiredEEs.iterator(); ees.hasNext();) {
			assertEquals("Wrong provider for osgi.ee", systemBundle, ees.next().getProviderWiring().getBundle());
		}
	}

	public void testRequiredExecutionEnvironment() {
		// install bundles that use BREE header
		Bundle tb1 = install("wiring.ee.tb1.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1})));
		Bundle tb2 = install("wiring.ee.tb2.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb2})));
		Bundle tb3 = install("wiring.ee.tb3.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb3})));
		Bundle tb4 = install("wiring.ee.tb4.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb4})));
		Bundle tb5 = install("wiring.ee.tb5.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb5})));
		Bundle tb6 = install("wiring.ee.tb6.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb6})));

		checkOSGiEEWiring(tb1, "OSGi/Minimum");
		checkOSGiEEWiring(tb2, "JavaSE");
		checkOSGiEEWiring(tb3, "AA/BB");
		checkOSGiEEWiring(tb4, "EE/FF-YY");
		checkOSGiEEWiring(tb5, "GG-XX/HH");
		checkOSGiEEWiring(tb6, "II-1.0/JJ-2.0");
	}

	private void checkOSGiEEWiring(Bundle tb, String eeName) {
		BundleWiring wiring = tb.adapt(BundleWiring.class);
		assertNotNull("Null wiring for resolved bundle: " + tb, wiring);

		List<BundleWire> eeWires = wiring.getRequiredWires(ExecutionEnvironmentNamespace.EXECUTION_ENVIRONMENT_NAMESPACE);
		assertNotNull("Null ee wires: " + tb, eeWires);
		assertEquals("Wrong number of ee wires: " + tb, 1, eeWires.size());

		BundleWire eeWire = eeWires.iterator().next();
		BundleCapability eeCapability = eeWire.getCapability();
		assertEquals("Wrong namespace", ExecutionEnvironmentNamespace.EXECUTION_ENVIRONMENT_NAMESPACE, eeCapability.getNamespace());
		assertEquals("Wrong ee name", eeCapability.getAttributes().get(ExecutionEnvironmentNamespace.EXECUTION_ENVIRONMENT_NAMESPACE), eeName);
	}

	public void testOptionalRequireCapability() {
		Bundle tb11 = install("resolver.tb11.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb11})));

		BundleWiring tb11Wiring = tb11.adapt(BundleWiring.class);
		assertNotNull("Wiring is null", tb11Wiring);
		List<BundleRequirement> requirements = tb11Wiring.getRequirements(null);
		assertNotNull("Requirements is null", requirements);
		assertEquals("Wrong number of requirements", 0, requirements.size());
		List<BundleWire> wires = tb11Wiring.getRequiredWires(null);
		assertNotNull("Wires is null", wires);
		assertEquals("Wrong number of wires", 0, wires.size());

		Bundle tb12 = install("resolver.tb12.jar");
		refreshBundles(Arrays.asList(new Bundle[] {tb11}));

		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb11, tb12})));
		tb11Wiring = tb11.adapt(BundleWiring.class);
		assertNotNull("Wiring is null", tb11Wiring);
		requirements = tb11Wiring.getRequirements(null);
		assertNotNull("Requirements is null", requirements);
		assertEquals("Wrong number of requirements", 1, requirements.size());
		wires = tb11Wiring.getRequiredWires(null);
		assertNotNull("Wires is null", wires);
		assertEquals("Wrong number of wires", 1, wires.size());
		BundleWire wire = wires.get(0);
		assertEquals("Wrong provider", tb12, wire.getProviderWiring().getBundle());
		assertEquals("Wrong requirer", tb11, wire.getRequirerWiring().getBundle());
	}

	// Note that this test is done in GetEntryResourceTest
	//	public void testFindEntries() {
	//		fail("Need to write a findEntries test.");
	//	}

	public void testListResources() {
		install("wiring.base.jar");
		// force base to resolve first
		assertTrue("Could not resolve test bundles", frameworkWiring.resolveBundles(bundles));

		Bundle exporter = install("wiring.exporter.jar");
		Bundle importer = install("wiring.importer.jar");
		Bundle requirer = install("wiring.requirer.jar");
		install("wiring.reexport.jar");

		assertTrue("Could not resolve test bundles", frameworkWiring.resolveBundles(bundles));

		BundleWiring exporterWiring = exporter.adapt(BundleWiring.class);
		BundleWiring importerWiring = importer.adapt(BundleWiring.class);
		BundleWiring requirerWiring = requirer.adapt(BundleWiring.class);

		// test that empty lists are returned when no resources are found
		Collection<String> empty = exporterWiring.listResources("", "*.notfound",
				BundleWiring.LISTRESOURCES_RECURSE);
		assertNotNull("Should return empty list", empty);
		assertEquals("Should have 0 resources", 0, empty.size());
		empty = importerWiring.listResources("", "*.notfound", BundleWiring.LISTRESOURCES_RECURSE);
		assertNotNull("Should return empty list", empty);
		assertEquals("Should have 0 resources", 0, empty.size());
		empty = requirerWiring.listResources("", "*.notfound", BundleWiring.LISTRESOURCES_RECURSE);
		assertNotNull("Should return empty list", empty);
		assertEquals("Should have 0 resources", 0, empty.size());

		// test exporter resources
		Collection<String> rootResources = exporterWiring.listResources("/root",
				"*.txt", 0);
		assertEquals("Wrong number of resources", 1, rootResources.size());
		assertEquals("Wrong resource", "root/root.export.txt", rootResources
				.iterator().next());
		checkResources(exporterWiring.getClassLoader(), rootResources);

		// note that root.B package has been substituted
		List<String> expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt", 
				   "root/A/b/b.export.txt", 
				  "root/A/A.export.txt",
				  "root/A/A.reexport.txt",
				   "root/B/a/a.export.txt",
				   "root/B/b/b.export.txt",
				  "root/B/B.base.txt", // this has been substituted
				  "root/C/C.reexport.txt",
				"root/root.export.txt"});
		rootResources = exporterWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(exporterWiring.getClassLoader(), rootResources);

		// test local resources of exporter; note that root.B resources are not available
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt", 
				   "root/A/b/b.export.txt", 
				  "root/A/A.export.txt", 
				   "root/B/a/a.export.txt",
				   "root/B/b/b.export.txt",
				"root/root.export.txt"});
		rootResources = exporterWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(exporterWiring.getClassLoader(), rootResources);

		// test importer resources
		rootResources = importerWiring.listResources("/root", "*.txt", 0);
		assertEquals("Wrong number of resources", 1, rootResources.size());
		assertEquals("Wrong resource", "root/root.local.txt", rootResources
				.iterator().next());
		checkResources(importerWiring.getClassLoader(), rootResources);

		// note that root.B package has been substituted
		rootResources = importerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.local.txt", 
				   "root/A/b/b.local.txt", 
				  "root/A/A.local.txt", 
				   "root/B/a/a.export.txt",
				   "root/B/b/b.export.txt",
				  "root/B/B.base.txt", // this has been substituted
				"root/root.local.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(importerWiring.getClassLoader(), rootResources);

		// test local resources, anything shadowed by an import must not be included
		rootResources = importerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.local.txt", 
				   "root/A/b/b.local.txt", 
				  "root/A/A.local.txt", 
				"root/root.local.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(importerWiring.getClassLoader(), rootResources);

		// test the require bundle case
		rootResources = requirerWiring.listResources("/root", "*.txt", 0);
		expected = Arrays.asList(new String[] {
				"root/root.export.txt",
				"root/root.local.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(requirerWiring.getClassLoader(), rootResources);

		// test require case; no shadowing of local resources; still have root.B substituted
		rootResources = requirerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt", 
				   "root/A/a/a.local.txt", 
				   "root/A/b/b.export.txt", 
				   "root/A/b/b.local.txt", 
				  "root/A/A.export.txt",
				  "root/A/A.reexport.txt",
				  "root/A/A.local.txt",
				   "root/B/a/a.export.txt",
				   "root/B/a/a.local.txt",
				   "root/B/b/b.export.txt",
				   "root/B/b/b.local.txt",
				  "root/B/B.base.txt", // this has been substituted
				  "root/B/B.local.txt",
				  "root/C/C.reexport.txt",
				  "root/root.export.txt",
				"root/root.local.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(requirerWiring.getClassLoader(), rootResources);

		// test require local resources; not there is no shadowing so we get all local resources
		rootResources = requirerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.local.txt", 
				   "root/A/b/b.local.txt", 
				  "root/A/A.local.txt",
				   "root/B/a/a.local.txt",
				   "root/B/b/b.local.txt",
				  "root/B/B.local.txt",
				"root/root.local.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(requirerWiring.getClassLoader(), rootResources);

		// install fragments to test
		install("wiring.exporter.frag.jar");
		install("wiring.importer.frag.jar");
		install("wiring.requirer.frag.jar");

		refreshBundles(bundles);
		assertTrue("failed to resolve test fragments", frameworkWiring.resolveBundles(bundles));

		// test that old wirings return null
		rootResources = exporterWiring.listResources("/root", "*.txt", 0);
		assertNull("Old wiring still accesses resources", rootResources);
		rootResources = importerWiring.listResources("/root", "*.txt", 0);
		assertNull("Old wiring still accesses resources", rootResources);
		rootResources = requirerWiring.listResources("/root", "*.txt", 0);
		assertNull("Old wiring still accesses resources", rootResources);

		// get the latest wiring
		exporterWiring = exporter.adapt(BundleWiring.class);
		importerWiring = importer.adapt(BundleWiring.class);
		requirerWiring = requirer.adapt(BundleWiring.class);

		// test exporter resources
		expected = Arrays.asList(new String[] {
				"root/root.export.txt",
				"root/root.frag.txt"});
		rootResources = exporterWiring.listResources("/root", "*.txt", 0);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(exporterWiring.getClassLoader(), rootResources);

		// note that root.B package has been substituted
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt",
				   "root/A/a/a.frag.txt",
				   "root/A/b/b.export.txt",
				   "root/A/b/b.frag.txt", 
				  "root/A/A.export.txt", 
				  "root/A/A.frag.txt",
				  "root/A/A.reexport.txt",
				   "root/B/a/a.export.txt",
				   "root/B/a/a.frag.txt",
				   "root/B/b/b.export.txt",
				   "root/B/b/b.frag.txt",
				  "root/B/B.base.txt", // this has been substituted
				  "root/C/C.reexport.txt",
			    "root/root.export.txt",
				"root/root.frag.txt"});
		rootResources = exporterWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(exporterWiring.getClassLoader(), rootResources);

		// test local resources of exporter; note that root.B resources are not available
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt",
				   "root/A/a/a.frag.txt",
				   "root/A/b/b.export.txt",
				   "root/A/b/b.frag.txt", 
				  "root/A/A.export.txt", 
				  "root/A/A.frag.txt",
				   "root/B/a/a.export.txt",
				   "root/B/a/a.frag.txt",
				   "root/B/b/b.export.txt",
				   "root/B/b/b.frag.txt",
				"root/root.export.txt",
				"root/root.frag.txt"});
		rootResources = exporterWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(exporterWiring.getClassLoader(), rootResources);

		// test importer resources
		expected = Arrays.asList(new String[] {
				"root/root.local.txt",
				"root/root.frag.txt"});
		rootResources = importerWiring.listResources("/root", "*.txt", 0);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(importerWiring.getClassLoader(), rootResources);

		// note that root.B package has been substituted
		rootResources = importerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.local.txt",
				   "root/A/a/a.frag.txt",
				   "root/A/b/b.local.txt",
				   "root/A/b/b.frag.txt",
				  "root/A/A.local.txt",
				  "root/A/A.frag.txt", 
				   "root/B/a/a.export.txt", 
				   "root/B/a/a.frag.txt",
				   "root/B/b/b.export.txt",
				   "root/B/b/b.frag.txt",
				  "root/B/B.base.txt", // this has been substituted
				"root/root.local.txt",
				"root/root.frag.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(importerWiring.getClassLoader(), rootResources);

		// test local resources, anything shadowed by an import must not be included
		rootResources = importerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.local.txt",
				   "root/A/a/a.frag.txt",
				   "root/A/b/b.local.txt",
				   "root/A/b/b.frag.txt",
				  "root/A/A.local.txt",
				  "root/A/A.frag.txt", 
				"root/root.local.txt",
				"root/root.frag.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(importerWiring.getClassLoader(), rootResources);

		// test the require bundle case
		rootResources = requirerWiring.listResources("/root", "*.txt", 0);
		expected = Arrays.asList(new String[] {
				"root/root.export.txt",
				"root/root.local.txt",
				"root/root.frag.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(requirerWiring.getClassLoader(), rootResources);

		// test require case; no shadowing of local resources; still have root.B substituted
		rootResources = requirerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt", 
				   "root/A/a/a.local.txt",
				   "root/A/a/a.frag.txt",
				   "root/A/b/b.export.txt", 
				   "root/A/b/b.local.txt",
				   "root/A/b/b.frag.txt",
				  "root/A/A.export.txt",
				  "root/A/A.reexport.txt",
				  "root/A/A.local.txt",
				  "root/A/A.frag.txt",
				   "root/B/a/a.export.txt",
				   "root/B/a/a.local.txt",
				   "root/B/a/a.frag.txt",
				   "root/B/b/b.export.txt",
				   "root/B/b/b.local.txt",
				   "root/B/b/b.frag.txt",
				  "root/B/B.base.txt", // this has been substituted
				  "root/B/B.local.txt",
				  "root/B/B.frag.txt",
				  "root/C/C.reexport.txt",
				"root/root.export.txt",
				"root/root.local.txt",
				"root/root.frag.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(requirerWiring.getClassLoader(), rootResources);

		// test require local resources; not there is no shadowing so we get all local resources
		rootResources = requirerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.local.txt",
				   "root/A/a/a.frag.txt",
				   "root/A/b/b.local.txt",
				   "root/A/b/b.frag.txt",
				  "root/A/A.local.txt",
				  "root/A/A.frag.txt",
				   "root/B/a/a.local.txt",
				   "root/B/a/a.frag.txt",
				   "root/B/b/b.local.txt",
				   "root/B/b/b.frag.txt",
				  "root/B/B.local.txt",
				  "root/B/B.frag.txt",
				"root/root.local.txt",
				"root/root.frag.txt"
		});
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(requirerWiring.getClassLoader(), rootResources);

		// test update case
		URL updateContent = getContext().getBundle().getEntry("wiring.exporter.v2.jar");
		assertNotNull("Could not find update content", updateContent);
		try {
			exporter.update(updateContent.openStream());
		} catch (Exception e) {
			fail("Failed to update bundle", e);
		}
		assertTrue("Failed to resolve bundle", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {exporter})));
		BundleWiring oldExporterWiring = exporterWiring;
		BundleWiring newExporterWiring = exporter.adapt(BundleWiring.class);

		// Do a sanity check to make sure the old wiring still works
		// note that root.B package has been substituted
		// note that the fragment should still be providing content to the old wiring
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt",
				   "root/A/a/a.frag.txt",
				   "root/A/b/b.export.txt",
				   "root/A/b/b.frag.txt", 
				  "root/A/A.export.txt", 
				  "root/A/A.frag.txt",
				  "root/A/A.reexport.txt",
				   "root/B/a/a.export.txt",
				   "root/B/a/a.frag.txt",
				   "root/B/b/b.export.txt",
				   "root/B/b/b.frag.txt",
				  "root/B/B.base.txt", // this has been substituted
				  "root/C/C.reexport.txt",
			    "root/root.export.txt",
				"root/root.frag.txt"});
		rootResources = oldExporterWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(oldExporterWiring.getClassLoader(), rootResources);

		// check the new wiring; no fragment attached
		// note that root.B package has been substituted
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt", 
				   "root/A/b/b.export.txt", 
				  "root/A/A.export.txt",
				  "root/A/A.reexport.txt",
				   "root/B/a/a.export.txt",
				   "root/B/b/b.export.txt",
				  "root/B/B.base.txt", // this has been substituted
				  "root/C/C.reexport.txt",
				"root/root.export.txt"});
		rootResources = newExporterWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE);
		assertResourcesEquals("Wrong resources", expected, rootResources);
		checkResources(newExporterWiring.getClassLoader(), rootResources);
	}

	public void testBSNMatchingAttributes() {
		Bundle tb13a = install("resolver.tb13a.jar");
		Bundle tb13b = install("resolver.tb13b.jar");
		Bundle tb13Client1 = install("resolver.tb13.client1.jar");
		Bundle tb13Client2 = install("resolver.tb13.client2.jar");
		Bundle tb13Client3 = install("resolver.tb13.client3.jar");
		Bundle tb13Client4 = install("resolver.tb13.client4.jar");
		Bundle tb13Frag1 = install("resolver.tb13.frag1.jar");
		Bundle tb13Frag2 = install("resolver.tb13.frag2.jar");
		Bundle tb13Frag3 = install("resolver.tb13.frag3.jar");
		Bundle tb13Frag4 = install("resolver.tb13.frag4.jar");

		assertFalse(frameworkWiring.resolveBundles(bundles));

		assertEquals("Unexpected state for: " + tb13a.getSymbolicName(), Bundle.RESOLVED, tb13a.getState());
		assertEquals("Unexpected state for: " + tb13b.getSymbolicName(), Bundle.RESOLVED, tb13b.getState());
		assertEquals("Unexpected state for: " + tb13Client1.getSymbolicName(), Bundle.INSTALLED, tb13Client1.getState());
		assertEquals("Unexpected state for: " + tb13Client2.getSymbolicName(), Bundle.RESOLVED, tb13Client2.getState());
		assertEquals("Unexpected state for: " + tb13Client3.getSymbolicName(), Bundle.INSTALLED, tb13Client3.getState());
		assertEquals("Unexpected state for: " + tb13Client4.getSymbolicName(), Bundle.RESOLVED, tb13Client4.getState());
		assertEquals("Unexpected state for: " + tb13Frag1.getSymbolicName(), Bundle.INSTALLED, tb13Frag1.getState());
		assertEquals("Unexpected state for: " + tb13Frag2.getSymbolicName(), Bundle.RESOLVED, tb13Frag2.getState());
		assertEquals("Unexpected state for: " + tb13Frag3.getSymbolicName(), Bundle.INSTALLED, tb13Frag3.getState());
		assertEquals("Unexpected state for: " + tb13Frag4.getSymbolicName(), Bundle.RESOLVED, tb13Frag4.getState());

		BundleWiring tb13Client1Wiring = tb13Client1.adapt(BundleWiring.class);
		assertNull("Expected null Wiring: " + tb13Client1.getSymbolicName(), tb13Client1Wiring);
		BundleWiring tb13Client3Wiring = tb13Client3.adapt(BundleWiring.class);
		assertNull("Expected null Wiring: " + tb13Client3.getSymbolicName(), tb13Client3Wiring);

		BundleWiring tb13aWiring = tb13a.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13a.getSymbolicName(), tb13aWiring);
		BundleWiring tb13bWiring = tb13b.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13b.getSymbolicName(), tb13bWiring);
		BundleWiring tb13Client2Wiring = tb13Client2.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13Client2.getSymbolicName(), tb13Client2Wiring);
		BundleWiring tb13Client4Wiring = tb13Client4.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13Client4.getSymbolicName(), tb13Client4Wiring);
		
		List<BundleRequirement> client2Requirements = tb13Client2Wiring
				.getRequirements(BundleNamespace.BUNDLE_NAMESPACE);
		assertEquals("Unexpected number of requirements", 1, client2Requirements.size());
		assertEquals("Wrong provider", tb13Client2, client2Requirements.get(0).getRevision().getBundle());
		
		List<BundleWire> client2RequiredWires = tb13Client2Wiring
				.getRequiredWires(BundleNamespace.BUNDLE_NAMESPACE);
		assertEquals("Unexpected number of wires", 1, client2RequiredWires.size());
		assertEquals("Wrong provider", tb13a, client2RequiredWires.get(0).getProviderWiring().getBundle());

		List<BundleWire> client4RequiredWires = tb13Client4Wiring
				.getRequiredWires(BundleNamespace.BUNDLE_NAMESPACE);
		assertEquals("Unexpected number of wires", 1, client4RequiredWires.size());
		assertEquals("Wrong provider", tb13b, client4RequiredWires.get(0).getProviderWiring().getBundle());
		
		List<BundleWire> tb13aProvidedWires = tb13aWiring
				.getProvidedWires(HostNamespace.HOST_NAMESPACE);
		assertEquals("Unexpected number of wires", 1, tb13aProvidedWires.size());
		assertEquals("Wrong fragment attached", tb13Frag2, tb13aProvidedWires.get(0).getRequirerWiring().getBundle());
		
		BundleWiring tb13Frag2Wiring = tb13Frag2.adapt(BundleWiring.class);
		assertNotNull("Fragments must be adaptable to BundleWiring", tb13Frag2Wiring);
		List<BundleWire> tb13Frag2RequiredWires = tb13Frag2Wiring
				.getRequiredWires(HostNamespace.HOST_NAMESPACE);
		assertEquals("Unexpected number of wires", 1, tb13Frag2RequiredWires.size());
		assertEquals("Wrong host attached", tb13a, tb13Frag2RequiredWires.get(0).getProviderWiring().getBundle());
		
		List<BundleWire> tb13bProvidedWires = tb13bWiring
				.getProvidedWires(HostNamespace.HOST_NAMESPACE);
		assertEquals("Unexpected number of wires", 1, tb13bProvidedWires.size());
		assertEquals("Wrong fragment attached", tb13Frag4, tb13bProvidedWires.get(0).getRequirerWiring().getBundle());
		
		BundleWiring tb13Frag4Wiring = tb13Frag4.adapt(BundleWiring.class);
		assertNotNull("Fragments must be adaptable to BundleWiring", tb13Frag2Wiring);
		List<BundleWire> tb13Frag4RequiredWires = tb13Frag4Wiring
				.getRequiredWires(HostNamespace.HOST_NAMESPACE);
		assertEquals("Unexpected number of wires", 1, tb13Frag4RequiredWires.size());
		assertEquals("Wrong host attached", tb13b, tb13Frag4RequiredWires.get(0).getProviderWiring().getBundle());
	}

	private void assertResourcesEquals(String message, Collection<?> expected, Collection<?> actual) {
		if (expected.size() != actual.size())
			fail(message + ": Collections are not the same size: " + expected + ":  " + actual);
		assertTrue(message + ": Colections do not contain the same content: " + expected + ":  " + actual, actual.containsAll(expected));
	}

	private void checkResources(ClassLoader cl, Collection<String> resources) {
		for(Iterator<String> iResources = resources.iterator(); iResources.hasNext();) {
			String path = iResources.next();
			URL resource = cl.getResource(path);
			assertNotNull("Could not find resource: " + path, resource);
		}
	}
	
	/**
	 * Ensures an implementation delivers a bundle wiring's provided wires in
	 * the proper order. The ordering rules are as follows.
	 * 
	 * (1) For a given name space, the list contains the wires in the order the
	 * capabilities were specified in the manifests of the bundle revision and
	 * the attached fragments of this bundle wiring.
	 * 
	 * (2) There is no ordering defined between wires in different namespaces.
	 * 
	 * (3) There is no ordering defined between multiple wires for the same
	 * capability, but the wires must be contiguous, and the group must be
	 * ordered as in (1).
	 */
	public void testProvidedWiresOrdering() {
		Bundle tb1 = install("wiring.tb1.jar");
		Bundle tb2 = install("wiring.tb2.jar");
		Bundle tb3 = install("wiring.tb3.jar");
		Bundle tb4 = install("wiring.tb4.jar");
		assertTrue("Bundles should have resolved", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb1,tb2,tb3,tb4})));
		BundleWiring tb1Wiring = tb1.adapt(BundleWiring.class);
		BundleWiring tb2Wiring = tb2.adapt(BundleWiring.class);
		BundleWiring tb3Wiring = tb3.adapt(BundleWiring.class);
		BundleWiring tb4Wiring = tb4.adapt(BundleWiring.class);
		List<BundleWire> tb1Wires = tb1Wiring
				.getProvidedWires(PackageNamespace.PACKAGE_NAMESPACE);
		assertEquals("Wrong number of wires", 6, tb1Wires.size());
		assertEquals(
				"Wrong order",
				"org.osgi.test.cases.framework.wiring.tb1a",
				tb1Wires.get(0).getCapability().getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		assertTrue("Wrong requirer", tb1Wires.get(0).getRequirerWiring().equals(tb2Wiring) || tb1Wires.get(0).getRequirerWiring().equals(tb3Wiring));
		assertEquals(
				"Wrong order",
				"org.osgi.test.cases.framework.wiring.tb1a",
				tb1Wires.get(1).getCapability().getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		assertTrue("Wrong requirer", tb1Wires.get(1).getRequirerWiring().equals(tb2Wiring) || tb1Wires.get(1).getRequirerWiring().equals(tb3Wiring));
		assertEquals(
				"Wrong order",
				"org.osgi.test.cases.framework.wiring.tb1b",
				tb1Wires.get(2).getCapability().getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		assertTrue("Wrong requirer", tb1Wires.get(2).getRequirerWiring().equals(tb2Wiring) || tb1Wires.get(2).getRequirerWiring().equals(tb4Wiring));
		assertEquals(
				"Wrong order",
				"org.osgi.test.cases.framework.wiring.tb1b",
				tb1Wires.get(3).getCapability().getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		assertTrue("Wrong requirer", tb1Wires.get(3).getRequirerWiring().equals(tb2Wiring) || tb1Wires.get(3).getRequirerWiring().equals(tb4Wiring));
		assertEquals(
				"Wrong order",
				"org.osgi.test.cases.framework.wiring.tb1c",
				tb1Wires.get(4).getCapability().getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		assertTrue("Wrong requirer", tb1Wires.get(4).getRequirerWiring().equals(tb3Wiring) || tb1Wires.get(4).getRequirerWiring().equals(tb4Wiring));
		assertEquals(
				"Wrong order",
				"org.osgi.test.cases.framework.wiring.tb1c",
				tb1Wires.get(5).getCapability().getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		assertTrue("Wrong requirer", tb1Wires.get(5).getRequirerWiring().equals(tb3Wiring) || tb1Wires.get(5).getRequirerWiring().equals(tb4Wiring));
	}
	
	/**
	 * Basic test for support of the DynamicImport-Package requirement.
	 */
	public void testDynamicImportPackage() throws Exception {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("wiring.tb1.jar");
		Bundle tb3 = install("wiring.tb5.jar");
		
		assertTrue("The bundles should have resolved", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[]{tb1,tb2,tb3})));

		BundleRevision tb3Revision = tb3.adapt(BundleRevision.class);
		
		BundleWiring tb1Wiring = tb1.adapt(BundleWiring.class);
		BundleWiring tb2Wiring = tb2.adapt(BundleWiring.class);
		BundleWiring tb3Wiring = tb3.adapt(BundleWiring.class);
		
		checkRequirements(
				tb3Revision
						.getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Revision.getDeclaredRequirements(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				1, 
				tb3Revision);
		checkRequirements(
				tb3Wiring.getRequirements(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Wiring.getRequirements(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				1, 
				tb3Wiring.getRevision());
		checkWires(
				tb3Wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Wiring.getRequiredWires(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				0);
		
		tb3.loadClass("org.osgi.test.cases.framework.resolver.tb1.Test");
		checkWires(
				tb3Wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Wiring.getRequiredWires(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				1);
		checkBundleWire(
				tb3Wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE)
						.get(0),
				tb1Wiring,
				tb3Wiring,
 tb1Wiring
						.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE)
						.get(0),
				tb3Wiring.getRequirements(PackageNamespace.PACKAGE_NAMESPACE)
						.get(0));
		
		tb3.loadClass("org.osgi.test.cases.framework.wiring.tb1a.PlaceHolder");
		checkWires(
				tb3Wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Wiring.getRequiredWires(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				2);
		BundleCapability bc = tb2Wiring.getCapabilities(
				PackageNamespace.PACKAGE_NAMESPACE).get(0);
		assertEquals("Wrong attribute",
				"org.osgi.test.cases.framework.wiring.tb1a", bc.getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		checkBundleWire(
				tb3Wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE)
						.get(1),
				tb2Wiring,
				tb3Wiring,
				bc,
 tb3Wiring
						.getRequirements(PackageNamespace.PACKAGE_NAMESPACE)
						.get(0));
		
		tb3.loadClass("org.osgi.test.cases.framework.wiring.tb1b.PlaceHolder");
		checkWires(
				tb3Wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE),
				tb3Wiring.getRequiredWires(null), 
				PackageNamespace.PACKAGE_NAMESPACE,
				3);
		bc = tb2Wiring.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE).get(
				1);
		assertEquals("Wrong attribute",
				"org.osgi.test.cases.framework.wiring.tb1b", bc.getAttributes()
						.get(PackageNamespace.PACKAGE_NAMESPACE));
		checkBundleWire(
				tb3Wiring.getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE)
						.get(2),
				tb2Wiring,
				tb3Wiring,
				bc,
 tb3Wiring
						.getRequirements(PackageNamespace.PACKAGE_NAMESPACE)
						.get(0));
		
		
		
	}
}
