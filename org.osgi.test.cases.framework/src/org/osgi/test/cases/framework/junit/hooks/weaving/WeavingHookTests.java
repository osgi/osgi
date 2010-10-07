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

package org.osgi.test.cases.framework.junit.hooks.weaving;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.Capability;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCase;

public class WeavingHookTests extends OSGiTestCase {
	private final static String TEST_NAME = "weaving.name";
	private final static String TEST_DYNAMIC_NAME = "weaving.dynamic.name";
	private final static String TEST_RESULT = "weaving.result";
	private final static String TEST_PACKAGE_NAME = "org.osgi.test.cases.framework.weaving.tbx.";
	private final static String TEST_WEAVING_SOURCE = "org.osgi.test.cases.framework.weaving.jar";
	private final List bundles = new ArrayList();
	private final List registrations = new ArrayList();
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
		if (bundle == null)
			fail("Failed to install bundle: " + bundle);
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}

	protected void setUp() throws Exception {
		registrations.clear();
		bundles.clear();
		frameworkWiring = (FrameworkWiring) getContext().getBundle(0).adapt(FrameworkWiring.class);
	}

	protected void tearDown() throws Exception {
		for (Iterator iRegistrations = registrations.iterator(); iRegistrations.hasNext();)
			try {
				((ServiceRegistration) iRegistrations.next()).unregister();
			} catch (IllegalStateException e) {
				// probably unregistered during test
			}
		registrations.clear();
		for (Iterator iBundles = bundles.iterator(); iBundles.hasNext();) {
			Bundle bundle = (Bundle) iBundles.next();
			try {
				if (!(bundle.getState() == Bundle.UNINSTALLED))
					bundle.uninstall();
			} catch (BundleException e) {
				// nothing
			}
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

	private ServiceRegistration registerHook(WeavingHook hook, int ranking) {
		Dictionary props = new Hashtable();
		props.put(Constants.SERVICE_RANKING, new Integer(ranking));
		ServiceRegistration reg = getContext().registerService(WeavingHook.class, hook, props);
		registrations.add(reg);
		return reg;
	}

	public void testWeavingErrors() {
		final Bundle tb1 = install("weaving.tb1.jar");
		final AssertionFailedError[] error = new AssertionFailedError[1];
		final int[] called = new int[] {0, 0, 0};
		final WovenClass[] wovenClass = new WovenClass[1];
		registerHook(new WeavingHook() {
			public void weave(BundleWiring wiring, WovenClass classData) {
				if (wiring.getBundle() != tb1)
					return;
				called[0]++;
				wovenClass[0] = classData;
				try {
					try {
						classData.setBytes(null);
						fail("Expected null pointer exception on setBytes");
					} catch (NullPointerException npe) {
						// expected
					}
					assertNull("Class definition is not null.", classData.getDefinedClass());
					assertEquals("Wrong class name", "org.osgi.test.cases.framework.weaving.tbx.Activator", classData.getClassName());
					assertFalse("Weaving is complete", classData.isWeavingComplete());
					Collection dynamicImports = classData.getDynamicImports();
					assertNotNull("dynamic imports is null.", dynamicImports);
					assertTrue("Failed to add dynamic import", dynamicImports.add("test.addition"));
					assertTrue("Failed to remove dynamic import", dynamicImports.remove("test.addition"));
					assertTrue("dynamic imports is not empty", dynamicImports.isEmpty());
				} catch (AssertionFailedError failed) {
					error[0] = failed;
				} catch (Throwable t) {
					error[0] = new AssertionFailedError("Unexpected error.");
					error[0].initCause(t);
				}
			}
		}, 2);
		// should always be called
		WeavingHook badHook = new WeavingHook() {
			public void weave(BundleWiring wiring, WovenClass classData) {
				if (wiring.getBundle() != tb1)
					return;
				called[1]++;
				throw new RuntimeException("Test exception");
			}
		};
		ServiceRegistration badReg = registerHook(badHook, 1);
		// should never be called, chain is abandoned because of runtime exception above
		registerHook(new WeavingHook() {
			public void weave(BundleWiring wiring, WovenClass classData)
					throws ClassFormatError {
				if (wiring.getBundle() != tb1)
					return;
				// should not get called
				called[2]++;
			}
		}, 0);
		System.setProperty(TEST_RESULT, "NO TEST");

		startBundle(tb1, error, true);

		assertEquals("Wrong number of weaving calls", 1, called[0]);
		assertEquals("Wrong number of weaving calls", 1, called[1]);
		assertEquals("Wrong number of weaving calls", 0, called[2]);

		assertTrue("Weaving is not complete", wovenClass[0].isWeavingComplete());
		assertNull("Class definition is not null", wovenClass[0].getDefinedClass());
		try {
			wovenClass[0].setBytes(new byte[] {1});
			fail("Expected a exception after weaving is complete.");
		} catch (IllegalStateException e) {
			// expected;
		}
		Collection dynamicImports = wovenClass[0].getDynamicImports();
		assertNotNull("dynamic imports is null", dynamicImports);
		try {
			dynamicImports.add("test.addition");
			fail("Expected failure to add to dynamic imports");
		} catch (UnsupportedOperationException e) {
			// expected;
		}

		// start again, the bad hook should be black listed
		startBundle(tb1, error, false);

		assertEquals("Wrong number of weaving calls", 2, called[0]);
		assertEquals("Wrong number of weaving calls", 1, called[1]);
		assertEquals("Wrong number of weaving calls", 1, called[2]);

		assertTrue("Weaving is not complete", wovenClass[0].isWeavingComplete());
		Class clazz = wovenClass[0].getDefinedClass();
		assertNotNull("Defined class is null.", clazz);

		try {
			tb1.stop();
		} catch (BundleException e) {
			fail("Failed to stop test bundle.", e);
		}
		// register the hook to get off the black list
		badReg.unregister();
		registerHook(badHook, 1);
		refreshBundles(Arrays.asList(new Bundle[] {tb1}));

		// start again, the bad hook should be off black list
		startBundle(tb1, error, true);

		assertEquals("Wrong number of weaving calls", 3, called[0]);
		assertEquals("Wrong number of weaving calls", 2, called[1]);
		assertEquals("Wrong number of weaving calls", 1, called[2]);
		assertTrue("Weaving is not complete", wovenClass[0].isWeavingComplete());
		assertNull("Class definition is not null", wovenClass[0].getDefinedClass());
	}

	public void testWeaving() {
		final Bundle tb1 = install("weaving.tb1.jar");
		final Bundle weaving = install(TEST_WEAVING_SOURCE);
		final String testName = TEST_PACKAGE_NAME + "TestWeaving";
		final AssertionFailedError[] error = new AssertionFailedError[1];
		final WovenClass[] wovenClass = new WovenClass[1];
		System.setProperty(TEST_NAME, testName);
		System.setProperty(TEST_RESULT, "WOVEN");
		registerHook(new WeavingHook() {
			public void weave(BundleWiring wiring, WovenClass classData)
					throws ClassFormatError {
				if (wiring.getBundle() != tb1)
					return;
				if (!classData.getClassName().equals(testName))
					return;
				wovenClass[0] = classData;
				try {
					classData.setBytes(getBytes(weaving, testName));
				} catch (AssertionFailedError e) {
					error[0] = e;
				}
			}
		}, 1);

		startBundle(tb1, error, false);
		Class clazz = wovenClass[0].getDefinedClass();
		assertNotNull("Defined class is null.", clazz);
		assertEquals("Wrong class", testName, clazz.getName());
	}

	public void testDynamicWeaving() {
		final Bundle tb1 = install("weaving.tb1.jar");
		install("weaving.pkg1.v100.jar");
		install("weaving.pkg1.v110.jar");
		final Bundle weaving = install(TEST_WEAVING_SOURCE);
		assertTrue("Could not resolve test bundles", frameworkWiring.resolveBundles(bundles));

		final String dynamicPackage = "org.osgi.test.cases.framework.weaving.pkg1";
		final String testName = TEST_PACKAGE_NAME + "TestDynamicImport";
		final AssertionFailedError[] error = new AssertionFailedError[1];
		final String[] dyanmicPackageVersion = new String[] {"; version=\"[1.0, 1.1)\""};

		BundleWiring tb1Wiring = (BundleWiring) tb1.adapt(BundleWiring.class);
		List imports = tb1Wiring.getRequiredCapabilities(Capability.PACKAGE_CAPABILITY);
		for (Iterator iImports = imports.iterator(); iImports.hasNext();) {
			Capability importPackage = (Capability) iImports.next();
			Object pkgName = (String) importPackage.getAttributes().get(Capability.PACKAGE_CAPABILITY);
			assertFalse("Bundle imports package: " + dynamicPackage, dynamicPackage.equals(pkgName));
		}

		System.setProperty(TEST_NAME, testName);
		System.setProperty(TEST_DYNAMIC_NAME, dynamicPackage + ".Test");
		System.setProperty(TEST_RESULT, "v100");
		registerHook(new WeavingHook() {
			public void weave(BundleWiring wiring, WovenClass classData)
					throws ClassFormatError {
				if (wiring.getBundle() != tb1)
					return;
				if (!classData.getClassName().equals(testName))
					return;
				try {
					classData.setBytes(getBytes(weaving, testName));
				} catch (AssertionFailedError e) {
					error[0] = e;
				}
				Collection dynamicImports = classData.getDynamicImports();
				dynamicImports.add(dynamicPackage + dyanmicPackageVersion[0]);
			}
		}, 1);

		startBundle(tb1, error, false);

		imports = tb1Wiring.getRequiredCapabilities(Capability.PACKAGE_CAPABILITY);
		boolean found = false;
		for (Iterator iImports = imports.iterator(); iImports.hasNext() && !found;) {
			Capability importPackage = (Capability) iImports.next();
			Object pkgName = (String) importPackage.getAttributes().get(Capability.PACKAGE_CAPABILITY);
			found = dynamicPackage.equals(pkgName);
		}
		assertTrue("Did not get wired to dynamic import: " + dynamicPackage, found);

		try {
			tb1.stop();
		} catch (BundleException e) {
			fail("Failed to stop bundle: " + tb1, e);
		}

		// force the dynamic wire to a different version
		dyanmicPackageVersion[0] = "; version=\"[1.1, 1.2)\"";
		System.setProperty(TEST_RESULT, "v110");
		refreshBundles(Arrays.asList(new Bundle[] {tb1}));
		assertTrue("Could not resolve test bundle", frameworkWiring.resolveBundles(Arrays.asList(new Bundle[] {tb1})));

		// get new wiring and make sure it is not wired to the test package
		tb1Wiring = (BundleWiring) tb1.adapt(BundleWiring.class);
		imports = tb1Wiring.getRequiredCapabilities(Capability.PACKAGE_CAPABILITY);
		for (Iterator iImports = imports.iterator(); iImports.hasNext();) {
			Capability importPackage = (Capability) iImports.next();
			Object pkgName = (String) importPackage.getAttributes().get(Capability.PACKAGE_CAPABILITY);
			assertFalse("Bundle imports package: " + dynamicPackage, dynamicPackage.equals(pkgName));
		}
		startBundle(tb1, error, false);

		imports = tb1Wiring.getRequiredCapabilities(Capability.PACKAGE_CAPABILITY);
		found = false;
		for (Iterator iImports = imports.iterator(); iImports.hasNext() && !found;) {
			Capability importPackage = (Capability) iImports.next();
			Object pkgName = (String) importPackage.getAttributes().get(Capability.PACKAGE_CAPABILITY);
			found = dynamicPackage.equals(pkgName);
		}
		assertTrue("Did not get wired to dynamic import: " + dynamicPackage, found);
	}

	private void startBundle(Bundle bundle, AssertionFailedError error[], boolean expectFailure) {
		try {
			bundle.start();
			if (expectFailure)
				fail("Expected failure to start bundle: " + bundle);
		} catch (BundleException e) {
			if (!expectFailure) {
				if (e.getCause() instanceof AssertionError)
					throw (AssertionError) e.getCause();
				fail("Failed to start test bundle", e);
			}
		} finally {
			if (error[0] != null)
				throw error[0];
		}
	}

	byte[] getBytes(Bundle source, String testName) {
		try {
			String testClassPath = testName.replace('.', '/') + ".class";
			URL url = source.getEntry(testClassPath);
			assertNotNull("Failed to find class resource: " + testClassPath, url);
			InputStream in = url.openStream();
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				bytes.write(buffer, 0, count);
			}
			in.close();
			return bytes.toByteArray();
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable t) {
			AssertionFailedError error = new AssertionFailedError("Unexpected error.");
			error.initCause(t);
			throw error;
		}
	}
}
