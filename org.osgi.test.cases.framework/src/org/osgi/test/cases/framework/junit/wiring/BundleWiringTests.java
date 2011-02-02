/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCase;

public class BundleWiringTests extends OSGiTestCase {
	private final List<Bundle> bundles = new ArrayList<Bundle>();
	FrameworkWiring frameworkWiring;
	
	
	public Bundle install(String bundle) {
		Bundle result = null;
		try {
			result = super.install(bundle);
		} catch (BundleException e) {
			fail("failed to install bundle: " + bundle, e);
		} catch (IOException e) {
			fail("failed to install bundle: " + bundle, e);
		}
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}

	protected void setUp() throws Exception {
		bundles.clear();
		frameworkWiring = (FrameworkWiring) getContext().getBundle(0).adapt(FrameworkWiring.class);
	}

	protected void tearDown() throws Exception {
		for (Iterator<Bundle> iBundles = bundles.iterator(); iBundles.hasNext();)
			try {
				iBundles.next().uninstall();
			} catch (BundleException e) {
				// nothing
			} catch (IllegalStateException e) {
				// happens if the test uninstalls the bundle itself
			}
		refreshBundles(bundles);
		bundles.clear();
	}

	private void refreshBundles(List<Bundle> bundles) {
		final boolean[] done = new boolean[] {false};
		FrameworkListener listener = new FrameworkListener() {
			public void frameworkEvent(FrameworkEvent event) {
				synchronized (done) {
					if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
						done[0] = true;
						done.notify();
					}
				}
			}
		};
		frameworkWiring.refreshBundles(bundles, new FrameworkListener[] {listener});
		synchronized (done) {
			if (!done[0])
				try {
					done.wait(5000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					fail("Unexepected interruption.", e);
				}
			if (!done[0])
				fail("Timed out waiting for refresh bundles to finish.");
		}
	}

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
		BundleRevision tb4Revision = testRevisions.get(3);
		List<BundleCapability> tb1AllCapabilities = tb1Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb1BundleCapabilities = tb1Revision.getDeclaredCapabilities(BundleRevision.BUNDLE_NAMESPACE);
		List<BundleCapability> tb1HostCapabilities = tb1Revision.getDeclaredCapabilities(BundleRevision.HOST_NAMESPACE);
		List<BundleCapability> tb1PackageCapabilities = tb1Revision.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
		List<BundleCapability> tb1TestCapabilities = tb1Revision.getDeclaredCapabilities("test");
		List<BundleCapability> tb1TestMultipleCapabilities = tb1Revision.getDeclaredCapabilities("test.multiple");
		checkCapabilities(tb1BundleCapabilities, tb1AllCapabilities, BundleRevision.BUNDLE_NAMESPACE, 1, tb1Revision);
		checkCapabilities(tb1HostCapabilities, tb1AllCapabilities, BundleRevision.HOST_NAMESPACE, 1, tb1Revision);
		checkCapabilities(tb1PackageCapabilities, tb1AllCapabilities, BundleRevision.PACKAGE_NAMESPACE, 1, tb1Revision);
		checkCapabilities(tb1TestCapabilities, tb1AllCapabilities, "test", 1, tb1Revision);
		checkCapabilities(tb1TestMultipleCapabilities, tb1AllCapabilities, "test.multiple", 2, tb1Revision);

		List<BundleCapability> tb4AllCapabilities = tb4Revision.getDeclaredCapabilities(null);
		List<BundleCapability> tb4TestFragmentCapabilities = tb4Revision.getDeclaredCapabilities("test.fragment");
		checkCapabilities(tb4TestFragmentCapabilities, tb4AllCapabilities, "test.fragment", 1, tb4Revision);

