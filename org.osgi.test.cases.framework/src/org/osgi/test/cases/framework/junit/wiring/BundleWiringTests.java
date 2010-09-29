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
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.BundleWirings;
import org.osgi.framework.wiring.Capability;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.framework.wiring.WiredCapability;
import org.osgi.test.support.OSGiTestCase;

public class BundleWiringTests extends OSGiTestCase {
	private final List bundles = new ArrayList();
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
		for (Iterator iBundles = bundles.iterator(); iBundles.hasNext();)
			try {
				((Bundle) iBundles.next()).uninstall();
			} catch (BundleException e) {
				// nothing
			} catch (IllegalStateException e) {
				// happens if the test uninstalls the bundle itself
			}
		refreshBundles(bundles);
		bundles.clear();
	}

	private void refreshBundles(List bundles) {
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

	public void testGetWiring() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");
		List testBundles = Arrays.asList(new Bundle[]{tb1, tb2, tb3, tb4, tb5});

		assertTrue(frameworkWiring.resolveBundles(testBundles));

		BundleWiring tb1Wiring = (BundleWiring) tb1.adapt(BundleWiring.class);
		BundleWiring tb2Wiring = (BundleWiring) tb2.adapt(BundleWiring.class);
		BundleWiring tb3Wiring = (BundleWiring) tb3.adapt(BundleWiring.class);
		BundleWiring tb4Wiring = (BundleWiring) tb4.adapt(BundleWiring.class);
		BundleWiring tb5Wiring = (BundleWiring) tb5.adapt(BundleWiring.class);
		BundleWiring[] wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring};
		checkBundleWiring((Bundle[]) testBundles.toArray(new Bundle[5]), wirings);

		List allTb1Capabilities = tb1Wiring.getProvidedCapabilities(null);
		List osgiBundleTb1Capabilities = tb1Wiring.getProvidedCapabilities(Capability.BUNDLE_CAPABILITY);
		List osgiPackageTb1Capabilities = tb1Wiring.getProvidedCapabilities(Capability.PACKAGE_CAPABILITY);
		List genTestTb1Capabilities = tb1Wiring.getProvidedCapabilities("test");
		List genTestMultipleTb1Capabilities = tb1Wiring.getProvidedCapabilities("test.multiple");

		WiredCapability[] capabilities = checkWiredCapabilities(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings, capabilities);

		tb1Wiring = (BundleWiring) tb1.adapt(BundleWiring.class);
		tb2Wiring = (BundleWiring) tb2.adapt(BundleWiring.class);
		tb3Wiring = (BundleWiring) tb3.adapt(BundleWiring.class);
		tb4Wiring = (BundleWiring) tb4.adapt(BundleWiring.class);
		tb5Wiring = (BundleWiring) tb5.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring};

		checkBundleWiring((Bundle[]) testBundles.toArray(new Bundle[5]), wirings);

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
		List updatedCapabilities = updatedWiring.getProvidedCapabilities(null);
		assertEquals("Wrong number of capabilities", 5, updatedCapabilities.size());
		for(Iterator iCapabilities = updatedCapabilities.iterator(); iCapabilities.hasNext();) {
			WiredCapability capability = (WiredCapability) iCapabilities.next();
			Collection requirers = capability.getRequirerWirings();
			assertNotNull("Requirers is null", requirers);
			assertEquals("Wrong number of requirers for : " + capability, 0, requirers.size());
		}

		assertTrue("Wiring is not in use for: " + tb1, tb1Wiring.isInUse());
		assertFalse("Wiring is current for: " + tb1, tb1Wiring.isCurrent());

		allTb1Capabilities = tb1Wiring.getProvidedCapabilities(null);
		osgiBundleTb1Capabilities = tb1Wiring.getProvidedCapabilities(Capability.BUNDLE_CAPABILITY);
		osgiPackageTb1Capabilities = tb1Wiring.getProvidedCapabilities(Capability.PACKAGE_CAPABILITY);
		genTestTb1Capabilities = tb1Wiring.getProvidedCapabilities("test");
		genTestMultipleTb1Capabilities = tb1Wiring.getProvidedCapabilities("test.multiple");

		capabilities = checkWiredCapabilities(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		checkNotInUseWirings(wirings, capabilities);
		checkNotInUseWirings(new BundleWiring[] {updatedWiring}, new WiredCapability[0]);

		tb1Wiring = (BundleWiring) tb1.adapt(BundleWiring.class);
		tb2Wiring = (BundleWiring) tb2.adapt(BundleWiring.class);
		tb3Wiring = (BundleWiring) tb3.adapt(BundleWiring.class);
		tb4Wiring = (BundleWiring) tb4.adapt(BundleWiring.class);
		tb5Wiring = (BundleWiring) tb5.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring};

		checkBundleWiring((Bundle[]) testBundles.toArray(new Bundle[5]), wirings);

		// test uninstall case
		try {
			tb1.uninstall();
		} catch (BundleException e) {
			fail("Unexpecte error on uninstall", e);
		}

		assertNull("Bundle wiring is not null for bundle: " + tb1, tb1.adapt(BundleWiring.class));
		// note that we do not reget tb1Wiring because it must be null on uninstall from the check above
		tb2Wiring = (BundleWiring) tb2.adapt(BundleWiring.class);
		tb3Wiring = (BundleWiring) tb3.adapt(BundleWiring.class);
		tb4Wiring = (BundleWiring) tb4.adapt(BundleWiring.class);
		tb5Wiring = (BundleWiring) tb5.adapt(BundleWiring.class);
		wirings = new BundleWiring[] {tb1Wiring, tb2Wiring, tb3Wiring, tb4Wiring, tb5Wiring};

		assertTrue("Wiring is not in use for: " + tb1, tb1Wiring.isInUse());
		assertFalse("Wring is current for: " + tb1, tb1Wiring.isCurrent());

		allTb1Capabilities = tb1Wiring.getProvidedCapabilities(null);
		osgiBundleTb1Capabilities = tb1Wiring.getProvidedCapabilities(Capability.BUNDLE_CAPABILITY);
		osgiPackageTb1Capabilities = tb1Wiring.getProvidedCapabilities(Capability.PACKAGE_CAPABILITY);
		genTestTb1Capabilities = tb1Wiring.getProvidedCapabilities("test");
		genTestMultipleTb1Capabilities = tb1Wiring.getProvidedCapabilities("test.multiple");

		capabilities = checkWiredCapabilities(tb1Wiring, tb2Wiring, tb3Wiring, tb5Wiring, tb4, allTb1Capabilities, osgiBundleTb1Capabilities, osgiPackageTb1Capabilities, genTestTb1Capabilities, genTestMultipleTb1Capabilities);

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
			BundleRevision revision = (BundleRevision) bundle.adapt(BundleRevision.class);
			assertNotNull("BundleRevision is null for: " + bundle, revision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), revision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), revision.getVersion());

			if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
				assertNull("BundleWiring is non-null for fragment: " + bundle, wiring);
				continue;
			}

			assertNotNull("BundleWiring is null for bundle: " + bundle, wiring);
			assertEquals("Wrong bundle for wiring: " + bundle, bundle, wiring.getBundle());

			BundleRevision wiringRevision = wiring.getBundleRevision();
			assertNotNull("Wiring revision is null for bundle: " + bundle, wiringRevision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), wiringRevision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), wiringRevision.getVersion());

			assertTrue("Wiring is not current for: " + bundle, wiring.isCurrent());
			assertTrue("Wiring is not in use for: " + bundle, wiring.isInUse());
		}
	}

	private void checkNotInUseWirings(BundleWiring[] wirings, WiredCapability[] capabilities) {
		for (int i = 0; i < wirings.length; i++) {
			BundleWiring wiring = wirings[i];
			if (wiring == null)
				continue; // fragment case
			assertFalse("Wiring is current for: " + wiring.getBundle(), wiring.isCurrent());
			assertFalse("Wiring is in use for: " + wiring.getBundle(), wiring.isInUse());
			assertNull("Wiring fragments must be null: " + wiring.getBundle(), wiring.getFragmentRevisions());
			assertNull("Wiring capabilities must be null: " + wiring.getBundle(), wiring.getProvidedCapabilities(null));
			assertNull("Wiring required must be null: " + wiring.getBundle(), wiring.getRequiredCapabilities(null));
			assertNull("Wiring class loader must be null: " + wiring.getBundle(), wiring.getClassLoader());
			assertNull("Wiring findEntries must be null: " + wiring.getBundle(), wiring.findEntries("/", "*", 0));
			assertNull("Wiring listResources must be null: " + wiring.getBundle(), wiring.listResources("/", "*", 0));
		}
		for (int i = 0; i < capabilities.length; i++) {
			WiredCapability capability = capabilities[i];
			assertNull("Provider is not null: " + capability, capability.getProviderWiring());
			assertNull("Requirers is not null: " + capability, capability.getRequirerWirings());
		}
	}

	private WiredCapability[] checkWiredCapabilities(BundleWiring tb1Wiring, BundleWiring tb2Wiring, BundleWiring tb3Wiring, BundleWiring tb5Wiring, Bundle tb4, List allTb1Capabilities, List osgiBundleTb1Capabilities, List osgiPackageTb1Capabilities, List genTestTb1Capabilities, List genTestMultipleTb1Capabilities) {
		assertEquals("Wrong number of capabilities", 5, allTb1Capabilities.size());
		assertEquals("Wrong number of osgi.bundle capabilities", 1, osgiBundleTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + osgiBundleTb1Capabilities, allTb1Capabilities.containsAll(osgiBundleTb1Capabilities));
		assertEquals("Wrong number of osgi.package capabilities", 1, osgiPackageTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + osgiPackageTb1Capabilities, allTb1Capabilities.containsAll(osgiPackageTb1Capabilities));
		assertEquals("Wrong number of generic test capabilities", 1, genTestTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + genTestTb1Capabilities, allTb1Capabilities.containsAll(genTestTb1Capabilities));
		assertEquals("Wrong number of generic test.multiple capabilities", 2, genTestMultipleTb1Capabilities.size());
		assertTrue("All capabilities is missing : " + genTestMultipleTb1Capabilities, allTb1Capabilities.containsAll(genTestMultipleTb1Capabilities));

		WiredCapability osgiBundleTb1 = (WiredCapability) osgiBundleTb1Capabilities.get(0);
		WiredCapability osgiPackageTb1 = (WiredCapability) osgiPackageTb1Capabilities.get(0);
		WiredCapability genTestTb1 = (WiredCapability) genTestTb1Capabilities.get(0);
		WiredCapability[] capabilities = new WiredCapability[] {osgiBundleTb1, osgiPackageTb1, genTestTb1};

		checkWiredCapability(osgiPackageTb1, tb1Wiring, tb2Wiring);
		checkWiredCapability(osgiBundleTb1, tb1Wiring, tb3Wiring);
		checkWiredCapability(genTestTb1, tb1Wiring, tb5Wiring);

		List fragments = tb1Wiring.getFragmentRevisions();
		assertEquals("Wrong number of fragments", 1, fragments.size());
		BundleRevision tb4Revision = (BundleRevision) fragments.get(0);
		assertEquals("Wrong fragment", tb4, tb4Revision.getBundle());

		return capabilities;
	}

	private void checkWiredCapability(WiredCapability capability,
			BundleWiring provider, BundleWiring requirer) {
		assertEquals("Wrong provider", provider, capability.getProviderWiring());
		Collection requirers = capability.getRequirerWirings();
		assertTrue("Requirer not included", requirers.contains(requirer));
		assertEquals("Wrong number of requirers", 1, requirers.size());

		List required = requirer.getRequiredCapabilities(capability.getNamespace());
		assertNotNull("Required capabilities is null", required);
		assertTrue("Expected capability is not is list of required: " + capability, required.contains(capability));
	}

	public void testGetWirings() {
		Bundle tb1 = install("resolver.tb1.v110.jar");
		Bundle tb2 = install("resolver.tb2.jar");
		Bundle tb3 = install("resolver.tb3.jar");
		Bundle tb4 = install("resolver.tb4.jar");
		Bundle tb5 = install("resolver.tb5.jar");
		List testBundles = Arrays.asList(new Bundle[]{tb1, tb2, tb3, tb4, tb5});

		assertTrue(frameworkWiring.resolveBundles(testBundles));

		BundleWirings tb1Wirings = (BundleWirings) tb1.adapt(BundleWirings.class);
		BundleWirings tb2Wirings = (BundleWirings) tb2.adapt(BundleWirings.class);
		BundleWirings tb3Wirings = (BundleWirings) tb3.adapt(BundleWirings.class);
		BundleWirings tb4Wirings = (BundleWirings) tb4.adapt(BundleWirings.class);
		BundleWirings tb5Wirings = (BundleWirings) tb5.adapt(BundleWirings.class);
		BundleWirings[] wirings = new BundleWirings[] {tb1Wirings, tb2Wirings, tb3Wirings, tb4Wirings, tb5Wirings};

		checkBundleWirings((Bundle[]) testBundles.toArray(new Bundle[testBundles.size()]), wirings, 1, true);

		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue(frameworkWiring.resolveBundles(testBundles));

		// do not reget the BundleWirings must survive refresh operations
		checkBundleWirings((Bundle[]) testBundles.toArray(new Bundle[testBundles.size()]), wirings, 1, true);

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
		checkBundleWirings(new Bundle[] {tb1}, new BundleWirings[] {tb1Wirings}, 2, true);
		checkRevisions(tb1Wirings, new BundleRevision[] {tb1Revision2, tb1Revision1});

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
		checkBundleWirings(new Bundle[] {tb1}, new BundleWirings[] {tb1Wirings}, 3, true);
		checkRevisions(tb1Wirings, new BundleRevision[] {tb1Revision3, tb1Revision2, tb1Revision1});
		
		// test the refresh case
		refreshBundles(Arrays.asList(new Bundle[]{tb1}));
		assertTrue("Could not resolve test bundles", frameworkWiring.resolveBundles(testBundles));

		// do not reget the BundleWirings must survive refresh operations
		checkBundleWirings((Bundle[]) testBundles.toArray(new Bundle[testBundles.size()]), wirings, 1, true);

		// test uninstall case
		try {
			tb1.uninstall();
		} catch (BundleException e) {
			fail("Unexpected error on uninstall", e);
		}

		// regetting tb1 wiring to test that we can still get it after uninstall
		// this wirings will only have 1 wiring and it is not current
		tb1Wirings = (BundleWirings) tb1.adapt(BundleWirings.class);
		checkBundleWirings(new Bundle[] {tb1}, new BundleWirings[] {tb1Wirings}, 1, false);
		// all other wirings are current and will only have one wiring each
		BundleWirings[] otherWirings = new BundleWirings[] {tb2Wirings, tb3Wirings, tb4Wirings, tb5Wirings};
		Bundle[] otherBundes = new Bundle[] {tb2, tb3, tb4, tb5};
		checkBundleWirings(otherBundes, otherWirings, 1, true);
	}

	private void checkRevisions(BundleWirings wirings,
			BundleRevision[] bundleRevisions) {
		List wiringList = wirings.getWirings();
		assertEquals("Wrong number of revisions", bundleRevisions.length, wiringList.size());
		int i = 0;
		for (Iterator iWirings = wiringList.iterator(); iWirings.hasNext(); i++)
			assertEquals("Wrong revision found", bundleRevisions[i], ((BundleWiring) iWirings.next()).getBundleRevision());
	}

	private void checkBundleWirings(Bundle[] bundles, BundleWirings[] wiringsList, int expectedNumWirings, boolean hasCurrent) {
		assertEquals("Lists are not the same size", bundles.length, wiringsList.length);
		for (int i = 0; i < wiringsList.length; i++) {
			BundleWirings wirings = wiringsList[i];
			Bundle bundle = bundles[i];
			assertNotNull("BundleWiring is null for bundle: " + bundle, wirings);
			assertEquals("Wrong bundle for wirings", bundle, wirings.getBundle());
			BundleRevision revision = (BundleRevision) bundle.adapt(BundleRevision.class);
			assertNotNull("BundleRevision is null for: " + bundle, revision);
			assertEquals("Wrong BSN", bundle.getSymbolicName(), revision.getSymbolicName());
			assertEquals("Wrong version", bundle.getVersion(), revision.getVersion());

			if ((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
				List hostWirings = wirings.getWirings();
				assertNotNull("Hosts wirings is null", hostWirings);
				assertEquals("Wrong number of host wirings", 1, hostWirings.size());
				BundleWiring hostWiring = (BundleWiring) hostWirings.get(0);
				List fragments = hostWiring.getFragmentRevisions();
				assertNotNull("Fragments is null", fragments);
				assertTrue("Fragment is not found: " + bundle, fragments.contains(revision));
				continue;
			}

			List inUseWirings = wirings.getWirings();
			assertNotNull("In use wirings is null", inUseWirings);
			assertEquals("Wrong number of in use wirings", expectedNumWirings, inUseWirings.size());
			int idxWirings = 0;
			for (Iterator iWirings = inUseWirings.iterator(); iWirings.hasNext(); idxWirings++) {
				BundleWiring wiring = (BundleWiring) iWirings.next();
				if (idxWirings == 0 && hasCurrent)
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
		List v100RequiredEEs = tb10v100Wiring.getRequiredCapabilities("osgi.ee");
		assertEquals("Wrong number of required osgi.ees", 7, v100RequiredEEs.size());
		Bundle systemBundle = getContext().getBundle(0);
		assertNotNull("SystemBundle is null", systemBundle);
		for (Iterator ees = v100RequiredEEs.iterator(); ees.hasNext();) {
			assertEquals("Wrong provider for osgi.ee", systemBundle, ((WiredCapability) ees.next()).getProviderRevision().getBundle());
		}

		BundleWiring tb10v120Wiring = (BundleWiring) tb10v120.adapt(BundleWiring.class);
		assertNotNull("Wiring is null for: " + tb10v120, tb10v120Wiring);
		List v120RequiredEEs = tb10v120Wiring.getRequiredCapabilities("osgi.ee");
		assertEquals("Wrong number of required osgi.ees", 1, v120RequiredEEs.size());
		assertNotNull("SystemBundle is null", systemBundle);
		for (Iterator ees = v120RequiredEEs.iterator(); ees.hasNext();) {
			assertEquals("Wrong provider for osgi.ee", systemBundle, ((WiredCapability) ees.next()).getProviderRevision().getBundle());
		}
	}

	public void testOptionalRequireCapability() {
		Bundle tb11 = install("resolver.tb11.jar");
		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb11})));

		BundleWiring tb11Wiring = (BundleWiring) tb11.adapt(BundleWiring.class);
		assertNotNull("Wiring is null", tb11Wiring);
		List capabilities = tb11Wiring.getRequiredCapabilities(null);
		assertNotNull("Capabilities is null", capabilities);
		assertEquals("Wrong number of capabilities", 0, capabilities.size());

		Bundle tb12 = install("resolver.tb12.jar");
		refreshBundles(Arrays.asList(new Bundle[] {tb11}));

		assertTrue(frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb11, tb12})));
		tb11Wiring = (BundleWiring) tb11.adapt(BundleWiring.class);
		assertNotNull("Wiring is null", tb11Wiring);
		capabilities = tb11Wiring.getRequiredCapabilities(null);
		assertNotNull("Capabilities is null", capabilities);
		assertEquals("Wrong number of capabilities", 1, capabilities.size());
		Capability capability = (Capability) capabilities.get(0);
		assertEquals("Wrong provider", tb12, capability.getProviderRevision().getBundle());
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
		List empty = exporterWiring.listResources("", "*.notfound", BundleWiring.LISTRESOURCES_RECURSE);
		assertNotNull("Should return empty list", empty);
		assertEquals("Should have 0 resources", 0, empty.size());
		empty = importerWiring.listResources("", "*.notfound", BundleWiring.LISTRESOURCES_RECURSE);
		assertNotNull("Should return empty list", empty);
		assertEquals("Should have 0 resources", 0, empty.size());
		empty = requirerWiring.listResources("", "*.notfound", BundleWiring.LISTRESOURCES_RECURSE);
		assertNotNull("Should return empty list", empty);
		assertEquals("Should have 0 resources", 0, empty.size());

		// test exporter resources
		List rootResources = exporterWiring.listResources("/root", "*.txt", 0);
		assertEquals("Wrong number of resources", 1, rootResources.size());
		assertEquals("Wrong resource", "root/root.export.txt", rootResources.get(0));
		checkResoruces(exporterWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(exporterWiring.getClassLoader(), rootResources);

		// test local resources of exporter; note that root.B resources are not available
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.export.txt", 
				   "root/A/b/b.export.txt", 
				  "root/A/A.export.txt", 
				   "root/B/a/a.export.txt",
				   "root/B/b/b.export.txt",
				"root/root.export.txt"});
		rootResources = exporterWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(exporterWiring.getClassLoader(), rootResources);

		// test importer resources
		rootResources = importerWiring.listResources("/root", "*.txt", 0);
		assertEquals("Wrong number of resources", 1, rootResources.size());
		assertEquals("Wrong resource", "root/root.local.txt", rootResources.get(0));
		checkResoruces(importerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(importerWiring.getClassLoader(), rootResources);

		// test local resources, anything shadowed by an import must not be included
		rootResources = importerWiring.listResources("/root", "*.txt", BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
		expected = Arrays.asList(new String[] {
				   "root/A/a/a.local.txt", 
				   "root/A/b/b.local.txt", 
				  "root/A/A.local.txt", 
				"root/root.local.txt"
		});
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(importerWiring.getClassLoader(), rootResources);

		// test the require bundle case
		rootResources = requirerWiring.listResources("/root", "*.txt", 0);
		expected = Arrays.asList(new String[] {
				"root/root.export.txt",
				"root/root.local.txt"
		});
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(requirerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(requirerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(requirerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(exporterWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(exporterWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(exporterWiring.getClassLoader(), rootResources);

		// test importer resources
		expected = Arrays.asList(new String[] {
				"root/root.local.txt",
				"root/root.frag.txt"});
		rootResources = importerWiring.listResources("/root", "*.txt", 0);
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(importerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(importerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(importerWiring.getClassLoader(), rootResources);

		// test the require bundle case
		rootResources = requirerWiring.listResources("/root", "*.txt", 0);
		expected = Arrays.asList(new String[] {
				"root/root.export.txt",
				"root/root.local.txt",
				"root/root.frag.txt"
		});
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(requirerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(requirerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(requirerWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(oldExporterWiring.getClassLoader(), rootResources);

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
		assertEquals("Wrong resources", expected, rootResources);
		checkResoruces(newExporterWiring.getClassLoader(), rootResources);
	}

	private void assertEquals(String message, List expected, List actual) {
		if (expected.size() != actual.size())
			fail(message + ": Lists are not the same size: " + expected + ":  " + actual);
		assertTrue(message + ": Lists do not contain the same content: " + expected + ":  " + actual, actual.containsAll(expected));
	}

	private void checkResoruces(ClassLoader cl, List resources) {
		for(Iterator iResources = resources.iterator(); iResources.hasNext();) {
			String path = (String) iResources.next();
			URL resource = cl.getResource(path);
			assertNotNull("Could not find resource: " + path, resource);
		}
	}
}
