/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.framework.junit.wiring;

import static org.osgi.test.support.OSGiTestCaseProperties.getScaling;
import static org.osgi.test.support.OSGiTestCaseProperties.getTimeout;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.namespace.HostNamespace;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Namespace;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.test.support.wiring.Wiring;

import junit.framework.AssertionFailedError;

public class FrameworkWiringTests extends WiringTest {
	protected void tearDown() throws Exception {
		for (Iterator<Bundle> iBundles = bundles.iterator(); iBundles.hasNext();)
			try {
				iBundles.next().uninstall();
			} catch (BundleException e) {
				// nothing
			} catch (IllegalStateException e) {
				// happens if the test uninstalls the bundle itself
			}
		Wiring.synchronousRefreshBundles(getContext(), bundles);
		bundles.clear();
	}

	public void testRefreshListeners() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");

		Collection<Bundle> testBundles = Arrays.asList(new Bundle[]{tb1, tb2, tb3, tb4, tb5});

		final boolean[] called = new boolean[] {false};
		final AssertionFailedError error[] = new AssertionFailedError[1];
		frameworkWiring.refreshBundles(testBundles, new FrameworkListener[] { 
				new FrameworkListener() {
					public void frameworkEvent(FrameworkEvent event) {
						synchronized (called) {
							try {
								called[0] = true;
								called.notify();
								assertEquals("Unexpected event type", FrameworkEvent.PACKAGES_REFRESHED, event.getType());
							} catch (AssertionFailedError e) {
								if (error[0] == null)
									error[0] = e;
							}
						}
					}
				}
			}
		);
		final long endTime = System.currentTimeMillis() + getTimeout()
				* getScaling();
		synchronized (called) {
			while (!called[0])
				try {
					called.wait(endTime - System.currentTimeMillis());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					fail("Unexepected interruption.", e);
				}
			if (!called[0] && (System.currentTimeMillis() > endTime)) {
				fail("Failed to call specified listener");
			}
			if (error[0] != null)
				throw error[0];
		}
	}

	public void testResolveBundles() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");

		Collection<Bundle> testBundles = Arrays.asList(new Bundle[]{tb1, tb2, tb3, tb4, tb5});
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		assertEquals("Wrong state for bundle: " + tb1, Bundle.RESOLVED, tb1.getState());
		assertEquals("Wrong state for bundle: " + tb2, Bundle.RESOLVED, tb2.getState());
		assertEquals("Wrong state for bundle: " + tb3, Bundle.RESOLVED, tb3.getState());
		assertEquals("Wrong state for bundle: " + tb4, Bundle.RESOLVED, tb4.getState());
		assertEquals("Wrong state for bundle: " + tb5, Bundle.RESOLVED, tb5.getState());
	}

	public void testDependencyClosure() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");

		Collection<Bundle> testBundles = Arrays.asList(new Bundle[]{tb1, tb2, tb3, tb4, tb5});
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		Collection<Bundle> closure = frameworkWiring.getDependencyClosure(Arrays.asList(new Bundle[]{tb1}));
		assertEquals("Wrong number in closure", 5, closure.size());
		assertTrue("Wrong bundles in closure: " + closure, closure.containsAll(testBundles));
		assertTrue("Wrong bundles in closure: " + closure, testBundles.containsAll(closure));

		closure = frameworkWiring.getDependencyClosure(Arrays.asList(new Bundle[]{tb2}));
		assertEquals("Wrong number in closure", 1, closure.size());
		assertTrue("Wrong bundle in closure", closure.contains(tb2));

		// Test fragment, this pulls in the host which pulls in everything else
		closure = frameworkWiring.getDependencyClosure(Arrays.asList(new Bundle[]{tb4}));
		assertEquals("Wrong number in closure", 5, closure.size());
		assertTrue("Wrong bundles in closure: " + closure, closure.containsAll(testBundles));
		assertTrue("Wrong bundles in closure: " + closure, testBundles.containsAll(closure));
	}

	public void testRemovalPending() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");

		Collection<Bundle> testBundles = Arrays.asList(new Bundle[]{tb1, tb2, tb3, tb4, tb5});
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		// TODO should investigate why other tests are not cleaning up their uninstalled bundles
		// for now doing a refresh of all removal pending to ensure a clean state
		Wiring.synchronousRefreshBundles(getContext());
		// Need to make sure there are no removal pendings already present.
		Collection<Bundle> removals = frameworkWiring.getRemovalPendingBundles();
		assertEquals("Removal pendings are left over: " + removals.toString(), 0, removals.size());
		try {
			tb1.uninstall();
		} catch (BundleException e) {
			fail("Failed to uninstall bundle", e);
		}

		removals = frameworkWiring.getRemovalPendingBundles();
		assertEquals("Wrong number of removals", 1, removals.size());
		assertTrue("Wrong bundle in removals", removals.contains(tb1));

		Collection<Bundle> closure = frameworkWiring.getDependencyClosure(removals);
		assertEquals("Wrong number in closure", 5, closure.size());
		assertTrue("Wrong bundles in closure: " + closure, closure.containsAll(testBundles));
		assertTrue("Wrong bundles in closure: " + closure, testBundles.containsAll(closure));

		try {
			tb4.uninstall();
		} catch (BundleException e) {
			fail("Failed to uninstall bundle", e);
		}

		removals = frameworkWiring.getRemovalPendingBundles();
		assertEquals("Wrong number of removals", 2, removals.size());
		assertTrue("Wrong bundle in removals", removals.contains(tb1));
		assertTrue("Wrong bundle in removals", removals.contains(tb4));

		closure = frameworkWiring.getDependencyClosure(removals);
		assertEquals("Wrong number in closure", 5, closure.size());
		assertTrue("Wrong bundles in closure: " + closure, closure.containsAll(testBundles));
		assertTrue("Wrong bundles in closure: " + closure, testBundles.containsAll(closure));
	}

	public void testFindResources() {
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb1 = install("resolver.tb1.v110.jar");

		BundleRevision systemRevision = getContext().getBundle(
				Constants.SYSTEM_BUNDLE_LOCATION).adapt(BundleRevision.class);

		// Get the system bundle identity, not that the alias system.bundle
		// cannot
		// be used here. The real symbolic name of the system bundle revision
		// must be used
		Collection<BundleCapability> systemIdentities = findProviders(
				IdentityNamespace.IDENTITY_NAMESPACE,
				systemRevision.getSymbolicName());
		assertEquals("Wrong number of system identity capabilities.", 1,
				systemIdentities.size());
		BundleCapability systemIdentity = systemIdentities.iterator().next();
		assertEquals("Wrong provider.", systemRevision,
				systemIdentity.getRevision());

		// Use a BundleRequirement to look up capabilities.
		BundleRevision tb1Revision = tb1.adapt(BundleRevision.class);
		BundleRevision tb4Revision = tb4.adapt(BundleRevision.class);
		List<BundleRequirement> hostReqs = tb4Revision
				.getDeclaredRequirements(HostNamespace.HOST_NAMESPACE);
		assertEquals("Wrong number of host reqs.", 1, hostReqs.size());
		Collection<BundleCapability> hostCaps = frameworkWiring
				.findProviders(hostReqs.get(0));
		assertEquals("Wrong number of host capabilities.", 1, hostCaps.size());
		assertEquals("Wrong provider.", tb1Revision, hostCaps.iterator().next()
				.getRevision());

		// Make sure non effective capabilities are returned
		Collection<BundleCapability> nonEffective = findProviders(
				"test.effective", null);
		assertEquals("Wrong number of capabilities.", 1, nonEffective.size());
		assertEquals("Wrong provider.", tb1Revision, nonEffective.iterator()
				.next().getRevision());

		// Search for a package
		Collection<BundleCapability> packageCaps = findProviders(
				PackageNamespace.PACKAGE_NAMESPACE,
				"org.osgi.test.cases.framework.resolver.tb1");
		assertEquals("Wrong number of capabilities.", 1, packageCaps.size());
		assertEquals("Wrong provider.", tb1Revision, packageCaps.iterator()
				.next().getRevision());

		// Search for a capability from a fragment
		Collection<BundleCapability> fragmentCaps = findProviders(
				"test.fragment", null);
		assertEquals("Wrong number of capabilities.", 1, fragmentCaps.size());
		assertEquals("Wrong provider.", tb4Revision, fragmentCaps.iterator()
				.next().getRevision());

		// Search for capabilities that have multiple matches
		Collection<BundleCapability> multipleCaps = findProviders(
				"test.multiple", null);
		assertEquals("Wrong number of capabilities.", 2, multipleCaps.size());
		for (BundleCapability multipleCap : multipleCaps) {
			assertEquals("Wrong provider.", tb1Revision,
					multipleCap.getRevision());
		}

		// Use wild card matching to search for identity
		Collection<BundleCapability> multipleIdentities = findProviders(
				IdentityNamespace.IDENTITY_NAMESPACE,
				"org.osgi.test.cases.framework.resolver.*");
		assertEquals("Wrong number of capabilities.", 2,
				multipleIdentities.size());
		boolean foundTb1 = false;
		boolean foundTb4 = false;
		for (BundleCapability identity : multipleIdentities) {
			foundTb1 |= tb1Revision.equals(identity.getRevision());
			foundTb4 |= tb4Revision.equals(identity.getRevision());
		}
		assertTrue("Did not find tb1 identity.", foundTb1);
		assertTrue("Did not find tb4 identity.", foundTb4);
	}

	private Collection<BundleCapability> findProviders(String namespace,
			String namespaceValue) {
		String filter = namespaceValue == null ? null : "(" + namespace + "="
				+ namespaceValue + ")";
		return frameworkWiring.findProviders(new TestRequirement(namespace,
				filter));
	}
	static class TestRequirement implements Requirement {
		private final String namespace;
		private final Map<String, String> directives;
		private final Map<String,Object>	attributes	= Collections
				.emptyMap();

		public TestRequirement(String namespace, String filter) {
			this.namespace = namespace;
			if (filter != null) {
				this.directives = Collections.singletonMap(
						Namespace.REQUIREMENT_FILTER_DIRECTIVE, filter);
			} else {
				this.directives = Collections.emptyMap();
			}
		}

		public String getNamespace() {
			return this.namespace;
		}

		public Map<String, String> getDirectives() {
			return directives;
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

		public Resource getResource() {
			// returning null because this is a synthetic requirement
			return null;
		}

	}
}