		// test osgi.wiring.host for fragment-attachment:="never"
		BundleRevision tb2Revision = testRevisions.get(1);
		List<BundleCapability> tb2AllCapabilities = tb2Revision.getDeclaredCapabilities(null);
		assertEquals("Wrong number of capabilities", 1, tb2AllCapabilities.size());
		List<BundleCapability> tb2BundleCapabilities = tb2Revision.getDeclaredCapabilities(BundleRevision.BUNDLE_NAMESPACE);
		checkCapabilities(tb2BundleCapabilities, tb2AllCapabilities, BundleRevision.BUNDLE_NAMESPACE, 1, tb2Revision);
		List<BundleCapability> tb2HostCapabilities = tb2Revision.getDeclaredCapabilities(BundleRevision.HOST_NAMESPACE);
		assertEquals("Expected no osgi.wiring.host capability", 0,
				tb2HostCapabilities.size());
	}

	void checkCapabilities(List<BundleCapability> toCheck, List<BundleCapability> all, String namespace, int expectedNum, BundleRevision provider) {
		assertEquals("Wrong number of capabilities for " + namespace + " from " + provider, expectedNum, toCheck.size());
		for (BundleCapability capability : toCheck) {
			assertTrue("Capability is not in all capabilities for revision", all.contains(capability));
			assertEquals("Wrong namespace", namespace, capability.getNamespace());
			assertEquals("Wrong provider", provider, capability.getRevision());
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

			/* The BundleRevision.getHostWirings() method has been removed. */
//			Collection<BundleWiring> hostWirings = revision.getHostWirings();
//			assertNotNull("Host wirings must never be null.", hostWirings);
//			if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
//				// we assume the fragment resolved to one host
//				assertEquals("Wrong number of host wirings found", 1, hostWirings.size());
//			} else {
//				// regular bundles must have empty host wirings
//				assertEquals("Must have empty wirings for regular bundles", 0, hostWirings.size());
//			}
		}
		return result;
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
		checkBundleWiring(testBundles.toArray(new Bundle[6]), wirings);

		List<BundleCapability> allTb1Capabilities = tb1Wiring.getCapabilities(null);
		List<BundleCapability> osgiBundleTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.BUNDLE_NAMESPACE);
		List<BundleCapability> osgiHostTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.HOST_NAMESPACE);
		List<BundleCapability> osgiPackageTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
		List<BundleCapability> genTestTb1Capabilities = tb1Wiring.getCapabilities("test");
		List<BundleCapability> genTestMultipleTb1Capabilities = tb1Wiring.getCapabilities("test.multiple");
		List<BundleCapability> genTestNoAttrsTb1Capabilities = tb1Wiring.getCapabilities("test.no.attrs");
		List<BundleCapability> genTestFragmentTb1Capabilities = tb1Wiring.getCapabilities("test.fragment");

		// check for osgi.wiring.host capability from wiring with
		// fragment-attachment:="false"
		List<BundleCapability> osgiHostTb2Capabilities = tb2Wiring.getCapabilities(BundleRevision.HOST_NAMESPACE);
		assertEquals("Expecting no osgi.wiring.host capability", 0,
				osgiHostTb2Capabilities.size());

		BundleCapability[] capabilities = checkWiredCapabilities(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb14Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiHostTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities, genTestFragmentTb1Capabilities, genTestNoAttrsTb1Capabilities);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings, capabilities);

		tb1Wiring = tb1.adapt(BundleWiring.class);
		tb2Wiring = tb2.adapt(BundleWiring.class);
		tb3Wiring = tb3.adapt(BundleWiring.class);
		tb4Wiring = tb4.adapt(BundleWiring.class);
		tb5Wiring = tb5.adapt(BundleWiring.class);
		tb14Wiring = tb14.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring, tb14Wiring};

		checkBundleWiring(testBundles.toArray(new Bundle[6]), wirings);

		// get wired capabilities before update
		osgiBundleTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.BUNDLE_NAMESPACE);
		osgiHostTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.HOST_NAMESPACE);
		osgiPackageTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
		genTestTb1Capabilities = tb1Wiring.getCapabilities("test");
		genTestMultipleTb1Capabilities = tb1Wiring.getCapabilities("test.multiple");
		genTestNoAttrsTb1Capabilities = tb1Wiring.getCapabilities("test.no.attrs");
		genTestFragmentTb1Capabilities = tb1Wiring.getCapabilities("test.fragment");

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
		BundleWiring updatedWiring = (BundleWiring) tb1.adapt(BundleWiring.class);
		checkBundleWiring(new Bundle[] {tb1}, new BundleWiring[] {updatedWiring});
		List<BundleCapability> updatedCapabilities = updatedWiring.getCapabilities(null);
		/* WiredCapability gone. getRequirerWirings() gone. */
