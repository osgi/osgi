/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.junit.div;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.test.cases.framework.div.tb6.BundleClass;
import org.osgi.test.support.FrameworkEventCollector;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Radio Systems AB
 */
public class DivTests extends DefaultTestBundleControl {
	public static final String	basePath	= "/org/osgi/test/cases/framework/div/";
	public static final String	basePkg		= "org.osgi.test.cases.framework.div.";

	/**
	 * Logs the manifest headers.
	 */
	public void testManifestHeaders() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb1.jar");
		try {
			tb.start();
			Dictionary h = tb.getHeaders("");
			assertEquals("numeric first char", h.get("5-"));
			assertEquals(basePkg + "tb1.CheckManifest", h
					.get("bundle-activator"));
			assertEquals("should contain the bundle category", h
					.get("bundle-category"));
			assertEquals("., foo/bar/dummy.jar", h.get("bundle-classpath"));
			assertEquals("info@ericsson.com", h.get("bundle-contactaddress"));
			assertEquals("should contain the bundle copyright", h
					.get("bundle-copyright"));
			assertEquals("Contains the manifest checked by the test case.", h
					.get("bundle-description"));
			assertEquals("http://www.ericsson.com", h.get("bundle-docurl"));
			assertEquals(basePkg + "tb1", h.get("bundle-name"));
			assertEquals("www.ericsson.se", h.get("bundle-updatelocation"));
			assertEquals("Ericsson Radio Systems AB", h.get("bundle-vendor"));
			assertEquals("Improper value for bundle manifest version 2", h
					.get("bundle-version"));
			assertEquals("12                          34", h.get("continue"));
			assertEquals(
					"org.osgi.dummy1;                         version=\"0.0\", org.osgi.dummy2,org.osgi.dummy3;version=\"19.67.34\"",
					h.get("export-package"));
			assertEquals(
					"should contain the exported services, not used by framework",
					h.get("export-service"));
			assertEquals(
					"This bundle is defined by developer and should be ignored by framework",
					h.get("fakeheader"));
			assertEquals(
					"should contain the imported services, not used by framework",
					h.get("import-service"));
			assertEquals("1.0", h.get("manifest-version"));
			assertEquals(
					"xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxEND",
					h.get("max-length"));
			assertEquals("\u00d0\u00de", h.get("unicode-test"));

			tb.stop();
		}
		catch (BundleException e) {
			fail("Exception in manifest headers", e);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests empty manifest headers.
	 */
	public void testMissingManifestHeaders() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb5.jar");
		try {
			tb.start();
			Dictionary h = tb.getHeaders();
			assertEquals(1, h.size());
			assertEquals("1.0", h.get("manifest-version"));
			tb.stop();
		}
		catch (BundleException e) {
			fail("Exception in testing missing manifest headers", e);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests extended classpath
	 */
	public void testBundleClassPath() {
		// instantiate an object from tbcinner.jar
		BundleClass dummy = null;
		try {
			dummy = new BundleClass();
		}
		catch (Throwable e) {
			fail(
					"We didn't manage to instansiate a class from the extended bundle class path",
					e);
		}
		assertEquals("different class loader", dummy.getClass()
				.getClassLoader(), this.getClass().getClassLoader());
	}

	/**
	 * Tests that location remains the same after an update
	 */
	public void testBundleLocation() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb5.jar");
		try {
			String originalLocation = tb.getLocation();
			long originalLastModified = tb.getLastModified();
			tb.update();
			assertEquals("bundle location changed after update.",
					originalLocation, tb.getLocation());
			assertFalse("bundle last modified did not change",
					originalLastModified == tb.getLastModified());
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests basic native code invocation.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCode() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb2.jar");
		try {
			tb.start();
		}
		catch (BundleException be) {
			fail("Native code not installed. " + reportProcessorOS(), be);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests to add a FrameworkListener.
	 */
	public void testFrameworkListener() throws Exception {
		FrameworkEventCollector fec = new FrameworkEventCollector(
				FrameworkEvent.ERROR);
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb3.jar");
		getContext().addFrameworkListener(fec);
		tb.start();
		tb.uninstall();
		List result = fec.getList(1, 10000 * OSGiTestCaseProperties
				.getScaling());
		getContext().removeFrameworkListener(fec);
		assertEquals("No FrameworkEvent received", 1, result.size());
		FrameworkEvent fe = (FrameworkEvent) result.get(0);
		assertEquals("No FrameworkEvent received", tb, fe.getBundle());
		Throwable t = fe.getThrowable();
		assertNotNull(t);
		assertTrue(t instanceof BundleException);
	}

	/**
	 * Tests the file system.
	 */
	public void testFileAccess() throws Exception {
		File file = getContext().getDataFile("testfile");
		if (file == null) {
			log("Framework lacks filesystem support, no error.");
			return;
		}
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.println("Line 1");
		out.println("Line 2");
		out.println("Line 3");
		out.close();
		BufferedReader in = new BufferedReader(new FileReader(file));
		assertEquals("Line 1", in.readLine());
		assertEquals("Line 2", in.readLine());
		assertEquals("Line 3", in.readLine());
		in.close();
		assertTrue(file.delete());
		try {
			in = new BufferedReader(new FileReader(file));
			fail("File was not gone, Error!");
		}
		catch (FileNotFoundException fnfe) {
			// expected
		}
	}

	public void testBundleZero() {
		// Bundle(0).update is tested in permission/tc5
		try {
			getContext().getBundle(0).start();
		}
		catch (BundleException e) {
			fail("bundle(0).start threw Exception", e);
		}
		try {
			getContext().getBundle(0).uninstall();
			fail("bundle(0).uninstall returned without Exception");
		}
		catch (BundleException e) {
			// expected
		}
	}

	public void testEERequirement() throws Exception {
		final String ee = getContext()
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
		log("EE: " + ee);
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb7a.jar");
		try {
			tb.start();
		}
		catch (BundleException e) {
			fail("Required Execution Environment is available", e);
		}
		finally {
			tb.uninstall();
		}

		tb = getContext().installBundle(getWebServer() + "div.tb7b.jar");
		try {
			tb.start();
			fail("Required Execution Environment is not available");
		}
		catch (BundleException e) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
		}
		finally {
			tb.uninstall();
		}

	}

	/**
	 * Tests native code selection filter. The bundle should be loaded even if
	 * no native code clause matches the selection filter, since there's an
	 * optional clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterOptional() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb12.jar");
		try {
			tb.start();
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code selection filter. The bundle should NOT be loaded if no
	 * native code clause matches the selection filter, since there's no
	 * optional clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterNoOptional() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb15.jar");
		try {
			tb.start();
			fail("Bundle should not start!");
		}
		catch (BundleException be) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code selection filter. The bundle should only be loaded if
	 * at least one native code clause matches the selection filter, since
	 * there's no optional clause present (*). This test also checks if the new
	 * osname alias (win32) matches properly (OSGi R4).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterAlias() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb16.jar");
		try {
			tb.start();
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code from a fragment bundle. The native code should be
	 * loaded from a fragment bundle of the host bundle.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFragment() throws Exception {
		Bundle tbFragment = getContext().installBundle(
				getWebServer() + "div.tb18.jar");
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb17.jar");
		try {
			tb.start();
		}
		finally {
			tb.uninstall();
			tbFragment.uninstall();
		}
	}

	/**
	 * Tests native code language filter. The bundle should NOT be loaded if no
	 * native code clause matches the os language, since there's no optional
	 * clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeLanguage() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb19.jar");
		try {
			tb.start();
			fail("Error: Bundle should NOT be loaded: language should not match");
		}
		catch (BundleException be) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code language filter. The bundle should be loaded since all
	 * valid languages are included in the filter.
	 * 
	 * @see http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt for valid
	 *      language codes.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeLanguageSuccess() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb20.jar");
		try {
			tb.start();
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests native code os version. The bundle should NOT be loaded if no
	 * native code clause matches the os version range, since there's no
	 * optional clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeVersion() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb21.jar");
		try {
			tb.start();
			fail("Error: Bundle should NOT be loaded: os version out of range");
		}
		catch (BundleException be) {
            // expecting exception, but bundle should not have resolved
            assertEquals("Bundle should not be resolved!", Bundle.INSTALLED, tb.getState());
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests successful native code os version. The bundle should be loaded
	 * since the version range should contain all valid os versions.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeVersionSuccess() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb22.jar");
		try {
			tb.start();
		}
		finally {
			tb.uninstall();
		}
	}

	public void testBundleEventConstants() {
		assertConstant(new Integer(0x00000001), "INSTALLED", BundleEvent.class);
		assertConstant(new Integer(0x00000002), "STARTED", BundleEvent.class);
		assertConstant(new Integer(0x00000004), "STOPPED", BundleEvent.class);
		assertConstant(new Integer(0x00000008), "UPDATED", BundleEvent.class);
		assertConstant(new Integer(0x00000010), "UNINSTALLED",
				BundleEvent.class);
		assertConstant(new Integer(0x00000020), "RESOLVED", BundleEvent.class);
		assertConstant(new Integer(0x00000040), "UNRESOLVED", BundleEvent.class);
		assertConstant(new Integer(0x00000080), "STARTING", BundleEvent.class);
		assertConstant(new Integer(0x00000100), "STOPPING", BundleEvent.class);
		assertConstant(new Integer(0x00000200), "LAZY_ACTIVATION",
				BundleEvent.class);
	}

	public void testFrameworkEventConstants() {
		assertConstant(new Integer(0x00000001), "STARTED", FrameworkEvent.class);
		assertConstant(new Integer(0x00000002), "ERROR", FrameworkEvent.class);
		assertConstant(new Integer(0x00000004), "PACKAGES_REFRESHED",
				FrameworkEvent.class);
		assertConstant(new Integer(0x00000008), "STARTLEVEL_CHANGED",
				FrameworkEvent.class);
		assertConstant(new Integer(0x00000010), "WARNING", FrameworkEvent.class);
		assertConstant(new Integer(0x00000020), "INFO", FrameworkEvent.class);
		assertConstant(new Integer(0x00000040), "STOPPED", FrameworkEvent.class);
		assertConstant(new Integer(0x00000080), "STOPPED_UPDATE",
				FrameworkEvent.class);
		assertConstant(new Integer(0x00000100),
				"STOPPED_BOOTCLASSPATH_MODIFIED", FrameworkEvent.class);
		assertConstant(new Integer(0x00000200), "WAIT_TIMEDOUT",
				FrameworkEvent.class);
	}

	public void testBundleGetEntry() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {

			URL url = bundle.getEntry(basePath + "tb10/Foo.class");
			assertNotNull(
					"Testing the method invocation with an existing entry", url);

			url = bundle.getEntry(basePath + "tb10/Nonexistent");
			assertNull(
					"Testing the method invocation with an nonexistent entries",
					url);

			bundle.uninstall();

			try {
				bundle.getEntry(basePath + "tb10/Nonexistent");
				fail("Testing the method invocation with an uninstalled bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}
		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetEntryPaths() throws Exception {
		String[] expectedEntryPaths = {
				"org/osgi/test/cases/framework/div/tb10/Activator.class",
				"org/osgi/test/cases/framework/div/tb10/Bar.class",
				"org/osgi/test/cases/framework/div/tb10/Foo.class",
				"org/osgi/test/cases/framework/div/tb10/TestService.class",
				"org/osgi/test/cases/framework/div/tb10/TestServiceImpl.class"};

		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			Enumeration enumeration = bundle.getEntryPaths(basePath + "tb10");
			assertNotNull("Check if some resource is returned", enumeration);

			int count = 0;
			while (enumeration.hasMoreElements()) {
				String entryPath = (String) enumeration.nextElement();

				for (int i = 0; i < expectedEntryPaths.length; i++) {
					if (entryPath.equals(expectedEntryPaths[i])) {
						count++;
					}
				}
			}

			assertEquals("Checking the returned entries",
					expectedEntryPaths.length, count);

			enumeration = bundle.getEntryPaths(basePath + "tb10/nonexistent");
			assertNull(
					"Testing the method invocation with nonexistent entries",
					enumeration);

			bundle.uninstall();

			try {
				bundle.getEntryPaths(basePath + "tb10/incorrect");
				fail("Testing the method invocation with an uninstalled bundle");
			}
			catch (IllegalStateException ex) {
				// Ignore this exception
				bundle = null;
			}

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetResource() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		Bundle fragment = getContext().installBundle(
				getWebServer() + "div.tb13.jar");

		try {
			URL url = bundle.getResource(basePath + "tb10/Foo.class");
			assertNotNull(
					"Testing the method invocation with an existing resource (using a absolute path)",
					url);
			url = bundle.getResource(basePath + "tb10/Nonexistent");
			assertNull(
					"Testing the method invocation with a nonexistent resource",
					url);

			url = fragment.getResource(basePath + "tb13/Foo.class");
			assertNull(
					"A fragment bundle cannot return a resource using the method getResource()",
					url);

			bundle.uninstall();

			try {
				bundle
						.getResource("/org/osgi/test/cases/framework/div/tb10/Foo.class");
				fail("Testing  the method invocation after uninstall the bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
			if (fragment != null) {
				fragment.uninstall();
			}
		}
	}

	public void testBundleGetResources() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		Bundle fragment = getContext().installBundle(
				getWebServer() + "div.tb13.jar");

		try {
			Enumeration enumeration = bundle.getResources(basePath
					+ "tb10/Foo.class");
			assertNotNull(
					"Testing the method invocation with an existing resource (using a absolute path)",
					enumeration);
			enumeration = bundle.getResources(basePath + "tb10/Nonexistent");
			assertNull(
					"Testing the method invocation with a nonexistent resource",
					enumeration);

			enumeration = fragment.getResources(basePath + "tb13/Foo.class");
			assertNull(
					"A fragment bundle cannot return a resource using the method getResource()",
					enumeration);

			bundle.uninstall();

			try {
				bundle.getResources(basePath + "tb10/Foo.class");
				fail("Testing  the method invocation after uninstall the bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
			if (fragment != null) {
				fragment.uninstall();
			}
		}
	}

	public void testBundleGetSymbolicName1() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			String bsn = bundle.getSymbolicName();
			assertEquals(
					"Testing the method getSymbolicName() with a symbolic name in the manifest",
					basePkg + "tb10", bsn);

			bundle.uninstall();
			bsn = bundle.getSymbolicName();
			bundle = null;
			assertEquals(
					"Testing the method getSymbolicName() with a symbolic name in the manifest",
					basePkg + "tb10", bsn);

		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetSymbolicName2() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb11.jar");
		try {
			String bsn = bundle.getSymbolicName();
			assertNull(
					"Testing the method getSymbolicName() without a symbolic name in the manifest",
					bsn);

			bundle.uninstall();
			bsn = bundle.getSymbolicName();
			bundle = null;

			assertNull(
					"Testing the method getSymbolicName() after uninstall the bundle (without a symbolic name in the manifest)",
					bsn);
		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleGetBundleContext1() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			assertNull("BundleContext for installed bundle must be null",
					bundle.getBundleContext());

			bundle.start();
			assertNotNull("BundleContext for started bundle must not be null",
					bundle.getBundleContext());

			assertEquals("Bundle id via BundleContext must equal original id",
					bundle.getBundleId(), bundle.getBundleContext().getBundle()
							.getBundleId());

			bundle.stop();
			assertNull("BundleContext for stopped bundle must be null", bundle
					.getBundleContext());
		}
		finally {
			bundle.uninstall();
			assertNull("BundleContext for uninstalled bundle must be null",
					bundle.getBundleContext());
		}

		bundle = getContext().getBundle(0);

		assertNotNull("BundleContext for system bundle must not be null",
				bundle.getBundleContext());

		assertEquals("Bundle id via BundleContext must equal zero", 0L, bundle
				.getBundleContext().getBundle().getBundleId());

		assertEquals(
				"BundleContext for test case should match context passed to activator",
				getContext(), getContext().getBundle().getBundleContext());
	}

	public void testBundleGetBundleContext2() throws Exception {
		Bundle host, fragment;

		fragment = getContext().installBundle(getWebServer() + "div.tb18.jar");
		host = getContext().installBundle(getWebServer() + "div.tb17.jar");

		try {
			host.start(); // resolve the bundles
			assertNotNull("BundleContext for host bundle must not be null",
					host.getBundleContext());
			assertNull("BundleContext for fragment bundle must be null",
					fragment.getBundleContext());

			host.stop();
			assertNull("BundleContext for stopped host bundle must be null",
					host.getBundleContext());
			assertNull("BundleContext for fragment bundle must be null",
					fragment.getBundleContext());
		}
		finally {
			fragment.uninstall();
			host.uninstall();
		}
	}

	public void testBundleLoadClass1() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb10.jar");
		try {
			bundle.start();
			Class clazz = null;
			try {
				clazz = bundle.loadClass(basePkg + "tb10.Foo");
			}
			catch (ClassNotFoundException ex) {
				fail(
						"Testing the method loadClass() with an installed bundle and a existing class",
						ex);
			}

			ServiceReference sr = getContext().getServiceReference(
					basePkg + "tb10.TestService");

			Object service = getContext().getService(sr);
			ClassLoader classLoader = (ClassLoader) service.getClass()
					.getMethod("getClassLoader", null).invoke(service, null);
			assertEquals(
					"Expecting the ClassLoader of the class and the bundle to be the same",
					clazz.getClassLoader(), classLoader);

			try {
				clazz = bundle
						.loadClass(basePkg + "tb10.NonExistent");
				fail("Testing the method loadClass() with an installed bundle and a nonexistent class");
			}
			catch (ClassNotFoundException ex) {
				// This is an expected exception and can be ignored
			}

			bundle.uninstall();
			try {
				bundle.loadClass(basePkg + "tb10.Foo");
				fail("Testing the method after uninstall the bundle");
			}
			catch (IllegalStateException ex) {
				// This is an expected exception and can be ignored
				bundle = null;
			}
		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}
		}
	}

	public void testBundleLoadClass2() throws Exception {
		Bundle bundle = getContext().installBundle(
				getWebServer() + "div.tb13.jar");
		try {
			try {
				bundle.loadClass(basePkg + "tb13.Foo");
				fail("Testing the method loadClass() with a fragment bundle");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}

			try {
				bundle.loadClass(basePkg + "tb13.Nonexistent");
				fail("Testing the method loadClass() with a fragment bundle");
			}
			catch (ClassNotFoundException ex) {
				// expected
			}
		}
		finally {
			bundle.uninstall();
		}
	}
	
	/**
	 * Tests service registration.
	 */
	public void testBundleContextRegisterService() throws Exception {
		Bundle tb24a = getContext().installBundle(
				getWebServer() + "div.tb24a.jar");
		Bundle tb24b = getContext().installBundle(
				getWebServer() + "div.tb24b.jar");
		Bundle tb24c = getContext().installBundle(
				getWebServer() + "div.tb24c.jar");

		tb24a.start();
		tb24b.start();

		try {
			tb24c.start();
			tb24c.stop();
		}
		catch (BundleException ex) {
			fail("A bundle can register a service when the package is shared");
		}
		finally {
			tb24b.stop();
			tb24a.stop();

			tb24c.uninstall();
			tb24b.uninstall();
			tb24a.uninstall();
		}
	}

	/**
	 * Test the Version constructor with legal parameters
	 * 
	 * @spec Version.<init>(int,int,int)
	 */
	public void testVersionConstructors() {
		new Version(0, 0, 0);

		new Version(0, 0, 0, "a");

		/**
		 * Test the Version constructor with legal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */

		new Version("0.0.0");

		/**
		 * Test the Version constructor with illegal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */
		try {
			new Version(-1, -1, -1);
			fail("Version created with illegal constructors");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}

		/**
		 * Test the Version constructor with legal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */
		try {
			new Version(null);
			fail("Version created with illegal constructors");
		}
		catch (Exception ex) {
			// This is an expected exception and may be ignored
		}

		/**
		 * Test the Version constructor with legal parameters
		 * 
		 * @spec Version.<init>(int,int,int)
		 */
		try {
			new Version("");
			fail("Version created with illegal constructors");
		}
		catch (IllegalArgumentException ex) {
			// This is an expected exception and may be ignored
		}
	}

	public void testVersionEquals() {
		Version version1;
		Version version2;

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 0);

		assertEquals("Testing the method equals() with the same versions",
				version1, version2);

		/**
		 * Test the method equals() with the same versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(0, 0, 0, "a");

		assertEquals("Testing the method equals() with the same versions",
				version1, version2);

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(1, 0, 0);

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 1, 0);

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 1);

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(0, 0, 0, "b");

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}

		/**
		 * Test the method equals() with different versions
		 * 
		 * @spec Version.equals(Object)
		 */

		version1 = new Version(0, 0, 0, "a");
		version2 = new Version(1, 1, 1, "b");

		if (version1.equals(version2)) {
			fail("Testing the method equals() with different versions");
		}
	}

	/**
	 * Test the method hashCode() when the equals() returns true
	 * 
	 * @spec Version.hashCode();
	 */
	public void testVersionHashCode() throws Exception {
		Version version1;
		Version version2;

		version1 = new Version(0, 0, 0);
		version2 = new Version(0, 0, 0);

		assertEquals(
				"The method hashCode() has different return values when the method equals() returns true for two Version instances",
				version1.hashCode(), version2.hashCode());
		version1 = new Version(0, 0, 0);
		version2 = new Version(1, 0, 0);

		if (version1.hashCode() == version2.hashCode()) {
			fail("The method hashCode() has the same return value when the method equals() returns false for two Version instances");
		}
	}

	public void testVersionGetMajor() {
		Version version;

		version = new Version(1, 2, 3);

		assertEquals(
				"Testing the method getMajor() using the constructor Version(int,int,int)",
				1, version.getMajor());

		version = new Version("1.2.3");

		assertEquals(
				"Testing the method getMajor() using the constructor Version(String)",
				1, version.getMajor());
	}

	public void testVersionGetMinor() {
		Version version;

		version = new Version(1, 2, 3);

		assertEquals(
				"Testing the method getMinor() using the constructor Version(int,int,int)",
				2, version.getMinor());

		version = new Version("1.2.3");

		assertEquals(
				"Testing the method getMinor() using the constructor Version(String)",
				2, version.getMinor());
	}

	public void testVersionGetMicro() {
		Version version;

		version = new Version(1, 2, 3);

		assertEquals(
				"Testing the method getMicro() using the constructor Version(int,int,int)",
				3, version.getMicro());

		version = new Version("1.2.3");

		assertEquals(
				"Testing the method getMicro() using the constructor Version(String)",
				3, version.getMicro());
	}

	/**
	 * Test the method getQualifier() using the constructor
	 * Version(int,int,int,String)
	 * 
	 * @spec Version.getQualifier()
	 */
	public void testVersionGetQualifier() throws Exception {
		Version version;

		version = new Version(1, 1, 1, "a");

		assertEquals(
				"Testing the method getQualifier() using the constructor Version(int,int,int,String)",
				"a", version.getQualifier());

		version = new Version("1.1.1.a");

		assertEquals(
				"Testing the method getQualifier using the constructor Version(String)",
				"a", version.getQualifier());
	}

	public void testVersionCompareTo() {
		/**
		 * Test the method compareTo() with first version number less than
		 * second version number
		 * 
		 * @spec Version.compareTo(Version);
		 */
		Version version1;
		Version version2;

		version1 = new Version(1, 1, 1);
		version2 = new Version(2, 1, 1);

		if (version1.compareTo(version2) >= 0) {
			fail("Testing the method compareTo() with first version number less than second version number");
		}

		/**
		 * Test the method compareTo() with first version number greater than
		 * second version number
		 * 
		 * @spec Version.compareTo(Version);
		 */

		version1 = new Version(2, 1, 1);
		version2 = new Version(1, 1, 1);

		if (version1.compareTo(version2) <= 0) {
			fail("Testing the method compareTo() with first version number greater than second version number");
		}

		/**
		 * Test the method compareTo() with same version numbers
		 * 
		 * @spec Version.compareTo(Version);
		 */

		version1 = new Version(1, 1, 1);
		version2 = new Version(1, 1, 1);

		if (version1.compareTo(version2) != 0) {
			fail("Testing the method compareTo() with same version numbers");
		}

		/**
		 * Test the method compareTo() with an incorrect object
		 * 
		 * @spec Version.compareTo(Version);
		 */
		String incorrect;
		Version version;

		incorrect = "";
		version = new Version(1, 1, 1);

		try {
			version.compareTo(incorrect);
			fail("Testing the method compareTo() with an incorrect object");
		}
		catch (ClassCastException ex) {
			// This is an expected exception and can be ignored
		}
	}

	public void testVersionInstanceOf() {
		Object version;

		version = new Version(1, 1, 1);

		assertTrue(
				"The class Version does not implements Comparable interface",
				version instanceof Comparable);
	}

	public void testVersionConstantsValues() {
		assertEquals("emptyVersion not equal to 0.0.0", new Version(0, 0, 0),
				Version.emptyVersion);
	}

	private String reportProcessorOS() {
		String os = getContext().getProperty("org.osgi.framework.os.name");
		String proc = getContext().getProperty("org.osgi.framework.processor");
		StringBuffer sb = new StringBuffer();
		sb.append("Current osname=\"").append(os).append("\" processor=\"")
				.append(proc);
		sb
				.append("\". For allowed constants see http://www.osgi.org/Specifications/Reference");
		return sb.toString();
	}

}