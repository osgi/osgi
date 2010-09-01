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

import junit.framework.AssertionFailedError;

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

	public void testFindEntries() {
		fail("Need to write a findEntries test.");
	}

	public void testListResources() {
		fail("Need to write a listResources test.");
	}
}