//		for(Iterator<BundleCapability> iCapabilities = updatedCapabilities.iterator(); iCapabilities.hasNext();) {
//			BundleCapability capability = iCapabilities.next();
//			Collection<BundleWiring> requirers = capability.getRequirerWirings();
//			assertNotNull("Requirers is null", requirers);
//			assertEquals("Wrong number of requirers for : " + capability, 0, requirers.size());
//		}

		assertTrue("Wiring is not in use for: " + tb1, tb1Wiring.isInUse());
		assertFalse("Wiring is current for: " + tb1, tb1Wiring.isCurrent());

		// Check that old wiring for tb1 is still correct
		allTb1Capabilities = tb1Wiring.getCapabilities(null);
		capabilities = checkWiredCapabilities(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb14Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiHostTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities, genTestFragmentTb1Capabilities, genTestNoAttrsTb1Capabilities);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings, capabilities);
		checkNotInUseWirings(new BundleWiring[] {updatedWiring}, new BundleCapability[0]);

		tb1Wiring = tb1.adapt(BundleWiring.class);
		tb2Wiring = tb2.adapt(BundleWiring.class);
		tb3Wiring = tb3.adapt(BundleWiring.class);
		tb4Wiring = tb4.adapt(BundleWiring.class);
		tb5Wiring = tb5.adapt(BundleWiring.class);
		tb14Wiring = tb14.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring, tb14Wiring};

		checkBundleWiring(testBundles.toArray(new Bundle[6]), wirings);

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

		allTb1Capabilities = tb1Wiring.getCapabilities(null);
		osgiBundleTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.BUNDLE_NAMESPACE);
		osgiHostTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.HOST_NAMESPACE);
		osgiPackageTb1Capabilities = tb1Wiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
		genTestTb1Capabilities = tb1Wiring.getCapabilities("test");
		genTestMultipleTb1Capabilities = tb1Wiring.getCapabilities("test.multiple");
		genTestNoAttrsTb1Capabilities = tb1Wiring.getCapabilities("test.no.attrs");
		genTestFragmentTb1Capabilities = tb1Wiring.getCapabilities("test.fragment");
		
		capabilities = checkWiredCapabilities(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb14Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiHostTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities, genTestFragmentTb1Capabilities, genTestNoAttrsTb1Capabilities);

		// test the refresh case
		refreshBundles(null);
		assertFalse(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings, capabilities);

		// Wirings must be null now since the bundles are not resolved
		assertNull("Bundle wiring is not null for bundle: " + tb1, tb1.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb2, tb2.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb3, tb3.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb4, tb4.adapt(BundleWiring.class));
		assertNull("Bundle wiring is not null for bundle: " + tb5, tb5.adapt(BundleWiring.class));
	}

	private void checkBundleWiring(Bundle[] bundles, BundleWiring[] wirings) {
		assertEquals("Lists are not the same size", bundles.length, wirings.length);
		for (int i = 0; i < wirings.length; i++) {
			BundleWiring wiring = wirings[i];
			Bundle bundle = bundles[i];
			BundleRevision revision = bundle.adapt(BundleRevision.class);
			assertNotNull("BundleRevision is null for: " + bundle, revision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), revision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), revision.getVersion());

			if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
				assertNull("BundleWiring is non-null for fragment: " + bundle, wiring);
				continue;
			}

			assertNotNull("BundleWiring is null for bundle: " + bundle, wiring);
			assertEquals("Wrong bundle for wiring: " + bundle, bundle, wiring.getBundle());

			BundleRevision wiringRevision = wiring.getRevision();
			assertNotNull("Wiring revision is null for bundle: " + bundle, wiringRevision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), wiringRevision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), wiringRevision.getVersion());

			assertTrue("Wiring is not current for: " + bundle, wiring.isCurrent());
			assertTrue("Wiring is not in use for: " + bundle, wiring.isInUse());
		}
	}

	private void checkNotInUseWirings(BundleWiring[] wirings, BundleCapability[] capabilities) {
		for (int i = 0; i < wirings.length; i++) {
			BundleWiring wiring = wirings[i];
			if (wiring == null)
				continue; // fragment case
			assertFalse("Wiring is current for: " + wiring.getBundle(), wiring.isCurrent());
			assertFalse("Wiring is in use for: " + wiring.getBundle(), wiring.isInUse());
//			assertNull("Wiring fragments must be null: " + wiring.getBundle(), wiring.getFragmentRevisions());
			assertNull("Wiring capabilities must be null: " + wiring.getBundle(), wiring.getCapabilities(null));
			assertNull("Wiring required must be null: " + wiring.getBundle(), wiring.getRequirements(null));
			assertNull("Wiring class loader must be null: " + wiring.getBundle(), wiring.getClassLoader());
			assertNull("Wiring findEntries must be null: " + wiring.getBundle(), wiring.findEntries("/", "*", 0));
			assertNull("Wiring listResources must be null: " + wiring.getBundle(), wiring.listResources("/", "*", 0));
			assertNull("Wiring providedWires must be null: " + wiring.getBundle(), wiring.getProvidedWires(null));
			assertNull("Wiring requiredWires must be null: " + wiring.getBundle(), wiring.getRequiredWires(null));
		}
//		for (int i = 0; i < capabilities.length; i++) {
//			BundleCapability capability = capabilities[i];
//			assertNull("Provider is not null: " + capability, capability.getProviderWiring());
//			assertNull("Requirers is not null: " + capability, capability.getRequirerWirings());
//		}
	}

	private BundleCapability[] checkWiredCapabilities(
			BundleWiring tb1Wiring, BundleWiring tb2Wiring, BundleWiring tb3Wiring, BundleWiring tb5Wiring, BundleWiring tb14Wiring, 
			Bundle tb4, 
			List<BundleCapability> allTb1Capabilities, List<BundleCapability> osgiBundleTb1Capabilities, List<BundleCapability> osgiHostTb1Capabilities, List<BundleCapability> osgiPackageTb1Capabilities, List<BundleCapability> genTestTb1Capabilities, List<BundleCapability> genTestMultipleTb1Capabilities, List<BundleCapability> genTestFragmentTb1Capabilities, List<BundleCapability> genTestNoAttrsTb1Capabilities) {
		assertEquals("Wrong number of capabilities", 8, allTb1Capabilities.size());
		assertEquals("Wrong number of osgi.wiring.bundle capabilities", 1,
				osgiBundleTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + osgiBundleTb1Capabilities, allTb1Capabilities.containsAll(osgiBundleTb1Capabilities));
		assertEquals("Wrong number of osgi.wiring.host capabilities", 1,
				osgiHostTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + osgiHostTb1Capabilities, allTb1Capabilities.containsAll(osgiHostTb1Capabilities));
		assertEquals("Wrong number of osgi.wiring.package capabilities", 1,
				osgiPackageTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + osgiPackageTb1Capabilities, allTb1Capabilities.containsAll(osgiPackageTb1Capabilities));
		assertEquals("Wrong number of generic test capabilities", 1, genTestTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + genTestTb1Capabilities, allTb1Capabilities.containsAll(genTestTb1Capabilities));
		assertEquals("Wrong number of generic test.multiple capabilities", 2, genTestMultipleTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + genTestMultipleTb1Capabilities, allTb1Capabilities.containsAll(genTestMultipleTb1Capabilities));
		assertEquals("Wrong number of generic test.fragment capabilities", 1, genTestTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + genTestFragmentTb1Capabilities, allTb1Capabilities.containsAll(genTestFragmentTb1Capabilities));
		assertEquals("Wrong number of generic test.no.attrs capabilities", 1, genTestNoAttrsTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + genTestNoAttrsTb1Capabilities, allTb1Capabilities.containsAll(genTestNoAttrsTb1Capabilities));

		BundleCapability osgiBundleTb1 = osgiBundleTb1Capabilities.get(0);
		BundleCapability osgiHostTb1 = osgiHostTb1Capabilities.get(0);
		BundleCapability osgiPackageTb1 = osgiPackageTb1Capabilities.get(0);
		BundleCapability genTestTb1 = genTestTb1Capabilities.get(0);
		BundleCapability genTestFragmentTb1 = genTestFragmentTb1Capabilities.get(0);
		BundleCapability genTestNoAttrsTb1 = genTestNoAttrsTb1Capabilities.get(0);
		BundleCapability[] capabilities = new BundleCapability[] {osgiBundleTb1, osgiHostTb1, osgiPackageTb1, genTestTb1, genTestFragmentTb1, genTestNoAttrsTb1};

		checkWiredCapability(osgiPackageTb1, tb1Wiring, tb2Wiring);
		checkWiredCapability(osgiHostTb1, tb1Wiring, tb1Wiring);
		checkWiredCapability(osgiBundleTb1, tb1Wiring, tb3Wiring);
		checkWiredCapability(genTestTb1, tb1Wiring, tb5Wiring);
		checkWiredCapability(genTestNoAttrsTb1, tb1Wiring, tb5Wiring);
		checkWiredCapability(genTestFragmentTb1, tb1Wiring, tb14Wiring);

//		List<BundleRevision> fragments = tb1Wiring.getFragmentRevisions();
//		assertEquals("Wrong number of fragments", 1, fragments.size());
//		BundleRevision tb4Revision = fragments.get(0);
//		assertEquals("Wrong fragment", tb4, tb4Revision.getBundle());

		return capabilities;
	}

	private void checkWiredCapability(BundleCapability capability,
			BundleWiring provider, BundleWiring requirer) {
		assertEquals("Wrong provider", provider, capability.getRevision().getWiring());
//		Collection<BundleWiring> requirers = capability.getRequirerWirings();
//		assertTrue("Requirer not included", requirers.contains(requirer));
//		assertEquals("Wrong number of requirers", 1, requirers.size());

		List<BundleRequirement> required = requirer.getRequirements(capability.getNamespace());
		assertNotNull("Required capabilities is null", required);
		assertTrue("Expected capability is not is list of required: " + capability, required.contains(capability));
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

		checkWirings((Bundle[]) testBundles.toArray(new Bundle[testBundles.size()]), revisions, 1, true);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		// do not reget the BundleRevisions must survive refresh operations
		checkWirings((Bundle[]) testBundles.toArray(new Bundle[testBundles.size()]), revisions, 1, true);

		// test the update case
		Bundle tb8 = install("resolver.tb8.jar");
		BundleRevision tb1Revision1 = (BundleRevision) tb1.adapt(BundleRevision.class);
		URL content = getContext().getBundle().getEntry("resolver.tb1.v120.jar");
		assertNotNull("Cannot find content for update", content);
		try {
			tb1.update(content.openStream());
		} catch (BundleException e) {
			fail("Unexpected update failure",e);
		} catch (IOException e) {
			fail("Unexpected update failure",e);
		}
		BundleRevision tb1Revision2 = (BundleRevision) tb1.adapt(BundleRevision.class);
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
		BundleRevision tb1Revision3 = (BundleRevision) tb1.adapt(BundleRevision.class);
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
		checkWirings((Bundle[]) testBundles.toArray(new Bundle[testBundles.size()]), revisions, 1, true);

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
			BundleRevision revision = (BundleRevision) bundle.adapt(BundleRevision.class);
			assertNotNull("BundleRevision is null for: " + bundle, revision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), revision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), revision.getVersion());

			BundleRevisions bundleRevisions = (BundleRevisions) bundlesRevisions[i];
			assertNotNull("BundleRevisions is null for bundle: " + bundle,
					bundleRevisions);
			assertEquals("Wrong bundle for revisions", bundle,
					bundleRevisions.getBundle());
			List<BundleRevision> revisions = bundleRevisions.getRevisions();
			assertEquals("Wrong revision for bundle", revision,
					revisions.get(0));
			assertEquals("Wrong number of in use revisions",
					expectedNumRevisions, revisions.size());

			int index = 0;
			for (Iterator<BundleRevision> iter = revisions.iterator(); iter.hasNext(); index++) {
				revision = (BundleRevision) iter.next();
				BundleWiring wiring = revision.getWiring();
//				Collection<BundleWiring> hostWirings = revision.getHostWirings();

				if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
//					assertNotNull("Hosts wirings is null", hostWirings);
					assertNull("bundle wiring is not null", wiring);
//					assertEquals("Wrong number of host wirings", 1,
//							hostWirings.size());
//					BundleWiring hostWiring = (BundleWiring) hostWirings
//							.iterator().next();
//					List<BundleRevision> fragments = hostWiring.getFragmentRevisions();
//					assertNotNull("Fragments is null", fragments);
//					assertTrue("Fragment is not found: " + bundle,
//							fragments.contains(revision));
					continue;
				}

