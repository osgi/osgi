/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.framework.junit.fragments;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test cases for testing fragment bundles and extension bundles.
 * 
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl implements
		BundleListener, FrameworkListener {

	private Hashtable			events		= new Hashtable();
	private static final String	SEPARATOR	= "#";
	private ServiceRegistration	bundleReg;

	protected void setUp() throws Exception {
		super.setUp();
		Hashtable props = new Hashtable();
		props.put("bundle", "fragments.tests");
		bundleReg = getContext().registerService(Bundle.class.getName(),
				getContext().getBundle(), props);
	}

	protected void tearDown() throws Exception {
		bundleReg.unregister();
		super.tearDown();
	}

	/**
	 * Fragment bundles must not specify a Bundle Activator. If the Bundle.start
	 * method is called on a Bundle object for a fragment, then the framework
	 * must throw a BundleException indicating that a fragment bundle cannot be
	 * started. If the Bundle.stop method is called on a Bundle object for a
	 * fragment, then the framework must throw a BundleException indicating that
	 * a fragment bundle cannot be stopped.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.start()
	 */
	public void testFragmentLifecycle() throws Exception {
		// Install fragment bundle
		Bundle tb1b = getContext().installBundle(
				getWebServer() + "fragments.tb1b.jar");
		try {
			// Try starting fragment
			tb1b.start();
			fail("Framework should throw BundleException for start().");
		}
		catch (BundleException e) {

		}
		try {
			// Try stopping fragment
			tb1b.stop();
			fail("Framework should throw BundleException for stop().");
		}
		catch (BundleException e) {

		}
		tb1b.uninstall();
	}

	/**
	 * Tests that a bundle fragment supplies classpath entries to host.
	 * 
	 * Tests that resources and classes are loaded by their host bundle's class
	 * loader.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.loadClass(String)
	 */
	public void testAppendClasspath01() throws Exception {
		Class classObj1;
		Class classObj2;

		// Install fragment bundle
		Bundle tb1b = getContext().installBundle(
				getWebServer() + "fragments.tb1b.jar");
		// Install host
		Bundle tb1a = getContext().installBundle(
				getWebServer() + "fragments.tb1a.jar");
		tb1a.start();
		try {
			// Load a resource that exists in a jar in the classpath of the
			// fragment bundle
			classObj1 = null;
			try {
				classObj1 = tb1a
						.loadClass("org.osgi.test.cases.framework.fragments.tb1c.TestClass");
			}
			catch (ClassNotFoundException e) {
				fail("The class org.osgi.test.cases.framework.fragments.tb1c.TestClass should be appended to the classpath of host.");
			}

			// Load a class resource that exists in the host
			classObj2 = tb1a
					.loadClass("org.osgi.test.cases.framework.fragments.tb1a.TestClass");

			// Test that class loaders are the same
			assertEquals("Class loader must be the same.", classObj2
					.getClassLoader(), classObj1.getClassLoader());
		}
		finally {
			tb1a.stop();
			tb1a.uninstall();
			tb1b.uninstall();
		}
	}

	/**
	 * Tests that if a classpath entry cannot be located in the bundle, then the
	 * Framework attempts to locate the classpath entry in each attached
	 * fragment bundle. The attached fragment bundles are searched in ascending
	 * bundle id order.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.getResource(String)
	 */
	public void testAppendClasspath02() throws Exception {

		// Install fragment bundles
		Bundle tb1g = getContext().installBundle(
				getWebServer() + "fragments.tb1g.jar");
		Bundle tb1b = getContext().installBundle(
				getWebServer() + "fragments.tb1b.jar");
		// Install and start host bundle
		Bundle tb1a = getContext().installBundle(
				getWebServer() + "fragments.tb1a.jar");
		tb1a.start();

		try {
			// Check bundle id order is as expected
			assertTrue(
					"Expecting bundle id for fragment bundle installed first to be smaller.",
					tb1g.getBundleId() < tb1b.getBundleId());

			// Find resource
			URL url = tb1a.getResource("resources/notinhost.txt");
			assertNotNull(
					"Expecting a URL that is not null because it exists.", url);

			// Check content within resource
			InputStream ins = url.openStream();
			try {
				assertNotNull(
						"Expecting to be able to open stream to resource.", ins);

				BufferedReader bufr = new BufferedReader(new InputStreamReader(
						ins));
				String line = bufr.readLine();
				assertEquals(
						"Expecting framework to recover resources in ascending bundle id order.",
						"tb1g", line);
			}
			finally {
				ins.close();
			}
		}
		finally {
			// Stop and uninstall
			tb1a.stop();
			tb1a.uninstall();
			tb1b.uninstall();
			tb1g.uninstall();
		}
	}

	/**
	 * The Framework must not allow a fragment to replace any class or resource
	 * of a host bundle.
	 * 
	 * Tests that URL.getPath method for a URL that uses the bundle resource or
	 * bundle entry protocol returns an absolute path (a path that starts with
	 * '/'). For example, the URL returned from
	 * Bundle.getEntry("myimages/test.gif") must have a path of
	 * "/myimages/test.gif".
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.getResource(String)
	 */
	public void testExistingResourceNotReplaced() throws Exception {
		URL url;
		InputStream ins;
		Bundle tb1a;
		BufferedReader bufr;
		String line;

		// Install fragment bundle
		Bundle tb1b = getContext().installBundle(
				getWebServer() + "fragments.tb1b.jar");
		// Install and start host bundle
		tb1a = getContext()
				.installBundle(getWebServer() + "fragments.tb1a.jar");
		tb1a.start();

		try {
			// Find resource
			url = tb1a.getResource("resources/resource.txt");
			assertNotNull("Expecting a not null URL.", url);

			// Test that getPath returns absolute path
			assertEquals("Absolute path expected.", "/resources/resource.txt",
					url.getPath());

			// Check content within resource
			ins = url.openStream();
			try {
				assertNotNull("Expecting to open stream to resource", ins);

				bufr = new BufferedReader(new InputStreamReader(ins));
				line = bufr.readLine();
				assertEquals(
						"Framework must not allow a fragment to replace any class or resource of a host bundle",
						"tb1a", line);
			}
			finally {
				ins.close();
			}
		}
		finally {
			// Stop and uninstall
			tb1a.stop();
			tb1a.uninstall();
			tb1b.uninstall();
		}
	}

	/**
	 * Tests that a fragment cannot be a host to another fragment.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFragmentCannotBeHost() throws Exception {
		// Install fragment bundles
		Bundle tb1d = getContext().installBundle(
				getWebServer() + "fragments.tb1d.jar");
		Bundle tb1b = getContext().installBundle(
				getWebServer() + "fragments.tb1b.jar");
		// Install host
		Bundle tb1a = getContext().installBundle(
				getWebServer() + "fragments.tb1a.jar");
		tb1a.start();

		try {
			// Test that state of fragment tb1d is INSTALLED
			assertEquals(
					"Expecting the state of fragment bundle tb1d to be INSTALLED",
					Bundle.INSTALLED, tb1d.getState());
			// Test that state of fragment tb1b is RESOLVED
			assertEquals(
					"Expecting the state of fragment bundle tb1b to be RESOLVED",
					Bundle.RESOLVED, tb1b.getState());
		}
		finally {
			tb1a.stop();
			tb1a.uninstall();
			tb1b.uninstall();
			tb1d.uninstall();
		}
	}

	/**
	 * Tests that a fragment can attach to multiple hosts.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFragmentMultipleHosts() throws Exception {
		// Install and start host bundle version 1.0
		Bundle tb3a = getContext().installBundle(
				getWebServer() + "fragments.tb3a.jar");

		// Install and start host bundle version 2.0
		Bundle tb3c = getContext().installBundle(
				getWebServer() + "fragments.tb3c.jar");

		// Install fragment bundle
		Bundle tb3d = getContext().installBundle(
				getWebServer() + "fragments.tb3d.jar");
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);

		// Try recovering resource from host that is in classpath of fragment
		try {
			pa.resolveBundles(new Bundle[] {tb3d});
			tb3a.start();
			tb3c.start();
			Class a = tb3a
					.loadClass("org.osgi.test.cases.framework.fragments.tb3d.SomeClass");
			Class c = tb3c
					.loadClass("org.osgi.test.cases.framework.fragments.tb3d.SomeClass");
			assertEquals("class from fragment does not have the same name", a
					.getName(), c.getName());
			assertFalse(
					"class from fragment not loaded by different class loaders",
					a.equals(c));
		}
		finally {
			tb3c.stop();
			tb3c.uninstall();
			tb3a.stop();
			tb3a.uninstall();
			tb3d.uninstall();
			ungetService(pa);
		}
	}

	/**
	 * Tests the fragment-attachment directive recognized by the framework for
	 * Bundle-SymbolicName taking the value of "never" which indicates that no
	 * fragments are allowed to attach to the host bundle at any time.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFragmentAttachmentDirective01() throws Exception {
		// Install and start host bundle
		Bundle tb3e = getContext().installBundle(
				getWebServer() + "fragments.tb3e.jar");
		tb3e.start();

		// Install fragment bundle
		Bundle tb3d = getContext().installBundle(
				getWebServer() + "fragments.tb3d.jar");
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		pa.resolveBundles(new Bundle[] {tb3d});

		// Try recovering resource from host that is in classpath of fragment
		try {
			tb3e
					.loadClass("org.osgi.test.cases.framework.fragments.tb3d.SomeClass");
			fail("The class should not be in the classpath of host bundle tb3e with fragment-attachment:=\"none\"");
		}
		catch (ClassNotFoundException e) {
		}
		finally {
			tb3e.stop();
			tb3e.uninstall();
			tb3d.uninstall();
			ungetService(pa);
		}
	}

	/**
	 * Tests the fragment-attachment directive recognized by the framework for
	 * Bundle-SymbolicName taking the value of "resolve-time" which indicates
	 * that fragments are allowed to attach to the host bundle only during the
	 * process of resolving the host bundle.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFragmentAttachmentDirective02() throws Exception {
		// Install and start host bundle
		Bundle tb3f = getContext().installBundle(
				getWebServer() + "fragments.tb3f.jar");
		tb3f.start();

		// Install fragment bundle, host already resolved
		Bundle tb3d = getContext().installBundle(
				getWebServer() + "fragments.tb3d.jar");
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		pa.resolveBundles(new Bundle[] {tb3d});

		// Try recovering resource from host that is in classpath of fragment
		try {
			tb3f
					.loadClass("org.osgi.test.cases.framework.fragments.tb3d.SomeClass");
			fail("The class should not be in the classpath of host bundle tb3f with fragment-attachment:=\"resolve-time\"");
		}
		catch (ClassNotFoundException e) {
		}
		finally {
			tb3f.stop();
			tb3f.uninstall();
			tb3d.uninstall();
			ungetService(pa);
		}
	}

	/**
	 * Tests the fragment-attachment directive recognized by the framework for
	 * Bundle-SymbolicName taking the value of "resolve-time" which indicates
	 * that fragments are allowed to attach to the host bundle only during the
	 * process of resolving the host bundle.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFragmentAttachmentDirective03() throws Exception {
		// Install fragment bundle, host not yet resolved
		Bundle tb3d = getContext().installBundle(
				getWebServer() + "fragments.tb3d.jar");

		// Install and start host bundle
		Bundle tb3f = getContext().installBundle(
				getWebServer() + "fragments.tb3f.jar");
		tb3f.start();

		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		pa.resolveBundles(new Bundle[] {tb3d});

		// Try recovering resource from host that is in classpath of fragment
		try {
			tb3f
					.loadClass("org.osgi.test.cases.framework.fragments.tb3d.SomeClass");
		}
		catch (ClassNotFoundException e) {
			fail("The class should be in the classpath of host bundle tb3f with fragment-attachment:=\"resolve-time\"");
		}
		finally {
			tb3f.stop();
			tb3f.uninstall();
			tb3d.uninstall();
			ungetService(pa);
		}
	}

	/**
	 * Tests that attaching a fragment bundle to an already resolved host bundle
	 * is not possible when the fragment's Import-Package entries add additional
	 * packages to the host.
	 * 
	 * Tests that if an error occurs during the attachment of a fragment bundle
	 * then the fragment bundle is not attached to the host. A fragment bundle
	 * must enter the resolved state only if it has been successfully attached
	 * to one or more host bundles.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testAttachToResolvedHost01() throws Exception {
		// Install and start host bundle
		Bundle tb1a = getContext().installBundle(
				getWebServer() + "fragments.tb1a.jar");
		tb1a.start();

		// Install fragment bundle, host is already ACTIVE
		Bundle tb1e = getContext().installBundle(
				getWebServer() + "fragments.tb1e.jar");
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		pa.resolveBundles(new Bundle[] {tb1e});

		// Verify that fragment bundle is still in INSTALLED state
		try {
			assertEquals("Expecting fragment bundle to be in INSTALLED state.",
					Bundle.INSTALLED, tb1e.getState());
		}
		finally {
			tb1a.stop();
			tb1a.uninstall();
			tb1e.uninstall();
			ungetService(pa);
		}
	}

	/**
	 * Tests that attaching a fragment bundle to an already resolved host bundle
	 * is not possible when the fragment's Require-Bundle entries add additional
	 * required bundles to the host.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testAttachToResolvedHost02() throws Exception {
		// Install and start host bundle
		Bundle tb1a = getContext().installBundle(
				getWebServer() + "fragments.tb1a.jar");
		tb1a.start();

		// Install required bundle
		Bundle tb1b = getContext().installBundle(
				getWebServer() + "fragments.tb1b.jar");

		// Install fragment bundle, host is already ACTIVE
		Bundle tb1f = getContext().installBundle(
				getWebServer() + "fragments.tb1f.jar");
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		pa.resolveBundles(new Bundle[] {tb1f});

		// Verify that fragment bundle is still in INSTALLED state
		try {
			assertEquals("Expecting fragment bundle to be in INSTALLED state.",
					Bundle.INSTALLED, tb1f.getState());
		}
		finally {
			tb1a.stop();
			tb1a.uninstall();
			tb1b.uninstall();
			tb1f.uninstall();
			ungetService(pa);
		}
	}

	/**
	 * Tests that when an attached fragment bundle is updated, the content of
	 * the previous fragment remains attached to the host bundle. The new
	 * content of the updated fragment must not be allowed to attach to the host
	 * bundle until the Framework is restarted or the host bundle is refreshed.
	 * 
	 * When a set of bundles are refreshed using the PackageAdmin API then each
	 * bundle in the set must have an UNRESOLVED BundleEvent published.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFragmentUpdate() throws Exception {
		// Add BundleEvent listener
		purgeEvents();
		getContext().addBundleListener(this);

		// Install the host bundle
		Bundle tb1a = getContext().installBundle(
				getWebServer() + "fragments.tb1a.jar");

		// Install fragment bundle
		Bundle tb1g = getContext().installBundle(
				getWebServer() + "fragments.tb1g.jar");

		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		pa.resolveBundles(new Bundle[] {tb1g});
		// Start the host bundle
		tb1a.start();

		try {
			// Verify that fragment bundle is in RESOLVED state
			assertEquals("Expecting fragment bundle to be in RESOLVED state.",
					Bundle.RESOLVED, tb1g.getState());

			// Update fragment bundle
			InputStream in = getContext().getBundle().getResource(
					"fragments.tb1h.jar").openStream();
			tb1g.update(in);
			in.close();

			// Test that class resource is accessible
			try {
				tb1a
						.loadClass("org.osgi.test.cases.framework.fragments.tb1g.TestClass");
			}
			catch (ClassNotFoundException e) {
				fail("The class should exist in the host.");
			}

			// Refresh the host bundle
			pa.refreshPackages(new Bundle[] {tb1a});
			Thread.sleep(5000); // asynchronous call, wait some time

			// Verify if UNRESOLVED event was published for each bundle
			if (!hasEventOccurred(tb1a, BundleEvent.class,
					BundleEvent.UNRESOLVED)
					&& !hasEventOccurred(tb1g, BundleEvent.class,
							BundleEvent.UNRESOLVED)) {
				fail("Expecting BundleEvent of type UNRESOLVED to be published");
			}

			// Test that class resource is not accessible
			try {
				tb1a
						.loadClass("org.osgi.test.cases.framework.fragments.tb1g.TestClass");
				fail("The class should not exist in the host.");
			}
			catch (ClassNotFoundException e) {

			}
		}
		finally {
			tb1a.stop();
			tb1a.uninstall();
			tb1g.uninstall();
			ungetService(pa);
			getContext().removeBundleListener(this);
			purgeEvents();
		}

	}

	/**
	 * Tests that a fragment Import-Package entry conflicts with a host
	 * Import-Package entry if it has the same package name and any of its
	 * directives or matching attributes are different. If a conflict is found,
	 * the fragment is not attached to the host bundle.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testConflictingImportPackage() throws Exception {
		// Install and start bundles that export packages
		Bundle tb7c = getContext().installBundle(
				getWebServer() + "fragments.tb7c.jar");
		tb7c.start();
		Bundle tb7d = getContext().installBundle(
				getWebServer() + "fragments.tb7d.jar");
		tb7d.start();

		// Install fragment bundle
		Bundle tb7b = getContext().installBundle(
				getWebServer() + "fragments.tb7b.jar");

		// Install the host bundle
		Bundle tb7a = getContext().installBundle(
				getWebServer() + "fragments.tb7a.jar");

		// Start the host bundle
		tb7a.start();

		try {
			// Verify that fragment bundle is still in INSTALLED state
			assertEquals("Expecting fragment bundle to be in INSTALLED state.",
					Bundle.INSTALLED, tb7b.getState());
		}
		finally {
			tb7a.stop();
			tb7a.uninstall();
			tb7c.stop();
			tb7c.uninstall();
			tb7d.stop();
			tb7d.uninstall();
			tb7b.uninstall();
		}

	}

	/**
	 * Tests a fragment Export-Package entry with a different version than a
	 * host Export-Package entry with the same package name. Test that it
	 * attaches normally and the export with a different version from the
	 * fragment is available for import.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testConflictingExportPackage() throws Exception {
		// Install fragment bundle
		Bundle tb7e = getContext().installBundle(
				getWebServer() + "fragments.tb7e.jar");

		// Install the host bundle
		Bundle tb7c = getContext().installBundle(
				getWebServer() + "fragments.tb7c.jar");

		// Start the host bundle
		tb7c.start();

		// Install the bundle that uses the package exported by the fragment
		Bundle tb7h = getContext().installBundle(
				getWebServer() + "fragments.tb7h.jar");

		try {
			// Verify that fragment bundle is in RESOLVED state
			assertEquals("Expecting fragment bundle to be in RESOLVED state.",
					Bundle.RESOLVED, tb7e.getState());

			// Try starting the bundle that uses the package exported by the
			// fragment
			try {
				tb7h.start();
			}
			catch (BundleException e) {
				fail("Expecting the bundle to start.  The imported package from the fragment should resolve.");
			}
		}
		finally {
			tb7c.stop();
			tb7c.uninstall();
			tb7e.uninstall();
		}

	}

	/**
	 * Tests that a fragment Require-Bundle entry conflicts with a host
	 * Require-Bundle entry only if it has the same bundle symbolic name but a
	 * different version range. If a conflict is found, the fragment is not
	 * attached to the host bundle.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testConflictingRequireBundle() throws Exception {
		// Install required bundles
		Bundle tb8bv1 = getContext().installBundle(
				getWebServer() + "fragments.tb8b.jar");
		Bundle tb8bv2 = getContext().installBundle(
				getWebServer() + "fragments.tb8c.jar");

		// Install the fragment bundle
		Bundle tb8d = getContext().installBundle(
				getWebServer() + "fragments.tb8d.jar");

		// Install the host bundle
		Bundle tb8a = getContext().installBundle(
				getWebServer() + "fragments.tb8a.jar");

		// Start the host bundle
		tb8a.start();

		try {
			// Verify that fragment bundle is in INSTALLED state
			assertEquals("Expecting fragment bundle to be in INSTALLED state.",
					Bundle.INSTALLED, tb8d.getState());
		}
		finally {
			tb8a.stop();
			tb8a.uninstall();
			tb8d.uninstall();
			tb8bv1.uninstall();
			tb8bv2.uninstall();
		}
	}

	/**
	 * Test that when a fragment bundle becomes unresolved the Framework
	 * detaches it from the host and reresolve the host bundle and reattaches
	 * the remaining attached fragments.
	 * 
	 * Tests that when the bundle is resolved by the framework, the framework
	 * publishes a Bundle Event of type RESOLVED. A bundle may become unresolved
	 * at some future time. This could occur as a result of uninstalling the
	 * bundle, refreshing its package or some other reason. When a bundle
	 * becomes unresolved, the framework must publish a Bundle Event of type
	 * UNRESOLVED.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.uninstall()
	 */
	public void testFragmentBundleDetach() throws Exception {
		URL url;
		InputStream ins;
		BufferedReader bufr;
		String line;

		// Add BundleListener
		purgeEvents();
		getContext().addBundleListener(this);

		// Install fragment bundles
		Bundle tb1b = getContext().installBundle(
				getWebServer() + "fragments.tb1b.jar");
		Bundle tb1g = getContext().installBundle(
				getWebServer() + "fragments.tb1g.jar");

		// Install the host bundle
		Bundle tb1a = getContext().installBundle(
				getWebServer() + "fragments.tb1a.jar");

		// Start the host bundle
		tb1a.start();
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);

		try {
			// Verify bundle events were published by the framework
			Thread.sleep(2000); // wait a while
			assertTrue("Expecting BundleEvent of type RESOLVED.",
					hasEventOccurred(tb1b, BundleEvent.class,
							BundleEvent.RESOLVED));
			assertTrue("Expecting BundleEvent of type RESOLVED.",
					hasEventOccurred(tb1g, BundleEvent.class,
							BundleEvent.RESOLVED));
			assertTrue("Expecting BundleEvent of type RESOLVED.",
					hasEventOccurred(tb1a, BundleEvent.class,
							BundleEvent.RESOLVED));

			// Verify that fragment bundles are in RESOLVED state
			assertEquals("Expecting fragment bundle to be in RESOLVED state.",
					Bundle.RESOLVED, tb1b.getState());
			assertEquals("Expecting fragment bundle to be in RESOLVED state.",
					Bundle.RESOLVED, tb1g.getState());

			// Verify resource from tb1b is accessible
			ins = tb1a.getResource("resources/notinhost.txt").openStream();
			bufr = new BufferedReader(new InputStreamReader(ins));
			line = bufr.readLine();
			assertEquals("Expecting resource from tb1b to be accessible.",
					"tb1b", line);
			bufr.close();

			// Detach / uninstall fragment bundle tb1b
			tb1b.uninstall();

			// Verify bundle event was published by the framework
			Thread.sleep(2000); // wait a while
			assertTrue("Expecting BundleEvent of type UNRESOLVED.",
					hasEventOccurred(tb1b, BundleEvent.class,
							BundleEvent.UNRESOLVED));

			// Verify that tb1b is in UNINSTALLED state
			assertEquals(
					"Expecting fragment bundle to be in UNINSTALLED state.",
					Bundle.UNINSTALLED, tb1b.getState());

			// Refresh host bundle
			pa.refreshPackages(new Bundle[] {tb1a});
			Thread.sleep(2000); // wait a while

			// Verify resource from tb1b is not accessible
			ins = tb1a.getResource("resources/notinhost.txt").openStream();
			bufr = new BufferedReader(new InputStreamReader(ins));
			line = bufr.readLine();
			assertEquals("Expecting resource from tb1b to be inaccessible.",
					"tb1g", line);
			bufr.close();
		}
		finally {
			tb1a.stop();
			tb1a.uninstall();
			tb1g.uninstall();
			getContext().removeBundleListener(this);
			purgeEvents();
			ungetService(pa);
		}
	}

	/**
	 * @param event
	 * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
	 */
	public void bundleChanged(BundleEvent event) {
		addEvent(event.getBundle(), BundleEvent.class, event.getType());
	}

	/**
	 * @param event
	 * @see org.osgi.framework.FrameworkListener#frameworkEvent(org.osgi.framework.FrameworkEvent)
	 */
	public void frameworkEvent(FrameworkEvent event) {
		addEvent(event.getBundle(), FrameworkEvent.class, event.getType());
	}

	/**
	 * Add an event to the table of events occurred since the last call to
	 * purgeEvents.
	 * 
	 * @param bundle Bundle whose event has been captured
	 * @param eventClass Class of the event object
	 * @param eventType Event type published
	 */
	public void addEvent(Bundle bundle, Class eventClass, int eventType) {
		trace("Captured " + eventClass.getName() + " for bundle "
				+ bundle.getSymbolicName() + " of type " + eventType);
		String key = bundle.getSymbolicName() + SEPARATOR
				+ eventClass.getName() + SEPARATOR + eventType;
		events.put(key, bundle);
	}

	/**
	 * Purge all events in the events table.
	 */
	public void purgeEvents() {
		events.clear();
	}

	/**
	 * Verify if the event has occurred (exists in the events table).
	 * 
	 * @param bundle Bundle whose event has been captured
	 * @param eventClass Class of the event object
	 * @param eventType Event type published
	 * @return
	 */
	public boolean hasEventOccurred(Bundle bundle, Class eventClass,
			int eventType) {
		boolean retVal = false;
		String key = bundle.getSymbolicName() + SEPARATOR
				+ eventClass.getName() + SEPARATOR + eventType;
		retVal = events.containsKey(key);
		return retVal;
	}

	/**
	 * Tests a fragment bundle where extension directive is not system.bundle.
	 * The installation of the fragment must fail.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBadExtensionBundle() throws Exception {
		// installing bad extension bundle
		try {
			Bundle tb4 = getContext().installBundle(
					getWebServer() + "fragments.tb4.jar");
			// instalation should fail
			failException("Expected installation failure " + tb4.getLocation(),
					BundleException.class);
		}
		catch (BundleException e) {
			// expected
		}
	}

	/**
	 * Tests if a boot classpath extension bundle's classpath is appended to the
	 * boot classpath. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionBundle() throws Exception {
		String class5 = "org.osgi.test.cases.framework.fragments.tb5.FooTB5";
		Bundle tb5 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				// install extension bundle
				tb5 = getContext().installBundle(
						getWebServer() + "fragments.tb5.jar");
				Bundle systemBundle = getContext().getBundle(0);
				// check if classloader is boot classloader
				try {
					assertEquals("loaded by the boot classloader", systemBundle
							.loadClass(class5).getClassLoader(), Class.class
							.getClassLoader());
					assertTrue("bootclasspath extension bundle is resolved",
							(tb5.getState() & Bundle.RESOLVED) != 0);
				}
				catch (ClassNotFoundException cnfe) {
					if ((tb5.getState() & Bundle.RESOLVED) != 0)
						fail("failed loading class from a resolved bootclasspath extension bundle");
					assertTrue("bootclasspath extension bundle is installed",
							(tb5.getState() & Bundle.INSTALLED) != 0);
				}
			}
			catch (BundleException be) {
				fail("installing bootclasspath extension bundle");
			}
			finally {
				if (tb5 != null) {
					tb5.uninstall();
				}
			}
		}
		else {
			String message = "bootclasspath extension bundle instalation not supported";
			try {
				// tries to install extension bundle
				tb5 = getContext().installBundle(
						getWebServer() + "fragments.tb5.jar");
				// instalation should fail
				failException(message, BundleException.class);
			}
			catch (BundleException e) {
				assertException(message, UnsupportedOperationException.class, e
						.getNestedException());
			}
			finally {
				if (tb5 != null) {
					tb5.uninstall();
				}
			}
		}
	}

	/**
	 * Tests if a framework extension bundle's classpath is appended to the
	 * framework classpath. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionBundle() throws Exception {
		String class6 = "org.osgi.test.cases.framework.fragments.tb6.FooTB6";
		Bundle tb6 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				// install extension bundle
				tb6 = getContext().installBundle(
						getWebServer() + "fragments.tb6.jar");
				// check if classloader is framework classloader
				try {
					assertEquals("loaded by the framework classloader",
							getContext().getClass().getClassLoader().loadClass(
									class6).getClassLoader(), getContext()
									.getClass().getClassLoader());
					assertTrue("framework extension bundle is resolved", (tb6
							.getState() & Bundle.RESOLVED) != 0);
				}
				catch (ClassNotFoundException cnfe) {
					if ((tb6.getState() & Bundle.RESOLVED) != 0)
						fail("failed loading class from a resolved framework extension bundle");
					assertTrue("framework extension bundle is installed", (tb6
							.getState() & Bundle.INSTALLED) != 0);
				}
			}
			catch (BundleException be) {
				fail("installing framework extension bundle");
			}
			finally {
				if (tb6 != null) {
					tb6.uninstall();
				}
			}

		}
		else {
			String message = "framework extension bundle instalation should fail";
			try {
				// tries to install extension bundle
				tb6 = getContext().installBundle(
						getWebServer() + "fragments.tb6.jar");
				// installation should fail
				failException(message, BundleException.class);
			}
			catch (BundleException e) {
				assertException(message, UnsupportedOperationException.class, e
						.getNestedException());
			}
			finally {
				if (tb6 != null) {
					tb6.uninstall();
				}
			}
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to load classes
	 * directly. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionBundleLoadClass() throws Exception {
		String class6 = "org.osgi.test.cases.framework.fragments.tb6.FooTB6";
		String message = "extension bundle cannot load classes";
		Bundle tb6 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			// install extension bundle
			tb6 = getContext().installBundle(
					getWebServer() + "fragments.tb6.jar");
			try {
				tb6.loadClass(class6);
				// should fail, since extension bundles are not able to load
				// classes directly
				failException(message, ClassNotFoundException.class);
			}
			catch (Exception e) {
				assertException(message, ClassNotFoundException.class, e);
			}
			finally {
				tb6.uninstall();
			}

		}
		else {
			trace("framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a boot classpath extension bundle is not able to load classes
	 * directly. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionBundleLoadClass() throws Exception {
		String class5 = "org.osgi.test.cases.framework.fragments.tb5.FooTB5";
		String message = "boot extension bundle cannot load classes";
		Bundle tb5 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			// install extension bundle
			tb5 = getContext().installBundle(
					getWebServer() + "fragments.tb5.jar");
			try {
				tb5.loadClass(class5);
				// should fail, since extension bundles are not able to load
				// classes directly
				failException(message, ClassNotFoundException.class);
			}
			catch (Exception e) {
				assertException(message, ClassNotFoundException.class, e);
			}
			finally {
				tb5.uninstall();
			}

		}
		else {
			trace("boot classpath extension bundles not supported");
		}
	}

	/**
	 * Tests if an extension bundle is treated as a framework extension by
	 * default. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionDefault() throws Exception {
		String class21 = "org.osgi.test.cases.framework.fragments.tb21.FooTB21";
		Bundle tb21 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				// install extension bundle
				tb21 = getContext().installBundle(
						getWebServer() + "fragments.tb21.jar");
				// check if classloader is framework classloader
				try {
					assertEquals("loaded by the framework classloader",
							getContext().getClass().getClassLoader().loadClass(
									class21).getClassLoader(), getContext()
									.getClass().getClassLoader());
					assertTrue("framework extension bundle is resolved", (tb21
							.getState() & Bundle.RESOLVED) != 0);
				}
				catch (ClassNotFoundException cnfe) {
					if ((tb21.getState() & Bundle.RESOLVED) != 0)
						fail("failed loading class from a resolved framework extension bundle");
					assertTrue("framework extension bundle is installed", (tb21
							.getState() & Bundle.INSTALLED) != 0);
				}
			}
			catch (BundleException be) {
				fail("installing framework extension bundle");
			}
			finally {
				if (tb21 != null) {
					tb21.uninstall();
				}
			}
		}
		else {
			trace("framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to load native
	 * libraries. Will only perform this test if
	 * <code>SUPPORTS_FRAMEWORK_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionNativeCode() throws Exception {
		String message = "extension bundle cannot load native code";
		Bundle tb22 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				tb22 = getContext().installBundle(
						getWebServer() + "fragments.tb22.jar");
				// should fail, since extension bundles are not able to
				// declare native code headers
				failException(message, BundleException.class);
			}
			catch (Exception e) {
				assertException(message, BundleException.class, e);
			}
			finally {
				if (tb22 != null) {
					tb22.uninstall();
				}
			}
		}
		else {
			trace("framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a boot classpath extension bundle is not able to load native
	 * libraries. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionNativeCode() throws Exception {
		String message = "extension bundle cannot load native code";
		Bundle tb13 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				tb13 = getContext().installBundle(
						getWebServer() + "fragments.tb13.jar");
				// should fail, since extension bundles are not able to
				// declare native code headers
				failException(message, BundleException.class);
			}
			catch (Exception e) {
				assertException(message, BundleException.class, e);
			}
			finally {
				if (tb13 != null) {
					tb13.uninstall();
				}
			}
		}
		else {
			trace("boot classpath extension bundles not supported");
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to import packages.
	 * Will only perform this test if <code>SUPPORTS_FRAMEWORK_EXTENSION</code>
	 * equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionImportPackage() throws Exception {
		String message = "extension bundle cannot import packages";
		Bundle tb9 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				tb9 = getContext().installBundle(
						getWebServer() + "fragments.tb9.jar");
				// should fail, since extension bundles are not able to
				// import packages
				failException(message, BundleException.class);
			}
			catch (Exception e) {
				assertException(message, BundleException.class, e);
			}
			finally {
				if (tb9 != null) {
					tb9.uninstall();
				}
			}
		}
		else {
			trace("framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a boot classpath extension bundle is not able to import
	 * packages. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionImportPackage() throws Exception {
		String message = "extension bundle cannot import packages";
		Bundle tb12 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				tb12 = getContext().installBundle(
						getWebServer() + "fragments.tb12.jar");
				// should fail, since extension bundles are not able to
				// import packages
				failException(message, BundleException.class);
			}
			catch (Exception e) {
				assertException(message, BundleException.class, e);
			}
			finally {
				if (tb12 != null) {
					tb12.uninstall();
				}
			}
		}
		else {
			trace("boot classpath extension bundles not supported");
		}
	}

	/**
	 * Tests if a framework extension bundle is not able to require bundles.
	 * Will only perform this test if <code>SUPPORTS_FRAMEWORK_EXTENSION</code>
	 * equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testFrameworkExtensionRequireBundle() throws Exception {
		String message = "extension bundle cannot require bundles";
		Bundle tb10 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_FRAMEWORK_EXTENSION))) {
			try {
				tb10 = getContext().installBundle(
						getWebServer() + "fragments.tb10.jar");
				// should fail, since extension bundles are not able to
				// require bundles
				failException(message, BundleException.class);
			}
			catch (Exception e) {
				assertException(message, BundleException.class, e);
			}
			finally {
				if (tb10 != null) {
					tb10.uninstall();
				}
			}
		}
		else {
			trace("framework extension bundles not supported");
		}
	}

	/**
	 * Tests if a boot classpath extension bundle is not able to require
	 * bundles. Will only perform this test if
	 * <code>SUPPORTS_BOOTCLASSPATH_EXTENSION</code> equals <code>true</code>.
	 * 
	 * @throws Exception if an error occurs or an assertion fails in the test.
	 * @spec Bundle.installBundle(String)
	 */
	public void testBootClasspathExtensionRequireBundle() throws Exception {
		String message = "extension bundle cannot require bundles";
		Bundle tb15 = null;
		if ("true".equals(getContext().getProperty(
				Constants.SUPPORTS_BOOTCLASSPATH_EXTENSION))) {
			try {
				tb15 = getContext().installBundle(
						getWebServer() + "fragments.tb15.jar");
				// should fail, since extension bundles are not able to
				// require bundles
				failException(message, BundleException.class);
			}
			catch (Exception e) {
				assertException(message, BundleException.class, e);
			}
			finally {
				if (tb15 != null) {
					tb15.uninstall();
				}
			}
		}
		else {
			trace("boot classpath extension bundles not supported");
		}
	}
}