//				assertNotNull("Hosts wirings is not null", hostWirings);
//				assertEquals("Must not have host wirings if not a fragment", 0, hostWirings.size());
				assertNotNull("bundle wiring is null", wiring);
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
		BundleWiring tb10v100Wiring = (BundleWiring) tb10v100.adapt(BundleWiring.class);
		assertNotNull("Wiring is null for: " + tb10v100, tb10v100Wiring);
		List v100RequiredEEs = tb10v100Wiring.getRequirements("osgi.ee");
		assertEquals("Wrong number of required osgi.ees", 7, v100RequiredEEs.size());
		Bundle systemBundle = getContext().getBundle(0);
		assertNotNull("SystemBundle is null", systemBundle);
		for (Iterator ees = v100RequiredEEs.iterator(); ees.hasNext();) {
			assertEquals("Wrong provider for osgi.ee", systemBundle, ((BundleCapability) ees.next()).getRevision().getBundle());
		}

		BundleWiring tb10v120Wiring = (BundleWiring) tb10v120.adapt(BundleWiring.class);
		assertNotNull("Wiring is null for: " + tb10v120, tb10v120Wiring);
		List v120RequiredEEs = tb10v120Wiring.getRequirements("osgi.ee");
		assertEquals("Wrong number of required osgi.ees", 1, v120RequiredEEs.size());
		assertNotNull("SystemBundle is null", systemBundle);
		for (Iterator ees = v120RequiredEEs.iterator(); ees.hasNext();) {
			assertEquals("Wrong provider for osgi.ee", systemBundle, ((BundleCapability) ees.next()).getRevision().getBundle());
		}
	}

	public void testOptionalRequireCapability() {
		Bundle tb11 = install("resolver.tb11.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb11})));

		BundleWiring tb11Wiring = (BundleWiring) tb11.adapt(BundleWiring.class);
		assertNotNull("Wiring is null", tb11Wiring);
		List capabilities = tb11Wiring.getRequirements(null);
		assertNotNull("Capabilities is null", capabilities);
		assertEquals("Wrong number of capabilities", 0, capabilities.size());

		Bundle tb12 = install("resolver.tb12.jar");
		refreshBundles(Arrays.asList(new Bundle[] {tb11}));

		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb11, tb12})));
		tb11Wiring = (BundleWiring) tb11.adapt(BundleWiring.class);
		assertNotNull("Wiring is null", tb11Wiring);
		capabilities = tb11Wiring.getRequirements(null);
		assertNotNull("Capabilities is null", capabilities);
		assertEquals("Wrong number of capabilities", 1, capabilities.size());
		BundleCapability capability = (BundleCapability) capabilities.get(0);
		assertEquals("Wrong provider", tb12, capability.getRevision().getBundle());
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

		BundleWiring exporterWiring = (BundleWiring) exporter.adapt(BundleWiring.class);
		BundleWiring importerWiring = (BundleWiring) importer.adapt(BundleWiring.class);
		BundleWiring requirerWiring = (BundleWiring) requirer.adapt(BundleWiring.class);

		// test that empty lists are returned when no resources are found
		Collection empty = exporterWiring.listResources("", "*.notfound",
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
		Collection rootResources = exporterWiring.listResources("/root",
				"*.txt", 0);
		assertEquals("Wrong number of resources", 1, rootResources.size());
		assertEquals("Wrong resource", "root/root.export.txt", rootResources
				.iterator().next());
		checkResources(exporterWiring.getClassLoader(), rootResources);

		// note that root.B package has been substituted
		List expected = Arrays.asList(new String[] {
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
		exporterWiring = (BundleWiring) exporter.adapt(BundleWiring.class);
		importerWiring = (BundleWiring) importer.adapt(BundleWiring.class);
		requirerWiring = (BundleWiring) requirer.adapt(BundleWiring.class);

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
		BundleWiring newExporterWiring = (BundleWiring) exporter.adapt(BundleWiring.class);

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

		BundleWiring tb13Client1Wiring = (BundleWiring) tb13Client1.adapt(BundleWiring.class);
		assertNull("Expected null Wiring: " + tb13Client1.getSymbolicName(), tb13Client1Wiring);
		BundleWiring tb13Client3Wiring = (BundleWiring) tb13Client3.adapt(BundleWiring.class);
		assertNull("Expected null Wiring: " + tb13Client3.getSymbolicName(), tb13Client3Wiring);

		BundleWiring tb13aWiring = (BundleWiring) tb13a.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13a.getSymbolicName(), tb13aWiring);
		BundleWiring tb13bWiring = (BundleWiring) tb13b.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13b.getSymbolicName(), tb13bWiring);
		BundleWiring tb13Client2Wiring = (BundleWiring) tb13Client2.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13Client2.getSymbolicName(), tb13Client2Wiring);
		BundleWiring tb13Client4Wiring = (BundleWiring) tb13Client4.adapt(BundleWiring.class);
		assertNotNull("Expected non-null wiring: " + tb13Client4.getSymbolicName(), tb13Client4Wiring);

		List client2Required = tb13Client2Wiring.getRequirements(BundleRevision.BUNDLE_NAMESPACE);
		assertEquals("Unexpected number of capabilities", 1, client2Required.size());
		assertEquals("Wrong provider", tb13a, ((BundleCapability) client2Required.get(0)).getRevision().getBundle());

		List client4Required = tb13Client4Wiring.getRequirements(BundleRevision.BUNDLE_NAMESPACE);
		assertEquals("Unexpected number of capabilities", 1, client4Required.size());
		assertEquals("Wrong provider", tb13b, ((BundleCapability) client4Required.get(0)).getRevision().getBundle());

//		List aFragments = tb13aWiring.getFragmentRevisions();
//		assertEquals("Wrong number of fragments", 1, aFragments.size());
//		assertEquals("Wrong fragment attached", tb13Frag2, ((BundleRevision) aFragments.get(0)).getBundle());

//		List bFragments = tb13bWiring.getFragmentRevisions();
//		assertEquals("Wrong number of fragments", 1, bFragments.size());
//		assertEquals("Wrong fragment attached", tb13Frag4, ((BundleRevision) bFragments.get(0)).getBundle());
	}

	private void assertResourcesEquals(String message, Collection expected, Collection actual) {
		if (expected.size() != actual.size())
			fail(message + ": Collections are not the same size: " + expected + ":  " + actual);
		assertTrue(message + ": Colections do not contain the same content: " + expected + ":  " + actual, actual.containsAll(expected));
	}

	private void checkResources(ClassLoader cl, Collection resources) {
		for(Iterator iResources = resources.iterator(); iResources.hasNext();) {
			String path = (String) iResources.next();
			URL resource = cl.getResource(path);
			assertNotNull("Could not find resource: " + path, resource);
		}
	}
}
